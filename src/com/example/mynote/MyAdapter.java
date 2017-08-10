package com.example.mynote;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
	
	public List<NoteBean> noteBeanList;
	private LayoutInflater layoutInfater;
	private String imagepath;
	
	public MyAdapter(Context context,List<NoteBean> noteBeanList){
		this.noteBeanList=noteBeanList;
		layoutInfater=LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return noteBeanList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		NoteBean bean=noteBeanList.get(position);
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView=layoutInfater.inflate(R.layout.item, null);
			viewHolder.tv_title=(TextView) convertView.findViewById(R.id.tv_title);
			viewHolder.tv_content=(TextView) convertView.findViewById(R.id.tv_content);
			viewHolder.tv_date=(TextView) convertView.findViewById(R.id.tv_date);
			viewHolder.iv_content=(ImageView) convertView.findViewById(R.id.iv_content);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		//设置tag标志，防止图片错位
		imagepath=null;
		if(SearchImage(bean.content)){
			viewHolder.iv_content.setTag(imagepath);
		}
		
		viewHolder.tv_title.setText(bean.title);
		viewHolder.tv_content.setText(bean.content);
		viewHolder.tv_date.setText(bean.time);
		
		//初始化图片，防止图片滑动重复
		viewHolder.iv_content.setImageResource(R.drawable.mynote);
		
		//进行判断，正文中有图片则添加，没有则隐藏默认图片
		if (viewHolder.iv_content.getTag() != null && viewHolder.iv_content.getTag().equals(imagepath)) {
			String imageUrl = Scheme.FILE.wrap(imagepath);
			DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)  
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)  
            .build();  
			ImageLoader.getInstance().displayImage(imageUrl, viewHolder.iv_content, options);
			viewHolder.iv_content.setVisibility(View.VISIBLE);//显示imageview
		}else{
			viewHolder.iv_content.setVisibility(View.GONE);//隐藏imageview
		}
		
		return convertView;
	}
	
	class ViewHolder{
		public TextView tv_title;
		public TextView tv_content;
		public TextView tv_date;
		public ImageView iv_content;
	}

	public boolean SearchImage(String content){
		List<String> textList = StringUtils.cutStringByImgTag(content);
        for (int i = 0; i < textList.size(); i++) {
            String text = textList.get(i);
            if (text.contains("<img")) {
                String imagePath = StringUtils.getImgSrc(text);
                imagepath=imagePath;
                return true;
            }
            
        }
        return false;
	}

}

