package tacos.web;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
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
  private static final String PROCESSED_REDIRECT = "redirect:/";
  
  private final OrderRepository orderRepository;
  
  @Autowired
  public OrderController(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @GetMapping("/current")
  public String orderForm(Model model, @AuthenticationPrincipal User user) {
    Order order = new Order();

    order.setName(user.getFirstName() + " " + user.getLastName());
    order.setStreet(user.getStreet());
    order.setCity(user.getCity());
    order.setZip(user.getZip());
    order.setState(user.getState());
    model.addAttribute("order", order);

    return ORDER_FORM_VIEW;
  }

  @PostMapping
  public String processOrder(@Valid final Order order, final Errors errors, 
      final SessionStatus sessionStatus, @AuthenticationPrincipal final User user) {
    if (errors.hasErrors()) {
      return ORDER_FORM_VIEW;
    }

    log.info("Order submitted: " + order);
    order.setUser(user);
    orderRepository.save(order);
    sessionStatus.setComplete();

    return PROCESSED_REDIRECT;
  }
}