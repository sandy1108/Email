
package com.email;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.email.app.MyApplication;
import com.email.bean.Email;
import com.email.utils.IOUtil;
import com.zywx.wsq.email.R;

public class MailContentActivity extends Activity {

    private TextView tv_addr, tv_mailsubject, tv_mailcontent;
    private ListView lv_mailattachment;
    private WebView wv_mailcontent;
    private ImageView btn_cancel;
    private TextView btn_relay;
    private ImageView image_create;
    private TextView tv_title;
    private ArrayList<InputStream> attachmentsInputStreams;
    private Email email;
//    private int position;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_mailcontent);
        email = (Email) getIntent().getSerializableExtra("EMAIL");
//        position = getIntent().getIntExtra("EMAIL", 0);
        attachmentsInputStreams = ((MyApplication) getApplication()).getAttachmentsInputStreams();
        init();
    }

    private void init() {
        handler = new MyHandler(this);
        tv_addr = (TextView) findViewById(R.id.tv_addr);
        tv_mailsubject = (TextView) findViewById(R.id.tv_mailsubject);
        tv_mailcontent = (TextView) findViewById(R.id.tv_mailcontent);
        if (email.getAttachments()!=null && email.getAttachments().size() > 0) {
            lv_mailattachment = (ListView) findViewById(R.id.lv_mailattachment);
//            lv_mailattachment.setVisibility(View.VISIBLE);
            lv_mailattachment.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, email.getAttachments()));
            lv_mailattachment.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            handler.obtainMessage(0, "开始下载\"" + email.getAttachments().get(position) + "\"").sendToTarget();
                            InputStream is = attachmentsInputStreams.get(position);
                            File f = new File(Environment.getExternalStorageDirectory().toString() + "/temp/");
                            if (!f.exists()) {
								f.mkdirs();
							}
                            String path = new IOUtil().stream2file(is, Environment.getExternalStorageDirectory().toString() + "/temp/" + email.getAttachments().get(position));
                            if (path == null) {
                                handler.obtainMessage(0, "下载失败！").sendToTarget();
                            } else {
                                handler.obtainMessage(0, "文件保存在：" + path).sendToTarget();
                            }
                        }
                    }).start();
                }
            });
        }

        btn_cancel = (ImageView) findViewById(R.id.image_break);   //返回
        btn_relay = (TextView) findViewById(R.id.tv_relay);   //回复
        btn_relay.setVisibility(View.VISIBLE);
        image_create = (ImageView) findViewById(R.id.image_create);
        image_create.setVisibility(View.GONE);
        tv_title = (TextView) this.findViewById(R.id.tv_title);
        tv_title.setText("收件箱");
        
        tv_addr.setText(email.getFrom());
        tv_mailsubject.setText(email.getSubject());
        if (email.isHtml()) {
            wv_mailcontent = (WebView) findViewById(R.id.wv_mailcontent);
            wv_mailcontent.setVisibility(View.VISIBLE);
            wv_mailcontent.loadDataWithBaseURL(null, email.getContent(), "text/html", "utf-8", null);
           // wv_mailcontent.getSettings().setLoadWithOverviewMode(true);
           // wv_mailcontent.getSettings().setUseWideViewPort(true);
            //设置缩放
            wv_mailcontent.getSettings().setBuiltInZoomControls(true);
            
            //网页适配
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int scale = dm.densityDpi;
            if (scale == 240) {
                wv_mailcontent.getSettings().setDefaultZoom(ZoomDensity.FAR);
            } else if (scale == 160) {
                wv_mailcontent.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
            } else {
                wv_mailcontent.getSettings().setDefaultZoom(ZoomDensity.CLOSE);
            }
            wv_mailcontent.setWebChromeClient(new WebChromeClient());
            tv_mailcontent.setVisibility(View.GONE);
        } else {
            tv_mailcontent.setText(email.getContent());
        }

        btn_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                MailContentActivity.this.finish();
            }
        });

        btn_relay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MailContentActivity.this, MailEditActivity.class).putExtra("EMAIL", email).putExtra("TYPE", 1).putExtra("email", "content"));
            }
        });       
     
    }

    private static class MyHandler extends Handler {

        private WeakReference<MailContentActivity> wrActivity;

        public MyHandler(MailContentActivity activity) {
            this.wrActivity = new WeakReference<MailContentActivity>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            final MailContentActivity activity = wrActivity.get();
            switch (msg.what) {
                case 0:
                    Toast.makeText(activity.getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        };
    };

}
