package com.example.tangjie.news;

import android.os.Message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.R.id.list;

/**
 * Created by 波 on 2018/1/1.
 */

public class HttpUtils {

    //从输入流读取数据
    public static String readInputStream(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"GBK"));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null){
            response.append(line);
        }
        return response.toString();
    }

    //解析主网页新闻数据
    public static boolean parseWithJsoup(String response,List<Contentmoudel> list) throws IOException {
        Document document = Jsoup.parse(response);
        Elements links = document.select("div.content");
        for (Element link :links){
            Contentmoudel contentmoudel = new Contentmoudel();
            contentmoudel.setTitle(link.select("a").text());
            contentmoudel.setLinkTitle("http:"+link.select("a").attr("href"));

            //遍历img 所有标签
            Attributes node = link.select("img").first().attributes();
            Iterator<Attribute> iterator = node.iterator();
            while (iterator.hasNext()) {
                Attribute attribute = iterator.next();
                String key = attribute.getKey();
                //属性中包含“src”字符串，但不是src的属性
                if (!key.equals("src") && key.contains("src")) {
                    String  otherSrc = attribute.getValue();
                    contentmoudel.setImageUrl(otherSrc);
  //                  contentmoudel.setImage(otherSrc);
                }else if (key.equals("src")){
                    String src = attribute.getValue();
                    contentmoudel.setImageUrl(src);
//                    contentmoudel.setImage(src);
                }
            }

            list.add(contentmoudel);
        }
        return true;
    }

    //发送网络数据请求
    //使用ContentMoudel获取linkAddress传入参数
    public static String sendRequestWithHttpUrlConnection(String address){
        String response = null;
        int code;       //定义返回码
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(address);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");      //设置请求方式
            httpURLConnection.setConnectTimeout(5000);
            code = httpURLConnection.getResponseCode();
            if (code == 200){
                //code==200，说明获取正常
                InputStream inputStream = httpURLConnection.getInputStream();
                response = HttpUtils.readInputStream(inputStream).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (httpURLConnection != null){
                httpURLConnection.disconnect();
            }
        }
        return response;
    }

    public static String parseNewsContent(String response){

        //获取内容
        Document document = Jsoup.parse(response);
        Elements elements = document.select("p");
        StringBuilder  stringBuilder = new StringBuilder();
        stringBuilder.append("    ");
        for (Element element : elements){
            String page = element.text();
            stringBuilder.append(page);
            stringBuilder.append("\r\n");
            stringBuilder.append("    ");
        }

        //获取时间
        return stringBuilder.toString();
    }



    public static boolean parseFirstPage(String response,List<Contentmoudel> list){
        Document document = Jsoup.parse(response);
        Elements links = document.select("div.Q-tpWrap");
        for (Element link : links){
            Contentmoudel contentmoudel = new Contentmoudel();
            contentmoudel.setTitle(link.select("a.linkto").text());
            contentmoudel.setLinkTitle(link.select("a.linkto").attr("abs:href"));
            contentmoudel.setImageUrl(link.select("img.picto").attr("src"));
            list.add(contentmoudel);
        }
        return true;
    }
}
