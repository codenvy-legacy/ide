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

import com.codenvy.api.factory.SimpleFactoryUrl;
import com.codenvy.api.factory.Variable;
import com.codenvy.ide.factory.shared.FactorySpec10;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import org.exoplatform.gwtframework.commons.rest.Marshallable;

/**
 * Marshaller for Simple factory url. To allow server to clone repository.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 23.10.13 vlad $
 */
public class SimpleFactoryUrlMarshaller implements Marshallable, FactorySpec10 {
    private SimpleFactoryUrl factory;

    /** Construct marshaller. */
    public SimpleFactoryUrlMarshaller(SimpleFactoryUrl factory) {
        this.factory = factory;
    }

    /** {@inheritDoc} */
    @Override
    public String marshal() {
        JSONObject factoryObj = new JSONObject();

        factoryObj.put(FACTORY_VERSION, new JSONString(valueOrEmpty(factory.getV())));
        factoryObj.put(VCS_TYPE, new JSONString(valueOrEmpty(factory.getVcs())));
        factoryObj.put(VCS_URL, new JSONString(valueOrEmpty(factory.getVcsurl())));
        factoryObj.put(COMMIT_ID, new JSONString(valueOrEmpty(factory.getCommitid())));
        factoryObj.put(ACTION, new JSONString(valueOrEmpty(factory.getAction())));
        factoryObj.put(OPEN_FILE, new JSONString(valueOrEmpty(factory.getOpenfile())));
        factoryObj.put(VCS_INFO, JSONBoolean.getInstance(factory.getVcsinfo()));
        factoryObj.put(ORG_ID, new JSONString(valueOrEmpty(factory.getOrgid())));
        factoryObj.put(AFFILIATE_ID, new JSONString(valueOrEmpty(factory.getAffiliateid())));
        factoryObj.put(VCS_BRANCH, new JSONString(valueOrEmpty(factory.getVcsbranch())));

        JSONObject projectAttributes = new JSONObject();
        projectAttributes.put(PROJECT_TYPE, new JSONString(valueOrEmpty(factory.getProjectattributes().get(PROJECT_TYPE))));
        projectAttributes.put(PROJECT_NAME, new JSONString(valueOrEmpty(factory.getProjectattributes().get(PROJECT_NAME))));
        factoryObj.put(PROFILE_ATTRIBUTES, projectAttributes);

        JSONArray variables = new JSONArray();

        for (int i = 0; i < factory.getVariables().size(); i++) {
            JSONArray glob = new JSONArray();
            JSONArray replacements = new JSONArray();
            JSONObject variable = new JSONObject();

            Variable var = factory.getVariables().get(i);

            for (int j = 0; j < var.getFiles().size(); j++) {
                glob.set(i, new JSONString(var.getFiles().get(j)));
            }

            for (int j = 0; j < var.getEntries().size(); j++) {
                JSONObject replacement = new JSONObject();
                replacement.put("find", new JSONString(var.getEntries().get(j).getFind()));
                replacement.put("replace", new JSONString(var.getEntries().get(j).getReplace()));
                replacement.put("replacemode", new JSONString(var.getEntries().get(j).getReplacemode()));

                replacements.set(j, replacement);
            }

            variable.put("files", glob);
            variable.put("entries", replacements);

            variables.set(i, variable);
        }

        factoryObj.put("variables", variables);

        return factoryObj.toString();
    }

    /**
     * Return empty string if value is null, otherwise return value.
     *
     * @param value
     *         value to check
     * @return empty string in case that value is null, otherwise value.
     */
    private String valueOrEmpty(String value) {
        if (value == null) {
            return "";
        }
        return value;
    }
}
