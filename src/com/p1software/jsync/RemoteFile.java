package com.p1software.jsync;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"name", "date", "time", "location", "size"})
public class RemoteFile extends RemoteObject {
    
    private int size;
    
    @JsonCreator
    protected RemoteFile(@JsonProperty("date") String date, @JsonProperty("time") String time, @JsonProperty("size") String size, @JsonProperty("location") String location, @JsonProperty("name") String name) {
        super(date, time, location, name);
        this.size = Integer.parseInt(size);
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public boolean equals(Object o) {
        return super.equals(o) && this.size == ((RemoteFile) o).size;
    }

}
