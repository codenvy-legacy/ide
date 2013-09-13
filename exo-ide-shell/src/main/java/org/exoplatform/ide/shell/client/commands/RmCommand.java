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
import org.exoplatform.ide.shell.client.cli.CommandLine;
import org.exoplatform.ide.shell.client.cli.Options;
import org.exoplatform.ide.shell.client.model.ClientCommand;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Aug 11, 2011 evgen $
 */
public class RmCommand extends ClientCommand {
    private static final Set<String> commads = new HashSet<String>();

    static {
        commads.add("rm");
    }

    /**
     *
     */
    public RmCommand() {
        super(commads, new Options(), CloudShell.messages.rmHelp());
    }

    /** @see org.exoplatform.ide.shell.client.model.ClientCommand#execute(org.exoplatform.ide.shell.client.cli.CommandLine) */
    @Override
    public void execute(CommandLine commandLine) {
        if (commandLine.hasOption("h")) {
            printHelp(CloudShell.messages.rmUsage(), CloudShell.messages.rmHeader());
            return;
        }
        @SuppressWarnings("unchecked")
        List<String> args = commandLine.getArgList();
        args.remove(commads.iterator().next());
        if (args.size() == 1) {
            String path = args.get(0);
            final String newPath = Utils.getPath(Environment.get().getCurrentFolder(), path);
            try {
                VirtualFileSystem.getInstance().getItemByPath(newPath,
                                                              new AsyncRequestCallback<ItemWrapper>(
                                                                      new ItemUnmarshaller(new ItemWrapper())) {

                                                                  @Override
                                                                  protected void onSuccess(ItemWrapper result) {
                                                                      deleteItem(result.getItem());
                                                                  }

                                                                  @Override
                                                                  protected void onFailure(Throwable exception) {
                                                                      CloudShell.console().println(exception.getMessage());
                                                                  }
                                                              });
            } catch (RequestException e) {
                CloudShell.console().println(e.getMessage());
            }
        }
    }

    private void deleteItem(Item item) {
        try {
            VirtualFileSystem.getInstance().delete(item, new AsyncRequestCallback<String>() {

                @Override
                protected void onSuccess(String result) {
                    CloudShell.console().printPrompt();
                }

                /**
                 * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
                 */
                @Override
                protected void onFailure(Throwable exception) {
                    CloudShell.console().println(exception.getMessage());
                }
            });
        } catch (RequestException e) {
            CloudShell.console().println(e.getMessage());
        }
    }

}
