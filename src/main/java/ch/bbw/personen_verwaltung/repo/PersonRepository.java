package ch.bbw.personen_verwaltung.repo;

import ch.bbw.personen_verwaltung.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {
}
