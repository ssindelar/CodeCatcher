package it.lelmarir.codecatcher.widgetset.client.codecatcher;


import it.lelmarir.codecatcher.widgetset.CodeCatcher;
import it.lelmarir.codecatcher.widgetset.client.codecatcher.CodeCatcherState.Shortcut;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;

import elemental.events.Event;
import elemental.events.KeyboardEvent.KeyCode;

@Connect(CodeCatcher.class)
public class CodeCatcherConnector extends AbstractExtensionConnector implements NativePreviewHandler {
	private static final long serialVersionUID = -2609631912936019843L;

	CodeCatcherServerRpc rpc = RpcProxy.create(CodeCatcherServerRpc.class, this);
	
	Logger logger = Logger.getLogger("CodeCatcher");
	
	
	private int codeStartKeyCode;
	private int codeEndKeyCode;
	private boolean isSkipFollowingReturn = false;
	private boolean isAlwaysOn = false;
	
	private boolean isReadingCode = false;
	private String code = "";
	private boolean isReadingJustEnded = false;
	private List<Shortcut> shortCuts = new ArrayList<Shortcut>();
	
	
	private HandlerRegistration handler;
	
	public CodeCatcherConnector() {
	}
	
	@Override
	protected void extend(ServerConnector target) {
		handler = com.google.gwt.user.client.Event.addNativePreviewHandler(this);
//		logger.log(Level.INFO, "[DEV] extended!");
	}
	
	@Override
	public void onUnregister() {
		handler.removeHandler();
		super.onUnregister();		
	}

	/*
	 * logger.log(Level.INFO, "[DEV] onPreviewNativeEvent: type: "+event.getNativeEvent().getType());
		logger.log(Level.INFO, "[DEV] onPreviewNativeEvent: char: "+event.getNativeEvent().getCharCode());
		logger.log(Level.INFO, "[DEV] onPreviewNativeEvent: key : "+event.getNativeEvent().getKeyCode());
	 * 			logger.log(Level.INFO, "[DEV] AON: reset buffer");
	 * 			logger.log(Level.INFO, "[DEV] AON: append: "+this.code);
	 * 
	 * logger.log(Level.INFO, "[DEV] onPreviewNativeEvent: not reading and start");
	 * logger.log(Level.INFO, "[DEV] onPreviewNativeEvent: reading and end");
	 * logger.log(Level.INFO, "[DEV] onPreviewNativeEvent: reading char"+(char) keyCode );
	 */
	
	@Override
	public void onPreviewNativeEvent(NativePreviewEvent event) {

		if (event.getNativeEvent().getType().equals(Event.KEYPRESS)) {
			// int charCode = event.getNativeEvent().getCharCode();
			int keyCode = event.getNativeEvent().getKeyCode();

			// logger.log(Level.INFO, "[DEV] keypress: char: "+charCode);
			// logger.log(Level.INFO, "[DEV] keypress: key : "+keyCode);

			if (this.isReadingJustEnded && this.isSkipFollowingReturn) {
				this.isReadingJustEnded = false;
				// check if "Return"-key was pressed.
				if (keyCode == KeyCode.ENTER) {
					event.cancel();
					return;
				}
			}

			if (this.checkForShortcut(event)) {
				return;
			}

			if (this.isAlwaysOn) {
				processAlwaysOn(event);
			} else {
				processNormalMode(event);
			}
		}

	}

	private boolean checkForShortcut(NativePreviewEvent event) {
		
		NativeEvent nativeEvent = event.getNativeEvent();
		
		int charCode = nativeEvent.getCharCode();
		int keyCode = nativeEvent.getKeyCode();
		
		// convert to alphabetic characters to uppercase
		if(!nativeEvent.getShiftKey() && charCode >= 97 && charCode <= 122){
//			logger.log(Level.INFO, "[DEV] checkForShortcut: shift not pressed");
			charCode -= 32;
		}
		
		for (Shortcut shortcut : this.shortCuts) {
			if ((shortcut.charCode == charCode || (charCode == 0 && shortcut.charCode == keyCode))
					&& shortcut.isAltDown == nativeEvent.getAltKey()
					&& shortcut.isCtrlDown == nativeEvent.getCtrlKey()
					&& shortcut.isShiftDown == nativeEvent.getShiftKey()) {

//				logger.log(Level.INFO, "[DEV] checkForShortcut: found:"
//						+ shortcut);

				ShortcutEvent serverEvent = new ShortcutEvent();
				serverEvent.setCharCode(shortcut.charCode);
				serverEvent.setAltDown(shortcut.isAltDown);
				serverEvent.setCtrlDown(shortcut.isCtrlDown);
				serverEvent.setShiftDown(shortcut.isShiftDown);
				rpc.onShortCutCatched(serverEvent);

				event.cancel();

				return true;
			}
		}
		
		return false;
	}

	/**
	 * Processing when intercepting all keys.
	 * @param event
	 * @param charCode
	 */
	private void processAlwaysOn(NativePreviewEvent event) {
		int keyCode = event.getNativeEvent().getKeyCode();
		int charCode = event.getNativeEvent().getCharCode();
		
//		logger.log(Level.INFO, "[DEV] alwasOn: char:"+charCode);
//		logger.log(Level.INFO, "[DEV] alwasOn: key :"+keyCode);
		
		if (charCode == codeStartKeyCode) {
			// reset buffer
			this.code = "";
		} else if (charCode == codeEndKeyCode) {
			this.sendBufferToServer();
			this.isReadingJustEnded = true;
		} else if (keyCode == KeyCode.ENTER) {
			// send with codeEndKeyCode or Return
			this.sendBufferToServer();
		} else if (charCode != 0) {
			// default: append new char
			this.code += (char) charCode;
		}

		event.cancel();
	}


	/**
	 * Normal processing with start and end key
	 * @param event
	 * @param charCode
	 */
	private void processNormalMode(NativePreviewEvent event){
		
		int charCode = event.getNativeEvent().getCharCode();
		
		if(charCode == 0){
			return;
		}
		
		if (!this.isReadingCode && charCode == codeStartKeyCode) {
			this.isReadingCode = true;
			event.cancel();
			this.code = "";
		} else if (this.isReadingCode) {
			event.cancel();
			if (charCode == codeEndKeyCode) {
				this.isReadingCode = false;
				this.isReadingJustEnded = true;
				this.sendBufferToServer();
			} else {
				this.code += (char) charCode;
			}
		}
	}

	private void sendBufferToServer(){
		CodeCatchedEvent serverEvent = new CodeCatchedEvent();
		serverEvent.setCode(this.code);
		rpc.onCodeCatched(serverEvent);
		this.code = "";
	}

	@Override
	public CodeCatcherState getState() {
		return (CodeCatcherState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
			super.onStateChanged(stateChangeEvent);

			CodeCatcherState state = this.getState();
			this.codeStartKeyCode = state.codeStartKeyCode;
			this.codeEndKeyCode = state.codeEndKeyCode;
			this.isSkipFollowingReturn = state.isSkipFollowingReturn;
			this.isAlwaysOn = state.isAlwaysOn;
			this.shortCuts = state.shortCuts;
			
//			logger.log(Level.INFO, "[DEV] codeStartKeyCode: "+this.codeStartKeyCode);
//			logger.log(Level.INFO, "[DEV] codeEndKeyCode: "+this.codeEndKeyCode);
//			logger.log(Level.INFO, "[DEV] isSkipReturn: "+this.isSkipFollowingReturn);
//			logger.log(Level.INFO, "[DEV] isAlwaysOn: "+this.isAlwaysOn);
//			logger.log(Level.INFO, "[DEV] shortCuts: "+this.shortCuts);
	}	
}

