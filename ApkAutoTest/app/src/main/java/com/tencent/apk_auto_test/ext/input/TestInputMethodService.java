package com.tencent.apk_auto_test.ext.input;

import android.app.Dialog;
import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodSubtype;

public class TestInputMethodService extends InputMethodService {
    private static TestInputMethodService service;
    private static final String TAG = TestInputMethodService.class
            .getSimpleName();

    public static TestInputMethodService getInstance() {
        return service;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        service = TestInputMethodService.this;
        Log.d(TAG, "TestInputMethodService create.");
    }

    @Override
    public boolean getCurrentInputStarted() {
        return super.getCurrentInputStarted();
    }

    @Override
    public Dialog getWindow() {
        return super.getWindow();
    }

    @Override
    public void hideWindow() {
        super.hideWindow();
    }

    @Override
    public boolean isFullscreenMode() {
        return super.isFullscreenMode();
    }

    @Override
    public boolean isInputViewShown() {
        return super.isInputViewShown();
    }

    @Override
    public void onBindInput() {
        super.onBindInput();
        Log.d(TAG, "New client bound to this inputmethod");
    }

    @Override
    public View onCreateInputView() {
        return super.onCreateInputView();
    }

    @Override
    protected void onCurrentInputMethodSubtypeChanged(
            InputMethodSubtype newSubtype) {
        super.onCurrentInputMethodSubtypeChanged(newSubtype);
    }

    @Override
    public void onFinishInput() {
        super.onFinishInput();
    }

    @Override
    public void onFinishInputView(boolean arg0) {
        super.onFinishInputView(arg0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
    }

    @Override
    public void onUnbindInput() {
        super.onUnbindInput();
    }

    @Override
    public void onWindowHidden() {
        super.onWindowHidden();
    }

    @Override
    public void onWindowShown() {
        super.onWindowShown();
    }

    @Override
    public void requestHideSelf(int flags) {
        super.requestHideSelf(flags);
    }

    @Override
    public void sendDownUpKeyEvents(int keyEventCode) {
        super.sendDownUpKeyEvents(keyEventCode);
    }

    @Override
    public void setInputView(View view) {
        super.setInputView(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        service = null;
    }
}
