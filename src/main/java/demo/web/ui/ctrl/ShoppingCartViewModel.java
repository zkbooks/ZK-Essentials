package demo.web.ui.ctrl;

import java.util.List;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;

import demo.model.DAOs;
import demo.model.OrderDAO;
import demo.model.bean.CartItem;
import demo.web.ShoppingCart;

public class ShoppingCartViewModel {
	
	private final EventQueue<Event> eq = EventQueues.lookup("shoppingQueue", EventQueues.DESKTOP, true);

	@Init
	public void init(@ContextParam(ContextType.BINDER) 
			final Binder binder) {
		eq.subscribe(new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				if((event instanceof ShoppingEvent) && 
				   ((ShoppingEvent)event).getType().equals(ShoppingEvent.Type.ADDTOCART)) {
					binder.notifyChange(ShoppingCartViewModel.this, "cartItems");
				}
			}
		});
	}
	
	private String orderNote;
	private CartItem selectedItem;
	
	public String getOrderNote() {
		return orderNote;
	}

	public void setOrderNote(String orderNote) {
		this.orderNote = orderNote;
	}

	public CartItem getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(CartItem selectedItem) {
		this.selectedItem = selectedItem;
	}

	public List<CartItem> getCartItems() {
		return UserUtils.getShoppingCart().getItems();
	}
	
	public ShoppingCart getShoppingCart() {
		return UserUtils.getShoppingCart();
	}
	
	@Command("submitOrder")
	@NotifyChange({"cartItems", "shoppingCart", "orderNote"})
	public void submitOrder() {
		DAOs.getOrderDAO().createOrder(UserUtils.getCurrentUserId(), getCartItems(), getOrderNote());
		
		eq.publish(new ShoppingEvent(ShoppingEvent.Type.CREATEORDER));
		
		clearOrders();
	}
	
	@Command("clearOrders")
	@NotifyChange({"cartItems", "shoppingCart"})
	public void clearOrders() {
		getShoppingCart().clear();
	}
	
	@Command("deleteOrder")
	@NotifyChange({"cartItems", "shoppingCart"})
	public void deleteOrder(@BindingParam("cartItem") CartItem cartItem) {
		getShoppingCart().remove(cartItem.getProduct().getId());
	}
}
