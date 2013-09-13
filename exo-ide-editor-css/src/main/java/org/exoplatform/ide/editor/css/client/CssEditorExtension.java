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
package org.exoplatform.ide.editor.css.client;

import com.google.gwt.core.client.GWT;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.editor.AddCommentsModifierEvent;
import org.exoplatform.ide.client.framework.module.EditorCreator;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.FileType;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.client.api.Editor;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class CssEditorExtension extends Extension {

    private static final String CSS_ICON = UIHelper.getGadgetImagesURL() + "css/css.png";

    public static final CssMessages MESSAGES = GWT.create(CssMessages.class);

    public static final CssBundle RESOURCES = GWT.create(CssBundle.class);

    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        RESOURCES.css().ensureInjected();

        IDE.getInstance().addControl(
                new NewItemControl("File/New/New CSS", MESSAGES.controlNewCssTitle(), MESSAGES.controlNewCssPrompt(),
                                   CSS_ICON, MimeType.TEXT_CSS).setGroupName(GroupNames.NEW_FILE));

        IDE.getInstance().getFileTypeRegistry()
           .addFileType(new FileType(MimeType.TEXT_CSS, "css", RESOURCES.cssImage()), new EditorCreator() {
               @Override
               public Editor createEditor() {
                   return new CssEditor(MimeType.TEXT_CSS);
               }
           });

//        IDE.getInstance().addOutlineItemCreator(MimeType.TEXT_CSS, new CssOutlineItemCreator());
        IDE.fireEvent(new AddCommentsModifierEvent(MimeType.TEXT_CSS, new CssCommentsModifier()));
    }

}
