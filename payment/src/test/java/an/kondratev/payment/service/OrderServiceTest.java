package an.kondratev.payment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import an.kondratev.payment.model.Order;
import an.kondratev.payment.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;

    @BeforeEach
    public void setUp() {
        testOrder = Order.builder()
                .orderId(1L)
                .customer("John Doe")
                .totalPrice(BigDecimal.valueOf(100))
                .orderStatus("NEW")
                .paymentStatus(false)
                .build();
    }

    @Test
    public void testSaveOrder() {
        when(repository.save(any(Order.class))).thenReturn(testOrder);

        Order savedOrder = orderService.saveOrder(testOrder);

        assertEquals(testOrder.getOrderId(), savedOrder.getOrderId());
        assertEquals(testOrder.getCustomer(), savedOrder.getCustomer());
        assertEquals(testOrder.getTotalPrice(), savedOrder.getTotalPrice());
        assertEquals(testOrder.getOrderStatus(), savedOrder.getOrderStatus());
        assertFalse(savedOrder.isPaymentStatus());

        verify(repository, times(1)).save(testOrder);
    }

    @Test
    public void testGetOrderById() {
        when(repository.getOrderByOrderId(1L)).thenReturn(testOrder);

        Order foundOrder = orderService.getOrderById(1L);

        assertNotNull(foundOrder);
        assertEquals(testOrder.getOrderId(), foundOrder.getOrderId());
        assertEquals(testOrder.getCustomer(), foundOrder.getCustomer());

        verify(repository, times(1)).getOrderByOrderId(1L);
    }

    @Test
    public void testUpdateOrder() {
        when(repository.getOrderByOrderId(1L)).thenReturn(testOrder);
        when(repository.save(any(Order.class))).thenReturn(testOrder);

        String result = orderService.updateOrder(1L);

        assertTrue(testOrder.isPaymentStatus());
        assertEquals("Order #1 updated", result);

        verify(repository, times(1)).getOrderByOrderId(1L);
        verify(repository, times(1)).save(testOrder);
    }

    @Test
    public void testUpdateOrder_NotFound() {
        when(repository.getOrderByOrderId(1L)).thenReturn(null);

        Exception exception = assertThrows(NullPointerException.class, () -> orderService.updateOrder(1L));

        String expectedMessage = "Order not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}