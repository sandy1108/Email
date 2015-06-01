package com.email;

import com.email.app.MyApplication;
import com.email.utils.NetWork;
import com.zywx.wsq.email.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity implements OnItemClickListener, OnClickListener {
	private ListView mListvView;
	private long mExitTime=0;
	private ImageView image_create,image_back;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		MyApplication.addActivity(HomeActivity.this);
		 final MyExpendAdapter adapter=new MyExpendAdapter();
		
		 mListvView=(ListView) findViewById(R.id.list);
//		expendView.setGroupIndicator(null);  //设置默认图标不显示
		 mListvView.setAdapter(adapter);
		 
		 image_create = (ImageView) this.findViewById(R.id.image_create);
		 image_back = (ImageView) this.findViewById(R.id.image_break);
		 
		 image_create.setOnClickListener(this);
		 image_back.setOnClickListener(this);
		 mListvView.setOnItemClickListener(this);
		
	}
	
	

	
	/**
	 * 适配器
	 * @author Administrator
	 *
	 */
	private class MyExpendAdapter extends BaseAdapter{
		
		
		String[]  title = {"收件箱", "草稿箱","已发送"};
		int [] image = {R.drawable.all, R.drawable.caogaoxiang,R.drawable.hasread};
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return title.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return title[position];
		}

		@Override
		public long getItemId(int id) {
			// TODO Auto-generated method stub
			return id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (convertView==null) {
				convertView=getLayoutInflater().inflate(R.layout.email_child, null);				
			}
			ImageView child_icon = (ImageView) convertView.findViewById(R.id.child_icon);
			child_icon.setImageResource(image[position]);
			TextView tv = (TextView) convertView.findViewById(R.id.tv);
			tv.setText(title[position]);
			return convertView;
		}
		
	}
	
	/**
	 * 返回退出系统
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
//			if((System.currentTimeMillis()-mExitTime)<2000){
//				android.os.Process.killProcess(android.os.Process.myPid());
//				  
//			}else{
//				Toast.makeText(HomeActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//				mExitTime=System.currentTimeMillis();
//			}
			new MyApplication().onTerminate();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		// TODO Auto-generated method stub
		
		switch (position) {
		case 0:   //全部
			
			switch (NetWork.getNetWorkType(HomeActivity.this)) {
			case -1:
				Toast.makeText(HomeActivity.this, "请查看网络!", Toast.LENGTH_SHORT).show();
				break;
			case 0:   //wifi
				startActivity(new Intent(HomeActivity.this, MailBoxActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
				break;
				
			case 1:   //mobile
				onNetWork();
				break;
			default:
				break;
			}
			break;
		case 1:    //草稿
			startActivity(new Intent(HomeActivity.this, MailCaogaoxiangActivity.class));
			break;

		case 2:    //已发送
			startActivity(new Intent(HomeActivity.this, MailSentActivity.class));
			break;

		}
		
		
	}
	
	public void onNetWork(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
		.setMessage("查看邮件会产生大量流量，您确定在当前网络下查看邮件？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				startActivity(new Intent(HomeActivity.this, MailBoxActivity.class));
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.image_break:
//			finish();
			new MyApplication().onTerminate();
			break;

		case R.id.image_create:
			startActivity(new Intent(HomeActivity.this, MailEditActivity.class));
			break;
		}
	}
}
