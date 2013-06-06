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
package com.codenvy.ide.ext.java.jdi.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.ext.java.jdi.client.actions.RunAction;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Extension add Java Runtime support to the IDE Application.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
@Extension(title = "Java Runtime Support.", version = "3.0.0")
public class JavaRuntimeExtension {
    /** Channel for the messages containing debugger events. */
    public static final String EVENTS_CHANNEL           = "debugger:events:";
    /** Channel for the messages containing the application names which may be stopped soon. */
    public static final String EXPIRE_SOON_APP_CHANNEL  = "debugger:expireSoonApp:";
    /** Channel for the messages containing message which informs about debugger is disconnected. */
    public static final String DISCONNECT_CHANNEL       = "debugger:disconnected:";
    /** Channel for the messages containing message which informs about application is stopped. */
    public static final String APPLICATION_STOP_CHANNEL = "runner:application-stopped:";

    @Inject
    public JavaRuntimeExtension(ActionManager actionManager, RunAction action) {
        actionManager.registerAction("runJavaProject", action);
        DefaultActionGroup run = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_RUN);
        run.add(action);
    }
}