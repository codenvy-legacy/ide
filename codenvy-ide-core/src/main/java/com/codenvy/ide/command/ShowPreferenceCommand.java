/*
 * Copyright (C) 2012 eXo Platform SAS.
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
import com.codenvy.ide.preferences.PreferencesAgentImpl;
import com.codenvy.ide.preferences.PreferencesPresenter;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * Command to show preferences dialog.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class ShowPreferenceCommand implements ExtendedCommand {
    private final Resources resources;

    private final PreferencesAgentImpl agent;

    /**
     * Create command.
     *
     * @param resources
     * @param agent
     */
    @Inject
    public ShowPreferenceCommand(Resources resources, PreferencesAgentImpl agent) {
        this.resources = resources;
        this.agent = agent;
    }

    /** {@inheritDoc} */
    @Override
    public void execute() {
        PreferencesPresenter dialog = new PreferencesPresenter(resources, agent);
        dialog.showPreferences();
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getIcon() {
        // TODO icon is incorrect
        return resources.file();
    }

    /** {@inheritDoc} */
    @Override
    public String getToolTip() {
        return "Preferences";
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