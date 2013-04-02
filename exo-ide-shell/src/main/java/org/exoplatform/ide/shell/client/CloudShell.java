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

package org.exoplatform.ide.shell.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.shell.shared.CLIResource;

import java.util.HashSet;
import java.util.Set;

/** Entry point classes define <code>onModuleLoad()</code>. */
public class CloudShell implements EntryPoint {
    public static final ShellAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(ShellAutoBeanFactory.class);

    public static final Messages messages = GWT.create(Messages.class);

    static ConsoleWriter consoleWriter;

    private static Set<CLIResource> commands = new HashSet<CLIResource>();

    public static final HandlerManager EVENT_BUS = new HandlerManager(null);

    public void onModuleLoad() {
        new ShellInitializer().init();
    }

    /** @return {@link ConsoleWriter} */
    public static ConsoleWriter console() {
        return consoleWriter;
    }

    public static Set<CLIResource> getCommands() {
        return commands;
    }

}
