package tacos.data.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tacos.data.IngredientRepository;
import tacos.domain.Ingredient;
import tacos.domain.Taco;

@Repository
@Profile("jdbc")
public class JdbcIngredientRepository implements IngredientRepository {

  private JdbcTemplate jdbcTemplate;


  @Autowired
  public JdbcIngredientRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }


  @Override
  public Iterable<Ingredient> findAll() {
    return jdbcTemplate.query(
        "SELECT id, name, type FROM ingredient", 
        this::mapRowToIngredient);
  }

  @Override
  public Optional<Ingredient> findById(String id) {
    return Optional.ofNullable(
        jdbcTemplate.queryForObject("SELECT id, name, type FROM ingredient WHERE id = ?", 
            this::mapRowToIngredient, id));
  }

  private Ingredient mapRowToIngredient(ResultSet rs, int rowNum) throws SQLException {
    return new Ingredient(
        rs.getString("id"), 
        rs.getString("name"), 
        Ingredient.Type.valueOf(rs.getString("type"))
    );
  }
  
  @Override
  public Ingredient save(Ingredient ingredient) {
    jdbcTemplate.update(
        "INSERT INTO ingredient (id, name, type) VALUES (?, ?, ?)",
        ingredient.getId(),
        ingredient.getName(),
        ingredient.getType().toString());
    return ingredient;
  }


  List<Ingredient> findByTacoOrderByName(Taco taco) {
    return jdbcTemplate.query(
        "SELECT ingredient.id as id, ingredient.name as name, ingredient.type as type " +
        "FROM ingredient " +
        "INNER JOIN taco_ingredients " +
        "ON ingredient.id = taco_ingredients.ingredients_id " +
        "WHERE taco_ingredients.taco_id = ? " +
        "ORDER BY ingredient.name",
        this::mapRowToIngredient, taco.getId());
  }
}