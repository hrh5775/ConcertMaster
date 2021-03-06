package team_f.server.controller;

import javafx.util.Pair;
import org.json.JSONArray;
import team_f.application.InstrumentationApplication;
import team_f.domain.interfaces.DomainEntity;
import team_f.jsonconnector.common.URIList;
import team_f.jsonconnector.entities.SpecialInstrumentation;
import team_f.jsonconnector.entities.list.ErrorList;
import team_f.jsonconnector.entities.list.InstrumentationList;
import team_f.jsonconnector.entities.special.request.InstrumentationRequest;
import team_f.jsonconnector.helper.ReadHelper;
import team_f.jsonconnector.helper.WriteHelper;
import team_f.server.helper.converter.InstrumentationConverter;
import team_f.server.helper.response.CommonResponse;
import team_f.server.helper.response.JsonResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@WebServlet(urlPatterns = {URIList.instrumentation})
public class Instrumentation extends HttpServlet {
    @Override
    public void init() throws ServletException {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String contentType = req.getContentType();
        resp.setCharacterEncoding("UTF-8");

        if (contentType != null && contentType.startsWith(MediaType.APPLICATION_JSON)) {
            CommonResponse.writeJSONObject(resp, new JSONArray());
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String contentType = req.getContentType();
        resp.setCharacterEncoding("UTF-8");

        if(contentType != null && contentType.startsWith(MediaType.APPLICATION_JSON)) {
            InstrumentationRequest request = (InstrumentationRequest) ReadHelper.readJSONObject(req.getReader(), InstrumentationRequest.class);

            if(request != null) {
                InstrumentationApplication facade = InstrumentationApplication.getInstance();
                team_f.jsonconnector.entities.Instrumentation instrumentation;
                javafx.util.Pair<DomainEntity, List<Pair<String, String>>> tmpErrorList;
                ErrorList errorList = null;

                switch (request.getActionType()) {
                    case GET_BY_PARAMETER:
                        // @TODO: add get by parameter functionality
                        break;
                    case GET_ALL:
                        List<team_f.domain.entities.Instrumentation> instrumentationEntityList = facade.getList();
                        List<team_f.jsonconnector.entities.Instrumentation> instrumentationList = new LinkedList<>();
                        InstrumentationList instrumentations = new InstrumentationList();

                        for(team_f.domain.entities.Instrumentation item : instrumentationEntityList) {
                            instrumentation = InstrumentationConverter.convertToJSON(item);
                            instrumentationList.add(instrumentation);
                        }

                        instrumentations.setInstrumentationList(instrumentationList);

                        resp.setContentType(MediaType.APPLICATION_JSON);
                        WriteHelper.writeJSONObject(resp.getWriter(), instrumentations);

                        break;
                    case CREATE:
                    case UPDATE:
                        instrumentation = request.getEntity();

                        if(instrumentation != null) {
                            List<team_f.application.entities.SpecialInstrumentation> specialInstrumentationList = new LinkedList<>();
                            team_f.application.entities.SpecialInstrumentation specialInstrumentation;

                            if(instrumentation.getSpecialInstrumentation() != null) {
                                for(SpecialInstrumentation item : instrumentation.getSpecialInstrumentation()) {
                                    specialInstrumentation = new team_f.application.entities.SpecialInstrumentation();
                                    specialInstrumentation.setID(item.getSpecialInstrumentationID());
                                    specialInstrumentation.setSpecialInstrumentation(item.getSpecialInstrumentation());
                                    specialInstrumentation.setSectionType(item.getSectionType());
                                    specialInstrumentation.setSpecialInstrumentationCount(item.getSpecialInstrumentCount());

                                    specialInstrumentationList.add(specialInstrumentation);
                                }
                            }

                            tmpErrorList = facade.addInstrumentation(0, instrumentation.getViolin1(), instrumentation.getViolin2(), instrumentation.getViola(), instrumentation.getViolincello(),
                                    instrumentation.getDoublebass(), instrumentation.getFlute(), instrumentation.getOboe(), instrumentation.getClarinet(), instrumentation.getBassoon(), instrumentation.getHorn(), instrumentation.getTrumpet(),
                                    instrumentation.getTrombone(), instrumentation.getTube(),instrumentation.getKettledrum(), instrumentation.getPercussion(), instrumentation.getHarp(),
                                    specialInstrumentationList);

                            errorList = JsonResponse.prepareErrorMessage(InstrumentationConverter.convertToJSON((team_f.domain.entities.Instrumentation) tmpErrorList.getKey()), tmpErrorList.getValue());
                        }

                        resp.setContentType(MediaType.APPLICATION_JSON);
                        WriteHelper.writeJSONObject(resp.getWriter(), errorList);

                        break;
                    case DELETE:
                        instrumentation = request.getEntity();

                        if(instrumentation != null) {
                            // @TODO: add delete functionality
                            /*tmpErrorList = facade.delete(instrumentation.getMusicalWorkID());
                            errorList = JsonResponse.prepareErrorMessage(InstrumentationConverter.convertToJSON((team_f.domain.entities.Instrumentation) tmpErrorList.getKey()), tmpErrorList.getValue());*/
                        }

                        resp.setContentType(MediaType.APPLICATION_JSON);
                        WriteHelper.writeJSONObject(resp.getWriter(), errorList);

                        break;
                    default:
                        break;
                }
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

}
