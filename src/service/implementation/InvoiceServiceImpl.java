package service.implementation;

import dao.InvoiceDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.List;
import model.Invoice;
import service.InvoiceService;

/**
 * Implementation of InvoiceService for RMI
 */
public class InvoiceServiceImpl extends UnicastRemoteObject implements InvoiceService {

    private InvoiceDao invoiceDao = new InvoiceDao();

    public InvoiceServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public Invoice createInvoice(Invoice invoice) throws RemoteException {
        return invoiceDao.createInvoice(invoice);
    }

    @Override
    public Invoice updateInvoice(Invoice invoice) throws RemoteException {
        return invoiceDao.updateInvoice(invoice);
    }

    @Override
    public Invoice deleteInvoice(Invoice invoice) throws RemoteException {
        return invoiceDao.deleteInvoice(invoice);
    }

    @Override
    public Invoice updateInvoiceStatus(Invoice invoice, String status) throws RemoteException {
        return invoiceDao.updateInvoiceStatus(invoice, status);
    }

    @Override
    public Invoice findInvoiceById(Invoice invoice) throws RemoteException {
        return invoiceDao.findInvoiceById(invoice);
    }

    @Override
    public Invoice findInvoiceByInvoiceNumber(String invoiceNumber) throws RemoteException {
        return invoiceDao.findInvoiceByInvoiceNumber(invoiceNumber);
    }

    @Override
    public List<Invoice> findInvoicesByClient(int clientId) throws RemoteException {
        return invoiceDao.findInvoicesByClient(clientId);
    }

    @Override
    public List<Invoice> findInvoicesByCase(int caseId) throws RemoteException {
        return invoiceDao.findInvoicesByCase(caseId);
    }

    @Override
    public List<Invoice> findInvoicesByStatus(String status) throws RemoteException {
        return invoiceDao.findInvoicesByStatus(status);
    }

    @Override
    public List<Invoice> findInvoicesByDateRange(LocalDate startDate, LocalDate endDate) throws RemoteException {
        return invoiceDao.findInvoicesByDateRange(startDate, endDate);
    }

    @Override
    public List<Invoice> findOverdueInvoices() throws RemoteException {
        return invoiceDao.findOverdueInvoices();
    }

    @Override
    public List<Invoice> findAllInvoices() throws RemoteException {
        return invoiceDao.findAllInvoices();
    }

    @Override
    public Invoice getInvoiceWithDetails(Invoice invoice) throws RemoteException {
        return invoiceDao.getInvoiceWithDetails(invoice);
    }

    @Override
    public Invoice updateInvoicePaidAmount(int invoiceId) throws RemoteException {
        return invoiceDao.updateInvoicePaidAmount(invoiceId);
    }

    @Override
    public Invoice createInvoiceFromUnbilledTimeEntries(int caseId, String invoiceNumber, LocalDate dueDate) throws RemoteException {
        return invoiceDao.createInvoiceFromUnbilledTimeEntries(caseId, invoiceNumber, dueDate);
    }

    @Override
    public String generateNextInvoiceNumber() throws RemoteException {
        return invoiceDao.generateNextInvoiceNumber();
    }
}