package com.codenvy.ide.ext.gae.client.project.general;

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.gae.client.GAEAsyncRequestCallback;
import com.codenvy.ide.ext.gae.client.GAEClientService;
import com.codenvy.ide.ext.gae.client.GAELocalization;
import com.codenvy.ide.ext.gae.client.actions.LoginAction;
import com.codenvy.ide.ext.gae.client.create.CreateApplicationPresenter;
import com.codenvy.ide.ext.gae.client.project.general.logs.LogsPresenter;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter that allow user to control general state for application in Google App Engine.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
@Singleton
public class GeneralTabPanePresenter implements Presenter, GeneralTabPaneView.ActionDelegate {
    private GeneralTabPaneView         view;
    private CreateApplicationPresenter createApplicationPresenter;
    private EventBus                   eventBus;
    private ConsolePart                console;
    private GAEClientService           service;
    private ResourceProvider           resourceProvider;
    private LoginAction                loginAction;
    private GAELocalization            constant;
    private LogsPresenter              logsPresenter;
    private Project                    project;

    /**
     * Constructor for general control presenter.
     */
    @Inject
    public GeneralTabPanePresenter(GeneralTabPaneView view, CreateApplicationPresenter createApplicationPresenter,
                                   EventBus eventBus, ConsolePart console, GAEClientService service,
                                   ResourceProvider resourceProvider, LoginAction loginAction,
                                   GAELocalization constant, LogsPresenter logsPresenter) {
        this.view = view;
        this.createApplicationPresenter = createApplicationPresenter;
        this.eventBus = eventBus;
        this.console = console;
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.loginAction = loginAction;
        this.constant = constant;
        this.logsPresenter = logsPresenter;

        this.view.setDelegate(this);
    }

    /**
     * Initialize Backend tab presenter.
     *
     * @param project
     *         project that opened in current moment.
     */
    public void init(Project project) {
        this.project = project;
    }

    /** {@inheritDoc} */
    @Override
    public void onUpdateApplicationClicked() {
        createApplicationPresenter.deploy(project);
    }

    /** {@inheritDoc} */
    @Override
    public void onRollBackApplicationClicked() {
        final String vfsId = resourceProvider.getVfsId();

        try {
            service.rollback(vfsId, project.getId(),
                             new GAEAsyncRequestCallback<Object>(null, console, eventBus, constant, loginAction) {
                                 @Override
                                 protected void onSuccess(Object result) {
                                     console.print(constant.rollbackUpdateSuccess());
                                 }
                             });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onGetApplicationLogsClicked() {
        logsPresenter.showDialog(project);
    }

    /** {@inheritDoc} */
    @Override
    public void onUpdateIndexesClicked() {
        final String vfsId = resourceProvider.getVfsId();

        try {
            service.updateIndexes(vfsId, project.getId(),
                                  new GAEAsyncRequestCallback<Object>(null, console, eventBus, constant, loginAction) {
                                      @Override
                                      protected void onSuccess(Object result) {
                                          console.print(constant.updateIndexesSuccessfully());
                                      }
                                  });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onVacuumIndexesClicked() {
        final String vfsId = resourceProvider.getVfsId();

        try {
            service.vacuumIndexes(vfsId, project.getId(),
                                  new GAEAsyncRequestCallback<Object>(null, console, eventBus, constant, loginAction) {
                                      @Override
                                      protected void onSuccess(Object result) {
                                          console.print(constant.vacuumIndexesSuccessfully());
                                      }
                                  });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onUpdatePageSpeedClicked() {
        final String vfsId = resourceProvider.getVfsId();

        try {
            service.updatePagespeed(vfsId, project.getId(),
                                    new GAEAsyncRequestCallback<Object>(null, console, eventBus, constant,
                                                                        loginAction) {
                                        @Override
                                        protected void onSuccess(Object result) {
                                            console.print(constant.updatePageSpeedSuccessfully());
                                        }
                                    });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onUpdateQueuesClicked() {
        final String vfsId = resourceProvider.getVfsId();

        try {
            service.updateQueues(vfsId, project.getId(),
                                 new GAEAsyncRequestCallback<Object>(null, console, eventBus, constant, loginAction) {
                                     @Override
                                     protected void onSuccess(Object result) {
                                         console.print(constant.updateQueuesSuccessfully());
                                     }
                                 });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onUpdateDoSClicked() {
        final String vfsId = resourceProvider.getVfsId();

        try {
            service.updateDos(vfsId, project.getId(),
                              new GAEAsyncRequestCallback<Object>(null, console, eventBus, constant, loginAction) {
                                  @Override
                                  protected void onSuccess(Object result) {
                                      console.print(constant.updateDosSuccessfully());
                                  }
                              });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }
}
