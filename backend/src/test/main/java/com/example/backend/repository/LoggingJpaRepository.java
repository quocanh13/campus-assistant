package com.example.backend.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import jakarta.persistence.EntityManager;

public class LoggingJpaRepository<T, ID>
        extends SimpleJpaRepository<T, ID> {

    private static final Logger log =
            LoggerFactory.getLogger(LoggingJpaRepository.class);

    public LoggingJpaRepository(
            JpaEntityInformation<T, ?> entityInformation,
            EntityManager entityManager) {

        super(entityInformation, entityManager);
    }

    @Override
    public <S extends T> S save(S entity) {
        log.info("Saving {}", entity.getClass().getSimpleName());
        S result = super.save(entity);
        log.info("Saved {}", entity.getClass().getSimpleName());
        return result;
    }

    @Override
    public void delete(T entity) {
        log.info("Deleting {}", entity.getClass().getSimpleName());
        super.delete(entity);
        log.info("Deleted {}", entity.getClass().getSimpleName());
    }
}