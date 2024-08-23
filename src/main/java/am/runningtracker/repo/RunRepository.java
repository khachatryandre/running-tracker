package am.runningtracker.repo;

import am.runningtracker.model.Run;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RunRepository extends JpaRepository<Run, Long> {
    List<Run> findByUserIdAndStartDateTimeBetween(Long userId, LocalDateTime from, LocalDateTime to);

    List<Run> findByUserId(Long userId);

    Optional<Run> findByUserIdAndStatus(Long userId, String status);

}