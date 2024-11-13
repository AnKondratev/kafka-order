package an.kondratev.payment.kafka;


import an.kondratev.payment.model.Order;
import an.kondratev.payment.service.OrderServiceInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final OrderServiceInterface orderService;

    @KafkaListener(topics = "new_orders", groupId = "my_consumer")
    public void listenOrder(String order) {
        try {
            Order newOrder = objectMapper.readValue(order, Order.class);
            orderService.saveOrder(newOrder);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Received Message: " + order);
    }
}