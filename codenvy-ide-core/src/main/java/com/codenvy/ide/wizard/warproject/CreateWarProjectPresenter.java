/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.wizard.warproject;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.wizard.newproject.AbstractCreateProjectPresenter;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.resources.marshal.ProjectUnmarshaller;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.template.TemplateService;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;


/**
 * Creates war project.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class CreateWarProjectPresenter extends AbstractCreateProjectPresenter {
    private ResourceProvider resourceProvider;

    private EventBus eventBus;

    private ConsolePart console;

    /**
     * Create new war project presenter.
     *
     * @param resourceProvider
     * @param eventBus
     * @param console
     */
    @Inject
    public CreateWarProjectPresenter(ResourceProvider resourceProvider, EventBus eventBus, ConsolePart console) {
        this.resourceProvider = resourceProvider;
        this.eventBus = eventBus;
        this.console = console;
    }

    /** {@inheritDoc} */
    @Override
    public void create(final AsyncCallback<Project> callback) {
        String projectName = getProjectName();

        try {
            TemplateService.getInstance().createProjectFromTemplate(resourceProvider.getVfsId(),
                                                                    resourceProvider.getRootId(), projectName, "jsp",
                                                                    new AsyncRequestCallback<Project>(
                                                                            new ProjectUnmarshaller(new Project(eventBus))) {
                                                                        @Override
                                                                        protected void onSuccess(Project result) {
                                                                            resourceProvider.getProject(result.getName(),
                                                                                                        new AsyncCallback<Project>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(
                                                                                                                    Project result) {
                                                                                                                callback.onSuccess(result);
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onFailure(
                                                                                                                    Throwable caught) {
                                                                                                                Log.error(









                                                                                                                        CreateWarProjectPresenter.class,
                                                                                                                        caught);
                                                                                                            }
                                                                                                        });
                                                                        }

                                                                        @Override
                                                                        protected void onFailure(Throwable exception) {
                                                                            Log.error(CreateWarProjectPresenter.class, exception);
                                                                        }
                                                                    });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }
}