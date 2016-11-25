/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\workplace-web\\ApkAutoTest\\app\\src\\main\\aidl\\com\\tencent\\apk_auto_test\\filelogsave\\IFileLogService.aidl
 */
package com.tencent.apk_auto_test.filelogsave;
public interface IFileLogService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.tencent.apk_auto_test.filelogsave.IFileLogService
{
private static final java.lang.String DESCRIPTOR = "com.tencent.apk_auto_test.filelogsave.IFileLogService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.tencent.apk_auto_test.filelogsave.IFileLogService interface,
 * generating a proxy if needed.
 */
public static com.tencent.apk_auto_test.filelogsave.IFileLogService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.tencent.apk_auto_test.filelogsave.IFileLogService))) {
return ((com.tencent.apk_auto_test.filelogsave.IFileLogService)iin);
}
return new com.tencent.apk_auto_test.filelogsave.IFileLogService.Stub.Proxy(obj);
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
case TRANSACTION_pitchFile:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.pitchFile(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_canExecute:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.canExecute();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_canRead:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.canRead();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_canWrite:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.canWrite();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_createNewFile:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.createNewFile();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_delete:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.delete();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_deleteOnExit:
{
data.enforceInterface(DESCRIPTOR);
this.deleteOnExit();
reply.writeNoException();
return true;
}
case TRANSACTION_exists:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.exists();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getAbsolutePath:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getAbsolutePath();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getCanonicalPath:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getCanonicalPath();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getFreeSpace:
{
data.enforceInterface(DESCRIPTOR);
long _result = this.getFreeSpace();
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_getName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getName();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getParent:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getParent();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getPath:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getPath();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getTotalSpace:
{
data.enforceInterface(DESCRIPTOR);
long _result = this.getTotalSpace();
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_getUsableSpace:
{
data.enforceInterface(DESCRIPTOR);
long _result = this.getUsableSpace();
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_isAbsolute:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isAbsolute();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isDirectory:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isDirectory();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isFile:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isFile();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isHidden:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isHidden();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_lastModified:
{
data.enforceInterface(DESCRIPTOR);
long _result = this.lastModified();
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_length:
{
data.enforceInterface(DESCRIPTOR);
long _result = this.length();
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_list:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String[] _result = this.list();
reply.writeNoException();
reply.writeStringArray(_result);
return true;
}
case TRANSACTION_mkdir:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.mkdir();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_mkdirs:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.mkdirs();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setExecutable:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _result = this.setExecutable(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setExecutable2:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _arg1;
_arg1 = (0!=data.readInt());
boolean _result = this.setExecutable2(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setLastModified:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
boolean _result = this.setLastModified(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setReadOnly:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.setReadOnly();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setReadable:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _result = this.setReadable(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setReadable2:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _arg1;
_arg1 = (0!=data.readInt());
boolean _result = this.setReadable2(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setWritable:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _result = this.setWritable(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setWritable2:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _arg1;
_arg1 = (0!=data.readInt());
boolean _result = this.setWritable2(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_LogSave:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
this.LogSave(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_LogRead:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _result = this.LogRead(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setDirctory:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setDirctory(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_writeExcel:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
java.lang.String _arg3;
_arg3 = data.readString();
this.writeExcel(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_clearFolder:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.clearFolder(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.tencent.apk_auto_test.filelogsave.IFileLogService
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
@Override public void pitchFile(java.lang.String filePath) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(filePath);
mRemote.transact(Stub.TRANSACTION_pitchFile, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean canExecute() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_canExecute, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean canRead() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_canRead, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean canWrite() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_canWrite, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean createNewFile() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_createNewFile, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean delete() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_delete, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void deleteOnExit() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_deleteOnExit, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean exists() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_exists, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String getAbsolutePath() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAbsolutePath, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String getCanonicalPath() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCanonicalPath, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public long getFreeSpace() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getFreeSpace, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String getName() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getName, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String getParent() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getParent, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String getPath() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPath, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public long getTotalSpace() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getTotalSpace, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public long getUsableSpace() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getUsableSpace, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean isAbsolute() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isAbsolute, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean isDirectory() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isDirectory, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean isFile() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isFile, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean isHidden() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isHidden, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public long lastModified() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_lastModified, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public long length() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_length, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String[] list() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_list, _data, _reply, 0);
_reply.readException();
_result = _reply.createStringArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean mkdir() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_mkdir, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean mkdirs() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_mkdirs, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean setExecutable(boolean executable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((executable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setExecutable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean setExecutable2(boolean executable, boolean ownerOnly) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((executable)?(1):(0)));
_data.writeInt(((ownerOnly)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setExecutable2, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean setLastModified(long time) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(time);
mRemote.transact(Stub.TRANSACTION_setLastModified, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean setReadOnly() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setReadOnly, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean setReadable(boolean readable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((readable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setReadable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean setReadable2(boolean readable, boolean ownerOnly) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((readable)?(1):(0)));
_data.writeInt(((ownerOnly)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setReadable2, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean setWritable(boolean writable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((writable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setWritable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean setWritable2(boolean writable, boolean ownerOnly) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((writable)?(1):(0)));
_data.writeInt(((ownerOnly)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setWritable2, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void LogSave(java.lang.String msg, java.lang.String name, java.lang.String logSaveFolderName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(msg);
_data.writeString(name);
_data.writeString(logSaveFolderName);
mRemote.transact(Stub.TRANSACTION_LogSave, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String LogRead(java.lang.String name) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(name);
mRemote.transact(Stub.TRANSACTION_LogRead, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setDirctory(java.lang.String dir) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(dir);
mRemote.transact(Stub.TRANSACTION_setDirctory, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void writeExcel(java.lang.String msg, int row, int column, java.lang.String fileName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(msg);
_data.writeInt(row);
_data.writeInt(column);
_data.writeString(fileName);
mRemote.transact(Stub.TRANSACTION_writeExcel, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void clearFolder(java.lang.String dir) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(dir);
mRemote.transact(Stub.TRANSACTION_clearFolder, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_pitchFile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_canExecute = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_canRead = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_canWrite = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_createNewFile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_delete = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_deleteOnExit = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_exists = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_getAbsolutePath = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_getCanonicalPath = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_getFreeSpace = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_getName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_getParent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_getPath = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_getTotalSpace = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_getUsableSpace = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_isAbsolute = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_isDirectory = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_isFile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_isHidden = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_lastModified = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
static final int TRANSACTION_length = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
static final int TRANSACTION_list = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
static final int TRANSACTION_mkdir = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
static final int TRANSACTION_mkdirs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
static final int TRANSACTION_setExecutable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
static final int TRANSACTION_setExecutable2 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
static final int TRANSACTION_setLastModified = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);
static final int TRANSACTION_setReadOnly = (android.os.IBinder.FIRST_CALL_TRANSACTION + 28);
static final int TRANSACTION_setReadable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 29);
static final int TRANSACTION_setReadable2 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 30);
static final int TRANSACTION_setWritable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 31);
static final int TRANSACTION_setWritable2 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 32);
static final int TRANSACTION_LogSave = (android.os.IBinder.FIRST_CALL_TRANSACTION + 33);
static final int TRANSACTION_LogRead = (android.os.IBinder.FIRST_CALL_TRANSACTION + 34);
static final int TRANSACTION_setDirctory = (android.os.IBinder.FIRST_CALL_TRANSACTION + 35);
static final int TRANSACTION_writeExcel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 36);
static final int TRANSACTION_clearFolder = (android.os.IBinder.FIRST_CALL_TRANSACTION + 37);
}
public void pitchFile(java.lang.String filePath) throws android.os.RemoteException;
public boolean canExecute() throws android.os.RemoteException;
public boolean canRead() throws android.os.RemoteException;
public boolean canWrite() throws android.os.RemoteException;
public boolean createNewFile() throws android.os.RemoteException;
public boolean delete() throws android.os.RemoteException;
public void deleteOnExit() throws android.os.RemoteException;
public boolean exists() throws android.os.RemoteException;
public java.lang.String getAbsolutePath() throws android.os.RemoteException;
public java.lang.String getCanonicalPath() throws android.os.RemoteException;
public long getFreeSpace() throws android.os.RemoteException;
public java.lang.String getName() throws android.os.RemoteException;
public java.lang.String getParent() throws android.os.RemoteException;
public java.lang.String getPath() throws android.os.RemoteException;
public long getTotalSpace() throws android.os.RemoteException;
public long getUsableSpace() throws android.os.RemoteException;
public boolean isAbsolute() throws android.os.RemoteException;
public boolean isDirectory() throws android.os.RemoteException;
public boolean isFile() throws android.os.RemoteException;
public boolean isHidden() throws android.os.RemoteException;
public long lastModified() throws android.os.RemoteException;
public long length() throws android.os.RemoteException;
public java.lang.String[] list() throws android.os.RemoteException;
public boolean mkdir() throws android.os.RemoteException;
public boolean mkdirs() throws android.os.RemoteException;
public boolean setExecutable(boolean executable) throws android.os.RemoteException;
public boolean setExecutable2(boolean executable, boolean ownerOnly) throws android.os.RemoteException;
public boolean setLastModified(long time) throws android.os.RemoteException;
public boolean setReadOnly() throws android.os.RemoteException;
public boolean setReadable(boolean readable) throws android.os.RemoteException;
public boolean setReadable2(boolean readable, boolean ownerOnly) throws android.os.RemoteException;
public boolean setWritable(boolean writable) throws android.os.RemoteException;
public boolean setWritable2(boolean writable, boolean ownerOnly) throws android.os.RemoteException;
public void LogSave(java.lang.String msg, java.lang.String name, java.lang.String logSaveFolderName) throws android.os.RemoteException;
public java.lang.String LogRead(java.lang.String name) throws android.os.RemoteException;
public void setDirctory(java.lang.String dir) throws android.os.RemoteException;
public void writeExcel(java.lang.String msg, int row, int column, java.lang.String fileName) throws android.os.RemoteException;
public void clearFolder(java.lang.String dir) throws android.os.RemoteException;
}
