package an.kondratev.shipping_order.controller;

import an.kondratev.shipping_order.kafka.KafkaProducer;
import an.kondratev.shipping_order.model.Order;
import an.kondratev.shipping_order.service.OrderServiceInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class MessagesControllerTest {

    @Mock
    private KafkaProducer producer;

    @Mock
    private OrderServiceInterface orderService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MessagesController messagesController;

    private Order testOrder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        testOrder = Order.builder()
                .orderId(1L)
                .customer("John Doe")
                .totalPrice(new BigDecimal("100.00"))
                .orderStatus("NEW")
                .paymentStatus(false)
                .build();
    }

    @Test
    public void testSendOrder_Success() throws JsonProcessingException, ExecutionException, InterruptedException {
        when(orderService.getOrderById(anyLong())).thenReturn(testOrder);
        when(objectMapper.writeValueAsString(testOrder)).thenReturn("{\"orderId\":1, \"customer\":\"John Doe\"}");

        String response = messagesController.sendOrder(1L);

        assertEquals("Order 1 sent successfully", response);
        verify(producer).sandMessageWithRetry("{\"orderId\":1, \"customer\":\"John Doe\"}", 5);
    }

    @Test
    public void testSendOrder_OrderNotFound() {
        when(orderService.getOrderById(anyLong())).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            messagesController.sendOrder(1L);
        });

        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    public void testSendOrder_JsonProcessingException() throws JsonProcessingException, ExecutionException, InterruptedException {
        when(orderService.getOrderById(anyLong())).thenReturn(testOrder);
        when(objectMapper.writeValueAsString(testOrder)).thenThrow(new JsonProcessingException("Error") {});

        String response = messagesController.sendOrder(1L);

        assertEquals("Failed to convert order to JSON", response);
        verify(producer, never()).sandMessageWithRetry(anyString(), anyInt());
    }

    @Test
    public void testSendOrder_ExecutionException() throws JsonProcessingException, ExecutionException, InterruptedException {
        when(orderService.getOrderById(anyLong())).thenReturn(testOrder);
        when(objectMapper.writeValueAsString(testOrder)).thenReturn("{\"orderId\":1, \"customer\":\"John Doe\"}");
        doThrow(new ExecutionException("Execution error", new Throwable())).when(producer).sandMessageWithRetry(anyString(), anyInt());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            messagesController.sendOrder(1L);
        });

        assertNotNull(exception);
        assertEquals("java.util.concurrent.ExecutionException: Execution error", exception.getMessage());
    }

    @Test
    public void testSendOrder_InterruptedException() throws JsonProcessingException, ExecutionException, InterruptedException {
        when(orderService.getOrderById(anyLong())).thenReturn(testOrder);
        when(objectMapper.writeValueAsString(testOrder)).thenReturn("{\"orderId\":1, \"customer\":\"John Doe\"}");
        doThrow(new InterruptedException("Interrupted")).when(producer).sandMessageWithRetry(anyString(), anyInt());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            messagesController.sendOrder(1L);
        });

        assertNotNull(exception);
        assertInstanceOf(InterruptedException.class, exception.getCause());
    }
}