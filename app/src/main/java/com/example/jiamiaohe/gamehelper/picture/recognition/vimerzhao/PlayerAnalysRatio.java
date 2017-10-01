package com.example.jiamiaohe.gamehelper.picture.recognition.vimerzhao;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jiamiaohe.gamehelper.MyApplication;
import com.example.jiamiaohe.gamehelper.http.HttpUtils;
import com.example.jiamiaohe.gamehelper.picture.recognition.BattleSituation;
import com.example.jiamiaohe.gamehelper.picture.recognition.BitmapRecognizeUtils;
import com.youtu.Youtu;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayerAnalysRatio {
    static final String TAG = PlayerAnalysRatio.class.getSimpleName();
    // 参数决定识别效果
    private static final int FRAGMENT_HEIGHT = 42;
    public static HashMap<String, ArrayList<Integer>> set;
    public static Bitmap mHorizontalBreak = null;
    public static Bitmap mVerticalBreak = null;
    private static int totalWidth = 0;
    String mPlayerIndex = null;
    Bitmap mNameBitmap = null;
    Bitmap mRoleNameBitmap = null;
    Bitmap mNumKillBitmap = null;
    Bitmap mNumDeadBitmap = null;
    Bitmap mNumHelpBitmap = null;
    Bitmap mNumMoneyBitmap = null;

    Bitmap mSkillBitmap = null;
    Bitmap mLevelBitmap = null;

    ArrayList<Bitmap> mProps = new ArrayList<Bitmap>();
    TextView mPropsTextView = null;
    TextView mNameText = null;
    TextView mRoleNameText = null;
    TextView mKillText = null;
    TextView mDeadText = null;
    TextView mHelpText = null;
    TextView mMoneyText = null;
    TextView mLevelText = null;
    Handler mHandler = new Handler();

    private static Rect BREAK = new Rect(0, 0, 120, 42);
    public static int HEIGHT_DELTA = FRAGMENT_HEIGHT + (BREAK.bottom - BREAK.top);
    public static Bitmap initData(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width == 1280 && height == 768) {
            set = RatioData.set1280_768;
        } else if (width * 9 == height * 16) {//16:9 比例
            bitmap = scaleBitmap(bitmap, 1920, 1080);
            set = RatioData.set1920_1080;
        }
        BREAK = new Rect(0, 0, 120, set.get(RatioData.OTHERS).get(3));//文字高度是变化的
        HEIGHT_DELTA = FRAGMENT_HEIGHT + (BREAK.bottom - BREAK.top);
        // 添加其他分辨率
        return bitmap;
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int bitmapWidth, int bitmapHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width == bitmapWidth && height == bitmapHeight) {
            //TODO: 这一句需要注释掉，在部分机型可能报错!!
            // Toast.makeText(MyApplication.getContext(), "标准", Toast.LENGTH_SHORT).show();
            return bitmap;
        }
        float scaleWidth = (float) bitmapWidth / width;
        float scaleHeight = (float) bitmapHeight / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    public static void saveBitmap(Bitmap bm, String name) {
        Log.e(TAG, "保存图片");
        File f = new File("/sdcard/tmp_pic", name);
        if (f.exists()) {
            f.delete();
        }


        File dir = new File("/sdcard/tmp_pic");
        Log.i(TAG, "parent = " + dir.getAbsolutePath() + ",   f = " + f.getAbsolutePath());
        if (!dir.exists()) {
            Log.i(TAG, "make parent = " + f.mkdirs());
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

    public static Bitmap combineHorizontal(Bitmap... bitmaps) {
        if (bitmaps.length <= 0) return null;

        int height = bitmaps[0].getHeight();
        int width = 0;
        for (Bitmap tmp : bitmaps) {
            width += tmp.getWidth();
            width += mHorizontalBreak.getWidth();
        }
        if (totalWidth == 0) totalWidth = width;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int cur = 0;

        for (Bitmap tmp : bitmaps) {
            canvas.drawBitmap(tmp, cur, 0, null);
            cur += tmp.getWidth();
            canvas.drawBitmap(mHorizontalBreak, cur, 0, null);
            cur += mHorizontalBreak.getWidth();
        }
        return bitmap;
    }

    public static Bitmap combineVertical(Bitmap... bitmaps) {
        if (bitmaps.length <= 0) return null;
        int width = bitmaps[0].getWidth();
        int height = 0;
        for (Bitmap tmp : bitmaps) {
            height += tmp.getHeight();
            height += mVerticalBreak.getHeight();
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int cur = 0;

        for (Bitmap tmp : bitmaps) {
            canvas.drawBitmap(tmp, 0, cur, null);
            cur += tmp.getHeight();
            canvas.drawBitmap(mVerticalBreak, 0, cur, null);
            cur += mVerticalBreak.getHeight();
        }
        return bitmap;
    }

    public void analys(Bitmap bitmap, int i, int j) {
        mPlayerIndex = i+""+j;
        initData(bitmap);
        ArrayList<Integer> xTextArr = set.get(RatioData.xText);
        ArrayList<Integer> yTextArr = set.get(RatioData.yText);
        ArrayList<Integer> wTextArr = set.get(RatioData.wText);
        ArrayList<Integer> xImgArr = set.get(RatioData.xImg);
        ArrayList<Integer> yImgArr = set.get(RatioData.yImg);
        Integer xText2 = set.get(RatioData.OTHERS).get(0);
        Integer xImg2 = set.get(RatioData.OTHERS).get(1);
        Integer imgSize = set.get(RatioData.OTHERS).get(2);
        Integer textH = set.get(RatioData.OTHERS).get(3);
        ArrayList<Integer> levelData = set.get(RatioData.levelTag);

        //skill
        ArrayList<Integer> xSkill = set.get(RatioData.xSkill);
        ArrayList<Integer> ySkill = set.get(RatioData.ySkill);
        int size = set.get(RatioData.sizeSkill).get(0);

        mLevelBitmap = Bitmap.createBitmap(bitmap, levelData.get(0) + j * (levelData.get(1) - levelData.get(0)),
                levelData.get(i+2), levelData.get(7), levelData.get(8));
        mNameBitmap = Bitmap.createBitmap(bitmap, xTextArr.get(0) + j * (xText2 - xTextArr.get(0)),
                yTextArr.get(i), wTextArr.get(0), textH);
        mRoleNameBitmap = Bitmap.createBitmap(bitmap, xTextArr.get(1) + j * (xText2 - xTextArr.get(0)),
                yTextArr.get(i), wTextArr.get(1), textH);
        mNumKillBitmap = Bitmap.createBitmap(bitmap, xTextArr.get(2) + j * (xText2 - xTextArr.get(0)),
                yTextArr.get(i), wTextArr.get(2), textH);
        mNumDeadBitmap = Bitmap.createBitmap(bitmap, xTextArr.get(3) + j * (xText2 - xTextArr.get(0)),
                yTextArr.get(i), wTextArr.get(3), textH);
        mNumHelpBitmap = Bitmap.createBitmap(bitmap, xTextArr.get(4) + j * (xText2 - xTextArr.get(0)),
                yTextArr.get(i), wTextArr.get(4), textH);
        mNumMoneyBitmap = Bitmap.createBitmap(bitmap, xTextArr.get(5) + j * (xText2 - xTextArr.get(0)),
                yTextArr.get(i), wTextArr.get(5), textH);

        mSkillBitmap = Bitmap.createBitmap(bitmap, xSkill.get(j), ySkill.get(i), size, size);

        mProps.clear();
        for (int k = 0; k < 6; k++) {
            mProps.add(Bitmap.createBitmap(bitmap,
                    xImgArr.get(k) + j * (xImg2 - xImgArr.get(0)), yImgArr.get(i), imgSize, imgSize));
//            if (index.equals("友方五") || index.equals("敌方三")) {
//                saveBitmap(mProps.get(temp++), System.currentTimeMillis()+""+ new Random().nextInt()+".jpg");
//            }
        }
    }

    public View getView() {
        if (mNameBitmap != null) {

            //sub
            LinearLayout sub = new LinearLayout(MyApplication.getContext());
            sub.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            sub.setOrientation(LinearLayout.HORIZONTAL);
            ImageView name = new ImageView(MyApplication.getContext());
            name.setImageBitmap(mNameBitmap);
            name.setPadding(0, 0, 2, 0);
            sub.addView(name);
            ImageView roleName = new ImageView(MyApplication.getContext());
            roleName.setImageBitmap(mRoleNameBitmap);
            roleName.setPadding(0, 0, 2, 0);
            sub.addView(roleName);
            ImageView killNum = new ImageView(MyApplication.getContext());
            killNum.setImageBitmap(mNumKillBitmap);
            killNum.setPadding(0, 0, 2, 0);
            sub.addView(killNum);
            ImageView deadNum = new ImageView(MyApplication.getContext());
            deadNum.setImageBitmap(mNumDeadBitmap);
            deadNum.setPadding(0, 0, 2, 0);
            sub.addView(deadNum);
            ImageView killHelp = new ImageView(MyApplication.getContext());
            killHelp.setImageBitmap(mNumHelpBitmap);
            killHelp.setPadding(0, 0, 2, 0);
            sub.addView(killHelp);
            ImageView money = new ImageView(MyApplication.getContext());
            money.setImageBitmap(mNumMoneyBitmap);
            money.setPadding(0, 0, 2, 0);
            sub.addView(money);

            ImageView level= new ImageView(MyApplication.getContext());
            level.setImageBitmap(mLevelBitmap);
            level.setPadding(0, 0, 2, 0);
            sub.addView(level);
            LinearLayout textLinear = new LinearLayout(MyApplication.getContext());
            textLinear.setOrientation(LinearLayout.HORIZONTAL);
            textLinear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            mNameText = new TextView(MyApplication.getContext());
            mNameText.setTextColor(Color.BLACK);
            mNameText.setPadding(0, 0, 2, 0);
            mNameText.setTextSize(10);
            mNameText.setText("name:");
            textLinear.addView(mNameText);
            mRoleNameText = new TextView(MyApplication.getContext());
            mRoleNameText.setTextColor(Color.BLACK);
            mRoleNameText.setPadding(0, 0, 2, 0);
            mRoleNameText.setTextSize(10);
            mRoleNameText.setText("role:");
            textLinear.addView(mRoleNameText);
            mKillText = new TextView(MyApplication.getContext());
            mKillText.setTextColor(Color.BLACK);
            mKillText.setPadding(0, 0, 2, 0);
            mKillText.setTextSize(10);
            mKillText.setText("kill:");
            textLinear.addView(mKillText);
            mDeadText = new TextView(MyApplication.getContext());
            mDeadText.setTextColor(Color.BLACK);
            mDeadText.setPadding(0, 0, 2, 0);
            mDeadText.setTextSize(10);
            mDeadText.setText("dead:");
            textLinear.addView(mDeadText);
            mHelpText = new TextView(MyApplication.getContext());
            mHelpText.setTextColor(Color.BLACK);
            mHelpText.setPadding(0, 0, 2, 0);
            mHelpText.setTextSize(10);
            mHelpText.setText("help:");
            textLinear.addView(mHelpText);

            mLevelText = new TextView(MyApplication.getContext());
            mLevelText.setTextColor(Color.BLACK);
            mLevelText.setPadding(0, 0, 2, 0);
            mLevelText.setTextSize(10);
            mLevelText.setText("level:");
            textLinear.addView(mLevelText);

            mMoneyText = new TextView(MyApplication.getContext());
            mMoneyText.setTextColor(Color.BLACK);
            mMoneyText.setPadding(0, 0, 2, 0);
            mMoneyText.setTextSize(10);
            mMoneyText.setText("money:");
            textLinear.addView(mMoneyText);


            LinearLayout propLinear = new LinearLayout(MyApplication.getContext());
            propLinear.setOrientation(LinearLayout.HORIZONTAL);
            propLinear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            LinearLayout propTextLinear = new LinearLayout(MyApplication.getContext());
            propTextLinear.setOrientation(LinearLayout.HORIZONTAL);
            propTextLinear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            for (Bitmap bitmap : mProps) {
                ImageView imageProp = new ImageView(MyApplication.getContext());
                imageProp.setPadding(0, 0, 2, 0);
                imageProp.setImageBitmap(bitmap);
                propLinear.addView(imageProp);
            }
            //add skill - image
            ImageView imageProp = new ImageView(MyApplication.getContext());
            imageProp.setPadding(0,0,2,0);
            imageProp.setImageBitmap(mSkillBitmap);
            propLinear.addView(imageProp);

            mPropsTextView = new TextView(MyApplication.getContext());
            mPropsTextView.setText("装备");
            mPropsTextView.setTextColor(Color.WHITE);
            mPropsTextView.setBackgroundColor(Color.BLACK);
            propTextLinear.addView(mPropsTextView);

            //main
            LinearLayout main = new LinearLayout(MyApplication.getContext());
            main.setOrientation(LinearLayout.VERTICAL);
            main.setPadding(0, 0, 0, 20);
            main.setHorizontalGravity(Gravity.CENTER);
            ImageView imageView = new ImageView(MyApplication.getContext());
            imageView.setPadding(0, 0, 0, 2);
            main.addView(imageView);
            main.addView(sub);
            main.addView(propLinear);
            main.addView(textLinear);
            main.addView(propTextLinear);

            return main;
        } else {
            TextView textView = new TextView(MyApplication.getContext());
            textView.setText(mPlayerIndex + "为空");

            return textView;
        }
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
                        mNameText.setText("name:" + name);
                        mRoleNameText.setText("role:" + roleName);
                        mKillText.setText("kill:" + killNum);
                        mDeadText.setText("dead:" + deadNum);
                        mHelpText.setText("help:" + helpNum);
                        mMoneyText.setText("money:" + moneyNum);
                    }
                });
            }
        }).start();

    }

    /**
     * 获取数据，不需要在这个方法开线程，否则需要网络请求10次
     *
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
        final String level = res[5];
        final String moneyNum = res[6];

        //mProps and skill
        ArrayList<String> propArray = new ArrayList<String>();
        StringBuilder stringBuilder = new StringBuilder();
        String skillName = BitmapRecognizeUtils.getInstance().getSkillName(mSkillBitmap);
        for (int i = 0; i < mProps.size(); i++) {
            String propName = BitmapRecognizeUtils.getInstance().getBitmapName(mProps.get(i), i + 1);
            stringBuilder.append(propName);
            propArray.add(propName);
            if (i < mProps.size() - 1) {
                stringBuilder.append('-');
            }
        }
        stringBuilder.append("---" + skillName);
        final String text = stringBuilder.toString();

        HttpUtils.getInstance().fillPersonArray(index, name, skillName, moneyNum, index==0?1:0, propArray);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mNameText.setText(name);
                mRoleNameText.setText("--" + roleName);
                mKillText.setText("--" + killNum);
                mDeadText.setText("--" + deadNum);
                mHelpText.setText("--" + helpNum);
                mMoneyText.setText("--" + moneyNum);
                mLevelText.setText("--" + level);
                mPropsTextView.setText(text);
            }
        });

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
        originHeiht = originHeiht * scale;
        originWidth = originWidth * scale;

        int expandWith = 100;
        int expandHeight = 100;
        Bitmap expandBitmap = Bitmap.createBitmap(originWidth + expandWith, originHeiht + expandHeight, Bitmap.Config.ARGB_8888);
        expandBitmap.eraseColor(bitmap.getPixel(0, 0));
        Canvas canvas = new Canvas(expandBitmap);
        Paint paintColor = new Paint();
        canvas.setMatrix(matrix);
        paintColor.setColor(Color.BLACK);
        canvas.drawBitmap(bitmap, expandWith / (2 * scale), expandHeight / (2 * scale), paintColor);

        return expandBitmap;
    }

    public String getStringFromBitmap(Bitmap bitmap) {
        //saveBitmap(bitmap, bitmap+"_expandBitmap.png");

        Bitmap expand = expandBitmap(bitmap);
        Youtu faceYoutu = new Youtu("10096205", "AKIDXtzJOEaAG7jAMJaUiOtACzWqysXxn2h7", "KPFqFSQBftrY4MPCiyxf7JI174mEWfYH", Youtu.API_YOUTU_END_POINT, "");
        JSONObject respose = null;
        int size = 0;
        String result = "";
        try {
            respose = faceYoutu.GeneralOcrWithBitmap(expand);
            size = respose.getJSONArray("items").length();

            for (int i = 0; i < size; i++) {
                result += respose.getJSONArray("items").getJSONObject(i).getString("itemstring");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Exception = " + e.toString());
        }
        Log.i(TAG, "player index = " + mPlayerIndex + "respose+  = " + respose + "， size = " + size + ", result = " + result);


        //saveBitmap(expand, "after_"+expand+"_ExpandBitmap.png");

        expand.recycle();

        return result;
    }

    public void setBreak(Bitmap source) {
        System.out.println(source.getWidth());
        mHorizontalBreak = Bitmap.createBitmap(source, BREAK.left, BREAK.top, BREAK.right - BREAK.left, BREAK.bottom - BREAK.top);
        // 垂直方向也需要一个，否则识别有问题
        int n = BattleSituation.PLAYER_WIDTH / (BREAK.right - BREAK.left);
        Bitmap[] tmp = new Bitmap[n];
        for (int i = 0; i < tmp.length; i++) tmp[i] = mHorizontalBreak;
        mVerticalBreak = combineHorizontal(tmp);
        mVerticalBreak = Bitmap.createBitmap(mVerticalBreak, 0, 0, totalWidth, BREAK.bottom - BREAK.top);
    }

    public Bitmap combineHorizontal() {
        // TODO:根据使用的优化方法设置
        boolean useRepeat = true;
        Bitmap newNumDeadBitmap, newNumKillBitmap, newNumHelpBitmap, newLevelBitmap;
        if (useRepeat) {

            newNumDeadBitmap = horizondDouble(mNumDeadBitmap);
            newNumKillBitmap = horizondDouble(mNumKillBitmap);
            newNumHelpBitmap = horizondDouble(mNumHelpBitmap);
            newLevelBitmap = horizondDouble(mLevelBitmap);

        } else {
            newNumDeadBitmap = mNumDeadBitmap;
            newNumKillBitmap = mNumKillBitmap;
            newNumHelpBitmap = mNumHelpBitmap;
            newLevelBitmap = mLevelBitmap;
        }
        return combineHorizontal(mNameBitmap, mRoleNameBitmap,
                newNumKillBitmap,
                newNumDeadBitmap,
                newNumHelpBitmap,
                newLevelBitmap,
                mNumMoneyBitmap);
    }

    private Bitmap horizondDouble(Bitmap originBitmap) {
        int width = originBitmap.getWidth();
        int height = originBitmap.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width * 2, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(originBitmap, 0, 0, null);
        canvas.drawBitmap(originBitmap, width, 0, null);
        return bitmap;
    }
    public static int getBreakHeight() {
        return  BREAK.bottom - BREAK.top;
    }

    public static int getBreakWidth() {
        return BREAK.right - BREAK.left;
    }
}
