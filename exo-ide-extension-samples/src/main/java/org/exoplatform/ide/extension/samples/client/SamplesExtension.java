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
package org.exoplatform.ide.extension.samples.client;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.samples.client.control.DocumentationControl;
import org.exoplatform.ide.extension.samples.client.control.FeedbackControl;
import org.exoplatform.ide.extension.samples.client.control.SupportControl;
import org.exoplatform.ide.extension.samples.client.control.WelcomeControl;
import org.exoplatform.ide.extension.samples.client.getstarted.GetStartedControl;
import org.exoplatform.ide.extension.samples.client.getstarted.GetStartedPresenter;
import org.exoplatform.ide.extension.samples.client.inviting.InviteClientService;
import org.exoplatform.ide.extension.samples.client.inviting.github.InviteGitHubDevelopersPresenter;
import org.exoplatform.ide.extension.samples.client.inviting.google.InviteGoogleDevelopersPresenter;
import org.exoplatform.ide.extension.samples.client.inviting.manage.ManageInviteControl;
import org.exoplatform.ide.extension.samples.client.inviting.manage.ManageInvitePresenter;
import org.exoplatform.ide.extension.samples.client.startpage.OpenStartPageEvent;
import org.exoplatform.ide.extension.samples.client.startpage.StartPagePresenter;
import org.exoplatform.ide.git.client.GitAutoBeanFactory;
import org.exoplatform.ide.git.client.github.collaborators.GitHubClientServiceImpl;

/**
 * Samples extension for IDE.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SamplesExtension.java Sep 2, 2011 12:34:36 PM vereshchaka $
 */
public class SamplesExtension extends Extension implements InitializeServicesHandler {

    public static final SamplesLocalizationConstant LOCALIZATION_CONSTANT = GWT.create(SamplesLocalizationConstant.class);

    /** {@inheritDoc} */
    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        new GitHubClientServiceImpl(event.getLoader());
        if (!IDE.currentWorkspace.isTemporary()) {
            IDE.fireEvent(new OpenStartPageEvent());
        }

        new InviteClientService(Utils.getRestContext(), Utils.getWorkspaceName());
    }

    /** {@inheritDoc} */
    @Override
    public void initialize() {
        SamplesClientBundle.INSTANCE.css().ensureInjected();

        IDE.getInstance().addControl(new WelcomeControl());
        IDE.getInstance().addControl(new ManageInviteControl());
        IDE.getInstance().addControl(new DocumentationControl());
        IDE.getInstance().addControl(new SupportControl());
        IDE.getInstance().addControl(new FeedbackControl());
        IDE.getInstance().addControl(new GetStartedControl());

        IDE.addHandler(InitializeServicesEvent.TYPE, this);

        new StartPagePresenter();
        new ManageInvitePresenter();
        new GetStartedPresenter();
        new InviteGoogleDevelopersPresenter();
        new InviteGitHubDevelopersPresenter();
    }

}
