package com.example.mynote;

import android.app.Activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;


public class UserLoginActivity extends Activity {
	
	private EditText username;
	private EditText password;
	private Button btn_login;
	private TextView btn_register;
	
	private NoteDao dao;

	private String urlstr="http://10.0.2.2/SimpleCloudNote/appLoginAction.php";//服务器php接口地址
	private String urlstrs="http://10.0.2.2/SimpleCloudNote/appNotesSyn.php";//服务器php接口地址
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);

		btn_login = (Button) findViewById(R.id.btn_login);
		btn_register = (TextView) findViewById(R.id.btn_register);
		
		dao=new NoteDao(this);
		
		btn_login.setOnClickListener(new View.OnClickListener() {  
			  
		    @Override  
		    public void onClick(View v) {//登陆按钮监听事件  
		        new Thread(new Runnable() {  
		            @Override  
		            public void run() {  
		                try {  
		                    int result = login();  
		                    //login()为向php服务器提交请求的函数，返回数据类型为int  
		                    if (result == 1) {  
		                        Log.e("log_tag", "登陆成功！");  
		                        //Toast toast=null;  
		                        Looper.prepare();  
		                        Toast.makeText(UserLoginActivity.this, "登陆成功！", Toast.LENGTH_SHORT).show();  
//		                        Looper.loop();
		                        noteSyn();//笔记同步函数
		                        Intent intent=new Intent(UserLoginActivity.this,MainActivity.class);//成功后进入主界面
		                        startActivity(intent);
		                        UserLoginActivity.this.finish();
		                        Looper.loop();
		                    } else if (result == -2) {  
		                        Log.e("log_tag", "密码错误！");  
		                        //Toast toast=null;  
		                        Looper.prepare();  
		                        Toast.makeText(UserLoginActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();  
		                        Looper.loop();  
		                    } else if (result == -1) {  
		                        Log.e("log_tag", "不存在该用户！");  
		                        //Toast toast=null;  
		                        Looper.prepare();  
		                        Toast.makeText(UserLoginActivity.this, "不存在该用户！", Toast.LENGTH_SHORT).show();  
		                        Looper.loop();  
		                    }  
		                } catch (IOException e) {  
		                    System.out.println(e.getMessage());  
		                }  
		            }  
		        }).start();  
		    }  
		});
		
		btn_register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(UserLoginActivity.this, UserRegisterActivity.class);
				startActivity(intent);
			}
		});
    }
    
    private int login() throws IOException {  
        int returnResult=0;  
        /*获取用户名和密码*/  
        String user_name=username.getText().toString();  
        String user_password=password.getText().toString();  
        if(user_name==null||user_name.length()<=0){  
            Looper.prepare();  
            Toast.makeText(UserLoginActivity.this,"请输入账号", Toast.LENGTH_LONG).show();  
            Looper.loop();  
            return 0;  
      
        }  
        if(user_password==null||user_password.length()<=0){  
            Looper.prepare();  
            Toast.makeText(UserLoginActivity.this,"请输入密码", Toast.LENGTH_LONG).show();  
            Looper.loop();  
            return 0;  
        }  
//        String urlstr="http://10.0.2.2/SimpleCloudNote/appLoginAction.php";//服务器php接口地址
        //建立网络连接  
        URL url = new URL(urlstr);  
        HttpURLConnection http= (HttpURLConnection) url.openConnection();  
        //POST方法传值  
        String params="username="+user_name+'&'+"userpwd="+user_password;  
        http.setDoOutput(true);  
        http.setRequestMethod("POST");  
        OutputStream out=http.getOutputStream();  
        out.write(params.getBytes());//post提交参数  
        out.flush();  
        out.close();  
      
        //读取网页返回的数据  
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(http.getInputStream()));//获得输入流  
        String line="";  
        StringBuilder sb=new StringBuilder();//建立输入缓冲区  
        while (null!=(line=bufferedReader.readLine())){//结束会读入一个null值  
            sb.append(line);//写缓冲区  
        }  
        String result= sb.toString();//返回结果  
      
        try {  
            /*获取服务器返回的JSON数据*/  
            JSONObject jsonObject= new JSONObject(result);  
            returnResult=jsonObject.getInt("status");//获取JSON数据中status字段值  
        } catch (Exception e) {  
            // TODO: handle exception  
            Log.e("log_tag", "the Error parsing data "+e.toString());  
        }  
        return returnResult;  
    }
    private void noteSyn() throws IOException{
    	String user_name=username.getText().toString();
//    	String urlstr="http://10.0.2.2/SimpleCloudNote/appNotesSyn.php";//服务器php接口地址
        //建立网络连接  
        URL url = new URL(urlstrs);  
        HttpURLConnection http= (HttpURLConnection) url.openConnection(); 
        
      //POST方法传值  
        String params="username="+user_name;  
        http.setDoOutput(true);  
        http.setRequestMethod("POST");  
        OutputStream out=http.getOutputStream();  
        out.write(params.getBytes());//post提交参数  
        out.flush();  
        out.close();
        
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(http.getInputStream()));//获得输入流  
        String line="";  
        StringBuilder sb=new StringBuilder();//建立输入缓冲区  
        while (null!=(line=bufferedReader.readLine())){//结束会读入一个null值  
            sb.append(line);//写缓冲区  
        }  
        String result= sb.toString();//返回结果  
        try {  
            /*获取服务器返回的JSON数据*/  
//            JSONObject jsonObject= new JSONObject(result);  
//            returnResult=jsonObject.getInt("status");//获取JSON数据中status字段值  
            /*json数据处理*/
            JSONArray arr = new JSONArray(result);
            for (int i = 0; i < arr.length(); i++) {  
                JSONObject temp = (JSONObject) arr.get(i);  
                String notetitle = temp.getString("noteTitle");  
                String notetext = temp.getString("noteText");
                String notetime = temp.getString("noteTime");
                Log.e("log_tag", notetitle);
                Log.e("log_tag", notetext);
                Log.e("log_tag", notetime);
                
                NoteBean noteBean=new NoteBean(notetitle, notetext,notetime,"0","0");
                dao.insert(noteBean);
            }
        } catch (Exception e) {  
            // TODO: handle exception  
            Log.e("log_tag", "the Error parsing data "+e.toString());  
        }
	}
}

