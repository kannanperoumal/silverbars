package org.silverbars.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;

import org.silverbars.service.OrderService;
import org.silverbars.types.MergedOrder;
import org.silverbars.types.Order;
import org.silverbars.types.OrderException;
import org.silverbars.types.OrderType;

/**
 * @author kannan
 *
 */
public class OrderServiceImpl implements OrderService {

	private static final Queue<Order> orders = new ConcurrentLinkedQueue<>();
	private static final Map<Order, MergedOrder> buyOrders = new ConcurrentSkipListMap<>();
	private static final Map<Order, MergedOrder> sellOrders = new ConcurrentSkipListMap<>();
	
	
	/* (non-Javadoc)
	 * @see org.silverbars.service.OrderService#registerOrder(org.silverbars.types.Order)
	 */
	@Override
	public Order registerOrder(Order order) throws OrderException {
		try {
			String err = extractValidationError(order);
			if(!err.equals("")) {
				throw new OrderException(err);
			}
			
			Map<Order, MergedOrder> orderMap = OrderType.BUY.equals(order.getOrderType())? buyOrders : sellOrders;
			MergedOrder mo = orderMap.get(order);
			if(mo==null) {
				mo = order.toMergedOrder();
				orderMap.put(order, mo);
			} else {
				mo.merge(order);
			}
			orders.add(order);
			return order;
		} catch (Exception e) {
			throw new OrderException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.silverbars.service.OrderService#cancelOrder(org.silverbars.types.Order)
	 */
	@Override
	public Order cancelOrder(Order order) throws OrderException {
		try {
			String err = extractValidationError(order);
			if(!err.equals("")) {
				throw new OrderException(err);
			}
			Map<Order, MergedOrder> orderMap = OrderType.BUY.equals(order.getOrderType())? buyOrders : sellOrders;
			MergedOrder mo = orderMap.get(order);
			if(mo==null) {
				throw new OrderException("The requested order for deletion doesn't created yet. Order:"+order);
			} 
			if(mo.getQuantity() <= order.getQuantity()) {
				orderMap.remove(order);
			} else {
				mo.remove(order);
			}
			orders.remove(order);
			return order;
		
		} catch (Exception e) {
			throw new OrderException("Exception occured at order removal. ", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.silverbars.service.OrderService#summaryOrders()
	 */
	@Override
	public Map<OrderType, Collection<MergedOrder>> summaryOrders() throws OrderException {
		try {
			Map<OrderType, Collection<MergedOrder>> result = new HashMap<>(2);
			result.put(OrderType.SELL, sellOrders.values());
			result.put(OrderType.BUY, buyOrders.values());
			return result;

		} catch (Exception e) {
			throw new OrderException("Exception occured at order summarization", e);
		}
	}
	
	private String extractValidationError(final Order o) {
		String errorMessage = "";
		if(o == null) {
			errorMessage += "Order doesn't contain any attributes\n";
		}
		if(o.getOrderType() == null) {
			errorMessage += "Order type is null\n";
		}
		if(o.getUserid() == null || o.getUserid().trim().equals("")) {
			errorMessage += "Order user id not supplied\n";
		}
		if(o.getPrice() < 1) {
			errorMessage += "Order price not supplied\n";
		}
		if(o.getQuantity() < 1) {
			errorMessage += "Order quantity not supplied\n";
		}
		return errorMessage;
	}
}
