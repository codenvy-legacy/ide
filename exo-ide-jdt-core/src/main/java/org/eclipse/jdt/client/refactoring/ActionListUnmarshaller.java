/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.eclipse.jdt.client.refactoring;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.websocket.rest.ResponseMessage;

import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class ActionListUnmarshaller implements Unmarshallable<List<Action>>,
        org.exoplatform.ide.client.framework.websocket.rest.Unmarshallable<List<Action>> {

    private static final String ACTION = "action";
    
    private static final String RESOURCE = "resource";
    
    private static final String DESTINATION = "destination";

    private List<Action> actions = new LinkedList<Action>();

    @Override
    public void unmarshal(ResponseMessage response) throws UnmarshallerException {
        unmarshal(response.getBody());
    }

    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        unmarshal(response.getText());
    }
    
    private void unmarshal(String text) throws UnmarshallerException {
        try {
            JSONArray array = JSONParser.parseLenient(text).isArray();
            for (int i = 0; i < array.size(); i++) {
                JSONObject actionItem = array.get(i).isObject();

                String action = null;
                String resource = null;
                String destination = null;
                
                if (actionItem.get(ACTION).isString() != null) {
                    action = actionItem.get(ACTION).isString().stringValue();
                }
                
                if (actionItem.get(RESOURCE).isString() != null) {
                    resource = actionItem.get(RESOURCE).isString().stringValue();
                }

                if (actionItem.get(DESTINATION).isString() != null) {
                    destination = actionItem.get(DESTINATION).isString().stringValue();
                }

                actions.add(new Action(action, resource, destination));
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            throw new UnmarshallerException("Can't parse JSON response.");
        }        
    }

    @Override
    public List<Action> getPayload() {
        return actions;
    }

}
