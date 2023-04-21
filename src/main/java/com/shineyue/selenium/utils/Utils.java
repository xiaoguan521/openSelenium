package com.shineyue.selenium.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author liuXiaoChen
 * Date 2022-11-03 - 19:05
 * Description TODO
 */
public class Utils {

    public static String getPath(String laiyuan,String geshi){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        String name = laiyuan+df.format(new Date())+(int)(Math.random()*100) + geshi;
        String fileName = "D:/screen/temp/"+name;
        String os = "os.name", linux = "linux", linux1 = "Linux";
        if (System.getProperty(os).toLowerCase().startsWith(linux)
                || System.getProperty(os).toLowerCase().startsWith(linux1)) {
            fileName = "/home/screen/temp/";
            File file = new File(fileName);
            if (!file.exists()) {
                file.mkdirs();
            }
            fileName = fileName + name;
        }
        return fileName;
    }
}
