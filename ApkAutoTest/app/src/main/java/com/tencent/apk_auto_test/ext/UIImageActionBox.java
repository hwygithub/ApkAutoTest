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
import com.tencent.apk_auto_test.data.StaticData;
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
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
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
    private Image lastImage = null;

    private double mX = 1.0;
    private double mY = 1.0;

    public enum Area {
        full, upper, middle, lower, left, right
    }

    /**
     * >=5.0 以上走新的截图方式
     * <5.0 走老的截图方式
     *
     * @param context context
     */
    public UIImageActionBox(Context context) {
        super(context);
        mImageShareApplication = (ImageShareApplication) context.getApplicationContext();

        //以1080P为标准根据分辨率拉伸需要的坐标值
        mY = Global.SCREEN_HEIGHT / 1920;
        mX = Global.SCREEN_WIDTH / 1080;

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
        return this.clickOnImage(matchImgName, waitTime, Area.full);
    }

    public boolean clickOnImage(String matchImgName, int waitTime, Area area) {
        long start = System.currentTimeMillis();
        Bitmap capImg = getScreenPic(area);
        Bitmap matchImg = getMatchPic(matchImgName);
        //匹配图片并返回匹配区域的中心点
        Point p = getMatchPoint(capImg, matchImg, area);
        Log.i(TAG, "----total cost time: " + (System.currentTimeMillis() - start));
        if (null == p) {
            Log.e(TAG, "get point null");
            TestResultPrinter mPrinter = TestResultPrinter.getInstance();
            mPrinter.printResult(StaticData.currentCase + ":click image:" + matchImgName, false);
            return false;
        }
        //点击匹配区域的中心点
        return click((float) (p.x), (float) (p.y), waitTime);
    }

    /**
     * 根据匹配图名称长按屏幕中匹配该图的中心坐标
     *
     * @return 是否点击成功
     */
    public boolean clickOnImage(String matchImgName, int waitTime, int clickTime) {
        return this.clickOnImage(matchImgName, waitTime, clickTime, Area.full);
    }

    public boolean clickOnImage(String matchImgName, int waitTime, int clickTime, Area area) {
        long start = System.currentTimeMillis();
        Bitmap capImg = getScreenPic(area);
        Bitmap matchImg = getMatchPic(matchImgName);
        //匹配图片并返回匹配区域的中心点
        Point p = getMatchPoint(capImg, matchImg, area);
        Log.i(TAG, "----total cost time: " + (System.currentTimeMillis() - start));
        if (null == p) {
            Log.e(TAG, "get point null");
            TestResultPrinter mPrinter = TestResultPrinter.getInstance();
            mPrinter.printResult(StaticData.currentCase + ":long click image:" + matchImgName, false);
            return false;
        }
        //点击匹配区域的中心点
        return click((float) (p.x), (float) (p.y), waitTime, clickTime);
    }

    /**
     * 根据匹配图名称点击屏幕中匹配该图的偏移坐标
     *
     * @param waitTime   sleep time
     * @param offsetType 0:x,1:y
     * @param offset     与该控件中心的偏移量，包含正负数
     * @return 是否点击成功
     */
    public boolean clickOnImageOffset(String matchImgName, int waitTime, int offsetType, int offset) {
        return this.clickOnImageOffset(matchImgName, waitTime, offsetType, offset, Area.full);
    }
    public boolean clickOnImageOffset(String matchImgName, int waitTime, int offsetType,
                                      int offset, Area area) {
        long start = System.currentTimeMillis();
        Bitmap capImg = getScreenPic(area);
        Bitmap matchImg = getMatchPic(matchImgName);
        //匹配图片并返回匹配区域的中心点
        Point p = getMatchPoint(capImg, matchImg, area);
        Log.i(TAG, "----total cost time: " + (System.currentTimeMillis() - start));
        if (null == p) {
            Log.e(TAG, "get point null");
            TestResultPrinter mPrinter = TestResultPrinter.getInstance();
            mPrinter.printResult(StaticData.currentCase + ":click image:" + matchImgName, false);
            return false;
        }
        switch (offsetType) {
            case 0:
                return click((float) (p.x + offset * mX), (float) p.y, waitTime);
            case 1:
                return click((float) p.x, (float) (p.y + offset * mY), waitTime);
            default:
                return click((float) (p.x), (float) (p.y), waitTime);
        }
    }

    /**
     * 判断图像是否存在
     *
     * @param matchImgName 资源名称
     * @return 是否存在
     */
    public boolean isImageExist(String matchImgName) {
        return this.isImageExist(matchImgName, Area.full);
    }
    public boolean isImageExist(String matchImgName, Area area) {
        long start = System.currentTimeMillis();
        //截图并读出bitmap格式的数据
        Bitmap capImg = getScreenPic(area);
        Bitmap matchImg = getMatchPic(matchImgName);
        //匹配图片并返回匹配区域的中心点
        Point p = getMatchPoint(capImg, matchImg, area);
        Log.i(TAG, "[isImageExist]----total cost time: " + (System.currentTimeMillis() - start));
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
                .VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY, mImageReader.getSurface(), null, null);

        if (null == virtualDisplay) {
            Log.e(TAG, "virtualDisplay is null!!!");
            return;
        }
    }

    private Point getMatchPoint(Bitmap capImg, Bitmap matchImg, Area area) {
        //缩放倍率
        int SIZE_SCALE = 2;
        //部分截图时，还原坐标用的修正值
        int deltaX, deltaY;
        //输入图片为空判断
        if (null == capImg) {
            Log.e(TAG, "capImg is not found");
            return null;
        }
        if (null == matchImg) {
            Log.e(TAG, "matchImg is not found");
            return null;
        }

        deltaX = matchImg.getWidth() / 2;
        deltaY = matchImg.getHeight() / 2;

        switch (area) {
            case lower:
                deltaY += capImg.getHeight();
                break;
            case middle:
                deltaY += capImg.getHeight() / 2;
                break;
            case right:
                deltaX += capImg.getWidth();
                break;
            default:
                break;
        }

        long startTime = System.currentTimeMillis();
        //根据分辨率与标准1080p高宽ed比率缩放匹配图
        if (mX != 1 | mY != 1) {
            zoomImg(matchImg, (float) mX, (float) mY);
            Log.v(TAG, "scale match image,mx=" + mX + "  my=" + mY + "---cost time:" + (System.currentTimeMillis() - startTime));
        }
        //缩放原图和匹配图，提高运行效率
        capImg = zoomImg(capImg, 1.0f / SIZE_SCALE, 1.0f / SIZE_SCALE);
        matchImg = zoomImg(matchImg, 1.0f / SIZE_SCALE, 1.0f / SIZE_SCALE);
        Log.i(TAG, "[getMatchPoint]#1 zoomImg cost time: " + (System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();
        //转成mat格式的数据
        Mat img1 = new Mat();
        Utils.bitmapToMat(capImg, img1);
        Mat img2 = new Mat();
        Utils.bitmapToMat(matchImg, img2);
        //创建输出结果的矩阵
        int result_cols = img1.cols() - img2.cols() + 1;
        int result_rows = img1.rows() - img2.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);
        Log.i(TAG, "[getMatchPoint]#2 bitmapToMat cost time: " + (System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();
        //进行匹配和标准化
        Imgproc.matchTemplate(img1, img2, result, Imgproc.TM_SQDIFF_NORMED);
        Log.i(TAG, "[getMatchPoint]#3 match cost time: " + (System.currentTimeMillis() - startTime));


        startTime = System.currentTimeMillis();
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
        Log.i(TAG, "[getMatchPoint]#4 find point cost time: " + (System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();
        Point matchLoc;
        //对于方法 SQDIFF 和 SQDIFF_NORMED, 越小的数值代表更高的匹配结果. 而对于其他方法, 数值越大匹配越好
        matchLoc = mmr.minLoc;
        //坐标值还原放大
        Point point = new Point();
        point.x = matchLoc.x + deltaX;
        point.y = matchLoc.y + deltaY;
        Log.i(TAG, "[getMatchPoint]#5 recovery cost time: " + (System.currentTimeMillis() - startTime));
        Log.i(TAG, "[getMatchPoint]match success , point.x:" + point.x + "\tpoint.y:" + point.y);

        return point;
    }

    private Bitmap zoomImg(Bitmap im, float xScale, float yScale) {
        int width = im.getWidth();
        int height = im.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(xScale, yScale);
        Bitmap newBitmap = Bitmap.createBitmap(im, 0, 0, width, height, matrix, true);
        return newBitmap;
    }

    private Bitmap getScreenPic(Area area) {
        long startTime = System.currentTimeMillis();
        int x, y, w, h;
        Bitmap capBmp = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0以上通过 MediaProjection 投影类获取到屏幕截图
            Image image = mImageReader.acquireLatestImage();
            if (null == image && null != lastImage) {
                Log.d(TAG, "image is null, use the last frame...");
                image = lastImage;
            } else if (null == image) {
                Log.e(TAG, "getScreenPic: --------------Cannot get the screenshot!");
                return null;
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
            switch (area) {
                case upper:
                    x = 0; y = 0; w = width; h = height / 2;
                    break;
                case middle:
                    x = 0; y = height / 4; w = width; h = height / 2;
                    break;
                case lower:
                    x = 0; y = height / 2; w = width; h = height / 2;
                    break;
                case left:
                    x = 0; y = 0; w = width / 2; h = height;
                    break;
                case right:
                    x = width / 2; y = 0; w = width / 2; h = height;
                    break;
                default:
                    x = 0; y = 0; w = width; h = height / 2;
                    break;
            }
            bitmap.copyPixelsFromBuffer(byteBuffer);
            bitmap = Bitmap.createBitmap(bitmap, x, y, w, h);
            Log.v(TAG, "[getScreenPic]capture screen image cost time :" + (System.currentTimeMillis() - startTime));

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
            lastImage = image;
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
            Log.v(TAG, "[getScreenPic]capture screen image cost time :" + (System.currentTimeMillis() - startTime));
        }
        return capBmp;
    }

    private Bitmap getMatchPic(String matchImgName) {
        long startTime = System.currentTimeMillis();
        //从res根据name读出match img的数据
        AssetManager assetManager = mContext.getResources().getAssets();
        Bitmap matchImg = null;
        try {
            //首先读入获取到长宽，然后再读入缩放后的图片
            matchImg = BitmapFactory.decodeStream(assetManager.open("drawable/" + matchImgName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "[getMatchPic]get match image cost time :" + (System.currentTimeMillis() - startTime));
        return matchImg;
    }

    /**
     * 存储当前截屏
     *
     * @param detail 当前截屏所属的用例
     */
    public void saveScreenshot(String detail) {
        Bitmap img = getScreenPic(Area.full);
        if (img == null || img.getHeight() == 0 || img.getWidth() == 0) {
            Log.e(TAG, "saveScreenshot: -------------image is empty");
            return;
        }
        String path = detail.replace(" ", "_").replace(":", "-").replace(".", "-");
        try {
            String pathUTF = new String(path.getBytes(), "utf-8");
            File file = new File("/sdcard/tencent-test/FailedScreenshot/" + pathUTF + ".png");
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            img.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Log.d(TAG, "saveScreenshot: Screenshot saved at" + path);
        } catch (IOException e) {
            Log.e(TAG, "file not found!!!");
        }
    }

    public void clearScreenshot() {
        try {
            File file = new File("/sdcard/tencent-test/FailedScreenshot");
            if (!file.exists())
                file.mkdirs();
            if (file.isDirectory()) {
                File[] childFiles = file.listFiles();
                if (childFiles != null && childFiles.length != 0) {
                    for (File tmpFile : childFiles) {
                        tmpFile.delete();
                    }
                }
            }
            Log.d(TAG, "clearScreenshot: Screenshot ready!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
