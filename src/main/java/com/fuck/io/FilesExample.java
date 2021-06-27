package com.fuck.io;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

public class FilesExample {
    public static void main(String[] args) throws IOException {
        delete();
    }

    private static void delete() throws IOException {
        Path path = Paths.get("d:\\a");
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                // 删除目录下的文件
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            /**
             * 离开目录后调用
             * @param dir
             * @param exc
             * @return
             * @throws IOException
             */
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                    throws IOException {
                // 删除目录
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

    private static void visitDire() throws IOException {
        Path path = Paths.get("/Users/admin");
        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

            /**
             * 进入目录前调用
             * @param dir
             * @param attrs
             * @return
             * @throws IOException
             */
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                System.out.println(dir);
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            /**
             * 进入目录后调用
             * @param file
             * @param attrs
             * @return
             * @throws IOException
             */
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                System.out.println(file);
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });
        System.out.println(dirCount);
        System.out.println(fileCount);
    }

    private static void move() throws IOException {
        Path source = Paths.get("helloword/data.txt");
        Path target = Paths.get("helloword/data.txt");
        // StandardCopyOption.ATOMIC_MOVE 保证文件移动的原子性
        Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
    }
}
