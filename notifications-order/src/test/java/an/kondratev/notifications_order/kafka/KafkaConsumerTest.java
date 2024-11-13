package an.kondratev.notifications_order.kafka;

import an.kondratev.notifications_order.model.Order;
import an.kondratev.notifications_order.service.OrderServiceInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class KafkaConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private OrderServiceInterface orderService;

    @InjectMocks
    private KafkaConsumer kafkaConsumer;

    private static final String VALID_ORDER_JSON = "{" +
            "\"orderId\":1," +
            "\"customer\":\"John Doe\"," +
            "\"totalPrice\":100.00," +
            "\"orderStatus\":\"NEW\"," +
            "\"paymentStatus\":true}";
    private static final String INVALID_ORDER_JSON = "invalid json";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListenOrderSuccessfully() throws JsonProcessingException {
        Order expectedOrder = Order.builder()
                .orderId(1L)
                .customer("John Doe")
                .totalPrice(BigDecimal.valueOf(100.00))
                .orderStatus("NEW")
                .paymentStatus(true)
                .build();

        when(objectMapper.readValue(VALID_ORDER_JSON, Order.class)).thenReturn(expectedOrder);

        kafkaConsumer.listenOrder(VALID_ORDER_JSON);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderService, times(1)).saveOrder(orderCaptor.capture());
        assertEquals(expectedOrder, orderCaptor.getValue());
    }

    @Test
    void testListenOrderHandlesJsonProcessingException() throws JsonProcessingException {
        when(objectMapper.readValue(VALID_ORDER_JSON, Order.class))
                .thenThrow(new JsonProcessingException("Error processing JSON") {});

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            kafkaConsumer.listenOrder(VALID_ORDER_JSON);
        });

        verify(orderService, never()).saveOrder(any());
        assertInstanceOf(JsonProcessingException.class, thrown.getCause());
    }
}