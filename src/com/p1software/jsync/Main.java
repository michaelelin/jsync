package com.p1software.jsync;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    
    public static void main(String[] args) {
        String url = "http://imsatiming.com/Results/2014/";
        DocumentParser parser = new DocumentParser().useMask(".*\\.csv$").addListener(new VerboseListener());
        // parser.useSyncFile("sync.json");
        parser.synchronize(url);
    }
    
}
