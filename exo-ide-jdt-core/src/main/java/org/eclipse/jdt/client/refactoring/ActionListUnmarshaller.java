/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.eclipse.jdt.client.refactoring;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.websocket.rest.ResponseMessage;

import java.util.ArrayList;
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
