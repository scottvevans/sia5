package tacos.data.impl;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import tacos.Ingredient;
import tacos.Taco;
import tacos.data.TacoRepository;

@Repository
public class JdbcTacoRepository implements TacoRepository {
  
  private static final String INSERT_TACO_SQL = "insert into Taco (name, createdAt) values (?, ?)";

  private static final String[] TACO_GENERATED_ID_COLUMN = new String[] {"ID"};

  private static final String INSERT_TACO_INGREDIENT_SQL = "insert into Taco_Ingredients (taco, ingredient) values (?, ?)";

  private final PreparedStatementCreatorFactory preparedStatementCreatorFactory;

  private final JdbcTemplate jdbc;


  public JdbcTacoRepository(final JdbcTemplate jdbc) {
    this.jdbc = jdbc;
    this.preparedStatementCreatorFactory = 
        new PreparedStatementCreatorFactory(INSERT_TACO_SQL, Types.VARCHAR, Types.TIMESTAMP );
    this.preparedStatementCreatorFactory.setGeneratedKeysColumnNames(TACO_GENERATED_ID_COLUMN);
  }

  @Override
  public Taco save(final Taco taco) {
    final long tacoId = saveTacoInfo(taco);

    taco.setId(tacoId);
    for (Ingredient ingredient : taco.getIngredients()) {
      saveIngredientToTaco(ingredient, tacoId);
    }

    return taco;
  }
  
  private long saveTacoInfo(final Taco taco) {
    taco.setCreatedAt(new Date());
    PreparedStatementCreator psc = preparedStatementCreatorFactory.newPreparedStatementCreator(
            Arrays.asList(taco.getName(), new Timestamp(taco.getCreatedAt().getTime())));
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbc.update(psc, keyHolder);

    return keyHolder.getKey().longValue();
  }

  private void saveIngredientToTaco(Ingredient ingredient, long tacoId) {
    jdbc.update(INSERT_TACO_INGREDIENT_SQL, tacoId, ingredient.getId());
  }

}