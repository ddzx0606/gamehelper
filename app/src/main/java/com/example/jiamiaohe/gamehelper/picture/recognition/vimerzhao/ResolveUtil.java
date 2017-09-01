package com.example.jiamiaohe.gamehelper.picture.recognition.vimerzhao;


import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.example.jiamiaohe.gamehelper.picture.recognition.PlayerAnalys;
import com.youtu.Youtu;

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

import static com.example.jiamiaohe.gamehelper.picture.recognition.PlayerAnalys.HEIGHT_DELTA;

public class ResolveUtil {

    public static final String APP_ID = "10096521";
    public static final String SECRET_ID = "AKIDpZBev3Nz3AD6oKvZyEJCxWt4FLKC6Qlu";
    public static final String SECRET_KEY = "FUbaNF0ayAAzUpxgmifXD3kHfsz3LYaV";
    public static final String USER_ID = "1769898935";
    public final static int NUMBER = 10;
    private final static int ITEM_COUNT = 6;
    private static Bitmap[] mPerPlayer = new Bitmap[NUMBER];
    private static Bitmap mTotalPlayer;

    private static Youtu faceYoutu;

    public static Youtu getYoutuInstance() {
        if (faceYoutu == null) {
            faceYoutu = new Youtu(APP_ID, SECRET_ID, SECRET_KEY, Youtu.API_YOUTU_END_POINT,USER_ID);
        }
        return faceYoutu;
    }

    static String path = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/tmp.png");

    public static void resolve(PlayerAnalys[] players) {
        for (int i = 0; i < players.length; i++) {
            players[i].setBreak(players[players.length-2].getmPlayerBitmap());
            mPerPlayer[i] =  players[i].combineHorizontal();
            System.out.println(i+"<<<<<<<<<<<");
        }
        mTotalPlayer = PlayerAnalys.combineVertical(mPerPlayer);

        // 锐化之后准确率反而下降？？
        // mTotalPlayer = sharpBitmap(mTotalPlayer);
        // 需要调试是写到存储卡上
        writeBitmap(path, mTotalPlayer);

    }

    public static void getData() {
        try {
            System.out.println("=============结果================");
            //JSONObject res = ResolveUtil.getYoutuInstance().GneneralOcr(path);
            JSONObject res = faceYoutu.GeneralOcrWithBitmap(mTotalPlayer);
            handle(res);
        } catch (IOException | JSONException | KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static ReturnData[] sTempData = new ReturnData[ITEM_COUNT*NUMBER];

    private static void handle(JSONObject res) throws JSONException {
        final int ITEM_COUNT = ResolveUtil.ITEM_COUNT;
        JSONArray array = res.getJSONArray("items");
        ReturnData[] returnDatas = new ReturnData[ITEM_COUNT];
        for (int i = 0; i < returnDatas.length; i++) {
            returnDatas[i] = new ReturnData();
        }
        for (int i = 0; i < sTempData.length; i++) {
            sTempData[i] = new ReturnData();

            sTempData[i].data = null;
            sTempData[i].xLoc = Integer.MAX_VALUE;
            sTempData[i].yLoc = Integer.MAX_VALUE;
        }
        int count = 0;

        for (int i = 0; i < array.length(); i++) {
            //每行个，识别顺序可能变化，所以需要根据x大小排序，y轴顺序较为稳定，所以暂时不做排序
            if (i!=0 && i%ITEM_COUNT== 0) {
                Arrays.sort(returnDatas);

                System.out.printf("%d: ", ++count);
                for (int k = 0; k < returnDatas.length; k++) {
                    data[count-1][k] = returnDatas[k].data;
                }
            }
            returnDatas[i%ITEM_COUNT].data = (String) ((JSONObject)array.get(i)).get("itemstring");
            returnDatas[i%ITEM_COUNT].xLoc = Integer.parseInt(((JSONObject)((JSONObject)array.get(i)).get("itemcoord")).get("x").toString());
            returnDatas[i%ITEM_COUNT].yLoc = Integer.parseInt(((JSONObject)((JSONObject)array.get(i)).get("itemcoord")).get("y").toString());

            sTempData[i].data = (String) ((JSONObject)array.get(i)).get("itemstring");
            sTempData[i].xLoc = Integer.parseInt(((JSONObject)((JSONObject)array.get(i)).get("itemcoord")).get("x").toString());
            sTempData[i].yLoc = Integer.parseInt(((JSONObject)((JSONObject)array.get(i)).get("itemcoord")).get("y").toString());

        }

        correctData();
    }

    private static int[] mLocationFlag = new int[NUMBER];
    private static ReturnData[][] sReturnData = new ReturnData[NUMBER][ITEM_COUNT];
    private static String[][] data = new String[NUMBER][ITEM_COUNT];
    static {
        mLocationFlag[0] = 0;
        for (int i = 1; i < mLocationFlag.length; i++) {
            mLocationFlag[i] = mLocationFlag[i-1] + HEIGHT_DELTA;
        }
    }
    /**
     * 修正数据，保证排版为10行6列，每行两个字符串，4个数字
     */
    private static void correctData() {
        // 思路，先看一下y的浮动范围，消除浮动在先按照y排序，在按照x排序
        Arrays.sort(sTempData);

        for (int i = 0; i < sTempData.length; i++) {
            System.out.println(sTempData[i].toString());
        }
        // 经过排序，如果不是“名字，名字，数字，数字，数字，数字”就需要修正

        for (int i = 0; i < sReturnData.length; i++) {
            for (int j = 0; j < sReturnData[0].length; j++) {
                sReturnData[i][j] = new ReturnData();
            }
        }
        int iCount = 0;
        // 默认文字能识别出来
        for (int i = 0; i < sTempData.length;) {
            if (iCount >= 10) break;

            int jCount = 0;
            sReturnData[iCount][jCount].data = sTempData[i].data;
            sReturnData[iCount][jCount].xLoc = sTempData[i].xLoc;
            sReturnData[iCount][jCount++].yLoc = sTempData[i++].yLoc;

            sReturnData[iCount][jCount].data = sTempData[i].data;
            sReturnData[iCount][jCount].xLoc= sTempData[i].xLoc;
            sReturnData[iCount][jCount++].yLoc = sTempData[i++].yLoc;

            for (int k = 0; k < 4;  k++) {
                // 这里有一种情况十分棘手，就是识别出来了，但成了字母
                // 暂时用长度为1定位
                // TODO: 其实需要处理的就是 中间3个较小的数字
                if (sTempData[i].data != null) {
                    if (TextUtils.isDigitsOnly(sTempData[i].data)) {
                        sReturnData[iCount][jCount].xLoc= sTempData[i].xLoc;
                        sReturnData[iCount][jCount].yLoc= sTempData[i].yLoc;
                        sReturnData[iCount][jCount++].data = sTempData[i++].data;
                    } else if (sTempData[i].data.length() == 1) {
                        sReturnData[iCount][jCount].xLoc = sTempData[i].xLoc;
                        sReturnData[iCount][jCount].yLoc = sTempData[i].yLoc;
                        sReturnData[iCount][jCount++].data = sTempData[i++].data;
                    }
                } else {
                    sReturnData[iCount][jCount++].xLoc = Integer.MAX_VALUE;
                    sReturnData[iCount][jCount++].yLoc = Integer.MAX_VALUE;
                    sReturnData[iCount][jCount++].data = "?";
                }
            }

            iCount++;
        }
        // 调整位置,硬编码后期需要优化
        int[] arverageLoc = new int[]{12, 342, 600, 790, 970, 1140};
        for (int i = 0; i < sReturnData.length; i++) {
            for (int j = 0; j < sReturnData[i].length; j++) {
                if (Math.abs(sReturnData[i][j].xLoc - arverageLoc[j]) > 50) {
                    for (int k = sReturnData[i].length-1; k > j; k--) {
                        sReturnData[i][k].data = sReturnData[i][k-1].data;
                        sReturnData[i][k].xLoc = sReturnData[i][k-1].xLoc;
                    }
                    sReturnData[i][j].data = "?";
                }
            }
        }
        for (int i = 0; i < sReturnData.length; i++) {
            for (int j = 0; j < sReturnData[i].length; j++) {
                data[i][j] = sReturnData[i][j].data;
            }
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
        int yLoc;

        @Override
        public String toString() {
            return data + "--" + xLoc + "--" + yLoc;
        }

        @Override
        public int compareTo(@NonNull ReturnData o) {
            if (Math.abs(this.yLoc - o.yLoc) > 20) {
                return this.yLoc - o.yLoc;
            }
            return this.xLoc - o.xLoc;
        }
    }

    //锐化
    public static Bitmap sharpBitmap(Bitmap bitmap) {
        int width, height;
        height = bitmap.getHeight();
        width = bitmap.getWidth();
        int[] pix = new int[width * height];
        bitmap.getPixels(pix, 0, width, 0, 0, width, height);


        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width+ x;
                int R = (pix[index] >> 16) & 0xff;     //bitwise shifting
                int G = (pix[index] >> 8) & 0xff;
                int B = pix[index] & 0xff;
                if (R < 100 && G < 100 && B < 100 ) {
                    R = G = B = 0;
                } else if (R > 200 && G > 200 && B > 200 ) {
                    R = G = B = 255;
                }
                pix[index] = 0xff000000 | (R << 16) | (G << 8) | B;
            }
        }
        bitmap.setPixels(pix, 0, width, 0, 0, width, height);

        return bitmap;
    }

    public static String[] getItem(int index) {

        return data[index];
    }
}
