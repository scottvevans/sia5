package tacos.data.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tacos.data.IngredientRepository;
import tacos.domain.Ingredient;

@Repository
@Profile("jdbc")
public class JdbcIngredientRepository implements IngredientRepository {

  private JdbcTemplate jdbc;


  @Autowired
  public JdbcIngredientRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }


  @Override
  public Iterable<Ingredient> findAll() {
    return jdbc.query(
        "SELECT id, name, type FROM ingredient", 
        this::mapRowToIngredient);
  }

  @Override
  public Optional<Ingredient> findById(String id) {
    return Optional.ofNullable(
        jdbc.queryForObject("SELECT id, name, type FROM ingredient WHERE id=?", 
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
    jdbc.update(
        "INSERT INTO ingredient (id, name, type) VALUES (?, ?, ?)",
        ingredient.getId(),
        ingredient.getName(),
        ingredient.getType().toString());
    return ingredient;
  }
}