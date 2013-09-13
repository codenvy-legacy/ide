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

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.shell.client.CloudShell;
import org.exoplatform.ide.shell.client.Environment;
import org.exoplatform.ide.shell.client.EnvironmentVariables;
import org.exoplatform.ide.shell.client.cli.CommandLine;
import org.exoplatform.ide.shell.client.cli.Options;
import org.exoplatform.ide.shell.client.model.ClientCommand;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.shared.Folder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Aug 10, 2011 evgen $
 */
public class CdCommand extends ClientCommand {

    private static final Set<String> commands = new HashSet<String>();

    static {
        commands.add("cd");
    }

    /**
     *
     */
    public CdCommand() {
        super(commands, new Options(), CloudShell.messages.cdHelp());
    }

    /** @see org.exoplatform.ide.shell.client.model.ClientCommand#execute(org.exoplatform.ide.shell.client.cli.CommandLine) */
    @Override
    public void execute(CommandLine commandLine) {
        @SuppressWarnings("unchecked")
        List<String> args = commandLine.getArgList();
        args.remove(0);

        if (commandLine.hasOption('h')) {
            printHelp(CloudShell.messages.cdUsage());
            return;
        }

        if (args.isEmpty()) {
            CloudShell.console().printPrompt();
        } else if (args.size() == 1) {
            String path = args.get(0);
            if (".".equals(path)) {
                CloudShell.console().printPrompt();
                return;
            }
            final String newPath = Utils.getPath(Environment.get().getCurrentFolder(), path);
            try {
                VirtualFileSystem.getInstance().getItemByPath(newPath,
                                                              new AsyncRequestCallback<ItemWrapper>(
                                                                      new ItemUnmarshaller(new ItemWrapper())) {

                                                                  @Override
                                                                  protected void onSuccess(ItemWrapper result) {
                                                                      if (result.getItem() instanceof Folder) {
                                                                          Environment.get().setCurrentFolder((Folder)result.getItem());
                                                                          Environment.get()
                                                                                     .saveValue(EnvironmentVariables.CURRENT_FOLDER_ID,
                                                                                                result.getItem().getId());
                                                                          CloudShell.console().printPrompt();
                                                                      } else
                                                                          CloudShell.console().println(CloudShell.messages.cdErrorFolder(
                                                                                  result.getItem().getName()));
                                                                  }

                                                                  @Override
                                                                  protected void onFailure(Throwable exception) {
                                                                      CloudShell.console()
                                                                                .println(CloudShell.messages.cdErrorFolder(newPath));
                                                                  }
                                                              });
            } catch (RequestException e) {
                CloudShell.console().println(CloudShell.messages.cdErrorFolder(newPath));
            }
        } else {
            CloudShell.console().println(CloudShell.messages.cdError());
        }
    }

}
