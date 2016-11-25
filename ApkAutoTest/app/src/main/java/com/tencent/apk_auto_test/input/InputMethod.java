package com.tencent.apk_auto_test.input;

import java.util.concurrent.TimeUnit;

import com.tencent.apk_auto_test.input.IInputMethodService;

import android.content.Context;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

public class InputMethod extends IInputMethodService.Stub {
    private static final String INPUT_NAME = "com.tencent.apk_auto_test/.input.TestInputMethodService";
    private static String userInput;
    private static final String TAG = "InputMethod";
    private Context context;

    public InputMethod(Context context) {
        this.context = context;

    }

    @Override
    public boolean setText(String text) throws RemoteException {
        if (setUpInputMethodIfNeed()) {
            waitMoment(500);
        }
        TestInputMethodService tms = TestInputMethodService.getInstance();
        if (tms != null) {
            InputConnection ic = tms.getCurrentInputConnection();
            if (ic != null) {
                return tms.getCurrentInputConnection().commitText(text, 1);
            }
        }
        return false;
    }

    @Override
    public boolean clearText() throws RemoteException {
        if (setUpInputMethodIfNeed()) {
            waitMoment(500);
        }
        TestInputMethodService tms = TestInputMethodService.getInstance();
        Log.d(TAG, "TestInputMethodService>>" + tms);
        if (tms != null) {
            InputConnection ic = tms.getCurrentInputConnection();
            if (ic != null) {
                int beforeLength = ic.getTextBeforeCursor(100, 0).length();// tms.getCurrentInputEditorInfo().initialSelStart;
                int endLength = ic.getTextAfterCursor(100, 0).length();// tms.getCurrentInputEditorInfo().initialSelEnd;
                if (beforeLength == 0 && endLength == 0) {
                    return true;
                }
                Log.d(TAG, "setText >>beforeLength::" + beforeLength);
                Log.d(TAG, "setText >>endLength::" + endLength);
                return ic.deleteSurroundingText(beforeLength, endLength);
            }
        }
        return false;
    }

    public boolean setUpInputMethodIfNeed() {
        String mLastInputMethodId = Settings.Secure.getString(context
                        .getApplicationContext().getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD);
        Log.d(TAG, "setUpInputMethodIfNeed : " + mLastInputMethodId);
        //系统签名编译的app才能使用改方法，调用WRITE_SECURE_SETTINGS
        /*if (!INPUT_NAME.equals(mLastInputMethodId)) {
            userInput = mLastInputMethodId;
            InputMethodManager ime = (InputMethodManager) context
                    .getApplicationContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            ime.setInputMethod(null, INPUT_NAME);
            return true;
        }*/
        return false;
    }

    public void setBackUserInputIfNeed() {
        if (userInput != null) {
            InputMethodManager ime = (InputMethodManager) context
                    .getApplicationContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            ime.setInputMethod(null, userInput);
            userInput = null;
        }
    }

    @Override
    public boolean isTestInputOn() throws RemoteException {
            /* simple TAG for TMS */
        return TestInputMethodService.getInstance() != null ? true : false;
    }

    private void waitMoment(int waitMoment) {
        try {
            // SystemClock.sleep(waitMoment);
            TimeUnit.MILLISECONDS.sleep(waitMoment);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
