package an.kondratev.notifications_order.controller;

import an.kondratev.notifications_order.kafka.KafkaProducer;
import an.kondratev.notifications_order.model.Order;
import an.kondratev.notifications_order.service.OrderServiceInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@AllArgsConstructor
public class MessagesController {

    private final KafkaProducer producer;
    private final OrderServiceInterface orderService;
    private final ObjectMapper objectMapper;

    @PostMapping("kafka/send_order/{id}")
    public String sendOrder(@PathVariable("id") Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            log.error("Order #{} not found", id);
            throw new RuntimeException("Order not found");
        }
        try {
            String jsonOrder = objectMapper.writeValueAsString(order);
            producer.sandMessageWithRetry(jsonOrder, 5);
            log.info("Order sent successfully");
            return "Order " + id + " sent successfully";
        } catch (JsonProcessingException e) {
            log.error("Failed to convert order to JSON: {}", e.getMessage());
            return "Failed to convert order to JSON";
        } catch (ExecutionException | InterruptedException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}