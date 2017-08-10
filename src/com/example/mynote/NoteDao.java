package com.example.mynote;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NoteDao {
	private MyDatabaseHelper mOpenHelper;

	public NoteDao(Context context){
		mOpenHelper = new MyDatabaseHelper(context);
	}
	
	//添加数据  
	public void insert(NoteBean note){
		//获取数据库
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		//判断数据库是否打开
		if(db.isOpen()){
			ContentValues values = new ContentValues();
			values.put("title", note.getTitle());
			values.put("content", note.getContent());
			values.put("love", note.getLove());
			values.put("trash", note.getTrash());
//			values.put("time", note.getTime());//APP新增
			db.insert("note", null, values);
			db.close();
		}
	}
	
	//删除数据
	public void delete(String title){
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		if(db.isOpen()){
			String whereClause = "title = ?";
			String[] whereArgs = {title};
			db.delete("note", whereClause, whereArgs);
			db.close();
		}
	}
	
	//更新数据
	public void update(String etTitle,String etContent,String relTitle,String relContent){
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		if(db.isOpen()){
			ContentValues values = new ContentValues();
			values.put("title", etTitle);
			values.put("content", etContent);
			db.update("note", values, "title=?",new String[] {relTitle});
			db.close();
		}
	}
	
	//查询表单所有数据
	public List<NoteBean> queryAll(){
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		if(db.isOpen()){
			//罗列出要选出的字段
			String[] columns = {"id","title","content","date","love","trash"};
			String selection = "trash=?";  //选择查询条件   null为查询所有
			String[] selectionArgs = {"0"}; //选择条件参数
			String groupBy = null; //分组语句
			String having = null; //过滤语句
			String orderBy = "id desc"; //按主键id降序排序
			Cursor cursor = db.query("note", columns, selection, selectionArgs, groupBy, having, orderBy);
			List<NoteBean> noteBeanList = new ArrayList<NoteBean>();
			NoteBean note = null;
			if(cursor!=null && cursor.getCount()>0){
				String title;
				String content;
				String date;
				String love;
				String trash;
				while(cursor.moveToNext()){
					title = cursor.getString(1);//获取第二列的值
					content = cursor.getString(2);//获取第三列的值
					date=cursor.getString(3);//获取第四列的值
					love=cursor.getString(4);
					trash=cursor.getString(5);
					note = new NoteBean(title,content,date,love,trash);
					noteBeanList.add(note);
				}
			}
			//cursor不关闭会一直存在，时间久了会出现内存溢出问题
			cursor.close();
			db.close();
			return noteBeanList;
		}
		return null;
	}
	
	public List<NoteBean> queryLove(){
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		if(db.isOpen()){
			//罗列出要选出的字段
			String[] columns = {"id","title","content","date","love","trash"};
			String selection = "love=?";  //选择查询条件   null为查询所有
			String[] selectionArgs = {"1"}; //选择条件参数
			String groupBy = null; //分组语句
			String having = null; //过滤语句
			String orderBy = "id desc"; //按主键id降序排序
			Cursor cursor = db.query("note", columns, selection, selectionArgs, groupBy, having, orderBy);
			List<NoteBean> noteBeanList = new ArrayList<NoteBean>();
			NoteBean note = null;
			if(cursor!=null && cursor.getCount()>0){
				String title;
				String content;
				String date;
				String love;
				String trash;
				while(cursor.moveToNext()){
					title = cursor.getString(1);//获取第二列的值
					content = cursor.getString(2);//获取第三列的值
					date=cursor.getString(3);//获取第四列的值
					love=cursor.getString(4);
					trash=cursor.getString(5);
					note = new NoteBean(title,content,date,love,trash);
					noteBeanList.add(note);
				}
			}
			//cursor不关闭会一直存在，时间久了会出现内存溢出问题
			cursor.close();
			db.close();
			return noteBeanList;
		}
		return null;
	}
	
	public List<NoteBean> queryTrash(){
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		if(db.isOpen()){
			//罗列出要选出的字段
			String[] columns = {"id","title","content","date","love","trash"};
			String selection = "trash=?";  //选择查询条件   null为查询所有
			String[] selectionArgs = {"1"}; //选择条件参数
			String groupBy = null; //分组语句
			String having = null; //过滤语句
			String orderBy = "id desc"; //按主键id降序排序
			Cursor cursor = db.query("note", columns, selection, selectionArgs, groupBy, having, orderBy);
			List<NoteBean> noteBeanList = new ArrayList<NoteBean>();
			NoteBean note = null;
			if(cursor!=null && cursor.getCount()>0){
				String title;
				String content;
				String date;
				String love;
				String trash;
				while(cursor.moveToNext()){
					title = cursor.getString(1);//获取第二列的值
					content = cursor.getString(2);//获取第三列的值
					date=cursor.getString(3);//获取第四列的值
					love=cursor.getString(4);
					trash=cursor.getString(5);
					note = new NoteBean(title,content,date,love,trash);
					noteBeanList.add(note);
				}
			}
			//cursor不关闭会一直存在，时间久了会出现内存溢出问题
			cursor.close();
			db.close();
			return noteBeanList;
		}
		return null;
	}
}

