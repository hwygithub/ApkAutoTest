package com.tencent.apk_auto_test.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

/**
 * Created by veehou on 2017/3/9.
 */

public class UICvOperate {
    private static final String TAG = "UICvOperate";

    private UIOperate mUiOperate;
    private Context mContext;

    public UICvOperate(Context context) {
        mContext = context;
        mUiOperate = new UIOperate(context);
    }

    /**
     * 根据匹配图名称点击屏幕中匹配该图的中心坐标
     *
     * @return
     */
    public boolean clickOnImage(String matchImgName, int waitTime) {
        long start = System.currentTimeMillis();
        //截图并读出bitmap格式的数据
        Bitmap capImg = getScreenPic();
        //从res根据name读出match img的数据
        AssetManager assetManager = mContext.getResources().getAssets();
        Bitmap matchImg = null;
        try {
            matchImg = BitmapFactory.decodeStream(assetManager.open("drawable/" + matchImgName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "cap and read bitmap cost time: " + (System.currentTimeMillis() - start));
        //匹配图片并返回匹配区域的中心点
        Point p = getMatchPoint(capImg, matchImg);
        if (null == p) {
            Log.e(TAG, "get point null");
            return false;
        }
        //点击匹配区域的中心点
        return mUiOperate.click((float) (p.x), (float) (p.y), waitTime);
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
        capImg = zoomImg(capImg, 0.5f);
        matchImg = zoomImg(matchImg, 0.5f);
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
        Log.i(TAG, "match cost time: " + (System.currentTimeMillis() - start));
        //Core.normalize(result, result, 0, 100, Core.NORM_MINMAX, -1, new Mat());
        //通过函数 minMaxLoc 定位最匹配的位置
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        //匹配的数值太小就认为不是该图

        Point matchLoc;
        //对于方法 SQDIFF 和 SQDIFF_NORMED, 越小的数值代表更高的匹配结果. 而对于其他方法, 数值越大匹配越好
        matchLoc = mmr.minLoc;
        //坐标值还原放大
        Point point = new Point();
        point.x = matchLoc.x * 2 + matchImg.getWidth();
        point.y = matchLoc.y * 2 + matchImg.getHeight();
        Log.i(TAG, "point.x:" + point.x + "\tpoint.y:" + point.y);
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
        String capPath = "/sdcard/screenshot.png";
        //su 命令执行截图命令
        ExecUtil.getScreenCap(capPath);
        //获取位图数据
        Bitmap capBmp = BitmapFactory.decodeFile(capPath);
        return capBmp;
    }


}
