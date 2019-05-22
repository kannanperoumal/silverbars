package org.silverbars.service;

import java.util.Collection;
import java.util.Map;

import org.silverbars.types.MergedOrder;
import org.silverbars.types.Order;
import org.silverbars.types.OrderException;
import org.silverbars.types.OrderType;

/**
 * @author kannan
 *
 */
public interface OrderService {
	/**
	 * This allows creating a new order
	 * @param order
	 * @return
	 * @throws OrderException
	 */
	Order registerOrder(Order order) throws OrderException;
	
	/**
	 * This allows removing a registered order
	 * @param order
	 * @return
	 * @throws OrderException
	 */
	Order cancelOrder(Order order) throws OrderException;
	
	/**
	 * This allows providing summary of order dash board
	 * @return
	 * @throws OrderException
	 */
	Map<OrderType, Collection<MergedOrder>> summaryOrders() throws OrderException;
	
	
}
