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
package com.codenvy.ide.ext.openshift.client.command;

import com.codenvy.ide.api.expressions.Expression;
import com.codenvy.ide.api.ui.menu.ExtendedCommand;
import com.codenvy.ide.ext.openshift.client.OpenShiftResources;
import com.codenvy.ide.ext.openshift.client.domain.CreateDomainPresenter;
import com.codenvy.ide.ext.openshift.client.login.LoggedInHandler;
import com.codenvy.ide.ext.openshift.client.login.LoginCanceledHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class ChangeDomainCommand implements ExtendedCommand {
    private final CreateDomainPresenter presenter;
    private final OpenShiftResources    resources;

    @Inject
    public ChangeDomainCommand(CreateDomainPresenter presenter, OpenShiftResources resources) {
        this.presenter = presenter;
        this.resources = resources;
    }

    @Override
    public void execute() {
        presenter.showDialog();
    }

    @Override
    public ImageResource getIcon() {
        return null;
    }

    @Override
    public String getToolTip() {
        return "Change domain name on openshift.com";
    }

    @Override
    public Expression inContext() {
        return null;
    }

    @Override
    public Expression canExecute() {
        return null;
    }
}
