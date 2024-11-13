package an.kondratev.payment.service;

import an.kondratev.payment.model.Order;
import an.kondratev.payment.repository.OrderRepository;
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
        order.setPaymentStatus(true);
        repository.save(order);
        return "Order #" + orderId + " updated";
    }

}
