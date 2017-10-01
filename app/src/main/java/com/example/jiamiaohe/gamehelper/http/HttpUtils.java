package com.example.jiamiaohe.gamehelper.http;

import android.os.Environment;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiamiaohe on 2017/9/30.
 */

public class HttpUtils {

    private static HttpUtils mHttpUtils = null;

    private final String TAG = "HttpUtils";

    private HttpUtils() {
        for(int i = 0; i < mPersonArray.length; i++) {
            mPersonArray[i] = new Person();
        }
    }

    public static HttpUtils getInstance() {
        if (mHttpUtils == null) {
            mHttpUtils = new HttpUtils();
        }

        return mHttpUtils;
    }

    public static final String ADD_URL = "http://123.207.82.157:8989/kog_equip_recommend";

    public void requestInThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                request();
            }
        }).start();
    }

    private void request() {

        try {
            //创建连接
            URL url = new URL(ADD_URL);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            //connection.setRequestProperty("Content-Type",
            //        "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-type", "text/html");
            connection.setRequestProperty("Accept-Charset", "utf-8");
            connection.setRequestProperty("contentType", "text/json");

            connection.connect();

            //POST请求
            DataOutputStream out = new DataOutputStream(
                    connection.getOutputStream());
//            JSONObject obj = new JSONObject();
//            obj.element("app_name", "asdf");
//            obj.element("app_ip", "10.21.243.234");
//            obj.element("app_port", 8080);
//            obj.element("app_type", "001");
//            obj.element("app_area", "asd");

            String json = "{\"group_a\":[{\"hero_name\":\"狄仁杰\",\"skill\":\"惩戒\",\"equip_list\":[\"末世\"],\"level\":15,\"host\":1},{\"hero_name\":\"王昭君\",\"skill\":\"闪现\",\"equip_list\":[\"末世\"],\"level\":15,\"host\":0},{\"hero_name\":\"王昭君\",\"skill\":\"闪现\",\"equip_list\":[\"末世\"],\"level\":15,\"host\":0},{\"hero_name\":\"王昭君\",\"skill\":\"闪现\",\"equip_list\":[\"末世\"],\"level\":15,\"host\":0},{\"hero_name\":\"王昭君\",\"skill\":\"闪现\",\"equip_list\":[\"末世\"],\"level\":15,\"host\":0}],\"group_b\":[{\"hero_name\":\"曹操\",\"skill\":\"疾跑\",\"equip_list\":[\"贤者庇护\"],\"level\":12,\"host\":0},{\"hero_name\":\"曹操\",\"skill\":\"疾跑\",\"equip_list\":[\"贤者庇护\"],\"level\":12,\"host\":0},{\"hero_name\":\"曹操\",\"skill\":\"疾跑\",\"equip_list\":[\"贤者庇护\"],\"level\":12,\"host\":0},{\"hero_name\":\"诸葛亮\",\"skill\":\"疾跑\",\"equip_list\":[\"贤者庇护\"],\"level\":12,\"host\":0},{\"hero_name\":\"王昭君\",\"skill\":\"疾跑\",\"equip_list\":[\"贤者庇护\"],\"level\":12,\"host\":0}]}";

            JSONObject main = new JSONObject();
            JSONArray groupA = new JSONArray();
            JSONArray groupB = new JSONArray();
            main.put("group_a", groupA);
            main.put("group_b", groupB);

            String[] names = {"狄仁杰", "王昭君", "王昭君", "王昭君", "王昭君", "曹操", "曹操", "曹操", "曹操", "曹操"};
            String[] skills = {"闪现", "闪现", "闪现", "闪现", "闪现", "闪现", "闪现", "闪现", "闪现", "闪现"};
            for(int i = 0; i < 10; i++) {
                JSONObject person = new JSONObject();

                //fill fake data
//                person.put("hero_name", names[i]);
//                person.put("skill", skills[i]);
//                person.put("level", 15);
//                person.put("host", i==0?1:0);
//                JSONArray equip = new JSONArray();
//                equip.put(0, "末世");
//                person.put("equip_list", equip);

                //fill true data
                person.put("hero_name", mPersonArray[i].name);
                person.put("skill", mPersonArray[i].skill);
                person.put("level", mPersonArray[i].level);
                person.put("host", mPersonArray[i].host);
                JSONArray equip = new JSONArray();

                for(int j = 0; j <  mPersonArray[i].equipList.size(); j++) {
                    equip.put(j, mPersonArray[i].equipList.get(j));
                }
                person.put("equip_list", equip);

                if (i < 5) {
                    groupA.put(i, person);
                } else {
                    groupB.put(i-5, person);
                }
            }
            //String jsonString = toUtf8("");
            String jsonUTF8 = java.net.URLEncoder.encode(main.toString(), "utf-8");
            Log.i(TAG, "send json = "+main.toString());
            out.writeBytes(toUtf8(jsonUTF8));
            out.flush();
            out.close();

            //读取响应
            Log.i(TAG, "getResponseCode = "+connection.getResponseCode());
            if(connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String lines;
                StringBuffer sb = new StringBuffer("");
                while ((lines = reader.readLine()) != null) {
                    lines = URLDecoder.decode(lines, "utf-8");
                    sb.append(lines);
                }
                Log.i(TAG, "" + ascii2native(sb.toString()));
                //System.out.print(ascii2native(sb.toString()));
                //JSONObject jsStr = new JSONObject(sb.toString());

                reader.close();
            }
            // 断开连接
            connection.disconnect();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static String toUtf8(String str) {
        String result = null;
        try {
            result = new String(str.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    private static String ascii2native ( String asciicode )
    {
        String[] asciis = asciicode.split ("\\\\u");
        String nativeValue = asciis[0];
        try
        {
            for ( int i = 1; i < asciis.length; i++ )
            {
                String code = asciis[i];
                nativeValue += (char) Integer.parseInt (code.substring (0, 4), 16);
                if (code.length () > 4)
                {
                    nativeValue += code.substring (4, code.length ());
                }
            }
        }
        catch (NumberFormatException e)
        {
            return asciicode;
        }
        return nativeValue;
    }

    public class Person {
        String name = "";
        String skill = "";
        int level = 1;
        int host = 0;
        List<String> equipList = new ArrayList();
    }

    Person mPersonArray[] = new Person[10];

    public void clearInformation() {
        for(Person person : mPersonArray) {
            person.name = "";
            person.skill = "";
            person.level = 1;
            person.host = 0;
            person.equipList.clear();
        }
    }

    public void fillPersonArray(int index, String name, String skill, String level, int host, List<String> equipList) {
        String numLevel = level.replaceAll("[^0-9]", "");
        int levelInt = Integer.parseInt(numLevel);
        Log.i(TAG, "fillPersonArray host = "+host+" level = "+level+", numLevel = "+numLevel+", levelInt = "+levelInt);

        mPersonArray[index].name = name;
        mPersonArray[index].skill = skill;
        mPersonArray[index].level = levelInt;
        mPersonArray[index].host = host;
        for(String equipName : equipList) {
            if (equipName != null && !equipName.equals("空")) {
                mPersonArray[index].equipList.add(equipName);
            }
        }
        Log.i(TAG, "equipList.size = "+equipList.size()+", mPersonArray[index].equipList.size = "+mPersonArray[index].equipList.size());

        if (index == 9) {
            requestInThread();
        }
    }
}
