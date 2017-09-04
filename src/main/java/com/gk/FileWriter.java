package com.gk;

import com.gk.model.Review;

public class FileWriter {
    private final String path;

    public FileWriter(String pathString) {
        this.path = pathString;
    }

    public void write(Review review) {
       System.out.println("###Write### " + review);
    }
}
