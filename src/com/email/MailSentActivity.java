package com.email;

import java.util.ArrayList;
import java.util.List;

import com.email.adapter.SentAdapter;
import com.email.app.MyApplication;
import com.email.bean.Email;
import com.email.db.SentDao;
import com.zywx.wsq.email.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MailSentActivity extends Activity implements OnClickListener{

	private ImageView image_back, image_create;
	private TextView tv_title;
	private ListView mListView;
	
	private List<Email> list;
	private SentAdapter mAdapter;
	String messageId;
	SentDao sent;
	
	//自定义的弹出框类  
    SelectPicPopupWindow menuWindow;  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.email_mailbox);
		sent = new SentDao(this);
		getAllSentEmail();
		init();
	}
	
	public void init(){
		
		image_back = (ImageView) this.findViewById(R.id.image_break);
		image_create = (ImageView) this.findViewById(R.id.image_create);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		tv_title.setText("已发送");
		mListView = (ListView) this.findViewById(R.id.lv_box);
		
		mAdapter = new SentAdapter(this, list, R.layout.message_item);
		mListView.setAdapter(mAdapter);
		image_back.setOnClickListener(this);
		image_create.setVisibility(View.GONE);
		
		
		//实例化SelectPicPopupWindow  
        menuWindow = new SelectPicPopupWindow(3,this, new itemsOnClick());  
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
			
				Log.e("====message_Id==========", list.get(position).getMessageID()+"");
				startActivity(new Intent(MailSentActivity.this, MailEditActivity.class).putExtra("email", "sent").putExtra("messageId", list.get(position).getMessageID()));
			}
		});
		
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int position, long id) {
				// TODO Auto-generated method stub
				messageId=list.get(position).getMessageID();
//				startActivityForResult(new Intent(MailSentActivity.this, DialogActivity.class).putExtra("email", "sent").putExtra("messageId", list.get(position).getMessageID()).putExtra("activity", 3),100);
				menuWindow.showAtLocation(MailSentActivity.this.findViewById(R.id.mail), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.image_break) {
			finish();
		}
	}
	
	public void getAllSentEmail(){
		
		list = new ArrayList<Email>();
		Log.e("====username========", MyApplication.info.getUserName()+"");
		list.addAll(sent.onSelectAll_SentMail());
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode==100 && resultCode==101) {
			sent.onDelete_SentMail(data.getStringExtra("messageId"));
			list.clear();
			list.addAll(sent.onSelectAll_SentMail());
			
			mAdapter.notifyDataSetChanged();
		}
		
	}
	
	public class itemsOnClick implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			switch (position) {
			case 0:  //转发
				startActivity(new Intent(MailSentActivity.this, MailEditActivity.class)
				.putExtra("email", "sentRelay").putExtra("messageId", messageId));
				break;
			case 1:  //全部转发
				startActivity(new Intent(MailSentActivity.this , MailEditActivity.class)
				.putExtra("email", "sentRelayAll").putExtra("messageId", messageId));
				break;
			case 2://删除
//				Intent intent  = new Intent();
//				intent.putExtra("messageId", messageId);
//				setResult(101, intent);
//				finish();
				delete(messageId);
				break;
				
			default:
				break;
			}
		}
		
	}
	public void delete(String messageId){
		sent.onDelete_SentMail(messageId);
		list.clear();
		list.addAll(sent.onSelectAll_SentMail());
		
		mAdapter.notifyDataSetChanged();
	}
	
	
}
