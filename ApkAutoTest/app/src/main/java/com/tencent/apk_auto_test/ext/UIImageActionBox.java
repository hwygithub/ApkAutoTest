package com.tencent.apk_auto_test.ext;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.tencent.apk_auto_test.core.TestResultPrinter;
import com.tencent.apk_auto_test.data.Global;
import com.tencent.apk_auto_test.ext.temp.ImageShareApplication;
import com.tencent.apk_auto_test.util.ProcessUtil;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * 图像相关的测试工具箱
 * <p>
 * Created by veehou on 2017/3/9.
 */

public class UIImageActionBox extends UIActionBox {
    private static final String TAG = "UIImageActionBox";

    private ImageShareApplication mImageShareApplication;
    private ImageReader mImageReader;

    /**
     * >=5.0 以上走新的截图方式
     * <5.0 走老的截图方式
     *
     * @param context               context
     * @param imageShareApplication app
     */
    public UIImageActionBox(Context context) {
        super(context);
        mImageShareApplication = (ImageShareApplication) context.getApplicationContext();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            startVirtualDisplay();
        }
    }


    /**
     * 根据匹配图名称点击屏幕中匹配该图的中心坐标
     *
     * @return 是否点击成功
     */
    public boolean clickOnImage(String matchImgName, int waitTime) {
        long start = System.currentTimeMillis();
        Bitmap capImg = getScreenPic();
        Bitmap matchImg = getMatchPic(matchImgName);
        //匹配图片并返回匹配区域的中心点
        Point p = getMatchPoint(capImg, matchImg);
        Log.i(TAG, "----total cost time: " + (System.currentTimeMillis() - start));
        if (null == p) {
            Log.e(TAG, "get point null");
            TestResultPrinter mPrinter = TestResultPrinter.getInstance();
            mPrinter.printResult("click image :" + matchImgName, false);
            return false;
        }
        //点击匹配区域的中心点
        return click((float) (p.x), (float) (p.y), waitTime);
    }

    /**
     * 判断图像是否存在
     *
     * @param matchImgName 资源名称
     * @return 是否存在
     */
    public boolean isImageExist(String matchImgName) {
        long start = System.currentTimeMillis();
        //截图并读出bitmap格式的数据
        Bitmap capImg = getScreenPic();
        Bitmap matchImg = getMatchPic(matchImgName);
        //匹配图片并返回匹配区域的中心点
        Point p = getMatchPoint(capImg, matchImg);
        Log.i(TAG, "----total cost time: " + (System.currentTimeMillis() - start));
        if (null == p) {
            Log.e(TAG, "get point null");
            return false;
        } else {
            Log.v(TAG, "find image " + matchImgName);
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startVirtualDisplay() {
        MediaProjectionManager mediaProjectionManager = mImageShareApplication.getMediaProjectionManager();
        int resultCode = mImageShareApplication.getResult();
        Intent intent = mImageShareApplication.getIntent();
        MediaProjection mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, intent);

        mImageReader = ImageReader.newInstance((int) Global.SCREEN_WIDTH, (int) Global.SCREEN_HEIGHT, 0x1, 2);
        if (null == mediaProjection) {
            Log.e(TAG, "get media projection error !!!");
            return;
        } else {
            Log.v(TAG, "open media projection ok...");
        }

        final VirtualDisplay virtualDisplay = mediaProjection.createVirtualDisplay(TAG + "-display", (int) Global.SCREEN_WIDTH, (int) Global
                .SCREEN_HEIGHT, Global
                .DENSITY_DPI, DisplayManager
                .VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, null);

        if (null == virtualDisplay) {
            Log.e(TAG, "virtualDisplay is null!!!");
            return;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private class TestImageAvailableListener implements ImageReader.OnImageAvailableListener {

        @Override
        public void onImageAvailable(ImageReader reader) {
            Log.v(TAG, "[onImageAvailable]");
        }
    }

    private Point getMatchPoint(Bitmap capImg, Bitmap matchImg) {
        //输入图片为空判断
        if (null == capImg) {
            Log.e(TAG, "capImg is not found");
            return null;
        }
        if (null == matchImg) {
            Log.e(TAG, "capImg is not found");
            return null;
        }
        //缩放原图和匹配图，提高运行效率
        int SIZE_SCALE = 2;
        capImg = zoomImg(capImg, 1.0f / SIZE_SCALE);
        matchImg = zoomImg(matchImg, 1.0f / SIZE_SCALE);
        //转成mat格式的数据
        Mat img1 = new Mat();
        Utils.bitmapToMat(capImg, img1);
        Mat img2 = new Mat();
        Utils.bitmapToMat(matchImg, img2);
        //创建输出结果的矩阵
        int result_cols = img1.cols() - img2.cols() + 1;
        int result_rows = img1.rows() - img2.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);
        long start = System.currentTimeMillis();
        //进行匹配和标准化
        Imgproc.matchTemplate(img1, img2, result, Imgproc.TM_SQDIFF_NORMED);

        //Core.normalize(result, result, 0, 100, Core.NORM_MINMAX, -1, new Mat());
        //通过函数 minMaxLoc 定位最匹配的位置
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        //匹配的最小数值太大就认为不是该图
        mmr.minLoc.x *= SIZE_SCALE;
        mmr.minLoc.y *= SIZE_SCALE;
        Log.i(TAG, "cv match min val: " + mmr.minVal + "--match x:" + mmr.minLoc.x + ",match y:" + mmr.minLoc.y);
        if (mmr.minVal > 0.03) {
            Log.e(TAG, "image match false!!!");
            return null;
        }
        Point matchLoc;
        //对于方法 SQDIFF 和 SQDIFF_NORMED, 越小的数值代表更高的匹配结果. 而对于其他方法, 数值越大匹配越好
        matchLoc = mmr.minLoc;
        //坐标值还原放大
        Point point = new Point();
        point.x = matchLoc.x + matchImg.getWidth() * SIZE_SCALE / 2;
        point.y = matchLoc.y + matchImg.getHeight() * SIZE_SCALE / 2;
        Log.i(TAG, "[getMatchPoint]match cost time: " + (System.currentTimeMillis() - start));
        Log.i(TAG, "[getMatchPoint]match success , point.x:" + point.x + "\tpoint.y:" + point.y);
        return point;
    }

    private Bitmap zoomImg(Bitmap im, float scale) {
        int width = im.getWidth();
        int height = im.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap newBitmap = Bitmap.createBitmap(im, 0, 0, width, height, matrix, true);
        return newBitmap;
    }

    private Bitmap getScreenPic() {
        Bitmap capBmp = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0以上通过 MediaProjection 投影类获取到屏幕截图
            long startTime = System.currentTimeMillis();
            Image image = mImageReader.acquireLatestImage();
            if (null == image) {
                Log.e(TAG, "image is null!!!");
            }
            int width = image.getWidth();
            int height = image.getHeight();
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer byteBuffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
            if (null == bitmap) {
                Log.e(TAG, "bitmap is null!!!");
                return null;
            }
            bitmap.copyPixelsFromBuffer(byteBuffer);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            Log.v(TAG, "[getScreenPic]capture cost time :" + (System.currentTimeMillis() - startTime));


           /*
           startTime = System.currentTimeMillis();
            File file = new File("sdcard/temp/temp_" + System.currentTimeMillis() + ".png");
            if (file.exists())
                file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (java.io.IOException e) {
                Log.e(TAG, "file not found!!!");
            }
            Log.v(TAG, "[getScreenPic]save bitmap cost time :" + (System.currentTimeMillis() - startTime));
            */
            image.close();
            capBmp = bitmap;
        } else {
            //5.0以下通过截图命令获取到屏幕截图
            String capPath = Environment.getExternalStorageDirectory() + "/screenshot.png";
            //su 命令执行截图命令
            ProcessUtil.getScreenCap(capPath);
            File file = new File(capPath);
            if (!file.exists()) {
                Log.e(TAG, "cap img file is not found");
                return null;
            }
            //获取位图数据
            try {
                //首先读入获取到长宽，然后再读入缩放后的图片
                capBmp = BitmapFactory.decodeStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return capBmp;
    }

    private Bitmap getMatchPic(String matchImgName) {
        //从res根据name读出match img的数据
        AssetManager assetManager = mContext.getResources().getAssets();
        Bitmap matchImg = null;
        try {
            //首先读入获取到长宽，然后再读入缩放后的图片
            matchImg = BitmapFactory.decodeStream(assetManager.open("drawable/" + matchImgName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matchImg;
    }


}
