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
package org.exoplatform.ide.client.project;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.application.IDELoader;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.template.TemplateService;
import org.exoplatform.ide.client.framework.template.TemplateServiceImpl;
import org.exoplatform.ide.client.model.IDEConfigurationLoader;
import org.exoplatform.ide.client.project.create.CreateProjectPresenter;
import org.exoplatform.ide.client.project.create.MavenModuleCreationCallback;
import org.exoplatform.ide.client.project.create.NewProjectMenuGroup;
import org.exoplatform.ide.client.project.explorer.ProjectExplorerPresenter;
import org.exoplatform.ide.client.project.explorer.ShowProjectExplorerControl;
import org.exoplatform.ide.client.project.list.ShowProjectsPresenter;
import org.exoplatform.ide.client.project.properties.ProjectPropertiesPresenter;
import org.exoplatform.ide.client.project.resource.OpenResourcePresenter;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ProjectSupportingModule implements ConfigurationReceivedSuccessfullyHandler{

    

    public ProjectSupportingModule() {
        IDE.getInstance().addControl(new NewProjectMenuGroup());
        IDE.getInstance().addControl(new ProjectPaaSControl());

        new CreateProjectPresenter();
        new MavenModuleCreationCallback();
        new ShowProjectsPresenter();

        new ProjectExplorerPresenter();

        IDE.getInstance().addControl(new ShowProjectExplorerControl());
        IDE.getInstance().addControl(new CloseProjectControl());

        new ProjectPropertiesPresenter();

        new ProjectCreatedEventHandler();

        new OpenResourcePresenter();

        IDE.getInstance().addControlsFormatter(new ProjectMenuItemFormatter());

        IDE.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
    

        new ProjectProcessor();
    }

    /** @see org.exoplatform.ide.client.framework.configuration.event
     * .ConfigurationReceivedSuccessfullyHandler#onConfigurationReceivedSuccessfully(org.exoplatform.ide.client.framework.configuration
     * .event.ConfigurationReceivedSuccessfullyEvent) */
    public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event) {
        if (TemplateService.getInstance() == null) {
            new TemplateServiceImpl(IDELoader.get());
        }
    }



   
}
