package com.email.app;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Session;
import javax.mail.Store;

import com.email.bean.MailInfo;

import android.app.Activity;
import android.app.Application;
import android.os.StrictMode.VmPolicy;

public class MyApplication extends Application {
	
	 private final static float TARGET_HEAP_UTILIZATION = 0.75f;
	 private static  List<Activity> activities = new ArrayList<Activity>();  

	 public static void addActivity(Activity activity) {  
	        activities.add(activity);  
	    }  
	  
	    @Override  
	    public void onTerminate() {  
	        super.onTerminate();  
	          
	        for (Activity activity : activities) {  
	            activity.finish();  
	        }  
	          
//	        onDestroy();  
	          
	        System.exit(0);  
	    }  
	 
	 @Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
//		VMRuntime.getRuntime().setTargetHeapUtilization(TARGET_HEAP_UTILIZATION);
	}
	
	
	public static Session session = null;
	public static Store getStore() {
		return store;
	}

	public static void setStore(Store store) {
		MyApplication.store = store;
	}

	public static MailInfo info=new MailInfo();
	
	private static Store store;
    private ArrayList<InputStream> attachmentsInputStreams;

    public ArrayList<InputStream> getAttachmentsInputStreams() {
        return attachmentsInputStreams;
    }

    public void setAttachmentsInputStreams(ArrayList<InputStream> attachmentsInputStreams) {
        this.attachmentsInputStreams = attachmentsInputStreams;
    }
	
}
