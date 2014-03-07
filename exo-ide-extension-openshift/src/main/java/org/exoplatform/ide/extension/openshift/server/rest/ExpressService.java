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
package org.exoplatform.ide.extension.openshift.server.rest;

import org.exoplatform.ide.extension.openshift.server.Express;
import org.exoplatform.ide.extension.openshift.server.ExpressException;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.extension.openshift.shared.RHUserInfo;
import org.exoplatform.ide.extension.ssh.server.SshKeyStoreException;
import org.exoplatform.ide.security.paas.CredentialStoreException;
import org.exoplatform.ide.vfs.server.LocalPathResolver;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.PropertyImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

import com.codenvy.commons.env.EnvironmentContext;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("{ws-name}/openshift/express")
public class ExpressService {
    private static final Log LOG = ExoLogger.getLogger(ExpressService.class);

    @Inject
    private Express express;

    @Inject
    private LocalPathResolver localPathResolver;

    @Inject
    private VirtualFileSystemRegistry vfsRegistry;

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    public void login(Map<String, String> credentials) throws ExpressException, CredentialStoreException {
        express.login(credentials.get("rhlogin"), credentials.get("password"));
    }

    @POST
    @Path("logout")
    public void logout() throws CredentialStoreException {
        express.logout();
    }

    @POST
    @Path("domain/create")
    public void createDomain(@QueryParam("namespace") String namespace,
                             @QueryParam("alter") boolean alter)
            throws ExpressException, SshKeyStoreException, CredentialStoreException {
        express.createDomain(namespace, alter);
    }

    @GET
    @Path("apps/type")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<String> applicationTypes() throws ExpressException, CredentialStoreException {
        return express.frameworks();
    }

    @GET
    @Path("sys/embeddable_cartridges")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<String> embeddableCartridges() throws ExpressException, CredentialStoreException {
        return express.embeddableCartridges();
    }

    @POST
    @Path("apps/create")
    @Produces(MediaType.APPLICATION_JSON)
    public AppInfo createApplication(@QueryParam("vfsid") String vfsId,
                                     @QueryParam("projectid") String projectId,
                                     @QueryParam("name") String appName,
                                     @QueryParam("type") String type,
                                     @QueryParam("scale") @DefaultValue("false") boolean scale,
                                     @QueryParam("instance") @DefaultValue("small") String instanceType)
            throws ExpressException, VirtualFileSystemException, CredentialStoreException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        AppInfo application =
                express.createApplication(appName,
                                          type,
                                          scale,
                                          instanceType,
                                          (projectId != null) ? new File(localPathResolver.resolve(vfs, projectId)) : null);

        Project project = (Project)vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);
        String value = project.getPropertyValue("isGitRepository");
        if (value == null || !value.equals("true")) {
            Property isGitRepositoryProperty = new PropertyImpl("isGitRepository", "true");
            List<Property> properties = new ArrayList<Property>(1);
            properties.add(isGitRepositoryProperty);
            vfs.updateItem(projectId, properties, null);
        }
        LOG.info("EVENT#application-created# WS#" + EnvironmentContext.getCurrent().getWorkspaceName()
                 + "# USER#" + ConversationState.getCurrent().getIdentity().getUserId() + "# PROJECT#" + project.getName() + "# TYPE#" + project.getProjectType()
                 + "# PAAS#OpenShift#");
        return application;
    }

    @GET
    @Path("apps/info")
    @Produces(MediaType.APPLICATION_JSON)
    public AppInfo applicationInfo(@QueryParam("name") String appName,
                                   @QueryParam("vfsid") String vfsId,
                                   @QueryParam("projectid") String projectId)
            throws ExpressException, VirtualFileSystemException, CredentialStoreException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        return express.applicationInfo(appName, (projectId != null) ? new File(localPathResolver.resolve(vfs, projectId)) : null);
    }

    @POST
    @Path("apps/destroy")
    public void destroyApplication(@QueryParam("name") String appName,
                                   @QueryParam("vfsid") String vfsId,
                                   @QueryParam("projectid") String projectId)
            throws ExpressException, VirtualFileSystemException, CredentialStoreException {
        express.destroyApplication(appName);
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        if (projectId != null) {
            // Update VFS properties. Need it to uniform client.
            Property p = new PropertyImpl("openshift-express-application", Collections.<String>emptyList());
            List<Property> properties = new ArrayList<Property>(1);
            properties.add(p);
            vfs.updateItem(projectId, properties, null);
        }
    }

    @POST
    @Path("apps/destroy/all")
    public void destroyAllApplicationIncludeNamespace(@QueryParam("namespace") boolean includeNamespace,
                                                      @QueryParam("projectid") String projectId,
                                                      @QueryParam("vfsid") String vfsId)
            throws ExpressException, CredentialStoreException, VirtualFileSystemException {
        express.destroyAllApplicationsIncludeNamespace(includeNamespace);
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        if (projectId != null) {
            Property p = new PropertyImpl("openshift-express-application", Collections.<String>emptyList());
            List<Property> properties = new ArrayList<Property>(1);
            properties.add(p);
            vfs.updateItem(projectId, properties, null);
        }
    }

    @POST
    @Path("apps/stop")
    public void stopApplication(@QueryParam("name") String appName) throws ExpressException, CredentialStoreException {
        express.stopApplication(appName);
    }

    @POST
    @Path("apps/start")
    public void startApplication(@QueryParam("name") String appName) throws ExpressException, CredentialStoreException {
        express.startApplication(appName);
    }

    @POST
    @Path("apps/restart")
    public void restartApplication(@QueryParam("name") String appName) throws ExpressException, CredentialStoreException {
        express.restartApplication(appName);
    }

    @GET
    @Path("apps/health")
    public String getApplicationHealth(@QueryParam("name") String appName) throws ExpressException, CredentialStoreException {
        return express.getApplicationHealth(appName);
    }

    @POST
    @Path("apps/embeddable_cartridges/add")
    @Produces(MediaType.APPLICATION_JSON)
    public AppInfo addEmbeddableCartridges(@QueryParam("name") String appName,
                                           @QueryParam("cartridge") List<String> embeddableCartridgeNames)
            throws ExpressException, CredentialStoreException, VirtualFileSystemException {
        return express.addEmbeddableCartridges(appName, embeddableCartridgeNames);
    }

    @POST
    @Path("apps/embedded_cartridges/start")
    public void startEmbeddableCartridge(@QueryParam("name") String appName,
                                         @QueryParam("cartridge") String embeddableCartridgeName)
            throws ExpressException, CredentialStoreException {
        express.startEmbeddedCartridge(appName, embeddableCartridgeName);
    }

    @POST
    @Path("apps/embedded_cartridges/stop")
    public void stopEmbeddableCartridge(@QueryParam("name") String appName,
                                        @QueryParam("cartridge") String embeddableCartridgeName)
            throws ExpressException, CredentialStoreException {
        express.stopEmbeddedCartridge(appName, embeddableCartridgeName);
    }

    @POST
    @Path("apps/embedded_cartridges/restart")
    public void restartEmbeddableCartridge(@QueryParam("name") String appName,
                                           @QueryParam("cartridge") String embeddableCartridgeName)
            throws ExpressException, CredentialStoreException {
        express.restartEmbeddedCartridge(appName, embeddableCartridgeName);
    }

    @POST
    @Path("apps/embedded_cartridges/reload")
    public void reloadEmbeddableCartridge(@QueryParam("name") String appName,
                                          @QueryParam("cartridge") String embeddableCartridgeName)
            throws ExpressException, CredentialStoreException {
        express.reloadEmbeddedCartridge(appName, embeddableCartridgeName);
    }

    @POST
    @Path("apps/embedded_cartridges/remove")
    @Produces(MediaType.APPLICATION_JSON)
    public AppInfo removeEmbeddableCartridge(@QueryParam("name") String appName,
                                             @QueryParam("cartridge") String embeddableCartridgeName)
            throws ExpressException, CredentialStoreException, VirtualFileSystemException {
        return express.removeEmbeddableCartridge(appName, embeddableCartridgeName);
    }

    @GET
    @Path("user/info")
    @Produces(MediaType.APPLICATION_JSON)
    public RHUserInfo userInfo(@QueryParam("appsinfo") boolean appsInfo) throws ExpressException, CredentialStoreException {
        return express.userInfo(appsInfo);
    }
}
