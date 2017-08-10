package com.example.mynote;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class ChangeTheme extends AppCompatActivity {
	
	private Toolbar toolbar;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changetheme);
		
		toolbar();
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
	}
	
	private void toolbar(){
    	toolbar = (Toolbar) findViewById(R.id.addtoolbar);
    	toolbar.setTitle("新建笔记");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
	
	private void changeTheme(int theme){
		overridePendingTransition(0, 0);
		finish();
		Intent intent=new Intent(this,MainActivity.class);
		intent.putExtra("theme", theme);
		overridePendingTransition(0, 0);
		startActivity(intent);
		ChangeTheme.this.finish();
	}
	
	public void clickTheme1(View view){
		changeTheme(R.style.AppThemeBase);
	}
	
	public void clickTheme2(View view){
		changeTheme(R.style.AppThemeBlack);
	}

}
