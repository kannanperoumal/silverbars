package org.silverbars.types;

import java.util.LinkedList;
import java.util.List;

/**
 * @author kannan
 *
 */
public final class MergedOrder implements Comparable<MergedOrder>{
	private int price;
	private double quantity;
	private OrderType orderType;
	private List<Order> orders;

	public MergedOrder(final Order order) {
		this.price = order.getPrice();
		this.quantity = order.getQuantity();
		this.orderType = order.getOrderType();
		this.orders = new LinkedList<>();
		this.orders.add(order);
	}
	public int getPrice() {
		return price;
	}
	public double getQuantity() {
		return quantity;
	}
	public OrderType getOrderType() {
		return orderType;
	}
	public int getNumberOfOrders() {
		return orders.size();
	}
	
	public void merge(final Order order) {
		this.quantity += order.getQuantity();
		this.orders.add(order);
	}
	
	public void remove(final Order order) {
		this.quantity -= order.getQuantity();
		this.orders.remove(order);
	}
	
	@Override
	public int compareTo(MergedOrder order) {
		if(OrderType.SELL.equals(this.orderType)) {
			return order.price - this.price;
		} else if(OrderType.BUY.equals(this.orderType)) {
			return this.price - order.price;
		}
		return 0;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((orderType == null) ? 0 : orderType.hashCode());
		result = prime * result + price;
		long temp;
		temp = Double.doubleToLongBits(quantity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MergedOrder other = (MergedOrder) obj;
		if (orderType != other.orderType)
			return false;
		if (price != other.price)
			return false;
		if (Double.doubleToLongBits(quantity) != Double.doubleToLongBits(other.quantity))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return orderType + ": "+ quantity +" kg for " + price + " GBP  "+ orders.size() + " order(s)";
	}
}
