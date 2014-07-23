package com.p1software.jsync;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class DocumentParser {
    private String mask = ".*";
    private EventManager manager;
    private ObjectMapper mapper;
    private String syncFile = "sync.json";
    
    public DocumentParser() {
    	this.manager = new EventManager();

        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Object.class, new ObjectDeserializer(mapper));
        
        this.mapper.registerModule(module);
    }
    
    public DocumentParser useMask(String mask) {
        this.mask = mask;
        return this;
    }
    
    public DocumentParser addListener(JsonListener listener) {
    	this.manager.registerListener(listener);
    	return this;
    }
    
    public DocumentParser useSyncFile(String syncFile) {
    	this.syncFile = syncFile;
    	return this;
    }
    
    public List<RemoteObject> getContents(String url, List<RemoteObject> parallel) {
        List<RemoteObject> contents = new ArrayList<RemoteObject>();
        try {
            Document doc = Jsoup.connect(url).get();
            List<Node> nodes = doc.body().getElementsByTag("pre").first().childNodes();
            Node description = null;
            int i = 0;
            for (Node node : nodes) {
                String name = node.nodeName();
                if (name.equals("br")) {
                    description = null;
                }
                else if (name.equals("#text")) {
                    description = node;
                }
                else if (name.equals("a") && description != null) {
                    RemoteObject object = RemoteObject.construct(new RemoteEntry(description, node));
                    if (object instanceof RemoteDirectory || object.getName().matches(mask)) {
                        RemoteDirectory cachedDir = null;
                        if (parallel == null) {
                            manager.callEvent(null, object);
                        }
                        else {
                            int c;
                            do {
                                if (i < parallel.size()) {
                                    RemoteObject cached = parallel.get(i);
                                    c = cached.compareTo(object);
                                    if (c > 0) { // Cached object is ahead (created)
                                        manager.callEvent(null, object);
                                        break;
                                    }
                                    else if (c < 0) { // Cached object is behind (removed)
                                        manager.callEvent(cached, null);
                                    }
                                    else if (!object.equals(cached)) { // Same name/location (changed)
                                        manager.callEvent(cached, object);
                                    }
                                    if (cached instanceof RemoteDirectory) {
                                        cachedDir = (RemoteDirectory) cached;
                                    }
                                    i++;
                                }
                                else {
                                    manager.callEvent(null, object);
                                    c = 1;
                                }
                            }
                            while (c < 0);
                        }
                        if (object instanceof RemoteDirectory) {
                            ((RemoteDirectory) object).buildAlongside(this, cachedDir);
                        }
                        contents.add(object);
                        description = null;
                    }
                }
            }
            if (parallel != null) {
                for (; i < parallel.size(); i++) {
                    manager.callEvent(parallel.get(i), null);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return contents;
    }
    
    public boolean synchronize(String remote) {
    	return synchronize(remote, ".");
    }
    
    public boolean synchronize(String remote, String local) {
    	try {
			List<RemoteObject> cached;
			File sync = new File(local, syncFile);
			try {
		    	cached = mapper.treeToValue(mapper.readTree(sync), ArrayList.class);
			}
			catch (Exception e) {
				cached = null;
			}
			List<RemoteObject> result = getContents(remote, cached);
			mapper.writeValue(sync, result);
			return true;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    }
    
}
