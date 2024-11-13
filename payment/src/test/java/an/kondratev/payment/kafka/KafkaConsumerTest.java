package an.kondratev.payment.kafka;

import an.kondratev.payment.model.Order;
import an.kondratev.payment.service.OrderServiceInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaConsumerTest {

    @InjectMocks
    private KafkaConsumer kafkaConsumer;

    @Mock
    private OrderServiceInterface orderService;

    @Mock
    private ObjectMapper objectMapper;

    private Order order;

    @BeforeEach
    public void setUp() {
        order = Order.builder()
                .orderId(1L)
                .customer("John Doe")
                .totalPrice(new BigDecimal("99.99"))
                .orderStatus("NEW")
                .paymentStatus(true)
                .build();
    }

    @Test
    public void testListenOrder_Success() throws JsonProcessingException {
        String orderJson = "{\"orderId\":1," +
                "\"customer\":\"John Doe\"," +
                "\"totalPrice\":99.99," +
                "\"orderStatus\":\"NEW\"," +
                "\"paymentStatus\":true}";
        when(objectMapper.readValue(orderJson, Order.class)).thenReturn(order);

        kafkaConsumer.listenOrder(orderJson);

        verify(orderService, times(1)).saveOrder(order);
    }

    @Test
    public void testListenOrder_JsonProcessingException() throws JsonProcessingException {
        String orderJson = "{\"orderId\":1," +
                "\"customer\":" +
                "\"John Doe\"," +
                "\"totalPrice\":99.99," +
                "\"orderStatus\":\"NEW\"," +
                "\"paymentStatus\":true}";
        when(objectMapper.readValue(orderJson, Order.class))
                .thenThrow(new JsonProcessingException("Error processing JSON") {
                });

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            kafkaConsumer.listenOrder(orderJson);
        });

        verify(orderService, never()).saveOrder(any());
    }
}