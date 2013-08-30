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

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window.Location;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.shell.client.ShellPresenter.Display;
import org.exoplatform.ide.shell.client.commands.CatCommand;
import org.exoplatform.ide.shell.client.commands.CdCommand;
import org.exoplatform.ide.shell.client.commands.ClearCommand;
import org.exoplatform.ide.shell.client.commands.HelpCommand;
import org.exoplatform.ide.shell.client.commands.JobsCommand;
import org.exoplatform.ide.shell.client.commands.KillJobCommand;
import org.exoplatform.ide.shell.client.commands.LsCommand;
import org.exoplatform.ide.shell.client.commands.MkdirCommand;
import org.exoplatform.ide.shell.client.commands.PwdCommand;
import org.exoplatform.ide.shell.client.commands.RmCommand;
import org.exoplatform.ide.shell.client.maven.BuildCommand;
import org.exoplatform.ide.shell.shared.CLIResource;
import org.exoplatform.ide.shell.shared.CLIResourceParameter;
import org.exoplatform.ide.shell.shared.ShellConfiguration;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.VFSInfoUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfoImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Aug 12, 2011 evgen $
 */
public class ShellInitializer {

    private final class AsyncRequestCallbackExtension extends AsyncRequestCallback<ItemWrapper> {
        private final Folder root;

        private AsyncRequestCallbackExtension(Unmarshallable<ItemWrapper> unmarshaller, Folder root) {
            super(unmarshaller);
            this.root = root;
        }

        @Override
        protected void onSuccess(ItemWrapper result) {
            if (result.getItem() instanceof Folder) {
                Environment.get().setCurrentFolder((Folder)result.getItem());
                Environment.get().saveValue(EnvironmentVariables.CURRENT_FOLDER_ID,
                                            result.getItem().getId());
            } else {
                Environment.get().setCurrentFolder(root);
                Environment.get().saveValue(EnvironmentVariables.CURRENT_FOLDER_ID,
                                            root.getId());
            }
            createShell();
        }

        @Override
        protected void onFailure(Throwable exception) {
            Environment.get().setCurrentFolder(root);
            Environment.get().saveValue(EnvironmentVariables.CURRENT_FOLDER_ID,
                                        root.getId());
            createShell();
        }
    }

    private static native String getConfigurationURL()/*-{
        return $wnd.configurationURL;
    }-*/;

    public void init() {
        try {
            AutoBean<ShellConfiguration> autoBean = CloudShell.AUTO_BEAN_FACTORY.shellConfiguration();
            AutoBeanUnmarshaller<ShellConfiguration> unmarshaller = new AutoBeanUnmarshaller<ShellConfiguration>(autoBean);
            ShellService.getService().loadConfiguration(new AsyncRequestCallback<ShellConfiguration>(unmarshaller) {

                                                            @Override
                                                            protected void onSuccess(ShellConfiguration result) {
                                                                CloudShell.getCommands().add(new HelpCommand());
                                                                CloudShell.getCommands().add(new ClearCommand());
                                                                Environment.get().saveValue(EnvironmentVariables.USER_NAME,
                                                                                            result.getUser().getUserId());
                                                                if (result.getUser().getUserId().equals("__anonim"))
                                                                {
                                                                    createShell();
                                                                    CloudShell.console().println(CloudShell.messages.welcomeMessage());
                                                                    return;
                                                                }
                                                                if (result.getVfsBaseUrl() != null) {
                                                                    Environment.get().saveValue(EnvironmentVariables.VFS_BASE_URL,
                                                                                                result.getVfsBaseUrl());
                                                                    initCommands();
                                                                    try {
                                                                        new VirtualFileSystem(Environment.get().getValue(EnvironmentVariables.VFS_BASE_URL))
                                                                                .init(new AsyncRequestCallback<VirtualFileSystemInfo>(
                                                                                        new VFSInfoUnmarshaller(
                                                                                                new VirtualFileSystemInfoImpl())) {

                                                                                    @Override
                                                                                    protected void onSuccess(VirtualFileSystemInfo result) {
                                                                                        Environment.get()
                                                                                                   .saveValue(EnvironmentVariables.VFS_ID,
                                                                                                              result.getId());
                                                                                        updateCurrentDir(result.getRoot());
                                                                                    }

                                                                                    @Override
                                                                                    protected void onFailure(Throwable exception) {
                                                                                        CloudShell.console()
                                                                                                  .println(exception.getMessage());
                                                                                    }
                                                                                });
                                                                    } catch (RequestException e) {
                                                                        CloudShell.console().println(e.getMessage());
                                                                    }
                                                                } else {
                                                                    createShell();
                                                                }
                                                            }

                                                            @Override
                                                            protected void onFailure(Throwable exception) {
                                                                exception.printStackTrace();
                                                                CloudShell.console().println(
                                                                        exception.getMessage() != null ? exception.getMessage() : "");
                                                            }
                                                        });

        } catch (RequestException e) {
            CloudShell.console().println(e.getMessage());
        }
    }

    /**
     *
     */
    private void initCommands() {
        CloudShell.getCommands().add(new LsCommand());
        CloudShell.getCommands().add(new MkdirCommand());
        CloudShell.getCommands().add(new PwdCommand());
        CloudShell.getCommands().add(new CdCommand());
        CloudShell.getCommands().add(new RmCommand());
        CloudShell.getCommands().add(new CatCommand());
        CloudShell.getCommands().add(new KillJobCommand());
        CloudShell.getCommands().add(new JobsCommand());

        CloudShell.getCommands().add(new BuildCommand());
    }

    /** Get the list of available commands. */
    private void getCommands() {
        try {
            ShellService.getService().getCommands(
                    new AsyncRequestCallback<Set<CLIResource>>(new CLIResourceUnmarshaller(new HashSet<CLIResource>())) {
                        @Override
                        protected void onSuccess(Set<CLIResource> result) {
                            // TODO think how to add async parameter to all resources.
                            CLIResourceParameter asyncParameter = new CLIResourceParameter();
                            asyncParameter.setName("async");
                            asyncParameter.setHasArg(false);
                            asyncParameter.setMandatory(false);
                            asyncParameter.getOptions().add("&");

                            for (CLIResource resource : result) {
                                resource.getParams().add(asyncParameter);
                            }
                            CloudShell.getCommands().addAll(result);
                            CloudShell.console().println(CloudShell.messages.welcomeMessage());
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

    /**
     * Update the current directory value.
     *
     * @param root
     */
    private void updateCurrentDir(final Folder root) {
        String id = Location.getParameter("workdir");
        if (id != null && !id.isEmpty()) {
            try {
                VirtualFileSystem.getInstance().getItemById(id, new AsyncRequestCallbackExtension(new ItemUnmarshaller(new ItemWrapper()), root));
            } catch (RequestException e) {
                Environment.get().setCurrentFolder(root);
                Environment.get().saveValue(EnvironmentVariables.CURRENT_FOLDER_ID, root.getId());
                createShell();
            }
        } else {
            Environment.get().setCurrentFolder(root);
            Environment.get().saveValue(EnvironmentVariables.CURRENT_FOLDER_ID, root.getId());
            createShell();
        }
    }

    /** Create Shell view. */
    private void createShell() {
        Display console = GWT.create(Display.class);
        CloudShell.consoleWriter = console;
        new ShellPresenter(console);
        getCommands();
    }
}
