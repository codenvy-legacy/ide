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
import org.exoplatform.ide.editor.api.codeassitant.*;

import java.util.List;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 4:51:03 PM evgen $
 */
public class FindClassesUnmarshaller implements Unmarshallable<List<Token>> {

    private static final String NAME = "name";

    private static final String MODIFIERS = "modifiers";

    private static final String TYPE = "type";

    private List<Token> tokens;

    /** @param tokens */
    public FindClassesUnmarshaller(List<Token> tokens) {
        this.tokens = tokens;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            parseClassesName(response.getText());
        } catch (Exception e) {
            throw new UnmarshallerException("Can't parse classes names.");
        }

    }

    private void parseClassesName(String body) {
        JSONObject object = JSONParser.parseLenient(body).isObject();
        if (object.containsKey("types")) {
            JSONArray jArray = object.get("types").isArray();
            for (int i = 0; i < jArray.size(); i++) {
                JSONObject jObject = jArray.get(i).isObject();
                if (jObject.containsKey(NAME) && jObject.containsKey(MODIFIERS) && jObject.containsKey(TYPE)
                    && !jObject.get(NAME).isString().stringValue().contains("$")) {
                    String fqn = jObject.get(NAME).isString().stringValue();
                    String name = fqn.substring(fqn.lastIndexOf(".") + 1);
                    String type = jObject.get(TYPE).isString().stringValue();
                    double modifiers = (int)jObject.get(MODIFIERS).isNumber().doubleValue();
                    Token token = new TokenImpl(name, TokenType.valueOf(type));
                    token.setProperty(TokenProperties.FQN, new StringProperty(fqn));
                    token.setProperty(TokenProperties.MODIFIERS, new NumericProperty(modifiers));
                    tokens.add(token);
                }
            }
        }
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload() */
    @Override
    public List<Token> getPayload() {
        return tokens;
    }
}
