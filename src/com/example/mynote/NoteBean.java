package com.example.mynote;

public class NoteBean {
	public String title;
	public String content;
	public String time;
	public String love;
	public String trash;
	
	public NoteBean(String title,String content,String time,String love,String trash){
		this.title=title;
		this.content=content;
		this.time=time;
		this.love=love;
		this.trash=trash;
	}
	
	public void setTime(String time){
		this.time=time;
	}
	
	public String getTime(){
		return time;
	}
	
	public void setTitle(String title){
		this.title=title;
	}
	
	public String getTitle(){
		return title;
	}
	
	public void setContent(String content){
		this.content=content;
	}
	
	public String getContent(){
		return content;
	}
	
	public void setLove(String love){
		this.love=love;
	}
	
	public String getLove(){
		return love;
	}
	
	public void setTrash(String trash){
		this.trash=trash;
	}
	
	public String getTrash(){
		return trash;
	}
}
