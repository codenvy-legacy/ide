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
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Aug 10, 2011 evgen $
 */
public class LsCommand extends ClientCommand {

    private static final Set<String> commads = new HashSet<String>();

    static {
        commads.add("ls");
    }

    /**
     *
     */
    public LsCommand() {
        super(commads, new Options(), CloudShell.messages.lsHelp());
    }

    /** @see org.exoplatform.ide.shell.client.model.ClientCommand#execute(org.exoplatform.ide.shell.client.cli.CommandLine) */
    @Override
    public void execute(CommandLine commandLine) {
        @SuppressWarnings("unchecked")
        final List<String> args = commandLine.getArgList();
        args.remove(0);

        if (commandLine.hasOption("h")) {
            printHelp(CloudShell.messages.lsUsage(), CloudShell.messages.lsHeader());
            return;
        }
        Folder current = Environment.get().getCurrentFolder();
        if (args.size() == 0) {
            getFolderContent(current);
        } else {

            String path = Utils.getPath(current, args.get(0));
            try {
                VirtualFileSystem.getInstance().getItemByPath(path,
                                                              new AsyncRequestCallback<ItemWrapper>(
                                                                      new ItemUnmarshaller(new ItemWrapper())) {

                                                                  @Override
                                                                  protected void onSuccess(ItemWrapper result) {
                                                                      if (result.getItem() instanceof Folder)
                                                                          getFolderContent((Folder)result.getItem());
                                                                      else
                                                                          CloudShell.console().print(CloudShell.messages.lsError(
                                                                                  result.getItem().getName()));
                                                                  }

                                                                  @Override
                                                                  protected void onFailure(Throwable exception) {
                                                                      CloudShell.console().print(CloudShell.messages.lsError(args.get(0)));
                                                                  }
                                                              });
            } catch (RequestException e) {
                CloudShell.console().print(CloudShell.messages.lsError(args.get(0)));
            }

        }

    }

    private void getFolderContent(final Folder folder) {
        try {
            VirtualFileSystem.getInstance().getChildren(folder,
                                                        new AsyncRequestCallback<List<Item>>(
                                                                new ChildrenUnmarshaller(new ArrayList<Item>())) {

                                                            @Override
                                                            protected void onSuccess(List<Item> result) {
                                                                CloudShell.console().print(Utils.formatItems(result));
                                                            }

                                                            /**
                                                             * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
                                                             */
                                                            @Override
                                                            protected void onFailure(Throwable exception) {
                                                                CloudShell.console().println(CloudShell.messages.lsError(folder.getPath()));
                                                            }
                                                        });
        } catch (RequestException e) {
            CloudShell.console().println(CloudShell.messages.lsError(folder.getPath()));
        }
    }

}
