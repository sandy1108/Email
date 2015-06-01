package com.email.utils;


public class Constant {

	public static  final class KEY{
		 public static final String KEY_EMAIL_ALL_TYPE ="INBOX";
	}
	public static final class ACTION{
		public static final String ACTION_MESSAGE = "message_action";
	}
	
	public static final class SQL{
		public static final String DB_NAME="SENTEMAIL.DB";
		public static final String DB_TABLE="email_sent";
		public static final String DB_TABLE_DELETE="email_delete";
		public static final String DB_KEY_MSG_ID="messageID";
		public static final String DB_KEY_FORM = "from";
	    public static final String DB_KEY_TO = "to";
	    public static final String DB_KEY_CC = "cc";
	    public static final String DB_KEY_BCC = "bcc";
	    public static final String DB_KEY_SUBJECT = "subject";
	    public static final String DB_KEY_SENT_DATA = "sentdata";
	    public static final String DB_KEY_CONTENT= "content";
	}
}
