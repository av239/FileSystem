package com.simplefs.file;

import com.simplefs.filesystem.FileSystem;

import java.util.ArrayList;
import java.util.List;

public class Directory extends AbstractFile {
    List<AbstractFile> fileList = new ArrayList<AbstractFile>();

    public Directory() {
        this.path = FileSystem.delimeter;
    }

    public Directory(String name) {
        this.path = "";
        if (!FileSystem.currentDir.path.equals("/")) {
            this.path = FileSystem.currentDir.path;
        }
        this.path += FileSystem.delimeter + name;
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public List<AbstractFile> getFileList() {
        return fileList;
    }

    public boolean addFile(AbstractFile f) {
        if (fileList.contains(f)) {
            return false;
        }
        fileList.add(f);
        return true;
    }

    public boolean removeFile(AbstractFile f) {
        return fileList.remove(f);
    }

    public boolean contains(AbstractFile f) {
        return fileList.contains(f);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DIRECTORY: ");
        sb.append("PATH: " + path + " ");
        sb.append("NAME: " + name + "\n");
        return sb.toString();
    }

    public String showStructure(int numTabs) {
        StringBuilder sb = new StringBuilder();
        for (AbstractFile f : fileList) {
            for (int i = 0; i < numTabs; i++) {
                sb.append("\t");
            }
            sb.append(f.toString());
            if (f instanceof Directory) {
                sb.append(((Directory) f).showStructure(numTabs + 1));
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Directory) {
            return (((Directory) obj).getName().equals(name) && ((Directory) obj).getPath().equals(path));
        }
        return false;
    }
}
