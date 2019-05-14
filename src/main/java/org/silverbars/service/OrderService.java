package org.silverbars.service;

import java.util.List;
import java.util.Map;

import org.silverbars.types.Order;
import org.silverbars.types.OrderType;

/**
 * @author kannan
 *
 */
public interface OrderService {
	/**
	 * This allows creating a new order
	 * @param order
	 * @return boolean
	 */
	boolean registerOrder(Order order);
	
	/**
	 * This allows removing a registered order
	 * @param order
	 * @return boolean
	 */
	boolean cancelOrder(Order order);
	
	/**
	 * This allows providing summary of order dash board
	 */
	Map<OrderType, Map<Integer, List<Order>>> summaryOrders();
	
	/**
	 * This returns number of available orders
	 * @return
	 */
	int availableOrders();
}
