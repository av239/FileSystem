package com.simplefs.filesystem;

import com.simplefs.file.Directory;
import com.simplefs.file.File;

public class FileSystem {
    // Handles data allocation for files
    private Container container = new Container();
    public static String delimeter = "/";
    // Root directory of a file system
    private static Directory rootDir = new Directory();
    // Current directory of a file system
    public static Directory currentDir = rootDir;

    public FileSystem() {
    }

    public Directory getCurrentDir() {
        return currentDir;
    }

    /**
     * File is created in current directory
     *
     * @param name
     * @return true if an operation was successful, false otherwise
     */
    public synchronized boolean createFile(String name) {
        return currentDir.addFile(new File(name, currentDir.getPath()));
    }

    /**
     * Creates file in a current directory
     *
     * @param f
     * @return true if a file was created successfully or false otherwise
     */
    public synchronized boolean createFile(File f) {
        f.setPath(currentDir.getPath());
        return currentDir.addFile(f);
    }

    /**
     * Creates new directory in a current directory
     *
     * @param name name of a created directory
     * @return true if a directory was created successfully or false otherwise
     */
    public synchronized boolean createDirectory(String name) {
        return currentDir.addFile(new Directory(name));
    }

    /**
     * Creates new directory in a current directory
     *
     * @param dir
     * @return true if a directory was created successfully or false otherwise
     */
    public synchronized boolean createDirectory(Directory dir) {
        return currentDir.addFile(dir);
    }

    public synchronized void writeFile(String filename, byte[] data) {
        File f = new File(filename);
        writeFile(f, data);
    }


    /**
     * Writes given data into a file
     *
     * @param f
     * @param data
     * @return true if an operation was successful, false otherwise
     */
    public synchronized boolean writeFile(File f, byte[] data) {
        if (!currentDir.contains(f)) {
            currentDir.addFile(f);
        }
        return container.allocateFile(f, data);
    }

    /**
     * Appends data into selected file
     *
     * @param f
     * @param dataToAppend
     * @return true if append operation was successful, false otherwise
     */
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

    /**
     * Read data from file
     *
     * @param f
     * @return byte array if read was successful, null otherwise
     */
    public synchronized byte[] readFile(File f) {
        if (currentDir.contains(f)) {
            return container.readFile(f);
        }
        return null;
    }

    /**
     * Deletes file from the current directory
     *
     * @param f
     */
    public synchronized void deleteFile(File f) {
        if (currentDir.contains(f)) {
            currentDir.removeFile(f);
            container.deleteFile(f);
        }
    }

    /**
     * Deletes directory from the current directory
     *
     * @param d
     */
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

    /**
     * Moves file from one directory into another
     *
     * @param fileToMove
     * @param from
     * @param to
     * @return true if move was successful, no otherwise
     */
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
