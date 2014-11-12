package it.lelmarir.codecatcher.widgetset.client.codecatcher;

import java.io.Serializable;

public class CodeCatchedEvent implements Serializable{
	private static final long serialVersionUID = -7295122867533919903L;
	
	private String code;
	private boolean isScanCode;
	
	public CodeCatchedEvent() {
	}
	
	public void setCode(String code){
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setScanCodeFlag(boolean isScanCode) {
		this.isScanCode = isScanCode;
	}
	
	public boolean isScanCodeFlag() {
		return isScanCode;
	}
}
