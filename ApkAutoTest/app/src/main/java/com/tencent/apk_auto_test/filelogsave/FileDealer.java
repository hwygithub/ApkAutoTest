package com.tencent.apk_auto_test.filelogsave;

import com.tencent.apk_auto_test.filelogsave.IFileLogService;
import com.tencent.apk_auto_test.receiver.BatteryReceiver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class FileDealer {
	Context mContext;

	private String dealFilePath = "";
	protected String TAG = "FileDealer";
	private IFileLogService fileLogService = null;
	private static FileDealer mFileDealer;

	private BatteryReceiver mBatteryReceiver;

	ServiceConnection myServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "myServiceConnection  connected");
			fileLogService = IFileLogService.Stub.asInterface(service);
			try {
				fileLogService.pitchFile(dealFilePath);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			try {
				fileLogService.setDirctory(dealFilePath);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			if (mBatteryReceiver != null) {
				mBatteryReceiver.recordHeader();
			}

			init();
		}

		public void onServiceDisconnected(ComponentName name) {
			fileLogService = null;
		}
	};

	public void init() {

	}

	public FileDealer(Context context, String filePath) {
		mContext = context;
		dealFilePath = filePath;
		bindService();
		mFileDealer = this;
	}

	public FileDealer(Context context, String filePath,
			BatteryReceiver batteryReceiver) {
		mContext = context;
		dealFilePath = filePath;
		mBatteryReceiver = batteryReceiver;
		bindService();
		mFileDealer = this;
	}

	public FileDealer(Context context) {
		mContext = context;
		bindService();
		mFileDealer = this;
	}

	public static FileDealer getInstance(Context mContext) {
		if (mFileDealer == null) {
			Log.e("FileDealer", "getInstance null");
			mFileDealer = new FileDealer(mContext);
		} else {
			Log.e("FileDealer", "getInstance exits");
		}
		return mFileDealer;
	}

	public static FileDealer getInstance(Context mContext, String path) {
		if (mFileDealer == null) {
			Log.e("FileDealer", "getInstance null");
			mFileDealer = new FileDealer(mContext, path);
		} else {
			Log.e("FileDealer", "getInstance exits");
			mFileDealer.pitchFile(path);
			mFileDealer.setDirctory(path);
		}
		return mFileDealer;
	}

	public static FileDealer getInstance(Context mContext, String path,
			BatteryReceiver batteryReceiver) {
		if (mFileDealer == null) {
			Log.e("FileDealer", "getInstance null");
			mFileDealer = new FileDealer(mContext, path, batteryReceiver);
		} else {
			Log.e("FileDealer", "getInstance exits");
			mFileDealer.pitchFile(path);
			mFileDealer.setDirctory(path);
			batteryReceiver.mDealer = mFileDealer;
			batteryReceiver.recordHeader();
		}
		return mFileDealer;
	}

	public void bindService() {
		if (null == fileLogService) {
			mContext.bindService(new Intent().setComponent(new ComponentName(
					"com.apptest.filelogsave",
					"com.apptest.filelogsave.FileLogService")),
					myServiceConnection, Context.BIND_AUTO_CREATE);
		}
	}

	public void unbindService() {
		mContext.unbindService(myServiceConnection);
	}

	public void pitchFile(String path) {
		if (fileLogService != null) {
			try {
				fileLogService.pitchFile(path);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
			dealFilePath = path;
			bindService();
		}
	}

	public void LogSave(String s, String name, String logSaveFolderPath) {
		if (null == fileLogService) {
			Log.w(TAG, "ServiceConnection  is not connected");
		}
		try {
			fileLogService.LogSave(s, name, logSaveFolderPath);
			Log.e(TAG, "logSave");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public String LogRead(String name) {
		if (null == fileLogService) {
			Log.w(TAG, "ServiceConnection  is not connected");
		}
		try {
			return fileLogService.LogRead(name);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean canExecute() {
		try {
			return fileLogService.canExecute();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean canRead() {
		try {
			return fileLogService.canRead();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean canWrite() {
		try {
			return fileLogService.canWrite();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean createNewFile() {
		try {
			fileLogService.createNewFile();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean delete() {
		try {
			return fileLogService.delete();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void deleteOnExit() {
		try {
			fileLogService.deleteOnExit();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public boolean exists() {
		try {
			return fileLogService.exists();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getAbsolutePath() {
		try {
			return fileLogService.getAbsolutePath();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getCanonicalPath() {
		try {
			return fileLogService.getCanonicalPath();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	public long getFreeSpace() {
		try {
			return fileLogService.getFreeSpace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public String getName() {
		try {
			return fileLogService.getName();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getParent() {
		try {
			return fileLogService.getParent();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getPath() {
		try {
			return fileLogService.getPath();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	public long getTotalSpace() {
		try {
			return fileLogService.getTotalSpace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public long getUsableSpace() {
		try {
			return fileLogService.getUsableSpace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public boolean isAbsolute() {
		try {
			return fileLogService.isAbsolute();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isDirectory() {
		try {
			return fileLogService.isDirectory();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isFile() {
		try {
			return fileLogService.isFile();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isHidden() {
		try {
			return fileLogService.isHidden();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public long lastModified() {
		try {
			return fileLogService.lastModified();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public long length() {
		try {
			return fileLogService.length();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public String[] list() {
		try {
			return fileLogService.list();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean mkdir() {
		try {
			return fileLogService.mkdir();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean mkdirs() {
		try {
			return fileLogService.mkdirs();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean setExecutable(boolean executable) {
		try {
			return fileLogService.setExecutable(executable);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean setExecutable2(boolean executable, boolean ownerOnly) {
		try {
			return fileLogService.setExecutable2(executable, ownerOnly);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean setLastModified(long time) {
		try {
			return fileLogService.setLastModified(time);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean setReadOnly() {
		try {
			return fileLogService.setReadOnly();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean setReadable(boolean readable) {
		try {
			return fileLogService.setReadable(readable);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean setReadable2(boolean readable, boolean ownerOnly) {
		try {
			return fileLogService.setReadable2(readable, ownerOnly);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean setWritable(boolean writable) {
		try {
			return fileLogService.setWritable(writable);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean setWritable2(boolean writable, boolean ownerOnly) {
		try {
			return fileLogService.setWritable2(writable, ownerOnly);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void writeExcel(String msg, int row, int column, String fileName) {
		try {
			fileLogService.writeExcel(msg, row, column, fileName);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void setDirctory(String dir) {
		try {
			fileLogService.setDirctory(dir);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void clearFolder(String dir) {
		try {
			fileLogService.clearFolder(dir);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
