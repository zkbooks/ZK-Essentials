package demo.model;

public class DAOs {
	
	static public OrderDAO getOrderDAO(){
		return new OrderDAO();
	}
	
	static public ProductDAO getProductDAO(){
		return new ProductDAO();
	}
	
	static public UserDAO getUserDAO(){
		return new UserDAO();
	}
}
