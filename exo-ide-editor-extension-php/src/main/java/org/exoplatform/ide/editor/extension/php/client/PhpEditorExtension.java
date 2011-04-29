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

package org.exoplatform.ide.editor.extension.php.client;


import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration;
import org.exoplatform.ide.editor.codemirror.CodeMirrorProducer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;
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

   /**
    * Localizable constants and messages
    */
   private final Messages messages = GWT.create(Messages.class);

   public final static DefaultContent DEFAULT_CONTENT = GWT.create(DefaultContent.class);
   
   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.EVENT_BUS.addHandler(InitializeServicesEvent.TYPE, this);

      IDE.getInstance().addControl(
         new NewItemControl("File/New/New PHP File", "PHP File", "Create PHP File", Images.PHP,
            MimeType.APPLICATION_PHP), DockTarget.NONE, false);
   }


   public void onInitializeServices(InitializeServicesEvent event)
   {
      IDE.getInstance().addEditor(
         new CodeMirrorProducer(MimeType.APPLICATION_PHP, "CodeMirror PHP editor", "php", Images.PHP, true,
            new CodeMirrorConfiguration("['parsexml.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'tokenizephp.js', 'parsephp.js', 'parsephphtmlmixed.js']", // generic code parsers
               "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css', '" + CodeMirrorConfiguration.PATH + "css/jscolors.css', '" + CodeMirrorConfiguration.PATH +  "css/csscolors.css', '" + CodeMirrorConfiguration.PATH +  "css/phpcolors.css']" // code styles
            )
         )
      );
   }
   
}
