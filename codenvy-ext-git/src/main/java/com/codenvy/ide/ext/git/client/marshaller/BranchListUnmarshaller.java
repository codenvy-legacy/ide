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
package com.codenvy.ide.ext.git.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.git.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Unmarshaller for list of branches.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 5, 2011 2:14:51 PM anya $
 */
public class BranchListUnmarshaller implements Unmarshallable<JsonArray<Branch>> {
    /** List of branches. */
    private JsonArray<Branch> branches;

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();
        if (text == null || text.isEmpty()) {
            return;
        }

        JSONArray array = JSONParser.parseStrict(text).isArray();

        if (array == null || array.size() <= 0)
            return;

        branches = JsonCollections.createArray();

        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.get(i).isObject();
            if (object == null)
                continue;
            String value = object.toString();
            DtoClientImpls.BranchImpl branch = DtoClientImpls.BranchImpl.deserialize(value);
            branches.add(branch);
        }
    }

    /** {@inheritDoc} */
    @Override
    public JsonArray<Branch> getPayload() {
        return branches;
    }
}