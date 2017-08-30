package com.example.jiamiaohe.gamehelper.picture.recognition;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.jiamiaohe.gamehelper.MyApplication;
import com.qcloud.image.ImageClient;
import com.qcloud.image.common_utils.CommonFileUtils;
import com.qcloud.image.demo.Demo;
import com.qcloud.image.request.TagDetectRequest;
import com.youtu.Youtu;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

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
        Log.i(TAG, "start to anlys bitmap = "+bitmap);

        for (int i = 0; i < mPlayers.length; i++) {
            Log.i(TAG, "start to analys mPlayers[i] = "+mPlayers[i]+", mPlayesRect[i] = "+mPlayesRect[i]+", mPlayersIndex[i] = "+mPlayersIndex[i]);
            mPlayers[i].analys(bitmap, mPlayesRect[i], mPlayersIndex[i]);
        }
    }

    public View getView() {
        ScrollView scrollView = new ScrollView(MyApplication.getContext());

        LinearLayout linearLayout = new LinearLayout(MyApplication.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout);

        for(PlayerAnalys player : mPlayers) {
            linearLayout.addView(player.getView());
            player.analyzeName();
        }

//        analysWholePic();
//        mPlayers[5].analyzeName();

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

//    private void anlysFromWanxiangYoutu() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//// 设置用户属性, 包括appid, secretId和SecretKey
//                // 这些属性可以通过万象优图控制台获取(https://console.qcloud.com/ci)
//                int appId = 0000000;//      YOUR_APPID
//                String secretId = "YOUR_SECRETID";
//                String secretKey = "YOUR_SECRETKEY";
//                String bucketName = "YOUR_BUCKET";
//                String ret ;
//                TagDetectRequest tagReq = null;
//                // ImageClient
//                ImageClient imageClient = new ImageClient(appId, secretId, secretKey);
//                // 2. 图片内容方式
//                System.out.println("====================================================");
//                byte[] tagImage = {0};
//                try {
//                    tagImage = CommonFileUtils.getFileContentByte("F:\\pic\\test.jpg");
//                } catch (Exception ex) {
//                    Logger.getLogger(Demo.class.getName()).log(Level.SEVERE, null, ex);
//                }
//
//                tagReq = new TagDetectRequest(bucketName, tagImage);
//                ret = imageClient.tagDetect(tagReq);
//                System.out.println("tag detect ret:" + ret);
//            }
//        }).start();
//
//    }
}
