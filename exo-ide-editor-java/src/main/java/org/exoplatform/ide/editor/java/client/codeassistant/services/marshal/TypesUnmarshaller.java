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
package org.exoplatform.ide.editor.java.client.codeassistant.services.marshal;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.editor.java.client.model.ShortTypeInfo;
import org.exoplatform.ide.editor.java.client.model.Types;

import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Dec 5, 2011 12:06:38 PM evgen $
 */
public class TypesUnmarshaller implements Unmarshallable<List<ShortTypeInfo>> {

    private static final String NAME = "name";

    private static final String MODIFIERS = "modifiers";

    private static final String TYPE = "type";

    private List<ShortTypeInfo> types;

    /** @param types */
    public TypesUnmarshaller(List<ShortTypeInfo> types) {
        this.types = types;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            doParse(response.getText());
        } catch (Exception e) {
            throw new UnmarshallerException("Can't parse classes names.");
        }
    }

    private void doParse(String body) {
        JSONObject object = JSONParser.parseLenient(body).isObject();
        if (object.containsKey("types")) {
            JSONArray jArray = object.get("types").isArray();

            if (jArray == null) {
                return;
            }

            for (int i = 0; i < jArray.size(); i++) {
                JSONObject jObject = jArray.get(i).isObject();
                if (jObject.containsKey(NAME) && !jObject.get(NAME).isString().stringValue().contains("$")) {
                    ShortTypeInfo info = new ShortTypeInfo();
                    info.setName(jObject.get(NAME).isString().stringValue());
                    info.setType(Types.valueOf(jObject.get(TYPE).isString().stringValue()));

                    for (String key : jObject.keySet()) {
                        if (key.equals("name")) {
                            String fqn = jObject.get(key).isString().stringValue();
                            info.setQualifiedName(fqn);
                            info.setName(fqn.substring(fqn.lastIndexOf(".") + 1));
                        }
                        if (key.equals(MODIFIERS)) {
                            info.setModifiers(new Integer((int)jObject.get(key).isNumber().doubleValue()));
                        }

                    }
                    types.add(info);
                }
            }
        }
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#getPayload() */
    @Override
    public List<ShortTypeInfo> getPayload() {
        return types;
    }
}
