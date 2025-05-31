package service.implementation;

import dao.TimeEntryDao;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.List;
import model.TimeEntry;
import service.TimeEntryService;

/**
 * Implementation of TimeEntryService for RMI
 */
public class TimeEntryServiceImpl extends UnicastRemoteObject implements TimeEntryService {

    private TimeEntryDao timeEntryDao = new TimeEntryDao();

    public TimeEntryServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public TimeEntry createTimeEntry(TimeEntry timeEntry) throws RemoteException {
        return timeEntryDao.createTimeEntry(timeEntry);
    }

    @Override
    public TimeEntry updateTimeEntry(TimeEntry timeEntry) throws RemoteException {
        return timeEntryDao.updateTimeEntry(timeEntry);
    }

    @Override
    public TimeEntry deleteTimeEntry(TimeEntry timeEntry) throws RemoteException {
        return timeEntryDao.deleteTimeEntry(timeEntry);
    }

    @Override
    public TimeEntry markTimeEntryAsBilled(TimeEntry timeEntry, int invoiceId) throws RemoteException {
        return timeEntryDao.markTimeEntryAsBilled(timeEntry, invoiceId);
    }

    @Override
    public TimeEntry findTimeEntryById(TimeEntry timeEntry) throws RemoteException {
        return timeEntryDao.findTimeEntryById(timeEntry);
    }

    @Override
    public TimeEntry findTimeEntryByEntryId(String entryId) throws RemoteException {
        return timeEntryDao.findTimeEntryByEntryId(entryId);
    }

    @Override
    public List<TimeEntry> findTimeEntriesByCase(int caseId) throws RemoteException {
        return timeEntryDao.findTimeEntriesByCase(caseId);
    }

    @Override
    public List<TimeEntry> findTimeEntriesByAttorney(int attorneyId) throws RemoteException {
        return timeEntryDao.findTimeEntriesByAttorney(attorneyId);
    }

    @Override
    public List<TimeEntry> findTimeEntriesByDateRange(LocalDate startDate, LocalDate endDate) throws RemoteException {
        return timeEntryDao.findTimeEntriesByDateRange(startDate, endDate);
    }

    @Override
    public List<TimeEntry> findUnbilledTimeEntriesByCase(int caseId) throws RemoteException {
        return timeEntryDao.findUnbilledTimeEntriesByCase(caseId);
    }

    @Override
    public List<TimeEntry> findTimeEntriesByInvoice(int invoiceId) throws RemoteException {
        return timeEntryDao.findTimeEntriesByInvoice(invoiceId);
    }

    @Override
    public List<TimeEntry> findAllTimeEntries() throws RemoteException {
        return timeEntryDao.findAllTimeEntries();
    }

    @Override
    public TimeEntry getTimeEntryWithDetails(TimeEntry timeEntry) throws RemoteException {
        return timeEntryDao.getTimeEntryWithDetails(timeEntry);
    }

    @Override
    public double getTotalHoursByCase(int caseId) throws RemoteException {
        return timeEntryDao.getTotalHoursByCase(caseId);
    }

    @Override
    public BigDecimal getTotalAmountByCase(int caseId) throws RemoteException {
        return timeEntryDao.getTotalAmountByCase(caseId);
    }
}