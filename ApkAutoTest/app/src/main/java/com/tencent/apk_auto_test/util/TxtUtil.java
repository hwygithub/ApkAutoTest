package com.tencent.apk_auto_test.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TxtUtil {

    private static final String TAG = "TxtUtil";

    public static void saveMsg(String path, String msg, String name,
                               Context context, int row, int column) {
        File destDir = new File(Environment.getExternalStorageDirectory()
                + "/MpBatteryAutoTest");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        Excel.WriteToExcel(msg, row, column, name);
    }

    public static void saveMsg(String path, String msg, String name) {
        File HistoryFolder;

        HistoryFolder = new File(path);

        if (!HistoryFolder.exists()) {
            HistoryFolder.mkdirs();
        }
        File file = new File(path + File.separator + name + ".txt");
        if (file.exists()) {
            //file.delete();
        }
        try {
            //第二个参数意义是说是否以append方式添加内容
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(msg);
            bw.write("\r\n");
            bw.flush();
            bw.close();
            Log.v(TAG, msg);
        } catch (Exception e) {
            Log.e("m", "file write error");
            e.printStackTrace();
        }
    }

    public static ArrayList<String> readMsgForList(File file) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            FileInputStream fin = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            String str = "";
            str = br.readLine();
            while (str != null) {
                str = str.trim();
                list.add(str);
                Log.e("", str + "");
                str = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
