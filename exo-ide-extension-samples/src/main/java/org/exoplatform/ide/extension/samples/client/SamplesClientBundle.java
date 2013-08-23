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
package org.exoplatform.ide.extension.samples.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * Samples client resources (images).
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SamplesClientBundle.java Sep 2, 2011 12:33:49 PM vereshchaka $
 */
public interface SamplesClientBundle extends ClientBundle {
    SamplesClientBundle INSTANCE = GWT.<SamplesClientBundle>create(SamplesClientBundle.class);

    /** Css resources for project wizard. */
    @Source("org/exoplatform/ide/extension/samples/client/wizard.css")
    Style css();

    @Source("org/exoplatform/ide/extension/samples/client/images/help.png")
    ImageResource help();

    @Source("org/exoplatform/ide/extension/samples/client/images/help_disabled.png")
    ImageResource helpDisabled();

    @Source("org/exoplatform/ide/extension/samples/client/images/contact.png")
    ImageResource contact();

    @Source("org/exoplatform/ide/extension/samples/client/images/contact_disabled.png")
    ImageResource contactDisabled();

    @Source("org/exoplatform/ide/extension/samples/client/images/invite/invite-background.png")
    ImageResource invitePageHeaderBackground();

    @Source("org/exoplatform/ide/extension/samples/client/images/invite/invite-background-48.png")
    ImageResource invitePageHeaderBackground48();

    @Source("org/exoplatform/ide/extension/samples/client/images/invite/user-default-photo.png")
    ImageResource userDefaultPhoto();

    /*
     * Buttons
     */
    @Source("org/exoplatform/ide/extension/samples/client/images/github_logo.png")
    ImageResource gitHubLogo();

    @Source("org/exoplatform/ide/extension/samples/client/images/buttons/welcome.png")
    ImageResource welcome();

    @Source("org/exoplatform/ide/extension/samples/client/images/invite/manage.png")
    ImageResource manageInvite();

    @Source("org/exoplatform/ide/extension/samples/client/images/invite/manage_disable.png")
    ImageResource manageInviteDisable();

    @Source("org/exoplatform/ide/extension/samples/client/images/invite/invite.png")
    ImageResource invite();

    @Source("org/exoplatform/ide/extension/samples/client/images/invite/invite_disable.png")
    ImageResource inviteDisable();

    @Source("org/exoplatform/ide/extension/samples/client/images/buttons/welcome-disabled.png")
    ImageResource welcomeDisabled();

    @Source("org/exoplatform/ide/extension/samples/client/images/buttons/github.png")
    ImageResource gitHub();

    @Source("org/exoplatform/ide/extension/samples/client/images/buttons/github_Disabled.png")
    ImageResource gitHubDisabled();

    @Source("org/exoplatform/ide/extension/samples/client/images/buttons/ok.png")
    ImageResource ok();

    @Source("org/exoplatform/ide/extension/samples/client/images/buttons/ok_Disabled.png")
    ImageResource okDisabled();

    @Source("org/exoplatform/ide/extension/samples/client/images/buttons/add.png")
    ImageResource add();

    @Source("org/exoplatform/ide/extension/samples/client/images/buttons/add_Disabled.png")
    ImageResource addDisabled();

    @Source("org/exoplatform/ide/extension/samples/client/images/buttons/cancel.png")
    ImageResource cancel();

    @Source("org/exoplatform/ide/extension/samples/client/images/buttons/cancel_Disabled.png")
    ImageResource cancelDisabled();

    @Source("org/exoplatform/ide/extension/samples/client/images/buttons/next.png")
    ImageResource next();

    @Source("org/exoplatform/ide/extension/samples/client/images/buttons/next_Disabled.png")
    ImageResource nextDisabled();

    @Source("org/exoplatform/ide/extension/samples/client/images/buttons/back.png")
    ImageResource back();

    @Source("org/exoplatform/ide/extension/samples/client/images/buttons/back_Disabled.png")
    ImageResource backDisabled();

    /*
     * Welcome page images
     */
    @Source("org/exoplatform/ide/extension/samples/client/images/welcome/logo.png")
    ImageResource ideLogo();

    @Source("org/exoplatform/ide/extension/samples/client/images/welcome/background.png")
    ImageResource welcomePageBgHeader();

    @Source("org/exoplatform/ide/extension/samples/client/images/welcome/bg-top-container.png")
    ImageResource welcomePageBgTopContainer();

    @Source("org/exoplatform/ide/extension/samples/client/images/welcome/spliter.png")
    ImageResource welcomePageSpliter();

    @Source("org/exoplatform/ide/extension/samples/client/images/welcome/tutorials.png")
    ImageResource welcomeTutorial();

    @Source("org/exoplatform/ide/extension/samples/client/images/welcome/clone-git-repository.png")
    ImageResource welcomeClone();

    @Source("org/exoplatform/ide/extension/samples/client/images/welcome/new-project.png")
    ImageResource welcomeProject();

    @Source("org/exoplatform/ide/extension/samples/client/images/welcome/convert.png")
    ImageResource convertToProject();

    @Source("org/exoplatform/ide/extension/samples/client/images/welcome/documentation.png")
    ImageResource documentation();

    @Source("org/exoplatform/ide/extension/samples/client/images/welcome/import-from-github.png")
    ImageResource importFromGithub();

    @Source("org/exoplatform/ide/extension/samples/client/images/welcome/support.png")
    ImageResource support();

    @Source("org/exoplatform/ide/extension/samples/client/images/welcome/survey.png")
    ImageResource survey();

    @Source("org/exoplatform/ide/extension/samples/client/images/welcome/invitation.png")
    ImageResource invitation();

    /*
     * Controls
     */
    @Source("org/exoplatform/ide/extension/samples/client/images/import-from-github.png")
    ImageResource importFromGithubControl();

    @Source("org/exoplatform/ide/extension/samples/client/images/import-from-github_Disabled.png")
    ImageResource importFromGithubDisabledControl();

    @Source("org/exoplatform/ide/extension/samples/client/images/import-samples.png")
    ImageResource importSamplesControl();

    @Source("org/exoplatform/ide/extension/samples/client/images/import-samples_Disabled.png")
    ImageResource importSamplesDisabledControl();

    /**
     * Technology images
     */
    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/technology/jar36x36.png")
    ImageResource jarTechnology();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/technology/js36x36.png")
    ImageResource jsTechnology();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/technology/jsp36x36.png")
    ImageResource jspTechnology();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/technology/multi-module36x36.png")
    ImageResource multiModuleTechnology();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/technology/php36x36.png")
    ImageResource phpTechnology();
    
    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/technology/nodejs36x36.png")
    ImageResource nodejsTechnology();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/technology/python36x36.png")
    ImageResource pythonTechnology();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/technology/ror36x36.png")
    ImageResource rorTechnology();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/technology/spring36x36.png")
    ImageResource springTechnology();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/technology/android36x36.png")
    ImageResource androidTechnology();

    /**
     * PaaSes image
     */

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/paas/appfog_36.png")
    ImageResource appfogPaaS();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/paas/appfog_36_Disabled.png")
    ImageResource appfogPaaSDisabled();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/paas/cloudbees_36.png")
    ImageResource cloudBeesPaaS();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/paas/cloudbees_36_Disabled.png")
    ImageResource cloudBeesPaaSDisabled();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/paas/cloudfoundry_36.png")
    ImageResource cloudfoundryPaaS();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/paas/cloudfoundry_36_Disabled.png")
    ImageResource cloudfoundryPaaSDisabled();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/paas/Elastic_Beanstalk_36.png")
    ImageResource beansTalkPaaS();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/paas/Elastic_Beanstalk_36_Disabled.png")
    ImageResource beansTalkPaaSDisabled();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/paas/tier3WebFabric_36.png")
    ImageResource tier3WebFabricPaaS();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/paas/tier3WebFabric_36_Disabled.png")
    ImageResource tier3WebFabricPaaSDisabled();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/paas/gae_36.png")
    ImageResource gaePaaS();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/paas/gae_36_Disabled.png")
    ImageResource gaePaaSDisabled();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/paas/heroku_36.png")
    ImageResource herokuPaaS();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/paas/heroku_36_Disabled.png")
    ImageResource herokuPaaSDisabled();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/paas/none-target.png")
    ImageResource nonePaaS();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/paas/openshift_36.png")
    ImageResource openShiftPaaS();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/paas/openshift_36_Disabled.png")
    ImageResource openShiftPaaSDisabled();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/paas/manymo_36.png")
    ImageResource manymoPaaS();

    @Source("org/exoplatform/ide/extension/samples/client/images/wizard/paas/manymo_36_Disabled.png")
    ImageResource mnymoPaaSDisabled();

    public interface Style extends CssResource {
        String inviteHeader();

        String inviteHeader48();

        String table();

        String itemsTree();

        String labelTitle();

        String labelDescription();

        String wizard();

        String topBox();

        String footer();

        String labelValue();

        String labelSubtitle();

        String loginLable();

        String leftFloat();

        String innerBox();

        String right();

        String middle();

        String newFolderDivInput();

        String welcomeHeader();

        String welcomeHeaderLogo();

        String welcomeHeaderText();

        String welcomeTopContainer();
    }

}
