package com.simplefs.filesystem;

public class MainClass {
    public static void main(String[] args) {
        /*FileSystem fs = new FileSystem();
        fs.createFile("a");
        fs.createFile("b");
        Directory dir1 = new Directory("dir");
        //fs.createDirectory("dir");
        fs.createDirectory(dir1);
        System.out.println(fs);
        fs.changeDir(dir1);
        fs.createDirectory("newdir");
        Directory dir2 = new Directory("dir2");
        fs.createDirectory(dir2);
        fs.changeDir(dir2);
        fs.createFile("file1");
        fs.createFile("file2");

        File fileToWrite = new File("tmp");
        byte data[] = new byte[10];
        data[0] = 100;
        data[data.length - 1] = 99;
        fs.createFile(fileToWrite);
        fs.writeFile(fileToWrite, data);
        byte dataRead[] = new byte[10];
        dataRead = fs.readFile(fileToWrite);
        System.out.println(fs);
        for (int i = 0; i < dataRead.length; i++) {
            System.out.println(dataRead[i]);
        }
        fs.deleteFile(fileToWrite);
        System.out.println(fs);*/


        /*Container c = new Container();
        File f = new File("a");
        byte[] data = new byte[] {1,2,3};
        c.allocateFile(f, data);
        System.out.println(c);
        File f1 = new File("b");
        data[2] = (byte) 100;
        c.allocateFile(f1, data);
        System.out.println(c);
        
        byte[] tmp = new byte[3];
        tmp = c.readFile(f1);
        for (int i = 0; i < tmp.length; i++){
            System.out.println(tmp[i]);
        }
        
        c.deleteFile(f1);
        System.out.println(c);*/


        /*fs.write(f, data);
        byte[] res = new byte[3];
        for (byte b : data){
            System.out.println(b);
        }
        res = fs.read(f);
        for (byte b : res){
            System.out.println(b);
        }*/
    }
}
