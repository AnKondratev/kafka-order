package an.kondratev.notifications_order.service;


import an.kondratev.notifications_order.model.Order;

public interface OrderServiceInterface {

    Order saveOrder(Order order);

    Order getOrderById(Long orderId);


    String updateOrder(Long orderId);
}
