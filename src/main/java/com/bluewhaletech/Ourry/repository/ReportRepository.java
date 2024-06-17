package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Report;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    public List<Report> findAll() {
        TypedQuery<Report> query = em.createQuery("select r from Report r", Report.class);
        return query.getResultList();
    }
}