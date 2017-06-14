package com.tencent.apk_auto_test.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
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
import com.tencent.apk_auto_test.ext.BlackBox;
import com.tencent.apk_auto_test.ext.temp.ImageShareApplication;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_MEDIA_PROJECTION = 0;
    // state
    // class
    private Context mContext;
    private BlackBox mBlackBox;
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
    private boolean mIsCheckOk = true;

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
        mBlackBox = new BlackBox(mContext);
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
        mList = mBlackBox.parserXml();
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
        Global.SCREEN_HEIGHT = getDpi();
        Global.DENSITY_DPI = mDisplayMetrics.densityDpi;

    }

    private int getDpi() {
        int dpi = 0;
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.heightPixels;
            Log.v(TAG, "height:" + dpi);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
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

        //设置5.0上面的截图方式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            if (null == mediaProjectionManager) {
                Log.e(TAG, "mediaProjectionManager is null");
                return;
            }
            Intent intent = mediaProjectionManager.createScreenCaptureIntent();
            startActivityForResult(intent, REQUEST_MEDIA_PROJECTION);
            ((ImageShareApplication) getApplication()).setMediaProjectionManager(mediaProjectionManager);
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == RESULT_OK && null != data) {
            ((ImageShareApplication) getApplication()).setIntent(data);
            ((ImageShareApplication) getApplication()).setResult(resultCode);
        } else {
            Log.e(TAG, "[onActivityResult] error!!!");
            mIsCheckOk = false;
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

                if (mIsCheckOk && manager.checkEnvironment()) {
                    //还原一些变量
                    for (int i = 0; i < StaticData.runList.size(); i++) {
                        int caseNumber = StaticData.runList.get(i).runCaseNumber;
                        StaticData.chooseArray[caseNumber] = true;
                    }
                    mBtnStart.setEnabled(false);
                    manager.startTest(runnerIndex);
                } else {
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
            mBlackBox.changeSerial2Array(testCase);
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
