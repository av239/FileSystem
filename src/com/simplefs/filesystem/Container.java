package com.simplefs.filesystem;

import com.simplefs.file.File;

import java.util.ArrayList;
import java.util.List;

public class Container {
    private List<Block> blocks = new ArrayList<Block>();
    public static int MAX_SIZE_AVAILABLE = 1000;
    private byte[] storage = new byte[MAX_SIZE_AVAILABLE];

    Container() {
        blocks.add(new Block(MAX_SIZE_AVAILABLE));
    }

    public int getNumBlocks() {
        return blocks.size();
    }

    public int getFileSize(File f) {
        for (Block b : blocks) {
            if (b.getFile().equals(f)) {
                return b.getSize();
            }
        }
        return 0;
    }


    /**
     * Find first block with enough space to allocate given data
     *
     * @param f
     * @param data
     * @return true, if an operation was successful, false otherwise
     */
    public synchronized boolean allocateFile(File f, byte[] data) {
        for (Block b : blocks) {
            // Using first-fit strategy to allocate data in a block
            if (b.isFree() && b.getSize() >= data.length) {
                // if a block can be split into 2 blocks
                if (b.getSize() > data.length) {
                    Block newBlock = new Block(b.getSize() - data.length);
                    newBlock.setStartAddress(b.getStartAddress() + data.length);
                    blocks.add(newBlock);

                }
                b.setFile(f);
                b.setSize(data.length);
                System.arraycopy(data, 0, storage, b.getStartAddress(), data.length);
                return true;
            }
        }
        return false;
    }

    /**
     * Reads data from file
     *
     * @param f
     * @return data stored in a file or null
     */
    public byte[] readFile(File f) {
        for (Block b : blocks) {
            if (!b.isFree() && b.getFile().equals(f)) {
                byte[] tmp = new byte[b.getSize()];
                System.arraycopy(storage, b.getStartAddress(), tmp, 0, b.getSize());
                return tmp;
            }
        }
        return null;
    }

    /**
     * Deletes file from container and merges continuous blocks
     *
     * @param f
     * @return
     */
    public synchronized boolean deleteFile(File f) {
        for (Block b : blocks) {
            if (!b.isFree() && b.getFile().equals(f)) {
                b.erase();
                for (int i = b.getStartAddress(); i <= b.getFinishAddress(); i++) {
                    storage[i] = 0;
                }
            }
        }
        mergeFreeBlocks();
        return true;
    }

    /**
     * merges contiguous free blocks into one big block
     */
    public synchronized void mergeFreeBlocks() {
        for (int i = 0; i < blocks.size() - 1; i++) {
            if (blocks.get(i).isFree() && blocks.get(i + 1).isFree()) {
                int newSize = blocks.get(i).getSize() + blocks.get(i + 1).getSize();
                Block newBlock = new Block(newSize);
                newBlock.setStartAddress(blocks.get(i).getStartAddress());

                blocks.remove(i);
                blocks.remove(i);
                blocks.add(newBlock);
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
}
