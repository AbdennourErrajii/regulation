package s2m.me.regulation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import s2m.me.regulation.domain.report.InstitutionReport;

public interface InstitutionReportRepository extends JpaRepository<InstitutionReport, Long> {
}
