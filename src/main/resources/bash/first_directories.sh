#!/bin/bash

# 创建名为manpages的数据库
mysql -u root -p888888 -e "CREATE DATABASE IF NOT EXISTS manpages;"

# 创建名为first_page的表
mysql -u root -p888888 manpages -e "CREATE TABLE first_directories (
  id INT NOT NULL AUTO_INCREMENT,
  directory_name VARCHAR(20) NOT NULL,
  PRIMARY KEY (id)
);"

# 遍历/usr/share/man目录下以"man"开头的目录，并将其名称插入到first_page表中
for dir in /usr/share/man/man*; do
    directory_name=$(basename "$dir")
    mysql -u root -p888888 manpages -e "INSERT INTO first_directories (directory_name) VALUES ('$directory_name')"
done
