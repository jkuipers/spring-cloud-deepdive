package talk;

import org.springframework.data.repository.CrudRepository;

public interface TalkRepository extends CrudRepository<Talk, Long> {
}
