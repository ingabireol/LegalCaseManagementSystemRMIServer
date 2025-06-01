package dao;

import model.OTP;
import model.User;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Data Access Object for OTP operations using Hibernate
 */
public class OTPDao {
    
    /**
     * Generates and stores a new OTP for a user
     * 
     * @param user The user for whom to generate OTP
     * @return OTP object if successful, null otherwise
     */
    public OTP generateOTP(User user) {
        try {
            // First, invalidate any existing OTPs for this user
            invalidateExistingOTPs(user.getId());
            
            // Generate 6-digit OTP
            String otpCode = generateOTPCode();
            
            // Create OTP object
            OTP otp = new OTP(user.getId(), user.getEmail(), otpCode);
            
            // Save to database using Hibernate
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.save(otp);
            
            transaction.commit();
            session.close();
            
            return otp;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Verifies an OTP code for a user
     * 
     * @param email The user's email
     * @param otpCode The OTP code to verify
     * @return OTP object if verification successful, null otherwise
     */
    public OTP verifyOTP(String email, String otpCode) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery(
                "FROM OTP o WHERE o.email = :email AND o.otpCode = :otpCode AND o.isUsed = false " +
                "ORDER BY o.createdAt DESC"
            );
            query.setParameter("email", email);
            query.setParameter("otpCode", otpCode);
            query.setMaxResults(1);
            
            OTP otp = (OTP) query.uniqueResult();
            
            if (otp != null) {
                // Check if OTP is valid (not expired and not used)
                if (otp.isValid()) {
                    // Mark OTP as used
                    Transaction transaction = session.beginTransaction();
                    otp.markAsUsed();
                    session.update(otp);
                    transaction.commit();
                    
                    session.close();
                    return otp;
                } else {
                    // Increment attempt count for invalid attempts
                    Transaction transaction = session.beginTransaction();
                    otp.incrementAttemptCount();
                    session.update(otp);
                    transaction.commit();
                }
            }
            
            session.close();
            return null;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Finds the latest OTP for a user by email
     * 
     * @param email The user's email
     * @return Latest OTP object if found, null otherwise
     */
    public OTP findLatestOTPByEmail(String email) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery(
                "FROM OTP o WHERE o.email = :email ORDER BY o.createdAt DESC"
            );
            query.setParameter("email", email);
            query.setMaxResults(1);
            
            OTP otp = (OTP) query.uniqueResult();
            session.close();
            
            return otp;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Invalidates all existing OTPs for a user
     * 
     * @param userId The user's ID
     * @return Number of OTPs invalidated
     */
    public int invalidateExistingOTPs(int userId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            Query query = session.createQuery(
                "UPDATE OTP o SET o.isUsed = true WHERE o.userId = :userId AND o.isUsed = false"
            );
            query.setParameter("userId", userId);
            
            int rowsAffected = query.executeUpdate();
            transaction.commit();
            session.close();
            
            return rowsAffected;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Cleans up expired OTPs from the database
     * 
     * @return Number of expired OTPs cleaned up
     */
    public int cleanupExpiredOTPs() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            Query query = session.createQuery(
                "DELETE FROM OTP o WHERE o.expiresAt < :currentTime"
            );
            query.setParameter("currentTime", LocalDateTime.now());
            
            int rowsAffected = query.executeUpdate();
            transaction.commit();
            session.close();
            
            return rowsAffected;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Generates a 6-digit OTP code
     * 
     * @return 6-digit OTP code as string
     */
    private String generateOTPCode() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // Generates number between 100000-999999
        return String.valueOf(otp);
    }
}