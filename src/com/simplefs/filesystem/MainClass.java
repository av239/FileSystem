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

    public static void writeFilesFromDir(Container c, String directory) {
        File dir = new File(directory);
        System.out.println(dir.isDirectory());
        File[] children = dir.listFiles();

        for (File f : children) {
            if (f.isFile()) {
                System.out.println(f.getName());
                writeFileContents(c, f);
            }
        }
    }

    public static void main(String[] args) {
        Container c = new Container();

        writeFilesFromDir(c, ".idea");

        for (int i = 0; i < fileList.size(); i++) {
            if (i % 2 == 0) {
                c.deleteFile(fileList.get(i));
            }
        }

        //writeFilesFromDir(c, "testdir");

        for (int i = 0; i < fileList.size(); i++) {
            if (i % 2 == 1) {
                byte dataRead[] = c.readFile(fileList.get(i));

                byte expectedData[] = getFileContent(".idea", fileList.get(i).getName());

                for (int j = 0; j < dataRead.length; j++) {
                    if (expectedData != null && dataRead[j] != expectedData[j]) {
                        throw new RuntimeException("ERROR!");
                    }
                }
            }
        }

        c.close();
    }
}
