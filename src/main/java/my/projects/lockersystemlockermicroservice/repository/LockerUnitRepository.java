package my.projects.lockersystemlockermicroservice.repository;

import my.projects.lockersystemlockermicroservice.entity.LockerUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LockerUnitRepository extends JpaRepository<LockerUnit, Long> {

}
