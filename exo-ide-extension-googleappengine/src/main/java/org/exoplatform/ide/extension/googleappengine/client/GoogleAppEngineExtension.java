/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.googleappengine.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.paas.PaaS;
import org.exoplatform.ide.client.framework.project.ProjectProperties;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.googleappengine.client.backends.BackendsHandler;
import org.exoplatform.ide.extension.googleappengine.client.create.CreateApplicationControl;
import org.exoplatform.ide.extension.googleappengine.client.create.CreateApplicationPresenter;
import org.exoplatform.ide.extension.googleappengine.client.cron.CronsHandler;
import org.exoplatform.ide.extension.googleappengine.client.deploy.DeployApplicationPresenter;
import org.exoplatform.ide.extension.googleappengine.client.dos.DosHandler;
import org.exoplatform.ide.extension.googleappengine.client.indexes.UpdateIndexesHandlerImpl;
import org.exoplatform.ide.extension.googleappengine.client.indexes.VaccumIndexesHandlerImpl;
import org.exoplatform.ide.extension.googleappengine.client.login.AccountsHandler;
import org.exoplatform.ide.extension.googleappengine.client.login.LoginControl;
import org.exoplatform.ide.extension.googleappengine.client.login.LoginPresenter;
import org.exoplatform.ide.extension.googleappengine.client.login.LogoutControl;
import org.exoplatform.ide.extension.googleappengine.client.logs.LogsPresenter;
import org.exoplatform.ide.extension.googleappengine.client.pagespeed.PageSpeedHandler;
import org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter;
import org.exoplatform.ide.extension.googleappengine.client.queues.QueuesHandler;
import org.exoplatform.ide.extension.googleappengine.client.rollback.RollbackUpdatePresenter;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 15, 2012 5:13:13 PM anya $
 */
public class GoogleAppEngineExtension extends Extension implements InitializeServicesHandler {

    public static final GoogleAppEngineAutoBeanFactory AUTO_BEAN_FACTORY = GWT
            .create(GoogleAppEngineAutoBeanFactory.class);

    public static final GAELocalization GAE_LOCALIZATION = GWT.create(GAELocalization.class);

    public static final String ID = "GAE";
    
    

    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        IDE.getInstance().registerPaaS(
                new PaaS(ID, "Google App Engine", new Image(GAEClientBundle.INSTANCE.googleAppEngine48()), new Image(
                        GAEClientBundle.INSTANCE.googleAppEngine48Disabled()), Arrays.asList(ProjectType.JAVA, ProjectType.PYTHON,
                                                                                             ProjectType.DJANGO, ProjectType.JSP,
                                                                                             ProjectType.WAR, ProjectType.PHP),
                         new DeployApplicationPresenter()));

        IDE.addHandler(InitializeServicesEvent.TYPE, this);

        IDE.getInstance().addControl(new GoogleAppEngineControl());
        IDE.getInstance().addControl(new CreateApplicationControl());

        // new DeployApplicationPresenter();
        new AppEngineProjectPresenter();
        new LoginPresenter();
        new RollbackUpdatePresenter();

        new LogsPresenter();
        new PageSpeedHandler();
        new UpdateIndexesHandlerImpl();
        new VaccumIndexesHandlerImpl();
        new QueuesHandler();
        new DosHandler();
        new CronsHandler();
        new BackendsHandler();
        IDE.getInstance().addControl(new LoginControl());
        IDE.getInstance().addControl(new LogoutControl());
    }

    /** @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide
     * .client.framework.application.event.InitializeServicesEvent) */
    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        new GoogleAppEngineClientServiceImpl(Utils.getRestContext(), Utils.getWorkspaceName(), event.getLoader(), IDE.messageBus());
        new CreateApplicationPresenter();
        new AccountsHandler();
    }

    public static boolean isAppEngineProject(ProjectModel project) {
        List<String> targets = project.getPropertyValues(ProjectProperties.TARGET.value());

        return ProjectResolver.APP_ENGINE_JAVA.equals(project.getProjectType())
               || ProjectResolver.APP_ENGINE_PYTHON.equals(project.getProjectType())
               || (targets != null && targets.contains(ID));
    }
}
