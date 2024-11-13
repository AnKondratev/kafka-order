package an.kondratev.shipping_order.controller;

import an.kondratev.shipping_order.model.Order;
import an.kondratev.shipping_order.service.OrderServiceInterface;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/")
@AllArgsConstructor
public class OrderController {

    private final OrderServiceInterface orderService;

    @PutMapping("ship/{id}")
    public String shipOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            log.error("Order #{} not found", id);
            return "Заказ не найден!";
        }
        log.info("Order #{} shipped", id);
        return orderService.updateOrder(order.getOrderId());
    }
}
