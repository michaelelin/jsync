package com.p1software.jsync;

import java.net.URL;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@JsonPropertyOrder({"name", "date", "time", "location"})
//@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class RemoteObject implements Comparable<RemoteObject> {
    
    protected String date;
    protected String time;
    protected String location;
    protected String name;
    
    public RemoteObject() {}
    
    protected RemoteObject(String date, String time, String location, String name) {
        this.date = date;
        this.time = time;
        this.location = location;
        this.name = name;
    }
    
    public static RemoteObject construct(RemoteEntry entry) {
        try {
            String[] descriptors = entry.getDescription().toString().trim().split("( )+");
            String cDate = descriptors[0];
            String cTime = descriptors[1] + " " + descriptors[2];
            String cHost = entry.getDescription().baseUri();
            String cPath = entry.getLocation().attr("href");
            String cName = entry.getLocation().childNode(0).toString().trim();
            
            String cLocation = new URL(new URL(cHost), cPath).toString();
            
            if (descriptors[3].equals("&lt;dir&gt;")) {
                return new RemoteDirectory(cDate, cTime, cLocation, cName);
            }
            else {
                String cSize = descriptors[3];
                return new RemoteFile(cDate, cTime, cSize, cLocation, cName);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String getDate() {
        return date;
    }
    
    public String getTime() {
        return time;
    }
    
    public String getName() {
        return name;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public int compareTo(RemoteObject o) {
        return o.getClass().equals(this.getClass()) ? location.compareTo(o.getLocation()) : this instanceof RemoteFile ? 1 : -1;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RemoteObject)) {
            return false;
        }
        RemoteObject rem = (RemoteObject) o;
        return compareTo(rem) == 0 && this.date.equals(rem.date) && this.time.equals(rem.time);
    }
    
    public String toString() {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {
            return mapper.writeValueAsString(this);
        }
        catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }
    
}
