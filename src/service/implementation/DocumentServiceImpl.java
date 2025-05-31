package service.implementation;

import dao.DocumentDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.List;
import model.Document;
import service.DocumentService;

/**
 * Implementation of DocumentService for RMI
 */
public class DocumentServiceImpl extends UnicastRemoteObject implements DocumentService {

    private DocumentDao documentDao = new DocumentDao();

    public DocumentServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public Document createDocument(Document document) throws RemoteException {
        return documentDao.createDocument(document);
    }

    @Override
    public Document updateDocument(Document document) throws RemoteException {
        return documentDao.updateDocument(document);
    }

    @Override
    public Document deleteDocument(Document document) throws RemoteException {
        return documentDao.deleteDocument(document);
    }

    @Override
    public Document updateDocumentStatus(Document document, String status) throws RemoteException {
        return documentDao.updateDocumentStatus(document, status);
    }

    @Override
    public Document findDocumentById(Document document) throws RemoteException {
        return documentDao.findDocumentById(document);
    }

    @Override
    public Document findDocumentByDocumentId(String documentId) throws RemoteException {
        return documentDao.findDocumentByDocumentId(documentId);
    }

    @Override
    public List<Document> findDocumentsByText(String searchText) throws RemoteException {
        return documentDao.findDocumentsByText(searchText);
    }

    @Override
    public List<Document> findDocumentsByCase(int caseId) throws RemoteException {
        return documentDao.findDocumentsByCase(caseId);
    }

    @Override
    public List<Document> findDocumentsByType(String documentType) throws RemoteException {
        return documentDao.findDocumentsByType(documentType);
    }

    @Override
    public List<Document> findDocumentsByDateRange(LocalDate startDate, LocalDate endDate) throws RemoteException {
        return documentDao.findDocumentsByDateRange(startDate, endDate);
    }

    @Override
    public List<Document> findAllDocuments() throws RemoteException {
        return documentDao.findAllDocuments();
    }

    @Override
    public Document getDocumentWithCase(Document document) throws RemoteException {
        return documentDao.getDocumentWithCase(document);
    }
}