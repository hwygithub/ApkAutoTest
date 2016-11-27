package com.tencent.apk_auto_test.input;
interface IInputMethodService
{
   boolean setText(String text,int delayTime);
   boolean clearText();
   boolean isTestInputOn();
   boolean setUpInputMethodIfNeed();
   void setBackUserInputIfNeed();
}