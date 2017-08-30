package com.example.jiamiaohe.gamehelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.os.AsyncTaskCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.jiamiaohe.gamehelper.picture.recognition.BattleSituation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;


/**
 * Created by wei on 16-12-1.
 */
public class ScreenShotterUtils implements ImageReader.OnImageAvailableListener{

        private final String TAG = "ScreenShotterUtils";

    private final SoftReference<Context> mRefContext;
    private ImageReader mImageReader;

    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;

    private String mLocalUrl = "";

    private OnShotListener mOnShotListener;

    private static ScreenShotterUtils mScreenShotterUtils = null;



    public static ScreenShotterUtils getInstance() {
        if (mScreenShotterUtils == null) {
            mScreenShotterUtils = new ScreenShotterUtils();
        }
        return mScreenShotterUtils;
    }

    private ScreenShotterUtils() {
        mRefContext = new SoftReference<>(MyApplication.getContext());
    }

    public void setSetShotterEnable(boolean enable) {
        mShotEnable = enable;
    }

    public boolean getShotterEnable() {
        return mShotEnable;
    }

    boolean mShotEnable = false;
    boolean mHasInit = false;
    public void canNotStartUtilsInit() {
        mHasInit = false;
    }

    public void init(Context context, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            mHasInit = true;
            mMediaProjection = getMediaProjectionManager().getMediaProjection(Activity.RESULT_OK,
                    data);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //1、准备Looper对象
                    Looper.prepare();
                    //2、在子线程中创建Handler
                    mWorkHandler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            Log.i("handleMessage:", Thread.currentThread().getName());
                        }
                    };
                    //3、调用Looper的loop()方法，取出消息对象
                    Looper.loop();
                }
            }).start();

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void virtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                getScreenWidth(),
                getScreenHeight(),
                Resources.getSystem().getDisplayMetrics().densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);

    }

    private void startScreenShot(OnShotListener onShotListener, String loc_url) {
        mLocalUrl = loc_url;
        startScreenShot(onShotListener);
    }

    class MyThread extends Thread {
        private Looper looper;//取出该子线程的Looper
        MyThread() {
        }

        @Override
        public void run() {
            super.run();
            Looper.prepare();//创建该子线程的Looper
            looper = Looper.myLooper();//取出该子线程的Looper
            Looper.loop();//只要调用了该方法才能不断循环取出消息
        }

        public Looper getLooper() {
            return looper;
        }
    }
    Handler mHanler = new Handler();
    Handler mWorkHandler = null;
    boolean mScreenShotAreSaving = false;
    public void startScreenShot(OnShotListener onShotListener) {
        if (!mHasInit || !mShotEnable) {
            Log.i(TAG, "startScreenShot return, mHasInit = "+mHasInit+", mShotEnable = "+mShotEnable);
            Toast.makeText(MyApplication.getContext(), "startScreenShot return", Toast.LENGTH_SHORT).show();
            return;
        }

        mOnShotListener = onShotListener;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.i(TAG, "startScreenShot");

            updateScreenChange();
            mScreenShotAreSaving = true;

            if (mImageReader == null) {
                Log.i(TAG, "startScreenShot create new mImageReader");
                mImageReader = ImageReader.newInstance(getScreenWidth(), getScreenHeight(), PixelFormat.RGBA_8888, 2);
            }

            if (mImageReader != null) {
                virtualDisplay();
                Log.i(TAG, "startScreenShot mImageReader set listener");
                mImageReader.setOnImageAvailableListener(this, mWorkHandler);
            }

//            Handler handler = new Handler();
//
//            handler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                    if (mScreenShotAreSaving) {
//                        Log.i(TAG, "alreaady start saving");
//                        return;
//                    }
//                    mScreenShotAreSaving = true;
//
//                    Image image = mImageReader.acquireLatestImage();
//
//                    AsyncTaskCompat.executeParallel(new SaveTask(), image);
//                }
//            },300);

        }

    }

    public void stop() {
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay=null;
        }
        mImageReader.close();
        mImageReader=null;
    }


    long mLastCaptureTime = 0;
    private ByteBuffer mFrameData;
    String mPicPath = null;
    @Override
    public void onImageAvailable(ImageReader imageReader) {
        Log.d(TAG, "onImageAvailable");
        Image image = imageReader.acquireNextImage();
        if (image != null) {
            int width = image.getWidth();
            int height = image.getHeight();

            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            //每个像素的间距
            int pixelStride = planes[0].getPixelStride();
            //总的间距
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;


            Log.d(TAG, "width " + image.getWidth() + ", height "+ image.getHeight() + ", format "
                    + image.getFormat() + ", pixelStride = "+pixelStride+",  rowStride = "+rowStride
                    + ", rowPadding = "+rowPadding);


            Bitmap bitmapTmp = Bitmap.createBitmap(width + rowPadding / pixelStride, height,
                    Bitmap.Config.ARGB_8888);
            bitmapTmp.copyPixelsFromBuffer(buffer);
            Bitmap bitmap = Bitmap.createBitmap(bitmapTmp, 0, 0, width, height);

            image.close();
            bitmapTmp.recycle();

            stop();

            File fileImage = null;
            if (bitmap != null) {
                try {

//                    if (TextUtils.isEmpty(mLocalUrl)) {
                        mLocalUrl = getContext().getExternalFilesDir("screenshot").getAbsoluteFile()
                                +
                                "/"
                                +
                                /*SystemClock.currentThreadTimeMillis()*/System.currentTimeMillis() + ".png";
//                    }
                    Log.i(TAG, "mLocalUrl = "+mLocalUrl);
                    fileImage = new File(mLocalUrl);

                    if (!fileImage.exists()) {
                        fileImage.createNewFile();
                    }
                    FileOutputStream out = new FileOutputStream(fileImage);
                    if (out != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.i(TAG, "FileNotFoundException e = "+e.toString());
                    fileImage = null;
                } catch (IOException e) {
                    Log.i(TAG, "IOException e = "+e.toString());
                    e.printStackTrace();
                    fileImage = null;
                }
            }
        }

        mHanler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), "截图成功", Toast.LENGTH_SHORT).show();
                mScreenShotAreSaving = false;
            }
        });
        //remove image reader
    }


    public class SaveTask extends AsyncTask<Image, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Image... params) {

            if (params == null || params.length < 1 || params[0] == null) {

                return null;
            }

            Log.i(TAG, "SaveTask doInBackground");

            Image image = params[0];

            int width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            //每个像素的间距
            int pixelStride = planes[0].getPixelStride();
            //总的间距
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height,
                    Bitmap.Config.RGB_565);
            bitmap.copyPixelsFromBuffer(buffer);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            image.close();
            File fileImage = null;
            if (bitmap != null) {
                try {

                    if (TextUtils.isEmpty(mLocalUrl)) {
                        mLocalUrl = getContext().getExternalFilesDir("screenshot").getAbsoluteFile()
                                +
                                "/"
                                +
                                /*SystemClock.currentThreadTimeMillis()*/System.currentTimeMillis() + ".png";
                    }
                    Log.i(TAG, "mLocalUrl = "+mLocalUrl);
                    fileImage = new File(mLocalUrl);

                    if (!fileImage.exists()) {
                        fileImage.createNewFile();
                    }
                    FileOutputStream out = new FileOutputStream(fileImage);
                    if (out != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    fileImage = null;
                } catch (IOException e) {
                    e.printStackTrace();
                    fileImage = null;
                }
            }

            if (fileImage != null) {
                return bitmap;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }

            if (mVirtualDisplay != null) {
                mVirtualDisplay.release();
            }

            if (mOnShotListener != null) {
                mOnShotListener.onFinish();
            }

            Log.i(TAG, "SaveTask onPostExecute");
            mScreenShotAreSaving = false;
            Toast.makeText(MyApplication.getContext(), "截图完成", Toast.LENGTH_SHORT).show();
        }
    }


    private MediaProjectionManager getMediaProjectionManager() {

        return (MediaProjectionManager) getContext().getSystemService(
                Context.MEDIA_PROJECTION_SERVICE);
    }

    private Context getContext() {
        return mRefContext.get();
    }


    private int getScreenWidth() {
//        Log.i(TAG, "getScreenWidth = "+Resources.getSystem().getDisplayMetrics().widthPixels);
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private int getScreenHeight() {
//        Log.i(TAG, "getScreenWidth = "+Resources.getSystem().getDisplayMetrics().heightPixels);
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    boolean mIsScreenLandscape = false;
    public void updateScreenChange() {

        Configuration mConfiguration = MyApplication.getContext().getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向

        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
            mIsScreenLandscape = true;
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
            mIsScreenLandscape = false;
        }
    }

    // a  call back listener
    public interface OnShotListener {
        void onFinish();
    }
}
