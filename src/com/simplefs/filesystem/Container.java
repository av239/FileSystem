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
    //private byte[] storage = new byte[MAX_SIZE_AVAILABLE];

    RandomAccessFile container;

    Container() throws FileNotFoundException {
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

        container = new RandomAccessFile(containerFile, "rw");
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
                //mergeFreeBlocks(index);
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

                blocks.remove(i);
                blocks.remove(i);
                blocks.add(newBlock);
            } else {
                return;
            }
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
