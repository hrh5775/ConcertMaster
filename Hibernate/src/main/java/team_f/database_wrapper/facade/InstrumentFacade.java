package team_f.database_wrapper.facade;

import team_f.database_wrapper.entities.*;
import team_f.domain.entities.Instrument;
import team_f.domain.enums.InstrumentType;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class InstrumentFacade extends BaseDatabaseFacade<Instrument> {
    public InstrumentFacade() {
        super();
    }

    public InstrumentFacade(EntityManager session) {
        super(session);
    }

    /**
     * Function to get all Instruments. Returns a List of Instruments
     *
     * @return instruments      List<Instrument>         returns a list of instruments
     */
    @Override
    public List<Instrument> getList() {
        EntityManager session = getCurrentSession();

        Query query = session.createQuery("from InstrumentEntity ");

        List<InstrumentEntity> instrumentEntities = query.getResultList();
        List<Instrument> instruments = new ArrayList<>(instrumentEntities.size());
        Instrument instrument;

        for (InstrumentEntity entity : instrumentEntities) {
            instrument = convertToInstrument(entity);
            instruments.add(instrument);
        }

        return instruments;
    }

    /** Function to get an domain entity Instrument object from the database_wrappe entity InsturmentEntity object.
     *
     * @param id
     * @return      instrument  Instrument
     */
    @Override
    public Instrument getByID(int id) {
        EntityManager session = getCurrentSession();
        Query query = session.createQuery("from InstrumentEntity where instrumentId = :id");
        query.setParameter("id", id);
        query.setMaxResults(1);

        List<InstrumentEntity> instrumentEntities = query.getResultList();
        Instrument instrument = new Instrument();

        if (instrumentEntities.size() > 0) {
            InstrumentEntity entity = instrumentEntities.get(0);
            instrument = convertToInstrument(entity);
        }

        return instrument;
    }

    /**
     * Function to convert a database_wrapper entity InstrumentEntity object to a domain entity Instrument object.
     *  Returns the Instrument after creating and setting information from InstrumentEntity object.
     *
     * @return instrument      Instrument        returns a instrument object
     */
    protected Instrument convertToInstrument(InstrumentEntity instrumentEntity) {
        Instrument instrument = new Instrument();

        instrument.setInstrumentID(instrumentEntity.getInstrumentId());
        instrument.setBrand(instrumentEntity.getBrand());
        instrument.setModel(instrumentEntity.getModel());

        InstrumentTypeFacade instrumentTypeFacade = new InstrumentTypeFacade(getCurrentSession());
        InstrumentType instrumentType = instrumentTypeFacade.getByID(instrumentEntity.getInstrumentType());
        instrument.setInstrumentType(instrumentType);

        return instrument;
    }

    @Override
    public int add(Instrument value) {
        return 0;
    }

    @Override
    public int update(Instrument value) {
        return 0;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }
}
