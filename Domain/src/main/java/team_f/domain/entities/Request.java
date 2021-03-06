package team_f.domain.entities;

import team_f.domain.enums.EntityType;
import team_f.domain.enums.RequestType;
import team_f.domain.enums.properties.InstrumentTypeProperty;

public class Request extends BaseDomainEntity<InstrumentTypeProperty> {
    private RequestType _requestType;
    private String _description;
    private EventDuty _eventDuty;
    private Person _person;

    public Request() {
        super(EntityType.REQUEST);
    }

    public RequestType getRequestType() {
        return _requestType;
    }

    public String getDescription() {
        return _description;
    }

    public EventDuty getEventDuty() {
        return _eventDuty;
    }

    public Person getPerson() {
        return _person;
    }

    public void setRequestType(RequestType requestType) {
        _requestType = requestType;
    }

    public void setEventDuty(EventDuty eventDuty) {
        _eventDuty = eventDuty;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public void setPerson(Person person) {
        _person = person;
    }

    @Override
    public int getID() {
        return -1;
    }
}
