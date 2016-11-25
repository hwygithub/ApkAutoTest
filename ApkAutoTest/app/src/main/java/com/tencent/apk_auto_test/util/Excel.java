package com.tencent.apk_auto_test.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import android.os.Environment;

public final class Excel {

	private static final String captureFilePath = "/sdcard/OppoResourceTest/";
	public static String DIRCTORY = "/MpBatteryAutoTest";
	public static final String OutFilePath = Environment.getExternalStorageDirectory() + DIRCTORY;
	private static final String TAG = "Clock_Test";
	private static String ExcelDate = null;
	private static String ExcelArray = null;
	private static String[] ExcelEncode = new String[2000];
	private static HashMap<String, Object> map = new HashMap<String, Object>();

	/**
	 * read the excel state
	 * 
	 * @param filename
	 *            file name
	 * @return
	 */
	public final static String readStateFromFile(String filename) {
		StringBuffer sb = new StringBuffer();
		StringBuilder sb1 = new StringBuilder("");
		File file = new File(captureFilePath + filename);
		StringBuffer out = new StringBuffer();
		if (!file.exists()) {
			return "0";
		}
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis, "GB2312"));
			String line = null;
			char[] b = new char[4096];
			for (int n; (n = br.read(b)) != -1;) {
				out.append(new String(b, 0, n));
			}
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toString();
	}

	/**
	 * read excel
	 * 
	 * @param name
	 * @param index
	 * @return
	 * @throws FileNotFoundException
	 */

	public final static String readExcel(String name, int index) throws FileNotFoundException {
		// Log.d(TAG, "readExcel");
		StringBuilder sb = new StringBuilder();

		try {

			FileInputStream inputStream = new FileInputStream(captureFilePath + name);
			// Log.d(TAG, name+"apptest.xlsx");
			POIFSFileSystem fSystem = new POIFSFileSystem(inputStream);
			HSSFWorkbook wb1 = new HSSFWorkbook(fSystem);
			HSSFSheet sheet = wb1.getSheetAt(0);
			// Iterator锟斤拷锟斤拷锟�
			Iterator<?> rowIterator = sheet.rowIterator();
			while (rowIterator.hasNext()) {
				HSSFRow row = (HSSFRow) rowIterator.next();
				// Log.e(TAG,"row number "+row.getRowNum());

				Iterator<?> cells = row.cellIterator();
				while (cells.hasNext()) {
					HSSFCell cell = (HSSFCell) cells.next();
					if (cell.getCellNum() == index) {
						switch (cell.getCellType()) {
						case HSSFCell.CELL_TYPE_NUMERIC:
							// Log.e(TAG,cell.getNumericCellValue()+"");
							// put(cell.getNumericCellValue()+"  "+"\n","Assert_Log");
							sb.append(cell.getNumericCellValue() + "//");
							break;
						case HSSFCell.CELL_TYPE_STRING:
							// Log.e(TAG,cell.getStringCellValue());
							// put(cell.getStringCellValue()+"  "+"\n","Assert_Log");
							sb.append(cell.getStringCellValue() + "//");
							break;
						case HSSFCell.CELL_TYPE_BOOLEAN:
							// Log.e(TAG,cell.getBooleanCellValue()+"");
							// put(cell.getBooleanCellValue()+"  "+"\n","Assert_Log");
							sb.append(cell.getBooleanCellValue() + "//");
							break;
						case HSSFCell.CELL_TYPE_FORMULA:
							// Log.e(TAG,cell.getCellFormula());
							// put(cell.getCellFormula()+"  "+"\n","Assert_Log");
							sb.append(cell.getCellFormula() + "//");
							break;
						default:
							System.out.println("unsupported cell type");
							break;
						}
					}
				}
			}
		} catch (Exception e) {
		}
		return sb.toString();

	}

	/**
	 * write log to excel
	 * 
	 * @param title
	 * @param result
	 * @param start
	 * @param end
	 * @throws Exception
	 */
	public static void WriteToExcel(String result, int row, int column, String filename) {

		try {

			File path = new File(OutFilePath + File.separator);
			File file = new File(OutFilePath + File.separator + filename);
			if (!path.exists()) {
				path.mkdirs();
			}

			if (!file.exists()) {
				try {
					HSSFWorkbook ws = new HSSFWorkbook();//
					HSSFSheet sheets = ws.createSheet();//
					FileOutputStream fileOut = new FileOutputStream(file);//
					ws.write(fileOut);//
					fileOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			FileInputStream inputStream = new FileInputStream(OutFilePath + File.separator + filename);
			// Log.d(TAG, "Area_Assert.xls");
			POIFSFileSystem fSystem = new POIFSFileSystem(inputStream);
			HSSFWorkbook wb1 = new HSSFWorkbook(fSystem);
			HSSFSheet sheet = wb1.getSheetAt(0);
			inputStream.close();

			HSSFRow newRow = sheet.getRow(row);
			if (newRow == null) {
				newRow = sheet.createRow(row);
			}
			// Log.d(TAG, "begin"+newRow);
			@SuppressWarnings("deprecation")
			HSSFCell newCell = newRow.createCell(column);
			newCell.setCellValue(result);
			// Log.d(TAG, "next"+newCell);
			FileOutputStream fos = new FileOutputStream(OutFilePath + File.separator + filename);
			wb1.write(fos);
			fos.close();
		} catch (Exception e) {
		}
	}
}
