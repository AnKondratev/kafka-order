package an.kondratev.payment.controller;

import an.kondratev.payment.model.Order;
import an.kondratev.payment.service.OrderServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/")
public class OrderController {

    private final OrderServiceInterface orderService;

    @GetMapping("get_order/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return new ResponseEntity<>(orderService.getOrderById(id), HttpStatus.OK);
    }

    @PutMapping("pay/{id}")
    public String payOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return "Заказ не найден!";
        }
        return orderService.updateOrder(order.getOrderId());
    }
}
