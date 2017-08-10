package com.example.mynote;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

@SuppressLint("SimpleDateFormat") 
public class RichTextActivity extends AppCompatActivity implements OnClickListener {
	private final int REQUEST_CODE_CAPTURE_CAMEIA = 100;//100
	private final int REQUEST_CODE_PICK_IMAGE = 200;//200
	private Context context;
	private LinearLayout line_rootView, line_addImg;
	private InterceptLinearLayout line_intercept;
	private RichTextEditor richText;
	private EditText et_name;
	private boolean isKeyBoardUp, isEditTouch;// 判断软键盘的显示与隐藏
	private File mCameraImageFile;// 照相机拍照得到的图片
	private FileUtils mFileUtils;
	private String ROLE = "add";// 当前页面是新增还是查看详情 add/modify
	private NoteDao dao;
	
	private final String IMAGE_SRC_REGEX = "<img[^<>]*?\\ssrc=['\"]?(.*?)['\"].*?>";
	private String mContent;
	private SparseArray<String> mImageArray;
	private Toolbar toolbar;
	@SuppressWarnings("unused")
	private View item;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		int theme=getIntent().getIntExtra("theme", -1);
    	if(theme!=-1){
    		setTheme(theme);
    	}
		super.onCreate(savedInstanceState);
		
		//解决android7打开相机闪退
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//		    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//		    StrictMode.setVmPolicy(builder.build());
//		}
		
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		setContentView(R.layout.richtext);
		
		toolbar();
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
		
		item = findViewById(R.id.edit);
		context = this;
		dao=new NoteDao(this);
		
		init();
		
	}
	

	private void init() {
		if (getIntent() != null)
			ROLE = getIntent().getStringExtra("role");

		mFileUtils = new FileUtils(context);

		line_addImg = (LinearLayout) findViewById(R.id.line_addImg);
		line_rootView = (LinearLayout) findViewById(R.id.line_rootView);
		line_intercept = (InterceptLinearLayout) findViewById(R.id.line_intercept);
		et_name = (EditText) findViewById(R.id.et_name);
		richText = (RichTextEditor) findViewById(R.id.richText);
		initRichEdit();
		if ("modify".equals(ROLE)) {
			toolbar.setTitle("笔记详情");
			line_intercept.setIntercept(true);
			richText.setIntercept(true);
			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
							| WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			getData();
		}
	}
	
	
	private void toolbar(){
    	toolbar = (Toolbar) findViewById(R.id.addtoolbar);
    	toolbar.setTitle("新建笔记");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

	private void initRichEdit() {
		ImageView img_addPicture, img_takePicture;
		img_addPicture = (ImageView) line_addImg
				.findViewById(R.id.img_addPicture);
		img_addPicture.setOnClickListener(this);
		img_takePicture = (ImageView) line_addImg
				.findViewById(R.id.img_takePicture);
		img_takePicture.setOnClickListener(this);

		et_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					isEditTouch = false;
					line_addImg.setVisibility(View.GONE);
				}
			}

		});
		richText.setLayoutClickListener(new RichTextEditor.LayoutClickListener() {
			@Override
			public void layoutClick() {
				isEditTouch = true;
				line_addImg.setVisibility(View.VISIBLE);
			}
		});

		line_rootView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						int heightDiff = line_rootView.getRootView()
								.getHeight() - line_rootView.getHeight();
						if (isEditTouch) {
							if (heightDiff > 500) {// 大小超过500时，一般为显示虚拟键盘事件,此判断条件不唯一
								isKeyBoardUp = true;
								line_addImg.setVisibility(View.VISIBLE);
							} else {
								if (isKeyBoardUp) {
									isKeyBoardUp = false;
									isEditTouch = false;
									line_addImg.setVisibility(View.GONE);
								}
							}
						}
					}
				});
	}

	private void getData() {
		Intent intent=getIntent();//使用intent获取上一个活动传过来的值
		final String titleData=intent.getStringExtra("title_data");
		final String contentData=intent.getStringExtra("content_data");
		et_name.setText(titleData);
		et_name.setSelection(titleData.length());//光标位置放置末端
		et_name.setCursorVisible(false);
		showEditData(contentData);
	}
	
	protected void showEditData(String content) {
        //richText.clearAllLayout();
        List<String> textList = StringUtils.cutStringByImgTag(content);
        for (int i = 0; i < textList.size(); i++) {
            String text = textList.get(i);
            if (text.contains("<img")) {
                String imagePath = StringUtils.getImgSrc(text);
                int width = ScreenUtils.getScreenWidth(this);
                int height = ScreenUtils.getScreenHeight(this);
                richText.measure(0,0);
                Bitmap bitmap = ImageFileUtils.getSmallBitmap(imagePath, width, height);
                if (bitmap != null){
                    richText.addImageViewAtIndex(richText.getLastIndex(), bitmap, imagePath);
                } else {
                    richText.addEditTextAtIndex(richText.getLastIndex(), text);
                }
            } else {
                richText.addEditTextAtIndex(richText.getLastIndex(), text);
            }
        }
    }

	private void openCamera() {
		try {
			File PHOTO_DIR = new File(mFileUtils.getStorageDirectory());
			if (!PHOTO_DIR.exists())
				PHOTO_DIR.mkdirs();// 创建照片的存储目录*/

			mCameraImageFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
			final Intent intent = getTakePickIntent(mCameraImageFile);
			startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMEIA);
		} catch (ActivityNotFoundException e) {
		}
	}

	public static Intent getTakePickIntent(File f) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	/**
	 * 用当前时间给取得的图片命名
	 */
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyy_MM_dd_HH_mm_ss");
		return dateFormat.format(date) + ".jpg";
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
		if("新建笔记".equals(toolbar.getTitle())){
			getMenuInflater().inflate(R.menu.noteadd, menu);
		}
		if("笔记详情".equals(toolbar.getTitle())){
			getMenuInflater().inflate(R.menu.noteedit, menu);
		}
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.edit) {
        	if("保存".equals(item.getTitle())){
        		String title=et_name.getText().toString();
    			List<EditData> editList =richText.buildEditData();
    			String content = dealEditData(editList);
    			dao.delete(title);
    			dao.insert(new NoteBean(title, content,null,"0","0"));
    			RichTextActivity.this.finish();
        	} 
        	//如果输入法在窗口上已经显示，则隐藏，反之则显示
        	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
        	imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
        	
        	line_intercept.setIntercept(false);
			richText.setIntercept(false);
        	toolbar.setTitle("修改笔记");
        	item.setTitle("保存");
        	et_name.setCursorVisible(true);
        	
        }
        if(id==R.id.save){
        	String title=et_name.getText().toString();
			List<EditData> editList =richText.buildEditData();
			String content = dealEditData(editList);
			dao.delete(title);
			dao.insert(new NoteBean(title, content,null,"0","0"));
			RichTextActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_CODE_PICK_IMAGE) {
			Uri uri = data.getData();
			richText.insertImage(mFileUtils.getFilePathFromUri(uri));
		} else if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
			richText.insertImage(mCameraImageFile.getAbsolutePath());
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mFileUtils.deleteRichTextImage();
	}

	
	
	private String dealEditData(List<EditData> editList) {
		String data = "";
		for (EditData itemData : editList) {
			if (itemData.inputStr != null) {
				data += itemData.inputStr;
			} else if (itemData.imagePath != null) {
				data += itemData.imagePath;
			}
		}
		return data;
	}

	public void createShowView(String content) {
		mContent = content;
		mImageArray = new SparseArray<String>();
		Matcher m = Pattern.compile(IMAGE_SRC_REGEX).matcher(mContent);
		while (m.find()) {
			mImageArray.append(mContent.indexOf("<img"), m.group(1));
			mContent = mContent.replaceFirst("<img[^>]*>", "");
		}
		if (mImageArray.size() == 0) {
			richText.insertText(mContent);
		} else {
			for (int i = 0; i < mImageArray.size(); i++) {
				String s;
				if (i == 0 && (mImageArray.size() - 1 == 0)) {
					s = mContent.substring(0, mImageArray.keyAt(i));
					richText.insertText(s);
					richText.insertImage(mImageArray.valueAt(i));
					s = mContent.substring(mImageArray.keyAt(i), mContent.length());
					richText.insertText(s);
				} else if (i == 0) {
					s = mContent.substring(0, mImageArray.keyAt(i));
					richText.insertText(s);
					richText.insertImage(mImageArray.valueAt(i));
				} else if (i == mImageArray.size() - 1) {
					s = mContent.substring(mImageArray.keyAt(i - 1), mImageArray.keyAt(i));
					richText.insertText(s);
					s = mContent.substring(mImageArray.keyAt(i), mContent.length());
					richText.insertImage(mImageArray.valueAt(i));
					richText.insertText(s);
				} else {
					s = mContent.substring(mImageArray.keyAt(i - 1), mImageArray.keyAt(i));
					richText.insertText(s);
					richText.insertImage(mImageArray.valueAt(i));
				}
			}
		}
	}
	
	public static Uri getImageContentUri(Context context, String imageFile) {
        String filePath = imageFile;
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
                return null;
        }
    }
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_addPicture:
			// 打开系统相册
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType("image/*");// 相片类型
			startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
			break;
		case R.id.img_takePicture:
			// 打开相机
			openCamera();
			break;

		}
	}
	
	

}
