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
package org.exoplatform.ide.extension.jenkins.client;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.jenkins.client.build.BuildApplicationPresenter;

/**
 * IDE Jenkins extension entry point
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class JenkinsExtension extends Extension implements InitializeServicesHandler {

    /** The generator of an {@link AutoBean}. */
    public static final JenkinsAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(JenkinsAutoBeanFactory.class);

    public static final JenkinsMessages MESSAGES = GWT.create(JenkinsMessages.class);

    public static final JenkinsResourceBundle RESOURCES = GWT.create(JenkinsResourceBundle.class);

    /** Channel for the messages containing status of the Jenkins job. */
    public static final String JOB_STATUS_CHANNEL = "jenkins:jobStatus:";

    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        // IDE.getInstance().addControl(new BuildControl(), DockTarget.NONE, false);
        IDE.addHandler(InitializeServicesEvent.TYPE, this);
        new BuildApplicationPresenter();
    }

    /** @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide
     * .client.framework.application.event.InitializeServicesEvent) */
    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        new JenkinsService(event.getLoader());
    }

}
