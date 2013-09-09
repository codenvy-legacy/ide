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
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.git.shared.Branch;

/**
 * Unmarshaller for {@link Branch} in JSON format.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 11, 2011 12:29:46 PM anya $
 */
public class BranchUnmarshaller implements Unmarshallable<Branch>, Constants {
    /** Branch. */
    private Branch branch;

    /**
     * @param branch branch
     */
    public BranchUnmarshaller(Branch branch) {
        this.branch = branch;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        if (response.getText() == null || response.getText().isEmpty()) {
            return;
        }

        JSONObject object = JSONParser.parseStrict(response.getText()).isObject();
        if (object == null)
            return;
        if (object.containsKey(ACTIVE)) {
            boolean active =
                             (object.get(ACTIVE).isBoolean() != null) ? object.get(ACTIVE).isBoolean().booleanValue() : false;
            branch.setActive(active);
        }
        if (object.containsKey(NAME)) {
            String name = (object.get(NAME).isString() != null) ? object.get(NAME).isString().stringValue() : "";
            branch.setName(name);
        }
        if (object.containsKey(DISPLAY_NAME)) {
            String displayName =
                                 (object.get(DISPLAY_NAME).isString() != null) ? object.get(DISPLAY_NAME).isString().stringValue() : "";
            branch.setDisplayName(displayName);
        }
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload() */
    @Override
    public Branch getPayload() {
        return branch;
    }
}
