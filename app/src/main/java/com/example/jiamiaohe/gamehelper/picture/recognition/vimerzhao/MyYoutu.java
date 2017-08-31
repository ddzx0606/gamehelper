package com.example.jiamiaohe.gamehelper.picture.recognition.vimerzhao;

import com.youtu.sign.Base64Util;
import com.youtu.sign.YoutuSign;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MyYoutu {
    public static final String API_YOUTU_END_POINT = "https://api.youtu.qq.com/youtu/";
    public static final String API_YOUTU_CHARGE_END_POINT = "https://vip-api.youtu.qq.com/youtu/";
    public static final String API_TENCENTYUN_END_POINT = "https://youtu.api.qcloud.com/youtu/";
    private static int EXPIRED_SECONDS = 2592000;
    private String m_appid;
    private String m_secret_id;
    private String m_secret_key;
    private String m_end_point;
    private String m_user_id;
    private boolean m_not_use_https;

    public MyYoutu(String appid, String secret_id, String secret_key, String end_point, String user_id) {
        this.m_appid = appid;
        this.m_secret_id = secret_id;
        this.m_secret_key = secret_key;
        this.m_end_point = end_point;
        this.m_user_id = user_id;
        this.m_not_use_https = !end_point.startsWith("https");
    }

    private void GetBase64FromFile(String filePath, StringBuffer base64) throws IOException {
        File imageFile = new File(filePath);
        if(imageFile.exists()) {
            InputStream in = new FileInputStream(imageFile);
            byte[] data = new byte[(int)imageFile.length()];
            in.read(data);
            in.close();
            base64.append(Base64Util.encode(data));
        } else {
            throw new FileNotFoundException(filePath + " not exist");
        }
    }

    private JSONObject SendHttpRequest(JSONObject postData, String mothod) throws IOException, JSONException, KeyManagementException, NoSuchAlgorithmException {
        StringBuffer mySign = new StringBuffer("");
        YoutuSign.appSign(this.m_appid, this.m_secret_id, this.m_secret_key, System.currentTimeMillis() / 1000L + (long)EXPIRED_SECONDS, this.m_user_id, mySign);
        System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
        System.setProperty("sun.net.client.defaultReadTimeout", "30000");
        URL url = new URL(this.m_end_point + mothod);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("accept", "*/*");
        connection.setRequestProperty("user-agent", "youtu-java-sdk");
        connection.setRequestProperty("Authorization", mySign.toString());
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Content-Type", "text/json");
        connection.connect();
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        postData.put("app_id", this.m_appid);
        out.write(postData.toString().getBytes("utf-8"));
        out.flush();
        out.close();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuffer resposeBuffer = new StringBuffer("");

        String lines;
        while((lines = reader.readLine()) != null) {
            lines = new String(lines.getBytes(), "utf-8");
            resposeBuffer.append(lines);
        }

        reader.close();
        connection.disconnect();
        JSONObject respose = new JSONObject(resposeBuffer.toString());
        return respose;
    }

    private JSONObject SendHttpsRequest(JSONObject postData, String mothod) throws NoSuchAlgorithmException, KeyManagementException, IOException, JSONException {
        SSLContext sc = SSLContext.getInstance("SSL");

        sc.init((KeyManager[])null, new TrustManager[]{new TrustAnyTrustManager((TrustAnyTrustManager)null)}, new SecureRandom());
        StringBuffer mySign = new StringBuffer("");
        YoutuSign.appSign(this.m_appid, this.m_secret_id, this.m_secret_key, System.currentTimeMillis() / 1000L + (long)EXPIRED_SECONDS, this.m_user_id, mySign);
        System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
        System.setProperty("sun.net.client.defaultReadTimeout", "30000");
        URL url = new URL(this.m_end_point + mothod);
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        connection.setSSLSocketFactory(sc.getSocketFactory());

        connection.setHostnameVerifier(new TrustAnyHostnameVerifier((TrustAnyHostnameVerifier)null));
        connection.setRequestMethod("POST");
        connection.setRequestProperty("accept", "*/*");
        connection.setRequestProperty("user-agent", "youtu-java-sdk");
        connection.setRequestProperty("Authorization", mySign.toString());
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Content-Type", "text/json");
        connection.connect();
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        postData.put("app_id", this.m_appid);
        out.write(postData.toString().getBytes("utf-8"));
        out.flush();
        out.close();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuffer resposeBuffer = new StringBuffer("");

        String lines;
        while((lines = reader.readLine()) != null) {
            lines = new String(lines.getBytes(), "utf-8");
            resposeBuffer.append(lines);
        }

        reader.close();
        connection.disconnect();
        JSONObject respose = new JSONObject(resposeBuffer.toString());
        return respose;
    }



    public JSONObject GneneralOcr(String image_path) throws IOException, JSONException, KeyManagementException, NoSuchAlgorithmException {
        StringBuffer image_data = new StringBuffer("");
        JSONObject data = new JSONObject();
        this.GetBase64FromFile(image_path, image_data);
        data.put("image", image_data.toString());
        JSONObject respose = this.m_not_use_https?this.SendHttpRequest(data, "ocrapi/generalocr"):this.SendHttpsRequest(data, "ocrapi/generalocr");
        return respose;
    }
    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        private TrustAnyHostnameVerifier() {
        }

        public TrustAnyHostnameVerifier(TrustAnyHostnameVerifier trustAnyHostnameVerifier) {

        }

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static class TrustAnyTrustManager implements X509TrustManager {
        private TrustAnyTrustManager() {
        }

        public TrustAnyTrustManager(TrustAnyTrustManager trustAnyTrustManager) {

        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}

