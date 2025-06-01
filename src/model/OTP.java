package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.*;

/**
 * Represents an OTP (One Time Password) for user authentication.
 */
@Entity
@Table(name = "otps")
public class OTP implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "user_id", nullable = false)
    private int userId;
    
    @Column(name = "otp_code", nullable = false, length = 6)
    private String otpCode;
    
    @Column(name = "email", nullable = false)
    private String email;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "is_used", nullable = false)
    private boolean isUsed;
    
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
    
    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;
    
    /**
     * Default constructor
     */
    public OTP() {
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusMinutes(10); // OTP expires in 10 minutes
        this.isUsed = false;
        this.attemptCount = 0;
    }
    
    /**
     * Constructor with essential fields
     */
    public OTP(int userId, String email, String otpCode) {
        this();
        this.userId = userId;
        this.email = email;
        this.otpCode = otpCode;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public boolean isUsed() { return isUsed; }
    public void setUsed(boolean used) { this.isUsed = used; }
    
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
    
    public int getAttemptCount() { return attemptCount; }
    public void setAttemptCount(int attemptCount) { this.attemptCount = attemptCount; }
    
    /**
     * Check if the OTP is expired
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    /**
     * Check if the OTP is valid (not used and not expired)
     */
    public boolean isValid() {
        return !isUsed && !isExpired();
    }
    
    /**
     * Increment the attempt count
     */
    public void incrementAttemptCount() {
        this.attemptCount++;
    }
    
    /**
     * Mark the OTP as used
     */
    public void markAsUsed() {
        this.isUsed = true;
        this.verifiedAt = LocalDateTime.now();
    }
    
    /**
     * Get remaining time in minutes before expiration
     */
    public long getRemainingMinutes() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expiresAt)) {
            return 0;
        }
        return java.time.Duration.between(now, expiresAt).toMinutes();
    }
    
    @Override
    public String toString() {
        return "OTP [id=" + id + ", userId=" + userId + ", email=" + email + 
               ", isUsed=" + isUsed + ", isExpired=" + isExpired() + "]";
    }
}