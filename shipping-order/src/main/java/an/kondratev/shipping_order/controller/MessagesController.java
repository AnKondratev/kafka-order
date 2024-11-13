package an.kondratev.shipping_order.controller;

import an.kondratev.shipping_order.kafka.KafkaProducer;
import an.kondratev.shipping_order.model.Order;
import an.kondratev.shipping_order.service.OrderServiceInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class MessagesController {

    private final KafkaProducer producer;
    private final OrderServiceInterface orderService;
    private final ObjectMapper objectMapper;

    @PostMapping("kafka/send_order/{id}")
    public String sendOrder(@PathVariable("id") Long id) {
        Order order = orderService.getOrderById(id);

        try {
            String jsonOrder = objectMapper.writeValueAsString(order);
            producer.sendOrder(jsonOrder);
            return "Order " + id + " sent successfully";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Failed to convert order to JSON";
        }
    }
}