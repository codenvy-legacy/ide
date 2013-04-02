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
package com.codenvy.ide.command;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.expressions.Expression;
import com.codenvy.ide.api.ui.menu.ExtendedCommand;
import com.codenvy.ide.perspective.OpenPerspectivePresenter;
import com.codenvy.ide.perspective.WorkspacePresenter;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * Open change perspective dialog.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class ShowOpenPerspectiveDialog implements ExtendedCommand {
    private final WorkspacePresenter workspacePresenter;

    private final Resources resources;

    /**
     * Create command.
     *
     * @param workspacePresenter
     * @param resources
     */
    @Inject
    public ShowOpenPerspectiveDialog(WorkspacePresenter workspacePresenter, Resources resources) {
        this.workspacePresenter = workspacePresenter;
        this.resources = resources;
    }

    /** {@inheritDoc} */
    @Override
    public void execute() {
        OpenPerspectivePresenter presenter = new OpenPerspectivePresenter(workspacePresenter, resources);
        presenter.show();
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getIcon() {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getToolTip() {
        return "Open perspective";
    }

    /** {@inheritDoc} */
    @Override
    public Expression inContext() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Expression canExecute() {
        return null;
    }
}