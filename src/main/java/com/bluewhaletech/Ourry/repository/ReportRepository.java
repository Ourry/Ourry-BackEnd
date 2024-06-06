package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Report;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class ReportRepository {
    @PersistenceContext
    private EntityManager em;

    public Long save(Report report) {
        if(report.getId() == null) {
            em.persist(report);
        } else {
            em.merge(report);
        }
        return report.getId();
    }
}
