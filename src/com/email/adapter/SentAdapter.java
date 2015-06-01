package com.email.adapter;

import java.util.List;

import com.email.bean.Email;
import com.zywx.wsq.email.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SentAdapter extends BaseAdapter{

	private Context context;
	private List<Email> mList;
	private int mResource;
	private LayoutInflater mInflater;
	
	public SentAdapter(Context context, List<Email> list, int resource){
		this.context = context;
		this.mList = list;
		this.mResource = resource;
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
			contextView = mInflater.inflate(mResource, null);
		}
		TextView from = (TextView) contextView.findViewById(R.id.from);
		TextView subject = (TextView) contextView.findViewById(R.id.subject);
		TextView tv_message = (TextView) contextView.findViewById(R.id.tv_message);
		from.setText(mList.get(position).getTo());
		subject.setText(mList.get(position).getSubject());
		tv_message.setText(mList.get(position).getContent());
				
		return contextView;
	}

	public void onnotify(){
		notifyDataSetChanged();
	}
}
