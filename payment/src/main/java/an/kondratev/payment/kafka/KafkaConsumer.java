package an.kondratev.payment.kafka;


import an.kondratev.payment.model.Order;
import an.kondratev.payment.service.OrderServiceInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @KafkaListener(topics = "new_orders", groupId = "my_consumer", concurrency = "5")
    public void listenOrder(String order) {
        try {
            Order newOrder = objectMapper.readValue(order, Order.class);
            orderService.saveOrder(newOrder);
            log.info("Received Order: {}", newOrder);
        } catch (JsonProcessingException e) {
            log.error("Error processing order: {}", order, e);
            throw new RuntimeException(e);
        }
        log.info("Received Message: {}", order);
    }
}
