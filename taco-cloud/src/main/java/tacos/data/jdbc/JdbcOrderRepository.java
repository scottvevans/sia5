package tacos.data.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import tacos.data.OrderRepository;
import tacos.domain.Order;
import tacos.domain.Taco;
import tacos.security.User;

@Repository
@Profile("jdbc")
@Slf4j
public class JdbcOrderRepository implements OrderRepository {

  private final JdbcTemplate jdbcTemplate;
  private final SimpleJdbcInsert orderInserter;
  private final SimpleJdbcInsert orderTacoInserter;
  private final JdbcTacoRepository jdbcTacoRepository;

  @Autowired
  public JdbcOrderRepository(final JdbcTemplate jdbcTemplate, JdbcTacoRepository jdbcTacoRepository) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcTacoRepository = jdbcTacoRepository;
    this.orderInserter = new SimpleJdbcInsert(jdbcTemplate).withTableName("taco_order").usingGeneratedKeyColumns("id");
    this.orderTacoInserter = new SimpleJdbcInsert(jdbcTemplate).withTableName("taco_order_tacos");
  }

  @Override
  public Order save(Order order) {
    log.info("save {}", order);
    order.setCreatedAt(new Date());

    long orderId = saveOrderDetails(order);

    order.setId(orderId);
    List<Taco> tacos = order.getTacos();
    log.info("saving tacos {} to order", tacos);
    for (Taco taco : tacos) {
      saveTacoToOrder(taco, orderId);
    }

    return order;
  }

  private long saveOrderDetails(Order order) {
    Map<String, Object> values = new HashMap<>();

    values.put("user_id", order.getUser().getId());
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

    values.put("order_id", orderId);
    values.put("tacos_id", taco.getId());

    orderTacoInserter.execute(values);
  }

  private String getFindByUserOrderByCreatedAtDescSql(User user, Long offset, Integer limit) {
    String defaultSql =
    "SELECT id, created_at, name, street, city, state, zip, cc_number, cc_expiration, cc_cvv " +
        "FROM taco_order WHERE user_id = ? ORDER BY created_at DESC";
    
    if (offset != null && limit != null) {
      defaultSql = defaultSql + " OFFSET " + offset + " ROWS FETCH FIRST " + limit + " ROWS ONLY";
    }
    
    return defaultSql;
  }

  @Override
  public List<Order> findByUserOrderByCreatedAtDesc(User user) {
    List<Order> orders = jdbcTemplate.query(
        getFindByUserOrderByCreatedAtDescSql(user, null, null),
        this::mapRowToOrder, user.getId());
    
    retrieveTacos(user, orders);
    
    return orders;
  }
  
  
  @Override 
  public Page<Order> findByUserOrderByCreatedAtDesc(User user, Pageable pageable) {
    int count = jdbcTemplate.queryForObject(
      "SELECT COUNT(*) FROM taco_order WHERE user_id = ?", Integer.class, user.getId());
    Page<Order> page = null;

    if (count == 0) {
      page = Page.empty(pageable);
    } else if (pageable.getOffset() >= count) {
      throw new IllegalArgumentException(
        String.format("offset %d >= count %d", pageable.getOffset(), count));
    } else {
      List<Order> orders = jdbcTemplate.query(
        getFindByUserOrderByCreatedAtDescSql(user, pageable.getOffset(), pageable.getPageSize()), 
        this::mapRowToOrder, user.getId());
      
      retrieveTacos(user, orders);
      page = new PageImpl<Order>(orders, pageable, count);
    }

    return page;
  }
  
  private Order mapRowToOrder(ResultSet rs, int rowNum) throws SQLException {
    Order order = new Order();
    order.setId(rs.getLong("id"));
    order.setCreatedAt(rs.getDate("created_at"));
    order.setName(rs.getString("name"));
    order.setName(rs.getString("street"));
    order.setName(rs.getString("city"));
    order.setName(rs.getString("state"));
    order.setName(rs.getString("zip"));
    order.setName(rs.getString("cc_number"));
    order.setName(rs.getString("cc_expiration"));
    order.setName(rs.getString("cc_cvv"));
    return order;
  }
  
  private void retrieveTacos(User user, List<Order> orders) {
    orders.forEach(order -> {
      order.setUser(user);
      order.setTacos(jdbcTacoRepository.findByTacoOrderOrderByCreatedAtDesc(order));
    });
  }
}