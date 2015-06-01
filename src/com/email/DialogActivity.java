package com.email;

import com.email.adapter.DialogAdapter;
import com.email.bean.Email;
import com.zywx.wsq.email.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class DialogActivity extends Activity{

	private DialogAdapter mAdapter;
	private int activity =0;
	
	private ListView mListView;
	private String email;
	private String messageId;
	private Email mailmessage;
	private int pos;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_layout);
		activity = getIntent().getIntExtra("activity", 0);
		init();
	}
	public void init(){
		mAdapter = new DialogAdapter(this, activity);
		
		mListView = (ListView) this.findViewById(R.id.listView);
		mListView.setAdapter(mAdapter);
		
		if (activity==1) {
			mailmessage = (Email) getIntent().getSerializableExtra("EMAIL");
			pos = getIntent().getIntExtra("position", 0);
			Log.e("===ac=====position====", pos+"");
		}else if (activity==2) {
			messageId = getIntent().getStringExtra("messageId");
		}else if (activity==3) {
			email = getIntent().getStringExtra("email");
			messageId = getIntent().getStringExtra("messageId");
		}
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				// TODO Auto-generated method stub
				//1 收件箱  2.草稿   3.已发送
				if (activity==1) {
					switch (position) {
					case 0:
						startActivity(new Intent(DialogActivity.this , MailEditActivity.class)
						.putExtra("EMAILMESSAGE", mailmessage).putExtra("email", "relay"));//回复
						finish();
						break;
					case 1:
						startActivity(new Intent(DialogActivity.this , MailEditActivity.class)
						.putExtra("EMAILMESSAGE", mailmessage).putExtra("email", "receiveRelay"));//转发
						finish();
						break;
					case 2:
						startActivity(new Intent(DialogActivity.this , MailEditActivity.class)
						.putExtra("EMAILMESSAGE", mailmessage).putExtra("email", "receiveRelayAll"));//转发全部
						break;
					case 3:
						Intent intent  = new Intent();
						intent.putExtra("position", pos);
						Log.e("====de====position====", pos+"");
						intent.putExtra("messageId", mailmessage.getMessageID());
						setResult(103, intent);
						finish();
						break;
					default:
						break;
					}
				}else if (activity==2) {
					//
					Intent intent  = new Intent();
					intent.putExtra("messageId", messageId);
					setResult(102, intent);
					finish();
				}else if (activity==3) {
					switch (position) {
					case 0:  //转发
						startActivity(new Intent(DialogActivity.this, MailEditActivity.class)
						.putExtra("email", "sentRelay").putExtra("messageId", messageId));
						finish();
						break;
					case 1:  //全部转发
						startActivity(new Intent(DialogActivity.this , MailEditActivity.class)
						.putExtra("email", "sentRelayAll").putExtra("messageId", messageId));
						break;
					case 2://删除
						Intent intent  = new Intent();
						intent.putExtra("messageId", messageId);
						setResult(101, intent);
						finish();
						break;
						
					default:
						break;
					}
				}
			}
		});
		
		
	}
	
	
}
