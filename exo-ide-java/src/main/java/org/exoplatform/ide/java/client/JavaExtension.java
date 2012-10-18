/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.java.client;

import com.google.inject.Inject;

import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.core.editor.EditorRegistry;
import org.exoplatform.ide.java.client.codeassistant.ContentAssistHistory;
import org.exoplatform.ide.java.client.editor.JavaEditorProvider;
import org.exoplatform.ide.java.client.templates.ContextTypeRegistry;
import org.exoplatform.ide.java.client.templates.TemplateStore;
import org.exoplatform.ide.resources.FileType;

import java.util.HashMap;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class JavaExtension
{
   
   /**
    * 
    */
   @Inject
   public JavaExtension(ResourceProvider resourceProvider, EditorRegistry editorRegistry, JavaEditorProvider javaEditorProvider)
   {
      FileType javaFile = new FileType(null, "application/java", "java");
      editorRegistry.register(javaFile, javaEditorProvider);
      resourceProvider.registerFileType(javaFile);
   }

   /**
    * @return
    */
   public static JavaExtension get()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @return
    */
   public HashMap<String, String> getOptions()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @return
    */
   public TemplateStore getTemplateStore()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @return
    */
   public ContextTypeRegistry getTemplateContextRegistry()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @return
    */
   public ContentAssistHistory getContentAssistHistory()
   {
      // TODO Auto-generated method stub
      return null;
   }
}
