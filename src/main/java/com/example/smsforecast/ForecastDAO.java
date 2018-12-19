package com.example.smsforecast;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Data Access Object - provide some specific data operations without exposing details of the database
 * Access data for the Vehicle entity.
 * Repository annotation allows Spring to find and configure the DAO.
 * Transactional annonation will cause Spring to call begin() and commit()
 * at the start/end of the method. If exception occurs it will also call rollback().
 */
@Repository
@Transactional
public class ForecastDAO {

    /**
     * PersistenceContext annotation used to specify there is a database source.
     * EntityManager is used to create and remove persistent entity instances,
     * to find entities by their primary key, and to query over entities.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Insert forecast into the database.
     * @param forecast
     */
    public void create(Forecast forecast) {
        entityManager.persist(forecast);
        return;
    }

    /**
     * Return the vehicle with the passed-in id.
     * @param id
     * @return
     */
    public Forecast getById(int id) {
        return entityManager.find(Forecast.class, id);
    }

    /**
     * Delete the user from the database.
     */
    public void delete(Forecast forecast) {
        if (entityManager.contains(forecast))
            entityManager.remove(forecast);
        else
            entityManager.remove(entityManager.merge(forecast));
        return;
    }

    /**
     * Update the passed user in the database.
     */
    public Forecast update(Forecast forecast) {
        entityManager.merge(forecast);
        return forecast;
    }

    /**
     * Return all the users stored in the database.
     */
    @SuppressWarnings("unchecked")
    public List<Forecast> getAll() {
        List<Forecast> list = entityManager.createQuery("from Forecast").getResultList();

        return list.subList(1, list.size());
    }
}
