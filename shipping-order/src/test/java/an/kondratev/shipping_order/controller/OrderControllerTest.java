package an.kondratev.shipping_order.controller;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import an.kondratev.shipping_order.model.Order;
import an.kondratev.shipping_order.service.OrderServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderServiceInterface orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .orderId(1L)
                .customer("Test Customer")
                .totalPrice(BigDecimal.valueOf(100))
                .orderStatus("Pending")
                .paymentStatus(false)
                .build();
    }

    @Test
    void whenOrderExists_thenShipOrderShouldReturnSuccessMessage() {
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(orderService.updateOrder(1L)).thenReturn("Заказ отправлен!");

        String response = orderController.shipOrder(1L);

        verify(orderService).getOrderById(1L);
        verify(orderService).updateOrder(1L);
        assertThat(response).isEqualTo("Заказ отправлен!");
    }

    @Test
    void whenOrderDoesNotExist_thenShipOrderShouldReturnErrorMessage() {
        when(orderService.getOrderById(2L)).thenReturn(null);

        String response = orderController.shipOrder(2L);

        verify(orderService).getOrderById(2L);
        verify(orderService, never()).updateOrder(anyLong());
        assertThat(response).isEqualTo("Заказ не найден!");
    }

}