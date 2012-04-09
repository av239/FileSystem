package com.simplefs.filesystem;

import com.simplefs.file.File;

public class Block {
    File file;
    private int size;
    private boolean isFree;
    private int startAddress;

    public File getFile() {
        if (file != null) {
            return file;
        }
        File dummy = new File("");
        return dummy;
    }

    public synchronized void setFile(File file) {
        this.file = file;
        this.isFree = false;
    }

    public int getStartAddress() {
        return startAddress;
    }

    public synchronized void setStartAddress(int startAddress) {
        this.startAddress = startAddress;
    }

    public synchronized void setSize(int size) {

        this.size = size;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IsFree: ").append(isFree);
        sb.append(" Start address: ").append(startAddress);
        sb.append(" Size: ").append(size);
        if (file != null) {
            sb.append("File: ").append(file);
        }
        sb.append("\n");
        return sb.toString();
    }

    public synchronized void setFree(boolean free) {
        isFree = free;
    }


    public int getSize() {
        return size;
    }


    Block(int size) {
        isFree = true;
        startAddress = 0;
        this.size = size;
    }

    Block(File f, int start, int size) {
        file = f;
        startAddress = start;
        this.size = size;
        isFree = false;
    }

    public int getFinishAddress() {
        return startAddress + size - 1;
    }

    public synchronized boolean erase() {
        isFree = true;
        file = null;
        return true;
    }

    public boolean isFree() {
        return isFree;
    }
}
