package dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import model.Payment;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Data Access Object for Payment operations using Hibernate
 */
public class PaymentDao {
    
    /**
     * Creates a new payment in the database
     */
    public Payment createPayment(Payment payment) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.save(payment);
            
            // Update invoice payment amount
            if (payment.getInvoice() != null) {
                payment.getInvoice().recalculateAmountPaid();
                session.update(payment.getInvoice());
            }
            
            transaction.commit();
            session.close();
            return payment;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Updates an existing payment in the database
     */
    public Payment updatePayment(Payment payment) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.update(payment);
            
            // Update invoice payment amount
            if (payment.getInvoice() != null) {
                payment.getInvoice().recalculateAmountPaid();
                session.update(payment.getInvoice());
            }
            
            transaction.commit();
            session.close();
            return payment;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Deletes a payment from the database
     */
    public Payment deletePayment(Payment payment) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            // Store invoice reference before deletion
            model.Invoice invoice = payment.getInvoice();
            
            session.delete(payment);
            
            // Update invoice payment amount
            if (invoice != null) {
                invoice.recalculateAmountPaid();
                session.update(invoice);
            }
            
            transaction.commit();
            session.close();
            return payment;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds a payment by ID
     */
    public Payment findPaymentById(Payment payment) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Payment foundPayment = (Payment) session.get(Payment.class, payment.getId());
            session.close();
            return foundPayment;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds a payment by payment ID
     */
    public Payment findPaymentByPaymentId(String paymentId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Payment p WHERE p.paymentId = :paymentId");
            query.setParameter("paymentId", paymentId);
            Payment payment = (Payment) query.uniqueResult();
            session.close();
            return payment;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds payments by invoice ID
     */
    @SuppressWarnings("unchecked")
    public List<Payment> findPaymentsByInvoice(int invoiceId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Payment p WHERE p.invoice.id = :invoiceId ORDER BY p.paymentDate DESC");
            query.setParameter("invoiceId", invoiceId);
            List<Payment> payments = query.list();
            session.close();
            return payments;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds payments by client ID
     */
    @SuppressWarnings("unchecked")
    public List<Payment> findPaymentsByClient(int clientId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Payment p WHERE p.client.id = :clientId ORDER BY p.paymentDate DESC");
            query.setParameter("clientId", clientId);
            List<Payment> payments = query.list();
            session.close();
            return payments;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds payments by payment method
     */
    @SuppressWarnings("unchecked")
    public List<Payment> findPaymentsByMethod(String paymentMethod) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Payment p WHERE p.paymentMethod = :paymentMethod ORDER BY p.paymentDate DESC");
            query.setParameter("paymentMethod", paymentMethod);
            List<Payment> payments = query.list();
            session.close();
            return payments;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds payments by date range
     */
    @SuppressWarnings("unchecked")
    public List<Payment> findPaymentsByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate ORDER BY p.paymentDate");
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            List<Payment> payments = query.list();
            session.close();
            return payments;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets all payments
     */
    @SuppressWarnings("unchecked")
    public List<Payment> findAllPayments() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Payment p ORDER BY p.paymentDate DESC");
            List<Payment> payments = query.list();
            session.close();
            return payments;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets a payment with invoice and client information
     */
    public Payment getPaymentWithDetails(Payment payment) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            // Load payment with eager fetching of invoice and client
            Query query = session.createQuery(
                "FROM Payment p " +
                "LEFT JOIN FETCH p.invoice " +
                "LEFT JOIN FETCH p.client " +
                "WHERE p.id = :paymentId"
            );
            query.setParameter("paymentId", payment.getId());
            Payment foundPayment = (Payment) query.uniqueResult();
            
            session.close();
            return foundPayment;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets the total payments received for a client
     */
    public BigDecimal getTotalPaymentsByClient(int clientId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("SELECT SUM(p.amount) FROM Payment p WHERE p.client.id = :clientId");
            query.setParameter("clientId", clientId);
            BigDecimal totalAmount = (BigDecimal) query.uniqueResult();
            session.close();
            return totalAmount != null ? totalAmount : BigDecimal.ZERO;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Gets the total payments received for a date range
     */
    public BigDecimal getTotalPaymentsByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate");
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            BigDecimal totalAmount = (BigDecimal) query.uniqueResult();
            session.close();
            return totalAmount != null ? totalAmount : BigDecimal.ZERO;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Generates the next payment ID based on the current highest number
     */
    public String generateNextPaymentId() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            // Get the maximum payment number
            Query query = session.createQuery("SELECT MAX(CAST(SUBSTRING(p.paymentId, 4) AS integer)) FROM Payment p WHERE p.paymentId LIKE 'PMT%'");
            Integer maxNum = (Integer) query.uniqueResult();
            
            session.close();
            
            // Format next payment ID
            int nextNum = (maxNum != null ? maxNum : 0) + 1;
            return String.format("PMT%06d", nextNum);
        } catch (Exception ex) {
            ex.printStackTrace();
            // Default format if error occurs
            return "PMT" + System.currentTimeMillis();
        }
    }
}