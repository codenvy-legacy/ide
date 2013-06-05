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
package com.codenvy.ide.ext.java.jdi.client.command;

import com.codenvy.ide.api.expressions.Expression;
import com.codenvy.ide.api.expressions.ProjectOpenedExpression;
import com.codenvy.ide.api.ui.menu.ExtendedCommand;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeResources;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerPresenter;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Command for "Run/Debug Application" action.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class DebugCommand implements ExtendedCommand {
    private final DebuggerPresenter       presenter;
    private final JavaRuntimeResources    resources;
    private final ProjectOpenedExpression expression;

    /**
     * Create command.
     *
     * @param presenter
     * @param resources
     * @param expression
     */
    @Inject
    public DebugCommand(DebuggerPresenter presenter, JavaRuntimeResources resources, ProjectOpenedExpression expression) {
        this.presenter = presenter;
        this.resources = resources;
        this.expression = expression;
    }

    /** {@inheritDoc} */
    @Override
    public void execute() {
        presenter.debugApplication();
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getIcon() {
        return resources.debugApp();
    }

    /** {@inheritDoc} */
    @Override
    public String getToolTip() {
        return "Runs application";
    }

    /** {@inheritDoc} */
    @Override
    public Expression inContext() {
        return expression;
    }

    /** {@inheritDoc} */
    @Override
    public Expression canExecute() {
        return null;
    }
}