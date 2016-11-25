package com.tencent.apk_auto_test.filelogsave;
interface IFileLogService
{	
	void pitchFile(String filePath);
	boolean	 canExecute();
	boolean	 canRead();
	boolean	 canWrite();
	boolean	 createNewFile();
	boolean	 delete();
	void	 deleteOnExit();
	boolean	 exists();
	String	 getAbsolutePath();
	String	 getCanonicalPath();
	long	 getFreeSpace();
	String	 getName();
	String	 getParent();
	String	 getPath();
	long	 getTotalSpace();
	long	 getUsableSpace();
	boolean	 isAbsolute();
	boolean	 isDirectory();
	boolean	 isFile();
	boolean	 isHidden();
	long	 lastModified();
	long	 length();
	String[]	 list();
	boolean	 mkdir();
	boolean	 mkdirs();
	boolean	 setExecutable(boolean executable);
	boolean	 setExecutable2(boolean executable, boolean ownerOnly);
	boolean	 setLastModified(long time);
	boolean	 setReadOnly();
	boolean	 setReadable(boolean readable);
	boolean	 setReadable2(boolean readable, boolean ownerOnly);
	boolean	 setWritable(boolean writable);
	boolean	 setWritable2(boolean writable, boolean ownerOnly);
	
	void LogSave(String msg, String name, String logSaveFolderName);
	String LogRead(String name);
	
	void setDirctory(String dir);
	void writeExcel(String msg, int row, int column,String fileName);
	
	void clearFolder(String dir);
}