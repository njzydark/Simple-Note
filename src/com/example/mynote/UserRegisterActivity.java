package com.example.mynote;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UserRegisterActivity extends Activity {
	
	private EditText username;
	private EditText userpwd;
	private EditText useremail;
	private Button regsb;
	private TextView reglogin;
	
	private String urlstr="http://10.0.2.2/SimpleCloudNote/appRegisterAction.php";//服务器php接口地址
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		username=(EditText) findViewById(R.id.regname);
		userpwd=(EditText) findViewById(R.id.regpwd);
		useremail=(EditText) findViewById(R.id.regemail);
		regsb=(Button) findViewById(R.id.regsb);
		reglogin=(TextView) findViewById(R.id.reglogin);
		
		regsb.setOnClickListener(new View.OnClickListener() {  
			  
		    @Override  
		    public void onClick(View v) {//登陆按钮监听事件  
		        new Thread(new Runnable() {  
		            @Override  
		            public void run() {  
		                try {  
		                    int result = register();  
		                    //login()为向php服务器提交请求的函数，返回数据类型为int  
		                    if (result == 0) {  
		                        Log.e("log_tag", "注册失败，用户名已存在");  
		                        //Toast toast=null;  
		                        Looper.prepare();  
		                        Toast.makeText(UserRegisterActivity.this, "注册失败，用户名已存在", Toast.LENGTH_SHORT).show();  
		                        Looper.loop();  
		                    } else if (result == 1) {  
		                        Log.e("log_tag", "注册失败，邮箱已存在");  
		                        //Toast toast=null;  
		                        Looper.prepare();  
		                        Toast.makeText(UserRegisterActivity.this, "注册失败，邮箱已存在", Toast.LENGTH_SHORT).show();  
		                        Looper.loop();  
		                    } else if (result == 2) {  
		                        Log.e("log_tag", "注册成功");  
		                        //Toast toast=null;  
		                        Looper.prepare();  
		                        Toast.makeText(UserRegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();  
//		                        Looper.loop();
		                        Intent intent=new Intent(UserRegisterActivity.this,UserLoginActivity.class);//注册成功后进入主界面
		                        startActivity(intent);
		                        UserRegisterActivity.this.finish();
		                        Looper.loop();
		                    }  
		                } catch (IOException e) {  
		                    System.out.println(e.getMessage());  
		                }  
		            }  
		        }).start();  
		    }  
		});
		
		reglogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(UserRegisterActivity.this,UserLoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
	
	private int register() throws IOException {  
        int returnResult=0;  
        /*获取用户名，密码和邮箱*/  
        String user_name=username.getText().toString();  
        String user_password=userpwd.getText().toString();
        String user_email=useremail.getText().toString();
        if(user_name==null||user_name.length()<=0){  
            Looper.prepare();  
            Toast.makeText(UserRegisterActivity.this,"请输入账号", Toast.LENGTH_LONG).show();  
            Looper.loop();  
            return 0;  
      
        }  
        if(user_password==null||user_password.length()<=0){  
            Looper.prepare();  
            Toast.makeText(UserRegisterActivity.this,"请输入密码", Toast.LENGTH_LONG).show();  
            Looper.loop();  
            return 0;  
        }
        if(user_email==null||user_email.length()<=0){  
            Looper.prepare();  
            Toast.makeText(UserRegisterActivity.this,"请输入邮箱地址", Toast.LENGTH_LONG).show();  
            Looper.loop();  
            return 0;  
        }
//        String urlstr="http://10.0.2.2/SimpleCloudNote/appRegisterAction.php";//服务器php接口地址
        //建立网络连接  
        URL url = new URL(urlstr);  
        HttpURLConnection http= (HttpURLConnection) url.openConnection();  
        //POST方法传值  
        String params="username="+user_name+'&'+"userpwd="+user_password+'&'+"useremail="+user_email;  
        http.setDoOutput(true);  
        http.setRequestMethod("POST");  
        OutputStream out=http.getOutputStream();  
        out.write(params.getBytes());//post提交参数  
        out.flush();  
        out.close();  
      
        
//        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection
//                       .getInputStream()));
//         
//        String lines="";
//        String ss="";
//        while((lines = in.readLine()) != null){
//        // System.out.println(lines);
//        ss+=lines;
//        }

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
            /*json数据处理*/
//            JSONArray arr = new JSONArray(result);
//            for (int i = 0; i < arr.length(); i++) {  
//                JSONObject temp = (JSONObject) arr.get(i);  
//                String notetitle = temp.getString("noteTitle");  
//                String notetext = temp.getString("noteText");
//                String notetime = temp.getString("noteTime");
//                Log.e("log_tag", notetitle);
//                Log.e("log_tag", notetext);
//                Log.e("log_tag", notetime);
//            }
        } catch (Exception e) {  
            // TODO: handle exception  
            Log.e("log_tag", "the Error parsing data "+e.toString());  
        }  
        return returnResult;  
    }
}

