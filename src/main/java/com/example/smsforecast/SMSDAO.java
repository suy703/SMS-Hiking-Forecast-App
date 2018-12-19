package com.example.smsforecast;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public class SMSDAO {

    /**
     * PersistenceContext annotation used to specify there is a database source.
     * EntityManager is used to create and remove persistent entity instances,
     * to find entities by their primary key, and to query over entities.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Insert forecast into the database.
     * @param sms
     */
    public void create(SMS sms) {
        entityManager.persist(sms);
        return;
    }

    /**
     * Return the vehicle with the passed-in id.
     * @param id
     * @return
     */
    public SMS getById(int id) {
        return entityManager.find(SMS.class, id);
    }

    /**
     * Delete the user from the database.
     */
    public void delete(SMS sms) {
        if (entityManager.contains(sms))
            entityManager.remove(sms);
        else
            entityManager.remove(entityManager.merge(sms));
        return;
    }

    /**
     * Update the passed user in the database.
     */
    public SMS update(SMS sms) {
        entityManager.merge(sms);
        return sms;
    }

    /**
     * Return all the users stored in the database.
     */
    @SuppressWarnings("unchecked")
    public List<SMS> getAll() {
        List<SMS> list = entityManager.createQuery("from SMS").getResultList();

        return list.subList(1, list.size());
    }
}
