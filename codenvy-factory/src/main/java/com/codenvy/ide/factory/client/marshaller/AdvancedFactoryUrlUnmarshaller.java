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

package com.codenvy.ide.factory.client.marshaller;

import com.codenvy.api.factory.AdvancedFactoryUrl;
import com.codenvy.api.factory.Variable;
import com.codenvy.ide.factory.shared.AdvancedFactorySpec;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Unmarshaller for Advanced factory.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 22.10.13 vlad $
 */
public class AdvancedFactoryUrlUnmarshaller implements Unmarshallable<AdvancedFactoryUrl>, AdvancedFactorySpec {
    private AdvancedFactoryUrl advancedFactoryUrl;

    /** Construct unmarshaller. */
    public AdvancedFactoryUrlUnmarshaller(AdvancedFactoryUrl advancedFactoryUrl) {
        this.advancedFactoryUrl = advancedFactoryUrl;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        if (response.getText() == null || response.getText().isEmpty()) {
            return;
        }

        JSONObject factoryObject = JSONParser.parseStrict(response.getText()).isObject();

        //v 1.1
        advancedFactoryUrl.setId(getValue(ID, factoryObject));
        advancedFactoryUrl.setStyle(getValue(STYLE, factoryObject));
        advancedFactoryUrl.setDescription(getValue(DESCRIPTION, factoryObject));
        advancedFactoryUrl.setContactmail(getValue(CONTACT_MAIL, factoryObject));
        advancedFactoryUrl.setAuthor(getValue(AUTHOR, factoryObject));

        if (!(factoryObject.containsKey(VARIABLES) && factoryObject.get(VARIABLES).isArray() != null)) {
            advancedFactoryUrl.setVariables(Collections.<Variable>emptyList());
        } else {
            advancedFactoryUrl.setVariables(getVariables(factoryObject.get(VARIABLES).isArray().toString()));
        }

        //TODO set links advancedFactoryUrl.setLinks(...); //not necessary for server side

        //v 1.0
        advancedFactoryUrl.setV(getValue(FACTORY_VERSION, factoryObject));
        advancedFactoryUrl.setVcs(getValue(VCS_TYPE, factoryObject));
        advancedFactoryUrl.setVcsurl(getValue(VCS_URL, factoryObject));
        advancedFactoryUrl.setCommitid(getValue(COMMIT_ID, factoryObject));
        advancedFactoryUrl.setAction(getValue(ACTION, factoryObject));
        advancedFactoryUrl.setOpenfile(getValue(OPEN_FILE, factoryObject));
        advancedFactoryUrl.setOrgid(getValue(ORG_ID, factoryObject));
        advancedFactoryUrl.setAffiliateid(getValue(AFFILIATE_ID, factoryObject));
        advancedFactoryUrl.setVcsbranch(getValue(VCS_BRANCH, factoryObject));

        if (factoryObject.containsKey(VCS_INFO) && factoryObject.get(VCS_INFO).isBoolean() != null) {
            advancedFactoryUrl.setVcsinfo(factoryObject.get(VCS_INFO).isBoolean().booleanValue());
        }

        JSONValue jsonAttributes = factoryObject.get(PROFILE_ATTRIBUTES);
        if (jsonAttributes.isObject() != null) {
            Map<String, String> profileAttributes = new HashMap<String, String>();
            profileAttributes.put(PROJECT_NAME, getValue(PROJECT_NAME, jsonAttributes.isObject()));
            profileAttributes.put(PROJECT_TYPE, getValue(PROJECT_TYPE, jsonAttributes.isObject()));

            advancedFactoryUrl.setProjectattributes(profileAttributes);
        }
    }

    /**
     * Retrieve value from json object if it is exists, otherwise return null.
     *
     * @param param
     *         parameter name to retrieve
     * @param object
     *         json object
     * @return string with parameter value or null
     */
    private String getValue(String param, JSONObject object) {
        if (object.containsKey(param) && object.get(param).isString() != null) {
            return object.get(param).isString().stringValue();
        }

        return null;
    }

    public static List<Variable> getVariables(String json) {
        List<Variable> variables = new ArrayList<Variable>();

        if (json == null) {
            return variables;
        }

        JSONArray vars = JSONParser.parseStrict(json).isArray();
        if (vars != null) {
            for (int i = 0; i < vars.size(); i++) {
                JSONObject variableObject = vars.get(i).isObject();

                List<String> files = new ArrayList<String>();
                List<Variable.Replacement> variableEntries = new ArrayList<Variable.Replacement>();

                JSONArray jsonFiles = variableObject.get("files").isArray();
                for (int j = 0; j < jsonFiles.size(); j++) {
                    files.add(jsonFiles.get(j).isString().stringValue());
                }

                JSONArray jsonEntries = variableObject.get("entries").isArray();
                for (int j = 0; j < jsonEntries.size(); j++) {
                    JSONObject entryObject = jsonEntries.get(j).isObject();
                    String find = entryObject.get("find").isString().stringValue();
                    String replace = entryObject.get("replace").isString().stringValue();

                    if (entryObject.get("replacemode") != null && entryObject.get("replacemode").isString() != null) {
                        String replaceMode = entryObject.get("replacemode").isString().stringValue();
                        variableEntries.add(new Variable.Replacement(find, replace, replaceMode));
                    } else {
                        variableEntries.add(new Variable.Replacement(find, replace));
                    }
                }

                variables.add(new Variable(files, variableEntries));
            }
        }

        return variables;
    }

    /** {@inheritDoc} */
    @Override
    public AdvancedFactoryUrl getPayload() {
        return advancedFactoryUrl;
    }
}
