/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.command;

import com.codenvy.ide.api.expressions.Expression;
import com.codenvy.ide.api.ui.menu.ExtendedCommand;
import com.codenvy.ide.openproject.OpenProjectPresenter;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;


/**
 * Command that handles the process of project opening. It shows dialog with all the project available and allows user to open one.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class OpenProjectCommand implements ExtendedCommand {
    private final OpenProjectPresenter presenter;

    /** Instantiates command */
    @Inject
    public OpenProjectCommand(OpenProjectPresenter presenter) {
        this.presenter = presenter;
    }

    /** {@inheritDoc} */
    @Override
    public void execute() {
        presenter.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getIcon() {
        return null;
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

    /** {@inheritDoc} */
    @Override
    public String getToolTip() {
        return "Open project";
    }
}