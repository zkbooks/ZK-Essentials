package demo.web.ui.ctrl;

import org.zkoss.zk.ui.event.Event;

public class ShoppingEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6082843618140247037L;

	public ShoppingEvent(EventType type) {
		super("onShopping");
		shoppingEventType = type;
	}
	
	public enum EventType {
		CREATEORDER, ADDTOCART
	}
	
	private EventType shoppingEventType;

	public EventType getShoppingEventType() {
		return shoppingEventType;
	}
}