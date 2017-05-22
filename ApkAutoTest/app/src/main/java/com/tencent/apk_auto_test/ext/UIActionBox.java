package com.tencent.apk_auto_test.ext;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tencent.apk_auto_test.util.ProcessUtil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 标准测试工具箱
 */
public class UIActionBox {
    public Context mContext;

    private static final String TAG = UIActionBox.class.getSimpleName();
    private static final boolean DEBUG = true;
    private final long mLongPressTimeout;

    public UIActionBox(Context ctx) {

        mContext = ctx;
        mLongPressTimeout = 1000 * 2 + 100;
    }

    /**
     * Clicks at coordinates without waiting for device idle. This may be used
     * for operations that require stressing the target.
     *
     * @param x
     * @param y
     * @param waitTime
     * @return
     */
    public boolean click(float x, float y, long waitTime) {
        Log.d(TAG, "click (" + x + ", " + y + ")");
//        try {
//            Runtime runtime = Runtime.getRuntime();
//            DataOutputStream dataOut;
//            Process process = runtime.exec("su ");
//            InputStream in = process.getInputStream();
//            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));
//            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//            String line = null;
//            dataOut = new DataOutputStream(process.getOutputStream());
//            dataOut.writeBytes("input tap " + x + " " + y + ";");
//            dataOut.flush();
//            dataOut.close();
//            process.waitFor();
//            sleep(waitTime);
//            while ((line = err.readLine()) != null) {
//                Log.i(TAG, line);
//                return false;
//            }
//            while ((line = bufferReader.readLine()) != null) {
//                Log.i(TAG, line);
//            }
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.i(TAG, e.getMessage());
//            return false;
//        }
        try {
            ProcessUtil.execute("input tap " + x + " " + y + ";");
            sleep(waitTime);
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;


    }

    /**
     * Clicks at coordinates without waiting for device idle. This may be used
     * for operations that require stressing the target.
     *
     * @param x
     * @param y
     * @param waitTime
     * @param clickTime
     * @return
     */

    public boolean click(float x, float y, long waitTime, int clickTime) {
        Log.d(TAG, "click long (" + x + ", " + y + ")," + clickTime);
        try {
            Runtime runtime = Runtime.getRuntime();
            DataOutputStream dataOut;
            Process process = runtime.exec("su ");
            InputStream in = process.getInputStream();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            dataOut = new DataOutputStream(process.getOutputStream());
            dataOut.writeBytes("input swipe " + x + " " + y + " " + x + " " + y + " " + clickTime + ";");
            dataOut.flush();
            dataOut.close();
            process.waitFor();
            sleep(waitTime);
            while ((line = err.readLine()) != null) {
                Log.i(TAG, line);
                return false;
            }
            while ((line = bufferReader.readLine()) != null) {
                Log.i(TAG, line);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, e.getMessage());
            return false;
        }

    }

    /**
     * Double Clicks at coordinates without waiting for device idle. This may be
     * used for operations that require stressing the target.
     *
     * @param x
     * @param y
     * @return
     */
    public boolean doubleClick(float x, float y) {
        Log.d(TAG, "doubleClick (" + x + ", " + y + ")");

        try {
            Runtime runtime = Runtime.getRuntime();
            DataOutputStream dataOut;
            Process process = runtime.exec("su ");
            InputStream in = process.getInputStream();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            dataOut = new DataOutputStream(process.getOutputStream());
            dataOut.writeBytes("input tap " + x + " " + y + ";");
            dataOut.writeBytes("input tap " + x + " " + y + ";");
            dataOut.flush();
            dataOut.close();
            process.waitFor();
            while ((line = err.readLine()) != null) {
                Log.i(TAG, line);
                return false;
            }
            while ((line = bufferReader.readLine()) != null) {
                Log.i(TAG, line);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, e.getMessage());
            return false;
        }
    }

    /**
     * Handle swipes in any direction.
     *
     * @param downX
     * @param downY
     * @param upX
     * @param upY
     * @param duration
     * @return
     */
    public boolean swipe(float downX, float downY, float upX, float upY, long duration) {
        try {
            Runtime runtime = Runtime.getRuntime();
            DataOutputStream dataOut;
            Process process = runtime.exec("su ");
            InputStream in = process.getInputStream();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            dataOut = new DataOutputStream(process.getOutputStream());
            dataOut.writeBytes("input swipe " + downX + " " + downY + " " + upX + " " + upY + " " + duration + ";");
            dataOut.flush();
            dataOut.close();
            process.waitFor();
            while ((line = err.readLine()) != null) {
                Log.i(TAG, line);
                return false;
            }
            while ((line = bufferReader.readLine()) != null) {
                Log.i(TAG, line);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, e.getMessage());
            return false;
        }
    }


    public boolean sendKey(int keyCode, int waitTime) {
        int metaState = 0;
        if (DEBUG) {
            Log.d(TAG, "sendKey (" + keyCode + ", " + metaState + ")");
        }
        try {
            Runtime runtime = Runtime.getRuntime();
            DataOutputStream dataOut;
            Process process = runtime.exec("su ");
            InputStream in = process.getInputStream();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            dataOut = new DataOutputStream(process.getOutputStream());
            dataOut.writeBytes("input keyevent " + keyCode + ";");
            dataOut.flush();
            dataOut.close();
            process.waitFor();
            sleep(waitTime);
            while ((line = err.readLine()) != null) {
                Log.i(TAG, line);
                return false;
            }
            while ((line = bufferReader.readLine()) != null) {
                Log.i(TAG, line);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, e.getMessage());
            return false;
        }
    }

    public boolean sendLongKey(int keyCode, int metaState) {
        if (DEBUG) {
            Log.d(TAG, "sendLongKey (" + keyCode + ", " + metaState + ")");
        }
        try {
            Runtime runtime = Runtime.getRuntime();
            DataOutputStream dataOut;
            Process process = runtime.exec("su ");
            InputStream in = process.getInputStream();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            dataOut = new DataOutputStream(process.getOutputStream());
            dataOut.writeBytes("input keyevent --longpress " + keyCode + ";");
            dataOut.flush();
            dataOut.close();
            process.waitFor();
            while ((line = err.readLine()) != null) {
                Log.i(TAG, line);
                return false;
            }
            while ((line = bufferReader.readLine()) != null) {
                Log.i(TAG, line);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, e.getMessage());
            return false;
        }
    }

    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void openApp(String pkg, String activity) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(pkg, activity));
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

}
