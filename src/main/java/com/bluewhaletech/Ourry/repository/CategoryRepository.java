package com.bluewhaletech.Ourry.repository;

import com.bluewhaletech.Ourry.domain.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CategoryRepository {
    @PersistenceContext
    private EntityManager em;

    public void save(Category category) {
        if(category.getCategoryId() == null) {
            em.persist(category);
        } else {
            em.merge(category);
        }
    }

    public Category findOne(Long categoryId) {
        return em.find(Category.class, categoryId);
    }
}