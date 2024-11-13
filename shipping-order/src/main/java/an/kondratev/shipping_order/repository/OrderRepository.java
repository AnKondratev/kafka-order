package an.kondratev.shipping_order.repository;


import an.kondratev.shipping_order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order getOrderByOrderId(Long orderId);
}
