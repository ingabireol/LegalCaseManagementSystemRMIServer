package dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import model.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Data Access Object for User operations and authentication using Hibernate
 */
public class UserDao {
    
    /**
     * Authenticates a user by username and password
     */
    public User authenticateUser(String username, String password) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM User u WHERE u.username = :username AND u.active = true");
            query.setParameter("username", username);
            User user = (User) query.uniqueResult();
            
            if (user != null) {
                // Compute hash of provided password
                String computedHash = hashPassword(password, user.getPasswordSalt());
                
                // Compare hashes
                if (user.getPasswordHash().equals(computedHash)) {
                    // Update last login time
                    updateLastLogin(user.getId());
                    session.close();
                    return user;
                }
            }
            
            session.close();
            return null; // Authentication failed
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Creates a new user in the database
     */
    public User createUser(User user, String password) {
        try {
            // Check if username already exists
            if (isUsernameExists(user.getUsername())) {
                return null;
            }
            
            // Generate salt and hash password
            String salt = generateSalt();
            String passwordHash = hashPassword(password, salt);
            
            user.setPasswordSalt(salt);
            user.setPasswordHash(passwordHash);
            
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.save(user);
            
            transaction.commit();
            session.close();
            return user;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Updates an existing user in the database
     */
    public User updateUser(User user) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.update(user);
            
            transaction.commit();
            session.close();
            return user;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Checks if a username already exists
     */
    public boolean isUsernameExists(String username) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username");
            query.setParameter("username", username);
            Long count = (Long) query.uniqueResult();
            session.close();
            return count > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    /**
     * Updates the last login timestamp for a user
     */
    private boolean updateLastLogin(int userId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            Query query = session.createQuery("UPDATE User u SET u.lastLogin = :currentTime WHERE u.id = :userId");
            query.setParameter("currentTime", LocalDateTime.now());
            query.setParameter("userId", userId);
            int rowsAffected = query.executeUpdate();
            
            transaction.commit();
            session.close();
            return rowsAffected > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    /**
     * Changes a user's password
     */
    public boolean changePassword(int userId, String currentPassword, String newPassword) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            // First verify the current password
            User user = (User) session.get(User.class, userId);
            if (user == null) {
                session.close();
                return false;
            }
            
            // Compute hash of provided current password
            String computedHash = hashPassword(currentPassword, user.getPasswordSalt());
            
            // Compare hashes
            if (user.getPasswordHash().equals(computedHash)) {
                // Current password is correct, update to new password
                Transaction transaction = session.beginTransaction();
                
                String newSalt = generateSalt();
                String newPasswordHash = hashPassword(newPassword, newSalt);
                
                user.setPasswordHash(newPasswordHash);
                user.setPasswordSalt(newSalt);
                session.update(user);
                
                transaction.commit();
                session.close();
                return true;
            }
            
            session.close();
            return false; // Current password is incorrect
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    /**
     * Resets a user's password to a new random password
     */
    public String resetPassword(String email) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM User u WHERE u.email = :email");
            query.setParameter("email", email);
            User user = (User) query.uniqueResult();
            
            if (user != null) {
                Transaction transaction = session.beginTransaction();
                
                // Generate new random password
                String newPassword = generateRandomPassword();
                String newSalt = generateSalt();
                String newPasswordHash = hashPassword(newPassword, newSalt);
                
                // Update user's password
                user.setPasswordHash(newPasswordHash);
                user.setPasswordSalt(newSalt);
                session.update(user);
                
                transaction.commit();
                session.close();
                return newPassword;
            }
            
            session.close();
            return null; // Email not found
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Deactivates a user account
     */
    public boolean deactivateUser(User user) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            user.setActive(false);
            session.update(user);
            
            transaction.commit();
            session.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    /**
     * Reactivates a user account
     */
    public boolean reactivateUser(User user) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            user.setActive(true);
            session.update(user);
            
            transaction.commit();
            session.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    /**
     * Finds a user by ID
     */
    public User findUserById(User user) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            User foundUser = (User) session.get(User.class, user.getId());
            session.close();
            return foundUser;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds a user by username
     */
    public User findUserByUsername(String username) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM User u WHERE u.username = :username");
            query.setParameter("username", username);
            User user = (User) query.uniqueResult();
            session.close();
            return user;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds a user by email
     */
    public User findUserByEmail(String email) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM User u WHERE u.email = :email");
            query.setParameter("email", email);
            User user = (User) query.uniqueResult();
            session.close();
            return user;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets a list of all active users
     */
    @SuppressWarnings("unchecked")
    public List<User> findAllActiveUsers() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM User u WHERE u.active = true ORDER BY u.username");
            List<User> users = query.list();
            session.close();
            return users;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets a list of users by role
     */
    @SuppressWarnings("unchecked")
    public List<User> findUsersByRole(String role) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM User u WHERE u.role = :role AND u.active = true ORDER BY u.fullName");
            query.setParameter("role", role);
            List<User> users = query.list();
            session.close();
            return users;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Generates a random salt for password hashing
     */
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Hashes a password with the given salt using SHA-256
     */
    private String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Generates a random password for password reset
     */
    private String generateRandomPassword() {
        // Define character sets
        String upperChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerChars = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "!@#$%^&*()-_=+";
        String allChars = upperChars + lowerChars + numbers + specialChars;
        
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        
        // Ensure at least one character from each set
        password.append(upperChars.charAt(random.nextInt(upperChars.length())));
        password.append(lowerChars.charAt(random.nextInt(lowerChars.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));
        
        // Add more random characters to reach desired length (12 characters)
        for (int i = 0; i < 8; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        
        // Shuffle the password characters
        char[] passwordArray = password.toString().toCharArray();
        for (int i = 0; i < passwordArray.length; i++) {
            int j = random.nextInt(passwordArray.length);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        
        return new String(passwordArray);
    }
}