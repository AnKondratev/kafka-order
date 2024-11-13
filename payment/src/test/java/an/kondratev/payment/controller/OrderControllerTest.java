package an.kondratev.payment.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.math.BigDecimal;

import an.kondratev.payment.model.Order;
import an.kondratev.payment.service.OrderServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderServiceInterface orderService;

    private Order order;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        order = Order.builder()
                .orderId(1L)
                .customer("John Doe")
                .totalPrice(new BigDecimal("100.00"))
                .orderStatus("CREATED")
                .paymentStatus(false)
                .build();
    }

    @Test
    public void testGetOrder_Success() {
        when(orderService.getOrderById(anyLong())).thenReturn(order);

        ResponseEntity<Order> response = orderController.getOrder(1L);

        assertEquals(OK, response.getStatusCode());
        assertEquals(order, response.getBody());
        verify(orderService).getOrderById(1L);
    }

    @Test
    public void testGetOrder_NotFound() {
        when(orderService.getOrderById(anyLong())).thenReturn(null);

        ResponseEntity<Order> response = orderController.getOrder(999L);

        assertEquals(NOT_FOUND, response.getStatusCode());
        verify(orderService).getOrderById(999L);
    }

    @Test
    public void testPayOrder_Success() {
        when(orderService.getOrderById(anyLong())).thenReturn(order);
        when(orderService.updateOrder(order.getOrderId())).thenReturn("Payment Successful");

        String response = orderController.payOrder(1L);

        assertEquals("Payment Successful", response);
        verify(orderService).getOrderById(1L);
        verify(orderService).updateOrder(order.getOrderId());
    }

    @Test
    public void testPayOrder_NotFound() {
        when(orderService.getOrderById(anyLong())).thenReturn(null);

        String response = orderController.payOrder(999L);

        assertEquals("Заказ не найден!", response);
        verify(orderService).getOrderById(999L);
        verify(orderService, never()).updateOrder(anyLong());
    }
}