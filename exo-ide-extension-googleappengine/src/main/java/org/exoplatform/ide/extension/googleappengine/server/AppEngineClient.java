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
package org.exoplatform.ide.extension.googleappengine.server;

import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.security.oauth.OAuthTokenProvider;
import com.codenvy.security.shared.Token;
import com.google.appengine.tools.admin.AppAdmin;
import com.google.appengine.tools.admin.AppAdminFactory.ApplicationProcessingOptions;
import com.google.appengine.tools.admin.AppAdminFactory.ConnectOptions;
import com.google.appengine.tools.admin.AppVersionUpload;
import com.google.appengine.tools.admin.Application;
import com.google.appengine.tools.admin.CronEntry;
import com.google.appengine.tools.admin.GenericApplication;
import com.google.appengine.tools.admin.IdeAppAdmin;
import com.google.appengine.tools.admin.ResourceLimits;
import com.google.appengine.tools.admin.UpdateListener;
import com.google.apphosting.utils.config.BackendsXml;
import com.google.apphosting.utils.config.BackendsXml.State;

import org.exoplatform.ide.extension.googleappengine.shared.ApplicationInfo;
import org.exoplatform.ide.extension.googleappengine.shared.ApplicationInfoImpl;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.PropertyImpl;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.codenvy.commons.lang.IoUtil.createTempDirectory;
import static com.codenvy.commons.lang.IoUtil.downloadFile;
import static com.codenvy.commons.lang.ZipUtils.unzip;


/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AppEngineClient {
    private final OAuthTokenProvider oauthTokenProvider;

    private static final Log LOG = ExoLogger.getLogger(AppEngineClient.class);

    public AppEngineClient(OAuthTokenProvider oauthTokenProvider) {
        this.oauthTokenProvider = oauthTokenProvider;
    }

    public void configureBackend(VirtualFileSystem vfs, String projectId, String backendName,
                                 String userId) throws IOException, VirtualFileSystemException {
        IdeAppAdmin admin = createApplicationAdmin(vfs, projectId, userId);
        try {
            admin.configureBackend(backendName);
        } finally {
            admin.getApplication().cleanStagingDirectory();
        }
    }

    public List<CronEntry> cronInfo(VirtualFileSystem vfs, String projectId,
                                    String userId) throws IOException, VirtualFileSystemException {
        IdeAppAdmin admin = createApplicationAdmin(vfs, projectId, userId);
        try {
            return admin.cronInfo();
        } finally {
            admin.getApplication().cleanStagingDirectory();
        }
    }

    public void deleteBackend(VirtualFileSystem vfs, String projectId, String backendName,
                              String userId) throws IOException, VirtualFileSystemException {
        IdeAppAdmin admin = createApplicationAdmin(vfs, projectId, userId);
        try {
            admin.deleteBackend(backendName);
        } finally {
            admin.getApplication().cleanStagingDirectory();
        }
    }

    public Map<String, Long> getResourceLimits(VirtualFileSystem vfs, String projectId,
                                               String userId) throws IOException, VirtualFileSystemException {
        IdeAppAdmin admin = createApplicationAdmin(vfs, projectId, userId);
        try {
            ResourceLimits limits = admin.getResourceLimits();
            Set<String> keys = limits.keySet();
            Map<String, Long> result = new HashMap<String, Long>(keys.size());
            for (String name : keys) {
                result.put(name, limits.get(name));
            }
            return result;
        } finally {
            admin.getApplication().cleanStagingDirectory();
        }
    }

    public List<BackendsXml.Entry> listBackends(VirtualFileSystem vfs, String projectId,
                                                String userId) throws IOException, VirtualFileSystemException {
        IdeAppAdmin admin = createApplicationAdmin(vfs, projectId, userId);
        try {
            return admin.listBackends();
        } finally {
            admin.getApplication().cleanStagingDirectory();
        }
    }

    public void setBackendState(VirtualFileSystem vfs, String projectId, String backendName, String backendState,
                                String userId) throws IOException, VirtualFileSystemException {
        IdeAppAdmin admin = createApplicationAdmin(vfs, projectId, userId);
        try {
            admin.setBackendState(backendName, State.valueOf(backendState));
        } finally {
            admin.getApplication().cleanStagingDirectory();
        }
    }

    public Reader requestLogs(VirtualFileSystem vfs, String projectId, int numDays, String logSeverity,
                              String userId) throws IOException, VirtualFileSystemException {
        IdeAppAdmin admin = createApplicationAdmin(vfs, projectId, userId);
        try {
            return admin.requestLogs(numDays, logSeverity != null ? AppAdmin.LogSeverity.valueOf(logSeverity) : null);
        } finally {
            admin.getApplication().cleanStagingDirectory();
        }
    }

    public void rollback(VirtualFileSystem vfs, String projectId,
                         String userId) throws IOException, VirtualFileSystemException {
        IdeAppAdmin admin = createApplicationAdmin(vfs, projectId, userId);
        try {
            admin.rollback();
        } finally {
            admin.getApplication().cleanStagingDirectory();
        }
    }

    public void rollbackBackend(VirtualFileSystem vfs, String projectId, String backendName,
                                String userId) throws IOException, VirtualFileSystemException {
        IdeAppAdmin admin = createApplicationAdmin(vfs, projectId, userId);
        try {
            admin.rollbackBackend(backendName);
        } finally {
            admin.getApplication().cleanStagingDirectory();
        }
    }

    public void rollbackAllBackends(VirtualFileSystem vfs, String projectId,
                                    String userId) throws IOException, VirtualFileSystemException {
        IdeAppAdmin admin = createApplicationAdmin(vfs, projectId, userId);
        try {
            admin.rollbackAllBackends();
        } finally {
            admin.getApplication().cleanStagingDirectory();
        }
    }

    public ApplicationInfo update(VirtualFileSystem vfs, String projectId, URL binaries,
                                  String userId) throws IOException, VirtualFileSystemException {
        IdeAppAdmin admin;
        if (binaries != null) {
            // If binaries provided use it. In this case Java project expected.
            admin = createApplicationAdmin(new JavaApplication(Application.readApplication(getApplicationBinaries
                                                                                                   (binaries).getPath())), userId);
        } else {
            admin = createApplicationAdmin(vfs, projectId, userId);
        }
        try {
            admin.update(DUMMY_UPDATE_LISTENER);
            final String id = admin.getApplication().getAppId();
            return new ApplicationInfoImpl(id, "http://" + id + ".appspot.com");
        } finally {
            writeProjectProperty(vfs, projectId, "gae-application", admin.getApplication().getAppId());
            admin.getApplication().cleanStagingDirectory();

            //IDE-2934
            Project project = (Project) vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);
            if (ProjectType.JAVA == getApplicationType(vfs, project) && binaries != null) {
                LOG.info("EVENT#application-created# WS#"
                        + EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_NAME) + "# USER#" + userId
                        + "# PROJECT#" + project.getName() + "# TYPE#" + project.getProjectType()
                        + "# PAAS#GAE#");
            }
        }
    }

    private java.io.File getApplicationBinaries(URL url) throws IOException {
        java.io.File tempFile = downloadFile(null, "ide-appengine", null, url);
        java.io.File appDir = new java.io.File(tempFile.getParentFile(), tempFile.getName() + "_dir");
        appDir.mkdir();
        unzip(tempFile, appDir);
        tempFile.delete();
        return appDir;
    }

    public void updateBackend(VirtualFileSystem vfs, String projectId, String backendName,
                              String userId) throws IOException, VirtualFileSystemException {
        IdeAppAdmin admin = createApplicationAdmin(vfs, projectId, userId);
        try {
            admin.updateBackend(backendName, DUMMY_UPDATE_LISTENER);
        } finally {
            admin.getApplication().cleanStagingDirectory();
        }
    }

    public void updateBackends(VirtualFileSystem vfs, String projectId, List<String> backendNames,
                               String userId) throws IOException, VirtualFileSystemException {
        IdeAppAdmin admin = createApplicationAdmin(vfs, projectId, userId);
        try {
            admin.updateBackends(backendNames, DUMMY_UPDATE_LISTENER);
        } finally {
            admin.getApplication().cleanStagingDirectory();
        }
    }

    public void updateAllBackends(VirtualFileSystem vfs, String projectId,
                                  String userId) throws IOException, VirtualFileSystemException {
        IdeAppAdmin admin = createApplicationAdmin(vfs, projectId, userId);
        try {
            admin.updateAllBackends(DUMMY_UPDATE_LISTENER);
        } finally {
            admin.getApplication().cleanStagingDirectory();
        }
    }

    public void updateCron(VirtualFileSystem vfs, String projectId,
                           String userId) throws IOException, VirtualFileSystemException {
        IdeAppAdmin admin = createApplicationAdmin(vfs, projectId, userId);
        try {
            admin.updateCron();
        } finally {
            admin.getApplication().cleanStagingDirectory();
        }
    }

    public void updateDos(VirtualFileSystem vfs, String projectId,
                          String userId) throws IOException, VirtualFileSystemException {
        IdeAppAdmin admin = createApplicationAdmin(vfs, projectId, userId);
        try {
            admin.updateDos();
        } finally {
            admin.getApplication().cleanStagingDirectory();
        }
    }

    public void updateIndexes(VirtualFileSystem vfs, String projectId,
                              String userId) throws IOException, VirtualFileSystemException {
        IdeAppAdmin admin = createApplicationAdmin(vfs, projectId, userId);
        try {
            admin.updateIndexes();
        } finally {
            admin.getApplication().cleanStagingDirectory();
        }
    }

    //   public void updatePagespeed(VirtualFileSystem vfs,
    //                               String projectId,
    //                               String userId) throws IOException, VirtualFileSystemException
    //   {
    //      IdeAppAdmin admin = createApplicationAdmin(vfs, projectId, userId);
    //      try
    //      {
    //         admin.updatePagespeed();
    //      }
    //      finally
    //      {
    //         admin.getApplication().cleanStagingDirectory();
    //      }
    //   }

    public void updateQueues(VirtualFileSystem vfs, String projectId,
                             String userId) throws IOException, VirtualFileSystemException {
        IdeAppAdmin admin = createApplicationAdmin(vfs, projectId, userId);
        try {
            admin.updateQueues();
        } finally {
            admin.getApplication().cleanStagingDirectory();
        }
    }

    public void vacuumIndexes(VirtualFileSystem vfs, String projectId,
                              String userId) throws IOException, VirtualFileSystemException {
        IdeAppAdmin admin = createApplicationAdmin(vfs, projectId, userId);
        try {
            admin.vacuumIndexes(null, DUMMY_UPDATE_LISTENER);
        } finally {
            admin.getApplication().cleanStagingDirectory();
        }
    }

    private IdeAppAdmin createApplicationAdmin(VirtualFileSystem vfs, String projectId,
                                               String userId) throws IOException, VirtualFileSystemException {
        return createApplicationAdmin(createApplication(vfs, projectId, userId), userId);
    }

    private IdeAppAdmin createApplicationAdmin(GenericApplication application,
                                               String userId) throws IOException, VirtualFileSystemException {
        ConnectOptions options = new ConnectOptions();
        if (userId != null) {
            Token token = oauthTokenProvider.getToken("google", userId);
            String oAuthToken = token != null ? token.getToken() : null;
            if (oAuthToken != null) {
                options.setOauthToken(oAuthToken);
            }
        }
        return new IdeAppAdmin(options, application, new PrintWriter(DUMMY_WRITER), new ApplicationProcessingOptions(),
                               AppVersionUpload.class);
    }

    private GenericApplication createApplication(VirtualFileSystem vfs,
                                                 String projectId, String userId) throws VirtualFileSystemException, IOException {
        String itemId = projectId;
        while (!vfs.getItem(itemId, false, PropertyFilter.ALL_FILTER).getItemType().value().equals(ItemType.PROJECT.value())){
            Folder folder = (Folder)vfs.getItem(itemId, false, PropertyFilter.ALL_FILTER);
            itemId = folder.getParentId();
        }
        Project project = (Project)vfs.getItem(itemId, false, PropertyFilter.ALL_FILTER);
        ProjectType type = getApplicationType(vfs, project);
        switch (type) {
            case JAVA: {
                Folder webApp = (Folder)vfs.getItemByPath(project.createPath("src/main/webapp"), null, false, PropertyFilter.NONE_FILTER);
                java.io.File appDir = createTempDirectory(null, "ide-appengine");
                unzip(vfs.exportZip(webApp.getId()).getStream(), appDir);
                JavaApplication app = new JavaApplication(Application.readApplication(appDir.getAbsolutePath()));
                if (projectId != null) {
                    writeProjectProperty(vfs, projectId, "gae-application", app.getAppId());
                    writeProjectProperty(vfs, projectId, "gae-target", app.getServer());
                }
                LOG.info("EVENT#application-created# WS#"
                         + EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_NAME) + "# USER#" + userId
                         + "# PROJECT#" + project.getName() + "# TYPE#" + project.getProjectType()
                         + "# PAAS#GAE#");
                return app;
            }
            // Do for PHP in the same way as for Python for the moment. Even appengine SDK use Python runtime for PHP so it is OK for us.
            case PYTHON:
            case PHP: {
                java.io.File appDir = createTempDirectory(null, "ide-appengine");
                unzip(vfs.exportZip(projectId).getStream(), appDir);
                java.io.File projectFile = new java.io.File(appDir, ".codenvy");
                if (projectFile.exists()) {
                    projectFile.delete();
                }
                PythonApplication app = type == ProjectType.PYTHON ? new PythonApplication(appDir) : new PythonApplication(appDir){
                    @Override
                    public String getSourceLanguage() {
                        return "PHP";
                    }
                };
                if (projectId != null) {
                    writeProjectProperty(vfs, projectId, "gae-application", app.getAppId());
                    writeProjectProperty(vfs, projectId, "gae-target", app.getServer());
                }
                LOG.info("EVENT#application-created# WS#"
                         + EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_NAME) + "# USER#" + userId
                         + "# PROJECT#" + project.getName() + "# TYPE#" + project.getProjectType()
                         + "# PAAS#GAE#");
                return app;
            }
            default:
                throw new RuntimeException("Unsupported type of application " + type);
        }
    }

    private void writeProjectProperty(VirtualFileSystem vfs, String projectId, String propertyName, String propertyValue)
            throws VirtualFileSystemException {
        Property p = new PropertyImpl(propertyName, propertyValue);
        List<Property> properties = new ArrayList<Property>(1);
        properties.add(p);
        vfs.updateItem(projectId, properties, null);
    }

    private enum ProjectType {
        JAVA, PYTHON, PHP /*, GO*/
    }

    private ProjectType getApplicationType(VirtualFileSystem vfs,
                                           Project project) throws VirtualFileSystemException, IOException {
        try {
            vfs.getItemByPath(project.createPath("src/main/webapp/WEB-INF/web.xml"), null, false, PropertyFilter.NONE_FILTER);
            return ProjectType.JAVA;
        } catch (ItemNotFoundException e) {
            try {
                ContentStream appYaml = vfs.getContent(project.createPath("app.yaml"), null);
                InputStream in = null;
                BufferedReader r = null;
                try {
                    in = appYaml.getStream();
                    r = new BufferedReader(new InputStreamReader(in));
                    YamlAppInfo appInfo = YamlAppInfo.parse(r);
                    if ("python".equals(appInfo.runtime) || "python27".equals(appInfo.runtime)) {
                        return ProjectType.PYTHON;
                    } else if ("php".equals(appInfo.runtime)) {
                        return ProjectType.PHP;
                    } else if ("java".equals(appInfo.runtime)) {
                        return ProjectType.JAVA;
                    }
               /*else if ("go".equals(appInfo.runtime))
               {
                  return ProjectType.GO;
               }*/
                } finally {
                    try {
                        if (r != null) {
                            r.close();
                        }
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException ignored) {
                    }
                }
            } catch (ItemNotFoundException ignored) {
            }
        }
        throw new RuntimeException("Unable determine type of application. ");
    }

   /* ============================================================================= */

    private static final Writer DUMMY_WRITER = new DummyWriter();

    private static class DummyWriter extends Writer {
        public void close() {
        }

        public void flush() {
        }

        public void write(char[] cBuf, int off, int len) {
        }
    }

    private static final UpdateListener DUMMY_UPDATE_LISTENER = new DummyUpdateListener();

    private static class DummyUpdateListener implements UpdateListener {
        @Override
        public void onSuccess(com.google.appengine.tools.admin.UpdateSuccessEvent event) {
        }

        @Override
        public void onProgress(com.google.appengine.tools.admin.UpdateProgressEvent event) {
        }

        @Override
        public void onFailure(com.google.appengine.tools.admin.UpdateFailureEvent event) {
        }
    }
}
