package com.tencent.apk_auto_test.ext;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

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
import java.io.IOException;

/**
 * 图像相关的测试工具箱
 * <p>
 * Created by veehou on 2017/3/9.
 */

public class UIImageActionBox extends UIActionBox {
    private static final String TAG = "UIImageActionBox";

    private final int SIZE_SCALE = 2;

    public UIImageActionBox(Context context) {
        super(context);
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
        Bitmap matchImg = getMatchPic(matchImgName);
        Log.i(TAG, "cap and read bitmap cost time: " + (System.currentTimeMillis() - start));
        //匹配图片并返回匹配区域的中心点
        Point p = getMatchPoint(capImg, matchImg);
        if (null == p) {
            Log.e(TAG, "get point null");
            return false;
        }
        //点击匹配区域的中心点
        return click((float) (p.x), (float) (p.y), waitTime);
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
        Log.i(TAG, "match cost time: " + (System.currentTimeMillis() - start));
        //Core.normalize(result, result, 0, 100, Core.NORM_MINMAX, -1, new Mat());
        //通过函数 minMaxLoc 定位最匹配的位置
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        //匹配的最小数值太大就认为不是该图
        mmr.minLoc.x *= SIZE_SCALE;
        mmr.minLoc.y *= SIZE_SCALE;
        Log.i(TAG, "cv match min val: " + mmr.minVal + "--match x:" + mmr.minLoc.x + ",match y:" + mmr.minLoc.y);
        if (mmr.minVal > 0.03) {
            Log.e(TAG, "image match false");
            return null;
        }
        Point matchLoc;
        //对于方法 SQDIFF 和 SQDIFF_NORMED, 越小的数值代表更高的匹配结果. 而对于其他方法, 数值越大匹配越好
        matchLoc = mmr.minLoc;
        //坐标值还原放大
        Point point = new Point();
        point.x = matchLoc.x + matchImg.getWidth() * SIZE_SCALE / 2;
        point.y = matchLoc.y + matchImg.getHeight() * SIZE_SCALE / 2;
        Log.i(TAG, "match success , point.x:" + point.x + "\tpoint.y:" + point.y);
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
        String capPath = Environment.getExternalStorageDirectory() + "/screenshot.png";
        //su 命令执行截图命令
        ProcessUtil.getScreenCap(capPath);
        File file = new File(capPath);
        if (!file.exists()) {
            Log.e(TAG, "cap img file is not found");
            return null;
        }
        //获取位图数据
        Bitmap capBmp = null;
        try {
            //首先读入获取到长宽，然后再读入缩放后的图片
            capBmp = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
