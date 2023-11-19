package com.example.citic.util;

import com.example.citic.dto.ManDirectoryDTO;
import com.example.citic.service.ManService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 静态文件生成工具类
 */
@Service
public class StaticUtil {
    @Resource
    ManService manService;

    // 文件根路径
    String fileRoot = "test\\man\\";

    /**
     * 创建文件并写入内容，使用NIO
     *
     * @param filePath
     * @param content
     * @throws IOException
     */
    private static void createAndWriteFile(String filePath, String content) throws IOException {
        Path path = Paths.get(filePath);

        // 如果文件不存在，创建文件
        if (!Files.exists(path)) {
            Files.createFile(path);
            // 写入内容
            Files.write(path, content.getBytes(), StandardOpenOption.WRITE);
        }

    }

    public void getDirectory() {
        // 获取目录
        List<ManDirectoryDTO> directory = manService.getDirectory();
        // 写入文件
        for (ManDirectoryDTO manDirectoryDTO : directory) {
            List<ManDirectoryDTO> childrens = manDirectoryDTO.getChildren();
            for (ManDirectoryDTO children : childrens) {
                writeFile(children.getPath(), children.getTitle());
            }
        }

    }

//    public void runThread(List<ManDirectoryDTO> childrens){
//        System.out.println("线程"+Thread.currentThread().getName());
//        for (ManDirectoryDTO children : childrens) {
//            writeFile(children.getPath(), children.getTitle());
//        }
//    }
    /**
     * 写入指定文件数据到本地
     *
     * @param path
     */
    public void writeFile(String path, String title) {
        // 获取文件内容
        String content = manService.getContent(path);

        // 文件路径处理

        String filePath = path.replace("-", "\\");
        if (filePath.contains(":")) {
            return;
        }

        // 寻找并输出第一个匹配到的数字
        String number = null;
        Pattern pattern = Pattern.compile("\\d");
        Matcher matcher = pattern.matcher(filePath);
        if (matcher.find()) {
            number = matcher.group();
        }

        // 如果找到破折号，截取前面的字符；否则，保持原始字符串
        int indexOfDash = path.indexOf("-");
        String firstPath = (indexOfDash != -1) ? path.substring(0, indexOfDash) : path;

        // 写入文件路径
        String writePath = fileRoot + firstPath + "\\" + title + "." + number + ".html";

        // 本地路径，判断是否有文件夹，没有则创建
        try {
            checkAndCreateFolders(fileRoot + firstPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 写文件
        try {
            createAndWriteFile(writePath, content);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 检查并创建文件夹，使用NIO
     *
     * @param fullPath
     * @throws IOException
     */
    private void checkAndCreateFolders(String fullPath) throws IOException {
        Path path = Paths.get(fullPath);

        for (int i = 0; i < path.getNameCount(); i++) {
            Path subpath = path.subpath(0, i + 1);
            if (!Files.exists(subpath) || !Files.isDirectory(subpath)) {
                Files.createDirectories(subpath);
                System.out.println("创建目录:" + subpath);
            }
        }
    }


}
