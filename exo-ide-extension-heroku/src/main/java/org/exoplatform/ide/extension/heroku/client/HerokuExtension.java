/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.heroku.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.paas.PaaS;
import org.exoplatform.ide.client.framework.project.ProjectProperties;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.extension.heroku.client.apps.ManageApplicationsPresenter;
import org.exoplatform.ide.extension.heroku.client.control.AddKeyControl;
import org.exoplatform.ide.extension.heroku.client.control.CreateApplicationControl;
import org.exoplatform.ide.extension.heroku.client.control.HerokuControlsGroup;
import org.exoplatform.ide.extension.heroku.client.control.SwitchAccountControl;
import org.exoplatform.ide.extension.heroku.client.create.CreateApplicationPresenter;
import org.exoplatform.ide.extension.heroku.client.delete.DeleteApplicationCommandHandler;
import org.exoplatform.ide.extension.heroku.client.deploy.DeployApplicationPresenter;
import org.exoplatform.ide.extension.heroku.client.imports.ImportApplicationPresenter;
import org.exoplatform.ide.extension.heroku.client.info.ApplicationInfoPresenter;
import org.exoplatform.ide.extension.heroku.client.key.KeysPresenter;
import org.exoplatform.ide.extension.heroku.client.login.LoginPresenter;
import org.exoplatform.ide.extension.heroku.client.logs.LogsPresenter;
import org.exoplatform.ide.extension.heroku.client.project.HerokuProjectPresenter;
import org.exoplatform.ide.extension.heroku.client.rake.RakeCommandPresenter;
import org.exoplatform.ide.extension.heroku.client.rename.RenameApplicationPresenter;
import org.exoplatform.ide.extension.heroku.client.stack.ChangeStackPresenter;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.Arrays;
import java.util.List;

/**
 * Heroku extension to be added to IDE Application.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 25, 2011 11:38:06 AM anya $
 */
public class HerokuExtension extends Extension implements InitializeServicesHandler {

    /** The generator for an {@link AutoBean} */
    public static final HerokuAutoBeanFactory      AUTO_BEAN_FACTORY     = GWT.create(HerokuAutoBeanFactory.class);

    public static final HerokuLocalizationConstant LOCALIZATION_CONSTANT = GWT.create(HerokuLocalizationConstant.class);

    public static final String                     ID                    = "Heroku";

    /**
     * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide
     *      .client.framework.application.event.InitializeServicesEvent)
     */
    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        new HerokuClientServiceImpl(event.getLoader(), IDE.messageBus());
    }

    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        IDE.getInstance().registerPaaS(
                                       new PaaS("Heroku", "Heroku", new Image(HerokuClientBundle.INSTANCE.heroku48()),
                                                new Image(
                                                          HerokuClientBundle.INSTANCE.heroku48Disabled()),
                                                Arrays.asList(ProjectType.RUBY_ON_RAILS),
                                                new DeployApplicationPresenter()));

        IDE.addHandler(InitializeServicesEvent.TYPE, this);

        // Add controls
        IDE.getInstance().addControl(new HerokuControlsGroup());
        IDE.getInstance().addControl(new CreateApplicationControl());

        // Add presenters
        new CreateApplicationPresenter();
        new ManageApplicationsPresenter();
        new DeleteApplicationCommandHandler();
        new ApplicationInfoPresenter();
        new RenameApplicationPresenter();
        new LoginPresenter();
        new KeysPresenter();
        new RakeCommandPresenter();
        new ChangeStackPresenter();
        new LogsPresenter();

        new HerokuProjectPresenter();
        new ImportApplicationPresenter();

        IDE.getInstance().addControl(new AddKeyControl());
        IDE.getInstance().addControl(new SwitchAccountControl());
    }

    public static boolean canBeDeployedToHeroku(ProjectModel project) {
        List<String> targets = project.getPropertyValues(ProjectProperties.TARGET.value());
        return (targets != null && targets.contains(ID));
    }
}
