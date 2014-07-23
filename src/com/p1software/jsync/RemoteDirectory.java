package com.p1software.jsync;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"name", "date", "time", "location", "contents"})
public class RemoteDirectory extends RemoteObject {
    
    private List<RemoteObject> contents;
    
    @JsonCreator
    protected RemoteDirectory(@JsonProperty("date") String date, @JsonProperty("time") String time, @JsonProperty("location") String location, @JsonProperty("name") String name) {
        super(date, time, location, name);
    }
    
    public void buildAlongside(DocumentParser parser, RemoteDirectory parallel) {
        setContents(parser.getContents(this.location, parallel == null ? null : parallel.getContents()));
    }
    
    public List<RemoteObject> getContents() {
        return contents;
    }
    
    public void setContents(List<RemoteObject> contents) {
        Collections.sort(contents);
        this.contents = contents;
    }
    
}
