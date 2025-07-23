package s2m.me.regulation.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import s2m.me.regulation.domain.report.SettlementReport;

@Repository
public interface SettlementReportRepository extends JpaRepository<SettlementReport, Long> {

}
