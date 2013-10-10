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

package com.codenvy.ide.ext.gae.client.wizard;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPage;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.UnauthorizedException;
import com.codenvy.ide.ext.gae.client.*;
import com.codenvy.ide.ext.gae.client.actions.LoginAction;
import com.codenvy.ide.ext.gae.client.create.CreateApplicationPresenter;
import com.codenvy.ide.ext.gae.client.marshaller.GaeUserUnmarshaller;
import com.codenvy.ide.ext.gae.shared.GaeUser;
import com.codenvy.ide.ext.java.client.JavaExtension;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.MimeType;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.ui.wizard.WizardKeys.PROJECT_NAME;

/**
 * Presenter that allow user to use Create Project Wizard to configure deployment application on Google App Engine.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class GAEWizardPagePresenter extends AbstractWizardPage implements GAEWizardView.ActionDelegate {
    private GAEWizardView              view;
    private EventBus                   eventBus;
    private String                     existedAppId;
    private GAELocalization            constant;
    private LoginAction                loginAction;
    private GAEClientService           service;
    private CreateApplicationPresenter createApplicationPresenter;
    private ResourceProvider           resourceProvider;
    private NotificationManager        notificationManager;
    private boolean                    isLoggedIn;
    private CommitCallback             callback;

    /** Constructor for Google App Engine Wizard page. */
    @Inject
    public GAEWizardPagePresenter(EventBus eventBus, GAEWizardView view, GAELocalization constant, LoginAction loginAction,
                                  GAEClientService service, GAEResources resources, CreateApplicationPresenter createApplicationPresenter,
                                  ResourceProvider resourceProvider, NotificationManager notificationManager) {
        super("Deploy project to Google App Engine", resources.googleAppEngine48());

        this.eventBus = eventBus;
        this.view = view;
        this.constant = constant;
        this.loginAction = loginAction;
        this.service = service;
        this.createApplicationPresenter = createApplicationPresenter;
        this.resourceProvider = resourceProvider;
        this.notificationManager = notificationManager;

        this.view.setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public void onApplicationIdChanged() {
        this.existedAppId = view.getApplicationId();
    }

    /** {@inheritDoc} */
    @Override
    public void onAppIdRequiredClicked() {
        view.enableApplicationIdField(view.getAppIdRequired());
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        view.enableApplicationIdField(view.getAppIdRequired());

        isLoggedIn();
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return validate();
    }

    /** {@inheritDoc} */
    @Override
    public void focusComponent() {
        //do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void removeOptions() {
        //do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void commit(@NotNull CommitCallback callback) {
        this.callback = callback;

        // TODO may be improve with getProject?
        resourceProvider.getProject(wizardContext.getData(PROJECT_NAME), new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                insertIntoProjectGaeConfig(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                GAEWizardPagePresenter.this.callback.onFailure(caught);
            }
        });
    }

    /**
     * Creating Google App Engine configuration file into newly created project.
     *
     * @param project
     *         project that created on previous step.
     */
    private void insertIntoProjectGaeConfig(final Project project) {
        String projectType = (String)project.getPropertyValue("vfs:projectType");

        AsyncCallback<File> internalCallback = new AsyncCallback<File>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Failed to create Google App Engine configuration file.");
            }

            @Override
            public void onSuccess(File result) {
                if (view.getAppIdRequired()) {
                    setExistedApplicationId(project);
                }
            }
        };

        if (projectType.equals(JavaExtension.JAVA_WEB_APPLICATION_PROJECT_TYPE)) {
            Folder webInfFolder = (Folder)project.findResourceByName("WEB-INF", Folder.TYPE);

            if (webInfFolder == null) {
                Window.alert("Failed to locate WEB-INF directory to create appengine-web.xml");
                return;
            }

            final String xmlConfigContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                                            "<appengine-web-app xmlns=\"http://appengine.google.com/ns/1.0\">\n" +
                                            "<application>example</application>\n" +
                                            "    <version>1</version>\n" +
                                            "    <threadsafe>true</threadsafe>\n" +
                                            "</appengine-web-app>";
            final String xmlConfigName = "appengine-web.xml";

            project.createFile(webInfFolder, xmlConfigName, xmlConfigContent, MimeType.APPLICATION_ATOM_XML,
                               internalCallback);

        } else {
            //TODO complete this section for python and php projects
            //TODO place app.yaml into root of project
            //draft
            //final String yamlPHPConfigContent = "application: example\n" +
            //                                    "version: 1\n" +
            //                                    "runtime: php\n" +
            //                                    "api_version: 1\n" +
            //                                    "handlers:\n" +
            //                                    "- url: /.*\n" +
            //                                    "  script: index.php";
            //
            //final String yamlConfigName = "app.yaml";
            //
            //project.createFile(project, yamlConfigName, yamlPHPConfigContent, MimeType.TEXT_YAML, internalCallback);
        }

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                if (isLoggedIn) {
                    createApplicationPresenter.showDialog(project, callback);
                }
            }
        });

        clearFields();
    }

    /** {@inheritDoc} */
    @Override
    public String getNotice() {
        if (!isLoggedIn) {
            return "This project will be created without deploy to Google App Engine.";
        } else if (view.getAppIdRequired() && view.getApplicationId().isEmpty()) {
            return "Please, enter existed application's ID.";
        }

        return null;
    }

    /**
     * Validate filling form if existed application id checkbox activated.
     *
     * @return true if checkbox activated and text box field with application id filled correctly.
     */
    private boolean validate() {
        return !view.getAppIdRequired() ||
               view.getAppIdRequired() && view.getApplicationId() != null && !view.getApplicationId().isEmpty();
    }

    /** Checks if user is logged in on Google Services. If user isn't logged in IDE ask user to login. */
    private void isLoggedIn() {
        GaeUserUnmarshaller unmarshaller = new GaeUserUnmarshaller();
        try {
            service.getLoggedUser(
                    new GAEAsyncRequestCallback<GaeUser>(unmarshaller, eventBus, constant, loginAction, notificationManager) {
                        @Override
                        protected void onSuccess(GaeUser result) {
                            if (GAEExtension.isUserHasGaeScopes(result.getToken())) {
                                isLoggedIn = true;
                            } else {
                                askUserToLogin();
                            }
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            if (exception instanceof UnauthorizedException) {
                                askUserToLogin();
                            }
                        }
                    });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** Ask user to login on Google Services, and when user agree, IDE open native popup window to start authorize user. */
    private void askUserToLogin() {
        boolean startAuthorize = Window.confirm(
                "You aren't authorize to complete creating application.\nDo you want to login on Google App Engine?");
        if (startAuthorize) {
            loginAction.doLogin(userLoggedInCallback);
        } else {
            isLoggedIn = false;
        }
    }

    /** Handler that checks if user is logged in and sets login status. */
    private AsyncCallback<Boolean> userLoggedInCallback = new AsyncCallback<Boolean>() {
        @Override
        public void onFailure(Throwable throwable) {
            //ignore
        }

        @Override
        public void onSuccess(Boolean loggedIn) {
            isLoggedIn = loggedIn;
        }
    };

    /**
     * Is user activated checkbox on wizard page sets application id into newly created project to allow user update existed application on
     * Google App Engine.
     *
     * @param project
     *         project in what should be changed configuration files.
     */
    private void setExistedApplicationId(Project project) {
        final String vfsId = resourceProvider.getVfsId();

        try {
            service.setApplicationId(vfsId, project.getId(), existedAppId,
                                     new GAEAsyncRequestCallback<Object>(null, eventBus, constant, null, notificationManager) {
                                         @Override
                                         protected void onSuccess(Object result) {
                                             //ignore
                                         }
                                     });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Clear fields. */
    private void clearFields() {
        view.setAppIdRequired(false);
        view.setApplicationId("");
    }
}
