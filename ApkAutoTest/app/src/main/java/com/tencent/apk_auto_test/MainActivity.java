package com.tencent.apk_auto_test;

import java.util.ArrayList;
import java.util.List;

import com.tencent.apk_auto_test.data.ChoosePartAdapter;
import com.tencent.apk_auto_test.data.RunPara;
import com.tencent.apk_auto_test.data.RunPartAdapter;
import com.tencent.apk_auto_test.data.StaticData;
import com.tencent.apk_auto_test.data.TestCase;
import com.tencent.apk_auto_test.runner.CmShowDataRunner;
import com.tencent.apk_auto_test.runner.CmShowMemRunner;
import com.tencent.apk_auto_test.util.ExecUtil;
import com.tencent.apk_auto_test.util.Function;
import com.tencent.apk_auto_test.util.Global;
import com.test.function.Assert;
import com.test.function.Operate;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
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
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
    // state
    public static final int FILE_SELECT_CODE = 0;
    private static final String TAG = "MainActivity";
    // class
    private Context mContext;
    private Function mFunction;
    private ChoosePartAdapter adapter;
    private Assert mAssert;
    private Operate mOperate;
    private InputMethodManager methodManager;
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


    }

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
        mContext = getApplicationContext();
        mFunction = new Function(mContext);
        mAssert = new Assert(mContext);
        mOperate = new Operate(mContext);
    }

    private void setListener() {
        mChooseList = (ListView) findViewById(R.id.chooseList);
        mRunList = (ListView) findViewById(R.id.runList);
        mBtnStart = (Button) findViewById(R.id.btn_startRun);
        mBtnStart.setOnClickListener(this);
        // Get the sim operator number
        // If the sim dismiss,disable the start button
        if (mFunction.getSimOperatorNumber() == 0) {
            // mBtnStart.setEnabled(false);
            Toast.makeText(mContext, "sim card miss!", Toast.LENGTH_SHORT)
                    .show();
            // If the resouce dismiss,diable the start button
        } else if (!mFunction.isResouceExist()) {
            Toast.makeText(mContext, "Resouce miss!", Toast.LENGTH_SHORT)
                    .show();
        } else {
            mBtnStart.setEnabled(true);
            Toast.makeText(mContext,
                    "sim operator number:" + mFunction.getSimOperatorNumber(),
                    Toast.LENGTH_SHORT).show();
            StaticData.phoneNumber = mFunction.getSimOperatorNumber();
        }

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

        StaticData.chooseArray = new boolean[19];
        for (int i = 0; i < 18; i++) {
            StaticData.chooseArray[i] = false;
        }

        //分辨率
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        Global.SCREEN_WIDTH = mDisplayMetrics.widthPixels;
        Global.SCREEN_HEIGHT = mDisplayMetrics.heightPixels;
        /*
        // Install filelogsave apk if it is not installed
        if (!mAssert.isApkInstall("com.apptest.filelogsave")) {
            try {
                mOperate.installAsserts("FileLogSave.apk");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */
    }

    private void setEnvironment() {
        SharedPreferences shareData = getSharedPreferences("State", 0);
        isHelpDialogLocked = shareData.getBoolean("isLocked", false);
        // Test uin and password
        StaticData.testUin = shareData.getString("testUin", "1002000164");
        StaticData.testPwd = shareData.getString("testPwd", "tencent");
        if (!isHelpDialogLocked) {
            showHelpDialog();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_startRun:
                //检查是否被root
                if (!ExecUtil.isRoot()) {
                    Toast.makeText(mContext, "设备占未root，无法开始测试！！", Toast.LENGTH_LONG).show();
                    return;
                }
                //检测手Q是否是debug版本
                if (!mFunction.isAppDebug("com.tencent.mobileqq")) {
                    //提示
                    Toast.makeText(mContext, "被测试app手Q版本需要使用debug版本！", Toast.LENGTH_LONG).show();
                    return;
                }
                //检查accessibilityservice是否开启
                if (!Function.isAccessibilitySettingsOn(this)) {
                    //如果没有获取到系统的设置信息，跳转手动开启界面
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                    //提示
                    Toast.makeText(mContext, "依赖服务尚未开启，保持辅助服务APK自动测试工具为开启状态！！！", Toast.LENGTH_LONG).show();
                    return;
                }
                //检查输入法是否开启成测试工具
                if (!Function.isInputMethodSettingsEnabled(this)) {
                    //跳转到输入法选择界面
                    startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
                    //提示
                    Toast.makeText(mContext, "测试输入法尚未开启，保持输入法中APK自动测试工具被勾选中", Toast.LENGTH_LONG).show();
                    return;
                }

                //检查输入法是否默认为测试工具
                if (!Function.isInputMethodSettingsDefault(this)) {
                    //监听如果是从输入法切换后回到工具界面时弹出输入法选择框
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showInputMethodPicker();
                    //提示
                    Toast.makeText(mContext, "测试输入法尚未开启，选中输入法为APK自动测试工具，可能对实际输入有影响！！！", Toast.LENGTH_LONG).show();
                    return;
                }

                // define the choose test case
                for (int i = 0; i < StaticData.runList.size(); i++) {
                    int caseNumber = StaticData.runList.get(i).runCaseNumber;
                    StaticData.chooseArray[caseNumber] = true;
                }
                //还原一些变量
                mBtnStart.setEnabled(false);
                switch (runnerIndex) {
                    case 0:
                        startService(new Intent(MainActivity.this, CmShowMemRunner.class));
                        break;
                    case 1:
                        startService(new Intent(MainActivity.this, CmShowDataRunner.class));
                        break;
                }

                Toast.makeText(mContext, "开始测试", Toast.LENGTH_LONG).show();
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

                                shared.commit();

                            }
                        }).create();
        dlg.show();
    }

}
