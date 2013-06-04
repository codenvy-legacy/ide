/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
            Log.info(getClass(), response.getText());
            JSONArray array = JSONParser.parseStrict(response.getText()).isArray();
            if (array == null || array.size() <= 0) {
                return;
            }

            for (int i = 0; i < array.size(); i++) {
                CLIResource cliResource = new CLIResource();
                JSONObject jsonRes = array.get(i).isObject();
                Log.info(getClass(), jsonRes.get(COMMAND));
                cliResource.setCommand(getStringSet(jsonRes.get(COMMAND).isArray()));
                Log.info(getClass(), jsonRes.get(PATH));
                cliResource.setPath(jsonRes.get(PATH).isString().stringValue());
                Log.info(getClass(), jsonRes.get(METHOD));
                cliResource.setMethod(jsonRes.get(METHOD).isString().stringValue());

                if (jsonRes.get(DESCRIPTION) != null && jsonRes.get(DESCRIPTION).isString() != null) {
                    Log.info(getClass(), jsonRes.get(DESCRIPTION));
                    cliResource.setDescription(jsonRes.get(DESCRIPTION).isString().stringValue());
                }

                if (jsonRes.containsKey(CONSUMES)) {
                    Log.info(getClass(), jsonRes.get(CONSUMES));
                    cliResource.setConsumes(getStringSet(jsonRes.get(CONSUMES).isArray()));
                }
                if (jsonRes.containsKey(PRODUCES)) {
                    Log.info(getClass(), jsonRes.get(PRODUCES));
                    cliResource.setProduces(getStringSet(jsonRes.get(PRODUCES).isArray()));
                }
                if (jsonRes.containsKey(PARAMS)) {
                    Log.info(getClass(), jsonRes.get(PARAMS));
                    cliResource.setParams(getParams(jsonRes.get(PARAMS).isArray()));
                }
                Log.info(getClass(), "BEFORE ADD");
                resources.add(cliResource);
                Log.info(getClass(), "FINISH");
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
            if (jsonParam.containsKey(NAME) && jsonParam.get(NAME).isNull() == null)
            {
                Log.info(getClass(), "NAME", jsonParam.get(NAME));
                parameter.setName(jsonParam.get(NAME).isString().stringValue());
            }
            else {
                return set;
            }
            Log.info(getClass(), "MANDATORY", jsonParam.get(MANDATORY));
            parameter.setMandatory(jsonParam.get(MANDATORY).isBoolean().booleanValue());
            if (jsonParam.containsKey(OPTIONS)) {
                Log.info(getClass(), "OPTIONS", jsonParam.get(OPTIONS));
                parameter.setOptions(getStringSet(jsonParam.get(OPTIONS).isArray()));
            }
            Log.info(getClass(), "HAS_ARG", jsonParam.get(HAS_ARG));
            if (jsonParam.containsKey(HAS_ARG))
                parameter.setHasArg(jsonParam.get(HAS_ARG).isBoolean().booleanValue());

            Log.info(getClass(), "TYPE", jsonParam.get(TYPE));
            if (jsonParam.containsKey(TYPE) && jsonParam.get(TYPE) != null)
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
