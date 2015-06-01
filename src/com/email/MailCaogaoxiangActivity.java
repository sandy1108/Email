package com.email;

import java.util.ArrayList;
import java.util.List;

import javax.mail.search.MessageIDTerm;

import com.email.MailSentActivity.itemsOnClick;
import com.email.app.MyApplication;
import com.email.bean.EmailCaogao;
import com.zywx.wsq.email.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MailCaogaoxiangActivity extends Activity {
	private ListView lv;
	private List<EmailCaogao> allcaogaos;
	private MyAdapter adapter;
	private Uri uri=Uri.parse("content://com.caogaoxiangprovider");
	private ImageView image_break,image_create;
	private TextView tv_title;
	//自定义的弹出框类  
    SelectPicPopupWindow menuWindow;
    String messageId;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.email_caogaoxiang);
		allcaogaos=getAllcaogaos();
		lv=(ListView) findViewById(R.id.caogaoxiang);
		
	    adapter=new MyAdapter();
		lv.setAdapter(adapter);
		image_break = (ImageView) this.findViewById(R.id.image_break);
		image_create = (ImageView) this.findViewById(R.id.image_create);
		image_create.setVisibility(View.GONE);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		tv_title.setText("草稿箱");
		
		//实例化SelectPicPopupWindow  
        menuWindow = new SelectPicPopupWindow(2,this, new itemsOnClick());  
		
		image_break.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				EmailCaogao caogao=allcaogaos.get(position);
				Intent intent=new Intent(MailCaogaoxiangActivity.this,MailEditActivity.class);
				intent.putExtra("email", "cao");
				intent.putExtra("mailid", caogao.getId());
				startActivity(intent);
				
			}
			
		});
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				// TODO Auto-generated method stub
//				Intent intent=new Intent(MailCaogaoxiangActivity.this,DialogActivity.class);
//				intent.putExtra("email", "cao");
//				intent.putExtra("activity", 2);
//				intent.putExtra("messageId", allcaogaos.get(position).getId()+"");
//				startActivityForResult(intent, 10);
				menuWindow.showAtLocation(MailCaogaoxiangActivity.this.findViewById(R.id.mail), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
				messageId = allcaogaos.get(position).getId()+"";
				return false;
			}
		});
		
	}
	

	public void delete(String messageId){
		getContentResolver().delete(uri, "id=?",new String[]{messageId});
		allcaogaos.clear();
		allcaogaos.addAll(getAllcaogaos());
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * 获取所有草稿
	 * @return
	 */
	private List<EmailCaogao> getAllcaogaos(){
		List<EmailCaogao> caogaos=new ArrayList<EmailCaogao>();
		Cursor c=getContentResolver().query(uri, null, "mailfrom=?", new String[]{MyApplication.info.getUserName()}, null);
		while(c.moveToNext()){
			EmailCaogao caogao=new EmailCaogao(c.getInt(0),c.getString(1),c.getString(2),
					           c.getString(3),c.getString(4));
			caogaos.add(caogao);	
		}
		c.close();
		return caogaos;
	};
	
	
	/**
	 * 适配器
	 * @author Administrator
	 *
	 */
	private class MyAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return allcaogaos.size();
		}

		@Override
		public Object getItem(int position) {
			return allcaogaos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View item=View.inflate(MailCaogaoxiangActivity.this, R.layout.email_caogaoxiang_item, null);
			TextView mailto=(TextView) item.findViewById(R.id.tv_mailto);
			TextView mailsubject=(TextView) item.findViewById(R.id.tv_mailsubject);
			
			EmailCaogao caogao=allcaogaos.get(position);
			mailto.setText(caogao.getMailto());
			mailsubject.setText(caogao.getSubject());
			
			return item;
		}
		
	}
	
	/**
	 * 返回
	 * @param v
	 */
	public void back(View v){
		finish();
	}
	
	public class itemsOnClick implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			switch (position) {
			case 0:  //转发
				delete(messageId);
				
			default:
				break;
			}
		}
		
	}

}
