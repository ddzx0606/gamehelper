package com.example.jiamiaohe.gamehelper.picture.recognition.vimerzhao;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.jiamiaohe.gamehelper.MyApplication;
import com.youtu.Youtu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.example.jiamiaohe.gamehelper.picture.recognition.vimerzhao.PlayerAnalysRatio.HEIGHT_DELTA;

public class ResolveUtil {

    private static final String APP_ID = "10096521";
    private static final String SECRET_ID = "AKIDpZBev3Nz3AD6oKvZyEJCxWt4FLKC6Qlu";
    private static final String SECRET_KEY = "FUbaNF0ayAAzUpxgmifXD3kHfsz3LYaV";
    private static final String USER_ID = "1769898935";
    private final static int NUMBER = 10;
    private final static int ITEM_COUNT = 7;
    static String path = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/tmp.png");
    private static Bitmap[] mPerPlayer = new Bitmap[NUMBER];
    private static Bitmap mTotalPlayer;
    private static Youtu faceYoutu;
    private static ReturnData[] sTempData = new ReturnData[ITEM_COUNT * NUMBER];
    private static int[] mLocationFlag = new int[NUMBER];
    private static ReturnData[][] sReturnData = new ReturnData[NUMBER][ITEM_COUNT];
    private static String[][] data = new String[NUMBER][ITEM_COUNT];

    static {
        mLocationFlag[0] = 0;
        for (int i = 1; i < mLocationFlag.length; i++) {
            mLocationFlag[i] = mLocationFlag[i - 1] + HEIGHT_DELTA;
        }
    }

    public static Youtu getYoutuInstance() {
        if (faceYoutu == null) {
            faceYoutu = new Youtu(APP_ID, SECRET_ID, SECRET_KEY, Youtu.API_YOUTU_END_POINT, USER_ID);
        }
        return faceYoutu;
    }


    public static void resolve(PlayerAnalysRatio[] players) {
        Bitmap source = getBitmapFromAsset(MyApplication.getContext(), "break.png");
        for (int i = 0; i < players.length; i++) {
            players[i].setBreak(source);
            mPerPlayer[i] = players[i].combineHorizontal();
        }
        mTotalPlayer = PlayerAnalysRatio.combineVertical(mPerPlayer);

        // 这个破方法越用效果越差
        // mTotalPlayer = sharpBitmap(mTotalPlayer);

        // 需要调试是写到存储卡上
        writeBitmap(path, mTotalPlayer);

    }
    private static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream str;
        Bitmap bitmap = null;
        try {
            str = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(str);
        } catch (IOException e) {
            // handle exception
        }

        return bitmap;
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
            if (i < NUMBER * ITEM_COUNT) {

                //每行个，识别顺序可能变化，所以需要根据x大小排序，y轴顺序较为稳定，所以暂时不做排序
                if (i != 0 && i % ITEM_COUNT == 0) {
                    Arrays.sort(returnDatas);

                    System.out.printf("%d: ", ++count);
                    for (int k = 0; k < returnDatas.length; k++) {
                        data[count - 1][k] = returnDatas[k].data;
                    }
                }
                returnDatas[i % ITEM_COUNT].data = (String) ((JSONObject) array.get(i)).get("itemstring");
                returnDatas[i % ITEM_COUNT].xLoc = Integer.parseInt(((JSONObject) ((JSONObject) array.get(i)).get("itemcoord")).get("x").toString());
                returnDatas[i % ITEM_COUNT].yLoc = Integer.parseInt(((JSONObject) ((JSONObject) array.get(i)).get("itemcoord")).get("y").toString());

                sTempData[i].data = (String) ((JSONObject) array.get(i)).get("itemstring");
                sTempData[i].xLoc = Integer.parseInt(((JSONObject) ((JSONObject) array.get(i)).get("itemcoord")).get("x").toString());
                sTempData[i].yLoc = Integer.parseInt(((JSONObject) ((JSONObject) array.get(i)).get("itemcoord")).get("y").toString());
            } else {
                System.out.println("失败");
            }

        }

        correctDataByRepeat();
    }

    private static HashMap<String, Number> modifyTable = new HashMap<>();
    static {
        modifyTable.put("O", 0);
        modifyTable.put("o", 0);
        modifyTable.put("C", 0);
        modifyTable.put("l", 1);
        modifyTable.put("L", 1);
        modifyTable.put("g", 9);
    }

    private static void correctDataByRepeat() {
        Arrays.sort(sTempData);

        try {

            for (int i = 0; i < sReturnData.length; i++) {
                for (int j = 0; j < sReturnData[0].length; j++) {
                    if (sReturnData[i][j] == null) {

                        sReturnData[i][j] = new ReturnData();
                    }
                }
            }
            int iCount = 0;
            // 初始化
            for (int i = 0; i < NUMBER; i++) {
                for (int j = 0; j < ITEM_COUNT; j++) {
                    sReturnData[i][j].data = "空";
                    sReturnData[i][j].xLoc = 0;
                    sReturnData[i][j].yLoc = i*2*PlayerAnalysRatio.getBreakHeight();
                }
            }
            final int RANGE = 20;
            int dealt = 0;
            // 默认文字能识别出来
            for (int i = 0; i < sTempData.length; ) {
                if (iCount >= NUMBER) break;

                int jCount = 0;
                if (sTempData[dealt].data != null && Math.abs(sTempData[i].yLoc - sReturnData[iCount][jCount].yLoc) < RANGE) {
                    dealt++;
                    sReturnData[iCount][jCount].data = sTempData[i].data;
                    sReturnData[iCount][jCount].xLoc = sTempData[i].xLoc;
                    sReturnData[iCount][jCount].yLoc = sTempData[i++].yLoc;
                }
                jCount++;

                if (sTempData[dealt].data != null && Math.abs(sTempData[i].yLoc - sReturnData[iCount][jCount].yLoc) < RANGE) {
                    sReturnData[iCount][jCount].data = sTempData[i].data;
                    sReturnData[iCount][jCount].xLoc = sTempData[i].xLoc;
                    sReturnData[iCount][jCount].yLoc = sTempData[i++].yLoc;
                }
                jCount++;

                for (int k = 0; k < 4; k++) {
                    System.out.println(iCount + "==" + jCount);
                    // TODO: 其实需要处理的就是 中间3个较小的数字
                    if (sTempData[dealt].data != null && Math.abs(sTempData[i].yLoc - sReturnData[iCount][jCount].yLoc) < RANGE) {
                        sReturnData[iCount][jCount].xLoc = sTempData[i].xLoc;
                        sReturnData[iCount][jCount].yLoc = sTempData[i].yLoc;
                        sReturnData[iCount][jCount].data = sTempData[i].data;
                        i++;
                    }

                    jCount++;
                }

                if (sTempData[dealt].data != null && Math.abs(sTempData[i].yLoc - sReturnData[iCount][jCount].yLoc) < RANGE) {
                    sReturnData[iCount][jCount].data = sTempData[i].data;
                    sReturnData[iCount][jCount].xLoc = sTempData[i].xLoc;
                    sReturnData[iCount][jCount].yLoc = sTempData[i++].yLoc;
                }

                iCount++;
            }
            // sync
            modifyData(sReturnData);
            for (int i = 0; i < sReturnData.length; i++) {
                for (int j = 0; j < sReturnData[i].length; j++) {
                    data[i][j] = sReturnData[i][j].data;
                    System.out.printf(data[i][j]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //定位一行数据中没识别出来的
    public static void modifyData(ReturnData[][] returnData) {
        ReturnData[][] origin = new ReturnData[NUMBER][ITEM_COUNT];
        for (int i = 0; i < returnData.length; i++) {
            for (int j = 0; j < returnData[i].length; j++) {
                origin[i][j] = new ReturnData();
                origin[i][j].data = returnData[i][j].data;
                origin[i][j].xLoc= returnData[i][j].xLoc;
                returnData[i][j].data = "空";

            }
        }
        int range = 30;
        int[] locArr = new int[ITEM_COUNT];
        ArrayList<Integer> originWidth = PlayerAnalysRatio.set.get(RatioData.wText);
        ArrayList<Integer> textWidth = new ArrayList<>();
        for (int i = 0; i < originWidth.size(); i++) {
            textWidth.add(i, originWidth.get(i));
        }
        int tmp = textWidth.get(2);
        textWidth.remove(2);
        textWidth.add(2, 2*tmp);

        tmp = textWidth.get(3);
        textWidth.remove(3);
        textWidth.add(3, 2*tmp);

        tmp = textWidth.get(4);
        textWidth.remove(4);
        textWidth.add(4, 2*tmp);

        textWidth.add(5, 2*  PlayerAnalysRatio.set.get(RatioData.levelTag).get(7));

        for (int i = 1; i < textWidth.size(); i++){
            locArr[i] = locArr[i-1] + textWidth.get(i-1) + PlayerAnalysRatio.getBreakWidth();
        }
        tmp = locArr[ITEM_COUNT-1];
        locArr[ITEM_COUNT-1] = locArr[ITEM_COUNT-2];
        locArr[ITEM_COUNT-2] = tmp;

        for (int i = 0; i < origin.length; i++) {
            for (int j = 0; j < origin[i].length; j++) {
                for (int k =  0; k < locArr.length; k++) {
                    if (Math.abs(locArr[j] - origin[i][k].xLoc) < range) {
                        sReturnData[i][j].data = origin[i][k].data;
                        sReturnData[i][j].xLoc = origin[i][k].xLoc;
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < NUMBER; i++) {
            for (int j = 2; j < 7; j++) {
                if (j < 5) {  //exclude money and include level
                    sReturnData[i][j].data = sReturnData[i][j].data.trim().substring(0, (sReturnData[i][j].data.length() + 1) / 2);
                    if (!TextUtils.isDigitsOnly(sReturnData[i][j].data)) {
                        if (modifyTable.get(sReturnData[i][j].data) != null) {
                            sReturnData[i][j].data = String.valueOf(modifyTable.get(sReturnData[i][j].data));
                        } else {
                            sReturnData[i][j].data = "空";
                        }
                    }
                } else if (j == 6) {
                    String tmpLevel = sReturnData[i][j].data;
                    char[] tmpLevelArray = tmpLevel.toCharArray();
                    for(int iLevelArray = 0; iLevelArray < tmpLevelArray.length; iLevelArray++) {
                        if (modifyTable.containsKey(tmpLevelArray[iLevelArray])) {
                            tmpLevelArray[iLevelArray] = String.valueOf(modifyTable.get(tmpLevelArray[iLevelArray])).charAt(0);
                        }
                    }
                    tmpLevel = tmpLevel.replaceAll("[^0-9]", "");
                    Log.i("hjm", "before = "+sReturnData[i][j].data+", after = "+tmpLevel);
                    if (tmpLevel.length() >= 2) {
                        sReturnData[i][j].data = tmpLevel.substring(0, tmpLevel.length() > 2 ? 2 : 1);
                    } else {
                        sReturnData[i][j].data = tmpLevel;
                    }
                }
            }
        }

    }

    /**
     * 修正数据，保证排版为10行6列，每行两个字符串，4个数字
     * 对不正确的数据填充0
     * TODO:使用时需要关闭重复图片的逻辑
     */
    private static void correctDataByCheck() {
        // 思路，先看一下y的浮动范围，消除浮动在先按照y排序，在按照x排序
        Arrays.sort(sTempData);

        for (int i = 0; i < sReturnData.length; i++) {
            for (int j = 0; j < sReturnData[0].length; j++) {
                if (sReturnData[i][j] == null) {

                    sReturnData[i][j] = new ReturnData();
                }
            }
        }
        int iCount = 0;

        // 经过排序，如果不是“名字，名字，数字，数字，数字，数字”就需要修正
        for (int i = 0; i < sTempData.length; ) {
            if (iCount >= 10) break;

            // 默认前两个能直接识别出
            int jCount = 0;
            sReturnData[iCount][jCount].data = sTempData[i].data;
            sReturnData[iCount][jCount].xLoc = sTempData[i].xLoc;
            sReturnData[iCount][jCount++].yLoc = sTempData[i++].yLoc;

            sReturnData[iCount][jCount].data = sTempData[i].data;
            sReturnData[iCount][jCount].xLoc = sTempData[i].xLoc;
            sReturnData[iCount][jCount++].yLoc = sTempData[i++].yLoc;

            // 后面4个数，其实就是前3个可能识别不到，所以要做判定
            for (int k = 0; k < 4; k++) {
                // 这里有一种情况十分棘手，就是识别出来了，但成了字母
                // 暂时用长度为1定位
                // TODO: 其实需要处理的就是 中间3个较小的数字
                if (sTempData[i].data != null) {
                    if (TextUtils.isDigitsOnly(sTempData[i].data)) {
                        sReturnData[iCount][jCount].xLoc = sTempData[i].xLoc;
                        sReturnData[iCount][jCount].yLoc = sTempData[i].yLoc;
                        sReturnData[iCount][jCount++].data = sTempData[i++].data;
                    } else if (sTempData[i].data.length() == 1) { // 单个数字被识别为字母,如0-->O
                        sReturnData[iCount][jCount].xLoc = sTempData[i].xLoc;
                        sReturnData[iCount][jCount].yLoc = sTempData[i].yLoc;
                        sReturnData[iCount][jCount++].data = sTempData[i++].data;
                    } else {//
                        sReturnData[iCount][jCount].xLoc = Integer.MAX_VALUE;
                        sReturnData[iCount][jCount].yLoc = Integer.MAX_VALUE;
                        sReturnData[iCount][jCount++].data = "?";
                    }
                } else {//
                    sReturnData[iCount][jCount].xLoc = Integer.MAX_VALUE;
                    sReturnData[iCount][jCount].yLoc = Integer.MAX_VALUE;
                    sReturnData[iCount][jCount++].data = "?";
                }
            }

            iCount++;
        }
        // 调整位置,先获取每个字符串的大致位置
        int[] arverageLoc = new int[]{12, 342, 600, 790, 970, 1140};
        // 根据位置优化
        for (int i = 0; i < sReturnData.length; i++) {
            for (int j = 0; j < sReturnData[i].length; j++) {
                if (Math.abs(sReturnData[i][j].xLoc - arverageLoc[j]) > 50) {
                    for (int k = sReturnData[i].length - 1; k > j; k--) {
                        sReturnData[i][k].data = sReturnData[i][k - 1].data;
                        sReturnData[i][k].xLoc = sReturnData[i][k - 1].xLoc;
                    }
                    sReturnData[i][j].data = "?";
                }
            }
        }
        // 同步数据
        for (int i = 0; i < sReturnData.length; i++) {
            for (int j = 0; j < sReturnData[i].length; j++) {
                data[i][j] = sReturnData[i][j].data;
            }
        }

    }


    public static void writeBitmap(String path, Bitmap bitmap) {
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

    //锐化
    public static Bitmap sharpBitmap(Bitmap bitmap) {
        int width, height;
        height = bitmap.getHeight();
        width = bitmap.getWidth();
        int[] pix = new int[width * height];
        bitmap.getPixels(pix, 0, width, 0, 0, width, height);


        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                int R = (pix[index] >> 16) & 0xff;     //bitwise shifting
                int G = (pix[index] >> 8) & 0xff;
                int B = pix[index] & 0xff;
                if (R < 100 && G < 100 && B < 100) {
                    R = G = B = 0;
                } else if (R > 200 && G > 200 && B > 200) {
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
    private final static int BITMAP_WIDTH = 1920;
    private final static int BITMAP_HEIGHT = 1080;
    public static Bitmap checkBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width == BITMAP_WIDTH && height == BITMAP_HEIGHT) {
            //TODO: 这一句需要注释掉，在部分机型可能报错!!
            // Toast.makeText(MyApplication.getContext(), "标准", Toast.LENGTH_SHORT).show();
            return bitmap;
        }
        float scaleWidth = (float) BITMAP_WIDTH / width;
        float scaleHeight = (float) BITMAP_HEIGHT / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    private static class ReturnData implements Comparable<ReturnData> {
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
}
