package com.p1software.jsync;

import java.io.PrintStream;

public class VerboseListener implements JsonListener {
    
    private PrintStream out;
    
    public VerboseListener() {
    	this(System.out);
    }
    
    public VerboseListener(PrintStream out) {
    	this.out = out;
    }

    @Override
    public void onCreated(RemoteObject object) {
        out.println("Created " + object.getClass().getSimpleName() + ": " + object.getLocation());
    }

    @Override
    public void onRemoved(RemoteObject object) {
        out.println("Removed " + object.getClass().getSimpleName() + ": " + object.getLocation());
    }

    @Override
    public void onChanged(RemoteObject prev, RemoteObject curr) {
        out.println("Changed " + curr.getClass().getSimpleName() + ": " + curr.getLocation());
    }

}
