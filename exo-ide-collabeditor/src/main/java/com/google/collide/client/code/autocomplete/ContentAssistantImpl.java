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
package com.google.collide.client.code.autocomplete;

import com.google.collide.shared.util.JsonCollections;

import com.google.collide.json.shared.JsonStringMap;

import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.contentassist.ContentAssistProcessor;
import org.exoplatform.ide.editor.api.contentassist.ContentAssistant;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class ContentAssistantImpl implements ContentAssistant
{
   
   public JsonStringMap<ContentAssistProcessor> processors = JsonCollections.createMap();

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.ContentAssistant#install(org.exoplatform.ide.editor.api.Editor)
    */
   @Override
   public void install(Editor textViewer)
   {
      // TODO Auto-generated method stub
      
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.ContentAssistant#uninstall()
    */
   @Override
   public void uninstall()
   {
      // TODO Auto-generated method stub
      
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.ContentAssistant#showPossibleCompletions()
    */
   @Override
   public String showPossibleCompletions()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.ContentAssistant#showContextInformation()
    */
   @Override
   public String showContextInformation()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.ContentAssistant#getContentAssistProcessor(java.lang.String)
    */
   @Override
   public ContentAssistProcessor getContentAssistProcessor(String contentType)
   {
      return processors.get(contentType);
   }

}
