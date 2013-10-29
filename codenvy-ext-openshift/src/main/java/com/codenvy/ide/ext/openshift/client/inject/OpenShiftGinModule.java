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
package com.codenvy.ide.ext.openshift.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.ext.openshift.client.OpenShiftClientService;
import com.codenvy.ide.ext.openshift.client.OpenShiftClientServiceImpl;
import com.codenvy.ide.ext.openshift.client.cartridge.CreateCartridgeView;
import com.codenvy.ide.ext.openshift.client.cartridge.CreateCartridgeViewImpl;
import com.codenvy.ide.ext.openshift.client.domain.CreateDomainView;
import com.codenvy.ide.ext.openshift.client.domain.CreateDomainViewImpl;
import com.codenvy.ide.ext.openshift.client.info.ApplicationInfoView;
import com.codenvy.ide.ext.openshift.client.info.ApplicationInfoViewImpl;
import com.codenvy.ide.ext.openshift.client.list.ApplicationListView;
import com.codenvy.ide.ext.openshift.client.list.ApplicationListViewImpl;
import com.codenvy.ide.ext.openshift.client.login.LoginView;
import com.codenvy.ide.ext.openshift.client.login.LoginViewImpl;
import com.codenvy.ide.ext.openshift.client.project.ProjectView;
import com.codenvy.ide.ext.openshift.client.project.ProjectViewImpl;
import com.codenvy.ide.ext.openshift.client.wizard.OpenShiftPageView;
import com.codenvy.ide.ext.openshift.client.wizard.OpenShiftPageViewImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/** @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a> */
@ExtensionGinModule
public class OpenShiftGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(OpenShiftClientService.class).to(OpenShiftClientServiceImpl.class).in(Singleton.class);

        bind(LoginView.class).to(LoginViewImpl.class).in(Singleton.class);
        bind(CreateCartridgeView.class).to(CreateCartridgeViewImpl.class).in(Singleton.class);
        bind(CreateDomainView.class).to(CreateDomainViewImpl.class).in(Singleton.class);
        bind(ApplicationInfoView.class).to(ApplicationInfoViewImpl.class).in(Singleton.class);
        bind(ApplicationListView.class).to(ApplicationListViewImpl.class).in(Singleton.class);
        bind(ProjectView.class).to(ProjectViewImpl.class).in(Singleton.class);
        bind(OpenShiftPageView.class).to(OpenShiftPageViewImpl.class).in(Singleton.class);
    }
}
