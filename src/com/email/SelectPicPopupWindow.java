package com.email;

import com.email.adapter.DialogAdapter;
import com.email.bean.Email;
import com.zywx.wsq.email.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;

public class SelectPicPopupWindow extends PopupWindow{
	
	private DialogAdapter mAdapter;

	private ListView mListView;
    private View mMenuView;  
   
    public SelectPicPopupWindow(int activity, Activity context,OnItemClickListener itemsOnClick) {  
        super(context);  
        LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        mMenuView = inflater.inflate(R.layout.dialog_layout, null);  
        mListView = (ListView) mMenuView.findViewById(R.id.listView);
    	mAdapter = new DialogAdapter(context, activity);
    	
    	mListView.setAdapter(mAdapter);
    	
    	mListView.setOnItemClickListener(itemsOnClick);
    	
    	//初始化PopupWindow
    	initPopupWindow();
    }  
    
    
    private void initPopupWindow(){
    	//设置按钮监听  
        //设置SelectPicPopupWindow的View  
        this.setContentView(mMenuView);  
        //设置SelectPicPopupWindow弹出窗体的宽  
        this.setWidth(LayoutParams.FILL_PARENT);  
        //设置SelectPicPopupWindow弹出窗体的高  
        this.setHeight(LayoutParams.WRAP_CONTENT);  
        //设置SelectPicPopupWindow弹出窗体可点击  
        this.setFocusable(true);  
        //设置SelectPicPopupWindow弹出窗体动画效果  
        this.setAnimationStyle(R.style.popupAnimation);  
        //实例化一个ColorDrawable颜色为半透明  
        ColorDrawable dw = new ColorDrawable(0xb0000000);  
        //设置SelectPicPopupWindow弹出窗体的背景  
        this.setBackgroundDrawable(dw);  
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框  
        mMenuView.setOnTouchListener(new OnTouchListener() {  
               
            public boolean onTouch(View v, MotionEvent event) {  
                   
                int height = mMenuView.findViewById(R.id.listView).getTop();  
                int y=(int) event.getY();  
                if(event.getAction()==MotionEvent.ACTION_UP){  
                    if(y<height){  
                        dismiss();  
                    }  
                }                 
                return true;  
            }  
        });  
    }
}
