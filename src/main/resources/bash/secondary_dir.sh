#!/bin/bash
set -e

# 创建名为manpages的数据库
mysql -u root -p888888 -e "CREATE DATABASE IF NOT EXISTS manpages;"

# 创建名为secondary_directories的表
mysql -u root -p888888 manpages <<EOF
CREATE TABLE IF NOT EXISTS secondary_dir (
  id INT NOT NULL AUTO_INCREMENT,
  subdirectory_name VARCHAR(50) NOT NULL,
  primary_directory_name VARCHAR(50) NOT NULL,
  content LONGTEXT NOT NULL,
  PRIMARY KEY (id)
);
EOF

# 遍历所有以man开头的一级目录
for dir in /usr/share/man/man*; do
    if [ -d "$dir" ]; then
        primary_directory_name=$(basename "$dir")  # 提取一级目录名
        for file in "$dir"/*.gz; do
            if [ -f "$file" ]; then
                base=$(basename "$file")
                # 提取二级目录名并去除倒数前两个后缀
                subdirectory_name=$(echo "$base" | awk -F. '{sub(/\.[0-9a-zA-Z]+$/, ""); sub(/\.[0-9a-zA-Z]+$/, ""); print}')
                # 使用命令替换，将man命令的输出保存到txt文件中
                man -Thtml -P cat "$file" > "$file.txt" 2>/dev/null
                # 读取txt文件内容并进行特殊字符转义
                content=$(cat "$file.txt" | sed "s/'/''/g")  # 对单引号进行转义
                content=$(echo "$content" | sed 's/\\/\\\\/g')  # 对反斜杠进行转义
                content=$(echo "$content" | sed 's/&/\\&/g')  # 对&进行转义

                mysql -u root -p888888 manpages <<EOF
INSERT INTO secondary_dir (subdirectory_name, primary_directory_name, content) VALUES ('$subdirectory_name', '$primary_directory_name', '$content');
EOF
            fi
        done
    fi
done