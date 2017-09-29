package com.example.jiamiaohe.gamehelper.picture.recognition;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.example.jiamiaohe.gamehelper.MyApplication;
import com.example.jiamiaohe.gamehelper.picture.recognition.vimerzhao.PlayerAnalysRatio;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by jiamiaohe on 2017/9/3.
 */

public class BitmapRecognizeUtils {

    private final String TAG = "BitmapRecognizeUtils";

    public static BitmapRecognizeUtils mBitmapRecognizeUtils = null;

    ArrayList<Bitmap> mList = new ArrayList<Bitmap>();
    ArrayList<String> mListName = new ArrayList<String>();

    public static BitmapRecognizeUtils getInstance() {
        if (mBitmapRecognizeUtils == null) {
            mBitmapRecognizeUtils = new BitmapRecognizeUtils();
        }

        return mBitmapRecognizeUtils;
    }

    private BitmapRecognizeUtils() {
        Bitmap bitmap;
        for(int i = 1; i < 100; i++) {
            bitmap = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), MyApplication.getContext().getResources().getIdentifier("b"+i, "drawable", MyApplication.getContext().getPackageName()));
            Log.i(TAG, "initBitmapList = bitmap = "+bitmap+", index = "+i);
            mList.add(scaleBitmap(bitmap));

        }

        AssetManager assetManager = MyApplication.getContext().getAssets();
        try {
            InputStream inputStream = assetManager.open("names.txt");
            InputStreamReader dataInput = new InputStreamReader(inputStream);
            BufferedReader breader = new BufferedReader(dataInput);

            String strLine = null;
            while((strLine =  breader.readLine()) != null) {
                mListName.add(strLine);
            }

            inputStream.close();
            dataInput.close();
            breader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "mListName size = "+mListName.size());
    }

    private Bitmap scaleBitmap(Bitmap bitmap) {
        int with = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (with == 64 && height == 64) {
            return bitmap;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(64.00f/with, 64.0f/height);

        Bitmap result =  Bitmap.createBitmap(bitmap, 0,0,with, height, matrix, true);

        bitmap.recycle();

        return result;
    }

    public void saveRoundBitmap(Bitmap bitmap) {
        Bitmap round = toRoundBitmap(bitmap);

        PlayerAnalysRatio.saveBitmap(round, "null_bitmap");
    }

    public Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            left = 0;
            top = 0;
            right = width;
            bottom = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);// 设置画笔无锯齿

        canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
        paint.setColor(color);

        // 以下有两种方法画圆,drawRounRect和drawCircle
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        canvas.drawCircle(roundPx, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(bitmap, src, dst, paint); //以Mode.SRC_IN模式合并bitmap和已经draw了的Circle
        bitmap.recycle();

        return output;
    }

    public String getBitmapName(Bitmap origin) {
        return getBitmapName(origin, 0);
    }

    public static int i = 1;
    Mat mat1 = new Mat();
    Mat mat2 = new Mat();
    Mat mat11 = new Mat();
    Mat mat22 = new Mat();
    public synchronized String getBitmapName(Bitmap origin, int id) {
        Log.i(TAG, "start to compare");

        Bitmap bitmap = Bitmap.createBitmap(origin);
        bitmap = scaleBitmap(bitmap);
        bitmap = toRoundBitmap(bitmap);


        double maxSimilar = 0;
        int maxSimilarIndex = 0;
        int index = 0;

        double similar = 0;
        for(Bitmap bitDest : mList) {
            Utils.bitmapToMat(bitmap, mat1);
            Utils.bitmapToMat(bitDest, mat2);
            Imgproc.cvtColor(mat1, mat11, Imgproc.COLOR_BGR2GRAY);  //COLOR_RGB2HSV  COLOR_BGR2GRAY
            Imgproc.cvtColor(mat2, mat22, Imgproc.COLOR_BGR2GRAY);
            similar = comPareHist(mat11, mat22, index);
            if (similar > maxSimilar) {
                maxSimilar = similar;
                maxSimilarIndex = index;
            }

            index++;
        }
        Log.i(TAG, "end to compare = "+mListName.get(maxSimilarIndex));

        if (!"空".equals(mListName.get(maxSimilarIndex))) {
            PlayerAnalysRatio.saveBitmap(bitmap, "kong"+(i++)+".png");
        }

        return mListName.get(maxSimilarIndex);

    }

    private double comPareHist(Mat srcMat,Mat desMat, int index){

        srcMat.convertTo(srcMat, CvType.CV_32F);
        desMat.convertTo(desMat, CvType.CV_32F);
        double target = Imgproc.compareHist(srcMat, desMat, Imgproc.CV_COMP_CORREL);   //相关性
        double target_2 = Imgproc.compareHist(srcMat, desMat, Imgproc.CV_COMP_CHISQR);  //卡方
        double target_3 = Imgproc.compareHist(srcMat, desMat, Imgproc.CV_COMP_INTERSECT);  //交集法
        double target_4 = Imgproc.compareHist(srcMat, desMat, Imgproc.CV_COMP_BHATTACHARYYA);  //常态分布比对的Bhattacharyya距离法

        Log.i(TAG, "similar ： target  ==" + target+"，target_2 =  "+target_2+"， target_3 =  "+target_3+"， target_4 = "+target_4+", index = "+index);
        return target;
    }

}
