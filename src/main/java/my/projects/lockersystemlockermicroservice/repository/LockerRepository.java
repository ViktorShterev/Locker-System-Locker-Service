package my.projects.lockersystemlockermicroservice.repository;

import my.projects.lockersystemlockermicroservice.entity.Locker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LockerRepository extends JpaRepository<Locker, Long> {

    Optional<Locker> findByLocation(String name);
}
