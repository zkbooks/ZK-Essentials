package demo.web.ui.ctrl;

import java.util.List;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;

import demo.model.OrderDAO;
import demo.model.bean.Order;

public class OrderViewViewModel  {
	private Order selectedItem;
	private static final OrderDAO orderdao = new OrderDAO();
	
	private final EventQueue<Event> eq = EventQueues.lookup("shoppingQueue", EventQueues.DESKTOP, true);
	
	@Init
	public void init(@ContextParam(ContextType.BIND_CONTEXT) BindContext ctx) {
		final Binder binder = ctx.getBinder();
		
		eq.subscribe(new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				if((event instanceof ShoppingEvent) && 
				   ((ShoppingEvent)event).getShoppingEventType().equals(ShoppingEvent.EventType.CREATEORDER)) {
					binder.notifyChange(OrderViewViewModel.this, "orders");					
				}
			}
		});
	}

	public Order getSelectedItem() {
		return selectedItem;
	}
	
	@NotifyChange("selectedItem")
	public void setSelectedItem(Order selectedItem) {
		this.selectedItem = selectedItem;
	}
	
	public List<Order> getOrders() {
		List<Order> orders = orderdao.findByUser(UserUtils.getCurrentUserId());
		return orders;
	}
	
	@Command
	@NotifyChange({"orders", "selectedItem"})
	public void cancelOrder() {
		if (getSelectedItem() == null) {
			return;
		}
		
		orderdao.cancelOrder(getSelectedItem().getId());
		setSelectedItem(null);
	}
}
