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
package org.exoplatform.ide.editor.codemirror;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorProducer;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CodeMirrorPlainTextProduser Feb 10, 2011 9:38:20 AM evgen $
 *
 */
public class CodeMirrorPlainTextProducer extends EditorProducer
{

   private Map<String, Object> params = new HashMap<String, Object>();

   /**
    * @param mimeType
    * @param description
    * @param ext
    * @param isDefault
    */
   public CodeMirrorPlainTextProducer(String mimeType, String description, String ext, boolean isDefault)
   {
      super(mimeType, description, ext, isDefault);
      params.put(CodeMirrorParams.CONFIGURATION, new CodeMirrorConfiguration("['parsexml.js', 'parsecss.js']", // generic code parsers
         "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css']" // code styles
      ));

      params.put(CodeMirrorParams.MIME_TYPE, mimeType);
   }

   /**
    * @see org.exoplatform.ide.editor.api.EditorProducer#createEditor(org.exoplatform.ide.client.framework.vfs.File, com.google.gwt.event.shared.HandlerManager, java.util.HashMap)
    */
   @Override
   public Editor createEditor(File file, HandlerManager eventBus, HashMap<String, Object> params)
   {
      params.putAll(this.params);
      return new CodeMirror(file, params, eventBus);
   }

}
