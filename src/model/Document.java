package model;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;

/**
 * Represents a legal document in the system.
 */
@Entity
@Table(name = "documents")
public class Document implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "document_id", unique = true, nullable = false)
    private String documentId;
    
    @Column(nullable = false)
    private String title;
    
    private String description;
    
    @Column(name = "document_type")
    private String documentType;
    
    @Column(name = "file_path")
    private String filePath;
    
    @Column(name = "date_added")
    private LocalDate dateAdded;
    
    @Column(name = "document_date")
    private LocalDate documentDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case associatedCase;
    
    @Column(name = "created_by")
    private int createdBy;
    
    private String status;
    
    /**
     * Default constructor
     */
    public Document() {
        this.dateAdded = LocalDate.now();
        this.status = "Active";
    }
    
    /**
     * Constructor with essential fields
     */
    public Document(String documentId, String title, String documentType, Case associatedCase) {
        this();
        this.documentId = documentId;
        this.title = title;
        this.documentType = documentType;
        this.associatedCase = associatedCase;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public LocalDate getDateAdded() { return dateAdded; }
    public void setDateAdded(LocalDate dateAdded) { this.dateAdded = dateAdded; }
    
    public LocalDate getDocumentDate() { return documentDate; }
    public void setDocumentDate(LocalDate documentDate) { this.documentDate = documentDate; }
    
    public Case getCase() { return associatedCase; }
    public void setCase(Case associatedCase) { this.associatedCase = associatedCase; }
    
    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    /**
     * Check if the document is active
     */
    public boolean isActive() {
        return "Active".equalsIgnoreCase(status);
    }
    
    /**
     * Get the file extension from the file path
     */
    public String getFileExtension() {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filePath.length() - 1) {
            return filePath.substring(lastDotIndex + 1).toUpperCase();
        }
        
        return "";
    }
    
    /**
     * Get the display name for the document
     */
    public String getDisplayName() {
        String extension = getFileExtension();
        if (!extension.isEmpty()) {
            return title + " (" + extension + ")";
        }
        return title;
    }
    
    @Override
    public String toString() {
        return "Document [id=" + id + ", documentId=" + documentId + ", title=" + title + 
               ", type=" + documentType + ", dateAdded=" + dateAdded + "]";
    }
}