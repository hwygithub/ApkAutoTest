/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\workplace-web\\ApkAutoTest\\app\\src\\main\\aidl\\com\\tencent\\apk_auto_test\\input\\IInputMethodService.aidl
 */
package com.tencent.apk_auto_test.input;
public interface IInputMethodService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.tencent.apk_auto_test.input.IInputMethodService
{
private static final java.lang.String DESCRIPTOR = "com.tencent.apk_auto_test.input.IInputMethodService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.tencent.apk_auto_test.input.IInputMethodService interface,
 * generating a proxy if needed.
 */
public static com.tencent.apk_auto_test.input.IInputMethodService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.tencent.apk_auto_test.input.IInputMethodService))) {
return ((com.tencent.apk_auto_test.input.IInputMethodService)iin);
}
return new com.tencent.apk_auto_test.input.IInputMethodService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_setText:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.setText(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_clearText:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.clearText();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isTestInputOn:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isTestInputOn();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setUpInputMethodIfNeed:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.setUpInputMethodIfNeed();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setBackUserInputIfNeed:
{
data.enforceInterface(DESCRIPTOR);
this.setBackUserInputIfNeed();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.tencent.apk_auto_test.input.IInputMethodService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public boolean setText(java.lang.String text) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(text);
mRemote.transact(Stub.TRANSACTION_setText, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean clearText() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_clearText, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean isTestInputOn() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isTestInputOn, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean setUpInputMethodIfNeed() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setUpInputMethodIfNeed, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setBackUserInputIfNeed() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setBackUserInputIfNeed, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_setText = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_clearText = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_isTestInputOn = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_setUpInputMethodIfNeed = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_setBackUserInputIfNeed = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public boolean setText(java.lang.String text) throws android.os.RemoteException;
public boolean clearText() throws android.os.RemoteException;
public boolean isTestInputOn() throws android.os.RemoteException;
public boolean setUpInputMethodIfNeed() throws android.os.RemoteException;
public void setBackUserInputIfNeed() throws android.os.RemoteException;
}
