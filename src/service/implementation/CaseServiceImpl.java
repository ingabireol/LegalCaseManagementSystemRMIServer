package service.implementation;

import dao.CaseDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import model.Case;
import service.CaseService;

/**
 * Implementation of CaseService for RMI
 */
public class CaseServiceImpl extends UnicastRemoteObject implements CaseService {

    private CaseDao caseDao = new CaseDao();

    public CaseServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public Case createCase(Case legalCase) throws RemoteException {
        return caseDao.createCase(legalCase);
    }

    @Override
    public Case updateCase(Case legalCase) throws RemoteException {
        return caseDao.updateCase(legalCase);
    }

    @Override
    public Case deleteCase(Case legalCase) throws RemoteException {
        return caseDao.deleteCase(legalCase);
    }

    @Override
    public Case updateCaseStatus(Case legalCase, String status) throws RemoteException {
        return caseDao.updateCaseStatus(legalCase, status);
    }

    @Override
    public Case findCaseById(Case legalCase) throws RemoteException {
        return caseDao.findCaseById(legalCase);
    }

    @Override
    public Case findCaseByCaseNumber(String caseNumber) throws RemoteException {
        return caseDao.findCaseByCaseNumber(caseNumber);
    }

    @Override
    public List<Case> findCasesByText(String searchText) throws RemoteException {
        return caseDao.findCasesByText(searchText);
    }

    @Override
    public List<Case> findCasesByClient(int clientId) throws RemoteException {
        return caseDao.findCasesByClient(clientId);
    }

    @Override
    public List<Case> findCasesByAttorney(int attorneyId) throws RemoteException {
        return caseDao.findCasesByAttorney(attorneyId);
    }

    @Override
    public List<Case> findCasesByStatus(String status) throws RemoteException {
        return caseDao.findCasesByStatus(status);
    }

    @Override
    public List<Case> findCasesByType(String caseType) throws RemoteException {
        return caseDao.findCasesByType(caseType);
    }

    @Override
    public List<Case> findCasesByDateRange(LocalDate startDate, LocalDate endDate) throws RemoteException {
        return caseDao.findCasesByDateRange(startDate, endDate);
    }

    @Override
    public List<Case> findAllCases() throws RemoteException {
        return caseDao.findAllCases();
    }

    @Override
    public Case getCaseWithDetails(Case legalCase) throws RemoteException {
        return caseDao.getCaseWithDetails(legalCase);
    }

    @Override
    public boolean assignAttorneyToCase(int caseId, int attorneyId) throws RemoteException {
        return caseDao.assignAttorneyToCase(caseId, attorneyId);
    }

    @Override
    public boolean removeAttorneyFromCase(int caseId, int attorneyId) throws RemoteException {
        return caseDao.removeAttorneyFromCase(caseId, attorneyId);
    }

    @Override
    public List<Case> getCasesDueForReview(int daysBefore) throws RemoteException {
        return caseDao.getCasesDueForReview(daysBefore);
    }

    @Override
    public List<Case> getActiveCases() throws RemoteException {
        return caseDao.getActiveCases();
    }

    @Override
    public List<Case> getCasesWithUpcomingDeadlines(int daysAhead) throws RemoteException {
        return caseDao.getCasesWithUpcomingDeadlines(daysAhead);
    }

    @Override
    public String generateNextCaseNumber() throws RemoteException {
        return caseDao.generateNextCaseNumber();
    }

    @Override
    public Map<String, Object> getCaseStatistics() throws RemoteException {
        return caseDao.getCaseStatistics();
    }
}