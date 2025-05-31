package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;

/**
 * Represents a payment made by a client for an invoice.
 */
@Entity
@Table(name = "payments")
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "payment_id", unique = true, nullable = false)
    private String paymentId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    @Column(name = "payment_date")
    private LocalDate paymentDate;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    private String reference;
    private String notes;
    
    /**
     * Default constructor
     */
    public Payment() {
        this.paymentDate = LocalDate.now();
    }
    
    /**
     * Constructor with essential fields
     */
    public Payment(String paymentId, Invoice invoice, BigDecimal amount, String paymentMethod) {
        this();
        this.paymentId = paymentId;
        this.invoice = invoice;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    
    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { 
        this.invoice = invoice;
        if (invoice != null) {
            this.client = invoice.getClient();
        }
    }
    
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { 
        this.amount = amount;
        // Update invoice payment amount if invoice is set
        if (invoice != null) {
            invoice.recalculateAmountPaid();
        }
    }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    /**
     * Get formatted display text for the payment
     */
    public String getDisplayText() {
        return paymentMethod + " payment of " + amount + " on " + paymentDate + 
               (reference != null && !reference.isEmpty() ? " (Ref: " + reference + ")" : "");
    }
    
    @Override
    public String toString() {
        return "Payment [id=" + id + ", paymentId=" + paymentId + ", invoiceId=" + 
               (invoice != null ? invoice.getId() : "null") +
               ", amount=" + amount + ", date=" + paymentDate + ", method=" + paymentMethod + "]";
    }
}