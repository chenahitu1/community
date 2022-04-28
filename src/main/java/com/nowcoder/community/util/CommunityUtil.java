package com.nowcoder.community.util;




import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {
    //生成随机字符串
    public static String generateUUID(){
        //UUID是java封装的一个生成随机字符串的类 里面包括横线
        return UUID.randomUUID().toString().replace("-","");
    }

    //MD5加密
    //hello+随机字符串-》密文
    public static String md5(String key){
        //判断明文是否为空
        if(StringUtils.isBlank(key)){
           return null;
        }
        //生成密文
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject json=new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map!=null){
            for(String key:map.keySet()){
                json.put(key,map.get(key));
            }
        }
        return json.toString();
    }
    public static String getJSONString(int code,String msg){
        return getJSONString(code,msg,null);
    }
    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }

    public static void main(String[] args) {
        Map<String,Object> map=new HashMap<>();
        map.put("name","张三");
        map.put("age","21");
        System.out.println(getJSONString(0,"ok",map));

    }
}
