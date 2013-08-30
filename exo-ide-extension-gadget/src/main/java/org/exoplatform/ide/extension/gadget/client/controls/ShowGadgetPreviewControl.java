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
package org.exoplatform.ide.extension.gadget.client.controls;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.gadget.client.GadgetClientBundle;
import org.exoplatform.ide.extension.gadget.client.event.PreviewGadgetEvent;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"developer"})
public class ShowGadgetPreviewControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler {

    public static final String ID = "Run/Show Gadget Preview";

    public static final String TITLE = "Show Gadget Preview";

    public ShowGadgetPreviewControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(TITLE);
        setImages(GadgetClientBundle.INSTANCE.preview(), GadgetClientBundle.INSTANCE.previewDisabled());
        setEvent(new PreviewGadgetEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        if (event.getFile() == null) {
            setVisible(false);
            setEnabled(false);
            return;
        }

        if (MimeType.GOOGLE_GADGET.equals(event.getFile().getMimeType())) {
            setVisible(true);
            if (!event.getFile().isPersisted()) {
                setEnabled(false);
            } else {
                setEnabled(true);
            }
        } else {
            setVisible(false);
            setEnabled(false);
        }
    }
}
