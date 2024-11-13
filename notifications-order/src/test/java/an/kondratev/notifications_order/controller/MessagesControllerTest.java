package an.kondratev.notifications_order.controller;

import an.kondratev.notifications_order.kafka.KafkaProducer;
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

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MessagesControllerTest {

    @InjectMocks
    private MessagesController messagesController;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private OrderServiceInterface orderService;

    @Mock
    private ObjectMapper objectMapper;

    private Order order;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        order = new Order(1L,
                "John Doe",
                BigDecimal.valueOf(100.00),
                "PROCESSING",
                true);
    }

    @Test
    public void testSendOrder_Success() throws JsonProcessingException, ExecutionException, InterruptedException {
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(objectMapper.writeValueAsString(order)).thenReturn("{" +
                "\"orderId\":1," +
                "\"customer\":\"John Doe\"," +
                "\"totalPrice\":100.00," +
                "\"orderStatus\":\"PROCESSING\"," +
                "\"paymentStatus\":true}");

        String result = messagesController.sendOrder(1L);

        assertEquals("Order 1 sent successfully", result);

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaProducer, times(1))
                .sandMessageWithRetry(argumentCaptor.capture(), eq(5));
        assertEquals("{" +
                "\"orderId\":1," +
                "\"customer\":\"John Doe\"," +
                "\"totalPrice\":100.00," +
                "\"orderStatus\":\"PROCESSING\"" +
                ",\"paymentStatus\":true}", argumentCaptor.getValue());
    }

    @Test
    public void testSendOrder_OrderNotFound() {
        when(orderService.getOrderById(99L)).thenReturn(null);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> messagesController.sendOrder(99L));
        assertEquals("Order not found", thrown.getMessage());

        try {
            verify(kafkaProducer, never()).sandMessageWithRetry(anyString(), anyInt());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSendOrder_JsonProcessingException()
            throws JsonProcessingException, ExecutionException, InterruptedException {
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(objectMapper.writeValueAsString(order)).thenThrow(new JsonProcessingException("Error") {
        });

        String result = messagesController.sendOrder(1L);

        assertEquals("Failed to convert order to JSON", result);
        verify(kafkaProducer, never()).sandMessageWithRetry(anyString(), anyInt());
    }

    @Test
    public void testSendOrder_ExecutionException()
            throws JsonProcessingException, ExecutionException, InterruptedException {
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(objectMapper.writeValueAsString(order)).thenReturn("{" +
                "\"orderId\":1," +
                "\"customer\":\"John Doe\"," +
                "\"totalPrice\":100.00," +
                "\"orderStatus\":\"PROCESSING\"," +
                "\"paymentStatus\":true}");
        doThrow(new ExecutionException("Execution problem",
                new Throwable())).when(kafkaProducer).sandMessageWithRetry(anyString(), anyInt());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> messagesController.sendOrder(1L));
        assertNotNull(thrown);
        assertNotNull(thrown.getCause());
        assertEquals("Execution problem", thrown.getCause().getMessage());
    }

    @Test
    public void testSendOrder_InterruptedException()
            throws JsonProcessingException, ExecutionException, InterruptedException {
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(objectMapper.writeValueAsString(order)).thenReturn("{" +
                "\"orderId\":1," +
                "\"customer\":\"John Doe\"," +
                "\"totalPrice\":100.00," +
                "\"orderStatus\":\"PROCESSING\"," +
                "\"paymentStatus\":true}");
        doThrow(new InterruptedException("Interrupted problem"))
                .when(kafkaProducer).sandMessageWithRetry(anyString(), anyInt());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> messagesController.sendOrder(1L));
        assertNotNull(thrown);
        assertNotNull(thrown.getCause());
        assertEquals("Interrupted problem", thrown.getCause().getMessage());
    }
}

