package an.kondratev.notifications_order.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import an.kondratev.notifications_order.model.Order;
import an.kondratev.notifications_order.service.OrderServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean 
    private OrderServiceInterface orderService;

    private Order order;

    @BeforeEach
    public void setUp() {
        order = new Order(
                1L,
                "John Doe",
                BigDecimal.valueOf(100.00),
                "Pending",
                true);
    }

    @Test
    public void testShipOrder_OrderExists() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(order);
        when(orderService.updateOrder(1L)).thenReturn("Order successfully updated");

        mockMvc.perform(put("/notification/{id}", 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Order successfully updated"));

        verify(orderService).getOrderById(1L);
        verify(orderService).updateOrder(1L);
    }

    @Test
    public void testShipOrder_OrderNotFound() throws Exception {
        when(orderService.getOrderById(anyLong())).thenReturn(null);

        mockMvc.perform(put("/notification/{id}", 2L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Order not found"));

        verify(orderService).getOrderById(2L);
        verify(orderService, never()).updateOrder(anyLong());
    }
}

