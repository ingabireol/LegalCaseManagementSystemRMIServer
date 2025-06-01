package dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;
import model.OTP;
import service.EmailService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Enhanced Data Access Object for User operations and authentication using Hibernate with OTP support and debug logging
 */
public class UserDao {
    
    private static final Logger logger = Logger.getLogger(UserDao.class.getName());
    private OTPDao otpDao;
    private EmailService emailService;
    
    public UserDao() {
        try {
            this.otpDao = new OTPDao();
            this.emailService = new EmailService();
            logger.info("UserDao initialized successfully with OTP and Email services");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize UserDao", e);
            throw new RuntimeException("UserDao initialization failed", e);
        }
    }
    
    /**
     * Authenticates a user by username and password (traditional method)
     */
    public User authenticateUser(String username, String password) {
        logger.info("Starting traditional authentication for username: " + username);
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM User u WHERE u.username = :username AND u.active = true");
            query.setParameter("username", username);
            User user = (User) query.uniqueResult();
            
            if (user != null) {
                logger.info("User found in database: " + username);
                // Compute hash of provided password
                String computedHash = hashPassword(password, user.getPasswordSalt());
                
                // Compare hashes
                if (user.getPasswordHash().equals(computedHash)) {
                    logger.info("Password verification successful for user: " + username);
                    // Update last login time
                    updateLastLogin(user.getId());
                    session.close();
                    return user;
                } else {
                    logger.warning("Password verification failed for user: " + username);
                }
            } else {
                logger.warning("User not found or inactive: " + username);
            }
            
            session.close();
            return null; // Authentication failed
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error during traditional authentication for user: " + username, ex);
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Initiates OTP-based authentication by sending OTP to user's email
     * 
     * @param email The user's email address
     * @return true if OTP sent successfully, false otherwise
     */
    public boolean initiateOTPLogin(String email) {
        logger.info("Starting OTP initiation for email: " + email);
        try {
            // Find user by email
            logger.info("Looking up user by email: " + email);
            User user = findUserByEmail(email);
            if (user == null) {
                logger.warning("User not found for email: " + email);
                return false;
            }
            
            if (!user.isActive()) {
                logger.warning("User account is inactive for email: " + email);
                return false;
            }
            
            logger.info("User found and active. User ID: " + user.getId() + ", Name: " + user.getFullName());
            
            // Generate OTP
            logger.info("Generating OTP for user: " + user.getFullName());
            OTP otp = otpDao.generateOTP(user);
            if (otp == null) {
                logger.severe("OTP generation failed for user: " + user.getFullName());
                return false;
            }
            
            logger.info("OTP generated successfully. OTP ID: " + otp.getId() + ", Code: " + otp.getOtpCode());
            
            // Send OTP via email
            logger.info("Attempting to send OTP email to: " + user.getEmail());
            boolean emailSent = emailService.sendOTPEmail(user.getEmail(), otp.getOtpCode(), user.getFullName());
            
            if (emailSent) {
                logger.info("OTP email sent successfully to: " + user.getEmail());
                return true;
            } else {
                logger.severe("Email sending failed for: " + user.getEmail());
                // If email sending fails, invalidate the OTP
                logger.info("Invalidating OTP due to email failure");
                otpDao.invalidateExistingOTPs(user.getId());
                return false;
            }
            
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error during OTP initiation for email: " + email, ex);
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Verifies OTP and completes authentication
     * 
     * @param email The user's email address
     * @param otpCode The OTP code provided by user
     * @return User object if OTP verification successful, null otherwise
     */
    public User authenticateWithOTP(String email, String otpCode) {
        logger.info("Starting OTP verification for email: " + email + " with code: " + otpCode);
        try {
            // Verify OTP
            OTP otp = otpDao.verifyOTP(email, otpCode);
            if (otp == null) {
                logger.warning("OTP verification failed for email: " + email);
                return null;
            }
            
            logger.info("OTP verified successfully for email: " + email);
            
            // Get user details
            User user = findUserById(otp.getUserId());
            if (user == null || !user.isActive()) {
                logger.warning("User not found or inactive after OTP verification for email: " + email);
                return null;
            }
            
            // Update last login time
            updateLastLogin(user.getId());
            logger.info("OTP authentication completed successfully for user: " + user.getFullName());
            
            return user;
            
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error during OTP verification for email: " + email, ex);
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Checks if user can request a new OTP (rate limiting)
     * 
     * @param email The user's email address
     * @return true if user can request new OTP, false otherwise
     */
    public boolean canRequestNewOTP(String email) {
        try {
            OTP latestOTP = otpDao.findLatestOTPByEmail(email);
            if (latestOTP == null) {
                logger.fine("No previous OTP found for email: " + email + " - can request new OTP");
                return true;
            }
            
            // Check if at least 2 minutes have passed since last OTP
            LocalDateTime twoMinutesAgo = LocalDateTime.now().minusMinutes(2);
            boolean canRequest = latestOTP.getCreatedAt().isBefore(twoMinutesAgo);
            logger.fine("OTP rate limiting check for " + email + ": " + canRequest);
            return canRequest;
            
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error checking OTP rate limiting for email: " + email, ex);
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets remaining time before user can request new OTP
     * 
     * @param email The user's email address
     * @return Remaining seconds before new OTP can be requested
     */
    public long getRemainingCooldownSeconds(String email) {
        try {
            OTP latestOTP = otpDao.findLatestOTPByEmail(email);
            if (latestOTP == null) {
                return 0;
            }
            
            LocalDateTime canRequestAt = latestOTP.getCreatedAt().plusMinutes(2);
            LocalDateTime now = LocalDateTime.now();
            
            if (now.isAfter(canRequestAt)) {
                return 0;
            }
            
            return java.time.Duration.between(now, canRequestAt).getSeconds();
            
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error getting cooldown time for email: " + email, ex);
            ex.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Creates a new user in the database
     */
    public User createUser(User user, String password) {
        logger.info("Creating new user: " + user.getUsername());
        try {
            // Check if username already exists
            if (isUsernameExists(user.getUsername())) {
                logger.warning("Username already exists: " + user.getUsername());
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
            logger.info("User created successfully: " + user.getUsername());
            return user;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error creating user: " + user.getUsername(), ex);
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Updates an existing user in the database
     */
    public User updateUser(User user) {
        logger.info("Updating user: " + user.getUsername());
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.update(user);
            
            transaction.commit();
            session.close();
            logger.info("User updated successfully: " + user.getUsername());
            return user;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error updating user: " + user.getUsername(), ex);
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
            boolean exists = count > 0;
            logger.fine("Username existence check for '" + username + "': " + exists);
            return exists;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error checking username existence: " + username, ex);
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
            logger.fine("Last login updated for user ID: " + userId);
            return rowsAffected > 0;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error updating last login for user ID: " + userId, ex);
            ex.printStackTrace();
        }
        return false;
    }
    
    /**
     * Changes a user's password
     */
    public boolean changePassword(int userId, String currentPassword, String newPassword) {
        logger.info("Changing password for user ID: " + userId);
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            // First verify the current password
            User user = (User) session.get(User.class, userId);
            if (user == null) {
                logger.warning("User not found for password change: " + userId);
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
                logger.info("Password changed successfully for user ID: " + userId);
                return true;
            } else {
                logger.warning("Current password verification failed for user ID: " + userId);
            }
            
            session.close();
            return false; // Current password is incorrect
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error changing password for user ID: " + userId, ex);
            ex.printStackTrace();
        }
        return false;
    }
    
    /**
     * Resets a user's password to a new random password
     */
    public String resetPassword(String email) {
        logger.info("Resetting password for email: " + email);
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
                
                // Send password reset email
                emailService.sendPasswordResetEmail(email, newPassword, user.getFullName());
                
                session.close();
                logger.info("Password reset successfully for email: " + email);
                return newPassword;
            } else {
                logger.warning("User not found for password reset: " + email);
            }
            
            session.close();
            return null; // Email not found
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error resetting password for email: " + email, ex);
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Deactivates a user account
     */
    public boolean deactivateUser(User user) {
        logger.info("Deactivating user: " + user.getUsername());
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            user.setActive(false);
            session.update(user);
            
            transaction.commit();
            session.close();
            logger.info("User deactivated successfully: " + user.getUsername());
            return true;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error deactivating user: " + user.getUsername(), ex);
            ex.printStackTrace();
        }
        return false;
    }
    
    /**
     * Reactivates a user account
     */
    public boolean reactivateUser(User user) {
        logger.info("Reactivating user: " + user.getUsername());
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            user.setActive(true);
            session.update(user);
            
            transaction.commit();
            session.close();
            logger.info("User reactivated successfully: " + user.getUsername());
            return true;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error reactivating user: " + user.getUsername(), ex);
            ex.printStackTrace();
        }
        return false;
    }
    
    /**
     * Finds a user by ID
     */
    public User findUserById(User user) {
        return findUserById(user.getId());
    }
    
    /**
     * Finds a user by ID
     */
    public User findUserById(int userId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            User foundUser = (User) session.get(User.class, userId);
            session.close();
            logger.fine("Find user by ID " + userId + ": " + (foundUser != null ? "found" : "not found"));
            return foundUser;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error finding user by ID: " + userId, ex);
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
            logger.fine("Find user by username '" + username + "': " + (user != null ? "found" : "not found"));
            return user;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error finding user by username: " + username, ex);
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds a user by email
     */
    public User findUserByEmail(String email) {
        logger.fine("Finding user by email: " + email);
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            System.out.println("In user finding");
            Query query = session.createQuery("FROM User u WHERE u.email = :email");
            query.setParameter("email", email);
            System.out.println("email: " +email);
            User user = (User) query.uniqueResult();
            System.out.println(user);
            session.close();
            logger.fine("Find user by email '" + email + "': " + (user != null ? "found - " + user.getUsername() : "not found"));
            return user;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error finding user by email: " + email, ex);
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
            logger.fine("Found " + (users != null ? users.size() : 0) + " active users");
            return users;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error finding all active users", ex);
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
            logger.fine("Found " + (users != null ? users.size() : 0) + " users with role: " + role);
            return users;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error finding users by role: " + role, ex);
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
            logger.log(Level.SEVERE, "Error hashing password", e);
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