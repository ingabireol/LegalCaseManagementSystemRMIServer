package service.implementation;

import dao.UserDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;
import service.UserService;

/**
 * Enhanced Implementation of UserService for RMI with OTP support
 */
public class UserServiceImpl extends UnicastRemoteObject implements UserService {

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());
    private UserDao userDao;

    public UserServiceImpl() throws RemoteException {
        super();
        this.userDao = new UserDao();
        logger.info("UserServiceImpl initialized with OTP support");
    }

    @Override
    public User authenticateUser(String username, String password) throws RemoteException {
        logger.info("Attempting traditional authentication for user: " + username);
        try {
            if (username == null || username.trim().isEmpty()) {
                logger.warning("Authentication failed: Username is null or empty");
                return null;
            }
            if (password == null || password.trim().isEmpty()) {
                logger.warning("Authentication failed: Password is null or empty");
                return null;
            }
            
            User user = userDao.authenticateUser(username.trim(), password);
            if (user != null) {
                logger.info("Traditional authentication successful for user: " + username);
            } else {
                logger.warning("Traditional authentication failed for user: " + username);
            }
            return user;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during traditional authentication for user: " + username, e);
            throw new RemoteException("Authentication failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean initiateOTPLogin(String email) throws RemoteException {
        logger.info("Attempting to initiate OTP login for email: " + email);
        try {
            if (email == null || email.trim().isEmpty()) {
                logger.warning("OTP initiation failed: Email is null or empty");
                return false;
            }
            
            String cleanEmail = email.trim().toLowerCase();
            
            // Validate email format
            if (!isValidEmailFormat(cleanEmail)) {
                logger.warning("OTP initiation failed: Invalid email format: " + cleanEmail);
                return false;
            }
            
            boolean result = userDao.initiateOTPLogin(cleanEmail);
            if (result) {
                logger.info("OTP initiated successfully for email: " + cleanEmail);
            } else {
                logger.warning("OTP initiation failed for email: " + cleanEmail);
            }
            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error initiating OTP login for email: " + email, e);
            throw new RemoteException("Failed to initiate OTP login: " + e.getMessage(), e);
        }
    }

    @Override
    public User authenticateWithOTP(String email, String otpCode) throws RemoteException {
        logger.info("Attempting OTP authentication for email: " + email);
        try {
            if (email == null || email.trim().isEmpty()) {
                logger.warning("OTP authentication failed: Email is null or empty");
                return null;
            }
            if (otpCode == null || otpCode.trim().isEmpty()) {
                logger.warning("OTP authentication failed: OTP code is null or empty");
                return null;
            }
            
            String cleanEmail = email.trim().toLowerCase();
            String cleanOtpCode = otpCode.trim();
            
            // Validate OTP format (6 digits)
            if (!cleanOtpCode.matches("^\\d{6}$")) {
                logger.warning("OTP authentication failed: Invalid OTP format for email: " + cleanEmail);
                return null;
            }
            
            User user = userDao.authenticateWithOTP(cleanEmail, cleanOtpCode);
            if (user != null) {
                logger.info("OTP authentication successful for email: " + cleanEmail);
            } else {
                logger.warning("OTP authentication failed for email: " + cleanEmail);
            }
            return user;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during OTP authentication for email: " + email, e);
            throw new RemoteException("OTP authentication failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean canRequestNewOTP(String email) throws RemoteException {
        try {
            if (email == null || email.trim().isEmpty()) {
                return false;
            }
            String cleanEmail = email.trim().toLowerCase();
            boolean canRequest = userDao.canRequestNewOTP(cleanEmail);
            logger.fine("OTP request eligibility for " + cleanEmail + ": " + canRequest);
            return canRequest;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error checking OTP request eligibility for email: " + email, e);
            throw new RemoteException("Failed to check OTP request eligibility: " + e.getMessage(), e);
        }
    }

    @Override
    public long getRemainingCooldownSeconds(String email) throws RemoteException {
        try {
            if (email == null || email.trim().isEmpty()) {
                return 0;
            }
            String cleanEmail = email.trim().toLowerCase();
            long remainingSeconds = userDao.getRemainingCooldownSeconds(cleanEmail);
            logger.fine("Remaining cooldown for " + cleanEmail + ": " + remainingSeconds + " seconds");
            return remainingSeconds;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error getting cooldown time for email: " + email, e);
            // Don't throw exception for cooldown check, just return 0
            return 0;
        }
    }

    @Override
    public User createUser(User user, String password) throws RemoteException {
        logger.info("Attempting to create user: " + (user != null ? user.getUsername() : "null"));
        try {
            if (user == null) {
                logger.warning("User creation failed: User object is null");
                return null;
            }
            if (password == null || password.trim().isEmpty()) {
                logger.warning("User creation failed: Password is null or empty");
                return null;
            }
            
            User createdUser = userDao.createUser(user, password);
            if (createdUser != null) {
                logger.info("User created successfully: " + user.getUsername());
            } else {
                logger.warning("User creation failed for: " + user.getUsername());
            }
            return createdUser;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating user: " + (user != null ? user.getUsername() : "null"), e);
            throw new RemoteException("Failed to create user: " + e.getMessage(), e);
        }
    }

    @Override
    public User updateUser(User user) throws RemoteException {
        logger.info("Attempting to update user: " + (user != null ? user.getUsername() : "null"));
        try {
            if (user == null) {
                logger.warning("User update failed: User object is null");
                return null;
            }
            
            User updatedUser = userDao.updateUser(user);
            if (updatedUser != null) {
                logger.info("User updated successfully: " + user.getUsername());
            } else {
                logger.warning("User update failed for: " + user.getUsername());
            }
            return updatedUser;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating user: " + (user != null ? user.getUsername() : "null"), e);
            throw new RemoteException("Failed to update user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isUsernameExists(String username) throws RemoteException {
        try {
            if (username == null || username.trim().isEmpty()) {
                return false;
            }
            boolean exists = userDao.isUsernameExists(username.trim());
            logger.fine("Username existence check for '" + username + "': " + exists);
            return exists;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error checking username existence: " + username, e);
            throw new RemoteException("Failed to check username existence: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean changePassword(int userId, String currentPassword, String newPassword) throws RemoteException {
        logger.info("Attempting password change for user ID: " + userId);
        try {
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                logger.warning("Password change failed: Current password is null or empty");
                return false;
            }
            if (newPassword == null || newPassword.trim().isEmpty()) {
                logger.warning("Password change failed: New password is null or empty");
                return false;
            }
            
            boolean success = userDao.changePassword(userId, currentPassword, newPassword);
            if (success) {
                logger.info("Password changed successfully for user ID: " + userId);
            } else {
                logger.warning("Password change failed for user ID: " + userId);
            }
            return success;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error changing password for user ID: " + userId, e);
            throw new RemoteException("Failed to change password: " + e.getMessage(), e);
        }
    }

    @Override
    public String resetPassword(String email) throws RemoteException {
        logger.info("Attempting password reset for email: " + email);
        try {
            if (email == null || email.trim().isEmpty()) {
                logger.warning("Password reset failed: Email is null or empty");
                return null;
            }
            
            String newPassword = userDao.resetPassword(email.trim());
            if (newPassword != null) {
                logger.info("Password reset successful for email: " + email);
            } else {
                logger.warning("Password reset failed for email: " + email);
            }
            return newPassword;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error resetting password for email: " + email, e);
            throw new RemoteException("Failed to reset password: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deactivateUser(User user) throws RemoteException {
        logger.info("Attempting to deactivate user: " + (user != null ? user.getUsername() : "null"));
        try {
            if (user == null) {
                logger.warning("User deactivation failed: User object is null");
                return false;
            }
            
            boolean success = userDao.deactivateUser(user);
            if (success) {
                logger.info("User deactivated successfully: " + user.getUsername());
            } else {
                logger.warning("User deactivation failed for: " + user.getUsername());
            }
            return success;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deactivating user: " + (user != null ? user.getUsername() : "null"), e);
            throw new RemoteException("Failed to deactivate user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean reactivateUser(User user) throws RemoteException {
        logger.info("Attempting to reactivate user: " + (user != null ? user.getUsername() : "null"));
        try {
            if (user == null) {
                logger.warning("User reactivation failed: User object is null");
                return false;
            }
            
            boolean success = userDao.reactivateUser(user);
            if (success) {
                logger.info("User reactivated successfully: " + user.getUsername());
            } else {
                logger.warning("User reactivation failed for: " + user.getUsername());
            }
            return success;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reactivating user: " + (user != null ? user.getUsername() : "null"), e);
            throw new RemoteException("Failed to reactivate user: " + e.getMessage(), e);
        }
    }

    @Override
    public User findUserById(User user) throws RemoteException {
        try {
            if (user == null) {
                logger.warning("Find user by ID failed: User object is null");
                return null;
            }
            
            User foundUser = userDao.findUserById(user);
            logger.fine("Find user by ID " + user.getId() + ": " + (foundUser != null ? "found" : "not found"));
            return foundUser;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding user by ID: " + (user != null ? user.getId() : "null"), e);
            throw new RemoteException("Failed to find user by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public User findUserByUsername(String username) throws RemoteException {
        try {
            if (username == null || username.trim().isEmpty()) {
                return null;
            }
            
            User user = userDao.findUserByUsername(username.trim());
            logger.fine("Find user by username '" + username + "': " + (user != null ? "found" : "not found"));
            return user;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding user by username: " + username, e);
            throw new RemoteException("Failed to find user by username: " + e.getMessage(), e);
        }
    }

    @Override
    public User findUserByEmail(String email) throws RemoteException {
        try {
            if (email == null || email.trim().isEmpty()) {
                return null;
            }
            
            User user = userDao.findUserByEmail(email.trim());
            logger.fine("Find user by email '" + email + "': " + (user != null ? "found" : "not found"));
            return user;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding user by email: " + email, e);
            throw new RemoteException("Failed to find user by email: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> findAllActiveUsers() throws RemoteException {
        logger.fine("Attempting to find all active users");
        try {
            List<User> users = userDao.findAllActiveUsers();
            logger.fine("Found " + (users != null ? users.size() : 0) + " active users");
            return users;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding all active users", e);
            throw new RemoteException("Failed to find active users: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> findUsersByRole(String role) throws RemoteException {
        logger.fine("Attempting to find users by role: " + role);
        try {
            if (role == null || role.trim().isEmpty()) {
                return null;
            }
            
            List<User> users = userDao.findUsersByRole(role.trim());
            logger.fine("Found " + (users != null ? users.size() : 0) + " users with role: " + role);
            return users;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding users by role: " + role, e);
            throw new RemoteException("Failed to find users by role: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validates email format using regex
     * 
     * @param email The email to validate
     * @return true if email format is valid, false otherwise
     */
    private boolean isValidEmailFormat(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
}