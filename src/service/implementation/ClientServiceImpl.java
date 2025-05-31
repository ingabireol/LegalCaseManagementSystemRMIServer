package service.implementation;

import dao.ClientDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.Client;
import service.ClientService;

/**
 * Implementation of ClientService for RMI
 */
public class ClientServiceImpl extends UnicastRemoteObject implements ClientService {

    private ClientDao clientDao = new ClientDao();

    public ClientServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public Client createClient(Client client) throws RemoteException {
        return clientDao.createClient(client);
    }

    @Override
    public Client updateClient(Client client) throws RemoteException {
        return clientDao.updateClient(client);
    }

    @Override
    public Client deleteClient(Client client) throws RemoteException {
        return clientDao.deleteClient(client);
    }

    @Override
    public Client findClientById(Client client) throws RemoteException {
        return clientDao.findClientById(client);
    }

    @Override
    public Client findClientByClientId(String clientId) throws RemoteException {
        return clientDao.findClientByClientId(clientId);
    }

    @Override
    public List<Client> findClientsByName(String name) throws RemoteException {
        return clientDao.findClientsByName(name);
    }

    @Override
    public Client findClientByEmail(String email) throws RemoteException {
        return clientDao.findClientByEmail(email);
    }

    @Override
    public List<Client> findClientsByType(String clientType) throws RemoteException {
        return clientDao.findClientsByType(clientType);
    }

    @Override
    public List<Client> findAllClients() throws RemoteException {
        return clientDao.findAllClients();
    }

    @Override
    public Client getClientWithCases(Client client) throws RemoteException {
        return clientDao.getClientWithCases(client);
    }
}