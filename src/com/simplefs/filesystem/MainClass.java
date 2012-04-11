package com.simplefs.filesystem;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MainClass {
    private static List<com.simplefs.file.File> fileList = new ArrayList<com.simplefs.file.File>();

    public static byte[] getFileContent(File f) {
        FileInputStream fins;
        byte data[] = null;

        try {
            fins = new FileInputStream(f);
            DataInputStream input = new DataInputStream(new BufferedInputStream(fins));

            data = new byte[(int) f.length()];
            input.read(data);
            fins.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static byte[] getFileContent(String directory, String filename) {
        File dir = new File(directory);
        File[] children = dir.listFiles();

        for (File f : children) {
            if (f.isFile()) {
                if (f.getName().equals(filename)) {
                    return getFileContent(f);
                }
            }
        }
        return null;
    }

    public static void writeFileContents(Container c, File f) {
        byte data[] = getFileContent(f);
        com.simplefs.file.File file = new com.simplefs.file.File(f.getName());

        c.allocateFile(file, data);

        fileList.add(file);
    }

    public static void writeFilesFromDir(Container c, File directory) {
        File[] children = directory.listFiles();

        for (File f : children) {
            if (f.isFile()) {
                System.out.println(f.getName());
                writeFileContents(c, f);
            } else {
                writeFilesFromDir(c, f);
            }
        }
    }

    public static void main(String[] args) {
        Container c = new Container();

        File ideaDir = new File(".idea");
        writeFilesFromDir(c, ideaDir);

        int numFilesToDelete = fileList.size() - 1;
        for (int i = 0; i < numFilesToDelete; i++) {
            c.deleteFile(fileList.get(i));
        }

        // the number of blocks depends on compact operation,
        // which is unknown to happen
        if (c.getNumBlocks() == (fileList.size() - numFilesToDelete) + 1 ||
                c.getNumBlocks() == (fileList.size() - numFilesToDelete)) {
            // if compact occured, no free blocks should be in a container
            // if compact did not occur, one free block must be in a container
        } else {
            throw new RuntimeException("Error in merge free blocks!");
        }

        /*File testDir = new File("testdir");
        writeFilesFromDir(c, testDir);*/

        for (int i = 0; i < numFilesToDelete; i++) {
            byte dataRead[] = c.readFile(fileList.get(i));

            if (dataRead != null) {
                throw new RuntimeException("ERROR in deleting file!");
            }
        }

        for (int i = numFilesToDelete; i < fileList.size(); i++) {
            byte dataRead[] = c.readFile(fileList.get(i));

            byte expectedData[] = getFileContent(".idea", fileList.get(i).getName());

            for (int j = 0; j < dataRead.length; j++) {
                if (expectedData != null && dataRead[j] != expectedData[j]) {
                    System.out.println(j);
                    throw new RuntimeException("ERROR in reading file!");
                }
            }

            //System.out.println("READ FILE:" + fileList.get(i).getName());
        }

        c.close();
    }
}
