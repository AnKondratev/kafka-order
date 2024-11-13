package an.kondratev.shipping_order.kafka;


import an.kondratev.shipping_order.model.Order;
import an.kondratev.shipping_order.service.OrderServiceInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final OrderServiceInterface orderService;

    @KafkaListener(topics = "payed_orders", groupId = "my_consumer", concurrency = "5")
    public void listenOrder(String order) {
        try {
            Order newOrder = objectMapper.readValue(order, Order.class);
            log.info("New order: {}", newOrder);
            orderService.saveOrder(newOrder);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        log.info("Received Message: {}", order);
    }
}
