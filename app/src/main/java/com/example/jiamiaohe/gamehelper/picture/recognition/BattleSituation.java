package com.example.jiamiaohe.gamehelper.picture.recognition;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.jiamiaohe.gamehelper.MyApplication;
import com.example.jiamiaohe.gamehelper.http.HttpUtils;
import com.example.jiamiaohe.gamehelper.picture.recognition.vimerzhao.PlayerAnalysRatio;
import com.example.jiamiaohe.gamehelper.picture.recognition.vimerzhao.RatioData;
import com.example.jiamiaohe.gamehelper.picture.recognition.vimerzhao.ResolveUtil;
import com.youtu.Youtu;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jiamiaohe on 2017/8/27.
 */

public class BattleSituation {

    private static final String TAG = "BattleSituation";

    public static BattleSituation mBattleSituation = null;

    public static synchronized BattleSituation getInstance() {
        if (mBattleSituation == null) {
            mBattleSituation = new BattleSituation();
        }

        return mBattleSituation;
    }

    public static final int PLAYER_HEIGHT = 130;
    public static final int PLAYER_WIDTH = 948;
    Rect mPlayesRect[] = {new Rect(15, 292, PLAYER_WIDTH+15, PLAYER_HEIGHT+292),   //with = 948  height = 140
                 new Rect(15, 423, PLAYER_WIDTH+15, PLAYER_HEIGHT+423),
                new Rect(15, 555, PLAYER_WIDTH+15, PLAYER_HEIGHT+555),
                new Rect(15, 687, PLAYER_WIDTH+15, PLAYER_HEIGHT+687),
                new Rect(15, 819, PLAYER_WIDTH+15, PLAYER_HEIGHT+819),
                new Rect(963, 291, PLAYER_WIDTH+963, PLAYER_HEIGHT+291),   //with = 948   height =
                new Rect(963, 423, PLAYER_WIDTH+963, PLAYER_HEIGHT+423),
                new Rect(963, 555, PLAYER_WIDTH+963, PLAYER_HEIGHT+555),
                new Rect(963, 687, PLAYER_WIDTH+963, PLAYER_HEIGHT+687),
                new Rect(963, 819, PLAYER_WIDTH+963, PLAYER_HEIGHT+819)};

    String mPlayersIndex[] = {"友方一", "友方二", "友方三", "友方四","友方五",
                            "敌方一", "敌方二", "敌方三", "敌方四", "敌方五"};

    PlayerAnalysRatio mPlayersRatio[] = new PlayerAnalysRatio[10];

    private BattleSituation() {
        for(int i = 0; i < mPlayersRatio.length; i++) {
            mPlayersRatio[i] = new PlayerAnalysRatio();
        }
    }


    public void analysForRatio(@NotNull Bitmap bitmap) {
        bitmap  = PlayerAnalysRatio.initData(bitmap);
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 5; i++) {
                mPlayersRatio[j*5+i].analys(bitmap, i, j);
            }
        }
        // 解析
        //Toast.makeText(MyApplication.getContext(), "识别中...", Toast.LENGTH_SHORT).show();
        ResolveUtil.resolve(mPlayersRatio);

    }
    public View getView() {
        ScrollView scrollView = new ScrollView(MyApplication.getContext());

        LinearLayout linearLayout = new LinearLayout(MyApplication.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout);

        for (int i = 0; i < mPlayersRatio.length; i++) {
            linearLayout.addView(mPlayersRatio[i].getView());
        }

//        analysWholePic();
//        mPlayers[5].analyzeName();

        // 在此开一个线程填充数据，网络请求一次，效率更高,请求完成再遍历修改
        new Thread() {
            @Override
            public void run() {
                super.run();

                HttpUtils.getInstance().clearInformation();

                ResolveUtil.getData();
                for (int i = 0; i < mPlayersRatio.length; i++) {
                    mPlayersRatio[i].analyzeName(i);
                }
                // 增强识别效果，对于错误数据单独请求，直到正确

            }
        }.start();
        return scrollView;
    }

    public boolean hasRadio(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Log.i(TAG, "initData width = "+width+", height = "+height);
        if (width * 9 == height * 16) {//16:9 比例
            return true;
        } else {
            return false;
        }
    }

}
