package com.email.bean;

import java.io.Serializable;

public class Status implements Serializable{

	private boolean isNextPage;
    private boolean loadstatus;
    
    
    
    public boolean isLoadstatus() {
		return loadstatus;
	}

	public void setLoadstatus(boolean loadstatus) {
		this.loadstatus = loadstatus;
	}

	public boolean isNextPage() {
		return isNextPage;
	}

	public void setNextPage(boolean isNextPage) {
		this.isNextPage = isNextPage;
	}

	public Status() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Status(boolean isNextPage, boolean loadstatus) {
		super();
		this.isNextPage = isNextPage;
		this.loadstatus = loadstatus;
	}
	
	
	
}
