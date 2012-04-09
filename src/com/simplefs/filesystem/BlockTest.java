package com.simplefs.filesystem;

import com.simplefs.file.File;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class BlockTest {
    @Test
    public void testErase() throws Exception {
        File f = new File("file");
        Block b = new Block(f, 100, 100);
        assertFalse(b.isFree());
        b.erase();
        assertTrue(b.isFree());
    }

    @Test
    public void testIsFree() throws Exception {
        Block b = new Block(100);
        assertTrue(b.isFree());
    }

    @Test
    public void testGetFinishAddress() throws Exception {
        File f = new File("f");
        Block b = new Block(f, 100, 10);
        assertTrue(b.getFinishAddress() == 109);
    }
}
