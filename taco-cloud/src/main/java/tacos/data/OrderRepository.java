package tacos.data;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tacos.domain.Order;
import tacos.security.User;

public interface OrderRepository {

  Order save(Order order);
  
  List<Order> findByUserOrderByCreatedAtDesc(User user);

  Page<Order> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

}