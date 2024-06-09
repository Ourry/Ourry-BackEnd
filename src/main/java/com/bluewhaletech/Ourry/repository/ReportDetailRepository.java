package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.ReportDetail;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class ReportDetailRepository {
    @PersistenceContext
    private EntityManager em;

    public void save(ReportDetail reportDetail) {
        if(reportDetail.getId() == null) {
            em.persist(reportDetail);
        } else {
            em.merge(reportDetail);
        }
    }
}