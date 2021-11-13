#!/usr/bin/env bash

# 关于 ： 语法 是在 PermissionDispatcher 学的，这个语法还是可以继续研究一下的
# 在 `/lib_img_crop` 目录下执行命令
./gradlew :lib_img_crop:clean
./gradlew :lib_img_crop:build
./gradlew :lib_img_crop:publish

#pwd