package io.github.tomgarden.lib.img.img_crop.util;

import android.content.Context;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 describe : null

 <p>author : tom
 <p>time : 18-10-9 17:41
 <p>email : tom.work@foxmail.com
 <p>CSDN : https://blog.csdn.net/u014587769
 <p>Git : https://github.com/TomGarden
 */
public class FileUtil {

    public static final String IMAGE = "image";

    //创建指定文件
    public static File getOrCreateFile(String path) {
        return FileUtil.getOrCreateFile(new File(path));
    }

    public static File getOrCreateFile(File file) {
        boolean result = false;
        try {
            if (!file.exists()) {
                FileUtil.getOrCreateDir(file.getParentFile());
                result = file.createNewFile();
            } else {
                if (!file.isFile()) {
                    result = file.createNewFile();
                } else {
                    result = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!result) {
            throw new RuntimeException("Create file failed!!!");
        }

        return file;
    }

    //创建指定文件夹
    public static File getOrCreateDir(String path) {
        return FileUtil.getOrCreateDir(new File(path));
    }

    public static File getOrCreateDir(File dir) {
        boolean result = false;
        if (!dir.exists()) {
            result = dir.mkdirs();
        } else {
            if (!dir.isDirectory()) {
                result = dir.mkdirs();
            } else {
                result = true;
            }
        }

        if (!result) {
            throw new RuntimeException("Create dir failed!!!");
        }

        return dir;
    }
    
    //拷贝文件
    public static void copyFile(File srcFile, File tarFile) {
        try {
            int byteRead = 0;
            if (srcFile.exists()) {
                InputStream inStream = new FileInputStream(srcFile);
                File file = getOrCreateFile(tarFile);
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                while ((byteRead = inStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, byteRead);
                }
                outputStream.flush();
                outputStream.close();
                inStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //拷贝文件
    public static void copyFile(FileDescriptor fileDescriptor, File tarFile) {
        try {
            int byteRead = 0;
            InputStream inStream = new FileInputStream(fileDescriptor);
            File file = getOrCreateFile(tarFile);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            while ((byteRead = inStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, byteRead);
            }
            outputStream.flush();
            outputStream.close();
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(String src, String tar) {
        File srcFile = new File(src);
        File tarFile = new File(tar);
        copyFile(srcFile, tarFile);
    }

    //拷贝文件夹
    public static void copyDir(String src, String tar) {
        try {
            getOrCreateDir(tar);
            String[] file = new File(src).list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (src.endsWith(File.separator)) {
                    temp = new File(src + file[i]);
                } else {
                    temp = new File(src + File.separator + file[i]);
                }
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(
                            tar + File.separator + (temp.getName()).toString());
                    byte[] b = new byte[1024];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {
                    copyFile(src + File.separator + file[i], tar + File.separator + file[i]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** '/storage/emulated/0/Android/data/packageName/cache/image' */
    public static File getExternalCacheImageDir(Context context) {
        return getExternalCacheXxxDir(context, IMAGE);
    }

    public static File getExternalCacheXxxDir(Context context, String dirXXX) {
        String path = context.getExternalCacheDir().getPath() + File.separator + "image";
        return getOrCreateDir(path);
    }

    public static byte[] createChecksum(String filename) throws Exception {
        InputStream fis =  new FileInputStream(filename);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return complete.digest();
    }

    // see this How-to for a faster way to convert
    // a byte array to a HEX string
    public static String getMD5Checksum(String filename) throws Exception {
        byte[] b = createChecksum(filename);
        String result = "";

        for (int i=0; i < b.length; i++) {
            result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }
}
