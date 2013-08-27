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
package org.exoplatform.ide.git.client.marshaller;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.git.shared.Branch;

import java.util.List;

/**
 * Unmarshaller for list of branches.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 5, 2011 2:14:51 PM anya $
 */
public class BranchListUnmarshaller implements Unmarshallable<List<Branch>>, Constants {
    /** List of branches. */
    private List<Branch> branches;

    /**
     * @param branches branches
     */
    public BranchListUnmarshaller(List<Branch> branches) {
        this.branches = branches;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        if (response.getText() == null || response.getText().isEmpty()) {
            return;
        }

        JSONArray array = JSONParser.parseStrict(response.getText()).isArray();

        if (array == null || array.size() <= 0)
            return;

        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.get(i).isObject();
            if (object == null)
                continue;
            String name = "";
            String displayName = "";
            boolean active = false;
            boolean remote = false;
            if (object.containsKey(ACTIVE)) {
                active = (object.get(ACTIVE).isBoolean() != null) ? object.get(ACTIVE).isBoolean().booleanValue() : false;
            }
            if (object.containsKey(REMOTE)) {
                remote = (object.get(REMOTE).isBoolean() != null) ? object.get(REMOTE).isBoolean().booleanValue() : false;
            }
            if (object.containsKey(NAME)) {
                name = (object.get(NAME).isString() != null) ? object.get(NAME).isString().stringValue() : name;
            }
            if (object.containsKey(DISPLAY_NAME)) {
                displayName =
                              (object.get(DISPLAY_NAME).isString() != null) ? object.get(DISPLAY_NAME).isString().stringValue()
                                  : displayName;
            }

            branches.add(new Branch(name, active, displayName, remote));
        }
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload() */
    @Override
    public List<Branch> getPayload() {
        return branches;
    }

}
