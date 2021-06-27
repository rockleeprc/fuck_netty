package com.fuck.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileCopyExample {
    public static void main(String[] args) throws IOException {
        m4();
    }

    private static void m4() throws IOException {
        String source = "D:\\Snipaste-1.16.2-x64";
        String target = "D:\\Snipaste-1.16.2-x64aaa";

        Files.walk(Paths.get(source)).forEach(path -> {
            try {
                String targetName = path.toString().replace(source, target);
                // 是目录
                if (Files.isDirectory(path)) {
                    Files.createDirectory(Paths.get(targetName));
                }
                // 是普通文件
                else if (Files.isRegularFile(path)) {
                    Files.copy(path, Paths.get(targetName));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void m3() throws IOException {
        Path source = Paths.get("data.txt");
        Path target = Paths.get("to.txt");
//        如果文件已存在，会抛异常 FileAlreadyExistsException
        Files.copy(source, target);
    }

    private static void m2() {
        try (
                FileChannel from = new FileInputStream("data.txt").getChannel();
                FileChannel to = new FileOutputStream("to.txt").getChannel();
        ) {
            long size = from.size();
            // left 变量代表还剩余多少字节
            for (long position = size; position > 0; ) {
                System.out.println("position:" + (size - position) + " left:" + position);
                position -= from.transferTo((size - position), position, to);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void m1() {
        String FROM = "data.txt";
        String TO = "to.txt";

        try (
                FileChannel from = new FileInputStream(FROM).getChannel();
                FileChannel to = new FileOutputStream(TO).getChannel();
        ) {
            // 是用零拷贝，传输文件有2G限制
            from.transferTo(0, from.size(), to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
