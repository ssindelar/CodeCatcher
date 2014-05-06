package it.lelmarir.codecatcher.widgetset;

import it.lelmarir.codecatcher.widgetset.client.codecatcher.CodeCatchedEvent;
import it.lelmarir.codecatcher.widgetset.client.codecatcher.CodeCatcherServerRpc;
import it.lelmarir.codecatcher.widgetset.client.codecatcher.CodeCatcherState;
import it.lelmarir.codecatcher.widgetset.client.codecatcher.CodeCatcherState.Shortcut;
import it.lelmarir.codecatcher.widgetset.client.codecatcher.ShortcutEvent;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.UI;

public class CodeCatcher extends AbstractExtension {
	private static final long serialVersionUID = 558846424963386852L;

	public interface CodeCatchedListener {
		void onCodeCatch(CodeCatchedEvent event);
	}

	private List<CodeCatchedListener> listeners = new ArrayList<CodeCatcher.CodeCatchedListener>();
	private List<ShortcutListener> shortcutListeners = new ArrayList<ShortcutListener>();

	private CodeCatcherServerRpc rpc = new CodeCatcherServerRpc() {
		private static final long serialVersionUID = 114033653705512975L;

		@Override
		public void onCodeCatched(CodeCatchedEvent event) {
			for (CodeCatchedListener listener : listeners) {
				listener.onCodeCatch(event);
			}
		}

		@Override
		public void onShortCutCatched(ShortcutEvent event) {
			List<ShortcutListener> copiedListeners = new ArrayList<ShortcutListener>(shortcutListeners);
			for (ShortcutListener listener : copiedListeners) {
				if (listener.getKeyCode() == event.getCharCode()
						&& event.isAltDown() == isModifierRequired(
								listener.getModifiers(), ModifierKey.ALT)
						&& event.isCtrlDown() == isModifierRequired(
								listener.getModifiers(), ModifierKey.CTRL)
						&& event.isShiftDown() == isModifierRequired(
								listener.getModifiers(), ModifierKey.SHIFT)) {
					listener.handleAction(this, UI.getCurrent());
				}
			}

		}
	};
	
	protected CodeCatcher() {
	}

	public CodeCatcher(AbstractClientConnector connector) {
		registerRpc(rpc);
		extend(connector);
	}

	@Override
	public CodeCatcherState getState() {
		return (CodeCatcherState) super.getState();
	}

	public void addListener(CodeCatchedListener listener) {
		this.listeners.add(listener);
	}

	public boolean removeListener(CodeCatchedListener listener) {
		return listeners.remove(listener);
	}

	public char getStartCharacter() {
		return (char) getState().codeStartKeyCode;
	}

	public void setStartCharacter(char character) {
		getState().codeStartKeyCode = (int) character;
		this.markAsDirty();
	}

	public char getEndCharacter() {
		return (char) getState().codeEndKeyCode;
	}

	public void setEndCharacter(char character) {
		int value = (int) character;
		getState().codeEndKeyCode = value;
		this.markAsDirty();
	}

	public boolean isSkipFollowingReturn() {
		return getState().isSkipFollowingReturn;
	}

	/**
	 * If active the first "Return"-key after the end of the scan-state will be
	 * skipped.
	 * 
	 * @param isSkip
	 */
	public void setSkipFollowingReturn(boolean isSkip) {
		getState().isSkipFollowingReturn = isSkip;
		this.markAsDirty();
	}

	public boolean isAlwaysOn() {
		return getState().isAlwaysOn;
	}

	/**
	 * Activates a global interception of all keypresses. When a start character
	 * is read the buffer is cleared and when a end character is read the buffer
	 * is send to server.
	 * 
	 * @param isAlwaysOn
	 */
	public void setAlwaysOn(boolean isAlwaysOn) {
		getState().isAlwaysOn = isAlwaysOn;
		this.markAsDirty();
	}

	public void clearShortCuts() {
		getState().shortCuts.clear();
		this.shortcutListeners.clear();
		this.markAsDirty();
	}

	public void addShortCut(ShortcutListener listener) {
		this.shortcutListeners.add(listener);
		Shortcut shortCut = new CodeCatcherState.Shortcut();
		shortCut.charCode = listener.getKeyCode();
		shortCut.isAltDown = isModifierRequired(listener.getModifiers(),
				ModifierKey.ALT);
		shortCut.isCtrlDown = isModifierRequired(listener.getModifiers(),
				ModifierKey.CTRL);
		shortCut.isShiftDown = isModifierRequired(listener.getModifiers(),
				ModifierKey.SHIFT);
		getState().shortCuts.add(shortCut);
		this.markAsDirty();
	}
	
	public void removeShortCut(ShortcutListener listener){
		this.shortcutListeners.remove(listener);
		List<Shortcut> oldShortCuts = new ArrayList<Shortcut>(
				this.getState().shortCuts);
		for (Shortcut shortCut : oldShortCuts) {
			if (shortCut.charCode == listener.getKeyCode()
					&& shortCut.isAltDown == isModifierRequired(
							listener.getModifiers(), ModifierKey.ALT)
					&& shortCut.isCtrlDown == isModifierRequired(
							listener.getModifiers(), ModifierKey.CTRL)
					&& shortCut.isShiftDown == isModifierRequired(
							listener.getModifiers(), ModifierKey.SHIFT)) {
				this.getState().shortCuts.remove(shortCut);
				break;
			}
		}
		this.markAsDirty();
	}

	private boolean isModifierRequired(int[] modifiers, int modifierKey) {
		for (int i : modifiers) {
			if (i == modifierKey) {
				return true;
			}
		}
		return false;
	}
}
