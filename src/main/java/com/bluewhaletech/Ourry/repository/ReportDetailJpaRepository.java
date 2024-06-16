package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Member;
import com.bluewhaletech.Ourry.domain.Report;
import com.bluewhaletech.Ourry.domain.ReportDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportDetailJpaRepository extends org.springframework.data.repository.Repository<ReportDetail, Long> {
    boolean existsByReportAndAuthor(Report report, Member member);

    List<ReportDetail> findByReport(Report report);
}