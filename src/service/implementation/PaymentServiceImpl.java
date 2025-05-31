package service.implementation;

import dao.PaymentDao;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.List;
import model.Payment;
import service.PaymentService;

/**
 * Implementation of PaymentService for RMI
 */
public class PaymentServiceImpl extends UnicastRemoteObject implements PaymentService {

    private PaymentDao paymentDao = new PaymentDao();

    public PaymentServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public Payment createPayment(Payment payment) throws RemoteException {
        return paymentDao.createPayment(payment);
    }

    @Override
    public Payment updatePayment(Payment payment) throws RemoteException {
        return paymentDao.updatePayment(payment);
    }

    @Override
    public Payment deletePayment(Payment payment) throws RemoteException {
        return paymentDao.deletePayment(payment);
    }

    @Override
    public Payment findPaymentById(Payment payment) throws RemoteException {
        return paymentDao.findPaymentById(payment);
    }

    @Override
    public Payment findPaymentByPaymentId(String paymentId) throws RemoteException {
        return paymentDao.findPaymentByPaymentId(paymentId);
    }

    @Override
    public List<Payment> findPaymentsByInvoice(int invoiceId) throws RemoteException {
        return paymentDao.findPaymentsByInvoice(invoiceId);
    }

    @Override
    public List<Payment> findPaymentsByClient(int clientId) throws RemoteException {
        return paymentDao.findPaymentsByClient(clientId);
    }

    @Override
    public List<Payment> findPaymentsByMethod(String paymentMethod) throws RemoteException {
        return paymentDao.findPaymentsByMethod(paymentMethod);
    }

    @Override
    public List<Payment> findPaymentsByDateRange(LocalDate startDate, LocalDate endDate) throws RemoteException {
        return paymentDao.findPaymentsByDateRange(startDate, endDate);
    }

    @Override
    public List<Payment> findAllPayments() throws RemoteException {
        return paymentDao.findAllPayments();
    }

    @Override
    public Payment getPaymentWithDetails(Payment payment) throws RemoteException {
        return paymentDao.getPaymentWithDetails(payment);
    }

    @Override
    public BigDecimal getTotalPaymentsByClient(int clientId) throws RemoteException {
        return paymentDao.getTotalPaymentsByClient(clientId);
    }

    @Override
    public BigDecimal getTotalPaymentsByDateRange(LocalDate startDate, LocalDate endDate) throws RemoteException {
        return paymentDao.getTotalPaymentsByDateRange(startDate, endDate);
    }

    @Override
    public String generateNextPaymentId() throws RemoteException {
        return paymentDao.generateNextPaymentId();
    }
}