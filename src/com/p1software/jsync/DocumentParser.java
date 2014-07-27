package com.p1software.jsync;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
            List<RemoteObject> objects = new ArrayList<RemoteObject>();
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
	                	objects.add(object);
	                	if (object instanceof RemoteDirectory) {
	                		if (parallel == null) {
	                			((RemoteDirectory) object).buildAlongside(this, null);
	                		}
	                		else {
		                		for (RemoteObject local : parallel) {
		                			if (local instanceof RemoteDirectory && object.getLocation().equals(local.getLocation())) {
		                				((RemoteDirectory) object).buildAlongside(this, (RemoteDirectory) local);
		                				break;
		                			}
		                		}
	                		}
	                		if (((RemoteDirectory) object).getContents() == null) {
	                			System.out.println("Contents of " + object.getName() + " null; parallel: " + parallel);
	                		}
	                	}
                	}
                }
            }
            Collections.sort(objects);
            for (RemoteObject object : objects) {
            	contents.add(object);
            }
            if (parallel == null) {
            	for (RemoteObject object : objects) {
            		manager.callEvent(null, object);
            	}
            }
            else {
            	objects.removeAll(parallel);
            	parallel.removeAll(contents);
            	for (int i = 0; i < objects.size(); i++) {
            		boolean flag = true;
            		for (int j = 0; j < parallel.size() && flag; j++) {
            			if (objects.get(i).getLocation().equals(parallel.get(j).getLocation())) {
            				manager.callEvent(parallel.get(j), objects.get(i));
            				parallel.remove(j);
            				flag = false;
            			}
            		}
            		if (flag) {
            			manager.callEvent(null, objects.get(i));
            		}
            		objects.remove(i--);
            	}
            	for (int i = 0; i < parallel.size(); i++) {
            		manager.callEvent(parallel.get(i), null);
            		parallel.remove(i--);
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
				System.out.println("Cached is null!");
				e.printStackTrace();
			}
			List<RemoteObject> result = getContents(remote, cached);
			if (cached.isEmpty()) {
				System.out.println("Cached is empty");
			}
			else {
				mapper.writeValue(sync, result);
			}
			return true;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    }
    
}
