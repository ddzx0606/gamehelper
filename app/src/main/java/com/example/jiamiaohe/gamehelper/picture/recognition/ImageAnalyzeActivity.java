package com.example.jiamiaohe.gamehelper.picture.recognition;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.jiamiaohe.gamehelper.picture.recognition.vimerzhao.ResolveUtil;

import org.opencv.android.OpenCVLoader;

/**
 * Created by jiamiaohe on 2017/8/27.
 */

public class ImageAnalyzeActivity extends Activity {

    private static final String TAG = "ImageAnalyzeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");

        Log.i(TAG, "onCreate = "+path);

        BitmapRecognizeUtils.getInstance();

        // 初始化, move it to MainActitiy
        //ResolveUtil.getYoutuInstance();


        // 注释掉原来的方法
        //BattleSituation.getInstance().analys(BitmapFactory.decodeFile(path));

        // 根据分辨率计算的版本
        BattleSituation.getInstance().analysForRatio(BitmapFactory.decodeFile(path));
        setContentView(BattleSituation.getInstance().getView());

//        LinearLayout linearLayout = new LinearLayout(this);
//        linearLayout.addView();
    }

    //move it to main activity
//    static {
//        if (!OpenCVLoader.initDebug()) {
//            Log.i(TAG, "opencv not load");
//        } else {
//            Log.i(TAG, "opencv load");
//        }
//    }
}
