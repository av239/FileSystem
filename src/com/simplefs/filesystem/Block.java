package com.simplefs.filesystem;

import com.simplefs.file.File;

public class Block {
    public void setFile(File file) {
        this.file = file;
        this.isFree = false;
    }

    public void setLength(int length) {
        this.length = length;
    }

    File file;
    private boolean isFree;
    // length of data stored in a file
    private int length;
    // start address of data in container storage
    private int offset;

    public synchronized File getFile() {
        if (file != null) {
            return file;
        }
        return new File("");
    }

    public synchronized int getOffset() {
        return offset;
    }

    public synchronized void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IsFree: ").append(isFree);
        sb.append(" Start address: ").append(offset);
        sb.append(" Size: ").append(length);
        if (file != null) {
            sb.append("File: ").append(file);
        }
        sb.append("\n");
        return sb.toString();
    }

    public synchronized int getLength() {
        return length;
    }


    Block(int length) {
        isFree = true;
        offset = 0;
        this.length = length;
    }

    Block(File f, int start, int length) {
        file = f;
        offset = start;
        this.length = length;
        isFree = false;
    }

    public synchronized int getFinishAddress() {
        return offset + length - 1;
    }

    public synchronized boolean erase() {
        isFree = true;
        file = null;
        return true;
    }

    public synchronized boolean isFree() {
        return isFree;
    }
}
