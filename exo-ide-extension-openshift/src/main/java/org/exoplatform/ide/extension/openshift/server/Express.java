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
package org.exoplatform.ide.extension.openshift.server;

import com.codenvy.ide.commons.cache.Cache;
import com.codenvy.ide.commons.cache.SLRUCache;
import com.openshift.client.ApplicationScale;
import com.openshift.client.IApplication;
import com.openshift.client.ICartridge;
import com.openshift.client.IDomain;
import com.openshift.client.IEmbeddableCartridge;
import com.openshift.client.IEmbeddedCartridge;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftConnectionFactory;
import com.openshift.client.OpenShiftException;
import com.openshift.internal.client.APIResource;
import com.openshift.internal.client.Cartridge;
import com.openshift.internal.client.EmbeddableCartridge;
import com.openshift.internal.client.GearProfile;

import org.exoplatform.ide.extension.openshift.shared.AppInfo;
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
import org.exoplatform.services.security.ConversationState;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/** @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a> */
public class Express {
    private static final Pattern GIT_URL_PATTERN = Pattern
            .compile("ssh://(\\w+)@(\\w+)-(\\w+)\\.rhcloud\\.com/~/git/(\\w+)\\.git/");

    private static final String OPENSHIFT_URL = "https://openshift.redhat.com";

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
        final Credential credential = new Credential();
        credentialStore.load(userId, "openshift_express", credential);
        credential.setAttribute("rhlogin", rhlogin);
        credential.setAttribute("password", password);
        credentialStore.save(userId, "openshift_express", credential);
    }

    public void logout() throws CredentialStoreException {
        final String userId = getUserId();
        removeOpenShiftConnection(userId);
        final Credential credential = new Credential();
        credentialStore.load(userId, "openshift_express", credential);
        credential.removeAttribute("rhlogin");
        credential.removeAttribute("password");
        credentialStore.save(userId, "openshift_express", credential);
    }

    private void removeOpenShiftConnection(String userId) {
        lock.lock();
        try {
            connections.remove(userId);
        } finally {
            lock.unlock();
        }
    }

    public void createDomain(String namespace, boolean alter)
            throws ExpressException, SshKeyStoreException, CredentialStoreException {
        IOpenShiftConnection connection = getOpenShiftConnection();
        createDomain(connection, namespace, alter);
    }

    private void createDomain(IOpenShiftConnection connection, String namespace, boolean alter)
            throws ExpressException, SshKeyStoreException {
        final String host = "rhcloud.com";

        SshKey publicKey;
        if (alter) {
            // Update SSH keys.
            sshKeyStore.removeKeys(host);
            sshKeyStore.genKeyPair(host, null, null);
            publicKey = sshKeyStore.getPublicKey(host);
        } else {
            publicKey = sshKeyStore.getPublicKey(host);
            if (publicKey == null) {
                sshKeyStore.genKeyPair(host, null, null);
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
     * @param app
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
    public AppInfo createApplication(String app,
                                     String type,
                                     boolean scale,
                                     String instanceType,
                                     File workDir) throws ExpressException, CredentialStoreException {
        IOpenShiftConnection connection = getOpenShiftConnection();
        return createApplication(connection, app, type, scale, instanceType, workDir);
    }

    private AppInfo createApplication(IOpenShiftConnection connection,
                                      String app,
                                      String type,
                                      boolean scale,
                                      String instanceType,
                                      File workDir)
            throws ExpressException {
        validateAppType(type, connection);
        IApplication application;
        try {
            application = connection.getUser().getDefaultDomain().createApplication(app,
                                                                                    Cartridge.valueOf(type),
                                                                                    scale ? ApplicationScale.SCALE
                                                                                          : ApplicationScale.NO_SCALE,
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
                application.getCreationTime().getTime()
        );
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

    public AppInfo addEmbeddableCartridges(String app, File workDir, List<String> embeddableCartridges)
            throws ExpressException, CredentialStoreException {
        if (app == null || app.isEmpty()) {
            app = detectAppName(workDir);
        }
        IOpenShiftConnection connection = getOpenShiftConnection();
        validateEmbeddableCartridgeType(embeddableCartridges, connection);
        return addEmbeddableCartridge(connection, app, embeddableCartridges);
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

    private AppInfo addEmbeddableCartridge(IOpenShiftConnection connection, String app, List<String> embeddableCartridges)
            throws ExpressException, CredentialStoreException {
        IApplication application = connection.getUser().getDefaultDomain().getApplicationByName(app);
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
            for (IEmbeddedCartridge embeddedCartridge : application.getEmbeddedCartridges()) {
                myApplication.getEmbeddedCartridges()
                             .add(new OpenShiftEmbeddableCartridgeImpl(embeddedCartridge.getName(),
                                                                       embeddedCartridge.getUrl(),
                                                                       embeddedCartridge.getCreationLog()));
            }
            return myApplication;
        }
        throw new ExpressException(404, String.format("Application '%s' not found", app), "text/plain");
    }

    public AppInfo removeEmbeddableCartridge(String app, File workDir, String embeddableCartridge)
            throws ExpressException, CredentialStoreException {
        if (app == null || app.isEmpty()) {
            app = detectAppName(workDir);
        }
        return removeEmbeddableCartridge(getOpenShiftConnection(), app, embeddableCartridge);
    }

    private AppInfo removeEmbeddableCartridge(IOpenShiftConnection connection, String app, String embeddableCartridge)
            throws ExpressException, CredentialStoreException {
        IApplication application = connection.getUser().getDefaultDomain().getApplicationByName(app);
        if (application != null) {
            application.removeEmbeddedCartridge(new EmbeddableCartridge(embeddableCartridge));
            AppInfoImpl myApplication = new AppInfoImpl(
                    application.getName(),
                    application.getCartridge().getName(),
                    application.getGitUrl(),
                    application.getApplicationUrl(),
                    application.getCreationTime().getTime()
            );
            for (IEmbeddedCartridge embeddedCartridge : application.getEmbeddedCartridges()) {
                myApplication.getEmbeddedCartridges()
                             .add(new OpenShiftEmbeddableCartridgeImpl(embeddedCartridge.getName(),
                                                                       embeddedCartridge.getUrl(),
                                                                       embeddedCartridge.getCreationLog()));
            }
            return myApplication;
        }
        throw new ExpressException(404, String.format("Application '%s' not found", app), "text/plain");
    }

    public AppInfo applicationInfo(String app, File workDir) throws ExpressException, CredentialStoreException {
        if (app == null || app.isEmpty()) {
            app = detectAppName(workDir);
        }
        return applicationInfo(getOpenShiftConnection(), app);
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
            for (IEmbeddedCartridge embeddedCartridge : application.getEmbeddedCartridges()) {
                myApplication.getEmbeddedCartridges()
                             .add(new OpenShiftEmbeddableCartridgeImpl(embeddedCartridge.getName(),
                                                                       embeddedCartridge.getUrl(),
                                                                       embeddedCartridge.getCreationLog()));
            }
            return myApplication;
        }
        throw new ExpressException(404, String.format("Application '%s' not found", app), "text/plain");
    }

    public void destroyApplication(String app, File workDir) throws ExpressException, CredentialStoreException {
        if (app == null || app.isEmpty()) {
            app = detectAppName(workDir);
        }
        IOpenShiftConnection connection = getOpenShiftConnection();
        destroyApplication(connection, app);
    }

    private void destroyApplication(IOpenShiftConnection connection, String app) throws ExpressException {
        try {
            connection.getUser().getDefaultDomain().getApplicationByName(app).destroy();
        } catch (OpenShiftException e) {
            throw new ExpressException(500, e.getMessage(), "text/plain");
        }
    }

    public Set<String> frameworks() throws ExpressException, CredentialStoreException {
        IOpenShiftConnection connection = getOpenShiftConnection();
        return frameworks(connection);
    }

    private Set<String> frameworks(IOpenShiftConnection connection) throws ExpressException {
        try {
            final List<ICartridge> cartridges = connection.getStandaloneCartridges();
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

    public RHUserInfo userInfo(boolean appsInfo) throws ExpressException, CredentialStoreException {
        IOpenShiftConnection connection = getOpenShiftConnection();
        return userInfo(connection, appsInfo);
    }

    private RHUserInfo userInfo(IOpenShiftConnection connection, boolean appsInfo) throws ExpressException {
        try {
            IUser user = connection.getUser();
            IDomain domain = null;

            if (user.hasDomain()) {
                domain = user.getDefaultDomain();
            }

            RHUserInfo userInfo =
                    new RHUserInfoImpl("rhcloud.com", null, user.getRhlogin(), (domain != null) ? domain.getId() : "Doesn't exist");
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
                    for (IEmbeddedCartridge embeddedCartridge : application.getEmbeddedCartridges()) {
                        myApplication.getEmbeddedCartridges()
                                     .add(new OpenShiftEmbeddableCartridgeImpl(embeddedCartridge.getName(),
                                                                               embeddedCartridge.getUrl(),
                                                                               embeddedCartridge.getCreationLog()));
                    }

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
        if (!(appName == null || appName.isEmpty())) {
            stopApplication(getOpenShiftConnection(), appName);
        } else {
            throw new ExpressException(200, "Application name required. ", "text/plain");
        }
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
        if (!(appName == null || appName.isEmpty())) {
            startApplication(getOpenShiftConnection(), appName);
        } else {
            throw new ExpressException(200, "Application name required. ", "text/plain");
        }
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
        if (!(appName == null || appName.isEmpty())) {
            restartApplication(getOpenShiftConnection(), appName);
        } else {
            throw new ExpressException(500, "Application name required. ", "text/plain");
        }
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
        if (!(appName == null || appName.isEmpty())) {
            return getApplicationHealth(getOpenShiftConnection(), appName);
        }
        throw new ExpressException(500, "Application name required. ", "text/plain");
    }

    //Need to improve this checking
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

    private static String detectAppName(File workDir) {
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
            connection.getUser(); // Throws exception if credentials is invalid.
            return connection;
        } catch (OpenShiftException e) {
            throw new ExpressException(500, String.format("Connection error. %s", e.getMessage()), "text/plain");
        }
    }
}
