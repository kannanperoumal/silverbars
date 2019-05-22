package org.silverbars.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Collection;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.silverbars.service.OrderService;
import org.silverbars.service.impl.OrderServiceImpl;
import org.silverbars.types.MergedOrder;
import org.silverbars.types.Order;
import org.silverbars.types.OrderException;
import org.silverbars.types.OrderType;

/**
 * @author kannan
 *
 */
class TestSwingUI {
	private OrderService orderService = new OrderServiceImpl();
	private JTextField txtUserId = new JTextField("UserId1", 10);
	private JTextField txtQty = new JTextField("5", 10);
	private JTextField txtPrice = new JTextField("50", 10);
	private JComboBox<OrderType> comOrderType = new JComboBox<>(new OrderType[] {OrderType.BUY, OrderType.SELL});
	private DefaultListModel<Order> orders = new DefaultListModel<>();
	private JList<Order> listOrder = new JList<>(orders);
	private JButton btnCreate = new JButton("Create");
	private JButton btnDelete = new JButton("Delete");
	private JButton btnSummarise = new JButton("Summarise");
	private Order selectedOrder;
	private int selectedIndex;
	private final JFrame frame;
	
	private final Object[] objBuyCol = new Object[]{"Buy", "No of orders", "Quantity", "Price"};
	private final Object[] objSelCol = new Object[]{"Sell", "No of orders", "Quantity", "Price"};
	
	private DefaultTableModel defBuy = new DefaultTableModel();
	private DefaultTableModel defSel = new DefaultTableModel();
	private JTable tabBuy = new JTable(defBuy);
	private JTable tabSell = new JTable(defSel);
	private JScrollPane scrollPaneBuy = new JScrollPane(tabBuy);
	private JScrollPane scrollPaneSell = new JScrollPane(tabSell);
	private JScrollPane scrollPaneOrder = new JScrollPane(listOrder);
	
	public TestSwingUI() {
		frame = new JFrame("Silver Bar Order Test");
		frame.setContentPane(getContentPane());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	private JPanel getContentPane() {
		defBuy.setColumnIdentifiers(objBuyCol);
		defSel.setColumnIdentifiers(objSelCol);
		btnDelete.setEnabled(false);
		
		initListeners();
		
		JPanel inputPanel =  new JPanel(new FlowLayout());
		inputPanel.add(upDown(new JLabel("Type: "), comOrderType));
		inputPanel.add(upDown(new JLabel("User id: "), txtUserId));
		inputPanel.add(upDown(new JLabel("Quanity: "), txtQty));
		inputPanel.add(upDown(new JLabel("Price: "), txtPrice));
		
		JPanel buttonPanel =  new JPanel(new FlowLayout());
		buttonPanel.add(btnCreate);
		buttonPanel.add(btnDelete);
		buttonPanel.add(btnSummarise);
		
		scrollPaneSell.setPreferredSize(new Dimension(350, 200));
		scrollPaneBuy.setPreferredSize(new Dimension(350, 200));
		JPanel extendedOrderPanel =  new JPanel(new FlowLayout());
		extendedOrderPanel.add(scrollPaneSell);
		extendedOrderPanel.add(scrollPaneBuy);
		
		JPanel orderPanel =  new JPanel(new FlowLayout());
		scrollPaneOrder.setPreferredSize(new Dimension(700, 300));
		orderPanel.add(scrollPaneOrder);
		
		JComponent c1 = upDown(inputPanel, buttonPanel);
		c1 = upDown(c1, extendedOrderPanel);
		c1 = upDown(c1, orderPanel);
		JPanel p =  new JPanel(new BorderLayout());
		p.add(c1, BorderLayout.CENTER);
		return p;
	}
	
	private void initListeners() {
		btnCreate.addActionListener(actionEvent -> {
			try {
				orders.addElement(orderService.registerOrder(toNewOrder()));
				updateDashBoard();
			} catch (OrderException e) {
				e.printStackTrace();
			}
		});
		
		btnDelete.addActionListener(actionEvent -> {
			try {
				if(orderService.cancelOrder(selectedOrder)!=null) { 
					listOrder.clearSelection();
					orders.removeElementAt(selectedIndex);
					updateDashBoard();
					updateButtons(false);
				}
			} catch (OrderException e) {
				e.printStackTrace();
			}
		});
		btnSummarise.addActionListener(actionEvent -> {
			try {
				final StringBuilder sb = new StringBuilder();
				orderService.summaryOrders().forEach((k, v)-> v.forEach(mo->sb.append(mo).append("\n")));
				JOptionPane.showMessageDialog(frame, sb.toString());
			} catch (OrderException e) {
				e.printStackTrace();
			}
		});
		listOrder.addListSelectionListener(listSelectionEvent -> toUI(listOrder.getSelectedValue(), listOrder.getSelectedIndex()));
	}
	
	private JComponent upDown(JComponent c1, JComponent c2) {
		JPanel p =  new JPanel(new BorderLayout());
		p.add(c1, BorderLayout.NORTH);
		p.add(c2, BorderLayout.CENTER);
		return p;
	}
	
	private Order toNewOrder() {
		Order o = new Order(txtUserId.getText().trim(), 
				Double.parseDouble(txtQty.getText().trim()), 
				Integer.parseInt(txtPrice.getText().trim()), 
				(OrderType) comOrderType.getSelectedItem());
		updateButtons(false);
		return o;
	}
	
	private void toUI(Order o, int index) {
		if(o==null)return;
		txtUserId.setText(o.getUserid());
		txtQty.setText(String.valueOf(o.getQuantity()));
		txtPrice.setText(String.valueOf(o.getPrice()));
		comOrderType.setSelectedItem(o.getOrderType());
		selectedOrder = o;
		selectedIndex = index;
		updateButtons(true);
	}
	
	private void updateButtons(boolean b) {
		btnDelete.setEnabled(b);
		listOrder.repaint();
	}
	
	private void updateDashBoard() throws OrderException {
		for(Entry<OrderType, Collection<MergedOrder>> e : orderService.summaryOrders().entrySet()) {
			DefaultTableModel model = OrderType.BUY.equals(e.getKey())? defBuy : defSel;
			if(e.getValue()==null || e.getValue().isEmpty()) {
				model.getDataVector().removeAllElements();
				model.fireTableDataChanged();
				continue;
			}
			Object[] col = OrderType.BUY.equals(e.getKey())? objBuyCol : objSelCol;
			Object[][] data = toObject(e.getValue());
			model.setDataVector(data, col);
			model.fireTableDataChanged();
		}
	}
	
	private Object[][] toObject(Collection<MergedOrder> data) {
		Object[][] o = new Object[data.size()][5];
		int i=0;
		for(MergedOrder mo : data) {
			o[i++]=new Object[] {mo.getOrderType(), mo.getNumberOfOrders(), mo.getQuantity(), mo.getPrice()};
		}
		return o;
	}
	
}
public class TestGUI {
	public static void main(String args[]) {
		new TestSwingUI();
	}
}
