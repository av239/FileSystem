package com.simplefs.filesystem;

import com.simplefs.file.File;
import org.junit.Test;

import java.util.Random;

import static junit.framework.Assert.*;

public class ContainerTest {
    /**
     * Tests if a file size exceeds available storage
     *
     * @throws Exception
     */
    @Test
    public void testAllocateFileBigSize() throws Exception {
        Container c = new Container();
        File f = new File("f");
        byte data[] = new byte[Container.MAX_SIZE_AVAILABLE + 1];
        assertFalse(c.allocateFile(f, data));
        data = new byte[Container.MAX_SIZE_AVAILABLE - 1];
        assertTrue(c.allocateFile(f, data));
    }

    /**
     * Test if a big block gets split into two smaller blocks
     *
     * @throws Exception
     */
    @Test
    public void testAllocateFile() throws Exception {
        Container c = new Container();
        assertEquals(1, c.getNumBlocks());
        File f = new File("f");
        byte data[] = generateRandomArray(Container.MAX_SIZE_AVAILABLE - 10);
        assertFalse(c.getNumBlocks() == 2);
        assertTrue(c.allocateFile(f, data));
    }

    /**
     * Test for allocating many small files
     *
     * @throws Exception
     */
    @Test
    public void testAllocateFileSmallFiles() throws Exception {
        Container c = new Container();

        for (int i = 0; i < Container.MAX_SIZE_AVAILABLE; i++) {
            File f = new File("f" + i);
            byte data[] = new byte[1];
            assertTrue(c.allocateFile(f, data));
        }

        File f = new File("tmp");
        byte data[] = new byte[1];

        assertFalse(c.allocateFile(f, data));
    }

    /**
     * Test if continuous free blocks are merged into one bigger block
     *
     * @throws Exception
     */
    @Test
    public void testMergeFreeBlocks() throws Exception {
        Container c = new Container();
        File f1 = new File("f1");
        File f2 = new File("f2");
        byte data[] = generateRandomArray(10);

        c.allocateFile(f1, data);
        c.allocateFile(f2, data);

        assertEquals(3, c.getNumBlocks());
        c.deleteFile(f2);

        assertEquals(2, c.getNumBlocks());
        c.deleteFile(f1);

        assertEquals(1, c.getNumBlocks());
    }

    @Test
    public void testReadFile() throws Exception {
        Container c = new Container();
        File f = new File("fileForRead");
        byte data[] = generateRandomArray(100);

        c.allocateFile(f, data);

        byte dataRead[] = c.readFile(f);
        for (int i = 0; i < data.length; i++) {
            assertEquals(data[i], dataRead[i]);
        }

    }

    @Test
    public void testDeleteFile() throws Exception {
        Container c = new Container();
        File f = new File("fileToDelete");
        byte data[] = new byte[1];
        c.allocateFile(f, data);
        c.deleteFile(f);
        assertEquals(c.readFile(f), null);
    }

    static byte[] generateRandomArray(int size) {
        byte result[] = new byte[size];
        Random rnd = new Random();
        for (int i = 0; i < size; i++) {
            result[i] = (byte) (rnd.nextInt() % 100);
        }
        return result;
    }
}
