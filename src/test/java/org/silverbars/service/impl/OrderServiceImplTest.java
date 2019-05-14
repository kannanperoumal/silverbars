package org.silverbars.service.impl;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.silverbars.service.OrderService;
import org.silverbars.types.Order;
import org.silverbars.types.OrderType;

public class OrderServiceImplTest {
	private OrderService os;
	
	@Before
	public void before() throws Exception {
		os = new OrderServiceImpl();
		os.registerOrder(new Order("user1", 1.5, 500, OrderType.SELL));
		os.registerOrder(new Order("user2", 2.5, 700, OrderType.SELL));
		os.registerOrder(new Order("user3", 1.5, 600, OrderType.BUY));
		os.registerOrder(new Order("user4", 2.5, 750, OrderType.BUY));
	}
	
	@Test
	public void testOrdersCreation() throws Exception {
		Assert.assertSame("It should return 4", 4, os.availableOrders());
		os.registerOrder(new Order("user11", 3.5, 1000, OrderType.SELL));
		Assert.assertSame("It should return 5", 5, os.availableOrders());
	}
	
	@Test
	public void testOrdersDeletion() throws Exception {
		Assert.assertSame("It should return 4", 4, os.availableOrders());
		os.cancelOrder(new Order("user2", 2.5, 700, OrderType.SELL));
		Assert.assertSame("It should return 3", 3, os.availableOrders());
	}
	
	@Test
	public void testOrdersMergeByPrice() throws Exception {
		Assert.assertSame("It should return 4", 4, os.availableOrders());
		os.registerOrder(new Order("user5", 1.5, 500, OrderType.SELL));
		Assert.assertSame("It should return 5", 5, os.availableOrders());
		Map<OrderType, Map<Integer, List<Order>>> summary = os.summaryOrders();
		Map<Integer, List<Order>> sellMap = summary.get(OrderType.SELL);
		int actualOrders = sellMap.get(500).size();
		Assert.assertTrue("The expected orders of sell are 2", 2==actualOrders);
	}
}
