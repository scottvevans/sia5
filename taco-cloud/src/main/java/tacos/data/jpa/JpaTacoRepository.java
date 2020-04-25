package tacos.data.jpa;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;

import tacos.data.TacoRepository;
import tacos.domain.Taco;

@Profile("!jdbc")
public interface JpaTacoRepository extends CrudRepository<Taco, Long>, TacoRepository {

}
