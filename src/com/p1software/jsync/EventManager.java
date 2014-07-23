package com.p1software.jsync;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    
    private List<JsonListener> listeners = new ArrayList<JsonListener>();
    
    public void registerListener(JsonListener listener) {
        listeners.add(listener);
    }
    
    public void callEvent(RemoteObject before, RemoteObject after) {
        if (after == null) {
            handleRemoved(before);
        }
        else if (before == null) {
            handleCreated(after);
        }
        else {
            handleChanged(before, after);
        }
    }
    
    private void handleCreated(RemoteObject object) {
        for (JsonListener listener : listeners) {
            listener.onCreated(object);
        }
    }
    
    private void handleRemoved(RemoteObject object) {
        for (JsonListener listener : listeners) {
            listener.onRemoved(object);
        }
    }
    
    private void handleChanged(RemoteObject prev, RemoteObject curr) {
        for (JsonListener listener : listeners) {
            listener.onChanged(prev, curr);
        }
    }
    
}
