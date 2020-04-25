package tacos.data.jdbc;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import tacos.data.OrderRepository;
import tacos.domain.Order;
import tacos.domain.Taco;

@Repository
@Profile("jdbc")
public class JdbcOrderRepository implements OrderRepository {

  private final SimpleJdbcInsert orderInserter;
  private final SimpleJdbcInsert orderTacoInserter;

  @Autowired
  public JdbcOrderRepository(final JdbcTemplate jdbc) {
    this.orderInserter = new SimpleJdbcInsert(jdbc).withTableName("taco_order").usingGeneratedKeyColumns("id");
    this.orderTacoInserter = new SimpleJdbcInsert(jdbc).withTableName("taco_order_tacos");
  }

  @Override
  public Order save(Order order) {
    order.setCreatedAt(new Date());

    long orderId = saveOrderDetails(order);

    order.setId(orderId);
    List<Taco> tacos = order.getTacos();
    for (Taco taco : tacos) {
      saveTacoToOrder(taco, orderId);
    }

    return order;
  }

  private long saveOrderDetails(Order order) {
    Map<String, Object> values = new HashMap<>();

    values.put("name", order.getName());
    values.put("street", order.getStreet());
    values.put("city", order.getCity());
    values.put("state", order.getState());
    values.put("zip", order.getZip());
    values.put("cc_number", order.getCcNumber());
    values.put("cc_expiration", order.getCcExpiration());
    values.put("cc_cvv", order.getCcCvv());
    values.put("created_at", order.getCreatedAt());

    return orderInserter.executeAndReturnKey(values).longValue();
  }

  private void saveTacoToOrder(Taco taco, long orderId) {
    Map<String, Object> values = new HashMap<>();

    values.put("taco_order_id", orderId);
    values.put("tacos_id", taco.getId());

    orderTacoInserter.execute(values);
  }
}