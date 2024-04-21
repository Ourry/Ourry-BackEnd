package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Alarm;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class AlarmRepository {
    @PersistenceContext
    private EntityManager em;

    public void save(Alarm alarm) {
        if(alarm.getAlarmId() == null) {
            em.persist(alarm);
        } else {
            em.merge(alarm);
        }
    }
}