package an.kondratev.payment.service;

import an.kondratev.payment.model.Order;
import an.kondratev.payment.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class OrderService implements OrderServiceInterface {

    private OrderRepository repository;

    @Override
    public Order saveOrder(Order order) {
        log.info("Saving order: {}", order);
        return repository.save(order);
    }

    @Override
    public Order getOrderById(Long orderId) {
        log.info("Retrieving order by id: {}", orderId);
        return repository.getOrderByOrderId(orderId);
    }

    @Override
    public String updateOrder(Long orderId) {

        Order order = repository.getOrderByOrderId(orderId);
        if (order == null) {
            log.error("Order not found: {}", orderId);
            throw new NullPointerException("Order not found");
        }
        order.setPaymentStatus(true);
        log.info("Updating order: {}", orderId);
        repository.save(order);
        return "Order #" + orderId + " updated";
    }
}
