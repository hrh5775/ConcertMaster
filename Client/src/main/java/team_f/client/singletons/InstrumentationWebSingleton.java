package team_f.client.singletons;

import team_f.client.configuration.Configuration;
import team_f.client.helper.RequestResponseHelper;
import team_f.client.pages.PageAction;
import team_f.client.pages.instrumentationmanagement.InstrumentationManagement;
import team_f.client.pages.instrumentationmanagement.InstrumentationParameter;
import team_f.jsonconnector.common.URIList;
import team_f.jsonconnector.entities.Instrumentation;
import team_f.jsonconnector.entities.Request;
import team_f.jsonconnector.entities.list.InstrumentationList;
import team_f.jsonconnector.entities.special.errorlist.InstrumentationErrorList;
import team_f.jsonconnector.enums.request.ActionType;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class InstrumentationWebSingleton {
    private static InstrumentationManagement _instrumentation;
    private static Configuration _configuration;

    private InstrumentationWebSingleton(){

    }

    public static InstrumentationManagement getInstance(Configuration configuration){
        if(_instrumentation == null) {
            _configuration = configuration;
            _instrumentation = new InstrumentationManagement();

            _instrumentation.setOnLoadList(new PageAction<List<Instrumentation>, InstrumentationParameter>() {
                @Override
                public List<Instrumentation> doAction(InstrumentationParameter value) {
                    if(value != null) {
                        Request request = new Request();
                        request.setActionType(ActionType.GET_ALL);

                        InstrumentationList instrumentationList = (InstrumentationList) RequestResponseHelper.writeAndReadJSONObject(getInstrumentationURL(), request, InstrumentationList.class);

                        if(instrumentationList != null) {
                            return instrumentationList.getInstrumentationList();
                        }
                    }

                    return null;
                }
            });
            _instrumentation.setOnCreate(new PageAction<InstrumentationErrorList, Instrumentation>() {
                @Override
                public InstrumentationErrorList doAction(Instrumentation value) {
                    Request request = new Request();
                    request.setActionType(ActionType.CREATE);
                    request.setEntity(value);

                    InstrumentationErrorList errorList = (InstrumentationErrorList) RequestResponseHelper.writeAndReadJSONObject(getInstrumentationURL(), request, InstrumentationErrorList.class);
                    return errorList;
                }

            });

            _instrumentation.setOnDelete(new PageAction<InstrumentationErrorList, Instrumentation>() {
                @Override
                public InstrumentationErrorList doAction(Instrumentation value) {
                    Request request = new Request();
                    request.setActionType(ActionType.DELETE);
                    request.setEntity(value);

                    InstrumentationErrorList errorList = (InstrumentationErrorList) RequestResponseHelper.writeAndReadJSONObject(getInstrumentationURL(), request, InstrumentationErrorList.class);
                    return errorList;
                }
            });

            _instrumentation.setOnEdit(new PageAction<InstrumentationErrorList, Instrumentation>() {
                @Override
                public InstrumentationErrorList doAction(Instrumentation value) {
                    Request request = new Request();
                    request.setActionType(ActionType.UPDATE);
                    request.setEntity(value);

                    InstrumentationErrorList errorList = (InstrumentationErrorList) RequestResponseHelper.writeAndReadJSONObject(getInstrumentationURL(), request, InstrumentationErrorList.class);
                    return errorList;
                }
            });
        }
        return _instrumentation;
    }

    private static URL getInstrumentationURL(){
        try {
            return new URL(new URL(_configuration.getRootURI()), URIList.instrumentation);
        } catch (MalformedURLException e) {
        }
        return null;
    }
}
