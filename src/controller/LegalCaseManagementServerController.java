package controller;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import service.implementation.*;

/**
 * Main server controller for Legal Case Management System
 * Sets up RMI registry and binds services
 */
public class LegalCaseManagementServerController {
    
    public static void main(String[] args) {
        try {
            // Configure the properties 
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");
            
            // Create RMI registry on port 5555
            Registry registry = LocateRegistry.createRegistry(5555);
            
            // Bind services to registry
            registry.rebind("clientService", new ClientServiceImpl());
            registry.rebind("caseService", new CaseServiceImpl());
            registry.rebind("attorneyService", new AttorneyServiceImpl());
            registry.rebind("documentService", new DocumentServiceImpl());
            registry.rebind("eventService", new EventServiceImpl());
            registry.rebind("timeEntryService", new TimeEntryServiceImpl());
            registry.rebind("invoiceService", new InvoiceServiceImpl());
            registry.rebind("paymentService", new PaymentServiceImpl());
            registry.rebind("userService", new UserServiceImpl());
            
            System.out.println("Legal Case Management Server is running on port 5555");
            System.out.println("Available services:");
            System.out.println("- Client Service");
            System.out.println("- Case Service");
            System.out.println("- Attorney Service");
            System.out.println("- Document Service");
            System.out.println("- Event Service");
            System.out.println("- Time Entry Service");
            System.out.println("- Invoice Service");
            System.out.println("- Payment Service");
            System.out.println("- User Service");
            System.out.println("Server ready to accept client connections...");
            
        } catch (Exception ex) {
            System.err.println("Error starting server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}