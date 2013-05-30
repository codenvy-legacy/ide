/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.googleappengine.server;

import com.codenvy.commons.security.oauth.OAuthTokenProvider;
import com.codenvy.commons.security.shared.Token;
import com.google.appengine.tools.admin.CronEntry;
import com.google.apphosting.utils.config.BackendsXml;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.exoplatform.ide.extension.googleappengine.shared.ApplicationInfo;
import org.exoplatform.ide.extension.googleappengine.shared.GaeUser;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.security.ConversationState;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("{ws-name}/appengine")
public class AppEngineService {
    private static final Pattern PATTERN_XML = Pattern.compile(".*<application>.*</application>.*");

    private static final Pattern PATTERN_YAML = Pattern.compile("application:.*");

    @Inject
    private AppEngineClient client;

    @Inject
    private OAuthTokenProvider oauthTokenProvider;

    @Inject
    private VirtualFileSystemRegistry vfsRegistry;

    @GET
    @Path("user")
    @Produces(MediaType.APPLICATION_JSON)
    public GaeUser getUser(@Context SecurityContext security) throws Exception {
        final String userId = getUserId(/*security*/);
        Token token = null;
        try {
            token = oauthTokenProvider.getToken("google", userId);
        } catch (IOException e) {
            // Error when try to refresh access token. User may try re-authenticate.
        }
        return new GaeUserImpl(userId, token);
    }

    @GET
    @Path("backend/configure")
    public void configureBackend(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
                                 @QueryParam("backend_name") String backendName,
                                 @Context SecurityContext security) throws Exception {
        client.configureBackend(vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
                                projectId, backendName, getUserId(/*security*/));
    }

    @GET
    @Path("cron/info")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CronEntry> cronInfo(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
                                    @Context SecurityContext security) throws Exception {
        return client.cronInfo(vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
                               projectId, getUserId(/*security*/));
    }

    @GET
    @Path("backend/delete")
    public void deleteBackend(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
                              @QueryParam("backend_name") String backendName,
                              @Context SecurityContext security) throws Exception {
        client.deleteBackend(vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId,
                             backendName, getUserId(/*security*/));
    }

    @GET
    @Path("resource_limits")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Long> getResourceLimits(@QueryParam("vfsid") String vfsId,
                                               @QueryParam("projectid") String projectId,
                                               @Context SecurityContext security) throws Exception {
        return client.getResourceLimits(vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
                                        projectId, getUserId(/*security*/));
    }

    @GET
    @Path("backends/list")
    @Produces(MediaType.APPLICATION_JSON)
    public List<BackendsXml.Entry> listBackends(@QueryParam("vfsid") String vfsId,
                                                @QueryParam("projectid") String projectId,
                                                @Context SecurityContext security) throws Exception {
        return client.listBackends(vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
                                   projectId, getUserId(/*security*/));
    }

    @GET
    @Path("logs")
    @Produces(MediaType.TEXT_PLAIN)
    public Reader requestLogs(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
                              @QueryParam("num_days") int numDays, @QueryParam("log_severity") String logSeverity,
                              @Context SecurityContext security) throws Exception {
        return client.requestLogs(vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
                                  projectId, numDays, logSeverity, getUserId(/*security*/));
    }

    @GET
    @Path("rollback")
    public void rollback(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
                         @Context SecurityContext security) throws Exception {
        client.rollback(vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId,
                        getUserId(/*security*/));
    }

    @GET
    @Path("backend/rollback")
    public void rollbackBackend(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
                                @QueryParam("backend_name") String backendName,
                                @Context SecurityContext security) throws Exception {
        client.rollbackBackend(vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
                               projectId, backendName, getUserId(/*security*/));
    }

    @GET
    @Path("backends/rollback")
    public void rollbackAllBackends(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
                                    @Context SecurityContext security) throws Exception {
        client.rollbackAllBackends(vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
                                   projectId, getUserId(/*security*/));
    }

    @GET
    @Path("backend/set_state")
    public void setBackendState(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
                                @QueryParam("backend_name") String backendName,
                                @QueryParam("backend_state") String backendState,
                                @Context SecurityContext security) throws Exception {
        client.setBackendState(vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
                               projectId, backendName, backendState, getUserId(/*security*/));
    }

    @GET
    @Path("update")
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationInfo update(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
                                  @QueryParam("bin") URL bin, @Context SecurityContext security) throws Exception {
        return client.update(vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId,
                             bin, getUserId(/*security*/));
    }

    @GET
    @Path("backends/update_all")
    public void updateAllBackends(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
                                  @Context SecurityContext security) throws Exception {
        client.updateAllBackends(vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
                                 projectId, getUserId(/*security*/));
    }

    @GET
    @Path("backend/update")
    public void updateBackend(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
                              @QueryParam("backend_name") String backendName,
                              @Context SecurityContext security) throws Exception {
        client.updateBackend(vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId,
                             backendName, getUserId(/*security*/));
    }

    @GET
    @Path("backends/update")
    public void updateBackends(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
                               @QueryParam("backends_name") List<String> backendNames,
                               @Context SecurityContext security) throws Exception {
        client.updateBackends(vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId,
                              backendNames, getUserId(/*security*/));
    }

    @GET
    @Path("cron/update")
    public void updateCron(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
                           @Context SecurityContext security) throws Exception {
        client.updateCron(vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId,
                          getUserId(/*security*/));
    }

    @GET
    @Path("dos/update")
    public void updateDos(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
                          @Context SecurityContext security) throws Exception {
        client.updateDos(vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId,
                         getUserId(/*security*/));
    }

    @GET
    @Path("indexes/update")
    public void updateIndexes(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
                              @Context SecurityContext security) throws Exception {
        client.updateIndexes(vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId,
                             getUserId(/*security*/));
    }

    //   @GET
    //   @Path("pagespeed/update")
    //   public void updatePagespeed(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
    //                               @Context SecurityContext security) throws Exception
    //   {
    //      client.updatePagespeed(vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
    // projectId,
    //         getUserId(/*security*/));
    //   }

    @GET
    @Path("queues/update")
    public void updateQueues(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
                             @Context SecurityContext security) throws Exception {
        client.updateQueues(vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId,
                            getUserId(/*security*/));
    }

    @GET
    @Path("vacuum_indexes")
    public void vacuumIndexes(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
                              @Context SecurityContext security) throws Exception {
        client.vacuumIndexes(vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId,
                             getUserId(/*security*/));
    }

    private String getUserId() {
        return ConversationState.getCurrent().getIdentity().getUserId();
    }

    @GET
    @Path("change-appid/{vfsid}/{projectid}")
    public Response changeApplicationId(@PathParam("vfsid") String vfsId, //
                                        @PathParam("projectid") String projectId, //
                                        @QueryParam("app_id") String appId,//
                                        @Context UriInfo uriInfo) throws VirtualFileSystemException, IOException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        Item item = vfs.getItem(projectId, false, PropertyFilter.NONE_FILTER);
        String path = item.getPath().endsWith("/") ? item.getPath().substring(0, item.getPath().length() - 1) : item
                .getPath();
        appId = StringUtils.removeStart(appId, "s~");

        try {
            changeAppEngXml(vfs, path, appId);
        } catch (ItemNotFoundException e) {
            try {
                changeAppEngYaml(vfs, path, appId);
            } catch (Exception e1) {
                return Response.serverError().entity("Unable to modify App Engine application settings.").type(MediaType
                                                                                                                       .TEXT_PLAIN).build();
            }
        }
        return Response.ok("<html><body onLoad=\"javascript:window.close();\" style=\"font-family: Verdana, Bitstream Vera Sans, " +
                           "sans-serif; font-size: 13px; font-weight: bold;\">" + "<div align=\"center\" style=\"margin: 100 auto; " +
                           "border: dashed 1px #CACACA; width: 450px;\">" + "<p>Your application has been created.<br>Close this tab " +
                           "and use the Deploy button in Codenvy.</p>" + "</div></body></html>")
                       .type(MediaType.TEXT_HTML).build();
    }

    /**
     * Change appengine-web.xml file setting application's id.
     *
     * @param vfs
     *         virtual file system
     * @param path
     *         path to project's root
     * @param appId
     *         application's id
     */
    private void changeAppEngXml(VirtualFileSystem vfs, String path,
                                 String appId) throws VirtualFileSystemException, IOException {
        String path2appengineXml = path + "/src/main/webapp/WEB-INF/appengine-web.xml";
        File fileAppEngXml = (File)vfs.getItemByPath(path2appengineXml, null, false, PropertyFilter.NONE_FILTER);
        String content = IOUtils.toString(vfs.getContent(fileAppEngXml.getId()).getStream());
        String newContent = PATTERN_XML.matcher(content).replaceFirst("<application>" + appId + "</application>");
        vfs.updateContent(fileAppEngXml.getId(), MediaType.valueOf(fileAppEngXml.getMimeType()),
                          new ByteArrayInputStream(newContent.getBytes()), null);
    }

    /**
     * Change app.yaml file setting application's id.
     *
     * @param vfs
     *         virtual file system
     * @param path
     *         path to project's root
     * @param appId
     *         application's id
     */
    private void changeAppEngYaml(VirtualFileSystem vfs, String path,
                                  String appId) throws VirtualFileSystemException, IOException {
        String path2appengineYaml = path + "/app.yaml";
        File fileAppEngYaml = (File)vfs.getItemByPath(path2appengineYaml, null, false, PropertyFilter.NONE_FILTER);
        String content = IOUtils.toString(vfs.getContent(fileAppEngYaml.getId()).getStream());
        String newContent = PATTERN_YAML.matcher(content).replaceFirst("application: " + appId);
        vfs.updateContent(fileAppEngYaml.getId(), MediaType.valueOf(fileAppEngYaml.getMimeType()),
                          new ByteArrayInputStream(newContent.getBytes()), null);
    }

}
