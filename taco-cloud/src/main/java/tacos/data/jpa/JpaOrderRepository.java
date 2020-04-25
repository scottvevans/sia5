package tacos.data.jpa;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;

import tacos.data.OrderRepository;
import tacos.domain.Order;

@Profile("!jdbc")
public interface JpaOrderRepository extends CrudRepository<Order, Long>, OrderRepository {

}
