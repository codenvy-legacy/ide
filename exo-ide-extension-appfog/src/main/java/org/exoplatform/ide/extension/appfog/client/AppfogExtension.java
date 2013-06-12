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
package org.exoplatform.ide.extension.appfog.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.paas.PaaS;
import org.exoplatform.ide.client.framework.project.ProjectProperties;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.appfog.client.apps.ApplicationsPresenter;
import org.exoplatform.ide.extension.appfog.client.control.AppfogControlGroup;
import org.exoplatform.ide.extension.appfog.client.control.ApplicationsControl;
import org.exoplatform.ide.extension.appfog.client.control.CreateApplicationControl;
import org.exoplatform.ide.extension.appfog.client.control.SwitchAccountControl;
import org.exoplatform.ide.extension.appfog.client.create.CreateApplicationPresenter;
import org.exoplatform.ide.extension.appfog.client.delete.DeleteApplicationPresenter;
import org.exoplatform.ide.extension.appfog.client.deploy.DeployApplicationPresenter;
import org.exoplatform.ide.extension.appfog.client.info.ApplicationInfoPresenter;
import org.exoplatform.ide.extension.appfog.client.login.LoginPresenter;
import org.exoplatform.ide.extension.appfog.client.project.AppfogProjectPresenter;
import org.exoplatform.ide.extension.appfog.client.services.CreateServicePresenter;
import org.exoplatform.ide.extension.appfog.client.services.ManageServicesPresenter;
import org.exoplatform.ide.extension.appfog.client.start.StartApplicationPresenter;
import org.exoplatform.ide.extension.appfog.client.update.UpdateApplicationPresenter;
import org.exoplatform.ide.extension.appfog.client.update.UpdatePropertiesPresenter;
import org.exoplatform.ide.extension.appfog.client.url.UnmapUrlPresenter;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyImpl;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class AppfogExtension extends Extension implements InitializeServicesHandler {
    public static final AppfogAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(AppfogAutoBeanFactory.class);

    public static final AppfogLocalizationConstant LOCALIZATION_CONSTANT = GWT.create(AppfogLocalizationConstant.class);

    public static final String DEFAULT_SERVER = "https://api.appfog.com";

    private static final String ID = "AppFog";

    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        new AppfogClientService(Utils.getRestContext(), Utils.getWorkspaceName(), event.getLoader(), IDE.messageBus());
    }

    @Override
    public void initialize() {
        IDE.getInstance().registerPaaS(
                new PaaS("AppFog", "AppFog", new Image(AppfogClientBundle.INSTANCE.appfog48()), new Image(
                        AppfogClientBundle.INSTANCE.appfog48Disabled()), Arrays.asList(ProjectType.JSP, ProjectType.RUBY_ON_RAILS,
                                                                                       ProjectType.SPRING, ProjectType.PYTHON,
                                                                                       ProjectType.PHP, ProjectType.WAR),
                         new DeployApplicationPresenter()));
        IDE.addHandler(InitializeServicesEvent.TYPE, this);

        IDE.getInstance().addControl(new AppfogControlGroup());
        IDE.getInstance().addControl(new CreateApplicationControl());

        IDE.getInstance().addControl(new ApplicationsControl());
        IDE.getInstance().addControl(new SwitchAccountControl());

        new CreateApplicationPresenter();
        new LoginPresenter();
        new StartApplicationPresenter();
        new ApplicationInfoPresenter();
        new UpdateApplicationPresenter();
        new DeleteApplicationPresenter();
        new UnmapUrlPresenter();
        new UpdatePropertiesPresenter();
        new ApplicationsPresenter();
        new AppfogProjectPresenter();
        new ManageServicesPresenter();
        new CreateServicePresenter();
    }

    public static boolean canBeDeployedToAF(ProjectModel project) {
        List<String> targets = project.getPropertyValues(ProjectProperties.TARGET.value());
        return (targets != null && targets.contains(ID));
    }

    public static void writeProperty(ProjectModel project, String propertyName, String propertyValue) {
        Property p = new PropertyImpl(propertyName, propertyValue);

        project.getProperties().add(p);

        ItemWrapper item = new ItemWrapper(project);
        ItemUnmarshaller unmarshaller = new ItemUnmarshaller(item);
        try {
            VirtualFileSystem.getInstance().updateItem(project, null, new AsyncRequestCallback<ItemWrapper>(unmarshaller) {

                @Override
                protected void onSuccess(ItemWrapper result) {
                    //nothing to do, only write property to project and it's all.
                }

                @Override
                protected void onFailure(Throwable e) {
                    //IDE.fireEvent(new ExceptionThrownEvent(e));
                }
            });
        } catch (RequestException e) {
            //IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}
