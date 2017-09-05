package com.example.jiamiaohe.gamehelper.picture.recognition;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.Image;
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
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

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

    ArrayList<Bitmap> mProps = new ArrayList<Bitmap>();
    TextView mPropsTextView = null;

    public static  final int PROP_WIDTH = 64;
    public static  final int PROP_HEIGHT = 64;
    public static final Rect mPropsRect[] = {new Rect(261,55, 261+PROP_WIDTH, 55+PROP_HEIGHT),
            new Rect(352,55, 352+PROP_WIDTH, 55+PROP_HEIGHT),
            new Rect(441,55, 441+PROP_WIDTH, 55+PROP_HEIGHT),
            new Rect(530,55, 530+PROP_WIDTH, 55+PROP_HEIGHT),
            new Rect(620,55, 620+PROP_WIDTH, 55+PROP_HEIGHT),
            new Rect(710,55, 710+PROP_WIDTH, 55+PROP_HEIGHT)};

    private static final int FRAGMENT_HEIGHT = 42;
    private static final Rect NAME_RECT = new Rect(270, 0, 490, FRAGMENT_HEIGHT);
    private static final Rect NAME_ROLE = new Rect(125, 0, 254, FRAGMENT_HEIGHT);
    //private static final Rect NUM_KILL = new Rect(514, 0, 569, FRAGMENT_HEIGHT);   //width = 55
    //private static final Rect NUM_DEAD = new Rect(592, 0, 650, FRAGMENT_HEIGHT);   //width = 52
    //private static final Rect NUM_HELP = new Rect(673, 0, 733, FRAGMENT_HEIGHT);   //width = 60
    private static final Rect NUM_KILL = new Rect(523, 0, 556, FRAGMENT_HEIGHT);   //width = 55
    private static final Rect NUM_DEAD = new Rect(605, 0, 637, FRAGMENT_HEIGHT);   //width = 52
    private static final Rect NUM_HELP = new Rect(690, 0, 723, FRAGMENT_HEIGHT);   //width = 60
    private static final Rect NUM_MONEY = new Rect(747, 0, 850, FRAGMENT_HEIGHT);

    public void analys(@NotNull Bitmap bitmap, @NotNull Rect rect, @NotNull String index) {
        //clear
        mProps.clear();

        mPlayerBitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.right-rect.left, rect.bottom-rect.top);
        mPlayerIndex = index;

        mNameBitmap = Bitmap.createBitmap(mPlayerBitmap, NAME_RECT.left, NAME_RECT.top, NAME_RECT.right-NAME_RECT.left, NAME_RECT.bottom-NAME_RECT.top);
        mRoleNameBitmap = Bitmap.createBitmap(mPlayerBitmap, NAME_ROLE.left, NAME_ROLE.top, NAME_ROLE.right-NAME_ROLE.left, NAME_ROLE.bottom-NAME_ROLE.top);
        mNumKillBitmap = Bitmap.createBitmap(mPlayerBitmap, NUM_KILL.left, NUM_KILL.top, NUM_KILL.right-NUM_KILL.left, NUM_KILL.bottom-NUM_KILL.top);
        mNumDeadBitmap = Bitmap.createBitmap(mPlayerBitmap, NUM_DEAD.left, NUM_DEAD.top, NUM_DEAD.right-NUM_DEAD.left, NUM_DEAD.bottom-NUM_DEAD.top);
        mNumHelpBitmap = Bitmap.createBitmap(mPlayerBitmap, NUM_HELP.left, NUM_HELP.top, NUM_HELP.right-NUM_HELP.left, NUM_HELP.bottom-NUM_HELP.top);
        mNumMoneyBitmap = Bitmap.createBitmap(mPlayerBitmap, NUM_MONEY.left, NUM_MONEY.top, NUM_MONEY.right-NUM_MONEY.left, NUM_MONEY.bottom-NUM_MONEY.top);

        Log.i(TAG, "mPlayerBitmap.width = "+mPlayerBitmap.getWidth()+", height = "+mPlayerBitmap.getHeight());
//        int temp = 0;
        for(Rect rect1 : mPropsRect) {
            Log.i(TAG, "rect1.left = "+rect1.left+", rect.right = "+rect1.right+", rect1 = "+(rect1.bottom-rect1.top));;
            mProps.add((Bitmap.createBitmap(mPlayerBitmap, rect1.left, rect1.top, rect1.right-rect1.left, rect1.bottom-rect1.top)));
//            if (index.equals("友方五") || index.equals("敌方三")) {
//                saveBitmap(mProps.get(temp++), System.currentTimeMillis()+""+ new Random().nextInt()+".jpg");
//            }
        }
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
            textLinear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
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

            LinearLayout propLinear = new LinearLayout(MyApplication.getContext());
            propLinear.setOrientation(LinearLayout.HORIZONTAL);
            propLinear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            LinearLayout propTextLinear = new LinearLayout(MyApplication.getContext());
            propTextLinear.setOrientation(LinearLayout.HORIZONTAL);
            propTextLinear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            for(Bitmap bitmap : mProps) {
                Log.i(TAG , "add prop image view "+bitmap);
                ImageView imageProp = new ImageView(MyApplication.getContext());
                imageProp.setPadding(0,0,2,0);
                imageProp.setImageBitmap(bitmap);
                propLinear.addView(imageProp);
            }

            mPropsTextView = new TextView(MyApplication.getContext());
            mPropsTextView.setText("装备");
            propTextLinear.addView(mPropsTextView);

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
            main.addView(propLinear);
            main.addView(textLinear);
            main.addView(propTextLinear);

//            if (mPlayerIndex.equals("友方二")) {
            // TODO:暂时注释
            /*
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder stringBuilder = new StringBuilder();
                        for(int i = 0; i < mProps.size(); i++) {
//                        for (Bitmap bitmap : mProps) {
                            stringBuilder.append(BitmapRecognizeUtils.getInstance().getBitmapName(mProps.get(i), i+1));
                            if (i < mProps.size()-1) {
                                stringBuilder.append('-');
                            }
                        }

                        final String text = stringBuilder.toString();

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mPropsTextView.setText(text);
                            }
                        });
                    }
                }).start();
                */
//            }

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
    private static final Rect BREAK = new Rect(0, 0, 120, 42);
    public static Bitmap mHorizontalBreak = null;
    public static Bitmap mVerticalBreak = null;
    public static int HEIGHT_DELTA =  FRAGMENT_HEIGHT + (BREAK.bottom-BREAK.top);
    private static int totalWidth = 0;

    public void setBreak(Bitmap source) {
        System.out.println(source.getWidth());
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
        // TODO:根据使用的优化方法设置
        boolean useRepeat = true;
        Bitmap newNumDeadBitmap, newNumKillBitmap, newNumHelpBitmap;
        if (useRepeat) {

            newNumDeadBitmap = horizondDouble(mNumDeadBitmap);
            newNumKillBitmap = horizondDouble(mNumKillBitmap);
            newNumHelpBitmap = horizondDouble(mNumHelpBitmap);
        } else {
            newNumDeadBitmap = mNumDeadBitmap;
            newNumKillBitmap = mNumKillBitmap;
            newNumHelpBitmap = mNumHelpBitmap;
        }
        return  combineHorizontal(mNameBitmap, mRoleNameBitmap,
                newNumKillBitmap,
                newNumDeadBitmap,
                newNumHelpBitmap,
                mNumMoneyBitmap);
    }

    private Bitmap horizondDouble(Bitmap originBitmap) {
        int width = originBitmap.getWidth();
        int height =originBitmap.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width*2, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(originBitmap, 0, 0, null);
        canvas.drawBitmap(originBitmap, width, 0, null);
        return bitmap;
    }
    public static Bitmap combineHorizontal(Bitmap...bitmaps) {
        if (bitmaps.length <= 0) return null;

        int height = bitmaps[0].getHeight();
        int width = 0;
        for (Bitmap tmp: bitmaps) {
            width += tmp.getWidth();
            width += mHorizontalBreak.getWidth();
        }
        if (totalWidth == 0) totalWidth = width;
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
