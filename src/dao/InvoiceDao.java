package dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import model.Invoice;
import model.TimeEntry;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Data Access Object for Invoice operations using Hibernate
 */
public class InvoiceDao {
    
    /**
     * Creates a new invoice in the database
     */
    public Invoice createInvoice(Invoice invoice) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.save(invoice);
            
            // Update time entries if they are associated with this invoice
            if (invoice.getTimeEntries() != null && !invoice.getTimeEntries().isEmpty()) {
                for (TimeEntry timeEntry : invoice.getTimeEntries()) {
                    timeEntry.setBilled(true);
                    timeEntry.setInvoice(invoice);
                    session.update(timeEntry);
                }
            }
            
            transaction.commit();
            session.close();
            return invoice;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Updates an existing invoice in the database
     */
    public Invoice updateInvoice(Invoice invoice) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.update(invoice);
            
            transaction.commit();
            session.close();
            return invoice;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Deletes an invoice from the database
     */
    public Invoice deleteInvoice(Invoice invoice) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            // Check if invoice has payments
            Query paymentQuery = session.createQuery("SELECT COUNT(p) FROM Payment p WHERE p.invoice.id = :invoiceId");
            paymentQuery.setParameter("invoiceId", invoice.getId());
            Long paymentCount = (Long) paymentQuery.uniqueResult();
            
            if (paymentCount > 0) {
                // Cannot delete invoice with payments
                transaction.rollback();
                session.close();
                return null;
            }
            
            // Update time entries to unbilled
            Query timeEntryQuery = session.createQuery("UPDATE TimeEntry t SET t.billed = false, t.invoice = null WHERE t.invoice.id = :invoiceId");
            timeEntryQuery.setParameter("invoiceId", invoice.getId());
            timeEntryQuery.executeUpdate();
            
            session.delete(invoice);
            
            transaction.commit();
            session.close();
            return invoice;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Updates the status of an invoice
     */
    public Invoice updateInvoiceStatus(Invoice invoice, String status) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            invoice.setStatus(status);
            session.update(invoice);
            
            transaction.commit();
            session.close();
            return invoice;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds an invoice by ID
     */
    public Invoice findInvoiceById(Invoice invoice) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Invoice foundInvoice = (Invoice) session.get(Invoice.class, invoice.getId());
            session.close();
            return foundInvoice;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds an invoice by invoice number
     */
    public Invoice findInvoiceByInvoiceNumber(String invoiceNumber) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Invoice i WHERE i.invoiceNumber = :invoiceNumber");
            query.setParameter("invoiceNumber", invoiceNumber);
            Invoice invoice = (Invoice) query.uniqueResult();
            session.close();
            return invoice;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds invoices by client ID
     */
    @SuppressWarnings("unchecked")
    public List<Invoice> findInvoicesByClient(int clientId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Invoice i WHERE i.client.id = :clientId ORDER BY i.issueDate DESC");
            query.setParameter("clientId", clientId);
            List<Invoice> invoices = query.list();
            session.close();
            return invoices;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds invoices by case ID
     */
    @SuppressWarnings("unchecked")
    public List<Invoice> findInvoicesByCase(int caseId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Invoice i WHERE i.legalCase.id = :caseId ORDER BY i.issueDate DESC");
            query.setParameter("caseId", caseId);
            List<Invoice> invoices = query.list();
            session.close();
            return invoices;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds invoices by status
     */
    @SuppressWarnings("unchecked")
    public List<Invoice> findInvoicesByStatus(String status) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Invoice i WHERE i.status = :status ORDER BY i.issueDate DESC");
            query.setParameter("status", status);
            List<Invoice> invoices = query.list();
            session.close();
            return invoices;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds invoices by date range
     */
    @SuppressWarnings("unchecked")
    public List<Invoice> findInvoicesByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Invoice i WHERE i.issueDate BETWEEN :startDate AND :endDate ORDER BY i.issueDate");
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            List<Invoice> invoices = query.list();
            session.close();
            return invoices;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds overdue invoices
     */
    @SuppressWarnings("unchecked")
    public List<Invoice> findOverdueInvoices() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery(
                "FROM Invoice i WHERE i.dueDate < CURRENT_DATE " +
                "AND i.status != :paid AND i.status != :cancelled ORDER BY i.dueDate"
            );
            query.setParameter("paid", Invoice.STATUS_PAID);
            query.setParameter("cancelled", Invoice.STATUS_CANCELLED);
            List<Invoice> invoices = query.list();
            session.close();
            return invoices;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets all invoices
     */
    @SuppressWarnings("unchecked")
    public List<Invoice> findAllInvoices() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Invoice i ORDER BY i.issueDate DESC");
            List<Invoice> invoices = query.list();
            session.close();
            return invoices;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets an invoice with all related details (client, case, time entries, payments)
     */
    public Invoice getInvoiceWithDetails(Invoice invoice) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            // Load invoice with eager fetching of all relationships
            Query query = session.createQuery(
                "FROM Invoice i " +
                "LEFT JOIN FETCH i.client " +
                "LEFT JOIN FETCH i.legalCase " +
                "LEFT JOIN FETCH i.timeEntries " +
                "LEFT JOIN FETCH i.payments " +
                "WHERE i.id = :invoiceId"
            );
            query.setParameter("invoiceId", invoice.getId());
            Invoice foundInvoice = (Invoice) query.uniqueResult();
            
            session.close();
            return foundInvoice;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Updates the paid amount for an invoice based on payments
     */
    public Invoice updateInvoicePaidAmount(int invoiceId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            // Get the invoice
            Invoice invoice = (Invoice) session.get(Invoice.class, invoiceId);
            if (invoice == null) {
                transaction.rollback();
                session.close();
                return null;
            }
            
            // Recalculate amount paid
            invoice.recalculateAmountPaid();
            
            // Update the invoice
            session.update(invoice);
            
            transaction.commit();
            session.close();
            return invoice;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Creates a new invoice from unbilled time entries for a case
     */
    public Invoice createInvoiceFromUnbilledTimeEntries(int caseId, String invoiceNumber, LocalDate dueDate) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            // Get case information
            Query caseQuery = session.createQuery("FROM Case c WHERE c.id = :caseId");
            caseQuery.setParameter("caseId", caseId);
            model.Case legalCase = (model.Case) caseQuery.uniqueResult();
            
            if (legalCase == null) {
                transaction.rollback();
                session.close();
                return null;
            }
            
            // Get unbilled time entries
            Query timeQuery = session.createQuery("FROM TimeEntry t WHERE t.associatedCase.id = :caseId AND t.billed = false");
            timeQuery.setParameter("caseId", caseId);
            @SuppressWarnings("unchecked")
            List<TimeEntry> unbilledEntries = timeQuery.list();
            
            if (unbilledEntries.isEmpty()) {
                transaction.rollback();
                session.close();
                return null;
            }
            
            // Create invoice
            Invoice invoice = new Invoice();
            invoice.setInvoiceNumber(invoiceNumber);
            invoice.setClient(legalCase.getClient());
            invoice.setCase(legalCase);
            invoice.setIssueDate(LocalDate.now());
            invoice.setDueDate(dueDate);
            invoice.setStatus(Invoice.STATUS_ISSUED);
            
            // Calculate total amount
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (TimeEntry entry : unbilledEntries) {
                if (entry.getHourlyRate() != null) {
                    BigDecimal entryAmount = entry.getHourlyRate().multiply(new BigDecimal(entry.getHours()));
                    totalAmount = totalAmount.add(entryAmount);
                }
            }
            invoice.setAmount(totalAmount);
            invoice.setAmountPaid(BigDecimal.ZERO);
            
            // Save invoice
            session.save(invoice);
            
            // Mark time entries as billed
            for (TimeEntry entry : unbilledEntries) {
                entry.setBilled(true);
                entry.setInvoice(invoice);
                session.update(entry);
            }
            
            // Set time entries
            invoice.setTimeEntries(unbilledEntries);
            
            transaction.commit();
            session.close();
            return invoice;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Generates the next invoice number based on the current highest number
     */
    public String generateNextInvoiceNumber() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            // Get the maximum invoice number
            Query query = session.createQuery("SELECT MAX(CAST(SUBSTRING(i.invoiceNumber, 4) AS integer)) FROM Invoice i WHERE i.invoiceNumber LIKE 'INV%'");
            Integer maxNum = (Integer) query.uniqueResult();
            
            session.close();
            
            // Format next invoice number
            int nextNum = (maxNum != null ? maxNum : 0) + 1;
            return String.format("INV%06d", nextNum);
        } catch (Exception ex) {
            ex.printStackTrace();
            // Default format if error occurs
            return "INV" + System.currentTimeMillis();
        }
    }
}