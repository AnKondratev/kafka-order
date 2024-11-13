package an.kondratev.notifications_order.controller;

import an.kondratev.notifications_order.model.Order;
import an.kondratev.notifications_order.service.OrderServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class OrderController {

    private final OrderServiceInterface orderService;

    @PutMapping("notification/{id}")
    public String shipOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return "Заказ не найден!";
        }
        return orderService.updateOrder(order.getOrderId());
    }
}
