package com.simplefs.file;

public class File extends AbstractFile {
    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public File(String name) {
        this.name = name;
    }

    public File(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FILE: ");
        sb.append("Name: " + name + "\n");
        return sb.toString();
    }

    /**
     * Two files are equal if they are located in the same directory and have identical names
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof File) {
            return (((File) obj).getName().equals(name)) && (((File) obj).getPath().equals(path));
        }
        return false;
    }
}
