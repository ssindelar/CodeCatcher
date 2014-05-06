package it.lelmarir.codecatcher.widgetset.client.codecatcher;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.annotations.DelegateToWidget;

public class CodeCatcherState extends com.vaadin.shared.AbstractComponentState {
	private static final long serialVersionUID = -4831619310144550110L;
	
	@DelegateToWidget
	public int codeStartKeyCode;
	@DelegateToWidget
	public int codeEndKeyCode;
	@DelegateToWidget
	public boolean isSkipFollowingReturn = false;
	@DelegateToWidget
	public boolean isAlwaysOn = false;
	@DelegateToWidget
	public List<Shortcut> shortCuts = new ArrayList<Shortcut>();
	
	
	public static class Shortcut implements Serializable {
		private static final long serialVersionUID = -3244080181563504577L;

		public int charCode;
		
		public boolean isShiftDown;
		public boolean isCtrlDown;
		public boolean isAltDown;
		@Override
		public String toString() {
			return "Shortcut [key=" + charCode + ", shift=" + isShiftDown
					+ ", ctrl=" + isCtrlDown + ", alt=" + isAltDown + "]";
		}
	}

}