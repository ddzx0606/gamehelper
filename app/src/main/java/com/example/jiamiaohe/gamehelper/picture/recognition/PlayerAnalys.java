package com.example.jiamiaohe.gamehelper.picture.recognition;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Handler;
import com.example.jiamiaohe.gamehelper.MyApplication;
import com.example.jiamiaohe.gamehelper.R;
import com.youtu.Youtu;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by jiamiaohe on 2017/8/27.
 */

public class PlayerAnalys {
    private static final  String TAG = "PlayerAnalys";

    Bitmap mPlayerBitmap = null;
    String mPlayerIndex = null;

    Bitmap mNameBitmap = null;
    Bitmap nRoleNameBitmap = null;
    Bitmap mNumKillBitmap = null;
    Bitmap mNumDeadBitmap = null;
    Bitmap mNumHelpBitmap = null;
    Bitmap mNumMoneyBitmap = null;

    private static final Rect NAME_RECT = new Rect(270, 0, 490, 42);
    private static final Rect NAME_ROLE = new Rect(125, 0, 254, 42);
    private static final Rect NUM_KILL = new Rect(514, 0, 569, 42);   //width = 55
    private static final Rect NUM_DEAD = new Rect(592, 0, 650, 42);   //width = 52
    private static final Rect NUM_HELP = new Rect(673, 0, 733, 42);   //width = 60
    private static final Rect NUM_MONEY = new Rect(747, 0, 850, 42);

    public void analys(@NotNull Bitmap bitmap, @NotNull Rect rect, @NotNull String index) {
        mPlayerBitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.right-rect.left, rect.bottom-rect.top);
        mPlayerIndex = index;

        mNameBitmap = Bitmap.createBitmap(mPlayerBitmap, NAME_RECT.left, NAME_RECT.top, NAME_RECT.right-NAME_RECT.left, NAME_RECT.bottom-NAME_RECT.top);
        nRoleNameBitmap = Bitmap.createBitmap(mPlayerBitmap, NAME_ROLE.left, NAME_ROLE.top, NAME_ROLE.right-NAME_ROLE.left, NAME_ROLE.bottom-NAME_ROLE.top);
        mNumKillBitmap = Bitmap.createBitmap(mPlayerBitmap, NUM_KILL.left, NUM_KILL.top, NUM_KILL.right-NUM_KILL.left, NUM_KILL.bottom-NUM_KILL.top);
        mNumDeadBitmap = Bitmap.createBitmap(mPlayerBitmap, NUM_DEAD.left, NUM_DEAD.top, NUM_DEAD.right-NUM_DEAD.left, NUM_DEAD.bottom-NUM_DEAD.top);
        mNumHelpBitmap = Bitmap.createBitmap(mPlayerBitmap, NUM_HELP.left, NUM_HELP.top, NUM_HELP.right-NUM_HELP.left, NUM_HELP.bottom-NUM_HELP.top);
        mNumMoneyBitmap = Bitmap.createBitmap(mPlayerBitmap, NUM_MONEY.left, NUM_MONEY.top, NUM_MONEY.right-NUM_MONEY.left, NUM_MONEY.bottom-NUM_MONEY.top);
    }

    TextView mNameText = null;
    TextView mRoleNameText = null;
    TextView mKillText = null;
    TextView mDeadText = null;
    TextView mHelpText = null;
    TextView mMoneyText = null;

    Handler mHandler = new Handler();
    public View getView() {
        if (mPlayerBitmap != null) {

            //sub
            LinearLayout sub = new LinearLayout(MyApplication.getContext());
            sub.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            sub.setOrientation(LinearLayout.HORIZONTAL);
            ImageView name = new ImageView(MyApplication.getContext());
            name.setImageBitmap(mNameBitmap);
            name.setPadding(0,0,2,0);
            sub.addView(name);
            ImageView roleName = new ImageView(MyApplication.getContext());
            roleName.setImageBitmap(nRoleNameBitmap);
            roleName.setPadding(0,0,2,0);
            sub.addView(roleName);
            ImageView killNum = new ImageView(MyApplication.getContext());
            killNum.setImageBitmap(mNumKillBitmap);
            killNum.setPadding(0,0,2,0);
            sub.addView(killNum);
            ImageView deadNum = new ImageView(MyApplication.getContext());
            deadNum.setImageBitmap(mNumDeadBitmap);
            deadNum.setPadding(0,0,2,0);
            sub.addView(deadNum);
            ImageView killHelp = new ImageView(MyApplication.getContext());
            killHelp.setImageBitmap(mNumHelpBitmap);
            killHelp.setPadding(0,0,2,0);
            sub.addView(killHelp);
            ImageView money = new ImageView(MyApplication.getContext());
            money.setImageBitmap(mNumMoneyBitmap);
            money.setPadding(0,0,2,0);
            sub.addView(money);

            LinearLayout textLinear = new LinearLayout(MyApplication.getContext());
            textLinear.setOrientation(LinearLayout.HORIZONTAL);
            sub.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            mNameText = new TextView(MyApplication.getContext());
            mNameText.setPadding(0,0,2,0);
            mNameText.setTextSize(10);
            mNameText.setText("name:");
            textLinear.addView(mNameText);
            mRoleNameText = new TextView(MyApplication.getContext());
            mRoleNameText.setPadding(0,0,2,0);
            mRoleNameText.setTextSize(10);
            mRoleNameText.setText("role:");
            textLinear.addView(mRoleNameText);
            mKillText = new TextView(MyApplication.getContext());
            mKillText.setPadding(0,0,2,0);
            mKillText.setTextSize(10);
            mKillText.setText("kill:");
            textLinear.addView(mKillText);
            mDeadText = new TextView(MyApplication.getContext());
            mDeadText.setPadding(0,0,2,0);
            mDeadText.setTextSize(10);
            mDeadText.setText("dead:");
            textLinear.addView(mDeadText);
            mHelpText = new TextView(MyApplication.getContext());
            mHelpText.setPadding(0,0,2,0);
            mHelpText.setTextSize(10);
            mHelpText.setText("help:");
            textLinear.addView(mHelpText);
            mMoneyText = new TextView(MyApplication.getContext());
            mMoneyText.setPadding(0,0,2,0);
            mMoneyText.setTextSize(10);
            mMoneyText.setText("money:");
            textLinear.addView(mMoneyText);

//            if (mPlayerIndex.equals("友方一")) {
//                Log.i(TAG, "友方一识别名字");
//                Youtu faceYoutu = new Youtu("10096205", "AKIDXtzJOEaAG7jAMJaUiOtACzWqysXxn2h7", "KPFqFSQBftrY4MPCiyxf7JI174mEWfYH", Youtu.API_YOUTU_END_POINT, "");
//                JSONObject respose = null;
//                //respose= faceYoutu.FaceCompareUrl("http://open.youtu.qq.com/content/img/slide-1.jpg","http://open.youtu.qq.com/content/img/slide-1.jpg");
//                try {
//                    respose = faceYoutu.GeneralOcrWithBitmap(mNameBitmap);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Log.i(TAG, "Exception = "+e.toString());
//                }
//
////                TextView textView = new TextView(MyApplication.getContext());
////                textView.setText(respose.);
//                //get respose
//                //System.out.println(respose);
//                Log.i(TAG, "respose = "+respose);
//            }


            //main
            LinearLayout main = new LinearLayout(MyApplication.getContext());
            main.setOrientation(LinearLayout.VERTICAL);
            main.setPadding(0,0,0,20);
            main.setHorizontalGravity(Gravity.CENTER);
            ImageView imageView = new ImageView(MyApplication.getContext());
            imageView.setImageBitmap(mPlayerBitmap);
            imageView.setPadding(0,0,0,2);
            main.addView(imageView);
            main.addView(sub);
            main.addView(textLinear);

            return main;
        } else {
            TextView textView = new TextView(MyApplication.getContext());
            textView.setText(mPlayerIndex+"为空");

            return textView;
        }
    }

    public Bitmap getPlayerBitmap() {
        return mPlayerBitmap;
    }

    public void analyzeName() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String name = getStringFromBitmap(mNameBitmap);
                final String roleName = getStringFromBitmap(nRoleNameBitmap);
                final String killNum = getStringFromBitmap(mNumKillBitmap);
                final String deadNum = getStringFromBitmap(mNumDeadBitmap);
                final String helpNum = getStringFromBitmap(mNumHelpBitmap);
                final String moneyNum = getStringFromBitmap(mNumMoneyBitmap);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mNameText.setText("name:"+name);
                        mRoleNameText.setText("role:"+roleName);
                        mKillText.setText("kill:"+killNum);
                        mDeadText.setText("dead:"+deadNum);
                        mHelpText.setText("help:"+helpNum);
                        mMoneyText.setText("money:"+moneyNum);
                    }
                });
            }
        }).start();

    }

    public static void saveBitmap(Bitmap bm, String name) {
        Log.e(TAG, "保存图片");
        File f = new File("/sdcard/tmp_pic", name);
        if (f.exists()) {
            f.delete();
        }


        File dir = new File("/sdcard/tmp_pic");
        Log.i(TAG, "parent = "+dir.getAbsolutePath()+",   f = "+f.getAbsolutePath());
        if (!dir.exists()) {
            Log.i(TAG, "make parent = "+f.mkdirs());
        }

        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Log.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    Bitmap expandBitmap(Bitmap bitmap) {

        int originWidth = bitmap.getWidth();
        int originHeiht = bitmap.getHeight();

//        Log.i(TAG, "Color.rgb(80,0,0) = "+Integer.toHexString(Color.rgb(80,0,0)));
//        int color = 0;
//        for(int i = 0; i < originWidth; i++) {
//            for(int j = 0; j < originHeiht; j++) {
//                color = bitmap.getPixel(i, j);
//                if (color > Color.rgb(150,0,0)) {
//                    bitmap.setPixel(i,j,Color.parseColor("#ffffff"));
//                } else if (color > Color.rgb(100,0,0)) {
//
//                } else {
//                    bitmap.setPixel(i,j,Color.parseColor("#000000"));
//                }
//            }
//        }

        int scale = 1;
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        originHeiht = originHeiht*scale;
        originWidth = originWidth*scale;

        int expandWith = 100;
        int expandHeight = 100;
        Bitmap expandBitmap = Bitmap.createBitmap(originWidth+expandWith, originHeiht+expandHeight, Bitmap.Config.ARGB_8888);
        expandBitmap.eraseColor(bitmap.getPixel(0,0));
        Canvas canvas = new Canvas(expandBitmap);
        Paint paintColor = new Paint();
        canvas.setMatrix(matrix);
        paintColor.setColor(Color.BLACK);
        canvas.drawBitmap(bitmap, expandWith/(2*scale), expandHeight/(2*scale), paintColor);

        return expandBitmap;
    }

    public String getStringFromBitmap(Bitmap bitmap) {
        saveBitmap(bitmap, bitmap+"_expandBitmap.png");

        Bitmap expand = expandBitmap(bitmap);
        Youtu faceYoutu = new Youtu("10096205", "AKIDXtzJOEaAG7jAMJaUiOtACzWqysXxn2h7", "KPFqFSQBftrY4MPCiyxf7JI174mEWfYH", Youtu.API_YOUTU_END_POINT, "");
        JSONObject respose = null;
        int size = 0;
        String result = "";
        try {
            respose = faceYoutu.GeneralOcrWithBitmap(expand);
            size = respose.getJSONArray("items").length();

            for(int i = 0; i < size; i++) {
                result += respose.getJSONArray("items").getJSONObject(i).getString("itemstring");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Exception = "+e.toString());
        }
        Log.i(TAG, "player index = "+ mPlayerIndex + "respose+  = "+respose + "， size = "+size+", result = "+result);

        saveBitmap(expand, "after_"+expand+"_ExpandBitmap.png");

        expand.recycle();

        return result;
    }
}
