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
package org.exoplatform.ide.shell.client;

import com.codenvy.ide.client.util.logging.Log;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.shell.shared.CLIResource;
import org.exoplatform.ide.shell.shared.CLIResourceParameter;
import org.exoplatform.ide.shell.shared.CLIResourceParameter.Type;

import java.util.HashSet;
import java.util.Set;

/**
 * Unmarshaller for set of {@link CLIResource}.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Aug 4, 2011 4:34:11 PM anya $
 */
public class CLIResourceUnmarshaller implements Unmarshallable<Set<CLIResource>>, Constants {
    /** CLI resources. */
    private Set<CLIResource> resources;

    /**
     * @param resources resources
     */
    public CLIResourceUnmarshaller(Set<CLIResource> resources) {
        this.resources = resources;
    }

    /**
     * @throws UnmarshallerException
     * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
     */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            JSONArray array = JSONParser.parseStrict(response.getText()).isArray();
            if (array == null || array.size() <= 0) {
                return;
            }

            for (int i = 0; i < array.size(); i++) {
                CLIResource cliResource = new CLIResource();
                JSONObject jsonRes = array.get(i).isObject();
                cliResource.setCommand(getStringSet(jsonRes.get(COMMAND).isArray()));
                cliResource.setPath(jsonRes.get(PATH).isString().stringValue());
                cliResource.setMethod(jsonRes.get(METHOD).isString().stringValue());
                if (jsonRes.get(DESCRIPTION) != null && jsonRes.get(DESCRIPTION).isString() != null)
                    cliResource.setDescription(jsonRes.get(DESCRIPTION).isString().stringValue());
                if (jsonRes.containsKey(CONSUMES) && jsonRes.get(CONSUMES).isArray() != null)
                    cliResource.setConsumes(getStringSet(jsonRes.get(CONSUMES).isArray()));
                if (jsonRes.containsKey(PRODUCES) && jsonRes.get(PRODUCES).isArray() != null)
                    cliResource.setProduces(getStringSet(jsonRes.get(PRODUCES).isArray()));
                if (jsonRes.containsKey(PARAMS) && jsonRes.get(PARAMS).isArray() != null)
                    cliResource.setParams(getParams(jsonRes.get(PARAMS).isArray()));
                resources.add(cliResource);
            }
        } catch (Exception e) {
            Log.error(getClass(), e);
            throw new UnmarshallerException(CloudShell.messages.commandsUnmarshallerError());
        }
    }

    /**
     * Get the set of {@link String} from {@link JSONArray}
     * 
     * @param array JSON array
     * @return {@link Set}
     */
    private Set<String> getStringSet(JSONArray array) {
        Set<String> set = new HashSet<String>();
        if (array == null || array.size() <= 0) {
            return set;
        }

        for (int i = 0; i < array.size(); i++) {
            set.add(array.get(i).isString().stringValue());
        }
        return set;
    }

    /**
     * Get the {@link Set} of {@link CLIResourceParameter} from JSON representation.
     * 
     * @param array JSON array of resource's parameters
     * @return {@link Set}
     */
    private Set<CLIResourceParameter> getParams(JSONArray array) {
        Set<CLIResourceParameter> set = new HashSet<CLIResourceParameter>();
        if (array == null || array.size() <= 0) {
            return set;
        }

        for (int i = 0; i < array.size(); i++) {
            CLIResourceParameter parameter = new CLIResourceParameter();
            JSONObject jsonParam = array.get(i).isObject();
            if (jsonParam.containsKey(NAME) && jsonParam.get(NAME).isString() != null)
                parameter.setName(jsonParam.get(NAME).isString().stringValue());
            if (jsonParam.containsKey(MANDATORY) && jsonParam.get(MANDATORY).isBoolean() != null)
               parameter.setMandatory(jsonParam.get(MANDATORY).isBoolean().booleanValue());
            if (jsonParam.containsKey(OPTIONS) && jsonParam.get(OPTIONS).isArray() != null)
                parameter.setOptions(getStringSet(jsonParam.get(OPTIONS).isArray()));
            
            if (jsonParam.containsKey(HAS_ARG) && jsonParam.get(HAS_ARG).isBoolean() != null)
                parameter.setHasArg(jsonParam.get(HAS_ARG).isBoolean().booleanValue());

            if (jsonParam.containsKey(TYPE) && jsonParam.get(TYPE).isString() != null)
                parameter.setType(Type.valueOf(jsonParam.get(TYPE).isString().stringValue()));
            set.add(parameter);
        }
        return set;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload() */
    @Override
    public Set<CLIResource> getPayload() {
        return resources;
    }

}
