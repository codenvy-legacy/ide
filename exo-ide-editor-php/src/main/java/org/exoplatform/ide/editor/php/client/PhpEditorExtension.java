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

package org.exoplatform.ide.editor.php.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.editor.AddCommentsModifierEvent;
import org.exoplatform.ide.client.framework.module.EditorCreator;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.FileType;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.client.api.Editor;

/**
 * Provides a text editing area along with UI for executing text commands on the.<br>
 * 
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Revision$
 */
public class PhpEditorExtension extends Extension implements InitializeServicesHandler {

    interface DefaultContent extends ClientBundle {
        @Source("hello.php")
        TextResource getSource();
    }

    public final static DefaultContent DEFAULT_CONTENT = GWT.create(DefaultContent.class);

    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(InitializeServicesEvent.TYPE, this);
        IDE.getInstance()
           .addControl(new NewItemControl("File/New/New PHP File", "PHP File", "Create PHP File", PhpClientBundle.INSTANCE.php(),
                                          PhpClientBundle.INSTANCE.phpDisabled(), MimeType.APPLICATION_PHP).setGroupName(GroupNames.NEW_SCRIPT));
        PhpClientBundle.INSTANCE.css().ensureInjected();
    }

    /**
     * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
     */
    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        IDE.getInstance().getFileTypeRegistry().addFileType(new FileType(MimeType.APPLICATION_PHP, "php", Images.INSTANCE.php()),
                    new EditorCreator() {
                        @Override
                        public Editor createEditor() {
                            return new PhpEditor(MimeType.APPLICATION_PHP);
                        }
                    });
        IDE.getInstance().getFileTypeRegistry().addFileType(new FileType(MimeType.APPLICATION_X_PHP, "php", Images.INSTANCE.php()),
                    new EditorCreator() {
                        @Override
                        public Editor createEditor() {
                            return new PhpEditor(MimeType.APPLICATION_X_PHP);
                        }
                    });
        IDE.getInstance().getFileTypeRegistry().addFileType(new FileType(MimeType.APPLICATION_X_HTTPD_PHP, "php", Images.INSTANCE.php()),
                    new EditorCreator() {
                        @Override
                        public Editor createEditor() {
                            return new PhpEditor(MimeType.APPLICATION_X_HTTPD_PHP);
                        }
                    });

        // PhpOutlineItemCreator phpOutlineItemCreator = new PhpOutlineItemCreator();
        // IDE.getInstance().addOutlineItemCreator(MimeType.APPLICATION_PHP, phpOutlineItemCreator);
        // IDE.getInstance().addOutlineItemCreator(MimeType.APPLICATION_X_PHP, phpOutlineItemCreator);
        // IDE.getInstance().addOutlineItemCreator(MimeType.APPLICATION_X_HTTPD_PHP, phpOutlineItemCreator);

         PhpCommentsModifier commentsModifier = new PhpCommentsModifier();
         IDE.fireEvent(new AddCommentsModifierEvent(MimeType.APPLICATION_PHP, commentsModifier));
         IDE.fireEvent(new AddCommentsModifierEvent(MimeType.APPLICATION_X_PHP, commentsModifier));
         IDE.fireEvent(new AddCommentsModifierEvent(MimeType.APPLICATION_X_HTTPD_PHP, commentsModifier));
    }
}
