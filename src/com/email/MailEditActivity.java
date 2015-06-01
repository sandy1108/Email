package com.email;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.email.adapter.GridViewAdapter;
import com.email.app.MyApplication;
import com.email.bean.Attachment;
import com.email.bean.Email;
import com.email.db.SentDao;
import com.email.utils.HttpUtil;
import com.zywx.wsq.email.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class MailEditActivity extends Activity implements OnClickListener {
	private EditText mail_to;
	private EditText mail_from;
	private EditText mail_topic;
	private EditText mail_content;
	
	private ImageView image_create;
	private ImageView image_break;
	private TextView send, tv_relay;
	private ImageButton add_lianxiren;
	private ImageButton attachment;
	private GridView gridView;
	private WebView pre_webview;
	private GridViewAdapter<Attachment> adapter = null;
	private int mailid=-1;
	
	private TextView tv_title;
	private LinearLayout layout_cc;
	private EditText et_mail_cc;
	
	 private static final int SUCCESS = 1;
	 private static final int FAILED = -1;
	 private boolean isCaogaoxiang=true;
	    private ProgressDialog dialog;
	    boolean isSent=false;
	    String email ="";
	    SentDao sent;
	    private Email emailMessage;
	    private WebSettings mWebSettings; 
	    
	    
	    HttpUtil util=new HttpUtil();
		private Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SUCCESS:
//					dialog.dismiss();
					//发件成功不存入草稿箱
					isCaogaoxiang=false;
					//判断邮件是否来自草稿箱那么可以从草稿箱删除了
					if(mailid>0){
						Uri uri=Uri.parse("content://com.caogaoxiangprovider");
						getContentResolver().delete(uri, "id=?", new String[]{mailid+""});
						uri=Uri.parse("content://com.attachmentprovider");
						getContentResolver().delete(uri, "mailid=?", new String[]{mailid+""});
						//返回草稿箱
						Toast.makeText(getApplicationContext(), "邮件发送成功", Toast.LENGTH_SHORT).show();
						Intent intent=new Intent(MailEditActivity.this, MailCaogaoxiangActivity.class);
						startActivity(intent);
						finish();
					}else{
						Toast.makeText(getApplicationContext(), "邮件发送成功", Toast.LENGTH_SHORT).show();	
						//清空之前填写的数据
						mail_from.getText().clear();
						mail_to.getText().clear();
						mail_topic.getText().clear();
						mail_content.getText().clear();
						adapter=new GridViewAdapter<Attachment>(MailEditActivity.this);
					}
					
					break;
				case FAILED:
					dialog.cancel();
					//发件失败是否存入草稿箱
					isCaogaoxiang=true;
					Toast.makeText(getApplicationContext(), "邮件发送失败", Toast.LENGTH_SHORT).show();
					break;
				}
				super.handleMessage(msg);
			}
	    	
	    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.email_writer);

		sent = new SentDao(this);
		
		init();
		
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		mail_to=(EditText) findViewById(R.id.mail_to);
		mail_from=(EditText) findViewById(R.id.mail_from);
		mail_topic=(EditText) findViewById(R.id.mail_topic);
		mail_content=(EditText) findViewById(R.id.content);
		send=(TextView) findViewById(R.id.tv_send);
		tv_relay=(TextView) findViewById(R.id.tv_relay);
		image_create = (ImageView) this.findViewById(R.id.image_create);
	
		pre_webview = (WebView) this.findViewById(R.id.pre_webview);
		
		
		initWebView();
		
		image_break = (ImageView) this.findViewById(R.id.image_break);
		attachment=(ImageButton) findViewById(R.id.add_att);
		add_lianxiren=(ImageButton) findViewById(R.id.add_lianxiren);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		tv_title.setText("写邮件");
		gridView=(GridView) findViewById(R.id.pre_view);
		gridView.setVisibility(View.VISIBLE);
		layout_cc = (LinearLayout) this.findViewById(R.id.layout_cc);
		et_mail_cc = (EditText) this.findViewById(R.id.et_mail_cc);
		
		
		mail_from.setText(MyApplication.info.getUserName());
		send.setOnClickListener(this);
		tv_relay.setOnClickListener(this);
		attachment.setOnClickListener(this);
		add_lianxiren.setOnClickListener(this);
		image_break.setOnClickListener(this);
		adapter = new GridViewAdapter<Attachment>(this);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new MyOnItemClickListener());
		
		
		
		email = getIntent().getStringExtra("email");
		
		if (getIntent().hasExtra("mailid")) {
			
			//判断是否从草稿箱来的
			mailid=getIntent().getIntExtra("mailid", -1);
		}
		
		if(mailid>-1){
			Uri uri=Uri.parse("content://com.caogaoxiangprovider");
			Cursor c=getContentResolver().query(uri, null, "mailfrom=? and id=?", new String[]{MyApplication.info.getUserName(),mailid+""}, null);
			if(c.moveToNext()){
				mail_to.setText(c.getString(2));
				mail_topic.setText(c.getString(3));
				mail_content.setText(c.getString(4));
			}
			
			uri=Uri.parse("content://com.attachmentprovider");
			c=getContentResolver().query(uri, null, "mailid=?", new String[]{mailid+""}, null);
			List<Attachment> attachments=new ArrayList<Attachment>();
			while(c.moveToNext()){
				Attachment att=new Attachment(c.getString(2),c.getString(1),c.getLong(3));
				attachments.add(att);
			}
			
			c.close();
			//显示附件
			if(attachments.size()>0){
				for(Attachment affInfos:attachments){
					adapter.appendToList(affInfos);
					int a = adapter.getList().size();
					int count = (int) Math.ceil(a / 4.0);
					gridView.setLayoutParams(new LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							(int) (94 * 1.5 * count)));
				}
			}
			
		}
		
		/*
		 * 判断是否是从已发送邮件中过来
		 */
		
		if (email!=null && email.trim().equals("sent")) {  //已发送
			
			
			isSent=true;
			String messageId = getIntent().getStringExtra("messageId");
			
			Email  em = sent.onSelect_SentMail(messageId);
			if (em!=null) {
				
			mail_content.setText(em.getContent());
			mail_to.setText(em.getTo());
			mail_from.setText(em.getFrom());
			mail_topic.setText(em.getSubject());
			send.setVisibility(View.GONE);
			mail_from.setFocusable(false);
			mail_to.setEnabled(false);
			mail_from.setEnabled(false);
			mail_topic.setEnabled(false);
			mail_content.setEnabled(false);
		}
		}else if(email!=null && email.trim().equals("sentRelay")){ //已发送  ---转发
			Log.e("==========", "转发");
			
			
			isSent=true;
			String messageId = getIntent().getStringExtra("messageId");
			
			Email  em = sent.onSelect_SentMail(messageId);
			if (em!=null) {	
			mail_content.setText(em.getContent());
			mail_content.setEnabled(false);
			mail_to.setText(em.getTo());
			mail_to.setEnabled(true);
			mail_from.setText(em.getFrom());
			mail_from.setEnabled(false);
			mail_from.setFocusable(false);
			mail_topic.setText(em.getSubject());
			mail_topic.setEnabled(false);
			et_mail_cc.setText(em.getCc());
			et_mail_cc.setEnabled(false);
			send.setVisibility(View.GONE);
			}
			
		}else if(email!=null && email.trim().equals("sentRelayAll")){  //已发送--全部转发
			Log.e("==========", "转发ALL");
			layout_cc.setVisibility(View.VISIBLE);
			
			
			isSent=true;
			String messageId = getIntent().getStringExtra("messageId");
			
			Email  em = sent.onSelect_SentMail(messageId);
			if (em!=null) {	
			mail_content.setText(em.getContent());
			mail_content.setEnabled(false);
			mail_to.setText(em.getTo());
			mail_to.setEnabled(true);
			mail_from.setText(em.getFrom());
			mail_from.setEnabled(false);
			mail_from.setFocusable(false);
			mail_topic.setText(em.getSubject());
			mail_topic.setEnabled(false);
			et_mail_cc.setText(em.getCc());
			et_mail_cc.setEnabled(false);
			send.setVisibility(View.GONE);
			}
			
		}else if(email!=null && email.trim().equals("receive")){ //收件箱直接进入
			
//			layout_cc.setVisibility(View.VISIBLE);
			
			
			emailMessage = (Email) getIntent().getSerializableExtra("EMAILMESSAGE");
			pre_webview.setVisibility(View.VISIBLE);
			mail_content.setVisibility(View.GONE);
			
			pre_webview.loadDataWithBaseURL(null, emailMessage.getContent(), "text/html", "utf-8", null);
			
			mail_to.setText(emailMessage.getTo());
			mail_from.setText(emailMessage.getFrom());
			mail_topic.setText(emailMessage.getSubject());
			mail_content.setText(emailMessage.getContent());
			
			mail_to.setEnabled(false);
			mail_from.setEnabled(false);
			mail_topic.setEnabled(false);
			mail_content.setEnabled(false);
			
		}else if(email!=null && email.trim().equals("relay")){   //回复
			
			
			emailMessage = (Email) getIntent().getSerializableExtra("EMAILMESSAGE");
//			pre_webview.setVisibility(View.VISIBLE);
			mail_content.setVisibility(View.VISIBLE);
			
//			pre_webview.loadDataWithBaseURL(null, emailMessage.getContent(), "text/html", "utf-8", null);
			
			mail_to.setText(emailMessage.getTo());
			mail_to.setEnabled(false);
			mail_from.setText(emailMessage.getFrom());
			mail_from.setEnabled(false);
			mail_topic.setText(emailMessage.getSubject());
			mail_topic.setEnabled(false);
//			mail_content.setText(emailMessage.getContent());
			
		}else if(email!=null && email.trim().equals("receiveRelay")){
			
			
			emailMessage = (Email) getIntent().getSerializableExtra("EMAILMESSAGE");
			pre_webview.setVisibility(View.VISIBLE);
			mail_content.setVisibility(View.GONE);
			
			pre_webview.loadDataWithBaseURL(null, emailMessage.getContent(), "text/html", "utf-8", null);
			
			mail_to.setText(emailMessage.getTo());
			mail_from.setText(emailMessage.getFrom());
			mail_topic.setText(emailMessage.getSubject());
			et_mail_cc.setText(emailMessage.getCc());
			
			mail_to.setEnabled(true);
			mail_from.setEnabled(false);
			mail_topic.setEnabled(false);
			mail_content.setEnabled(false);
		}else if(email!=null && email.trim().equals("receiveRelayAll")){
			layout_cc.setVisibility(View.VISIBLE);
			
			emailMessage = (Email) getIntent().getSerializableExtra("EMAILMESSAGE");
			pre_webview.setVisibility(View.VISIBLE);
			mail_content.setVisibility(View.GONE);
			
			pre_webview.loadDataWithBaseURL(null, emailMessage.getContent(), "text/html", "utf-8", null);
			
			mail_to.setText(emailMessage.getTo());
			mail_from.setText(emailMessage.getFrom());
			mail_topic.setText(emailMessage.getSubject());
			et_mail_cc.setText(emailMessage.getCc());
			
			mail_to.setEnabled(true);
			mail_from.setEnabled(false);
			mail_topic.setEnabled(false);
			mail_content.setEnabled(false);
		}else if(email!=null && email.trim().equals("content")){
			
			
			emailMessage = (Email) getIntent().getSerializableExtra("EMAIL");
			pre_webview.setVisibility(View.GONE);
			mail_content.setVisibility(View.VISIBLE);
			
//			pre_webview.loadDataWithBaseURL(null, emailMessage.getContent(), "text/html", "utf-8", null);
			
			mail_to.setText(emailMessage.getTo());
			mail_from.setText(emailMessage.getFrom());
			mail_topic.setText(emailMessage.getSubject());
			et_mail_cc.setText(emailMessage.getCc());
			
			mail_to.setEnabled(true);
			mail_from.setEnabled(false);
			mail_topic.setEnabled(true);
			mail_content.setEnabled(true);
		}else if(email!=null && email.trim().equals("cao")){
				send.setVisibility(View.VISIBLE);
				tv_relay.setVisibility(View.GONE);
			}
		
		image_create.setVisibility(View.GONE);
		send.setVisibility(View.VISIBLE);
		tv_relay.setVisibility(View.GONE);
		
		
	}
	
	public void initWebView(){
		//设置支持JavaScript等 
        mWebSettings = pre_webview.getSettings(); 
        mWebSettings.setJavaScriptEnabled(true); 
        mWebSettings.setBuiltInZoomControls(true); 
        mWebSettings.setLightTouchEnabled(true); 
        mWebSettings.setSupportZoom(true); 
        pre_webview.setHapticFeedbackEnabled(false); 
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_send:
			sendMail();
			finish();
			break;
		case R.id.tv_relay:
			sendMail();
			finish();
			break;
		case R.id.add_att:
			addAttachment();
			break;
		case R.id.add_lianxiren:
			Intent intent=new Intent(MailEditActivity.this,MailAddConstact.class);
			startActivityForResult(intent, 2);
			break;
		case R.id.image_break:
			back();
			break;
		}
		
	};
	
	/**
	 * 添加附件
	 */
	private void addAttachment() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/");
		startActivityForResult(intent, 1);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1:
				Uri uri = null;
				if (data != null) {
					uri = data.getData();
				}

				String path = uri.getPath();
				Attachment affInfos = Attachment.GetFileInfo(path);
				adapter.appendToList(affInfos);
				int a = adapter.getList().size();
				int count = (int) Math.ceil(a / 4.0);
				gridView.setLayoutParams(new LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						(int) (94 * 1.5 * count)));
				break;
			}
		}
		
		/**
		 * 多个联系人
		 */
		if(requestCode==2){
			List<String> chooseUsers=data.getStringArrayListExtra("chooseUsers");
			StringBuilder str=new StringBuilder();
			for(int i=0;i<chooseUsers.size();i++){
				if(i==chooseUsers.size()-1){
					str.append("<"+chooseUsers.get(i)+">");
				}else{
					str.append("<"+chooseUsers.get(i)+">,");
				}
			}
			mail_to.setText(str.toString());
			
		}
	}
    
	/**
	 * 设置邮件数据
	 */
	private void sendMail(){
		MyApplication.info.setAttachmentInfos(adapter.getList());
		MyApplication.info.setFromAddress(mail_from.getText().toString().trim());
		MyApplication.info.setSubject(mail_topic.getText().toString().trim());
		MyApplication.info.setContent(mail_content.getText().toString().trim());
		//收件人
		final String str=mail_to.getText().toString().trim();
		final String str2 = et_mail_cc.getText().toString().trim();
		String []recevers=str.split(",");
//		String[] rece=new String[recevers.length+re.length];
		List<String> list = new ArrayList<String>();
		for(int i=0;i<recevers.length;i++){
			if(recevers[i].startsWith("<")&&recevers[i].endsWith(">")){
				recevers[i]=recevers[i].substring(recevers[i].lastIndexOf("<")+1, recevers[i].lastIndexOf(">"));
//				rece[i]=recevers[i];
				list.add(recevers[i]);
			}
		}
	
		if (str2.trim()!=null &&str2.trim().length()>0) {
			String[] str_msg = str2.split(",");
			for (int i = 0; i < str_msg.length; i++) {
				list.add(recevers[i].substring(recevers[i].lastIndexOf("<")+1, recevers[i].lastIndexOf(">")));
			}
			String[] rece_msg = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				rece_msg[i]=list.get(i);
			}
			MyApplication.info.setReceivers(recevers);
		}else{

//		Log.e("====length========", ""+rece.length);
		
		MyApplication.info.setReceivers(recevers);
		}
		
		dialog=new ProgressDialog(MailEditActivity.this);
		dialog.setMessage("正在发送");
//		dialog.show();
		
		/**
		 * 发送
		 */
		new Thread(){
			@Override
			public void run() {
				boolean flag=util.sendTextMail(MyApplication.info, MyApplication.session);
				Message msg=new Message();
				//发送邮件
				if(flag){
					msg.what=SUCCESS;
					handler.sendMessage(msg);
					
					sent.onInsert_SentMail(str, MyApplication.info.getSubject(), MyApplication.info.getFromAddress(), getTime(), MyApplication.info.getContent());
					
				}else{
					msg.what=FAILED;
					handler.sendMessage(msg);
				}
			}
			
		}.start();

	}
	public String getTime(){
		SimpleDateFormat    formatter    =   new    SimpleDateFormat("yyyy-MM-dd HH:mm:ss");       
		Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间       
		String    str    =    formatter.format(curDate);   
		return str;
	}
	
	/**
	 * 点击事件
	 * @author Administrator
	 *
	 */
	private class MyOnItemClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1,
				final int arg2, long arg3) {
			Attachment infos = (Attachment) adapter.getItem(arg2);
			AlertDialog.Builder builder = new AlertDialog.Builder(
					MailEditActivity.this);
			builder.setTitle(infos.getFileName());
			builder.setIcon(getResources().getColor(
					android.R.color.transparent));
			builder.setMessage("是否删除当前附件");
			builder.setNegativeButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							adapter.clearPositionList(arg2);
							int a = adapter.getList().size();
							int count = (int) Math.ceil(a / 4.0);
							gridView.setLayoutParams(new LayoutParams(
									LinearLayout.LayoutParams.MATCH_PARENT,
									(int) (94 * 1.5 * count)));
						}
					});
			builder.setPositiveButton("取消",null);
			builder.create().show();
		}
	}
	
	/**
	 * 返回
	 * @param v
	 */
	public void back(){
		if(isCaogaoxiang&&mail_to.getText().toString().trim()!=null && isSent==false){
			AlertDialog.Builder builder=new Builder(MailEditActivity.this);
			builder.setMessage("是否存入草稿箱");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//保存至数据库
					saveToCaogaoxiang();
					finish();
				}
				
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
				
			});
			builder.show();
		}else{
			finish();
		}
		
	}
    
	/**
	 * 返回按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(isCaogaoxiang&&mail_to.getText().toString().trim()!=null){
				AlertDialog.Builder builder=new Builder(MailEditActivity.this);
				builder.setMessage("是否存入草稿箱");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//保存至数据库
						saveToCaogaoxiang();
					}
					
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
					
				});
				builder.show();
			}	
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 保存至草稿箱
	 */
	private void saveToCaogaoxiang(){
		Uri uri=Uri.parse("content://com.caogaoxiangprovider");
		ContentValues values=new ContentValues();
		values.put("mailfrom", MyApplication.info.getUserName());
		values.put("mailto", mail_to.getText().toString().trim());
		values.put("subject", mail_topic.getText().toString().trim());
		values.put("content", mail_content.getText().toString().trim());
		String url=getContentResolver().insert(uri, values).toString();
		int id=Integer.parseInt(url.substring(url.length()-1));
		//保存附件
		if(adapter.getList().size()>0){
			Uri att_uri=Uri.parse("content://com.attachmentprovider");
			List<Attachment> attachments=adapter.getmList();
			values.clear();
			for(int i=0;i<attachments.size();i++){
				Attachment att=attachments.get(i);
				values.put("filename", att.getFileName());
				values.put("filepath", att.getFilePath());
				values.put("filesize", att.getFileSize());
				values.put("mailid", id);
				getContentResolver().insert(att_uri, values);
			}
		}
		Toast.makeText(MailEditActivity.this, "保存至草稿箱", Toast.LENGTH_SHORT).show();
	};

}
