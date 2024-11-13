package an.kondratev.shipping_order.service;


import an.kondratev.shipping_order.model.Order;
import an.kondratev.shipping_order.repository.OrderRepository;
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
        order.setOrderStatus("Deliverable");
        log.info("Updating order: {}", order);
        repository.save(order);
        return "Order status #" + orderId + " updated";
    }
}
