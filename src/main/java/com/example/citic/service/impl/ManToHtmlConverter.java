package com.example.citic.service.impl;
// 实现了html文件中的超链接设置
import java.io.*;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManToHtmlConverter {
    // 用于给已经转换好的HTML文件中对应地方添加全局超链接的方法
    public static String hyperLink(String line) {
        // 在现在的HTML文件中，需要匹配的格式是<*>*******</*>(*)
        // 同时存在部分格式为****(*) 因此做了两次正则匹配
        Pattern pattern = Pattern.compile("(\\w+)\\((\\d+)\\)");
        Matcher matcher = pattern.matcher(line);

        while (matcher.find()) {
            String newLetterPart = linkTrans(matcher);

            line = line.replace(matcher.group(0), newLetterPart);

        }

        Pattern patternType2 = Pattern.compile("<[a-zA-Z]>(\\w+)</[a-zA-Z]>\\((\\d+)\\)");
        Matcher matcherType2 = patternType2.matcher(line);

        while (matcherType2.find()) {
            String newLetterPart = linkTrans(matcherType2);

            line = line.replace(matcherType2.group(0), newLetterPart);
        }


        return line;
    }

    // 正则匹配内容替换
    private static String linkTrans(Matcher matcher) {
        String letterPart = matcher.group(1); // 页面标题信息
        String numberPart = matcher.group(2); // 页面所属的章节信息

        // 将匹配到的内容替换为HTML语言的超链接格式，后续可以根据需求再修改
        // 目前设置的超链接地址：www.testlink_" + letterPart + "_" + numberPart + ".com，
        // 页面上显示的内容为：test_hyper_link_ + letterPart
        String newLetterPart = String.format(
                "<a href=\"%s\" title=\"%s\" target=\"%s\">%s</a> ",
                String.format("man%s-%s", numberPart, letterPart), // 超链接跳转的地址，例：man2-link
                letterPart,
                "_blank",
                letterPart + String.format("(%s)", numberPart) // 页面上显示的文本 例link(2)
        );
        return newLetterPart;
    }

    // 用于对数据库中content字段内容进行更新，使用jdbc完成，请按需修改
    public static void updateSQL() {
        String url = "jdbc:mysql://localhost:3306/mans?serverTimezone=UTC";
        String username = "root";
        String password = "";

        String selectQuery = "SELECT content, id FROM secondary_dir";
        String updateTST = "UPDATE secondary_dir SET content = ? WHERE id = ?";

        try {
            // 建立数据库连接
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);

            // 查询数据库
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = selectStatement.executeQuery();

            // 处理查询结果
            while (resultSet.next()) {
                String content = resultSet.getString("content");
                int id = resultSet.getInt("id");

                // 对内容进行更新
                String updatedContent = hyperLink(content);

                // 执行更新操作
                PreparedStatement updateStatement = connection.prepareStatement(updateTST);
                updateStatement.setString(1, updatedContent);
                updateStatement.setInt(2, id);

                int rowsAffected = updateStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Update successful for row with ID " + id);
                } else {
                    System.out.println("No records were updated for row with ID " + id);
                }

                // 关闭更新操作的 statement
                updateStatement.close();
            }

            // 关闭查询操作的 statement 和数据库连接
            resultSet.close();
            selectStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    public static String readFile(String filePath) {
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }


    public static void writeHtmlFile(String filePath, String htmlContent) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(htmlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) throws IOException {

        updateSQL();

//        String tstfile = readFile("C:\\Users\\QQQQ\\Desktop\\tstLink.txt");
//        String tstOutput = hyperLink(tstfile);
//        writeHtmlFile("C:\\Users\\QQQQ\\Desktop\\tstLink.html",tstOutput);
    }
}