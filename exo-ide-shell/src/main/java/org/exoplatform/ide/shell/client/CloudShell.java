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
