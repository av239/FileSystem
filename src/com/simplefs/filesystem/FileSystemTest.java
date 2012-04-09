package com.simplefs.filesystem;

import com.simplefs.file.Directory;
import com.simplefs.file.File;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class FileSystemTest {
    /**
     * Test for duplicate files and directories
     *
     * @throws Exception
     */
    @Test
    public void testCreateFile() throws Exception {
        FileSystem fs = new FileSystem();
        assertEquals(0, fs.getCurrentDir().getFileList().size());
        fs.createFile("f");
        // check if a duplicate file will be created
        assertFalse(fs.createFile("f"));
        // check if a duplicate directory will be created
        fs.createDirectory("dir");
        assertFalse(fs.createDirectory("dir"));
        //System.out.println(fs);
        assertEquals(2, fs.getCurrentDir().getFileList().size());
    }

    @Test
    public void testAppendFileNonExisting() throws Exception {
        FileSystem fs = new FileSystem();
        File f = new File("nonexistingfile");
        byte data[] = new byte[10];
        //System.out.println("ASS: " + fs.appendFile(f, data));
        assertFalse(fs.appendFile(f, data));
    }

    @Test
    public void testAppendFile() throws Exception {
        FileSystem fs = new FileSystem();
        File f = new File("f");

        byte data[] = ContainerTest.generateRandomArray(10);

        fs.writeFile(f, data);
        //System.out.println(fs);

        byte dataRead[];
        dataRead = fs.readFile(f);
        for (int i = 0; i < data.length; i++) {
            assertEquals(dataRead[i], data[i]);
        }

        byte dataToAppend[] = new byte[3];
        dataToAppend[dataToAppend.length - 1] = 99;
        assertTrue(fs.appendFile(f, dataToAppend));

        dataRead = fs.readFile(f);

        for (int i = 0; i < data.length; i++) {
            assertEquals(dataRead[i], data[i]);
        }
        for (int i = data.length; i < data.length + dataToAppend.length; i++) {
            assertEquals(dataRead[i], dataToAppend[i - data.length]);
        }
    }

    @Test
    public void testAddDirectory() throws Exception {
        FileSystem fs = new FileSystem();
        Directory d = new Directory("dir");
        fs.createDirectory(d);
        assertTrue(fs.getCurrentDir().getFileList().contains(d));
    }

    @Test
    public void testWriteFile() throws Exception {
        FileSystem fs = new FileSystem();
        File f = new File("f");

        byte data[] = new byte[Container.MAX_SIZE_AVAILABLE];
        data[0] = 7;
        data[data.length - 1] = 7;

        assertTrue(fs.writeFile(f, data));
        //System.out.println(fs);

        byte dataRead[];
        dataRead = fs.readFile(f);
        for (int i = 0; i < data.length; i++) {
            assertEquals(dataRead[i], data[i]);
        }
    }

    @Test
    public void testWriteFileLarge() throws Exception {
        FileSystem fs = new FileSystem();
        File f = new File("f");

        byte data[] = ContainerTest.generateRandomArray(Container.MAX_SIZE_AVAILABLE + 1);

        assertFalse(fs.writeFile(f, data));
    }

    @Test
    public void testDeleteFile() throws Exception {
        FileSystem fs = new FileSystem();
        File f = new File("f");
        fs.createFile(f);
        assertEquals(1, fs.getCurrentDir().getFileList().size());
        fs.deleteFile(f);
        assertEquals(0, fs.getCurrentDir().getFileList().size());
    }

    @Test
    public void testMoveFile() throws Exception {
        FileSystem fs = new FileSystem();
        File f = new File("fileTOMOVE");

        fs.createFile(f);

        Directory to = new Directory("to");
        fs.createDirectory(to);

        fs.moveFile(f, fs.getCurrentDir(), to);

        assertFalse(fs.getCurrentDir().contains(f));
        assertTrue(to.contains(f));
    }
}
