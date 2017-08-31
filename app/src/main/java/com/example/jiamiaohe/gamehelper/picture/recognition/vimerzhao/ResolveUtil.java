package com.example.jiamiaohe.gamehelper.picture.recognition.vimerzhao;


import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.example.jiamiaohe.gamehelper.picture.recognition.PlayerAnalys;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ResolveUtil {

    public static final String APP_ID = "10096521";
    public static final String SECRET_ID = "AKIDpZBev3Nz3AD6oKvZyEJCxWt4FLKC6Qlu";
    public static final String SECRET_KEY = "FUbaNF0ayAAzUpxgmifXD3kHfsz3LYaV";
    public static final String USER_ID = "1769898935";
    private final static int NUMBER = 10;
    private final static int ITEM_COUNT = 6;
    private static Bitmap[] mPerPlayer = new Bitmap[NUMBER];
    private static Bitmap mTotalPlayer;

    private static MyYoutu faceYoutu;
    public static MyYoutu getYoutuInstance() {
        if (faceYoutu == null) {
            faceYoutu = new MyYoutu(APP_ID, SECRET_ID, SECRET_KEY, MyYoutu.API_YOUTU_END_POINT,USER_ID);
        }
        return faceYoutu;
    }

    private static  String[][] data = new String[NUMBER][ITEM_COUNT];

    static String path = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/tmp.png");
    public static void resolve(PlayerAnalys[] players) {
        for (int i = 0; i < players.length; i++) {
            players[i].setBreak(players[players.length-2].getmPlayerBitmap());
            mPerPlayer[i] =  players[i].combineHorizontal();
            System.out.println(i+"<<<<<<<<<<<");
        }
        mTotalPlayer = PlayerAnalys.combineVertical(mPerPlayer);
        writeBitmap(path, mTotalPlayer);

    }
    public static void getData() {
        try {
            System.out.println("=============结果================");
            JSONObject res = ResolveUtil.getYoutuInstance().GneneralOcr(path);
            handle(res);
        } catch (IOException | JSONException | KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static void handle(JSONObject res) throws JSONException {

        final int ITEM_COUNT = ResolveUtil.ITEM_COUNT;
        JSONArray array = res.getJSONArray("items");
        ReturnData[] returnDatas = new ReturnData[ITEM_COUNT];
        for (int i = 0; i < returnDatas.length; i++) {
            returnDatas[i] = new ReturnData();
        }

        int count = 0;

        for (int i = 0; i < array.length(); i++) {
            //每行个，识别顺序可能变化，所以需要根据x大小排序，y轴顺序较为稳定，所以暂时不做排序

            if (i!=0 && i%ITEM_COUNT== 0) {
                Arrays.sort(returnDatas);

                System.out.printf("%d: ", ++count);
                for (int k = 0; k < returnDatas.length; k++) {
                    data[count-1][k] = returnDatas[k].data;
                    System.out.printf("%s--", returnDatas[k].data);
                }
                System.out.println();
            }
            returnDatas[i%ITEM_COUNT].data = (String) ((JSONObject)array.get(i)).get("itemstring");
            returnDatas[i%ITEM_COUNT].xLoc = Integer.parseInt(((JSONObject)((JSONObject)array.get(i)).get("itemcoord")).get("x").toString());

        }
    }
    private static void writeBitmap(String path, Bitmap bitmap) {
        File file = new File(path);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static class ReturnData implements  Comparable<ReturnData> {
        String data;
        int xLoc;
        @Override
        public int compareTo(@NonNull ReturnData o) {
            return this.xLoc - o.xLoc;
        }
    }

    public static String[] getItem(int index) {
        return data[index];
    }
}
