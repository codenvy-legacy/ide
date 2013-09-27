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
package org.exoplatform.ide.extension.gadget.client;

import com.google.gwt.user.client.ui.Image;

import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.gadget.client.event.PreviewGadgetEvent;
import org.exoplatform.ide.extension.gadget.client.event.PreviewGadgetHandler;
import org.exoplatform.ide.extension.gadget.client.ui.GadgetPreviewPane;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.Link;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class GadgetPluginEventHandler implements EditorActiveFileChangedHandler, PreviewGadgetHandler,
                                                 ConfigurationReceivedSuccessfullyHandler, ViewClosedHandler {

    private FileModel activeFile;

    private IDEConfiguration applicationConfiguration;

    private boolean previewOpened = false;

    private GadgetPreviewPane gadgetPreviewPane;

    public GadgetPluginEventHandler() {
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(PreviewGadgetEvent.TYPE, this);
        IDE.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        this.activeFile = event.getFile();
        if (previewOpened) {
            IDE.getInstance().closeView(GadgetPreviewPane.ID);
            previewOpened = false;
        }

    }

    /** @see org.exoplatform.ide.extension.gadget.client.event.PreviewGadgetHandler#onPreviewGadget(org.exoplatform.ide.extension.gadget
     * .client.event.PreviewGadgetEvent) */
    @Override
    public void onPreviewGadget(PreviewGadgetEvent event) {
        String href = activeFile.getLinkByRelation(Link.REL_CONTENT_BY_PATH).getHref();
        href = href.replace(applicationConfiguration.getContext(), "");//
        if (gadgetPreviewPane == null) {
            gadgetPreviewPane = new GadgetPreviewPane(href, applicationConfiguration.getGadgetServer());
            gadgetPreviewPane.setIcon(new Image(GadgetClientBundle.INSTANCE.preview()));
            IDE.getInstance().openView(gadgetPreviewPane);
        } else {
            if (!gadgetPreviewPane.isViewVisible()) {
                gadgetPreviewPane.setViewVisible();
            }
        }
        gadgetPreviewPane.showGadget();
        previewOpened = true;
    }

    /** @see org.exoplatform.ide.client.framework.configuration.event
     * .ConfigurationReceivedSuccessfullyHandler#onConfigurationReceivedSuccessfully(org.exoplatform.ide.client.framework.configuration
     * .event.ConfigurationReceivedSuccessfullyEvent) */
    @Override
    public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event) {
        applicationConfiguration = event.getConfiguration();
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (gadgetPreviewPane == null)
            return;
        if (event.getView().getId().equals(gadgetPreviewPane.getId())) {
            previewOpened = false;
            gadgetPreviewPane = null;
        }
    }

}
