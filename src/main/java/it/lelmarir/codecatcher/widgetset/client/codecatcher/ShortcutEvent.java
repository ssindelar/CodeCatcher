package it.lelmarir.codecatcher.widgetset.client.codecatcher;

import java.io.Serializable;

public class ShortcutEvent implements Serializable{
	private static final long serialVersionUID = -5664418502802694263L;
	private int charCode;
	public boolean isShiftDown;
	public boolean isCtrlDown;
	public boolean isAltDown;
	
	public ShortcutEvent() {
	}

	public int getCharCode() {
		return charCode;
	}

	public void setCharCode(int charCode) {
		this.charCode = charCode;
	}
	

	public boolean isShiftDown() {
		return isShiftDown;
	}

	public void setShiftDown(boolean isShiftDown) {
		this.isShiftDown = isShiftDown;
	}

	public boolean isCtrlDown() {
		return isCtrlDown;
	}

	public void setCtrlDown(boolean isCtrlDown) {
		this.isCtrlDown = isCtrlDown;
	}

	public boolean isAltDown() {
		return isAltDown;
	}

	public void setAltDown(boolean isAltDown) {
		this.isAltDown = isAltDown;
	}
}
