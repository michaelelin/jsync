package com.p1software.jsync;

public interface JsonListener {
    
    public void onCreated(RemoteObject object);
    
    public void onRemoved(RemoteObject object);
    
    public void onChanged(RemoteObject prev, RemoteObject curr);
    
}
