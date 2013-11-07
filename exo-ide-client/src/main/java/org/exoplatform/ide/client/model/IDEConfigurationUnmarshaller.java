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
package org.exoplatform.ide.client.model;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.configuration.IDEInitialConfiguration;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.workspaceinfo.CurrentWorkspaceInfo;
import org.exoplatform.ide.client.framework.workspaceinfo.WorkspaceInfo;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: May 25, 2011 evgen $
 */
public class IDEConfigurationUnmarshaller implements Unmarshallable<IDEInitialConfiguration> {
    private final static String CONTEXT = "context";

    public static final String LOOPBACK_SERVICE_CONTEXT = "/ide/loopbackcontent";

    private static final String APP_CONFIG = "configuration";

    private static final String USER_SETTINGS = "userSettings";

    private static final String VFS_ID = "vfsId";

    private static final String VFS_BASE_URL = "vfsBaseUrl";

    private static final String USER = "user";
    
    private static final String CURRENT_WORKSPACE = "currentWorkspace";
    
    private static final String WORKSPACE_INFO = "workspaceInfo";

    private static final String INVALID_CONFIGURATION_TITLE = IDE.ERRORS_CONSTANT.confInvalidConfTitle();

    private IDEInitialConfiguration initializationConfiguration;

    private IDEConfiguration configuration;

    private JSONObject defaultAppConfiguration;

    /**
     * @param initializationConfiguration
     * @param defaultAppConfiguration
     */
    public IDEConfigurationUnmarshaller(IDEInitialConfiguration initializationConfiguration,
                                        JSONObject defaultAppConfiguration) {
        this.initializationConfiguration = initializationConfiguration;
        this.defaultAppConfiguration = defaultAppConfiguration;
        configuration = new IDEConfiguration();
        configuration.setHiddenFiles(getHiddenFiles());
        this.initializationConfiguration.setIdeConfiguration(configuration);
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            JSONValue value = JSONParser.parseStrict(response.getText());
            if (value.isObject() != null) {
                JSONObject object = value.isObject();
                
                parseAppConfig(defaultAppConfiguration);
                ApplicationSettings applicationSettings = new ApplicationSettings();
                initializationConfiguration.setSettings(applicationSettings);
                if (object.containsKey(USER_SETTINGS)) {
                    new ApplicationSettingsUnmarshaller(applicationSettings).parseSettings(object.get(USER_SETTINGS));
                }

                if (object.containsKey(VFS_BASE_URL)) {
                    initializationConfiguration.getIdeConfiguration().setVfsBaseUrl(object.get(VFS_BASE_URL).isString().stringValue());
                }

                if (object.containsKey(VFS_ID)) {
                    initializationConfiguration.getIdeConfiguration().setVfsId(object.get(VFS_ID).isString().stringValue());
                }

                if (object.containsKey(USER)) {
                    String payload = object.get(USER).isObject().toString();
                    AutoBean<UserInfo> autoBean = AutoBeanCodex.decode(IDE.AUTO_BEAN_FACTORY, UserInfo.class, payload);
                    initializationConfiguration.setUserInfo(autoBean.as());
                }
                
                if (object.containsKey(CURRENT_WORKSPACE)) {
                    String payload = object.get(CURRENT_WORKSPACE).isObject().toString();
                    AutoBean<CurrentWorkspaceInfo> autoBean = AutoBeanCodex.decode(IDE.AUTO_BEAN_FACTORY, CurrentWorkspaceInfo.class, payload);
                    initializationConfiguration.setCurrentWorkspace(autoBean.as());
                }
                
                if (object.containsKey(WORKSPACE_INFO)) {
                    String payload = object.get(WORKSPACE_INFO).isObject().toString();
                    AutoBean<WorkspaceInfo> autoBean = AutoBeanCodex.decode(IDE.AUTO_BEAN_FACTORY, WorkspaceInfo.class, payload);
                    initializationConfiguration.setWorkspaceInfo(autoBean.as());
                }

            } else
                throw new Exception(IDE.ERRORS_CONSTANT.configurationReceivedJsonValueNotAnObject());
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnmarshallerException(IDE.ERRORS_CONSTANT.configurationCantParseApplicationSettings());
        }
    }

    private void parseAppConfig(JSONObject jsonConfiguration) {

        if (jsonConfiguration.containsKey(CONTEXT)) {
            configuration.setContext(jsonConfiguration.get(CONTEXT).isString().stringValue());
            configuration.setLoopbackServiceContext(configuration.getContext() + LOOPBACK_SERVICE_CONTEXT);
        } else {
            showErrorMessage(CONTEXT);
            return;
        }
    }

    private void showErrorMessage(String message) {
        String m = IDE.IDE_LOCALIZATION_MESSAGES.configurationInvalidConfiguration(message);
        Dialogs.getInstance().showError(INVALID_CONFIGURATION_TITLE, m);
    }

    private static native String getHiddenFiles() /*-{
        return $wnd.hiddenFiles;
    }-*/;

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload() */
    @Override
    public IDEInitialConfiguration getPayload() {
        return initializationConfiguration;
    }

}
