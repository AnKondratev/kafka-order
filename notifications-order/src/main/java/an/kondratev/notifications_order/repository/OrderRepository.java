package an.kondratev.notifications_order.repository;


import an.kondratev.notifications_order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order getOrderByOrderId(Long orderId);
}
