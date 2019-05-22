package org.silverbars.service.impl;

import java.util.Collection;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.silverbars.service.OrderService;
import org.silverbars.types.MergedOrder;
import org.silverbars.types.Order;
import org.silverbars.types.OrderException;
import org.silverbars.types.OrderType;

public class OrderServiceImplTest {
	private static OrderService orderService;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		orderService = new OrderServiceImpl();
		orderService.registerOrder(new Order("user1", 1.5, 500, OrderType.SELL));
		orderService.registerOrder(new Order("user2", 2.5, 700, OrderType.SELL));
		orderService.registerOrder(new Order("user3", 1.5, 600, OrderType.BUY));
		orderService.registerOrder(new Order("user4", 2.5, 750, OrderType.BUY));
	}
	
	@AfterClass
	public static void afterClass() throws Exception {
	}
	
	@Before
	public void before() throws Exception {
	}
	
	@After
	public void after() throws Exception {
	}
	
	@Test
	public void testOrder_nonMerge() throws Exception {
		Map<OrderType, Collection<MergedOrder>> map = orderService.summaryOrders();
		Assert.assertEquals("Sell orders size check",  2, map.get(OrderType.SELL).size());
		Assert.assertEquals("Buy orders size check",  2, map.get(OrderType.BUY).size());
		
		// create new order
		Order o1 = new Order("user5", 3.5, 850, OrderType.SELL);
		Order o2 = new Order("user6", 3.5, 850, OrderType.BUY);
		orderService.registerOrder(o1);
		orderService.registerOrder(o2);
		
		map = orderService.summaryOrders();
		Assert.assertEquals("Sell orders size check",  3, map.get(OrderType.SELL).size());
		Assert.assertEquals("Buy orders size check",  3, map.get(OrderType.BUY).size());
		
		orderService.cancelOrder(o1);
		orderService.cancelOrder(o2);
	}
	
	@Test
	public void testOrder_merge() throws Exception {
		Map<OrderType, Collection<MergedOrder>> map = orderService.summaryOrders();
		Assert.assertEquals("Sell orders size check",  2, map.get(OrderType.SELL).size());
		Assert.assertEquals("Buy orders size check",  2, map.get(OrderType.BUY).size());

		final double quantity1 = 3.5;
		final int price = 1000;
		Order o1 = new Order("user5", quantity1, price, OrderType.SELL);
		orderService.registerOrder(o1);
		map = orderService.summaryOrders();
		Assert.assertEquals("Sell orders size check",  3, map.get(OrderType.SELL).size());
		
		MergedOrder mergedOrder = map.get(OrderType.SELL).stream().filter(mo->mo.getPrice()==price).findFirst().orElse(null);
		Assert.assertTrue("Quantity check",  quantity1 == mergedOrder.getQuantity());
		
		// create new order with the same price of previous order
		final double quantity2 = 6.5;
		Order o2 = new Order("user6", quantity2, price, OrderType.SELL);
		orderService.registerOrder(o2);
		map = orderService.summaryOrders();
		Assert.assertEquals("Sell orders size check",  3, map.get(OrderType.SELL).size());
		
		// check quantity of newly created order
		mergedOrder = map.get(OrderType.SELL).stream().filter(mo->mo.getPrice()==price).findFirst().orElse(null);
		Assert.assertTrue("Merged case quantity check",  (quantity1+quantity2) == mergedOrder.getQuantity());
		
		// delete the merge orders
		orderService.cancelOrder(o1);
		orderService.cancelOrder(o2);
		
		// Expect null as result on price 1000
		mergedOrder = map.get(OrderType.SELL).stream().filter(mo->mo.getPrice()==price).findFirst().orElse(null);
		Assert.assertNull("MergedOrder Null check as it is removed",  mergedOrder);
	}
	
	@Test
	public void testOrder_exception() throws Exception {
		OrderException oe=null;
		try {
			orderService.registerOrder(new Order("", 11.5, 1100, null));
		} catch(OrderException ex) {
			oe = ex;
		}
		String errMessage = "Order type is null\nOrder user id not supplied\n";
		Assert.assertNotNull("Exception not null check",  oe);
		Assert.assertTrue("Exception message check",  oe.getMessage().contains(errMessage));
	}
	
}
