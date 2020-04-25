package tacos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import tacos.data.IngredientRepository;
import tacos.data.OrderRepository;
import tacos.data.TacoRepository;
import tacos.security.UserRepository;


@WebMvcTest
public class AbstractControllerTest {
	@Autowired
	protected MockMvc mockMvc;
	
	@MockBean
  protected IngredientRepository ingredientRepository;

	@MockBean
  protected OrderRepository orderRepository;

	@MockBean
  protected PasswordEncoder passwordEncoder;

	@MockBean
  protected TacoRepository tacoRepository;

	@MockBean
	protected UserRepository userRepository;
}
