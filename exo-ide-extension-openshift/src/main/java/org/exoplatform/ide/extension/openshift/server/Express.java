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
package org.exoplatform.ide.extension.openshift.server;

import com.codenvy.commons.lang.cache.Cache;
import com.codenvy.commons.lang.cache.SLRUCache;
import com.openshift.client.ApplicationScale;
import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftConnectionFactory;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.*;
import com.openshift.internal.client.*;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.response.ResourceDTOFactory;
import com.openshift.internal.client.utils.IOpenShiftJsonConstants;

import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.extension.openshift.shared.OpenShiftEmbeddableCartridge;
import org.exoplatform.ide.extension.openshift.shared.RHUserInfo;
import org.exoplatform.ide.extension.ssh.server.SshKey;
import org.exoplatform.ide.extension.ssh.server.SshKeyStore;
import org.exoplatform.ide.extension.ssh.server.SshKeyStoreException;
import org.exoplatform.ide.git.server.GitConnection;
import org.exoplatform.ide.git.server.GitConnectionFactory;
import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.shared.InitRequest;
import org.exoplatform.ide.git.shared.Remote;
import org.exoplatform.ide.git.shared.RemoteAddRequest;
import org.exoplatform.ide.git.shared.RemoteListRequest;
import org.exoplatform.ide.security.paas.Credential;
import org.exoplatform.ide.security.paas.CredentialStore;
import org.exoplatform.ide.security.paas.CredentialStoreException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.jboss.dmr.ModelNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a> */
public class Express {
    private static final Pattern GIT_URL_PATTERN =
            Pattern.compile("ssh://(\\w+)@(\\w+)-(\\w+)\\.rhcloud\\.com/~/git/(\\w+)\\.git/");
    private static final Log     LOG             = ExoLogger.getLogger(Express.class);
    private static final String  OPENSHIFT_URL   = "https://openshift.redhat.com";

    private static final String LINK_LIST_CARTRIDGES   = "LIST_CARTRIDGES";
    private static final String LINK_START_CARTRIDGE   = "START";
    private static final String LINK_STOP_CARTRIDGE    = "STOP";
    private static final String LINK_RESTART_CARTRIDGE = "RESTART";
    private static final String LINK_RELOAD_CARTRIDGE  = "RELOAD";

    private static Method GET_LINK_METHOD;
    private static Method GET_SERVICE_METHOD;
    private static Method GET_MODEL_NODE_METHOD;

    static {
        // Unfortunately cannot access methods for getting links to access info about application.
        // At the same time cannot get info about properties of embedded cartridges, e.g. get username and password for database.
        // Use reflection to resolve this problem.
        try {
            GET_LINK_METHOD = AbstractOpenShiftResource.class.getDeclaredMethod("getLink", String.class);
            GET_LINK_METHOD.setAccessible(true);
            GET_SERVICE_METHOD = AbstractOpenShiftResource.class.getDeclaredMethod("getService");
            GET_SERVICE_METHOD.setAccessible(true);
            GET_MODEL_NODE_METHOD = ResourceDTOFactory.class.getDeclaredMethod("getModelNode", String.class);
            GET_MODEL_NODE_METHOD.setAccessible(true);
        } catch (Exception ignored) {
        }
    }

    private final CredentialStore                     credentialStore;
    private final SshKeyStore                         sshKeyStore;
    private final OpenShiftConnectionFactory          openShiftConnectionFactory;
    // Provide cache for openshift-express connections.
    // Objects look heavy enough, and they stored info between requests.
    // Access to cache protected by lock.
    private final Cache<String, IOpenShiftConnection> connections;
    private final Lock                                lock;

    public Express(CredentialStore credentialStore, SshKeyStore sshKeyStore) {
        this.credentialStore = credentialStore;
        this.sshKeyStore = sshKeyStore;
        this.openShiftConnectionFactory = new OpenShiftConnectionFactory();
        this.connections = new SLRUCache<String, IOpenShiftConnection>(20, 10) {
            @Override
            protected void evict(String key, IOpenShiftConnection value) {
                if (value instanceof APIResource) {
                    ((APIResource)value).disconnect();
                }
            }
        };
        this.lock = new ReentrantLock();
    }

    public void login(String rhlogin, String password) throws ExpressException, CredentialStoreException {
        final String userId = getUserId();
        removeOpenShiftConnection(userId);
        newOpenShiftConnection(rhlogin, password);
        final Credential credential = getCredentials(userId);
        credential.setAttribute("rhlogin", rhlogin);
        credential.setAttribute("rhmail", rhlogin);
        credential.setAttribute("password", password);
        credentialStore.save(userId, "openshift_express", credential);
    }

    public void logout() throws CredentialStoreException {
        final String userId = getUserId();
        removeOpenShiftConnection(userId);
        final Credential credential = getCredentials(userId);
        credential.removeAttribute("rhlogin");
        credential.removeAttribute("rhmail");
        credential.removeAttribute("password");
        credentialStore.save(userId, "openshift_express", credential);
    }

    private Credential getCredentials(String userId) throws CredentialStoreException {
        final Credential credential = new Credential();
        credentialStore.load(userId, "openshift_express", credential);
        return credential;
    }

    private void removeOpenShiftConnection(String userId) {
        lock.lock();
        try {
            connections.remove(userId);
        } finally {
            lock.unlock();
        }
    }

    public void createDomain(String namespace, boolean alter) throws ExpressException, SshKeyStoreException, CredentialStoreException {
        createDomain(getOpenShiftConnection(), namespace, alter);
    }

    private void createDomain(IOpenShiftConnection connection, String namespace, boolean alter)
            throws ExpressException,
                   SshKeyStoreException,
                   CredentialStoreException {
        final String host = "rhcloud.com";

        SshKey publicKey;
        if (alter) {
            // Update SSH keys.
            sshKeyStore.removeKeys(host);
            sshKeyStore.genKeyPair(host, null, null, getCredentials(getUserId()).getAttribute("rhmail"));
            publicKey = sshKeyStore.getPublicKey(host);
        } else {
            publicKey = sshKeyStore.getPublicKey(host);
            if (publicKey == null) {
                sshKeyStore.genKeyPair(host, null, null, getCredentials(getUserId()).getAttribute("rhmail"));
                publicKey = sshKeyStore.getPublicKey(host);
            }
        }

        try {
            OpenShiftSSHKey sshKey = new OpenShiftSSHKey(publicKey);
            final IUser user = connection.getUser();
            if (user.getSSHKeyByName("default") != null) {
                user.getSSHKeyByName("default").setKeyType(sshKey.getKeyType(), sshKey.getPublicKey());
            } else {
                user.putSSHKey("default", sshKey);
            }
            if (!user.hasDomain(namespace)) {
                user.createDomain(namespace);
            }
        } catch (OpenShiftException e) {
            throw new ExpressException(500, e.getMessage(), "text/plain");
        }
    }

    /**
     * Create new application.
     *
     * @param appName
     *         application name
     * @param type
     *         application type
     * @param scale
     *         enable|disable application scaling
     * @param instanceType
     *         instance type, supported types: "micro", "small", "medium", "large", "exlarge", "jumbo"
     * @param workDir
     *         application directory
     * @return description of newly created application
     */
    public AppInfo createApplication(String appName,
                                     String type,
                                     boolean scale,
                                     String instanceType,
                                     File workDir) throws ExpressException, CredentialStoreException {
        return createApplication(getOpenShiftConnection(), appName, type, scale, instanceType, workDir);
    }

    private AppInfo createApplication(IOpenShiftConnection connection,
                                      String appName,
                                      String type,
                                      boolean scale,
                                      String instanceType,
                                      File workDir) throws ExpressException {
        validateAppType(type, connection);
        IApplication application;
        try {
            application = connection.getUser().getDefaultDomain()
                                    .createApplication(appName,
                                                       new StandaloneCartridge(type),
                                                       scale ? ApplicationScale.SCALE : ApplicationScale.NO_SCALE,
                                                       new GearProfile(instanceType));
        } catch (OpenShiftException e) {
            throw new ExpressException(500, e.getMessage(), "text/plain");
        }

        String gitUrl = application.getGitUrl();
        if (workDir != null) {
            GitConnection git = null;
            try {
                git = GitConnectionFactory.getInstance().getConnection(workDir, null);
                git.init(new InitRequest());
                git.remoteAdd(new RemoteAddRequest("express", gitUrl));
            } catch (GitException gite) {
                throw new RuntimeException(gite.getMessage(), gite);
            } finally {
                if (git != null) {
                    git.close();
                }
            }
        }

        return new AppInfoImpl(
                application.getName(),
                application.getCartridge().getName(),
                application.getGitUrl(),
                application.getApplicationUrl(),
                application.getCreationTime().getTime());
    }

    private void validateAppType(String type, IOpenShiftConnection connection) throws ExpressException {
        Set<String> supportedTypes = frameworks(connection);
        if (!supportedTypes.contains(type)) {
            StringBuilder msg = new StringBuilder();
            msg.append("Unsupported application type '");
            msg.append(type);
            msg.append("'. Must be ");
            int i = 0;
            for (String t : supportedTypes) {
                if (i > 0) {
                    msg.append(" or ");
                }
                msg.append(t);
                i++;
            }
            throw new IllegalArgumentException(msg.toString());
        }
    }

    public AppInfo addEmbeddableCartridges(String appName, List<String> embeddableCartridges)
            throws ExpressException,
                   CredentialStoreException {
        assertNotEmpty(appName, "Application name required. ");
        IOpenShiftConnection connection = getOpenShiftConnection();
        validateEmbeddableCartridgeType(embeddableCartridges, connection);
        return addEmbeddableCartridge(connection, appName, embeddableCartridges);
    }

    private void validateEmbeddableCartridgeType(List<String> embeddableCartridges, IOpenShiftConnection connection)
            throws ExpressException {
        Set<String> supportedCartridges = embeddableCartridges(connection);
        for (String embeddableCartridge : embeddableCartridges) {
            if (!supportedCartridges.contains(embeddableCartridge)) {
                StringBuilder msg = new StringBuilder();
                msg.append("Unsupported embeddable cartridge type '");
                msg.append(embeddableCartridge);
                msg.append("'. Must be ");
                int i = 0;
                for (String t : supportedCartridges) {
                    if (i > 0) {
                        msg.append(" or ");
                    }
                    msg.append(t);
                    i++;
                }
                throw new IllegalArgumentException(msg.toString());
            }
        }
    }

    private AppInfo addEmbeddableCartridge(IOpenShiftConnection connection, String appName, List<String> embeddableCartridges)
            throws ExpressException,
                   CredentialStoreException {
        try {
            IApplication application = connection.getUser().getDefaultDomain().getApplicationByName(appName);
            if (application != null) {
                List<IEmbeddableCartridge> myEmbeddableCartridges = new ArrayList<IEmbeddableCartridge>(embeddableCartridges.size());
                for (String embeddableCartridge : embeddableCartridges) {
                    myEmbeddableCartridges.add(new EmbeddableCartridge(embeddableCartridge));
                }
                application.addEmbeddableCartridges(myEmbeddableCartridges);
                AppInfoImpl myApplication = new AppInfoImpl(
                        application.getName(),
                        application.getCartridge().getName(),
                        application.getGitUrl(),
                        application.getApplicationUrl(),
                        application.getCreationTime().getTime()
                );
                myApplication.getEmbeddedCartridges().addAll(getApplicationEmbeddableCartridges(application));
                return myApplication;
            }
        } catch (OpenShiftException e) {
            throw new ExpressException(500, e.getMessage(), "text/plain");
        }
        throw new ExpressException(404, String.format("Application '%s' not found", appName), "text/plain");
    }

    public AppInfo removeEmbeddableCartridge(String appName, String embeddableCartridge)
            throws ExpressException, CredentialStoreException {
        assertNotEmpty(appName, "Application name required. ");
        return removeEmbeddableCartridge(getOpenShiftConnection(), appName, embeddableCartridge);
    }

    private AppInfo removeEmbeddableCartridge(IOpenShiftConnection connection, String appName, String embeddableCartridge)
            throws ExpressException,
                   CredentialStoreException {
        IApplication application = connection.getUser().getDefaultDomain().getApplicationByName(appName);
        if (application != null) {
            application.removeEmbeddedCartridge(new EmbeddableCartridge(embeddableCartridge));
            AppInfoImpl myApplication = new AppInfoImpl(
                    application.getName(),
                    application.getCartridge().getName(),
                    application.getGitUrl(),
                    application.getApplicationUrl(),
                    application.getCreationTime().getTime()
            );
            myApplication.getEmbeddedCartridges().addAll(getApplicationEmbeddableCartridges(application));
            return myApplication;
        }
        throw new ExpressException(404, String.format("Application '%s' not found", appName), "text/plain");
    }

    public AppInfo applicationInfo(String appName, File workDir) throws ExpressException, CredentialStoreException {
        if (appName == null || appName.isEmpty()) {
            appName = detectAppName(workDir);
        }
        return applicationInfo(getOpenShiftConnection(), appName);
    }

    private AppInfo applicationInfo(IOpenShiftConnection connection, String app) throws ExpressException {
        IApplication application = connection.getUser().getDefaultDomain().getApplicationByName(app);
        if (application != null) {
            AppInfoImpl myApplication = new AppInfoImpl(
                    application.getName(),
                    application.getCartridge().getName(),
                    application.getGitUrl(),
                    application.getApplicationUrl(),
                    application.getCreationTime().getTime()
            );
            myApplication.getEmbeddedCartridges().addAll(getApplicationEmbeddableCartridges(application));
            return myApplication;
        }
        throw new ExpressException(404, String.format("Application '%s' not found", app), "text/plain");
    }

    private Collection<OpenShiftEmbeddableCartridge> getApplicationEmbeddableCartridges(IApplication application) {
        final List<IEmbeddedCartridge> embeddedCartridges = application.getEmbeddedCartridges();
        final Map<String, OpenShiftEmbeddableCartridge> myEmbeddedCartridges =
                new LinkedHashMap<String, OpenShiftEmbeddableCartridge>(embeddedCartridges.size());
        for (IEmbeddedCartridge cartridge : embeddedCartridges) {
            myEmbeddedCartridges.put(cartridge.getName(), new OpenShiftEmbeddableCartridgeImpl(cartridge.getName(),
                                                                                               cartridge.getUrl()));
        }
        try {
            final Link link = (Link)GET_LINK_METHOD.invoke(application, LINK_LIST_CARTRIDGES);
            final IRestService service = (IRestService)GET_SERVICE_METHOD.invoke(application);
            final String content = service.request(link.getHref(), link.getHttpMethod(), null);
            final ModelNode rootNode = (ModelNode)GET_MODEL_NODE_METHOD.invoke(null, content);
            if (rootNode.has(IOpenShiftJsonConstants.PROPERTY_DATA)) {
                for (ModelNode cartridgeNode : rootNode.get(IOpenShiftJsonConstants.PROPERTY_DATA).asList()) {
                    final ModelNode nameNode = cartridgeNode.get(IOpenShiftJsonConstants.PROPERTY_NAME);
                    if (nameNode.isDefined()) {
                        final String name = nameNode.asString();
                        final OpenShiftEmbeddableCartridge openShiftCartridge = myEmbeddedCartridges.get(name);
                        if (openShiftCartridge != null && cartridgeNode.has("properties")) {
                            List<ModelNode> properties = cartridgeNode.get("properties").asList();
                            for (ModelNode property : properties) {
                                openShiftCartridge.getProperties().put(property.get("name").asString(),
                                                                       property.get("value").asString());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.warn("Failed to get link for load embedded cartridges list, use API method instead. " +
                     "Some info about cartridge may be not available. ", e);
        }
        return myEmbeddedCartridges.values();
    }

    public void destroyApplication(String appName) throws ExpressException, CredentialStoreException {
        assertNotEmpty(appName, "Application name required. ");
        destroyApplication(getOpenShiftConnection(), appName);
    }

    private void destroyApplication(IOpenShiftConnection connection, String app) throws ExpressException {
        try {
            connection.getUser().getDefaultDomain().getApplicationByName(app).destroy();
        } catch (OpenShiftException e) {
            throw new ExpressException(500, e.getMessage(), "text/plain");
        }
    }

    public void destroyAllApplicationsIncludeNamespace(boolean includeNamespace) throws ExpressException, CredentialStoreException {
        destroyAllApplicationsIncludeNamespace(getOpenShiftConnection(), includeNamespace);
    }

    private void destroyAllApplicationsIncludeNamespace(IOpenShiftConnection connection, boolean includeNamespace) throws ExpressException {
        try {
            if (includeNamespace) {
                connection.getUser().getDefaultDomain().destroy(true);
            } else {
                for (IApplication application : connection.getUser().getDefaultDomain().getApplications()) {
                    application.destroy();
                }
            }
        } catch (OpenShiftException e) {
            throw new ExpressException(500, e.getMessage(), "text/plain");
        }
    }

    public Set<String> frameworks() throws ExpressException, CredentialStoreException {
        return frameworks(getOpenShiftConnection());
    }

    private Set<String> frameworks(IOpenShiftConnection connection) throws ExpressException {
        try {
            final List<IStandaloneCartridge> cartridges = connection.getStandaloneCartridges();
            Set<String> frameworks = new LinkedHashSet<String>(cartridges.size());
            for (ICartridge cartridge : cartridges) {
                frameworks.add(cartridge.getName());
            }
            return frameworks;
        } catch (OpenShiftException e) {
            throw new ExpressException(500, e.getMessage(), "text/plain");
        }
    }

    public Set<String> embeddableCartridges() throws ExpressException, CredentialStoreException {
        return embeddableCartridges(getOpenShiftConnection());
    }

    private Set<String> embeddableCartridges(IOpenShiftConnection connection) throws ExpressException {
        try {
            final List<IEmbeddableCartridge> cartridges = connection.getEmbeddableCartridges();
            Set<String> frameworks = new LinkedHashSet<String>(cartridges.size());
            for (IEmbeddableCartridge cartridge : cartridges) {
                frameworks.add(cartridge.getName());
            }
            return frameworks;
        } catch (OpenShiftException e) {
            throw new ExpressException(500, e.getMessage(), "text/plain");
        }
    }

    public void startEmbeddedCartridge(String appName, String embeddedCartridgeName) throws ExpressException, CredentialStoreException {
        assertNotEmpty(appName, "Application name required. ");
        assertNotEmpty(embeddedCartridgeName, "Cartridge  name required. ");
        Map<String, Object> params = new HashMap<String, Object>(1);
        params.put("event", "start");
        sendEmbeddedCartridgeEvent(getOpenShiftConnection(), appName, embeddedCartridgeName, LINK_START_CARTRIDGE, params);
    }

    public void stopEmbeddedCartridge(String appName, String embeddedCartridgeName) throws ExpressException, CredentialStoreException {
        assertNotEmpty(appName, "Application name required. ");
        assertNotEmpty(embeddedCartridgeName, "Cartridge  name required. ");
        Map<String, Object> params = new HashMap<String, Object>(1);
        params.put("event", "stop");
        sendEmbeddedCartridgeEvent(getOpenShiftConnection(), appName, embeddedCartridgeName, LINK_STOP_CARTRIDGE, params);
    }

    public void restartEmbeddedCartridge(String appName, String embeddedCartridgeName) throws ExpressException, CredentialStoreException {
        assertNotEmpty(appName, "Application name required. ");
        assertNotEmpty(embeddedCartridgeName, "Cartridge  name required. ");
        Map<String, Object> params = new HashMap<String, Object>(1);
        params.put("event", "restart");
        sendEmbeddedCartridgeEvent(getOpenShiftConnection(), appName, embeddedCartridgeName, LINK_RESTART_CARTRIDGE, params);
    }

    public void reloadEmbeddedCartridge(String appName, String embeddedCartridgeName) throws ExpressException, CredentialStoreException {
        assertNotEmpty(appName, "Application name required. ");
        assertNotEmpty(embeddedCartridgeName, "Cartridge  name required. ");
        Map<String, Object> params = new HashMap<String, Object>(1);
        params.put("event", "reload");
        sendEmbeddedCartridgeEvent(getOpenShiftConnection(), appName, embeddedCartridgeName, LINK_RELOAD_CARTRIDGE, params);
    }

    private void sendEmbeddedCartridgeEvent(IOpenShiftConnection connection,
                                            String appName,
                                            String embeddedCartridgeName,
                                            String eventLink,
                                            Map<String, Object> params) throws ExpressException, CredentialStoreException {
        IApplication application = connection.getUser().getDefaultDomain().getApplicationByName(appName);
        if (application == null) {
            throw new ExpressException(500, String.format("Application '%s' does not exist. ", appName), "text/plain");
        }
        IEmbeddedCartridge embeddedCartridge = application.getEmbeddedCartridge(embeddedCartridgeName);
        if (embeddedCartridge == null) {
            throw new ExpressException(500,
                                       String.format("Not found cartridge %s in application '%s'. ", embeddedCartridgeName, appName),
                                       "text/plain");
        }
        try {
            final Link link = (Link)GET_LINK_METHOD.invoke(embeddedCartridge, eventLink);
            final IRestService service = (IRestService)GET_SERVICE_METHOD.invoke(application);
            service.request(link.getHref(), link.getHttpMethod(), params);
        } catch (Exception e) {
            throw new ExpressException(500, e.getMessage(), "text/plain");
        }
    }

    public RHUserInfo userInfo(boolean appsInfo) throws ExpressException, CredentialStoreException {
        return userInfo(getOpenShiftConnection(), appsInfo);
    }

    private RHUserInfo userInfo(IOpenShiftConnection connection, boolean appsInfo) throws ExpressException {
        try {
        IUser user = connection.getUser();
        IDomain domain = null;

        if (user.hasDomain()) {
            domain = user.getDefaultDomain();
        }

        RHUserInfo userInfo =
                new RHUserInfoImpl("rhcloud.com", null, user.getRhlogin(), (domain != null) ? domain.getId() : null);
        if (appsInfo && domain != null) {
            List<AppInfo> appInfoList = new ArrayList<AppInfo>();
            for (IApplication application : domain.getApplications()) {
                AppInfoImpl myApplication = new AppInfoImpl(
                        application.getName(),
                        application.getCartridge().getName(),
                        application.getGitUrl(),
                        application.getApplicationUrl(),
                        application.getCreationTime().getTime()
                );
                myApplication.getEmbeddedCartridges().addAll(getApplicationEmbeddableCartridges(application));
                appInfoList.add(myApplication);
            }
            userInfo.setApps(appInfoList);
        }
        return userInfo;
        } catch (OpenShiftException e) {
            throw new ExpressException(500, e.getMessage(), "text/plain");
        }
    }

    public void stopApplication(String appName) throws ExpressException, CredentialStoreException {
        assertNotEmpty(appName, "Application name required. ");
        stopApplication(getOpenShiftConnection(), appName);
    }

    private void stopApplication(IOpenShiftConnection connection, String appName) throws ExpressException {
        try {
            IApplication application = connection.getUser().getDefaultDomain().getApplicationByName(appName);
            if (application == null) {
                throw new ExpressException(500, String.format("Application '%s' does not exist. ", appName), "text/plain");
            }
            application.stop();
        } catch (OpenShiftException e) {
            throw new ExpressException(500, e.getMessage(), "text/plain");
        }
    }

    public void startApplication(String appName) throws ExpressException, CredentialStoreException {
        assertNotEmpty(appName, "Application name required. ");
        startApplication(getOpenShiftConnection(), appName);
    }

    private void startApplication(IOpenShiftConnection connection, String appName) throws ExpressException {
        try {
            IApplication application = connection.getUser().getDefaultDomain().getApplicationByName(appName);
            if (application == null) {
                throw new ExpressException(500, String.format("Application '%s' does not exist. ", appName), "text/plain");
            }
            application.start();
        } catch (OpenShiftException e) {
            throw new ExpressException(500, e.getMessage(), "text/plain");
        }
    }

    public void restartApplication(String appName) throws ExpressException, CredentialStoreException {
        assertNotEmpty(appName, "Application name required. ");
        restartApplication(getOpenShiftConnection(), appName);
    }

    private void restartApplication(IOpenShiftConnection connection, String appName) throws ExpressException {
        try {
            IApplication application = connection.getUser().getDefaultDomain().getApplicationByName(appName);
            if (application == null) {
                throw new ExpressException(500, String.format("Application '%s' does not exist. ", appName), "text/plain");
            }
            application.restart();
        } catch (OpenShiftException e) {
            throw new ExpressException(500, e.getMessage(), "text/plain");
        }
    }

    public String getApplicationHealth(String appName) throws ExpressException, CredentialStoreException {
        assertNotEmpty(appName, "Application name required. ");
        return getApplicationHealth(getOpenShiftConnection(), appName);
    }

    // Need to improve this checking
    private String getApplicationHealth(IOpenShiftConnection connection, String appName) throws ExpressException {
        InputStream checkStream = null;
        try {
            IApplication application = connection.getUser().getDefaultDomain().getApplicationByName(appName);
            if (application == null) {
                throw new ExpressException(500, String.format("Application '%s' does not exist. ", appName), "text/plain");
            }
            String appUrl = application.getApplicationUrl();
            checkStream = new URL(appUrl).openStream();
            return "STARTED";
        } catch (OpenShiftException e) {
            throw new ExpressException(500, e.getMessage(), "text/plain");
        } catch (IOException e) {
            if (e.getMessage().startsWith("Server returned HTTP response code: 503")) {
                return "STOPPED";
            }
        } finally {
            if (checkStream != null) {
                try {
                    checkStream.close();
                } catch (IOException ignored) {
                }
            }
        }

        return "STOPPED";
    }

    private String detectAppName(File workDir) {
        String app = null;
        if (workDir != null && new File(workDir, ".git").exists()) {
            GitConnection git = null;
            List<Remote> remotes;
            try {
                git = GitConnectionFactory.getInstance().getConnection(workDir, null);
                remotes = git.remoteList(new RemoteListRequest(null, true));
            } catch (GitException ge) {
                throw new RuntimeException(ge.getMessage(), ge);
            } finally {
                if (git != null) {
                    git.close();
                }
            }
            for (Iterator<Remote> iterator = remotes.iterator(); iterator.hasNext() && app == null; ) {
                Remote r = iterator.next();
                Matcher m = GIT_URL_PATTERN.matcher(r.getUrl());
                if (m.matches()) {
                    app = m.group(4);
                }
            }
        }
        if (app == null || app.isEmpty()) {
            throw new RuntimeException(
                    "Not an Openshift Express application. Please select root folder of Openshift Express project. ");
        }
        return app;
    }

    private IOpenShiftConnection getOpenShiftConnection() throws ExpressException, CredentialStoreException {
        final String userId = getUserId();
        lock.lock();
        try {
            IOpenShiftConnection connection = connections.get(userId);
            if (connection == null) {
                final Credential credential = new Credential();
                credentialStore.load(userId, "openshift_express", credential);
                final String rhlogin = credential.getAttribute("rhlogin");
                final String password = credential.getAttribute("password");
                if (rhlogin == null || password == null) {
                    throw new ExpressException(200, "Authentication required.\n", "text/plain");
                }
                connections.put(userId, connection = newOpenShiftConnection(rhlogin, password));
            }
            return connection;
        } catch (OpenShiftException e) {
            throw new ExpressException(500, String.format("Connection error. %s", e.getMessage()), "text/plain");
        } finally {
            lock.unlock();
        }
    }

    private String getUserId() {
        return ConversationState.getCurrent().getIdentity().getUserId();
    }

    private IOpenShiftConnection newOpenShiftConnection(String rhlogin, String password) throws ExpressException {
        try {
            IOpenShiftConnection connection =
                    openShiftConnectionFactory.getConnection("show-domain-info", rhlogin, password, OPENSHIFT_URL);
            connection.getUser(); // Throws exception if credential is invalid.
            return connection;
        } catch (OpenShiftException e) {
            throw new ExpressException(500, String.format("Connection error. %s", e.getMessage()), "text/plain");
        }
    }

    private void assertNotEmpty(String str, String message) throws ExpressException {
        if (str == null || str.isEmpty()) {
            throw new ExpressException(500, message, "text/plain");
        }
    }
}
