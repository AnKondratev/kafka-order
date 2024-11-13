package an.kondratev.notifications_order.controller;

import an.kondratev.notifications_order.model.Order;
import an.kondratev.notifications_order.service.OrderServiceInterface;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/")
@AllArgsConstructor
public class OrderController {

    private final OrderServiceInterface orderService;

    @PutMapping("notification/{id}")
    public String shipOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            log.error("Order #{} not found", id);
            return "Order not found";
        }
        log.info("Order #{} shipped", id);
        return orderService.updateOrder(order.getOrderId());
    }
}
