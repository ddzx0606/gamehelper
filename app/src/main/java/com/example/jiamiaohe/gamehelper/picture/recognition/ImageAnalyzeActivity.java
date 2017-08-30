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

/**
 * Created by jiamiaohe on 2017/8/27.
 */

public class ImageAnalyzeActivity extends Activity {

    private final String TAG = "ImageAnalyzeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");

        Log.i(TAG, "onCreate = "+path);

        BattleSituation.getInstance().analys(BitmapFactory.decodeFile(path));
        setContentView(BattleSituation.getInstance().getView());

//        LinearLayout linearLayout = new LinearLayout(this);
//        linearLayout.addView();
    }
}
