package com.tencent.apk_auto_test.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.tencent.apk_auto_test.R;
import com.tencent.apk_auto_test.core.TestManager;
import com.tencent.apk_auto_test.data.ChoosePartAdapter;
import com.tencent.apk_auto_test.data.Global;
import com.tencent.apk_auto_test.data.RunPara;
import com.tencent.apk_auto_test.data.RunPartAdapter;
import com.tencent.apk_auto_test.data.StaticData;
import com.tencent.apk_auto_test.data.TestCase;
import com.tencent.apk_auto_test.util.Function;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_MEDIA_PROJECTION = 0;
    // state
    // class
    private Context mContext;
    private Function mFunction;
    private ChoosePartAdapter adapter;
    private MediaProjectionManager mediaProjectionManager;
    // widget
    private ListView mChooseList;
    private ListView mRunList;
    private Spinner mSpnTestOrder;
    private Button mBtnStart;
    // data
    private boolean isHelpDialogLocked;
    private List<TestCase> mList;
    private int runnerIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setClass();
        setListener();
        setData();
        setEnvironment();
        setOpenCv();
    }


    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this,
                mLoaderCallback);
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem actionBar = menu.add(0, 0, 0,
                getResources().getString(R.string.str_help_title));
        actionBar.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                showHelpDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    private void setClass() {
        mContext = this;
        mFunction = new Function(mContext);
    }

    private void setListener() {
        mChooseList = (ListView) findViewById(R.id.chooseList);
        mRunList = (ListView) findViewById(R.id.runList);
        mBtnStart = (Button) findViewById(R.id.btn_startRun);
        mBtnStart.setOnClickListener(this);

        mSpnTestOrder = (Spinner) findViewById(R.id.spn_test_order);
        mSpnTestOrder
                .setOnItemSelectedListener(new SpinnerItemSelectedListener());
        // Get testCase from the xml
        mList = mFunction.parserXml();
        if (null == mList) {
            return;
        }
        // Set spinner data
        String[] mItems = new String[mList.size()];
        for (int j = 0; j < mItems.length; j++) {
            mItems[j] = mList.get(j).getName();
        }
        ArrayAdapter<String> _Adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mItems);
        mSpnTestOrder.setAdapter(_Adapter);

        StaticData.mBar = (ProgressBar) findViewById(R.id.progbar_show);
        StaticData.mBar.setProgress(0);
        StaticData.endText = (TextView) findViewById(R.id.endText);
    }

    private void setData() {
        StaticData.chooseListText = mList.get(runnerIndex).getCaseName();
        // Set case adapter
        adapter = new ChoosePartAdapter(mContext);
        mChooseList.setAdapter(adapter);

        StaticData.runList = new ArrayList<RunPara>();
        StaticData.runAdapter = new RunPartAdapter(mContext);
        mRunList.setAdapter(StaticData.runAdapter);

        StaticData.chooseArray = new boolean[50];
        for (int i = 0; i < 50; i++) {
            StaticData.chooseArray[i] = false;
        }

        //分辨率
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        Global.SCREEN_WIDTH = mDisplayMetrics.widthPixels;
        Global.SCREEN_HEIGHT = mDisplayMetrics.heightPixels;
        Global.DENSITY_DPI = mDisplayMetrics.densityDpi;

    }

    private void setEnvironment() {
        // 帮助
        SharedPreferences shareData = getSharedPreferences("State", 0);
        isHelpDialogLocked = shareData.getBoolean("isLocked", false);
        // Test uin and password
        StaticData.testUin = shareData.getString("testUin", "1002000164");
        StaticData.testPwd = shareData.getString("testPwd", "tencent");
        if (!isHelpDialogLocked) {
            showHelpDialog();
        }

//        //设置5.0上面的截图方式
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
//            Intent intent = mediaProjectionManager.createScreenCaptureIntent();
//            startActivityForResult(intent, REQUEST_MEDIA_PROJECTION);
//        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            MediaProjection mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
            ImageReader imageReader = ImageReader.newInstance((int) Global.SCREEN_WIDTH, (int) Global.SCREEN_HEIGHT, 0x1, 2);
            if (null == mediaProjection) {
                Log.e(TAG, "get media projection error !!!");
            } else {
                Log.v(TAG, "open media projection ok...");
            }
            VirtualDisplay virtualDisplay = mediaProjection.createVirtualDisplay(TAG + "-display", (int) Global.SCREEN_WIDTH, (int) Global
                    .SCREEN_HEIGHT, Global
                    .DENSITY_DPI, DisplayManager
                    .VIRTUAL_DISPLAY_FLAG_PUBLIC, imageReader.getSurface(), null, null);


            Image image = imageReader.acquireLatestImage();
            if (null == image) {
                Log.e(TAG, "image is null!!!");
                return;
            }
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer byteBuffer = planes[0].getBuffer();
        }
    }

    private void setOpenCv() {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
            Log.e(TAG, "initialization error");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_startRun:
                TestManager manager = new TestManager(mContext);
                boolean isCheckOK = manager.checkEnvironment();
                if (isCheckOK) {
                    //还原一些变量
                    for (int i = 0; i < StaticData.runList.size(); i++) {
                        int caseNumber = StaticData.runList.get(i).runCaseNumber;
                        StaticData.chooseArray[caseNumber] = true;
                    }
                    mBtnStart.setEnabled(false);
                    manager.startTest(runnerIndex);
                }
                break;
        }
    }

    class SpinnerItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View v, int index,
                                   long arg3) {
            //重新拉取数据,更新choose adapter
            StaticData.chooseListText = mList.get(index).getCaseName();
            adapter.notifyDataSetChanged();

            StaticData.timeGene = mList.get(index).getTimeGene();
            StaticData.runState = mList.get(index).getRunState();
            String[] testCase = mList.get(index).getCaseOrder();
            runnerIndex = index;
            //重新从xml拉取用例的顺序，并更新 run adapter
            mFunction.changeSerial2Array(testCase);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }

    }

    private void showHelpDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View DialogView = factory.inflate(R.layout.dialog_help, null);
        final CheckBox ck = (CheckBox) DialogView.findViewById(R.id.dialog_no_show);
        ck.setChecked(isHelpDialogLocked);
        final EditText editUin = (EditText) DialogView.findViewById(R.id.edit_test_uin);
        editUin.setText(StaticData.testUin);
        final EditText editPassword = (EditText) DialogView.findViewById(R.id.edit_test_psd);
        editPassword.setText(StaticData.testPwd);

        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle(R.string.str_help_title)
                .setView(DialogView)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String testUin = editUin.getText().toString();
                                StaticData.testUin = testUin;
                                String testPwd = editPassword.getText().toString();
                                StaticData.testPwd = testPwd;

                                Editor shared = getSharedPreferences("State", 0).edit();
                                shared.putBoolean("isLocked", ck.isChecked());
                                shared.putString("testUin", testUin);
                                shared.putString("testPwd", testPwd);

                                shared.apply();

                            }
                        }).create();
        dlg.show();
    }

}
