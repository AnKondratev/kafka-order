package an.kondratev.notifications_order.service;

import an.kondratev.notifications_order.model.Order;
import an.kondratev.notifications_order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testOrder = Order.builder()
                .orderId(1L)
                .customer("John Doe")
                .totalPrice(BigDecimal.valueOf(100.00))
                .orderStatus("Pending")
                .paymentStatus(false)
                .build();
    }

    @Test
    void testSaveOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order savedOrder = orderService.saveOrder(testOrder);

        assertEquals(testOrder, savedOrder);
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    void testGetOrderById() {
        when(orderRepository.getOrderByOrderId(1L)).thenReturn(testOrder);

        Order foundOrder = orderService.getOrderById(1L);

        assertEquals(testOrder, foundOrder);
        verify(orderRepository, times(1)).getOrderByOrderId(1L);
    }

    @Test
    void testUpdateOrder() {
        when(orderRepository.getOrderByOrderId(1L)).thenReturn(testOrder);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        String updateMessage = orderService.updateOrder(1L);

        assertEquals("Order status #1 updated, Order delivered!", updateMessage);
        assertEquals("Delivered!", testOrder.getOrderStatus());
        verify(orderRepository, times(1)).getOrderByOrderId(1L);
        verify(orderRepository, times(1)).save(testOrder);
    }
}