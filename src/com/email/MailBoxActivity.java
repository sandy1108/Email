package com.email;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.email.MailCaogaoxiangActivity.itemsOnClick;
import com.email.app.MyApplication;
import com.email.bean.Email;
import com.email.bean.Status;
import com.email.db.SentDao;
import com.email.service.MailReceiver;
import com.email.service.SelectAllEmailService;
import com.email.utils.Constant;
import com.zywx.wsq.email.R;

public class MailBoxActivity extends Activity implements  OnClickListener, OnItemLongClickListener, OnItemClickListener {

	private ArrayList<Email> mailslist = new ArrayList<Email>();
	private ArrayList<ArrayList<InputStream>> attachmentsInputStreamsList = new ArrayList<ArrayList<InputStream>>();
	private MyBroadcastReceiverEmail mBroadcastReceiverEmail;
	private MyAdapter myAdapter;
	private ListView lv_box;
	private ProgressDialog dialog;
	private TextView tv_load_page;
	private TextView tv_title;
	
	private Uri uri = Uri.parse("content://com.emailstatusprovider");
	private int curr_page=1;   //当前页
	private boolean isNextPage = true;   //是否有下页
	private boolean loadstatus =false;   //是否加载完成
	
	private ImageView image_create,image_break;
	private LinearLayout layout_down;
	
	private String messageId;
	//自定义的弹出框类  
    SelectPicPopupWindow menuWindow;
	SentDao sent;
	
	Intent mIntent;
	private Email email;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		MyApplication.addActivity(MailBoxActivity.this);
		setContentView(R.layout.email_mailbox);
		mIntent = new Intent(this, SelectAllEmailService.class);
		
		//实例化SelectPicPopupWindow  
        menuWindow = new SelectPicPopupWindow(1,this, new itemsOnClick());  
        
		initView();
		sent = new SentDao(this);
		registerAReceiver();
		
		onStartService(curr_page);
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		onStopService();
		
		if (mBroadcastReceiverEmail != null) {
			unregisterReceiver(mBroadcastReceiverEmail);
		}
	}
	
	private void initView() {
		lv_box = (ListView) findViewById(R.id.lv_box);
		myAdapter = new MyAdapter();
		lv_box.setAdapter(myAdapter);
		image_break = (ImageView) this.findViewById(R.id.image_break);
		image_create = (ImageView) this.findViewById(R.id.image_create);
		tv_load_page = (TextView) this.findViewById(R.id.tv_load_page);
		layout_down = (LinearLayout) this.findViewById(R.id.layout_down);
		layout_down.setVisibility(View.VISIBLE);
		tv_load_page.setText("正在加载  第 "+(mailslist.size()+1)+" 条");
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		tv_title.setText("收件箱");
		image_break.setOnClickListener(this);
		image_create.setOnClickListener(this);
		
		lv_box.setOnItemClickListener(this);
		lv_box.setOnItemLongClickListener(this);
		dialog = new ProgressDialog(this);
		dialog.setMessage("加载中。。。");
		dialog.show();
		
	}

	/**
	 * 适配器
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mailslist.size();
		}

		@Override
		public Object getItem(int position) {
			return mailslist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			 ViewHolder holder;
			if (convertView==null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(MailBoxActivity.this).inflate(
						R.layout.message_item, null);
				holder.tv_from = (TextView) convertView
						.findViewById(R.id.from);
				holder.tv_message = (TextView) convertView
						.findViewById(R.id.tv_message);
				holder.tv_subject = (TextView) convertView
						.findViewById(R.id.subject);
				
				convertView.setTag(holder);
			}
			 holder = (ViewHolder)convertView.getTag();
			
			holder.tv_message.setText(mailslist.get(position).getContent());
			holder.tv_from.setText(mailslist.get(position).getFrom());
			holder.tv_subject.setText(mailslist.get(position).getSubject());
			
			return convertView;
		}

		
	}

	static class ViewHolder{
		TextView tv_from;
		TextView tv_message;
		TextView tv_subject;
	}
	class MyBroadcastReceiverEmail extends  BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.e("===========", "=======接收到消息=====");
			if (intent.hasExtra("email")) {
				
			
			Email email = (Email) intent.getSerializableExtra("email");
			mailslist.add(email);
			attachmentsInputStreamsList.add(0, email.getAttachmentsInputStreams());
			}
			myAdapter.notifyDataSetChanged();
			
			tv_load_page.setText("正在加载  第 "+(mailslist.size()+1)+" 条");
			
			if(intent.hasExtra("status")){
				Status st = (Status) intent.getSerializableExtra("status");
				isNextPage = st.isNextPage();
				loadstatus = st.isLoadstatus();
				if (loadstatus) {
					layout_down.setVisibility(View.GONE);
					onStopService();
				}else{
					layout_down.setVisibility(View.VISIBLE);
				}
			}
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			
		}
		
	}
	
	public void registerAReceiver(){
		mBroadcastReceiverEmail = new MyBroadcastReceiverEmail();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constant.ACTION.ACTION_MESSAGE);
		registerReceiver(mBroadcastReceiverEmail, intentFilter);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.image_break:
//			onStopService();
//			finish();
			startActivity(new Intent(MailBoxActivity.this, HomeActivity.class));
			break;

		case R.id.image_create:
			startActivity(new Intent(MailBoxActivity.this, MailEditActivity.class));
			break;
		}
	}


	public void onStartService(int page){
		mIntent.putExtra("curr_page", page);
		startService(mIntent);
		
	}
	public void onStopService(){
		stopService(mIntent);
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
		// TODO Auto-generated method stub
//		
//		final Intent intent = new Intent(MailBoxActivity.this,
//				DialogActivity.class);
//		intent.putExtra("activity", 1);
//		intent.putExtra("position", position);
//		Log.e("======long==position====", position+"");
//		intent.putExtra("EMAIL",mailslist.get(position));
//		startActivityForResult(intent, 1);
		messageId = mailslist.get(position).getMessageID();
		email = mailslist.get(position);
		menuWindow.showAtLocation(MailBoxActivity.this.findViewById(R.id.mail), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
		return false;
	}

	
	private void delete(Email email){
		String messageId=email.getMessageID();
		
		sent.ondelete_receiveEmail(messageId);
		for (int i = 0; i < mailslist.size(); i++) {
			if (mailslist.get(i).getMessageID().trim().equals(messageId)) {
				
				mailslist.remove(i);
			}
		}
		
		myAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		String mailID = mailslist.get(position).getMessageID();

		
		((MyApplication) getApplication())
				.setAttachmentsInputStreams(attachmentsInputStreamsList
						.get(position));
		
		final Intent intent = new Intent(MailBoxActivity.this,
				MailContentActivity.class);
		intent.putExtra("EMAIL",mailslist.get(position));
		
		startActivity(intent);
	}
	
	public class itemsOnClick implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			switch (position) {
			case 0:
				startActivity(new Intent(MailBoxActivity.this , MailEditActivity.class)
				.putExtra("EMAILMESSAGE", email).putExtra("email", "relay"));//回复
				break;
			case 1:
				startActivity(new Intent(MailBoxActivity.this , MailEditActivity.class)
				.putExtra("EMAILMESSAGE", email).putExtra("email", "receiveRelay"));//转发
				break;
			case 2:
				startActivity(new Intent(MailBoxActivity.this , MailEditActivity.class)
				.putExtra("EMAILMESSAGE", email).putExtra("email", "receiveRelayAll"));//转发全部
				break;
			case 3:
				delete(email);
				break;
				
			default:
				break;
			}
		}
		
	}
}
