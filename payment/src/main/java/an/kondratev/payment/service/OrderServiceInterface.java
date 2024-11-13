package an.kondratev.payment.service;

import an.kondratev.payment.model.Order;

public interface OrderServiceInterface {

    Order saveOrder(Order order);

    Order getOrderById(Long orderId);


    String updateOrder(Long orderId);
}
