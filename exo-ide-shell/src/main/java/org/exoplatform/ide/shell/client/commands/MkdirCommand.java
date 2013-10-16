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
import org.exoplatform.ide.vfs.client.marshal.FolderUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.shared.Folder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Aug 10, 2011 evgen $
 */
public class MkdirCommand extends ClientCommand {

    private static final Set<String> commads = new HashSet<String>();

    static {
        commads.add("mkdir");
    }

    /**
     *
     */
    public MkdirCommand() {
        super(commads, new Options(), CloudShell.messages.mkdirHelp());
    }

    /** @see org.exoplatform.ide.shell.client.model.ClientCommand#execute(org.exoplatform.ide.shell.client.cli.CommandLine) */
    @Override
    public void execute(CommandLine commandLine) {
        if (commandLine.hasOption("h")) {
            printHelp(CloudShell.messages.mkdirUsage(), CloudShell.messages.mkdirHeader());
            return;
        }
        @SuppressWarnings("unchecked")
        final List<String> args = commandLine.getArgList();
        args.remove(0);
        if (args.isEmpty()) {
            CloudShell.console().println(CloudShell.messages.mkdirError());
            return;
        }
        final Folder parentFolder = Environment.get().getCurrentFolder();
        
        if (parentFolder.getLinks().isEmpty()){
            try {
                VirtualFileSystem.getInstance()
                                 .getItemById(parentFolder.getId(),
                                              new AsyncRequestCallback<ItemWrapper>(
                                                                                    new ItemUnmarshaller(
                                                                                                         new ItemWrapper(parentFolder))) {

                                                  @Override
                                                  protected void onSuccess(ItemWrapper result) {
                                                      parentFolder.setLinks(result.getItem().getLinks());
                                                      performCreation(args, parentFolder);
                                                  }

                                                  @Override
                                                  protected void onFailure(Throwable exception) {
                                                      CloudShell.console().println(exception.getMessage());
                                                  }
                                              });
            } catch (RequestException e) {
                CloudShell.console().println(e.getMessage());
            }
        } else {
            performCreation(args, parentFolder);
        }
        
        
       

    }
    
    private void performCreation(List<String> args, Folder parentFolder){
        for (String name : args) {

            FolderModel newFolder = new FolderModel();
            newFolder.setName(name);
            try {
                VirtualFileSystem.getInstance().createFolder(parentFolder,
                                                             new AsyncRequestCallback<FolderModel>(new FolderUnmarshaller(newFolder)) {

                                                                 @Override
                                                                 protected void onSuccess(FolderModel result) {
                                                                     CloudShell.console().println(result.getName());
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
}
