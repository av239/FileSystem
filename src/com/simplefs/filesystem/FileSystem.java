package com.simplefs.filesystem;

import com.simplefs.file.Directory;
import com.simplefs.file.File;

public class FileSystem {
    private Container container = new Container();
    public static String delimeter = "/";
    private static Directory rootDir = new Directory();
    public static Directory currentDir = rootDir;

    public FileSystem() {
    }

    public Directory getCurrentDir() {
        return currentDir;
    }

    /**
     * File is created in a current directory
     *
     * @param name
     */
    public synchronized boolean createFile(String name) {
        return currentDir.addFile(new File(name, currentDir.getPath()));
    }

    public synchronized boolean createFile(File f) {
        f.setPath(currentDir.getPath());
        return currentDir.addFile(f);
    }

    public synchronized boolean createDirectory(String name) {
        return currentDir.addFile(new Directory(name));
    }

    public synchronized boolean createDirectory(Directory dir) {
        return currentDir.addFile(dir);
    }

    public synchronized void writeFile(String filename, byte[] data) {
        File f = new File(filename);
        writeFile(f, data);
    }


    public synchronized boolean writeFile(File f, byte[] data) {
        if (!currentDir.contains(f)) {
            currentDir.addFile(f);
        }
        return container.allocateFile(f, data);
    }

    public synchronized boolean appendFile(File f, byte[] dataToAppend) {
        if (currentDir.contains(f)) {
            System.out.println(f);
            byte oldData[];
            oldData = readFile(f);
            byte newData[] = new byte[getFileSize(f) + dataToAppend.length];

            System.arraycopy(oldData, 0, newData, 0, oldData.length);
            System.arraycopy(dataToAppend, 0, newData, oldData.length, dataToAppend.length);

            deleteFile(f);
            createFile(f);
            return writeFile(f, newData);
        }
        return false;
    }

    public byte[] readFile(File f) {
        if (currentDir.contains(f)) {
            return container.readFile(f);
        }
        return null;
    }

    public synchronized void deleteFile(File f) {
        if (currentDir.contains(f)) {
            currentDir.removeFile(f);
            container.deleteFile(f);
        }
    }

    public synchronized void deleteDir(Directory d) {
        if (currentDir.contains(d)) {
            currentDir.removeFile(d);
        }
    }

    public synchronized void changeDir(Directory newDir) {
        currentDir = newDir;
    }

    public int getFileSize(File f) {
        return container.getFileSize(f);
    }

    public boolean moveFile(File fileToMove, Directory from, Directory to) {
        boolean result = from.removeFile(fileToMove);
        return result && (to.addFile(fileToMove));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FILE SYSTEM:\n");
        sb.append(rootDir.showStructure(0));
        return sb.toString();
    }
}
