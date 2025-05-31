package dao;

import java.time.LocalDate;
import java.util.List;
import model.Case;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Data Access Object for Case operations using Hibernate
 */
public class CaseDao {
    
    /**
     * Creates a new case in the database
     */
    public Case createCase(Case legalCase) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.save(legalCase);
            
            transaction.commit();
            session.close();
            return legalCase;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Updates an existing case in the database
     */
    public Case updateCase(Case legalCase) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.update(legalCase);
            
            transaction.commit();
            session.close();
            return legalCase;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Deletes a case from the database
     */
    public Case deleteCase(Case legalCase) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.delete(legalCase);
            
            transaction.commit();
            session.close();
            return legalCase;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Updates the status of a case
     */
    public Case updateCaseStatus(Case legalCase, String status) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            legalCase.setStatus(status);
            session.update(legalCase);
            
            transaction.commit();
            session.close();
            return legalCase;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds a case by ID
     */
    public Case findCaseById(Case legalCase) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Case foundCase = (Case) session.get(Case.class, legalCase.getId());
            session.close();
            return foundCase;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds a case by case number
     */
    public Case findCaseByCaseNumber(String caseNumber) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Case c WHERE c.caseNumber = :caseNumber");
            query.setParameter("caseNumber", caseNumber);
            Case legalCase = (Case) query.uniqueResult();
            session.close();
            return legalCase;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds cases by title or description
     */
    @SuppressWarnings("unchecked")
    public List<Case> findCasesByText(String searchText) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Case c WHERE c.title LIKE :searchText OR c.description LIKE :searchText");
            query.setParameter("searchText", "%" + searchText + "%");
            List<Case> cases = query.list();
            session.close();
            return cases;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds cases by client ID
     */
    @SuppressWarnings("unchecked")
    public List<Case> findCasesByClient(int clientId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Case c WHERE c.client.id = :clientId");
            query.setParameter("clientId", clientId);
            List<Case> cases = query.list();
            session.close();
            return cases;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds cases by attorney ID
     */
    @SuppressWarnings("unchecked")
    public List<Case> findCasesByAttorney(int attorneyId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("SELECT c FROM Case c JOIN c.attorneys a WHERE a.id = :attorneyId");
            query.setParameter("attorneyId", attorneyId);
            List<Case> cases = query.list();
            session.close();
            return cases;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds cases by status
     */
    @SuppressWarnings("unchecked")
    public List<Case> findCasesByStatus(String status) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Case c WHERE c.status = :status");
            query.setParameter("status", status);
            List<Case> cases = query.list();
            session.close();
            return cases;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds cases by case type
     */
    @SuppressWarnings("unchecked")
    public List<Case> findCasesByType(String caseType) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Case c WHERE c.caseType = :caseType");
            query.setParameter("caseType", caseType);
            List<Case> cases = query.list();
            session.close();
            return cases;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds cases by filing date range
     */
    @SuppressWarnings("unchecked")
    public List<Case> findCasesByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Case c WHERE c.fileDate BETWEEN :startDate AND :endDate");
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            List<Case> cases = query.list();
            session.close();
            return cases;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets all cases
     */
    @SuppressWarnings("unchecked")
    public List<Case> findAllCases() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Case c ORDER BY c.fileDate DESC");
            List<Case> cases = query.list();
            session.close();
            return cases;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets a case with all its details including client, attorneys, documents, events, and time entries
     */
    public Case getCaseWithDetails(Case legalCase) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            // Load case with eager fetching of all relationships
            Query query = session.createQuery(
                "FROM Case c " +
                "LEFT JOIN FETCH c.client " +
                "LEFT JOIN FETCH c.attorneys " +
                "LEFT JOIN FETCH c.documents " +
                "LEFT JOIN FETCH c.events " +
                "LEFT JOIN FETCH c.timeEntries " +
                "WHERE c.id = :caseId"
            );
            query.setParameter("caseId", legalCase.getId());
            Case foundCase = (Case) query.uniqueResult();
            
            session.close();
            return foundCase;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Assigns an attorney to a case
     */
    public boolean assignAttorneyToCase(int caseId, int attorneyId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            // Check if assignment already exists
            Query checkQuery = session.createQuery(
                "SELECT COUNT(*) FROM Case c JOIN c.attorneys a WHERE c.id = :caseId AND a.id = :attorneyId"
            );
            checkQuery.setParameter("caseId", caseId);
            checkQuery.setParameter("attorneyId", attorneyId);
            Long count = (Long) checkQuery.uniqueResult();
            
            if (count > 0) {
                // Assignment already exists
                transaction.rollback();
                session.close();
                return false;
            }
            
            // Insert new assignment
            Query insertQuery = session.createSQLQuery(
                "INSERT INTO case_attorneys (case_id, attorney_id) VALUES (:caseId, :attorneyId)"
            );
            insertQuery.setParameter("caseId", caseId);
            insertQuery.setParameter("attorneyId", attorneyId);
            int rowsAffected = insertQuery.executeUpdate();
            
            transaction.commit();
            session.close();
            return rowsAffected > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    /**
     * Removes an attorney from a case
     */
    public boolean removeAttorneyFromCase(int caseId, int attorneyId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            Query deleteQuery = session.createSQLQuery(
                "DELETE FROM case_attorneys WHERE case_id = :caseId AND attorney_id = :attorneyId"
            );
            deleteQuery.setParameter("caseId", caseId);
            deleteQuery.setParameter("attorneyId", attorneyId);
            int rowsAffected = deleteQuery.executeUpdate();
            
            transaction.commit();
            session.close();
            return rowsAffected > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    /**
     * Gets cases that are due for review (based on filing date)
     */
    @SuppressWarnings("unchecked")
    public List<Case> getCasesDueForReview(int daysBefore) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            // Cases filed X days ago that might need review
            Query query = session.createQuery(
                "FROM Case c WHERE DATEDIFF(CURRENT_DATE, c.fileDate) >= :daysBefore " +
                "AND c.status NOT IN ('Closed', 'Cancelled') ORDER BY c.fileDate"
            );
            query.setParameter("daysBefore", daysBefore);
            List<Case> cases = query.list();
            
            session.close();
            return cases;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets active cases (not closed or cancelled)
     */
    @SuppressWarnings("unchecked")
    public List<Case> getActiveCases() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery(
                "FROM Case c WHERE c.status NOT IN ('Closed', 'Cancelled') ORDER BY c.fileDate DESC"
            );
            List<Case> cases = query.list();
            session.close();
            return cases;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets cases with upcoming deadlines
     */
    @SuppressWarnings("unchecked")
    public List<Case> getCasesWithUpcomingDeadlines(int daysAhead) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            // Find cases that have events within the specified days
            Query query = session.createQuery(
                "SELECT DISTINCT c FROM Case c JOIN c.events e " +
                "WHERE e.eventDate BETWEEN CURRENT_DATE AND :futureDate " +
                "AND e.status NOT IN ('Completed', 'Cancelled') " +
                "AND c.status NOT IN ('Closed', 'Cancelled') " +
                "ORDER BY e.eventDate"
            );
            
            LocalDate futureDate = LocalDate.now().plusDays(daysAhead);
            query.setParameter("futureDate", futureDate);
            List<Case> cases = query.list();
            
            session.close();
            return cases;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Generates the next case number based on the current highest number
     */
    public String generateNextCaseNumber() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            // Get the maximum case number
            Query query = session.createQuery(
                "SELECT MAX(CAST(SUBSTRING(c.caseNumber, 5) AS integer)) FROM Case c " +
                "WHERE c.caseNumber LIKE 'CASE%'"
            );
            Integer maxNum = (Integer) query.uniqueResult();
            
            session.close();
            
            // Format next case number
            int nextNum = (maxNum != null ? maxNum : 0) + 1;
            return String.format("CASE%06d", nextNum);
        } catch (Exception ex) {
            ex.printStackTrace();
            // Default format if error occurs
            return "CASE" + System.currentTimeMillis();
        }
    }
    
    /**
     * Gets case statistics for dashboard
     */
    public java.util.Map<String, Object> getCaseStatistics() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            java.util.Map<String, Object> stats = new java.util.HashMap<>();
            
            // Total cases
            Query totalQuery = session.createQuery("SELECT COUNT(*) FROM Case");
            Long totalCases = (Long) totalQuery.uniqueResult();
            stats.put("totalCases", totalCases);
            
            // Active cases
            Query activeQuery = session.createQuery(
                "SELECT COUNT(*) FROM Case c WHERE c.status NOT IN ('Closed', 'Cancelled')"
            );
            Long activeCases = (Long) activeQuery.uniqueResult();
            stats.put("activeCases", activeCases);
            
            // Cases by status
            Query statusQuery = session.createQuery(
                "SELECT c.status, COUNT(*) FROM Case c GROUP BY c.status"
            );
            @SuppressWarnings("unchecked")
            List<Object[]> statusResults = statusQuery.list();
            java.util.Map<String, Long> casesByStatus = new java.util.HashMap<>();
            for (Object[] result : statusResults) {
                casesByStatus.put((String) result[0], (Long) result[1]);
            }
            stats.put("casesByStatus", casesByStatus);
            
            // Cases by type
            Query typeQuery = session.createQuery(
                "SELECT c.caseType, COUNT(*) FROM Case c GROUP BY c.caseType"
            );
            @SuppressWarnings("unchecked")
            List<Object[]> typeResults = typeQuery.list();
            java.util.Map<String, Long> casesByType = new java.util.HashMap<>();
            for (Object[] result : typeResults) {
                casesByType.put((String) result[0], (Long) result[1]);
            }
            stats.put("casesByType", casesByType);
            
            // Cases filed this month
            Query monthQuery = session.createQuery(
                "SELECT COUNT(*) FROM Case c WHERE YEAR(c.fileDate) = YEAR(CURRENT_DATE) " +
                "AND MONTH(c.fileDate) = MONTH(CURRENT_DATE)"
            );
            Long casesThisMonth = (Long) monthQuery.uniqueResult();
            stats.put("casesThisMonth", casesThisMonth);
            
            // Cases filed this year
            Query yearQuery = session.createQuery(
                "SELECT COUNT(*) FROM Case c WHERE YEAR(c.fileDate) = YEAR(CURRENT_DATE)"
            );
            Long casesThisYear = (Long) yearQuery.uniqueResult();
            stats.put("casesThisYear", casesThisYear);
            
            session.close();
            return stats;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new java.util.HashMap<>();
    }
}