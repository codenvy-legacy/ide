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
package org.exoplatform.ide.shell.client.commands;

import org.exoplatform.ide.shell.client.CloudShell;
import org.exoplatform.ide.shell.client.cli.CommandLine;
import org.exoplatform.ide.shell.client.cli.Options;
import org.exoplatform.ide.shell.client.model.ClientCommand;
import org.exoplatform.ide.shell.shared.CLIResource;

import java.util.*;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Aug 10, 2011 evgen $
 */
public class HelpCommand extends ClientCommand {

    private static final Set<String> commands = new HashSet<String>();

    static {
        commands.add("help");
    }

    /**
     *
     */
    public HelpCommand() {
        super(commands, new Options(), CloudShell.messages.helpHelp());
    }

    /** @see org.exoplatform.ide.shell.client.model.ClientCommand#execute(org.exoplatform.ide.shell.client.cli.CommandLine) */
    @Override
    public void execute(CommandLine commandLine) {
        Map<String, String> commands = new TreeMap<String, String>();
        int max = 0;
        String tab = "  ";
        for (CLIResource res : CloudShell.getCommands()) {
            for (String s : res.getCommand()) {
                commands.put(s, res.getDescription() == null ? "" : res.getDescription());
                if (s.length() > max)
                    max = s.length();
            }
        }
        StringBuilder help = new StringBuilder();
        for (String name : commands.keySet()) {
            char chars[] = new char[tab.length() + max - name.length()];
            Arrays.fill(chars, (char)' ');
            String s = new String(chars);
            help.append(tab);
            help.append(name);
            help.append(s);
            help.append(commands.get(name));
            help.append("\n");
        }
        CloudShell.console().print(help.toString());
    }

}
