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

package com.codenvy.ide.ext.gae.client.project.cron;

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.gae.client.GAEAsyncRequestCallback;
import com.codenvy.ide.ext.gae.client.GAEClientService;
import com.codenvy.ide.ext.gae.client.GAELocalization;
import com.codenvy.ide.ext.gae.client.marshaller.CronListUnmarshaller;
import com.codenvy.ide.ext.gae.shared.CronEntry;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter that allow user to control crons information.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
@Singleton
public class CronTabPanePresenter implements Presenter, CronTabPaneView.ActionDelegate {
    private CronTabPaneView  view;
    private GAEClientService service;
    private EventBus         eventBus;
    private ConsolePart      console;
    private ResourceProvider resourceProvider;
    private GAELocalization  constant;
    private Project          project;

    /**
     * Constructor for crons presenter.
     */
    @Inject
    public CronTabPanePresenter(CronTabPaneView view, GAEClientService service, EventBus eventBus, ConsolePart console,
                                ResourceProvider resourceProvider, GAELocalization constant) {
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
        this.project = project;

        final String vfsId = resourceProvider.getVfsId();
        CronListUnmarshaller unmarshaller = new CronListUnmarshaller(JsonCollections.<CronEntry>createArray());

        try {
            service.cronInfo(vfsId, project.getId(),
                             new GAEAsyncRequestCallback<JsonArray<CronEntry>>(unmarshaller, console, eventBus,
                                                                               constant, null) {
                                 @Override
                                 protected void onSuccess(JsonArray<CronEntry> result) {
                                     view.setCronEntryData(result);
                                 }
                             });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onUpdateButtonClicked() {
        final String vfsId = resourceProvider.getVfsId();

        try {
            service.updateCron(vfsId, project.getId(),
                               new GAEAsyncRequestCallback<Object>(null, console, eventBus, constant, null) {
                                   @Override
                                   protected void onSuccess(Object result) {
                                       init(project);
                                       console.print(constant.updateCronsSuccessfully());
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
