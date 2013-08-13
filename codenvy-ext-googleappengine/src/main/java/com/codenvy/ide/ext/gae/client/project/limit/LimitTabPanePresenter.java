package com.codenvy.ide.ext.gae.client.project.limit;

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.gae.client.GAEAsyncRequestCallback;
import com.codenvy.ide.ext.gae.client.GAEClientService;
import com.codenvy.ide.ext.gae.client.GAELocalization;
import com.codenvy.ide.ext.gae.client.marshaller.ResourceLimitsUnmarshaller;
import com.codenvy.ide.ext.gae.shared.ResourceLimit;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter that allow user to view limits for the selected application on Google App Engine.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
@Singleton
public class LimitTabPanePresenter implements Presenter, LimitTabPaneView.ActionDelegate {
    private LimitTabPaneView view;
    private GAEClientService service;
    private EventBus         eventBus;
    private ConsolePart      console;
    private ResourceProvider resourceProvider;
    private GAELocalization  constant;

    /**
     * Constructor for limits presenter.
     */
    @Inject
    public LimitTabPanePresenter(LimitTabPaneView view, GAEClientService service, EventBus eventBus,
                                 ConsolePart console, ResourceProvider resourceProvider, GAELocalization constant) {
        this.view = view;
        this.service = service;
        this.eventBus = eventBus;
        this.console = console;
        this.resourceProvider = resourceProvider;
        this.constant = constant;

        this.view.setDelegate(this);
    }

    /**
     * Initialize Backend tab presenter.
     *
     * @param project
     *         project that opened in current moment.
     */
    public void init(Project project) {
        final String vfsId = resourceProvider.getVfsId();

        ResourceLimitsUnmarshaller unmarshaller =
                new ResourceLimitsUnmarshaller(JsonCollections.<ResourceLimit>createArray());

        try {
            service.getResourceLimits(vfsId, project.getId(),
                                      new GAEAsyncRequestCallback<JsonArray<ResourceLimit>>(unmarshaller, console,
                                                                                            eventBus, constant, null) {
                                          @Override
                                          protected void onSuccess(JsonArray<ResourceLimit> result) {
                                              view.setResourceLimits(result);
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
