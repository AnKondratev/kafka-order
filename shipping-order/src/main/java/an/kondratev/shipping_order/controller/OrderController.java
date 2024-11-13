package an.kondratev.shipping_order.controller;

import an.kondratev.shipping_order.model.Order;
import an.kondratev.shipping_order.service.OrderServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class OrderController {

    private final OrderServiceInterface orderService;

    @PutMapping("ship/{id}")
    public String shipOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return "Заказ не найден!";
        }
        return orderService.updateOrder(order.getOrderId());
    }
}
