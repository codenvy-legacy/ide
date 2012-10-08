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
package org.exoplatform.ide.core.editor;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.core.editor.css.CssEditorProvider;
import org.exoplatform.ide.editor.EditorProvider;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonIntegerMap;
import org.exoplatform.ide.resources.FileType;

/**
 * Registry for holding {@link EditorProvider} for specific {@link FileType}.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class EditorRegistry
{

   private JsonIntegerMap<EditorProvider> registry;

   @Inject
   public EditorRegistry(@Named("defaulEditor") EditorProvider defaultProvider,
      @Named("defaultFileType") FileType defaultFile, JavaEditorProvider javaEditorProvider,
      ResourceProvider resourceProvider, CssEditorProvider cssEditorProvider)
   {
      super();
      registry = JsonCollections.createIntegerMap();
      register(defaultFile, defaultProvider);
      //XXX for demo only
      FileType javaFile = new FileType(null, "application/java", "java");
      register(javaFile, javaEditorProvider);
      resourceProvider.registerFileType(javaFile);
      FileType cssFile = new FileType(null, "text/css", "css");
      register(cssFile, cssEditorProvider);
      resourceProvider.registerFileType(cssFile);
   }

   /**
    * Register editor provider for file type.
    * @param fileType
    * @param provider
    */
   public void register(FileType fileType, EditorProvider provider)
   {
      registry.put(fileType.getId(), provider);
   }

   /**
    * Get default editor provide assigned for file type;
    * @param fileType resource file type 
    * @return editor provider
    */
   public EditorProvider getDefaultEditor(FileType fileType)
   {
      return registry.get(fileType.getId());
   }
}
