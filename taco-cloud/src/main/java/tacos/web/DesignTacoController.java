package tacos.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import lombok.extern.slf4j.Slf4j;
import tacos.Taco;
import tacos.data.IngredientRepository;
import tacos.data.TacoRepository;
import tacos.util.CollectionUtils;
import tacos.Ingredient;
import tacos.Ingredient.Type;
import tacos.Order;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {

  private static final String DESIGN_VIEW = "design";

  private static final String ORDER_REDIRECT = "redirect:/orders/current";

  private final IngredientRepository ingredientRepository;

  private final TacoRepository tacoRepository;
  
  @Autowired
  public DesignTacoController(IngredientRepository ingredientRepository, TacoRepository tacoRepository) {
    this.ingredientRepository = ingredientRepository;
    this.tacoRepository = tacoRepository;
  }
  
  @ModelAttribute
  public void addIngredientsToModel(Model model) {

    final List<Ingredient> ingredients = 
        CollectionUtils.toList(ingredientRepository.findAll());
    
    Type[] types = Ingredient.Type.values();
    for (Type type : types) {
      model.addAttribute(type.toString().toLowerCase(),
          filterByType(ingredients, type));
    }
  }
  
  @ModelAttribute(name = "order")
  public Order order() {
    return new Order();
  }

  @ModelAttribute(name = "taco")
  public Taco taco() {
    return new Taco();
  }


  @GetMapping
  public String showDesignForm(final Model model) {
    model.addAttribute("design", new Taco());
    return DESIGN_VIEW;
  }
  
  @PostMapping
  public String processDesign(
      @Valid @ModelAttribute("design") final Taco design, final Errors errors, @ModelAttribute final Order order) {
    if (errors.hasErrors()) {
      return DESIGN_VIEW;
    } 

    log.info("Processing taco design: " + design);
    final Taco saved = tacoRepository.save(design);
    
    order.addDesign(saved);
    
    return ORDER_REDIRECT;
  }

  private List<Ingredient> filterByType(final List<Ingredient> ingredients, final Type type) {
          return ingredients
          .stream()
          .filter(x -> x.getType().equals(type))
          .collect(Collectors.toList());
  }
}