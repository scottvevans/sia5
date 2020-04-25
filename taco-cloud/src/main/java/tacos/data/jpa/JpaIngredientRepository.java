package tacos.data.jpa;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;

import tacos.data.IngredientRepository;
import tacos.domain.Ingredient;

@Profile("!jdbc")
public interface JpaIngredientRepository extends CrudRepository<Ingredient, String>, IngredientRepository {

}
