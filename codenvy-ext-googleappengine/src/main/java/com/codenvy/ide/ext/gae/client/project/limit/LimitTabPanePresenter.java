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

package com.codenvy.ide.ext.gae.client.project.limit;

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.gae.client.GAEAsyncRequestCallback;
import com.codenvy.ide.ext.gae.client.GAEClientService;
import com.codenvy.ide.ext.gae.client.GAELocalization;
import com.codenvy.ide.ext.gae.client.marshaller.ResourceLimitsUnmarshaller;
import com.codenvy.ide.ext.gae.shared.ResourceLimit;
import com.codenvy.ide.json.JsonArray;
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
    private LimitTabPaneView    view;
    private GAEClientService    service;
    private EventBus            eventBus;
    private ResourceProvider    resourceProvider;
    private GAELocalization     constant;
    private NotificationManager notificationManager;

    /** Constructor for limits presenter. */
    @Inject
    public LimitTabPanePresenter(LimitTabPaneView view, GAEClientService service, EventBus eventBus, ResourceProvider resourceProvider,
                                 GAELocalization constant, NotificationManager notificationManager) {
        this.view = view;
        this.service = service;
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.notificationManager = notificationManager;

        this.view.setDelegate(this);
    }

    /**
     * Initialize Backend tab presenter.
     *
     * @param project
     *         project that opened in current moment.
     */
    public void init(Project project) {
        final String vfsId = resourceProvider.getVfsInfo().getId();
        ResourceLimitsUnmarshaller unmarshaller = new ResourceLimitsUnmarshaller();

        try {
            service.getResourceLimits(vfsId, project.getId(),
                                      new GAEAsyncRequestCallback<JsonArray<ResourceLimit>>(unmarshaller, eventBus, constant, null,
                                                                                            notificationManager) {
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
