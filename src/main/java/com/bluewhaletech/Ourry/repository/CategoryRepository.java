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

    public Optional<Category> findOne(Long categoryId) {
        return Optional.ofNullable(em.find(Category.class, categoryId));
    }
}
