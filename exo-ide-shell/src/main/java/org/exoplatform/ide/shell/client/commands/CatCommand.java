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
import org.exoplatform.ide.vfs.client.marshal.FileContentUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Aug 11, 2011 evgen $
 */
public class CatCommand extends ClientCommand {

    private static final Set<String> commands = new HashSet<String>();

    static {
        commands.add("cat");
    }

    private List<String> files;

    private StringBuilder out = new StringBuilder();

    /**
     *
     */
    public CatCommand() {
        super(commands, new Options(), CloudShell.messages.catHelp());
    }

    /** @see org.exoplatform.ide.shell.client.model.ClientCommand#execute(org.exoplatform.ide.shell.client.cli.CommandLine) */
    @Override
    public void execute(CommandLine commandLine) {
        @SuppressWarnings("unchecked")
        List<String> args = commandLine.getArgList();
        args.remove(0);
        if (commandLine.hasOption("h")) {
            printHelp(CloudShell.messages.catUsage(commands.iterator().next()));
            return;
        }

        if (args.isEmpty()) {
            printHelp(CloudShell.messages.catUsage(commands.iterator().next()));
        } else {
            files = args;
            out.setLength(0);
            getNextContent();
        }
    }

    private void getNextContent() {
        Folder workDir = Environment.get().getCurrentFolder();
        if (files.size() != 0) {

            String newPath = Utils.getPath(workDir, files.get(0));
            try {
                VirtualFileSystem.getInstance().getItemByPath(newPath,
                                                              new AsyncRequestCallback<ItemWrapper>(
                                                                      new ItemUnmarshaller(new ItemWrapper())) {

                                                                  @Override
                                                                  protected void onSuccess(ItemWrapper result) {
                                                                      try {
                                                                          Item i = result.getItem();
                                                                          if (i instanceof FileModel) {
                                                                              VirtualFileSystem.getInstance().getContent(
                                                                                      new AsyncRequestCallback<FileModel>(
                                                                                              new FileContentUnmarshaller((FileModel)i)) {

                                                                                          @Override
                                                                                          protected void onSuccess(FileModel result) {
                                                                                              String content =
                                                                                                      Utils.htmlEncode(result.getContent());
                                                                                              out.append(content);
                                                                                              out.append("\n");
                                                                                              files.remove(0);
                                                                                              getNextContent();
                                                                                          }

                                                                                          @Override
                                                                                          protected void onFailure(Throwable exception) {
                                                                                              CloudShell.console().println(
                                                                                                      CloudShell.messages
                                                                                                                .catGetFileContentError());
                                                                                          }
                                                                                      });
                                                                          } else {
                                                                              CloudShell.console().println(
                                                                                      CloudShell.messages.catFolderError(i.getName()));
                                                                          }
                                                                      } catch (RequestException e) {
                                                                          CloudShell.console()
                                                                                    .println(CloudShell.messages.catGetFileContentError());
                                                                      }
                                                                  }

                                                                  @Override
                                                                  protected void onFailure(Throwable exception) {
                                                                      CloudShell.console()
                                                                                .println(CloudShell.messages.catGetFileContentError());
                                                                  }
                                                              });
            } catch (RequestException e) {
                CloudShell.console().println(CloudShell.messages.catFileNotFound(files.get(0)));
            }
        } else {
            CloudShell.console().print(out.toString());
        }

    }

}
