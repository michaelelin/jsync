package com.p1software.jsync;

import org.jsoup.nodes.Node;

class RemoteEntry {
    
    private Node description;
    private Node location;
    
    public RemoteEntry(Node description, Node location) {
        this.description = description;
        this.location = location;
    }
    
    public Node getDescription() {
        return description;
    }
    
    public Node getLocation() {
        return location;
    }
    
}
