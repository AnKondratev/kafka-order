package an.kondratev.payment.model;

import jakarta.persistence.*;
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