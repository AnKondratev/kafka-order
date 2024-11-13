package an.kondratev.payment.controller;

import an.kondratev.payment.model.Order;
import an.kondratev.payment.service.OrderServiceInterface;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/")
public class OrderController {

    private final OrderServiceInterface orderService;

    @GetMapping("get_order/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            log.error("Order #{}not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        log.info("Get order with id {}", id);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PutMapping("pay/{id}")
    public String payOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            log.error("An error occurred while trying to pay for the order, order#{} not found", id);
            return "Заказ не найден!";
        }
        log.info("Order #{} payed", id);
        return orderService.updateOrder(order.getOrderId());
    }
}
