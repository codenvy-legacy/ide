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

package org.exoplatform.ide.client.application;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.workspace.event.SwitchVFSEvent;
import org.exoplatform.ide.client.workspace.event.SwitchVFSHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.VFSInfoUnmarshaller;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfoImpl;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class VirtualFileSystemSwitcher implements SwitchVFSHandler, ConfigurationReceivedSuccessfullyHandler {

    private String vfsBaseUrl;

    private String vfsId;

    public VirtualFileSystemSwitcher() {
        IDE.addHandler(SwitchVFSEvent.TYPE, this);
        IDE.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
    }

    @Override
    public void onSwitchVFS(SwitchVFSEvent event) {
        if (vfsId != null) {
            IDE.fireEvent(new VfsChangedEvent(null));
        }

        vfsId = event.getVfsID();
        String workspaceUrl = vfsBaseUrl;

        try {
            new VirtualFileSystem(workspaceUrl, new GWTLoader()).init(new AsyncRequestCallback<VirtualFileSystemInfo>(
                    new VFSInfoUnmarshaller(new VirtualFileSystemInfoImpl())) {
                @Override
                protected void onSuccess(VirtualFileSystemInfo result) {
                    IDE.fireEvent(new VfsChangedEvent(result));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Dialogs.getInstance().showError("Workspace " + vfsId + " not found.");
                }
            });

        } catch (Exception e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    @Override
    public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event) {
        vfsBaseUrl = event.getConfiguration().getVfsBaseUrl();
    }

}
