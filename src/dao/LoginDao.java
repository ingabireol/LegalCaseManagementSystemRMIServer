package dao;

import model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Data Access Object for user login and authentication operations.
 */
public class LoginDao {
    private String db_url = "jdbc:mysql://localhost:3306/legalcasemgmtdb";
    private String db_username = "root";
    private String db_passwd = "Ornella12345!";
    
    /**
     * Authenticates a user by username and password
     * 
     * @param username The username
     * @param password The password (plaintext)
     * @return User object if authentication successful, null otherwise
     */
    public User authenticateUser(String username, String password) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT id, password_hash, password_salt FROM users WHERE username = ? AND active = TRUE";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("id");
                String storedHash = rs.getString("password_hash");
                String salt = rs.getString("password_salt");
                
                // Compute hash of provided password
                String computedHash = hashPassword(password, salt);
                
                // Compare hashes
                if (storedHash.equals(computedHash)) {
                    // Update last login time
                    updateLastLogin(userId);
                    
                    // Get the full user details and return
                    User user = findUserById(userId);
                    return user;
                }
            }
            
            con.close();
            return null;  // Authentication failed
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Creates a new user in the database
     * 
     * @param user The user to create
     * @param password The password (plaintext)
     * @return User object with ID populated if creation successful, null otherwise
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
            
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "INSERT INTO users (username, password_hash, password_salt, email, full_name, " +
                         "role, registration_date, last_login, active) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, NULL, ?)";
            PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pst.setString(1, user.getUsername());
            pst.setString(2, passwordHash);
            pst.setString(3, salt);
            pst.setString(4, user.getEmail());
            pst.setString(5, user.getFullName());
            pst.setString(6, user.getRole());
            pst.setDate(7, Date.valueOf(user.getRegistrationDate()));
            pst.setBoolean(8, user.isActive());
            
            int rowsAffected = pst.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
                rs.close();
                con.close();
                return user;
            }
            
            con.close();
            return null;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Checks if a username already exists
     * 
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    public boolean isUsernameExists(String username) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT COUNT(*) AS count FROM users WHERE username = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            
            ResultSet rs = pst.executeQuery();
            boolean exists = false;
            
            if (rs.next()) {
                exists = rs.getInt("count") > 0;
            }
            
            con.close();
            return exists;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Updates the last login timestamp for a user
     * 
     * @param userId The ID of the user
     * @return Number of rows affected
     */
    private int updateLastLogin(int userId) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "UPDATE users SET last_login = NOW() WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            
            int rowsAffected = pst.executeUpdate();
            con.close();
            return rowsAffected;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Changes a user's password
     * 
     * @param userId The ID of the user
     * @param currentPassword The current password
     * @param newPassword The new password
     * @return true if password changed successfully, false otherwise
     */
    public boolean changePassword(int userId, String currentPassword, String newPassword) {
        try {
            // First verify the current password
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT password_hash, password_salt FROM users WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String salt = rs.getString("password_salt");
                
                // Compute hash of provided current password
                String computedHash = hashPassword(currentPassword, salt);
                
                // Compare hashes
                if (storedHash.equals(computedHash)) {
                    // Current password is correct, update to new password
                    String newSalt = generateSalt();
                    String newPasswordHash = hashPassword(newPassword, newSalt);
                    
                    sql = "UPDATE users SET password_hash = ?, password_salt = ? WHERE id = ?";
                    PreparedStatement updatePst = con.prepareStatement(sql);
                    updatePst.setString(1, newPasswordHash);
                    updatePst.setString(2, newSalt);
                    updatePst.setInt(3, userId);
                    
                    int rowsAffected = updatePst.executeUpdate();
                    con.close();
                    return rowsAffected > 0;
                }
            }
            
            con.close();
            return false;  // Current password is incorrect
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Resets a user's password to a new random password
     * 
     * @param email The email of the user
     * @return The new password if reset successful, null otherwise
     */
    public String resetPassword(String email) {
        try {
            // First check if email exists
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT id FROM users WHERE email = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, email);
            
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("id");
                
                // Generate new random password
                String newPassword = generateRandomPassword();
                String newSalt = generateSalt();
                String newPasswordHash = hashPassword(newPassword, newSalt);
                
                // Update user's password
                sql = "UPDATE users SET password_hash = ?, password_salt = ? WHERE id = ?";
                PreparedStatement updatePst = con.prepareStatement(sql);
                updatePst.setString(1, newPasswordHash);
                updatePst.setString(2, newSalt);
                updatePst.setInt(3, userId);
                
                int rowsAffected = updatePst.executeUpdate();
                con.close();
                
                if (rowsAffected > 0) {
                    return newPassword;
                }
            }
            
            con.close();
            return null;  // Email not found or update failed
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Deactivates a user account
     * 
     * @param userId The ID of the user
     * @return true if deactivation successful, false otherwise
     */
    public boolean deactivateUser(int userId) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "UPDATE users SET active = FALSE WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            
            int rowsAffected = pst.executeUpdate();
            con.close();
            return rowsAffected > 0;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Reactivates a user account
     * 
     * @param userId The ID of the user
     * @return true if reactivation successful, false otherwise
     */
    public boolean reactivateUser(int userId) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "UPDATE users SET active = TRUE WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            
            int rowsAffected = pst.executeUpdate();
            con.close();
            return rowsAffected > 0;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Finds a user by ID
     * 
     * @param userId The ID of the user
     * @return User object if found, null otherwise
     */
    public User findUserById(int userId) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM users WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            
            ResultSet rs = pst.executeQuery();
            User user = null;
            
            if (rs.next()) {
                user = extractUserFromResultSet(rs);
            }
            
            con.close();
            return user;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Finds a user by username
     * 
     * @param username The username to search for
     * @return User object if found, null otherwise
     */
    public User findUserByUsername(String username) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            
            ResultSet rs = pst.executeQuery();
            User user = null;
            
            if (rs.next()) {
                user = extractUserFromResultSet(rs);
            }
            
            con.close();
            return user;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Finds a user by email
     * 
     * @param email The email to search for
     * @return User object if found, null otherwise
     */
    public User findUserByEmail(String email) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM users WHERE email = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, email);
            
            ResultSet rs = pst.executeQuery();
            User user = null;
            
            if (rs.next()) {
                user = extractUserFromResultSet(rs);
            }
            
            con.close();
            return user;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Gets a list of all active users
     * 
     * @return List of active users
     */
    public List<User> findAllActiveUsers() {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM users WHERE active = TRUE ORDER BY username";
            PreparedStatement pst = con.prepareStatement(sql);
            
            ResultSet rs = pst.executeQuery();
            List<User> userList = new ArrayList<>();
            
            while (rs.next()) {
                User user = extractUserFromResultSet(rs);
                userList.add(user);
            }
            
            con.close();
            return userList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Gets a list of users by role
     * 
     * @param role The role to search for
     * @return List of users with the specified role
     */
    public List<User> findUsersByRole(String role) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "SELECT * FROM users WHERE role = ? AND active = TRUE ORDER BY full_name";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, role);
            
            ResultSet rs = pst.executeQuery();
            List<User> userList = new ArrayList<>();
            
            while (rs.next()) {
                User user = extractUserFromResultSet(rs);
                userList.add(user);
            }
            
            con.close();
            return userList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Updates a user's information
     * 
     * @param user The user to update
     * @return true if update successful, false otherwise
     */
    public boolean updateUser(User user) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_username, db_passwd);
            String sql = "UPDATE users SET email = ?, full_name = ?, role = ?, active = ? WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, user.getEmail());
            pst.setString(2, user.getFullName());
            pst.setString(3, user.getRole());
            pst.setBoolean(4, user.isActive());
            pst.setInt(5, user.getId());
            
            int rowsAffected = pst.executeUpdate();
            con.close();
            return rowsAffected > 0;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Extract user data from a ResultSet row
     * 
     * @param rs ResultSet positioned at the user row
     * @return User object populated with data
     * @throws Exception If an error occurs
     */
    private User extractUserFromResultSet(ResultSet rs) throws Exception {
        User user = new User();
        
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(rs.getString("role"));
        
        // Handle dates
        Date registrationDate = rs.getDate("registration_date");
        if (registrationDate != null) {
            user.setRegistrationDate(registrationDate.toLocalDate());
        }
        
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }
        
        user.setActive(rs.getBoolean("active"));
        
        return user;
    }
    
    /**
     * Generates a random salt for password hashing
     * 
     * @return A random salt string
     */
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Hashes a password with the given salt using SHA-256
     * 
     * @param password The password to hash
     * @param salt The salt to use
     * @return The hashed password
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
     * 
     * @return A random password
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