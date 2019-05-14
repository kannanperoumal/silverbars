package org.silverbars.types;

/**
 * @author kannan 
 *
 */
public final class Order implements Comparable<Order>{
	private String userid;
	private double quantity;
	private int price;
	private OrderType orderType;
	
	public Order(String userid, double quantity, int price, OrderType orderType) {
		this.userid = userid;
		this.quantity = quantity;
		this.price = price;
		this.orderType = orderType;
	}

	public String getUserid() {
		return userid;
	}
	public double getQuantity() {
		return quantity;
	}
	public int getPrice() {
		return price;
	}
	public OrderType getOrderType() {
		return orderType;
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
		result = prime * result + ((userid == null) ? 0 : userid.hashCode());
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
		Order other = (Order) obj;
		if (orderType != other.orderType)
			return false;
		if (price != other.price)
			return false;
		if (Double.doubleToLongBits(quantity) != Double.doubleToLongBits(other.quantity))
			return false;
		if (userid == null) {
			if (other.userid != null)
				return false;
		} else if (!userid.equals(other.userid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Order [userid=" + userid + ", quantity=" + quantity + ", price=" + price + ", orderType=" + orderType
				+ "]";
	}

	@Override
	public int compareTo(Order o) {
		if(OrderType.SELL.equals(this.orderType)) {
			return o.price - this.price;
		} else if(OrderType.BUY.equals(this.orderType)) {
			return this.price - o.price;
		}
		return 0;
	}
}
