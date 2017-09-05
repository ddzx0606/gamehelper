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

    PlayerAnalys mPlayers[] = new PlayerAnalys[10];

    private BattleSituation() {
        Log.i(TAG, "BattleSituation mPlayers = "+mPlayers.length);
//        for(PlayerAnalys player : mPlayers) {
//            player = new PlayerAnalys();
//        }
        for(int i = 0; i < mPlayers.length; i++) {
            mPlayers[i] = new PlayerAnalys();
        }
    }

    public void analys(@NotNull Bitmap bitmap) {
        // 检查Bitmap
        bitmap = ResolveUtil.checkBitmap(bitmap);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/newBitmap.jpeg");
        ResolveUtil.writeBitmap(path, bitmap);

        Log.i(TAG, "start to anlys bitmap = "+bitmap);

        for (int i = 0; i < mPlayers.length; i++) {
            Log.i(TAG, "start to analys mPlayers[i] = "+mPlayers[i]+", mPlayesRect[i] = "+mPlayesRect[i]+", mPlayersIndex[i] = "+mPlayersIndex[i]);
            mPlayers[i].analys(bitmap, mPlayesRect[i], mPlayersIndex[i]);
        }
        // 解析
        Toast.makeText(MyApplication.getContext(), "识别中...", Toast.LENGTH_SHORT).show();
        ResolveUtil.resolve(mPlayers);

    }

    public View getView() {
        ScrollView scrollView = new ScrollView(MyApplication.getContext());

        LinearLayout linearLayout = new LinearLayout(MyApplication.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout);

        for (int i = 0; i < mPlayers.length; i++) {
            linearLayout.addView(mPlayers[i].getView());
        }

//        analysWholePic();
//        mPlayers[5].analyzeName();

        // 在此开一个线程填充数据，网络请求一次，效率更高,请求完成再遍历修改
        new Thread() {
            @Override
            public void run() {
                super.run();
                ResolveUtil.getData();
                for (int i = 0; i < mPlayers.length; i++) {
                    mPlayers[i].analyzeName(i);
                }
                // 增强识别效果，对于错误数据单独请求，直到正确

            }
        }.start();
        return scrollView;
    }

    private void analysWholePic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "analysWholePic");

                Bitmap bitmap = Bitmap.createBitmap(PLAYER_WIDTH, PLAYER_HEIGHT*10, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);

                for(int i = 0; i < 10; i++){
                    canvas.drawBitmap(mPlayers[i].getPlayerBitmap(), 0, PLAYER_HEIGHT*i, new Paint());
                }

                PlayerAnalys.saveBitmap(bitmap, "pingjie.png");

                Youtu faceYoutu = new Youtu("10096205", "AKIDXtzJOEaAG7jAMJaUiOtACzWqysXxn2h7", "KPFqFSQBftrY4MPCiyxf7JI174mEWfYH", Youtu.API_YOUTU_END_POINT, "");
                JSONObject respose = null;
                //respose= faceYoutu.FaceCompareUrl("http://open.youtu.qq.com/content/img/slide-1.jpg","http://open.youtu.qq.com/content/img/slide-1.jpg");
                try {
                    //respose = faceYoutu.GeneralOcrWithBitmap(BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.drawable.text_wenzi));
                    respose = faceYoutu.GeneralOcrWithBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "Exception = "+e.toString());
                }

                try {
                    JSONArray jasonArray = respose.getJSONArray("items");

                    Log.i(TAG, "jasonArray size = "+jasonArray.length());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                Log.i(TAG, "respose = "+respose);
            }
        }).start();
    }

}
