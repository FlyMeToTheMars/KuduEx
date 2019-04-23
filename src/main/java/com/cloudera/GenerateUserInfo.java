package com.cloudera;

import javafx.beans.property.IntegerProperty;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 模拟生成数据
 */
public class GenerateUserInfo {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("缺少输入参数，[filepath]");
            System.exit(0);
        }

        String filePath = args[0];



        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(filePath);
            long begin3 = System.currentTimeMillis();
            int num = Cities.bankNames.length;
            for(int i =0; i < num; i++) {
                if(i%10 == 0) {
                    for(int a = 0; a < 500000; a++) {
                        fw.write(RandomUserInfo.getUserInfo(Cities.bankNames[i]) + "\r\n");
                    }
                } else {
                    for(int b = 0; b < 55000; b ++ ) {
                        fw.write(RandomUserInfo.getUserInfo(Cities.bankNames[i]) + "\r\n");
                    }
                }

            }

            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
