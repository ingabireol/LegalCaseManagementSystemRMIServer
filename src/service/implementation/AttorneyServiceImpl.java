package service.implementation;

import dao.AttorneyDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.Attorney;
import service.AttorneyService;

/**
 * Implementation of AttorneyService for RMI
 */
public class AttorneyServiceImpl extends UnicastRemoteObject implements AttorneyService {

    private AttorneyDao attorneyDao = new AttorneyDao();

    public AttorneyServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public Attorney createAttorney(Attorney attorney) throws RemoteException {
        return attorneyDao.createAttorney(attorney);
    }

    @Override
    public Attorney updateAttorney(Attorney attorney) throws RemoteException {
        return attorneyDao.updateAttorney(attorney);
    }

    @Override
    public Attorney deleteAttorney(Attorney attorney) throws RemoteException {
        return attorneyDao.deleteAttorney(attorney);
    }

    @Override
    public Attorney findAttorneyById(Attorney attorney) throws RemoteException {
        return attorneyDao.findAttorneyById(attorney);
    }

    @Override
    public Attorney findAttorneyByAttorneyId(String attorneyId) throws RemoteException {
        return attorneyDao.findAttorneyByAttorneyId(attorneyId);
    }

    @Override
    public List<Attorney> findAttorneysByName(String name) throws RemoteException {
        return attorneyDao.findAttorneysByName(name);
    }

    @Override
    public List<Attorney> findAttorneysBySpecialization(String specialization) throws RemoteException {
        return attorneyDao.findAttorneysBySpecialization(specialization);
    }

    @Override
    public List<Attorney> findAttorneysByCase(int caseId) throws RemoteException {
        return attorneyDao.findAttorneysByCase(caseId);
    }

    @Override
    public List<Attorney> findAllAttorneys() throws RemoteException {
        return attorneyDao.findAllAttorneys();
    }

    @Override
    public Attorney getAttorneyWithCases(Attorney attorney) throws RemoteException {
        return attorneyDao.getAttorneyWithCases(attorney);
    }
}