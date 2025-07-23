package s2m.me.regulation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import s2m.me.regulation.domain.Settlement;
import s2m.me.regulation.enums.SettlementStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    Optional<Settlement> findBySessionId(String sessionId);

    // Au lieu de retourner une Map, retourner une List
    @Query("SELECT s FROM Settlement s WHERE s.status = 'PENDING'")
    List<Settlement> findPendingSettlements();

    List<Settlement> findByStatus(SettlementStatus status);
}
// Note: La requête JPQL pour une Map n'est pas standard. Une approche plus sûre est de retourner une List<Settlement> et de la transformer en Map dans le service.