package dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import model.TimeEntry;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Data Access Object for TimeEntry operations using Hibernate
 */
public class TimeEntryDao {
    
    /**
     * Creates a new time entry in the database
     */
    public TimeEntry createTimeEntry(TimeEntry timeEntry) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.save(timeEntry);
            
            transaction.commit();
            session.close();
            return timeEntry;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Updates an existing time entry in the database
     */
    public TimeEntry updateTimeEntry(TimeEntry timeEntry) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.update(timeEntry);
            
            transaction.commit();
            session.close();
            return timeEntry;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Deletes a time entry from the database
     */
    public TimeEntry deleteTimeEntry(TimeEntry timeEntry) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            // Check if time entry is billed
            if (timeEntry.isBilled()) {
                // Cannot delete billed time entry
                transaction.rollback();
                session.close();
                return null;
            }
            
            session.delete(timeEntry);
            
            transaction.commit();
            session.close();
            return timeEntry;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Marks a time entry as billed
     */
    public TimeEntry markTimeEntryAsBilled(TimeEntry timeEntry, int invoiceId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            timeEntry.setBilled(true);
            // Note: You would need to set the invoice object here if you have it
            session.update(timeEntry);
            
            transaction.commit();
            session.close();
            return timeEntry;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds a time entry by ID
     */
    public TimeEntry findTimeEntryById(TimeEntry timeEntry) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            TimeEntry foundTimeEntry = (TimeEntry) session.get(TimeEntry.class, timeEntry.getId());
            session.close();
            return foundTimeEntry;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds a time entry by entry ID
     */
    public TimeEntry findTimeEntryByEntryId(String entryId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM TimeEntry t WHERE t.entryId = :entryId");
            query.setParameter("entryId", entryId);
            TimeEntry timeEntry = (TimeEntry) query.uniqueResult();
            session.close();
            return timeEntry;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds time entries by case ID
     */
    @SuppressWarnings("unchecked")
    public List<TimeEntry> findTimeEntriesByCase(int caseId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM TimeEntry t WHERE t.associatedCase.id = :caseId ORDER BY t.entryDate DESC");
            query.setParameter("caseId", caseId);
            List<TimeEntry> timeEntries = query.list();
            session.close();
            return timeEntries;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds time entries by attorney ID
     */
    @SuppressWarnings("unchecked")
    public List<TimeEntry> findTimeEntriesByAttorney(int attorneyId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM TimeEntry t WHERE t.attorney.id = :attorneyId ORDER BY t.entryDate DESC");
            query.setParameter("attorneyId", attorneyId);
            List<TimeEntry> timeEntries = query.list();
            session.close();
            return timeEntries;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds time entries by date range
     */
    @SuppressWarnings("unchecked")
    public List<TimeEntry> findTimeEntriesByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM TimeEntry t WHERE t.entryDate BETWEEN :startDate AND :endDate ORDER BY t.entryDate");
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            List<TimeEntry> timeEntries = query.list();
            session.close();
            return timeEntries;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds unbilled time entries for a case
     */
    @SuppressWarnings("unchecked")
    public List<TimeEntry> findUnbilledTimeEntriesByCase(int caseId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM TimeEntry t WHERE t.associatedCase.id = :caseId AND t.billed = false ORDER BY t.entryDate");
            query.setParameter("caseId", caseId);
            List<TimeEntry> timeEntries = query.list();
            session.close();
            return timeEntries;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds time entries by invoice ID
     */
    @SuppressWarnings("unchecked")
    public List<TimeEntry> findTimeEntriesByInvoice(int invoiceId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM TimeEntry t WHERE t.invoice.id = :invoiceId ORDER BY t.entryDate");
            query.setParameter("invoiceId", invoiceId);
            List<TimeEntry> timeEntries = query.list();
            session.close();
            return timeEntries;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets all time entries
     */
    @SuppressWarnings("unchecked")
    public List<TimeEntry> findAllTimeEntries() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM TimeEntry t ORDER BY t.entryDate DESC");
            List<TimeEntry> timeEntries = query.list();
            session.close();
            return timeEntries;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets a time entry with case and attorney information
     */
    public TimeEntry getTimeEntryWithDetails(TimeEntry timeEntry) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            // Load time entry with eager fetching of case and attorney
            Query query = session.createQuery(
                "FROM TimeEntry t " +
                "LEFT JOIN FETCH t.associatedCase " +
                "LEFT JOIN FETCH t.attorney " +
                "WHERE t.id = :timeEntryId"
            );
            query.setParameter("timeEntryId", timeEntry.getId());
            TimeEntry foundTimeEntry = (TimeEntry) query.uniqueResult();
            
            session.close();
            return foundTimeEntry;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets the total hours worked on a case
     */
    public double getTotalHoursByCase(int caseId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("SELECT SUM(t.hours) FROM TimeEntry t WHERE t.associatedCase.id = :caseId");
            query.setParameter("caseId", caseId);
            Double totalHours = (Double) query.uniqueResult();
            session.close();
            return totalHours != null ? totalHours : 0.0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0.0;
    }
    
    /**
     * Gets the total billable amount for a case
     */
    public BigDecimal getTotalAmountByCase(int caseId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("SELECT SUM(t.hours * t.hourlyRate) FROM TimeEntry t WHERE t.associatedCase.id = :caseId");
            query.setParameter("caseId", caseId);
            BigDecimal totalAmount = (BigDecimal) query.uniqueResult();
            session.close();
            return totalAmount != null ? totalAmount : BigDecimal.ZERO;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
}