package team_f.database_wrapper.facade;

import javafx.util.Pair;
import team_f.database_wrapper.entities.*;
import team_f.database_wrapper.helper.StoreHelper;
import team_f.domain.entities.EventDuty;
import team_f.domain.entities.Instrumentation;
import team_f.domain.entities.MusicalWork;
import team_f.domain.entities.Person;
import team_f.domain.enums.EventStatus;
import team_f.domain.enums.EventType;
import team_f.domain.enums.InstrumentType;
import team_f.domain.enums.properties.EventDutyProperty;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class EventFacade extends BaseDatabaseFacade<EventDuty> {
    private PersonFacade _personFacade = new PersonFacade();

    public EventFacade() {
        super();
    }

    public EventFacade(EntityManager session) {
        super(session);
    }

    /**
     * Function to add a ModelLogic. Returns the EventDutyId after saving it in entities
     * paul:
     * converts domain entity eventDuty object into database_wrapper entity EventDutyIntity object
     * merge if eventDutyID > 0	else persist
     * if it isn't a nonmusical event a database_wrapper entity object EventDutyMusicalWorkEntity is created
     * and the musicalWorkFacade gets the list of musical work Entities for this event
     * checks for and avoids duplicated primary keys for musicalwork and eventIds
     * set EventDuty and MusicalWork and alternativeInstrumentations for the EventDutyMusicalWorkEntity
     * persists the EventDutyMusicalWorkEntity and adds it to EventDutyMusicalWorkList
     * merge if eventDutyID > 0	else persist
     * uses StoreHelpers storeEntities-Method to commit or rolls back
     * returns the stored event's ID
     *
     * @return EventDutyId      int         returns the primary key of the event
     */
    private Integer addEvent(EventDuty event) {
        EntityManager session = getCurrentSession();
        session.getTransaction().begin();

        EventDutyEntity eventEntity = convertToEventDutyEntity(event);

        if (eventEntity.getEventDutyId() > 0) {
            session.merge(eventEntity);
        } else {
            session.persist(eventEntity);
            session.flush();
        }

        if (!(eventEntity.getEventType().equals(EventType.NonMusicalEvent))) {
            Collection<EventDutyMusicalWorkEntity> emweList = new LinkedList<>();
            EventDutyMusicalWorkEntity emwe;
            MusicalWorkFacade musicalWorkFacade = new MusicalWorkFacade(getCurrentSession());
            List<MusicalWork> intermediateMusicalWorkEntities = musicalWorkFacade.getMusicalWorksForEvent(eventEntity.getEventDutyId());

            loop:
            for (MusicalWork musicalWork : event.getMusicalWorkList()) {
                // @TODO: this loop and the following if are only to avoid duplicated primary keys for musicalwork and eventIds
                // @TODO: a possible workaround could be to remove the intermediate table objects which will be updated
                // @TODO: Hibernate does not really like manyToMany relationships
                for (MusicalWork intermeWork : intermediateMusicalWorkEntities) {
                    if (intermeWork.getMusicalWorkID() == musicalWork.getMusicalWorkID()) {
                        continue loop;
                    }

                    break;
                }

                emwe = new EventDutyMusicalWorkEntity();
                emwe.setEventDuty(eventEntity.getEventDutyId());
                emwe.setMusicalWork(musicalWork.getMusicalWorkID());

                if(musicalWork.getAlternativeInstrumentation() != null) {
                    emwe.setAlternativeInstrumentation(musicalWork.getAlternativeInstrumentation().getInstrumentationID());
                }

                session.persist(emwe);

                emweList.add(emwe);
            }

            eventEntity.setEventDutyMusicalWorksByEventDutyId(emweList);

            if (eventEntity.getEventDutyId() > 0) {
                session.merge(eventEntity);
            } else {
                session.persist(eventEntity);
                session.flush();
            }
        }

        StoreHelper.storeEntities(session);
        return eventEntity.getEventDutyId();
    }

    /** Function to find one event with a specific ID from the DB
     *  converts the database_wrapper EventDutyEntity object to a domain entity EventDuty object
     *  and sets the musical Works for this domain entity from the DB with MusicalWorkFacade
     * @param id
     * @return  event
     */
    @Override
    public EventDuty getByID(int id) {
        EntityManager session = getCurrentSession();
        Query query = session.createQuery("from EventDutyEntity where eventDutyId = :id");
        query.setParameter("id", id);
        query.setMaxResults(1);

        List<EventDutyEntity> eventDutyEntities = query.getResultList();
        EventDuty event = new EventDuty();

        if (eventDutyEntities.size() > 0) {
            EventDutyEntity e = eventDutyEntities.get(0);
            event = convertToEventDuty(e);

            MusicalWorkFacade musicalWorkFacade = new MusicalWorkFacade(getCurrentSession());

            event.setMusicalWorkList(musicalWorkFacade.getMusicalWorksForEvent(e.getEventDutyId()));
        }

        return event;
    }

    /** Function to get Events for a specific month in a specific year
     *  converts the database_wrapper objects into domain objects
     *
     * @param month
     * @param year
     * @return  events
     */
    public List<EventDuty> getEventsByMonth(int month, int year) {
        EntityManager session = getCurrentSession();

        Query query = session.createQuery("from EventDutyEntity where MONTH(starttime) = :month and YEAR(starttime) = :year");
        query.setParameter("month", month);
        query.setParameter("year", year);

        List<EventDutyEntity> eventEntities = query.getResultList();
        List<EventDuty> events = new ArrayList<>();

        for (EventDutyEntity eventDutyEntity : eventEntities) {
            EventDuty event = convertToEventDuty(eventDutyEntity);

            events.add(event);
        }

        return events;
    }

    /** Function to get Events for a specific day in a specific month in a specific year
     *  converts the database_wrapper objects into domain objects
     *
     * @param day
     * @param month
     * @param year
     * @return      events
     */
    public List<EventDuty> getEventsByDay(int day, int month, int year) {
        EntityManager session = getCurrentSession();

        Query query = session.createQuery("from EventDutyEntity where DAY(starttime) = :day and MONTH(starttime) = :month and YEAR(starttime) = :year");
        query.setParameter("day", day);
        query.setParameter("month", month);
        query.setParameter("year", year);

        List<EventDutyEntity> eventEntities = query.getResultList();
        List<EventDuty> events = new ArrayList<>();

        for (EventDutyEntity eventDutyEntity : eventEntities) {
            EventDuty event;
            event = convertToEventDuty(eventDutyEntity);

            events.add(event);
        }

        return events;
    }

    /** Function to get Events for a specific time frame
     *  converts the database_wrapper objects into domain objects

     *
     * @param start
     * @param end
     * @return    events
     */
    public List<EventDuty> getEventsByTimeFrame(LocalDateTime start, LocalDateTime end) {
        EntityManager session = getCurrentSession();

        Query query = session.createQuery("from EventDutyEntity where not ((:end < starttime) OR (:start > endtime))");
        query.setParameter("end", end);
        query.setParameter("start", start);

        List<EventDutyEntity> eventEntities = query.getResultList();
        List<EventDuty> events = new ArrayList<>();

        for (EventDutyEntity eventDutyEntity : eventEntities) {
            EventDuty event = convertToEventDuty(eventDutyEntity);

            events.add(event);
        }

        return events;
    }

    /**
     * Function to convert dadabase_wrapper EventDutyEntity Object to domain entity EventDuty object.
     * Returns the EventDuty after creating and setting information from EventDutyEntity object.
     *
     * @return eventDuty    EventDuty       returns EventDuty Object
     */
    protected EventDuty convertToEventDuty(EventDutyEntity entity) {
        EventDuty event = new EventDuty();

        event.setEventDutyID(entity.getEventDutyId());
        event.setName(entity.getName());
        event.setLocation(entity.getLocation());
        event.setConductor(entity.getConductor());
        event.setStartTime(entity.getStarttime());
        event.setDescription(entity.getDescription());
        event.setEndTime(entity.getEndtime());
        event.setEventStatus(EventStatus.valueOf(entity.getEventStatus().toString()));
        event.setEventType(EventType.valueOf(entity.getEventType().toString()));
        event.setDefaultPoints(entity.getDefaultPoints());

        if (entity.getRehearsalFor() != null) {
            event.setRehearsalFor(getByID(entity.getRehearsalFor()));
        }

        InstrumentationFacade instrumentationFacade = new InstrumentationFacade(getCurrentSession());
        MusicalWorkFacade musicalWorkFacade = new MusicalWorkFacade(getCurrentSession());

        for (MusicalWork musicalWork : musicalWorkFacade.getMusicalWorksForEvent(entity.getEventDutyId())) {
            if(musicalWork.getAlternativeInstrumentation() != null) {
                event.addMusicalWork(musicalWork, instrumentationFacade.getByID(musicalWork.getAlternativeInstrumentation().getInstrumentationID()));
            } else {
                event.addMusicalWork(musicalWork, null);
            }
        }

        return event;
    }

    /**
     * Function to convert domain entity EventDuty Object to database_wrapper entity EventDutyEntity object.
     * Returns the EventDutyEntity after creating and setting information from EventDuty object.
     * @return eventDutyEntity    EventDutyEntity       returns EventDutyEntity Object
     */
    protected EventDutyEntity convertToEventDutyEntity(EventDuty event) {
        EventDutyEntity eventDutyEntity = new EventDutyEntity();

        eventDutyEntity.setEventDutyId(event.getEventDutyID());
        eventDutyEntity.setName(event.getName());
        eventDutyEntity.setDescription(event.getDescription());
        eventDutyEntity.setStarttime(event.getStartTime());
        eventDutyEntity.setEndtime(event.getEndTime());
        eventDutyEntity.setEventType(team_f.database_wrapper.enums.EventType.valueOf(event.getEventType().toString()));
        eventDutyEntity.setEventStatus(team_f.database_wrapper.enums.EventStatus.valueOf(event.getEventStatus().toString()));
        eventDutyEntity.setConductor(event.getConductor());
        eventDutyEntity.setLocation(event.getLocation());
        eventDutyEntity.setDefaultPoints(event.getDefaultPoints());
        if (event.getRehearsalFor() != null) {
            eventDutyEntity.setRehearsalFor(event.getRehearsalFor().getEventDutyID());
        }

        // There are only a few musical works associated with an event. Therefore get the full list instead of asking the DB every time and modify the enums when needed.
        Collection<EventDutyMusicalWorkEntity> eventDutyMusicalWorkEntities = eventDutyEntity.getEventDutyMusicalWorksByEventDutyId();
        List<MusicalWork> musicalWorks = event.getMusicalWorkList();

        if (eventDutyMusicalWorkEntities != null) {
            for (MusicalWork musicalWork : musicalWorks) {
                for (EventDutyMusicalWorkEntity item : eventDutyMusicalWorkEntities) {
                    if (item.getMusicalWork() == musicalWork.getMusicalWorkID() && item.getEventDuty() == event.getEventDutyID()) {
                        if(musicalWork.getAlternativeInstrumentation() != null) {
                            item.setAlternativeInstrumentation(musicalWork.getAlternativeInstrumentation().getInstrumentationID());
                        }
                    }
                }
            }

            eventDutyEntity.setEventDutyMusicalWorksByEventDutyId(eventDutyMusicalWorkEntities);
        }

        return eventDutyEntity;
    }

    /** Function to calculate the maximum number of musicians needed for an event. First sets the required number of
     *      instruments to 0 for each insturmentType. For the instrumentationList of the eventDuty checks if more than 0
     *          instruments of the instrumentType are required and sets that number into the maxInstrumentation of the
     *          eventDuty.
     *
     * @param eventDuty
     */
    public void calculateMaxInstrumentation(EventDuty eventDuty) {

        Instrumentation maxInstrumentation = new Instrumentation();
        for (InstrumentType instrumentType : InstrumentType.values()) {
            maxInstrumentation.setByInstrumentType(instrumentType, 0);
        }

        for (Instrumentation instrumentation : eventDuty.getInstrumentationList()) {
            for (InstrumentType instrumentType: InstrumentType.values()) {
                if (maxInstrumentation.getByInstrumentType(instrumentType) < instrumentation.getByInstrumentType(instrumentType)) {
                    maxInstrumentation.setByInstrumentType(instrumentType, instrumentation.getByInstrumentType(instrumentType));
                }
            }
        }

        eventDuty.setMaxInstrumentation(maxInstrumentation);
    }

    /** Function returns errorList which's entries show if and for what instrumentType there are not enough musicians
     *
     * @param eventDuty
     * @return  errorList
     */
    public List<Pair<String, String>> evaluateMusicianCountForEvent(EventDuty eventDuty) {
        List<Pair<String, String>> errorList = new LinkedList<>();
        List<EventDuty> eventList;

        eventList = getEventsByTimeFrame(eventDuty.getStartTime(), eventDuty.getEndTime());

        calculateMaxInstrumentation(eventDuty);

        List<Pair<InstrumentType, List<Person>>> list = _personFacade.getMusicianListByPlayedInstrumentType(_personFacade.getList());
        Instrumentation totalInstrumentation = eventDuty.getMaxInstrumentation();

        for (EventDuty event : eventList) {
            calculateMaxInstrumentation(event);
            totalInstrumentation.addToInstrumentations(event.getMaxInstrumentation());
        }

        for (InstrumentType instrumentType : InstrumentType.values()) {
            Pair<InstrumentType, List<Person>> pairList = _personFacade.getMusicianListByInstrumentType(instrumentType, list);

            if (totalInstrumentation.getByInstrumentType(instrumentType) > pairList.getValue().size()) {
                errorList.add(new Pair<>(String.valueOf(EventDutyProperty.START_DATE), "not enough musicians for section " + instrumentType + " at the given timeframe"));
            }
        }

        return errorList;
    }

    @Override
    public List<EventDuty> getList() {
        return getEventsByTimeFrame(LocalDateTime.now().minusYears(100), LocalDateTime.now().plusYears(100));
    }

    @Override
    public int add(EventDuty value) {
        return addEvent(value);
    }

    @Override
    public int update(EventDuty value) {
        return addEvent(value);
    }

    @Override
    public boolean delete(int id) {
        return false;
    }
}
