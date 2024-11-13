package an.kondratev.shipping_order.service;


import an.kondratev.shipping_order.model.Order;
import an.kondratev.shipping_order.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderService implements OrderServiceInterface {

    private OrderRepository repository;

    @Override
    public Order saveOrder(Order order) {
        return repository.save(order);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return repository.getOrderByOrderId(orderId);
    }



    @Override
    public String updateOrder(Long orderId) {
        Order order = repository.getOrderByOrderId(orderId);
        order.setOrderStatus("Deliverable");
        repository.save(order);
        return "Order status #" + orderId + " updated";
    }
}
