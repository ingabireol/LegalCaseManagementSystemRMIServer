package service;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Email service for sending OTP codes to users with enhanced debug logging
 */
public class EmailService {
    
    private static final Logger logger = Logger.getLogger(EmailService.class.getName());
    
    // Email configuration - Update these with your SMTP settings
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_USERNAME = "ingabireolivier99@gmail.com"; // Update this
    private static final String EMAIL_PASSWORD = "vcyo uvkm eszj bluq"; // Update this
    private static final String FROM_EMAIL = "legalcase@gmail.com"; // Update this
    private static final String FROM_NAME = "Legal Case Management System";
    
    public EmailService() {
        logger.info("EmailService initialized");
        logger.info("SMTP Configuration - Host: " + SMTP_HOST + ", Port: " + SMTP_PORT + ", From: " + FROM_EMAIL);
        
        // Check if email credentials are configured
        if ("your-email@gmail.com".equals(EMAIL_USERNAME) || "your-app-password".equals(EMAIL_PASSWORD)) {
            logger.severe("EMAIL CONFIGURATION ERROR: Please update EMAIL_USERNAME and EMAIL_PASSWORD in EmailService.java");
            logger.severe("Current EMAIL_USERNAME: " + EMAIL_USERNAME);
            logger.severe("Current EMAIL_PASSWORD: " + (EMAIL_PASSWORD.equals("your-app-password") ? "NOT CONFIGURED" : "CONFIGURED"));
        }
    }
    
    /**
     * Test method to check email configuration
     */
    public boolean testEmailConfiguration() {
        logger.info("Testing email configuration...");
        try {
            Properties props = createEmailProperties();
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
                }
            });
            
            // Test connection
            Transport transport = session.getTransport("smtp");
            transport.connect(SMTP_HOST, EMAIL_USERNAME, EMAIL_PASSWORD);
            transport.close();
            
            logger.info("Email configuration test SUCCESSFUL");
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Email configuration test FAILED", e);
            return false;
        }
    }
    
    /**
     * Sends an OTP email to the specified email address
     * 
     * @param toEmail The recipient's email address
     * @param otpCode The OTP code to send
     * @param userName The user's name for personalization
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendOTPEmail(String toEmail, String otpCode, String userName) {
        logger.info("Attempting to send OTP email to: " + toEmail + " with code: " + otpCode);
        
        // Check configuration first
        if ("your-email@gmail.com".equals(EMAIL_USERNAME) || "your-app-password".equals(EMAIL_PASSWORD)) {
            logger.severe("Cannot send email: Email service not configured properly");
            logger.severe("Please update EMAIL_USERNAME and EMAIL_PASSWORD in EmailService.java");
            return false;
        }
        
        try {
            // Set up email properties
            Properties props = createEmailProperties();
            logger.info("Email properties configured");
            
            // Create session with authentication
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    logger.fine("Authenticating with email server");
                    return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
                }
            });
            
            // Enable debug mode for JavaMail (optional - can be commented out)
            session.setDebug(true);
            
            logger.info("Email session created successfully");
            
            // Create email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Your Login OTP - Legal Case Management System");
            
            logger.info("Email message headers configured");
            
            // Create email content
            String emailContent = createOTPEmailContent(otpCode, userName);
            message.setContent(emailContent, "text/html; charset=utf-8");
            
            logger.info("Email content generated, attempting to send...");
            
            // Send email
            Transport.send(message);
            
            logger.info("OTP email sent successfully to: " + toEmail);
            return true;
            
        } catch (MessagingException me) {
            logger.log(Level.SEVERE, "MessagingException while sending OTP email to: " + toEmail, me);
            
            // Log specific details about the MessagingException
            if (me instanceof AuthenticationFailedException) {
                logger.severe("Authentication failed - check EMAIL_USERNAME and EMAIL_PASSWORD");
                logger.severe("Make sure you're using an App Password for Gmail, not your regular password");
            } else if (me instanceof SendFailedException) {
                logger.severe("Send failed - check recipient email address and SMTP settings");
            }
            
            return false;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "General exception while sending OTP email to: " + toEmail, ex);
            return false;
        }
    }
    
    /**
     * Creates email properties for SMTP configuration
     */
    private Properties createEmailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);
        
        // Additional properties for debugging
        props.put("mail.debug", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        
        logger.fine("Email properties created: " + props.toString());
        return props;
    }
    
    /**
     * Creates the HTML content for the OTP email
     * 
     * @param otpCode The OTP code
     * @param userName The user's name
     * @return HTML email content
     */
    private String createOTPEmailContent(String otpCode, String userName) {
        logger.fine("Creating email content for user: " + userName + " with OTP: " + otpCode);
        
        StringBuilder content = new StringBuilder();
        
        content.append("<!DOCTYPE html>");
        content.append("<html>");
        content.append("<head>");
        content.append("<meta charset='UTF-8'>");
        content.append("<title>Login OTP</title>");
        content.append("<style>");
        content.append("body { font-family: Arial, sans-serif; line-height: 1.6; margin: 0; padding: 20px; background-color: #f4f4f4; }");
        content.append(".container { max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }");
        content.append(".header { text-align: center; color: #2a3a56; margin-bottom: 30px; }");
        content.append(".otp-box { background-color: #0277bd; color: white; font-size: 32px; font-weight: bold; text-align: center; padding: 20px; border-radius: 8px; margin: 20px 0; letter-spacing: 8px; }");
        content.append(".warning { background-color: #fff3e0; border-left: 4px solid #ff9800; padding: 15px; margin: 20px 0; }");
        content.append(".footer { text-align: center; color: #666; font-size: 12px; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px; }");
        content.append("</style>");
        content.append("</head>");
        content.append("<body>");
        
        content.append("<div class='container'>");
        content.append("<div class='header'>");
        content.append("<h1>Legal Case Management System</h1>");
        content.append("<h2>Login Verification Code</h2>");
        content.append("</div>");
        
        content.append("<p>Dear ").append(userName != null ? userName : "User").append(",</p>");
        content.append("<p>You have requested to log into the Legal Case Management System. Please use the following One-Time Password (OTP) to complete your login:</p>");
        
        content.append("<div class='otp-box'>").append(otpCode).append("</div>");
        
        content.append("<div class='warning'>");
        content.append("<strong>Important Security Information:</strong>");
        content.append("<ul>");
        content.append("<li>This OTP is valid for <strong>10 minutes</strong> only</li>");
        content.append("<li>Do not share this code with anyone</li>");
        content.append("<li>If you did not request this login, please ignore this email</li>");
        content.append("<li>For security reasons, this code can only be used once</li>");
        content.append("</ul>");
        content.append("</div>");
        
        content.append("<p>If you're having trouble logging in or did not request this code, please contact your system administrator immediately.</p>");
        
        content.append("<p>Best regards,<br>");
        content.append("Legal Case Management System<br>");
        content.append("Security Team</p>");
        
        content.append("<div class='footer'>");
        content.append("<p>This is an automated message. Please do not reply to this email.</p>");
        content.append("<p>© 2025 Legal Case Management System. All rights reserved.</p>");
        content.append("</div>");
        
        content.append("</div>");
        content.append("</body>");
        content.append("</html>");
        
        logger.fine("Email content created successfully");
        return content.toString();
    }
    
    /**
     * Sends a password reset email (for future use)
     * 
     * @param toEmail The recipient's email address
     * @param newPassword The new temporary password
     * @param userName The user's name
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendPasswordResetEmail(String toEmail, String newPassword, String userName) {
        logger.info("Attempting to send password reset email to: " + toEmail);
        
        // Check configuration first
        if ("your-email@gmail.com".equals(EMAIL_USERNAME) || "your-app-password".equals(EMAIL_PASSWORD)) {
            logger.severe("Cannot send password reset email: Email service not configured properly");
            return false;
        }
        
        try {
            Properties props = createEmailProperties();
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
                }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Password Reset - Legal Case Management System");
            
            String emailContent = createPasswordResetEmailContent(newPassword, userName);
            message.setContent(emailContent, "text/html; charset=utf-8");
            
            Transport.send(message);
            logger.info("Password reset email sent successfully to: " + toEmail);
            return true;
            
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Failed to send password reset email to: " + toEmail, ex);
            return false;
        }
    }
    
    /**
     * Creates the HTML content for password reset email
     */
    private String createPasswordResetEmailContent(String newPassword, String userName) {
        StringBuilder content = new StringBuilder();
        
        content.append("<!DOCTYPE html>");
        content.append("<html>");
        content.append("<head>");
        content.append("<meta charset='UTF-8'>");
        content.append("<title>Password Reset</title>");
        content.append("<style>");
        content.append("body { font-family: Arial, sans-serif; line-height: 1.6; margin: 0; padding: 20px; background-color: #f4f4f4; }");
        content.append(".container { max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }");
        content.append(".header { text-align: center; color: #2a3a56; margin-bottom: 30px; }");
        content.append(".password-box { background-color: #c62828; color: white; font-size: 18px; font-weight: bold; text-align: center; padding: 20px; border-radius: 8px; margin: 20px 0; }");
        content.append(".warning { background-color: #ffebee; border-left: 4px solid #f44336; padding: 15px; margin: 20px 0; }");
        content.append(".footer { text-align: center; color: #666; font-size: 12px; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px; }");
        content.append("</style>");
        content.append("</head>");
        content.append("<body>");
        
        content.append("<div class='container'>");
        content.append("<div class='header'>");
        content.append("<h1>Legal Case Management System</h1>");
        content.append("<h2>Password Reset</h2>");
        content.append("</div>");
        
        content.append("<p>Dear ").append(userName != null ? userName : "User").append(",</p>");
        content.append("<p>Your password has been reset. Your new temporary password is:</p>");
        
        content.append("<div class='password-box'>").append(newPassword).append("</div>");
        
        content.append("<div class='warning'>");
        content.append("<strong>Important:</strong>");
        content.append("<ul>");
        content.append("<li>Please change this temporary password immediately after logging in</li>");
        content.append("<li>Do not share this password with anyone</li>");
        content.append("<li>If you did not request this reset, please contact your administrator</li>");
        content.append("</ul>");
        content.append("</div>");
        
        content.append("<p>Best regards,<br>");
        content.append("Legal Case Management System<br>");
        content.append("Security Team</p>");
        
        content.append("<div class='footer'>");
        content.append("<p>This is an automated message. Please do not reply to this email.</p>");
        content.append("<p>© 2025 Legal Case Management System. All rights reserved.</p>");
        content.append("</div>");
        
        content.append("</div>");
        content.append("</body>");
        content.append("</html>");
        
        return content.toString();
    }
}