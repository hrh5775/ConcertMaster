package team_f.client.pages.instrumentationmanagement;

import team_f.jsonconnector.entities.Instrumentation;

/**
 * Created by dominik on 11.05.17.
 */
public class InstrumentationParameter {
    private Instrumentation _instrumentation;

    public Instrumentation getInstrumentation(){
        return _instrumentation;
    }

    public void setInstrumentation(Instrumentation instrumentation){
        _instrumentation = instrumentation;
    }
}
