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
package org.exoplatform.ide.editor.codeassistant;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

import org.exoplatform.ide.editor.api.codeassitant.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Token parser for JSON tokens.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: JSONTokenParser.java Jan 24, 2011 10:37:59 AM vereshchaka $
 */
public class JSONTokenParser {

    private interface TokenFields {
        public static final String NAME = "name";

        public static final String TYPE = "type";

        public static final String SUB_TOKEN_LIST = "subTokenList";

        public static final String SHORT_DECRIPTION = "shortDescription";

        public static final String CODE = "code";

        public static final String FULL_DESCRIPTION = "fullDescription";

        public static final String FQN = "fqn";

        public static final String VARTYPE = "varType";
    }

    public List<Token> getTokens(JSONArray json) {
        List<Token> tokens = new ArrayList<Token>();
        for (int i = 0; i < json.size(); i++) {
            JSONObject jObject = json.get(i).isObject();
            String name =
                    jObject.get(TokenFields.NAME).isString() == null ? null : jObject.get(TokenFields.NAME).isString()
                                                                                     .stringValue();
            Token t = new TokenImpl(name, TokenType.valueOf(jObject.get(TokenFields.TYPE).isString().stringValue()));

            if (jObject.get(TokenFields.SHORT_DECRIPTION) != null) {
                t.setProperty(TokenProperties.SHORT_HINT, new StringProperty(jObject.get(TokenFields.SHORT_DECRIPTION)
                                                                                    .isString().stringValue()));
            }

            if (jObject.get(TokenFields.CODE) != null) {
                t.setProperty(TokenProperties.CODE, new StringProperty(jObject.get(TokenFields.CODE).isString()
                                                                              .stringValue()));
            }
            if (jObject.get(TokenFields.FULL_DESCRIPTION) != null) {
                t.setProperty(TokenProperties.FULL_TEXT, new StringProperty(jObject.get(TokenFields.FULL_DESCRIPTION)
                                                                                   .isString().stringValue()));
            }
            if (jObject.get(TokenFields.FQN) != null) {
                t.setProperty(TokenProperties.FQN,
                              new StringProperty(jObject.get(TokenFields.FQN).isString().stringValue()));
            }

            if (jObject.get(TokenFields.SUB_TOKEN_LIST) != null) {
                t.setProperty(TokenProperties.SUB_TOKEN_LIST,
                              new ArrayProperty(getTokens(jObject.get(TokenFields.SUB_TOKEN_LIST).isArray())));
            }
            if (jObject.get(TokenFields.VARTYPE) != null) {
                t.setProperty(TokenProperties.ELEMENT_TYPE, new StringProperty(jObject.get(TokenFields.VARTYPE).isString()
                                                                                      .stringValue()));
            }

            tokens.add(t);
        }

        return tokens;
    }

}
