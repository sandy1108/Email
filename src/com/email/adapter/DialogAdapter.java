package com.email.adapter;

import java.util.ArrayList;
import java.util.List;

import com.email.bean.Email;
import com.zywx.wsq.email.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DialogAdapter extends BaseAdapter{

	private Context context;
	private LayoutInflater mInflater;
	List<String> mList = new ArrayList<String>();
	private  int activity;  //1 收件箱  2.草稿   3.已发送
	public DialogAdapter(Context context, int activity){
		this.context = context;
		this.activity = activity;
		
		if (activity==1) {
			mList.add("回复");
			mList.add("转发");
			mList.add("全部转发");
			mList.add("删除");
		}else if(activity==2){
			mList.add("删除");
		}else if(activity==3){
//			mList.add("回复");
			mList.add("转发");
			mList.add("全部转发");
			mList.add("删除");
		}
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int id) {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public View getView(int position, View contextView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		
		if (contextView==null) {
			
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			contextView = mInflater.inflate(R.layout.list_item, null);
		}
		TextView dialog = (TextView) contextView.findViewById(R.id.tv_dialog_item_name);
		dialog.setText(mList.get(position));
				
		return contextView;
	}

}
