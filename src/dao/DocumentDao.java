package dao;

import java.time.LocalDate;
import java.util.List;
import model.Document;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Data Access Object for Document operations using Hibernate
 */
public class DocumentDao {
    
    /**
     * Creates a new document in the database
     */
    public Document createDocument(Document document) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.save(document);
            
            transaction.commit();
            session.close();
            return document;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Updates an existing document in the database
     */
    public Document updateDocument(Document document) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.update(document);
            
            transaction.commit();
            session.close();
            return document;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Deletes a document from the database
     */
    public Document deleteDocument(Document document) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.delete(document);
            
            transaction.commit();
            session.close();
            return document;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Updates the status of a document
     */
    public Document updateDocumentStatus(Document document, String status) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            document.setStatus(status);
            session.update(document);
            
            transaction.commit();
            session.close();
            return document;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds a document by ID
     */
    public Document findDocumentById(Document document) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Document foundDocument = (Document) session.get(Document.class, document.getId());
            session.close();
            return foundDocument;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds a document by document ID
     */
    public Document findDocumentByDocumentId(String documentId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Document d WHERE d.documentId = :documentId");
            query.setParameter("documentId", documentId);
            Document document = (Document) query.uniqueResult();
            session.close();
            return document;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds documents by title or description
     */
    @SuppressWarnings("unchecked")
    public List<Document> findDocumentsByText(String searchText) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Document d WHERE d.title LIKE :searchText OR d.description LIKE :searchText");
            query.setParameter("searchText", "%" + searchText + "%");
            List<Document> documents = query.list();
            session.close();
            return documents;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds documents by case ID
     */
    @SuppressWarnings("unchecked")
    public List<Document> findDocumentsByCase(int caseId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Document d WHERE d.associatedCase.id = :caseId ORDER BY d.dateAdded DESC");
            query.setParameter("caseId", caseId);
            List<Document> documents = query.list();
            session.close();
            return documents;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds documents by document type
     */
    @SuppressWarnings("unchecked")
    public List<Document> findDocumentsByType(String documentType) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Document d WHERE d.documentType = :documentType");
            query.setParameter("documentType", documentType);
            List<Document> documents = query.list();
            session.close();
            return documents;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds documents by date range
     */
    @SuppressWarnings("unchecked")
    public List<Document> findDocumentsByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Document d WHERE d.documentDate BETWEEN :startDate AND :endDate");
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            List<Document> documents = query.list();
            session.close();
            return documents;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets all documents
     */
    @SuppressWarnings("unchecked")
    public List<Document> findAllDocuments() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Document d ORDER BY d.dateAdded DESC");
            List<Document> documents = query.list();
            session.close();
            return documents;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets a document with its case information
     */
    public Document getDocumentWithCase(Document document) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            // Load document with eager fetching of case
            Query query = session.createQuery("FROM Document d LEFT JOIN FETCH d.associatedCase WHERE d.id = :documentId");
            query.setParameter("documentId", document.getId());
            Document foundDocument = (Document) query.uniqueResult();
            
            session.close();
            return foundDocument;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}