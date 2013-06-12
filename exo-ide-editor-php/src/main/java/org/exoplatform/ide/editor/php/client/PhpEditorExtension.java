/*
 * Copyright (C) 2010 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
