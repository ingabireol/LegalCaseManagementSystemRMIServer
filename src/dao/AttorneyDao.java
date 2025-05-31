package dao;

import java.util.List;
import model.Attorney;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Data Access Object for Attorney operations using Hibernate
 */
public class AttorneyDao {
    
    /**
     * Creates a new attorney in the database
     */
    public Attorney createAttorney(Attorney attorney) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.save(attorney);
            
            transaction.commit();
            session.close();
            return attorney;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Updates an existing attorney in the database
     */
    public Attorney updateAttorney(Attorney attorney) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.update(attorney);
            
            transaction.commit();
            session.close();
            return attorney;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Deletes an attorney from the database
     */
    public Attorney deleteAttorney(Attorney attorney) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            // Check if attorney has cases or time entries
            Query caseQuery = session.createQuery("SELECT COUNT(c) FROM Case c JOIN c.attorneys a WHERE a.id = :attorneyId");
            caseQuery.setParameter("attorneyId", attorney.getId());
            Long caseCount = (Long) caseQuery.uniqueResult();
            
            Query timeQuery = session.createQuery("SELECT COUNT(t) FROM TimeEntry t WHERE t.attorney.id = :attorneyId");
            timeQuery.setParameter("attorneyId", attorney.getId());
            Long timeCount = (Long) timeQuery.uniqueResult();
            
            if (caseCount > 0 || timeCount > 0) {
                // Cannot delete attorney with cases or time entries
                transaction.rollback();
                session.close();
                return null;
            }
            
            session.delete(attorney);
            
            transaction.commit();
            session.close();
            return attorney;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds an attorney by ID
     */
    public Attorney findAttorneyById(Attorney attorney) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Attorney foundAttorney = (Attorney) session.get(Attorney.class, attorney.getId());
            session.close();
            return foundAttorney;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds an attorney by attorney ID
     */
    public Attorney findAttorneyByAttorneyId(String attorneyId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Attorney a WHERE a.attorneyId = :attorneyId");
            query.setParameter("attorneyId", attorneyId);
            Attorney attorney = (Attorney) query.uniqueResult();
            session.close();
            return attorney;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds attorneys by name (first name, last name, or full name)
     */
    @SuppressWarnings("unchecked")
    public List<Attorney> findAttorneysByName(String name) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery(
                "FROM Attorney a WHERE a.firstName LIKE :name OR a.lastName LIKE :name OR " +
                "CONCAT(a.firstName, ' ', a.lastName) LIKE :name"
            );
            query.setParameter("name", "%" + name + "%");
            List<Attorney> attorneys = query.list();
            session.close();
            return attorneys;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds attorneys by specialization
     */
    @SuppressWarnings("unchecked")
    public List<Attorney> findAttorneysBySpecialization(String specialization) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Attorney a WHERE a.specialization = :specialization");
            query.setParameter("specialization", specialization);
            List<Attorney> attorneys = query.list();
            session.close();
            return attorneys;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds attorneys by case ID
     */
    @SuppressWarnings("unchecked")
    public List<Attorney> findAttorneysByCase(int caseId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("SELECT a FROM Attorney a JOIN a.cases c WHERE c.id = :caseId");
            query.setParameter("caseId", caseId);
            List<Attorney> attorneys = query.list();
            session.close();
            return attorneys;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets all attorneys
     */
    @SuppressWarnings("unchecked")
    public List<Attorney> findAllAttorneys() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Attorney a ORDER BY a.lastName, a.firstName");
            List<Attorney> attorneys = query.list();
            session.close();
            return attorneys;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets an attorney with all their cases loaded
     */
    public Attorney getAttorneyWithCases(Attorney attorney) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            // Load attorney with eager fetching of cases
            Query query = session.createQuery("FROM Attorney a LEFT JOIN FETCH a.cases WHERE a.id = :attorneyId");
            query.setParameter("attorneyId", attorney.getId());
            Attorney foundAttorney = (Attorney) query.uniqueResult();
            
            session.close();
            return foundAttorney;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}