package com.shineyue.selenium.controller;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import com.shineyue.selenium.utils.Tools;
import com.shineyue.selenium.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author liuXiaoChen
 * Date 2022-11-15 - 17:35
 * Description
 */
@RestController
public class OpenSeleniumController {
    @Autowired
    private Tools tools;
    @RequestMapping(value = "/openSelenium/getUrl2Image.service", method = RequestMethod.POST)
    public JSONObject getUrlImage(@RequestBody JSONObject json) {
        String domain=json.getStr("domain");
        System.out.println("接收到的参数"+domain);
        String path = Utils.getPath("url2image", ".png");
        try {
            double screenshot = tools.screenshot(domain, 1L, 800, 100L, 1L,path);
            System.out.println("转换耗时："+screenshot+"s");
            json.put("sucess",true);
            json.put("path",path);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("异常了");
            json.put("sucess",false);
            json.put("msg", e.getMessage());
        }
        return json;
    }
}
