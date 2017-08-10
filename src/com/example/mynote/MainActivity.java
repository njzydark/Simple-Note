package com.example.mynote;

import java.util.List;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;//v4废弃
import android.support.v7.app.AlertDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;


	@TargetApi(23) public class MainActivity extends AppCompatActivity {//使用toolbar并且兼容低版本,activity必须继承AppCompatActivity
	
	private ListView lv;
	private ListView ls;
	private FloatingActionButton floatingActionButton;
	private Toolbar toolbar;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle actionBarDrawerToggle;
	private MyAdapter myAdapter;
	private NoteDao dao;
	private List<NoteBean> noteBeanList;
	private NavigationView mNavigationView;
	private int theme;
	
	// 要申请的权限
	private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
	private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	theme=getIntent().getIntExtra("theme", -1);
    	if(theme!=-1){
    		setTheme(theme);
    	}
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        	  
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                showDialogTipUserRequestPermission();
            }
        }
        
        toolbar();
        drawerLayout();
        floatingActionButton();
        
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        setupDrawerContent(mNavigationView);
        mNavigationView.getMenu().getItem(0).setChecked(true); 
        
        lv=(ListView) findViewById(R.id.lv_main);
        lv.setDivider(null);//去除分割线，以便使用cardview
        
        ls=(ListView) findViewById(R.id.listview);
        
        dao=new NoteDao(this);
        noteBeanList=dao.queryAll();

        myAdapter=new MyAdapter(this, noteBeanList);
        lv.setAdapter(myAdapter);
        //ls.setAdapter(myAdapter);
        ls.setTextFilterEnabled(true);
        lv.setEmptyView(findViewById(R.id.tv_empty));
        lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				NoteBean note=noteBeanList.get(position);
				Intent intent=new Intent(MainActivity.this,RichTextActivity.class);
				String titleData=note.getTitle();
				String contentData=note.getContent();
				String timeData=note.getTime();
				intent.putExtra("title_data", titleData);
				intent.putExtra("content_data", contentData);
				intent.putExtra("time_data", timeData);
				intent.putExtra("role", "modify");
				intent.putExtra("theme", theme);
				startActivity(intent);
			}
		});
        
        lv.setOnItemLongClickListener(new OnItemLongClickListener() {
        	@Override
        	public boolean onItemLongClick(AdapterView<?> parent, View view,
        			final int position, long id) {
        		// TODO Auto-generated method stub
        		final CharSequence[] items = { "收藏", "删除" };
                AlertDialog dlg = new AlertDialog.Builder(MainActivity.this).setTitle("笔记操作").setItems(items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int item) {
                                //这里item是根据选择的方式,
                                //在items数组里面定义了两种方式, 拍照的下标为1所以就调用拍照方法
                                if(item==1){
                                	NoteBean note=noteBeanList.get(position);
                                	String title=note.getTitle();
                                	String content=note.getContent();
                                	String love=note.getLove();
                                	String trash=note.getTrash();
                                	if(trash.equals("0")){
                                		dao.delete(title);
                                		dao.insert(new NoteBean(title, content,null,love,"1"));
                                		onResume();
                                	}else{
                                		dao.delete(title);
                                		onResume2();
                                	}
                                }else if(item==0){
                                	NoteBean note=noteBeanList.get(position);
                                	String title=note.getTitle();
                                	String content=note.getContent();
                                	String love=note.getLove();
                                	String trash=note.getTrash();
                                	dao.delete(title);
                        			dao.insert(new NoteBean(title, content,null,"1",trash));
                                	onResume();
                                }
                            }
                        }).create();
                dlg.show();
        		return true;
        	}
		});

    }

    private void setupDrawerContent(NavigationView navigationView)
    {
        navigationView.setNavigationItemSelectedListener(

                new NavigationView.OnNavigationItemSelectedListener()
                {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item)
                    {
                        switch (item.getItemId()) {  
                        case R.id.nav_home:  
                        	floatingActionButton.setVisibility(View.VISIBLE);
                        	onResume();
                            break;  
                        case R.id.nav_love:
                        	floatingActionButton.setVisibility(View.GONE);
                        	onResume1();
                            break;  
                        case R.id.nav_recycle:  
                        	floatingActionButton.setVisibility(View.GONE);
                        	onResume2();
                            break;
                        case R.id.nav_theme:  
                        	Intent intent = new Intent(MainActivity.this, ChangeTheme.class);
            				startActivity(intent);
            				MainActivity.this.finish();
                            break;
                        case R.id.nav_login:  
                        	Intent intent1 = new Intent(MainActivity.this, UserLoginActivity.class);
            				startActivity(intent1);
            				MainActivity.this.finish();
                            break;
                        case R.id.nav_optipns:  
                            break;
                        case R.id.nav_exit:
                        	finish();
                        	return true;
                        }
                        item.setChecked(false);
                        drawerLayout.closeDrawers();
						return true;
                    }
                });
    }
    
    @Override
    protected void onResume() {//哈哈，开心。找了那么多资料，listview动态刷新搞定！！！
    	// TODO Auto-generated method stub
    	super.onResume();
    	noteBeanList.clear();
    	noteBeanList.addAll(dao.queryAll());
    	myAdapter.notifyDataSetChanged();
    	
    }
    
    protected void onResume1() {//哈哈，开心。找了那么多资料，listview动态刷新搞定！！！
    	// TODO Auto-generated method stub
    	super.onResume();
    	noteBeanList.clear();
    	noteBeanList.addAll(dao.queryLove());
    	myAdapter.notifyDataSetChanged();
    	
    }
    
    protected void onResume2() {//哈哈，开心。找了那么多资料，listview动态刷新搞定！！！
    	// TODO Auto-generated method stub
    	super.onResume();
    	noteBeanList.clear();
    	noteBeanList.addAll(dao.queryTrash());
    	myAdapter.notifyDataSetChanged();
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// TODO Auto-generated method stub
    	getMenuInflater().inflate(R.menu.main, menu);
    	MenuItem menuItem=menu.findItem(R.id.search);
    	SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);//加载searchview
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    ls.setFilterText(newText);
                }else{
                    ls.clearTextFilter();
                }
                return true;
            }
        });
        searchView.setSubmitButtonEnabled(true);//设置是否显示搜索按钮
        searchView.setQueryHint("查找");//设置提示信息
        searchView.setIconifiedByDefault(true);//设置搜索默认为图标
    	return true;
    }
    
    private void toolbar(){
    	toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    private void drawerLayout(){
    	drawerLayout=(DrawerLayout) findViewById(R.id.drawer);
    	
        //ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close)废弃
        actionBarDrawerToggle=new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name,R.string.app_name){
        	@Override
        	public void onDrawerOpened(View drawerView) {
        		// TODO Auto-generated method stub
        		super.onDrawerOpened(drawerView);
        	}
        	@Override
        	public void onDrawerClosed(View drawerView) {
        		// TODO Auto-generated method stub
        		super.onDrawerClosed(drawerView);
        	}
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);//drawerLayout.setDrawerListener(actionBarDrawerToggle)废弃
        actionBarDrawerToggle.syncState();
    }

    private void floatingActionButton(){
    	floatingActionButton=(FloatingActionButton) findViewById(R.id.button_fb);
    	floatingActionButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(MainActivity.this, "fb pressed", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(MainActivity.this, RichTextActivity.class)
				.putExtra("role", "add");
				intent.putExtra("theme", theme);
				startActivity(intent);
			}
		});
    }
    
    
 // 提示用户该请求权限的弹出框
    private void showDialogTipUserRequestPermission() {

        new AlertDialog.Builder(this)
                .setTitle("存储权限不可用")
                .setMessage("简记需要获取存储权限，为您存储图片等信息。\n否则，您将无法正常使用简记。")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRequestPermission();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                    }
                }).setCancelable(false).show();
    }

    // 开始提交请求权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 23);
    }

    // 用户权限 申请 的回调方法
    @TargetApi(23) @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 23) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    } else
                        finish();
                } else {
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 提示用户去应用设置界面手动开启权限

    private void showDialogTipUserGoToAppSettting() {

        dialog = new AlertDialog.Builder(this)
                .setTitle("存储权限不可用")
                .setMessage("请在-应用设置-权限-中，允许支付宝使用存储权限来保存用户数据")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                @Override
                   public void onClick(DialogInterface dialog, int which) {
                       // 跳转到应用设置界面
                       goToAppSetting();
                   }
               })
               .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                   @Override
                  public void onClick(DialogInterface dialog, int which) {
                       finish();
                   }
               }).setCancelable(false).show();
   }

   // 跳转到当前应用的设置界面
   private void goToAppSetting() {
       Intent intent = new Intent();

       intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
       Uri uri = Uri.fromParts("package", getPackageName(), null);
       intent.setData(uri);

       startActivityForResult(intent, 123);
   }
}
