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
package com.codenvy.ide.resources.marshal;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;


/**
 *
 */
public class ChildNamesUnmarshaller implements Unmarshallable<JsonArray<String>> {
    private JsonArray<String> items;

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            JSONValue jsonValue = JSONParser.parseLenient(response.getText());
            parseItems(jsonValue.isObject().get("items").isArray());
        } catch (Exception exc) {
            String message = "Can't parse folder content at <b>" + "id" + "</b>! ";
            throw new UnmarshallerException(message, exc);
        }
    }

    /** {@inheritDoc} */
    @Override
    public JsonArray<String> getPayload() {
        return this.items;
    }

    /**
     * Parse JSON Array as the list of names
     *
     * @param itemsArray
     *         JSON array
     * @return list of children items
     */
    private void parseItems(JSONArray itemsArray) {
        items = JsonCollections.createArray();

        for (int i = 0; i < itemsArray.size(); i++) {
            // get Json Object
            JSONObject object = itemsArray.get(i).isObject();
            // get name
            String name = object.get("name").isString().stringValue();
            items.add(name);
        }
    }

}
