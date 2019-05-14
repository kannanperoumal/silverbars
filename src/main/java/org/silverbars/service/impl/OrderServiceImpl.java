package org.silverbars.service.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import org.silverbars.service.OrderService;
import org.silverbars.types.Order;
import org.silverbars.types.OrderType;

/**
 * @author kannan
 *
 */
public class OrderServiceImpl implements OrderService {

	private final Queue<Order> ordersPersistance = new ConcurrentLinkedQueue<>();
	private final Comparator<Integer> sellComarator = (Integer p1, Integer p2)->p1-p2;
	private final Comparator<Integer> buyComarator = (Integer p1, Integer p2)->p2-p1;
	
	@Override
	public boolean registerOrder(Order o) {
		try {
			String err = extractValidationError(o);
			if(!err.equals("")) {
				throw new Exception(err);
			}
			return ordersPersistance.add(o);
		
		} catch (Exception e) {
			System.err.println("Exception occured at order creation. "+ e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean cancelOrder(Order o) {
		try {
			String err = extractValidationError(o);
			if(!err.equals("")) {
				throw new Exception(err);
			}
			return ordersPersistance.remove(o);
		
		} catch (Exception e) {
			System.err.println("Exception occured at order deletion. "+ e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Map<OrderType, Map<Integer, List<Order>>> summaryOrders() {
		try {
			Map<Integer, List<Order>> sellSorted = new TreeMap<>(sellComarator);
			Map<Integer, List<Order>> sellUnsortedMap = ordersPersistance.stream()
					.filter(o->OrderType.SELL.equals(o.getOrderType()))
					.collect(Collectors.groupingBy(e-> ((Order)e).getPrice()));

			// map sorting naturally
			sellSorted.putAll(sellUnsortedMap);
			sellSorted.forEach((price, orders)-> System.out.println(OrderType.SELL + " "+ orders.stream().collect(Collectors.summingDouble(o->((Order)o).getQuantity())) + " kg for £"+ price));
				
			Map<Integer, List<Order>> buySorted = new TreeMap<>(buyComarator);
			Map<Integer, List<Order>> buyUnsortedMap = ordersPersistance.stream()
					.filter(o->OrderType.BUY.equals(o.getOrderType()))
					.collect(Collectors.groupingBy(e-> ((Order)e).getPrice()));
			// sort in natural 
			buySorted.putAll(buyUnsortedMap);
			buySorted.forEach((price, orders)-> System.out.println(OrderType.BUY + " "+ orders.stream().collect(Collectors.summingDouble(o->((Order)o).getQuantity())) + " kg for £"+ price));
			
			// this return is for testing purpose
			Map<OrderType, Map<Integer, List<Order>>> result = new HashMap<>(2);
			result.put(OrderType.SELL, sellSorted);
			result.put(OrderType.BUY, buySorted);
			return result;

		} catch (Exception e) {
			System.err.println("Exception occured at order summarization. "+ e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public int availableOrders() {
		return ordersPersistance.size();
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
