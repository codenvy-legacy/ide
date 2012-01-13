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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration;
import org.exoplatform.ide.editor.codemirror.CodeMirrorProducer;
import org.exoplatform.ide.editor.php.client.codeassistant.PhpCodeAssistant;
import org.exoplatform.ide.editor.php.client.codemirror.PhpAutocompleteHelper;
import org.exoplatform.ide.editor.php.client.codemirror.PhpOutlineItemCreator;
import org.exoplatform.ide.editor.php.client.codemirror.PhpParser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * 
 * Provides a text editing area along with UI for executing text commands on the.<br>
 * 
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Revision$
 */
public class PhpEditorExtension extends Extension implements InitializeServicesHandler
{

   interface DefaultContent extends ClientBundle
   {
      @Source("hello.php")
      TextResource getSource();
   }

   public final static DefaultContent DEFAULT_CONTENT = GWT.create(DefaultContent.class);

   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(InitializeServicesEvent.TYPE, this);

      IDE.getInstance().addControl(
         new NewItemControl("File/New/New PHP File", "PHP File", "Create PHP File", PhpClientBundle.INSTANCE.php(),
            PhpClientBundle.INSTANCE.phpDisabled(), MimeType.APPLICATION_PHP));

      PhpClientBundle.INSTANCE.css().ensureInjected();
   }

   public void onInitializeServices(InitializeServicesEvent event)
   {
      CodeMirrorConfiguration phpCodeMirrorConfiguration =
         new CodeMirrorConfiguration()
            .setGenericParsers(
               "['parsexml.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'tokenizephp.js', 'parsephp.js', 'parsephphtmlmixed.js']")
            .setGenericStyles(
               "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css', '" + CodeMirrorConfiguration.PATH
                  + "css/jscolors.css', '" + CodeMirrorConfiguration.PATH + "css/csscolors.css', '"
                  + CodeMirrorConfiguration.PATH + "css/phpcolors.css']").setParser(new PhpParser())
            .setCanBeOutlined(true).setAutocompleteHelper(new PhpAutocompleteHelper())
            .setCodeAssistant(new PhpCodeAssistant()).setCanHaveSeveralMimeTypes(true);

      IDE.getInstance().addEditor(
         new CodeMirrorProducer(MimeType.APPLICATION_PHP, "CodeMirror PHP editor", "php", Images.INSTANCE.php(), true,
            phpCodeMirrorConfiguration));

      IDE.getInstance().addEditor(
         new CodeMirrorProducer(MimeType.APPLICATION_X_PHP, "CodeMirror PHP editor", "php", Images.INSTANCE.php(),
            true, phpCodeMirrorConfiguration));

      IDE.getInstance().addEditor(
         new CodeMirrorProducer(MimeType.APPLICATION_X_HTTPD_PHP, "CodeMirror PHP editor", "php",
            Images.INSTANCE.php(), true, phpCodeMirrorConfiguration));

      PhpOutlineItemCreator phpOutlineItemCreator = new PhpOutlineItemCreator();
      IDE.getInstance().addOutlineItemCreator(MimeType.APPLICATION_PHP, phpOutlineItemCreator);
      IDE.getInstance().addOutlineItemCreator(MimeType.APPLICATION_X_PHP, phpOutlineItemCreator);
      IDE.getInstance().addOutlineItemCreator(MimeType.APPLICATION_X_HTTPD_PHP, phpOutlineItemCreator);
   }

}