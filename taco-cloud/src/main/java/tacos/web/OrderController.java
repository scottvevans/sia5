package tacos.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import lombok.extern.slf4j.Slf4j;
import tacos.data.OrderRepository;
import tacos.domain.Order;
import tacos.security.User;

@Slf4j
@Controller
@RequestMapping("/orders")
@SessionAttributes("order")
public class OrderController {
  private static final String ORDER_FORM_VIEW = "orderForm";
  private static final String ORDER_LIST_VIEW = "orderList";
  private static final String PROCESSED_REDIRECT = "redirect:/";
  
  private final OrderRepository orderRepository;
  private final OrderProps orderProps;
  
  @Autowired
  public OrderController(final OrderRepository orderRepository, final OrderProps orderProps) {
    this.orderRepository = orderRepository;
    this.orderProps = orderProps;
  }

  @GetMapping("/current")
  public String orderForm(@ModelAttribute final Order order, @AuthenticationPrincipal User user) {
    order.setName(user.getFirstName() + " " + user.getLastName());
    order.setStreet(user.getStreet());
    order.setCity(user.getCity());
    order.setZip(user.getZip());
    order.setState(user.getState());
    log.info("orderForm order: " + order.bark());

    return ORDER_FORM_VIEW;
  }

  @PostMapping
  public String processOrder(@Valid @ModelAttribute final Order order, final Errors errors, 
      final SessionStatus sessionStatus, @AuthenticationPrincipal final User user) {
    if (errors.hasErrors()) {
      return ORDER_FORM_VIEW;
    }

    log.info("Order submitted: " + order.bark());
    order.setUser(user);
    Order saved = orderRepository.save(order);
    log.info("Saved order: " + saved.bark());
    sessionStatus.setComplete();

    return PROCESSED_REDIRECT;
  }

  @GetMapping
  public String ordersForUser(@AuthenticationPrincipal User user, Model model) {
    Pageable pageable = PageRequest.of(0, orderProps.getPageSize());

    Page<Order> userOrders = orderRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    log.info("orders for user {} = {}", user, userOrders);

    model.addAttribute("orders", userOrders);
    return ORDER_LIST_VIEW;
  }
}