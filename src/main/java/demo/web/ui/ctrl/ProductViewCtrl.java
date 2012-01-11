package demo.web.ui.ctrl;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;

import demo.model.ProductDAO;
import demo.model.bean.Product;
import demo.web.OverQuantityException;

/**
 * @author zkessentials store
 * 
 *         This is the controller for the product view as referenced in
 *         index.zul
 * 
 */
public class ProductViewCtrl extends SelectorComposer<Div> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4327599559929787819L;

	private final EventQueue<Event> eq = EventQueues.lookup("shoppingQueue",
			EventQueues.DESKTOP, true);

	@Wire
	private Grid prodGrid;

	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);

		ProductDAO prodDao = new ProductDAO();
		List<Product> prods = prodDao.findAllAvailable();

		ListModelList<Product> prodModel = new ListModelList<Product>(prods);
		prodGrid.setModel(prodModel);
		
		MouseEvent me = new MouseEvent("onBlah", prodGrid);
		Events.postEvent(prodGrid, me);
	}
	
	//this is dirty, remove after the selector composer fix
	@Listen("onBlah=#prodGrid")
	public void onBlah$prodGrid() {
		List<Component> components = Selectors.find(prodGrid, "#PrdoDiv #prodGrid row productOrder");
		
		for(Component c : components) {
			if(c instanceof ProductOrder) {
				final ProductOrder po = (ProductOrder)c;
				
				po.btnAdd.addEventListener(Events.ON_CLICK, new EventListener<Event>(){
					public void onEvent(Event event) throws Exception {
						ProductViewCtrl.this.addProduct(po);
					}
				});
			}
		}
	}
	
	@Listen("onAddProductOrder=#PrdoDiv prodGrid row productOrder")
	public void addProduct(/*Event fe*/ProductOrder po) {

		/*if (!(fe.getTarget() instanceof ProductOrder)) {
			return;
		}

		ProductOrder po = (ProductOrder) fe.getTarget();*/

		try {
			UserUtils.getShoppingCart(Executions.getCurrent().getSession())
					.add(po.getProduct(), po.getQuantity());
		} catch (OverQuantityException e) {
			po.setError(e.getMessage());
		}

		ShoppingEvent se = new ShoppingEvent("shopping event");
		se.setShoppingEventType(ShoppingEvent.EventType.ADD);
		eq.publish(se);

	}
}
