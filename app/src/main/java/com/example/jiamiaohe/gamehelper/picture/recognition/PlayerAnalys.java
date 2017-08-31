package com.example.jiamiaohe.gamehelper.picture.recognition;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Handler;
import com.example.jiamiaohe.gamehelper.MyApplication;
import com.example.jiamiaohe.gamehelper.picture.recognition.vimerzhao.ResolveUtil;
import com.youtu.Youtu;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by jiamiaohe on 2017/8/27.
 */

public class PlayerAnalys {
    private static final  String TAG = "PlayerAnalys";

    Bitmap mPlayerBitmap = null;
    String mPlayerIndex = null;

    Bitmap mNameBitmap = null;
    Bitmap mRoleNameBitmap = null;
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
        mRoleNameBitmap = Bitmap.createBitmap(mPlayerBitmap, NAME_ROLE.left, NAME_ROLE.top, NAME_ROLE.right-NAME_ROLE.left, NAME_ROLE.bottom-NAME_ROLE.top);
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
            roleName.setImageBitmap(mRoleNameBitmap);
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
            mNameText.setTextColor(Color.BLACK);
            mNameText.setPadding(0,0,2,0);
            mNameText.setTextSize(10);
            mNameText.setText("name:");
            textLinear.addView(mNameText);
            mRoleNameText = new TextView(MyApplication.getContext());
            mRoleNameText.setTextColor(Color.BLACK);
            mRoleNameText.setPadding(0,0,2,0);
            mRoleNameText.setTextSize(10);
            mRoleNameText.setText("role:");
            textLinear.addView(mRoleNameText);
            mKillText = new TextView(MyApplication.getContext());
            mKillText.setTextColor(Color.BLACK);
            mKillText.setPadding(0,0,2,0);
            mKillText.setTextSize(10);
            mKillText.setText("kill:");
            textLinear.addView(mKillText);
            mDeadText = new TextView(MyApplication.getContext());
            mDeadText.setTextColor(Color.BLACK);
            mDeadText.setPadding(0,0,2,0);
            mDeadText.setTextSize(10);
            mDeadText.setText("dead:");
            textLinear.addView(mDeadText);
            mHelpText = new TextView(MyApplication.getContext());
            mHelpText.setTextColor(Color.BLACK);
            mHelpText.setPadding(0,0,2,0);
            mHelpText.setTextSize(10);
            mHelpText.setText("help:");
            textLinear.addView(mHelpText);
            mMoneyText = new TextView(MyApplication.getContext());
            mMoneyText.setTextColor(Color.BLACK);
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
                final String roleName = getStringFromBitmap(mRoleNameBitmap);
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

    /**
     * 获取数据，不需要在这个方法开线程，否则需要网络请求10次
     * @param index
     */
    public void analyzeName(final int index) {
        String[] res;

        // 每个线程请求一次虽然可以即时更新，但是效率太低
        //ResolveUtil.getData();

        res = ResolveUtil.getItem(index);

        final String name = res[0];
        final String roleName = res[1];
        final String killNum = res[2];
        final String deadNum = res[3];
        final String helpNum = res[4];
        final String moneyNum = res[5];
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mNameText.setText(name);
                mRoleNameText.setText("--"+roleName);
                mKillText.setText("--"+killNum);
                mDeadText.setText("--"+deadNum);
                mHelpText.setText("--"+helpNum);
                mMoneyText.setText("--"+moneyNum);
            }
        });

    }

    public void setNameText(String str) {
        mNameText.setText(str);
    }

    public void setRoleNameText(String str) {
        mRoleNameText.setText(str);
    }

    public void setKillText(String str) {
        mKillText.setText(str);
    }

    public void setDeadText(String str) {
        mDeadText.setText(str);
    }

    public void setHelpText(TextView helpText) {
        mHelpText = helpText;
    }

    public void setMoneyText(TextView moneyText) {
        mMoneyText = moneyText;
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

    // 参数决定识别效果
    private static final Rect BREAK = new Rect(130, 45, 250, 97);
    Bitmap mHorizontalBreak = null;
    static Bitmap mVerticalBreak = null;
    private static int totalWidth = 0;
    
    public Bitmap getmPlayerBitmap() {
        return mPlayerBitmap;
    }
    public void setBreak(Bitmap source) {
        mHorizontalBreak = Bitmap.createBitmap(source, BREAK.left, BREAK.top, BREAK.right-BREAK.left, BREAK.bottom-BREAK.top);
        // 垂直方向也需要一个，否则识别有问题
        if (mVerticalBreak == null && totalWidth != 0) {
            int n = BattleSituation.PLAYER_WIDTH/(BREAK.right - BREAK.left);
            Bitmap[] tmp = new Bitmap[n];
            for (int i = 0; i < tmp.length; i++) tmp[i] = mHorizontalBreak;
            mVerticalBreak = combineHorizontal(tmp);
            mVerticalBreak = Bitmap.createBitmap(mVerticalBreak, 0, 0, totalWidth, BREAK.bottom - BREAK.top);
        }
    }

    public Bitmap combineHorizontal() {
        return  combineHorizontal(mNameBitmap, mRoleNameBitmap, mNumKillBitmap, mNumDeadBitmap, mNumHelpBitmap, mNumMoneyBitmap);
    }
    public Bitmap combineHorizontal(Bitmap...bitmaps) {
        if (bitmaps.length <= 0) return null;

        int height = bitmaps[0].getHeight();
        int width = 0;
        for (Bitmap tmp: bitmaps) {
            System.out.println(tmp.getWidth());
            width += tmp.getWidth();
            width += mHorizontalBreak.getWidth();
        }
        if (totalWidth == 0) totalWidth = width;
        System.out.println("结果"+width);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int cur = 0;

        for (Bitmap tmp: bitmaps) {
            canvas.drawBitmap(tmp, cur, 0, null);
            cur += tmp.getWidth();
            canvas.drawBitmap(mHorizontalBreak, cur, 0, null);
            cur += mHorizontalBreak.getWidth();
        }
        return bitmap;
    }

    public static Bitmap combineVertical(Bitmap...bitmaps) {
        if (bitmaps.length <= 0) return null;
        int width = bitmaps[0].getWidth();
        int height = 0;
        for (Bitmap tmp: bitmaps) {
            height += tmp.getHeight();
            height += mVerticalBreak.getHeight();
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int cur = 0;

        for (Bitmap tmp: bitmaps) {
            canvas.drawBitmap(tmp, 0, cur,null);
            cur += tmp.getHeight();
            canvas.drawBitmap(mVerticalBreak, 0, cur, null);
            cur += mVerticalBreak.getHeight();
        }
        return bitmap;
    }
}
