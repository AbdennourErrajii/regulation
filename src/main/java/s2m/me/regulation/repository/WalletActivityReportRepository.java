package s2m.me.regulation.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import s2m.me.regulation.domain.report.WalletActivityReport;

@Repository
public interface WalletActivityReportRepository extends JpaRepository<WalletActivityReport,Long> {
}
