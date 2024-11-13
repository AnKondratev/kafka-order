package an.kondratev.shipping_order.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "update_order")
public class Order {
    @Id
    private Long orderId;
    private String customer;
    private BigDecimal totalPrice;
    private String orderStatus;
    private boolean paymentStatus;
}