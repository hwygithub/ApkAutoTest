package com.tencent.apk_auto_test.data;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Environment;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StaticData {
    // Resouce uri
    public static final String BROWSE_URL = "http://www.baidu.com";
    public static final String DOWNLOAD_URL = "http://218.206.177.209:8080/waptest/browser15/file/test.exe";
    public static final String TEXT_URL = Environment.getExternalStorageDirectory()
            + "/0BatteryTestResouce/TestTxt.txt";
    public static final String APK_URL = Environment.getExternalStorageDirectory() + "/0BatteryTestResouce/DeadRun.apk";
    public static final String IMAGE_URL = Environment.getExternalStorageDirectory()
            + "/0BatteryTestResouce/SendMms.jpg";
    public static final String CASE_URL = Environment.getExternalStorageDirectory()
            + "/0BatteryTestResouce/TestCase.xml";
    public static final String MUSIC_URL = "file://" + Environment.getExternalStorageDirectory()
            + "/0BatteryTestResouce/TestMusic.mp3";
    public static final String VEDIO_URL = "file://" + Environment.getExternalStorageDirectory()
            + "/0BatteryTestResouce/TestVideo.mp4";
    public static final String Galley_URL = Environment.getExternalStorageDirectory() + "/0BatteryTestResouce/01.png";

    // // class
    //public static FileDealer mDealer;/* The service with AIDL to write excel */
    // // array
    public static RunPartAdapter runAdapter;
    public static ArrayList<RunPara> runList;
    public static String[] chooseListText;
    public static boolean[] chooseArray;

    // //widget
    public static ProgressBar mBar;
    public static TextView endText;
    // // config
    // debug to set 0.1,to shorten the test time
    public static double timeGene = 1;
    public static String vision = "Mp_Battery_Test_Out_v0.1_150114";
    /*
     * test phone number:cmcc:10086,unicom:10010 ,telecom:10000
     */
    public static int phoneNumber = 10086;
    public static int minLevel = 5;/* the finish test with min level */
    public static int screenOffTimeUnit = 30;
    // //data
    public static int caseNumber;
    public static long caseStartTime;
    public static long testStartTime;
    public static String testFinishEvent;
    public static String runState;/* Test time:Circle or once */
    public static String testEndSendNumber;
    // Test parameter
    public static int testScreenOffTime = 1800;/* TestScreenofftime:30min */
    // //state
    public static boolean isRunningInputService = false;

}
