package com.example.mynote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
	
		public MyDatabaseHelper(Context context) {
			//第一参数   上下文
			//第二参数   数据库名字
			//第三参数   游标集   可以自定义，一般默认
			//第四参数   版本号   至少从1开始
			super(context, "note_Db", null, 1);
			// TODO Auto-generated constructor stub
		}

		//数据库第一次创建时会调用这个方法
		//一般用来初始化一些表
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			//创建表，分四列
			//id   主键id 用于排序等操作
			//title   标题
			//content   正文
			//date   日期，一旦更改数据，自动获取当前日期
			String sql = "create table note(id integer primary key autoincrement,title text,content text,date timestamp not null default (datetime('now','localtime')),love text,trash text);";
			//执行sql语句
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}


}

