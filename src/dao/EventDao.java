package dao;

import java.time.LocalDate;
import java.util.List;
import model.Event;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Data Access Object for Event operations using Hibernate
 */
public class EventDao {
    
    /**
     * Creates a new event in the database
     */
    public Event createEvent(Event event) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.save(event);
            
            transaction.commit();
            session.close();
            return event;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Updates an existing event in the database
     */
    public Event updateEvent(Event event) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.update(event);
            
            transaction.commit();
            session.close();
            return event;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Deletes an event from the database
     */
    public Event deleteEvent(Event event) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            session.delete(event);
            
            transaction.commit();
            session.close();
            return event;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Updates the status of an event
     */
    public Event updateEventStatus(Event event, String status) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            
            event.setStatus(status);
            session.update(event);
            
            transaction.commit();
            session.close();
            return event;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds an event by ID
     */
    public Event findEventById(Event event) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Event foundEvent = (Event) session.get(Event.class, event.getId());
            session.close();
            return foundEvent;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds an event by event ID
     */
    public Event findEventByEventId(String eventId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Event e WHERE e.eventId = :eventId");
            query.setParameter("eventId", eventId);
            Event event = (Event) query.uniqueResult();
            session.close();
            return event;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds events by case ID
     */
    @SuppressWarnings("unchecked")
    public List<Event> findEventsByCase(int caseId) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Event e WHERE e.associatedCase.id = :caseId ORDER BY e.eventDate, e.startTime");
            query.setParameter("caseId", caseId);
            List<Event> events = query.list();
            session.close();
            return events;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds events by date
     */
    @SuppressWarnings("unchecked")
    public List<Event> findEventsByDate(LocalDate date) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Event e WHERE e.eventDate = :date ORDER BY e.startTime");
            query.setParameter("date", date);
            List<Event> events = query.list();
            session.close();
            return events;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds events by date range
     */
    @SuppressWarnings("unchecked")
    public List<Event> findEventsByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Event e WHERE e.eventDate BETWEEN :startDate AND :endDate ORDER BY e.eventDate, e.startTime");
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            List<Event> events = query.list();
            session.close();
            return events;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds events by status
     */
    @SuppressWarnings("unchecked")
    public List<Event> findEventsByStatus(String status) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Event e WHERE e.status = :status ORDER BY e.eventDate, e.startTime");
            query.setParameter("status", status);
            List<Event> events = query.list();
            session.close();
            return events;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Finds upcoming events with reminders due
     */
    @SuppressWarnings("unchecked")
    public List<Event> findUpcomingEventsWithReminders() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            // Find events where reminder is set and the event date minus reminder days equals today
            Query query = session.createQuery(
                "FROM Event e WHERE e.reminderSet = true " +
                "AND DATEDIFF(e.eventDate, CURRENT_DATE) = e.reminderDays " +
                "AND e.status != 'Completed' AND e.status != 'Cancelled' " +
                "ORDER BY e.eventDate, e.startTime"
            );
            
            List<Event> events = query.list();
            session.close();
            return events;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets all events
     */
    @SuppressWarnings("unchecked")
    public List<Event> findAllEvents() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Event e ORDER BY e.eventDate DESC, e.startTime");
            List<Event> events = query.list();
            session.close();
            return events;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets an event with its case information
     */
    public Event getEventWithCase(Event event) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            // Load event with eager fetching of case
            Query query = session.createQuery("FROM Event e LEFT JOIN FETCH e.associatedCase WHERE e.id = :eventId");
            query.setParameter("eventId", event.getId());
            Event foundEvent = (Event) query.uniqueResult();
            
            session.close();
            return foundEvent;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}