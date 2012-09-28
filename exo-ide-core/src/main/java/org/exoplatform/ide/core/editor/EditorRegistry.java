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

import com.google.inject.name.Named;

import com.google.inject.Inject;

import org.exoplatform.ide.editor.EditorProvider;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;

/**
 * Registry interface for holding {@link EditorProvider} for specific mime types.
 * <p>
 * Editor register not limit number providers for one mime type. In general for specific mime type
 * can exist more the one {@link EditorProvider}
 * </p> 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class EditorRegistry
{

   private JsonStringMap<JsonArray<EditorProvider>> registry = JsonCollections.createStringMap();

   private EditorProvider defaultProvider;

   @Inject
   public EditorRegistry(@Named("defaulEditor") EditorProvider defaultProvider, JavaEditorProvider javaEditorProvider)
   {
      super();
      this.defaultProvider = defaultProvider;
      //XXX for demo only
      register("application/java", javaEditorProvider);
   }

   /**
    * Register editor provider for mime type.
    * @param mimeType
    * @param provider
    */
   public void register(String mimeType, EditorProvider provider)
   {
      if (!registry.containsKey(mimeType))
      {
         registry.put(mimeType, JsonCollections.<EditorProvider> createArray());
      }
      registry.get(mimeType).add(provider);
   }

   /**
    * Get default editor provide assigned for mime type;
    * @param mimeType resource mime type 
    * @return editor provider
    */
   public EditorProvider getDefaultEditor(String mimeType)
   {
      //FIXME first dummy implementation return first found editor as default
      if (registry.containsKey(mimeType))
      {
         return registry.get(mimeType).get(0);
      }
      else
      {
         return defaultProvider;
      }

   }

   /**
    * Get all registered providers for mime type
    * @param mimeType the mime type of resource
    * @return array of <code>EditorProvider</code>
    */
   public EditorProvider[] getAvailableEditors(String mimeType)
   {
      //TODO
      return null;
   }

   //TODO maybe need setDefaultEditor

}
