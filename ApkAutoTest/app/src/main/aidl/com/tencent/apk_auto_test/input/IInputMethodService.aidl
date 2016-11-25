package com.tencent.apk_auto_test.input;
interface IInputMethodService
{
   boolean setText(String text);
   boolean clearText();
   boolean isTestInputOn();
   boolean setUpInputMethodIfNeed();
   void setBackUserInputIfNeed();
}