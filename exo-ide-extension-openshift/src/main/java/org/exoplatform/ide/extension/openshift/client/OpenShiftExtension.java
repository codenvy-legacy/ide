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
package org.exoplatform.ide.extension.openshift.client;

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
import org.exoplatform.ide.extension.openshift.client.cartridge.AddCartridgePresenter;
import org.exoplatform.ide.extension.openshift.client.controls.CreateApplicationControl;
import org.exoplatform.ide.extension.openshift.client.controls.CreateDomainControl;
import org.exoplatform.ide.extension.openshift.client.controls.OpenShiftControlsGroup;
import org.exoplatform.ide.extension.openshift.client.controls.ShowUserInfoControl;
import org.exoplatform.ide.extension.openshift.client.controls.SwitchAccountControl;
import org.exoplatform.ide.extension.openshift.client.controls.UpdatePublicKeyControl;
import org.exoplatform.ide.extension.openshift.client.create.CreateApplicationPresenter;
import org.exoplatform.ide.extension.openshift.client.delete.DeleteApplicationCommandHandler;
import org.exoplatform.ide.extension.openshift.client.deploy.DeployApplicationPresenter;
import org.exoplatform.ide.extension.openshift.client.domain.CreateDomainPresenter;
import org.exoplatform.ide.extension.openshift.client.info.ApplicationInfoPresenter;
import org.exoplatform.ide.extension.openshift.client.key.UpdatePublicKeyCommandHandler;
import org.exoplatform.ide.extension.openshift.client.login.LoginPresenter;
import org.exoplatform.ide.extension.openshift.client.preview.PreviewApplicationPresenter;
import org.exoplatform.ide.extension.openshift.client.project.OpenShiftProjectPresenter;
import org.exoplatform.ide.extension.openshift.client.start.StartApplicationPresenter;
import org.exoplatform.ide.extension.openshift.client.user.ApplicationListPresenter;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.Arrays;
import java.util.List;

/**
 * OpenShift extension to be added to IDE.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 6, 2011 2:21:00 PM anya $
 */
public class OpenShiftExtension extends Extension implements InitializeServicesHandler {
    /** The generator of an {@link AutoBean}. */
    public static final OpenShiftAutoBeanFactory      AUTO_BEAN_FACTORY     = GWT.create(OpenShiftAutoBeanFactory.class);
    /** Localization constants. */
    public static final OpenShiftLocalizationConstant LOCALIZATION_CONSTANT = GWT.create(OpenShiftLocalizationConstant.class);
    /* Useful constants */
    public static final String                        LOGIN                 = "rhlogin";
    public static final String                        PASSWORD              = "password";
    /* Application information */
    public static final String                        NAME                  = "name";
    public static final String                        TYPE                  = "type";
    public static final String                        GIT_URL               = "gitUrl";
    public static final String                        PUBLIC_URL            = "publicUrl";
    public static final String                        CREATION_DATE         = "creationTime";
    /* User info */
    public static final String                        DOMAIN                = "rhcDomain";
    public static final String                        UUID                  = "uuid";
    public static final String                        NAMESPACE             = "namespace";
    public static final String                        APPS                  = "apps";
    public static final String                        ID                    = "OpenShift";

    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        IDE.getInstance().registerPaaS(new PaaS("OpenShift", "OpenShift", new Image(OpenShiftClientBundle.INSTANCE.openShiftControl48()),
                                                new Image(
                                                          OpenShiftClientBundle.INSTANCE.openShiftControl48Disabled()),
                                                Arrays.asList(ProjectType.RUBY_ON_RAILS,
                                                              ProjectType.PHP, ProjectType.JSP, ProjectType.NODE_JS,
                                                              ProjectType.PYTHON, ProjectType.WAR),
                                                true, new DeployApplicationPresenter()));

        IDE.addHandler(InitializeServicesEvent.TYPE, this);

        // Add controls:
        IDE.getInstance().addControl(new OpenShiftControlsGroup());
        IDE.getInstance().addControl(new CreateDomainControl());
        IDE.getInstance().addControl(new CreateApplicationControl());

        IDE.getInstance().addControl(new ShowUserInfoControl());
        IDE.getInstance().addControl(new UpdatePublicKeyControl());
        IDE.getInstance().addControl(new SwitchAccountControl());

        new OpenShiftExceptionsHandler();

        // Create presenters:
        new LoginPresenter();
        new CreateDomainPresenter();
        new CreateApplicationPresenter();
        new DeleteApplicationCommandHandler();
        new ApplicationInfoPresenter();
        new PreviewApplicationPresenter();
        new ApplicationListPresenter();
        new AddCartridgePresenter();
        new UpdatePublicKeyCommandHandler();

        new OpenShiftProjectPresenter();
        new StartApplicationPresenter();
    }

    /**
     * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide
     *      .client.framework.application.event.InitializeServicesEvent)
     */
    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        new OpenShiftClientServiceImpl(event.getLoader(), IDE.messageBus());
    }

    public static boolean canBeDeployedToOpenShift(ProjectModel project) {
        List<String> targets = project.getPropertyValues(ProjectProperties.TARGET.value());
        return (targets != null && targets.contains(ID));
    }
}
