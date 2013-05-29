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
package org.exoplatform.ide;

import com.codenvy.commons.env.EnvironmentContext;

import org.everrest.core.impl.provider.json.ArrayValue;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectValue;
import org.exoplatform.ide.conversationstate.IdeUser;
import org.exoplatform.ide.vfs.server.VirtualFileSystemFactory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: May 23, 2011 evgen $
 */
@Path("{ws-name}/configuration")
public class IDEConfigurationService {

    private static Log LOG = ExoLogger.getLogger(IDEConfigurationService.class);

    @PathParam("ws-name")
    private String wsName;
    /**
     * periodic request to prevent session expiration
     * TODO: need find better solutions
     */
    @GET
    @Path("ping")
    public void ping()
    {
    }

    @GET
    @Path("/init")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> inializationParameters(@Context UriInfo uriInfo, @Context HttpServletRequest request) {

        try {
            String vfsId = (String)EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_ID);
            Map<String, Object> result = new HashMap<String, Object>();
            ConversationState curentState = ConversationState.getCurrent();
            if (curentState != null) {
                Identity identity = curentState.getIdentity();
                IdeUser user = new IdeUser(identity.getUserId(), identity.getRoles(), request.getSession().getId());
                LOG.info("Getting user identity: " + identity.getUserId());
                result.put("user", user);
                final Map<String, Object> userSettings = getUserSettings();
                result.put("userSettings", userSettings);
            }
            result.put("vfsId", vfsId);
            result.put("vfsBaseUrl", uriInfo.getBaseUriBuilder().path(VirtualFileSystemFactory.class).path("v2").build(wsName).toString());
            return result;
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"developer"})
    public String getConfiguration() {
        try {
            String conf = readSettings();
            return conf;
        } catch (Exception e) {
            throw new WebApplicationException(e, 404);
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"developer"})
    public void setConfiguration(String body) throws IOException {
        writeSettings(body);
    }


    // ------Implementation---------

    /**
     * Get user setting as Map.
     *
     * @return map of user settings
     * @throws JsonException
     * @throws IOException
     */
    public Map<String, Object> getUserSettings() throws JsonException, IOException {
        String userConfiguration = readSettings();
        final Map<String, Object> userSettings = new HashMap<String, Object>();

        final JsonParser jsonParser = new JsonParser();
        jsonParser.parse(new InputStreamReader(new ByteArrayInputStream(userConfiguration.getBytes())));
        JsonValue jsonValue = jsonParser.getJsonObject();

        Iterator<String> iterator = jsonValue.getKeys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            JsonValue value = jsonValue.getElement(key);
            if (value.isObject()) {
                ObjectValue ob = (ObjectValue)value;
                Map<String, String> map = new HashMap<String, String>();
                Iterator<String> obIterator = ob.getKeys();
                while (obIterator.hasNext()) {
                    String k = obIterator.next();
                    map.put(k, ob.getElement(k).getStringValue());
                }
                userSettings.put(key, map);
            } else if (value.isArray()) {
                List<String> list = new ArrayList<String>();
                ArrayValue ar = (ArrayValue)value;
                Iterator<JsonValue> arrIterator = ar.getElements();

                while (arrIterator.hasNext()) {
                    list.add(arrIterator.next().getStringValue());

                }
                userSettings.put(key, list);
            } else if (value.isString()) {
                userSettings.put(key, value.getStringValue());
            } else if (value.isBoolean()) {
                userSettings.put(key, value.getBooleanValue());
            } else if (value.isNumeric()) {
                userSettings.put(key, value.getNumberValue());
            }

        }
        return userSettings;
    }

    /**
     * Write the user settings to a file.
     *
     * @param data
     * @throws IOException
     */
    protected void writeSettings(String data) throws IOException {
    }

    /**
     * Read the user settings from file and return it.
     *
     * @return user settings
     * @throws IOException
     */
    protected String readSettings() throws IOException {
        return "{}"; //TODO: small hack add for supporting previous version of IDE. In 1.2 changed structure of user settings
    }
}
