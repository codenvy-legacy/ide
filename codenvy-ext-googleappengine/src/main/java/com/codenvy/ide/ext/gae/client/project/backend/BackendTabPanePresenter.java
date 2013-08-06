package com.codenvy.ide.ext.gae.client.project.backend;

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.gae.client.GAEAsyncRequestCallback;
import com.codenvy.ide.ext.gae.client.GAEClientService;
import com.codenvy.ide.ext.gae.client.GAELocalization;
import com.codenvy.ide.ext.gae.client.actions.LoginAction;
import com.codenvy.ide.ext.gae.client.marshaller.BackendsUnmarshaller;
import com.codenvy.ide.ext.gae.shared.Backend;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
@Singleton
public class BackendTabPanePresenter implements Presenter, BackendTabPaneView.ActionDelegate {
    private BackendTabPaneView view;
    private GAEClientService   service;
    private EventBus           eventBus;
    private ConsolePart        console;
    private ResourceProvider   resourceProvider;
    private GAELocalization    constant;
    private LoginAction        loginAction;
    private Project            project;

    @Inject
    public BackendTabPanePresenter(BackendTabPaneView view, GAEClientService service,
                                   EventBus eventBus, ConsolePart console,
                                   ResourceProvider resourceProvider,
                                   GAELocalization constant, LoginAction loginAction) {
        this.view = view;
        this.service = service;
        this.eventBus = eventBus;
        this.console = console;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.loginAction = loginAction;

        this.view.setDelegate(this);
    }

    public void init(Project project) {
        this.project = project;

        final String vfsId = resourceProvider.getVfsId();
        BackendsUnmarshaller unmarshaller = new BackendsUnmarshaller(JsonCollections.<Backend>createArray());

        try {
            service.listBackends(vfsId, project.getId(),
                                 new GAEAsyncRequestCallback<JsonArray<Backend>>(unmarshaller, console, eventBus,
                                                                                 constant, loginAction) {
                                     @Override
                                     protected void onSuccess(JsonArray<Backend> result) {
                                         view.setBackendsList(result);
                                         view.setEnableUpdateButtons(false);
                                     }
                                 });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    @Override
    public void onConfigureBackendClicked() {
        final String vfsId = resourceProvider.getVfsId();
        final Backend backend = view.getSelectedBackend();

        if (backend == null) {
            Window.alert("You should select backend to complete this operation.");
            return;
        }

        try {
            service.configureBackend(vfsId, project.getId(), backend.getName(),
                                     new GAEAsyncRequestCallback<Object>(null, console, eventBus, constant,
                                                                         loginAction) {
                                         @Override
                                         protected void onSuccess(Object result) {
                                             console.print(constant.configureBackendSuccessfully(backend.getName()));
                                             init(project);
                                         }
                                     });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    @Override
    public void onDeleteBackendClicked() {
        final String vfsId = resourceProvider.getVfsId();
        final Backend backend = view.getSelectedBackend();

        if (backend == null) {
            Window.alert("You should select backend to complete this operation.");
            return;
        }

        try {
            service.deleteBackend(vfsId, project.getId(), backend.getName(),
                                  new GAEAsyncRequestCallback<Object>(null, console, eventBus, constant,
                                                                      loginAction) {
                                      @Override
                                      protected void onSuccess(Object result) {
                                          console.print(constant.deleteBackendSuccessfully(backend.getName()));
                                          init(project);
                                      }
                                  });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    @Override
    public void onUpdateBackendClicked() {
        final String vfsId = resourceProvider.getVfsId();
        final Backend backend = view.getSelectedBackend();

        if (backend == null) {
            Window.alert("You should select backend to complete this operation.");
            return;
        }

        try {
            service.updateBackend(vfsId, project.getId(), backend.getName(),
                                  new GAEAsyncRequestCallback<Object>(null, console, eventBus, constant,
                                                                      loginAction) {
                                      @Override
                                      protected void onSuccess(Object result) {
                                          console.print(constant.updateBackendSuccessfully(backend.getName()));
                                          init(project);
                                      }
                                  });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    @Override
    public void onRollBackBackendClicked() {
        final String vfsId = resourceProvider.getVfsId();
        final Backend backend = view.getSelectedBackend();

        if (backend == null) {
            Window.alert("You should select backend to complete this operation.");
            return;
        }

        try {
            service.rollbackBackend(vfsId, project.getId(), backend.getName(),
                                    new GAEAsyncRequestCallback<Object>(null, console, eventBus, constant,
                                                                        loginAction) {
                                        @Override
                                        protected void onSuccess(Object result) {
                                            console.print(constant.rollbackBackendSuccessfully(backend.getName()));
                                            init(project);
                                        }
                                    });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    @Override
    public void onUpdateAllBackendsClicked() {
        final String vfsId = resourceProvider.getVfsId();

        try {
            service.updateAllBackends(vfsId, project.getId(),
                                      new GAEAsyncRequestCallback<Object>(null, console, eventBus, constant,
                                                                          loginAction) {
                                          @Override
                                          protected void onSuccess(Object result) {
                                              console.print(constant.updateAllBackendsSuccessfully());
                                              init(project);
                                          }
                                      });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    @Override
    public void onRollBackAllBackendsClicked() {
        final String vfsId = resourceProvider.getVfsId();

        try {
            service.rollbackAllBackends(vfsId, project.getId(),
                                        new GAEAsyncRequestCallback<Object>(null, console, eventBus, constant,
                                                                            loginAction) {
                                            @Override
                                            protected void onSuccess(Object result) {
                                                console.print(constant.rollbackAllBackendsSuccessfully());
                                                init(project);
                                            }
                                        });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    @Override
    public void onUpdateBackendState(String backendName, Backend.State backendState) {
        final String vfsId = resourceProvider.getVfsId();

        try {
            service.setBackendState(vfsId, project.getId(), backendName, backendState.toString(),
                                    new GAEAsyncRequestCallback<Object>(null, console, eventBus, constant,
                                                                        loginAction) {
                                        @Override
                                        protected void onSuccess(Object result) {
                                            init(project);
                                        }
                                    });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }
}
