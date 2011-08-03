/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.editor.extension.css.client;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration;
import org.exoplatform.ide.editor.codemirror.CodeMirrorProducer;
import org.exoplatform.ide.editor.extension.css.client.codeassistant.CssCodeAssistant;
import org.exoplatform.ide.editor.extension.css.client.codemirror.CssParser;
import org.exoplatform.ide.editor.extension.css.client.outline.CssOutlineItemCreator;

import com.google.gwt.core.client.GWT;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class CssEditorExtension extends Extension
{

   
   public static final CssMessages MESSAGES = GWT.create(CssMessages.class);
   
   public static final CssBundle RESOURCES = GWT.create(CssBundle.class); 
   
   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize()
    */
   @Override
   public void initialize()
   {
      
      RESOURCES.css().ensureInjected();

      IDE.getInstance().addEditor(new CodeMirrorProducer(MimeType.TEXT_CSS, MESSAGES.cssEditor(), "css",
         Images.CSS, true, 
         new CodeMirrorConfiguration().
            setGenericParsers("['parsecss.js']").
            setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/csscolors.css']").
            setParser(new CssParser()).
            setCodeAssistant(new CssCodeAssistant())         
      ));
      
      IDE.getInstance().addOutlineItemCreator(MimeType.TEXT_CSS, new CssOutlineItemCreator());
   }

}
