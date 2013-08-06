package com.codenvy.ide.ext.gae.client.wizard;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.template.CreateProjectProvider;
import com.codenvy.ide.api.template.TemplateAgent;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPagePresenter;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.UnauthorizedException;
import com.codenvy.ide.ext.gae.client.*;
import com.codenvy.ide.ext.gae.client.actions.LoginAction;
import com.codenvy.ide.ext.gae.client.create.CreateApplicationPresenter;
import com.codenvy.ide.ext.gae.client.marshaller.GaeUserUnmarshaller;
import com.codenvy.ide.ext.gae.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.gae.shared.GaeUser;
import com.codenvy.ide.ext.java.client.JavaExtension;
import com.codenvy.ide.resources.model.*;
import com.codenvy.ide.rest.MimeType;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class GAEWizardPresenter extends AbstractWizardPagePresenter implements GAEWizardView.ActionDelegate {
    private GAEWizardView              view;
    private EventBus                   eventBus;
    private String                     existedAppId;
    private ConsolePart                console;
    private GAELocalization            constant;
    private LoginAction                loginAction;
    private GAEClientService           service;
    private TemplateAgent              templateAgent;
    private CreateApplicationPresenter createApplicationPresenter;
    private ResourceProvider           resourceProvider;
    private Loader                     loader;
    private CreateProjectProvider      createProjectProvider;
    private boolean                    isLoggedIn;

    @Inject
    public GAEWizardPresenter(EventBus eventBus, GAEWizardView view, ConsolePart console,
                              GAELocalization constant, LoginAction loginAction,
                              GAEClientService service, TemplateAgent templateAgent, GAEResources resources,
                              CreateApplicationPresenter createApplicationPresenter, ResourceProvider resourceProvider,
                              Loader loader) {
        super("Deploy project to Google App Engine", resources.googleAppEngine48());

        this.eventBus = eventBus;
        this.view = view;
        this.console = console;
        this.constant = constant;
        this.loginAction = loginAction;
        this.service = service;
        this.templateAgent = templateAgent;
        this.createApplicationPresenter = createApplicationPresenter;
        this.resourceProvider = resourceProvider;
        this.loader = loader;

        this.view.setDelegate(this);
    }

    @Override
    public void onApplicationIdChanged() {
        this.existedAppId = view.getApplicationId();
    }

    @Override
    public void onAppIdRequiredClicked() {
        view.enableApplicationIdField(view.getAppIdRequired());
    }

    @Override
    public void go(AcceptsOneWidget container) {
        createProjectProvider = templateAgent.getSelectedTemplate().getCreateProjectProvider();

        view.enableApplicationIdField(view.getAppIdRequired());

        isLoggedIn();
        container.setWidget(view);
    }


    @Override
    public WizardPagePresenter flipToNext() {
        return null;
    }

    @Override
    public boolean canFinish() {
        return validate();
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public boolean isCompleted() {
        return validate();
    }

    @Override
    public void doFinish() {
        createProjectProvider.create(new AsyncCallback<Project>() {
            @Override
            public void onFailure(Throwable throwable) {
                Log.error(GAEWizardPresenter.class, throwable);
            }

            @Override
            public void onSuccess(Project project) {
                insertIntoProjectGaeConfig(project);
            }
        });
    }

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
                    createApplicationPresenter.showDialog(project);
                }
            }
        });

        clearFields();
    }

    @Override
    public String getNotice() {
        if (!isLoggedIn) {
            return "This project will be created without deploy to Google App Engine.";
        } else if (view.getAppIdRequired() && view.getApplicationId().isEmpty()) {
            return "Please, enter existed application's ID.";
        }

        return null;
    }

    private boolean validate() {
        return !view.getAppIdRequired() ||
               view.getAppIdRequired() && view.getApplicationId() != null && !view.getApplicationId().isEmpty();
    }

    private void isLoggedIn() {
        DtoClientImpls.GaeUserImpl user = DtoClientImpls.GaeUserImpl.make();
        GaeUserUnmarshaller unmarshaller = new GaeUserUnmarshaller(user);
        try {
            service.getLoggedUser(
                    new GAEAsyncRequestCallback<GaeUser>(unmarshaller, console, eventBus, constant, loginAction) {
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
            console.print(e.getMessage());
        }
    }

    private void askUserToLogin() {
        boolean startAuthorize = Window.confirm(
                "You aren't authorize to complete creating application.\nDo you want to login on Google App Engine?");
        if (startAuthorize) {
            loginAction.doLogin(userLoggedInCallback);
        } else {
            isLoggedIn = false;
        }
    }

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

    private void setExistedApplicationId(Project project) {
        final String vfsId = resourceProvider.getVfsId();

        try {
            service.setApplicationId(vfsId, project.getId(), existedAppId,
                                     new GAEAsyncRequestCallback<Object>(null, console, eventBus, constant, null) {
                                         @Override
                                         protected void onSuccess(Object result) {
                                             //ignore
                                         }
                                     });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void clearFields() {
        view.setAppIdRequired(false);
        view.setApplicationId("");
    }
}
