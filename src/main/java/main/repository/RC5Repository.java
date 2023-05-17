package main.repository;

import main.domain.RC5Data;
import main.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RC5Repository extends JpaRepository<RC5Data, Long> {

	Optional<RC5Data> findRC5DataByUserAndHash(User user, Long hash);

	@Override
	RC5Data save(RC5Data entity);

	@Override
	void delete(RC5Data entity);
}
