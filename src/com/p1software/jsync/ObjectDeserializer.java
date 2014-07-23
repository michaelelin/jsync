package com.p1software.jsync;

import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectDeserializer extends JsonDeserializer<RemoteObject> {
    
    private ObjectMapper mapper;
    
    public ObjectDeserializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public RemoteObject deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        if (node.isObject()) {
            if (node.has("contents")) {
                RemoteDirectory obj = new RemoteDirectory(node.get("date").asText(), node.get("time").asText(), node.get("location").asText(), node.get("name").asText());
                obj.setContents(mapper.treeToValue(node.get("contents"), ArrayList.class));
                return obj;
            }
            else {
                RemoteFile obj = new RemoteFile(node.get("date").asText(), node.get("time").asText(), node.get("size").asText(), node.get("location").asText(), node.get("name").asText());
                return obj;
            }
        }
        return null;
    }

}
