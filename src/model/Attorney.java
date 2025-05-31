package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

/**
 * Represents an attorney in the legal system.
 */
@Entity
@Table(name = "attorneys")
public class Attorney implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "attorney_id", unique = true, nullable = false)
    private String attorneyId;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    private String email;
    private String phone;
    private String specialization;
    
    @Column(name = "bar_number")
    private String barNumber;
    
    @Column(name = "hourly_rate")
    private double hourlyRate;
    
    @ManyToMany(mappedBy = "attorneys", fetch = FetchType.LAZY)
    private List<Case> cases = new ArrayList<>();
    
    @OneToMany(mappedBy = "attorney", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TimeEntry> timeEntries = new ArrayList<>();
    
    /**
     * Default constructor
     */
    public Attorney() {
    }
    
    /**
     * Constructor with essential fields
     */
    public Attorney(String attorneyId, String firstName, String lastName, String email) {
        this();
        this.attorneyId = attorneyId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getAttorneyId() { return attorneyId; }
    public void setAttorneyId(String attorneyId) { this.attorneyId = attorneyId; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    
    public String getBarNumber() { return barNumber; }
    public void setBarNumber(String barNumber) { this.barNumber = barNumber; }
    
    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
    
    public List<Case> getCases() { return cases; }
    public void setCases(List<Case> cases) { this.cases = cases; }
    
    public List<TimeEntry> getTimeEntries() { return timeEntries; }
    public void setTimeEntries(List<TimeEntry> timeEntries) { this.timeEntries = timeEntries; }
    
    /**
     * Gets the full name of the attorney
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Add a case to this attorney
     */
    public void addCase(Case legalCase) {
        if (!cases.contains(legalCase)) {
            this.cases.add(legalCase);
            if (!legalCase.getAttorneys().contains(this)) {
                legalCase.addAttorney(this);
            }
        }
    }
    
    /**
     * Get attorney's display name with specialization
     */
    public String getDisplayName() {
        if (specialization != null && !specialization.isEmpty()) {
            return getFullName() + " (" + specialization + ")";
        }
        return getFullName();
    }
    
    @Override
    public String toString() {
        return "Attorney [id=" + id + ", attorneyId=" + attorneyId + ", name=" + getFullName() + 
               ", specialization=" + specialization + "]";
    }
}