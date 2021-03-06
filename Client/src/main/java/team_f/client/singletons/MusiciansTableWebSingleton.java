package team_f.client.singletons;

import team_f.client.configuration.Configuration;
import team_f.client.helper.RequestResponseHelper;
import team_f.client.pages.PageAction;
import team_f.client.pages.musicianmanagement.MusicianManagement;
import team_f.client.pages.musicianmanagement.PersonParameter;
import team_f.client.pages.musicianmanagement.PersonReturnValue;
import team_f.jsonconnector.common.URIList;
import team_f.jsonconnector.entities.*;
import team_f.jsonconnector.entities.list.InstrumentTypeList;
import team_f.jsonconnector.entities.list.PersonList;
import team_f.jsonconnector.entities.special.errorlist.PersonErrorList;
import team_f.jsonconnector.enums.request.ActionType;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MusiciansTableWebSingleton {
    private static MusicianManagement _musicianTable;
    private static Configuration _configuration;

    private MusiciansTableWebSingleton() {
    }

    public static MusicianManagement getInstance(Configuration configuration) {
        if (_musicianTable == null) {
            _configuration = configuration;
            _musicianTable = new MusicianManagement();

            _musicianTable.setOnLoad(new PageAction<PersonReturnValue, PersonParameter>() {
                @Override
                public PersonReturnValue doAction(PersonParameter value) {
                    // @TODO: fix issues
                    /*if(value != null) {
                        Request request = new Request();
                        request.setActionType(ActionType.GET_ALL);

                        InstrumentTypeList instrumentTypeList = (InstrumentTypeList) RequestResponseHelper.writeAndReadJSONObject(getInstrumentTypeList(), request, InstrumentTypeList.class);

                        if(instrumentTypeList != null) {
                            PersonReturnValue personReturnValue = new PersonReturnValue();
                            personReturnValue.setInstrumentTypeList(instrumentTypeList.getInstrumentTypeList());

                            return personReturnValue;
                        }
                    }*/

                    return null;
                }
            });

            _musicianTable.setOnLoadList(new PageAction<List<Person>, PersonParameter>() {
                @Override
                public List<Person> doAction(PersonParameter value) {
                    if(value != null) {
                        Request request = new Request();
                        request.setActionType(ActionType.GET_ALL);

                        PersonList personList = (PersonList) RequestResponseHelper.writeAndReadJSONObject(getPersonURL(), request, PersonList.class);

                        if(personList != null) {
                            return personList.getPersonList();
                        }
                    }

                    return null;
                }
            });

            _musicianTable.setOnCreate(new PageAction<PersonErrorList, Person>() {
                @Override
                public PersonErrorList doAction(Person value) {
                    Request request = new Request();
                    request.setActionType(ActionType.CREATE);
                    request.setEntity(value);

                    PersonErrorList errorList = (PersonErrorList) RequestResponseHelper.writeAndReadJSONObject(getRegisterURL(), request, PersonErrorList.class);
                    return errorList;
                }
            });

            _musicianTable.setOnEdit(new PageAction<PersonErrorList, Person>() {
                @Override
                public PersonErrorList doAction(Person value) {
                    if(value != null) {
                        Request request = new Request();
                        request.setActionType(ActionType.UPDATE);
                        request.setEntity(value);

                        PersonErrorList errorList = (PersonErrorList) RequestResponseHelper.writeAndReadJSONObject(getPersonURL(), request, PersonErrorList.class);
                        return errorList;
                    }

                    return null;
                }
            });

            _musicianTable.setOnDelete(new PageAction<PersonErrorList, Person>() {
                @Override
                public PersonErrorList doAction(Person value) {
                    // @TODO: implement
                    return null;
                }
            });
        }

        return _musicianTable;
    }

    private static URL getRegisterURL() {
        try {
            return new URL(new URL(_configuration.getRootURI()), URIList.register);
        } catch (MalformedURLException e) {
        }

        return null;
    }

    private static URL getPersonURL() {
        try {
            return new URL(new URL(_configuration.getRootURI()), URIList.person);
        } catch (MalformedURLException e) {
        }

        return null;
    }
}


