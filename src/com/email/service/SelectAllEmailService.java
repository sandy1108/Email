package com.email.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.email.bean.Email;
import com.email.bean.Status;
import com.email.db.SentDao;
import com.email.utils.Constant;
import com.email.utils.Tools;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

public class SelectAllEmailService extends IntentService {

	private TimerTask task;
	private Timer timer;
	private List<MailReceiver> mailReceivers;
	private boolean isNextPage;
	int curr_page = 1;
	int unit_page = 10;
	int total_page = 0;
	int size = 0;
	SentDao sent;

	public SelectAllEmailService() {
		super("");
		// TODO Auto-generated constructor stub
		sent = new SentDao(this);
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		curr_page = intent.getIntExtra("curr_page", 1);
		// getemaill();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		startTimer();
	}

	public void startTimer() {
		timer = new Timer();
		task = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare();
				getemaill();
			}
		};

		timer.schedule(task, 1000);

	}

	public void getemaill() {
		try {
			mailReceivers = MailHelper.getInstance(SelectAllEmailService.this)
					.getAllMail(Constant.KEY.KEY_EMAIL_ALL_TYPE);
			// Message[] message = getAllEmail();
			total_page = mailReceivers.size() % unit_page == 0 ? mailReceivers
					.size() / unit_page : mailReceivers.size() / unit_page + 1;
			// total_page =
			// message.length%unit_page==0?message.length/unit_page:message.length/unit_page+1;

			size = mailReceivers.size();

			Log.e("===total_page====", total_page + "");
			Log.e("====curr_page===", curr_page + "");
			Log.e("====size===", size + "");

			List<Map<String, String>> list = sent.ondelete_ReceiveEmail();
			List<String> data = new ArrayList<String>();
			for (int j = 0; j < list.size(); j++) {
				data.add(list.get(j).get("messageId"));

			}

			for (int i = mailReceivers.size() - 1; i > 0; i--) {
				// for (int i = mailReceivers.size()-1; i >
				// (size>curr_page*unit_page?size-curr_page*unit_page:0); i--) {

				if (!data.contains(mailReceivers.get(i).getMessageID())) {

					Intent intent = new Intent();
					intent.setAction(Constant.ACTION.ACTION_MESSAGE);
					MailReceiver mailReceiver = mailReceivers.get(i);
					// ResolveMail mailReceiver = new ResolveMail((MimeMessage)
					// message[i]);

					Email email = new Email();
					try {

						email.setMessageID(mailReceiver.getMessageID());

						email.setFrom(mailReceiver.getFrom());

						email.setTo(mailReceiver.getMailAddress("TO"));

						email.setCc(mailReceiver.getMailAddress("CC"));

						email.setBcc(mailReceiver.getMailAddress("BCC"));

						email.setSubject(mailReceiver.getSubject());

						email.setSentdata(mailReceiver.getSentData());
						Tools.getData("======1==");
						email.setContent(mailReceiver.getMailContent());

						Tools.getData("======2==");
						email.setReplysign(mailReceiver.getReplySign());

						email.setHtml(mailReceiver.isHtml());

						email.setNews(mailReceiver.isNew());

						email.setAttachments(mailReceiver.getAttachments());

						email.setCharset(mailReceiver.getCharset());

						// email.setAttachmentsInputStreams(mailReceiver.getAttachmentsInputStreams());

						Bundle bundle = new Bundle();
						bundle.putSerializable("email", email);
						intent.putExtras(bundle);

						sendBroadcast(intent);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			Intent intent = new Intent();
			intent.setAction(Constant.ACTION.ACTION_MESSAGE);
			Status st = new Status();
			if (curr_page != total_page) {
				st.setNextPage(true);
			} else {
				st.setNextPage(false);
			}
			st.setLoadstatus(true);
			Bundle bundle = new Bundle();
			bundle.putSerializable("status", st);
			intent.putExtras(bundle);
			sendBroadcast(intent);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
