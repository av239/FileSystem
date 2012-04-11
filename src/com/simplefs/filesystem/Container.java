package com.simplefs.filesystem;

import com.simplefs.file.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class Container {
    private List<Block> blocks = new ArrayList<Block>();
    public static int MAX_SIZE_AVAILABLE = 1000;

    private double loadFactor = 0.0;
    // if a loadFactor is lower then threshold, compact the file
    private double threshold = 0.5;
    private int bytesStored = 0;

    //private byte[] storage = new byte[MAX_SIZE_AVAILABLE];

    RandomAccessFile container;

    Container() {
        String containerFile = "container.bin";
        java.io.File f = new java.io.File(containerFile);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            f.delete();
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            container = new RandomAccessFile(containerFile, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getNumBlocks() {
        return blocks.size();
    }

    public synchronized int getFileSize(File f) {
        for (Block b : blocks) {
            if (b.getFile().equals(f)) {
                return b.getLength();
            }
        }
        return 0;
    }


    /**
     * Find first block with enough space to allocate given data
     *
     * @param f    file to write
     * @param data byte array that will be written
     * @return true, if an operation was successful, false otherwise
     */
    public boolean allocateFile(File f, byte[] data) {
        for (Block b : blocks) {
            if (b.isFree() && b.getLength() >= data.length) {
                try {
                    int offset = b.getOffset();
                    int len = data.length;

                    // if a block can be split
                    if (len < b.getLength()) {
                        Block newBlock = new Block(b.getLength() - len);
                        newBlock.setOffset(b.getOffset() + len);
                        blocks.add(newBlock);
                    }

                    b.setFile(f);
                    b.setLength(len);

                    container.seek(offset);
                    container.write(data);

                    bytesStored += len;
                    loadFactor = bytesStored / container.length();
                    System.out.println("load factor:" + loadFactor);

                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // if there is no free blocks, create a new one
        int offset = 0;
        int len = data.length;

        if (!blocks.isEmpty()) {
            offset = blocks.get(blocks.size() - 1).getFinishAddress() + 1;
        }

        Block b = new Block(f, offset, len);

        try {
            container.write(data);
            bytesStored += len;
            loadFactor = (double) bytesStored / container.length();
            //System.out.println("load factor:" + loadFactor);
        } catch (IOException e) {
            e.printStackTrace();
        }

        blocks.add(b);

        return true;
    }

    /**
     * Reads data from file
     *
     * @param f file to read
     * @return data stored in a file or null
     */
    public synchronized byte[] readFile(File f) {
        for (Block b : blocks) {
            if (!b.isFree() && b.getFile().equals(f)) {
                byte dataRead[] = new byte[b.getLength()];
                try {
                    container.seek(b.getOffset());
                    int res = container.read(dataRead);
                    assert (res == b.getLength());
                    return dataRead;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Deletes file from container and merges continuous blocks
     *
     * @param f file to delete
     * @return true if operation was successful
     */
    public synchronized boolean deleteFile(File f) {
        int index = 0;
        for (Block b : blocks) {
            if (!b.isFree() && b.getFile().equals(f)) {
                b.erase();
                bytesStored -= b.getLength();
                try {
                    loadFactor = (double) bytesStored / container.length();
                    //System.out.println("load factor in delete:" + loadFactor);

                    if (loadFactor < threshold) {
                        compact();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mergeFreeBlocks(index);
                return true;
            }
            index++;
        }
        return false;
    }

    /**
     * merges contiguous free blocks into one big block
     *
     * @param startIndex index to start searching for contiguous free blocks
     */
    public synchronized void mergeFreeBlocks(int startIndex) {
        /*for (int i = 0; i < blocks.size() - 1; i++) {
            if (blocks.get(i).isFree() && blocks.get(i + 1).isFree()) {
                int newSize = blocks.get(i).getLength() + blocks.get(i + 1).getLength();
                Block newBlock = new Block(newSize);
                newBlock.setOffset(blocks.get(i).getOffset());

                blocks.remove(i);
                blocks.remove(i);
                blocks.add(newBlock);
            }
        }*/
        int index = startIndex;
        while (index > 0 && blocks.get(index - 1).isFree()) {
            index--;
        }
        for (int i = index; i < blocks.size() - 1; i++) {
            if (blocks.get(i).isFree() && blocks.get(i + 1).isFree()) {
                int newSize = blocks.get(i).getLength() + blocks.get(i + 1).getLength();
                Block newBlock = new Block(newSize);
                newBlock.setOffset(blocks.get(i).getOffset());

                blocks.set(i, newBlock);
                blocks.remove(i + 1);
            } else {
                return;
            }
        }

    }

    public void compact() {
        byte copy[] = new byte[bytesStored];
        int position = 0;

        for (Block b : blocks) {
            if (!b.isFree()) {
                byte data[] = readFile(b.getFile());
                System.arraycopy(data, 0, copy, position, data.length);
                b.setOffset(position);
                position += data.length;
            }
        }

        try {
            container.setLength(0);
            container.write(copy);
            loadFactor = 1.0;
            System.out.println("compact was called");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BLOCKS:\n");
        for (Block b : blocks) {
            sb.append(b.toString()).append(" ");
        }
        return sb.toString();
    }

    public void close() {
        try {
            container.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
