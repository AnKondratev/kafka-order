package an.kondratev.payment.controller;

import an.kondratev.payment.kafka.KafkaProducer;
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
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendControllerTest {

    @Mock
    private KafkaProducer producer;

    @Mock
    private OrderServiceInterface orderService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private SendController sendController;

    private Order order;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .orderId(1L)
                .customer("John Doe")
                .totalPrice(BigDecimal.valueOf(100.0))
                .orderStatus("NEW")
                .paymentStatus(true)
                .build();
    }

    @Test
    void testSendOrder_Success() throws JsonProcessingException, InterruptedException, ExecutionException {
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(objectMapper.writeValueAsString(order)).thenReturn("{\"orderId\":1,\"customer\":\"John Doe\"}");

        String response = sendController.sendOrder(1L);

        assertEquals("Order 1 sent successfully", response);
        verify(producer, times(1)).sandMessageWithRetry("{\"orderId\":1,\"customer\":\"John Doe\"}", 5);
    }

    @Test
    void testSendOrder_OrderNotFound() {
        when(orderService.getOrderById(2L)).thenReturn(null);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> sendController.sendOrder(2L));
        assertEquals("Order #2 not found", thrown.getMessage());
    }

    @Test
    void testSendOrder_JsonProcessingException() throws JsonProcessingException, ExecutionException, InterruptedException {
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(objectMapper.writeValueAsString(order)).thenThrow(new JsonProcessingException("JSON error") {
        });

        String response = sendController.sendOrder(1L);

        assertEquals("Failed to convert order to JSON", response);
        verify(producer, never()).sandMessageWithRetry(any(), anyInt());
    }

    @Test
    void testSendOrder_ExecutionException() throws JsonProcessingException, InterruptedException, ExecutionException {
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(objectMapper.writeValueAsString(order)).thenReturn("{\"orderId\":1,\"customer\":\"John Doe\"}");
        doThrow(new ExecutionException(new Throwable("Execution error"))).when(producer).sandMessageWithRetry(any(), anyInt());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> sendController.sendOrder(1L));

        assertInstanceOf(ExecutionException.class, thrown.getCause());
        assertTrue(thrown.getCause().getMessage().contains("Execution error"));
    }
}