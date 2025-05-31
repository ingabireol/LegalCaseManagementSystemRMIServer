package dao;

import java.util.List;
import model.Client;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Data Access Object for Client operations using Hibernate
 */
public class ClientDao {
    
    /**
     * Creates a new client in the database
     */
    public Client createClient(Client client) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.save(client);
            
            transaction.commit();
            session.close();
            return client;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Updates an existing client in the database
     */
    public Client updateClient(Client client) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.update(client);
            
            transaction.commit();
            session.close();
            return client;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Deletes a client from the database
     */
    public Client deleteClient(Client client) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            // Check if client has cases
            Query query = session.createQuery("SELECT COUNT(c) FROM Case c WHERE c.client.id = :clientId");
            query.setParameter("clientId", client.getId());
            Long caseCount = (Long) query.uniqueResult();
            
            if (caseCount > 0) {
                // Cannot delete client with cases
                transaction.rollback();
                session.close();
                return null;
            }
            
            session.delete(client);
            
            transaction.commit();
            session.close();
            return client;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds a client by ID
     */
    public Client findClientById(Client client) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Client foundClient = (Client) session.get(Client.class, client.getId());
            session.close();
            return foundClient;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds a client by client ID
     */
    public Client findClientByClientId(String clientId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Client c WHERE c.clientId = :clientId");
            query.setParameter("clientId", clientId);
            Client client = (Client) query.uniqueResult();
            session.close();
            return client;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds clients by name or contact person
     */
    @SuppressWarnings("unchecked")
    public List<Client> findClientsByName(String name) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Client c WHERE c.name LIKE :name OR c.contactPerson LIKE :name");
            query.setParameter("name", "%" + name + "%");
            List<Client> clients = query.list();
            session.close();
            return clients;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds a client by email
     */
    public Client findClientByEmail(String email) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Client c WHERE c.email = :email");
            query.setParameter("email", email);
            Client client = (Client) query.uniqueResult();
            session.close();
            return client;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds clients by client type
     */
    @SuppressWarnings("unchecked")
    public List<Client> findClientsByType(String clientType) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Client c WHERE c.clientType = :clientType");
            query.setParameter("clientType", clientType);
            List<Client> clients = query.list();
            session.close();
            return clients;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets all clients
     */
    @SuppressWarnings("unchecked")
    public List<Client> findAllClients() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Client c ORDER BY c.name");
            List<Client> clients = query.list();
            session.close();
            return clients;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets a client with all their cases loaded
     */
    public Client getClientWithCases(Client client) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            // Load client with eager fetching of cases
            Query query = session.createQuery("FROM Client c LEFT JOIN FETCH c.cases WHERE c.id = :clientId");
            query.setParameter("clientId", client.getId());
            Client foundClient = (Client) query.uniqueResult();
            
            session.close();
            return foundClient;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}