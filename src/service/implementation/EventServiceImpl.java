package service.implementation;

import dao.EventDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.List;
import model.Event;
import service.EventService;

/**
 * Implementation of EventService for RMI
 */
public class EventServiceImpl extends UnicastRemoteObject implements EventService {

    private EventDao eventDao = new EventDao();

    public EventServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public Event createEvent(Event event) throws RemoteException {
        return eventDao.createEvent(event);
    }

    @Override
    public Event updateEvent(Event event) throws RemoteException {
        return eventDao.updateEvent(event);
    }

    @Override
    public Event deleteEvent(Event event) throws RemoteException {
        return eventDao.deleteEvent(event);
    }

    @Override
    public Event updateEventStatus(Event event, String status) throws RemoteException {
        return eventDao.updateEventStatus(event, status);
    }

    @Override
    public Event findEventById(Event event) throws RemoteException {
        return eventDao.findEventById(event);
    }

    @Override
    public Event findEventByEventId(String eventId) throws RemoteException {
        return eventDao.findEventByEventId(eventId);
    }

    @Override
    public List<Event> findEventsByCase(int caseId) throws RemoteException {
        return eventDao.findEventsByCase(caseId);
    }

    @Override
    public List<Event> findEventsByDate(LocalDate date) throws RemoteException {
        return eventDao.findEventsByDate(date);
    }

    @Override
    public List<Event> findEventsByDateRange(LocalDate startDate, LocalDate endDate) throws RemoteException {
        return eventDao.findEventsByDateRange(startDate, endDate);
    }

    @Override
    public List<Event> findEventsByStatus(String status) throws RemoteException {
        return eventDao.findEventsByStatus(status);
    }

    @Override
    public List<Event> findUpcomingEventsWithReminders() throws RemoteException {
        return eventDao.findUpcomingEventsWithReminders();
    }

    @Override
    public List<Event> findAllEvents() throws RemoteException {
        return eventDao.findAllEvents();
    }

    @Override
    public Event getEventWithCase(Event event) throws RemoteException {
        return eventDao.getEventWithCase(event);
    }
}