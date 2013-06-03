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
import com.codenvy.ide.ext.openshift.client.project.ProjectPresenter;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ShowOpenShiftProjectCommand implements ExtendedCommand {
    private final ProjectPresenter                 projectPresenter;
    private final OpenShiftResources               resources;
    private final OpenShiftProjectOpenedExpression expression;

    @Inject
    public ShowOpenShiftProjectCommand(ProjectPresenter projectPresenter, OpenShiftResources resources,
                                       OpenShiftProjectOpenedExpression expression) {
        this.projectPresenter = projectPresenter;
        this.resources = resources;
        this.expression = expression;
    }

    @Override
    public void execute() {
        projectPresenter.showDialog();
    }

    @Override
    public ImageResource getIcon() {
        return resources.openShift();
    }

    @Override
    public String getToolTip() {
        return "Shows OpenShift project properties";
    }

    @Override
    public Expression inContext() {
        return expression;
    }

    @Override
    public Expression canExecute() {
        return null;
    }
}
