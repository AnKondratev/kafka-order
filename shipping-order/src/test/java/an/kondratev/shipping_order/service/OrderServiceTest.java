package an.kondratev.shipping_order.service;

import an.kondratev.shipping_order.model.Order;
import an.kondratev.shipping_order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveOrder_ShouldSaveAndReturnOrder() {
        Order order = new Order(
                1L,
                "John Doe",
                BigDecimal.valueOf(100.0),
                "Pending",
                false);
        when(orderRepository.save(order)).thenReturn(order);

        Order savedOrder = orderService.saveOrder(order);

        assertNotNull(savedOrder);
        assertEquals("John Doe", savedOrder.getCustomer());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void getOrderById_ShouldReturnOrder_WhenOrderExists() {
        Long orderId = 1L;
        Order order = new Order(
                orderId,
                "John Doe",
                BigDecimal.valueOf(100.0),
                "Pending",
                false);
        when(orderRepository.getOrderByOrderId(orderId)).thenReturn(order);

        Order retrievedOrder = orderService.getOrderById(orderId);

        assertNotNull(retrievedOrder);
        assertEquals(orderId, retrievedOrder.getOrderId());
        verify(orderRepository, times(1)).getOrderByOrderId(orderId);
    }

    @Test
    void updateOrder_ShouldUpdateOrderStatus() {
        Long orderId = 1L;
        Order order = new Order(
                orderId,
                "John Doe",
                BigDecimal.valueOf(100.0),
                "Pending",
                false);
        when(orderRepository.getOrderByOrderId(orderId)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);

        String result = orderService.updateOrder(orderId);

        assertEquals("Order status #1 updated", result);
        assertEquals("Deliverable", order.getOrderStatus());
        verify(orderRepository, times(1)).save(order);
    }

}