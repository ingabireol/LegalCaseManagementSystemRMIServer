package service.implementation;

import dao.UserDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.User;
import service.UserService;

/**
 * Implementation of UserService for RMI
 */
public class UserServiceImpl extends UnicastRemoteObject implements UserService {

    private UserDao userDao = new UserDao();

    public UserServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public User authenticateUser(String username, String password) throws RemoteException {
        return userDao.authenticateUser(username, password);
    }

    @Override
    public User createUser(User user, String password) throws RemoteException {
        return userDao.createUser(user, password);
    }

    @Override
    public User updateUser(User user) throws RemoteException {
        return userDao.updateUser(user);
    }

    @Override
    public boolean isUsernameExists(String username) throws RemoteException {
        return userDao.isUsernameExists(username);
    }

    @Override
    public boolean changePassword(int userId, String currentPassword, String newPassword) throws RemoteException {
        return userDao.changePassword(userId, currentPassword, newPassword);
    }

    @Override
    public String resetPassword(String email) throws RemoteException {
        return userDao.resetPassword(email);
    }

    @Override
    public boolean deactivateUser(User user) throws RemoteException {
        return userDao.deactivateUser(user);
    }

    @Override
    public boolean reactivateUser(User user) throws RemoteException {
        return userDao.reactivateUser(user);
    }

    @Override
    public User findUserById(User user) throws RemoteException {
        return userDao.findUserById(user);
    }

    @Override
    public User findUserByUsername(String username) throws RemoteException {
        return userDao.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) throws RemoteException {
        return userDao.findUserByEmail(email);
    }

    @Override
    public List<User> findAllActiveUsers() throws RemoteException {
        return userDao.findAllActiveUsers();
    }

    @Override
    public List<User> findUsersByRole(String role) throws RemoteException {
        return userDao.findUsersByRole(role);
    }
}