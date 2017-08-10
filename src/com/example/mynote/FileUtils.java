package com.example.mynote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class FileUtils {
	private Context mContext;
	private String TAG = "FileUtils";
	/**
	 * sd卡的根目录
	 */
	private static String mSdRootPath = Environment
			.getExternalStorageDirectory().getPath();
	/**
	 * 手机的缓存根目录
	 */
	private static String mDataRootPath = null;

	/**
	 * 保存Image的目录名
	 */
	private final static String FOLDER_NAME = "/AndroidImage";

	/**
	 * 图文详情图片缓存
	 */
	private final static String IMAGE_RICHTEXT = "/RichTextImage";

	public FileUtils(Context context) {
		mContext = context;
		mDataRootPath = context.getCacheDir().getPath();
	}

	/**
	 * 获取手机根目录，若有SD卡就是SD卡根目录，否则为手机缓存目录
	 * 
	 * @return
	 */
	public String getRootDirectory() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED) ? mSdRootPath : mDataRootPath;
	}

	/**
	 * 获取储存Image的目录
	 * 
	 * @return
	 */
	public String getStorageDirectory() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED) ? mSdRootPath + FOLDER_NAME
				: mDataRootPath + FOLDER_NAME;
	}

	public String getRichTextImageDirectory() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED) ? mSdRootPath + IMAGE_RICHTEXT
				: mDataRootPath + IMAGE_RICHTEXT;
	}

	/**
	 * @param fileName
	 * @param bitmap
	 * @return 保存的图片的路径
	 */
	public String savaRichTextImage(String fileName, Bitmap bitmap) {
		fileName = fileName.replaceAll("[^\\w]", "");
		if (bitmap == null) {
			return null;
		}
		String result = null;
		String path = getRichTextImageDirectory();
		File folderFile = new File(path);
		if (!folderFile.exists()) {
			folderFile.mkdirs();
		}
		File file = new File(path + File.separator + fileName + ".jpg");
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
			result = file.getAbsolutePath();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.i(TAG, "savaImage() has FileNotFoundException");
		} catch (IOException e) {
			e.printStackTrace();
			Log.i(TAG, "savaImage() has IOException");
		}

		return result;
	}

	public void deleteRichTextImage() {
		String path = getRichTextImageDirectory();
		File f = new File(path);
		deleteFile(f);
	}

	public void deleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return;
			}
			for (File f : childFile) {
				deleteFile(f);
			}
			file.delete();
		}
	}

	/**
	 * 根据Uri获取图片文件的绝对路径
	 */
	public  String getFilePathFromUri(final Uri uri) {
		if (uri==null) {
			return null;
		}

		final String scheme = uri.getScheme();
		String data = null;
		if (scheme == null) {
			data = uri.getPath();
		} else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
			data = uri.getPath();
		} else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
			Cursor cursor = mContext.getContentResolver().query(uri,
					new String[] { MediaStore.Images.ImageColumns.DATA }, null,
					null, null);
			if (null != cursor) {
				if (cursor.moveToFirst()) {
					int index = cursor
							.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
					if (index > -1) {
						data = cursor.getString(index);
					}
				}
				cursor.close();
			}
		}
		return data;
	}

}
