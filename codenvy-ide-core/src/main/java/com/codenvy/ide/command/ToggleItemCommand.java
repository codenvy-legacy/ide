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
import com.codenvy.ide.api.event.ChangeToggleItemStateEvent;
import com.codenvy.ide.api.expressions.Expression;
import com.codenvy.ide.api.ui.menu.ToggleCommand;
import com.codenvy.ide.toolbar.ToggleItemExpression;
import com.google.gwt.resources.client.ImageResource;
import com.google.web.bindery.event.shared.EventBus;


/**
 * Command to change toggle item.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class ToggleItemCommand implements ToggleCommand {
    private final Expression inContext;

    private final Expression canExecute;

    private final ToggleItemExpression stateExpression;

    private final EventBus eventBus;

    private final Resources resources;

    /**
     * Create command.
     *
     * @param resources
     * @param eventBus
     * @param inContext
     * @param canExecute
     * @param stateExpression
     */
    public ToggleItemCommand(Resources resources, EventBus eventBus, Expression inContext, Expression canExecute,
                             ToggleItemExpression stateExpression) {
        this.eventBus = eventBus;
        this.inContext = inContext;
        this.canExecute = canExecute;
        this.stateExpression = stateExpression;
        this.resources = resources;
    }

    /** {@inheritDoc} */
    @Override
    public void execute() {
        stateExpression.onStateChanged();
        eventBus.fireEvent(new ChangeToggleItemStateEvent(stateExpression.getId()));
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getIcon() {
        return resources.folderOpen();
    }

    /** {@inheritDoc} */
    @Override
    public Expression inContext() {
        return inContext;
    }

    /** {@inheritDoc} */
    @Override
    public Expression canExecute() {
        return canExecute;
    }

    /** {@inheritDoc} */
    @Override
    public ToggleItemExpression getState() {
        return stateExpression;
    }

    /** {@inheritDoc} */
    @Override
    public String getToolTip() {
        return "ToolTip";
    }
}