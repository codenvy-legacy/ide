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
package org.exoplatform.ide.client.outline.ui;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.ide.client.framework.outline.ui.OutlineItemCreator;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: EditorFactory Feb 22, 2011 11:06:05 AM evgen $
 *
 */
public class OutlineItemCreatorFactory
{
   private static Map<String, OutlineItemCreator> outlineItemCreators = new HashMap<String, OutlineItemCreator>();

   static
   {
//      addOutlineItemCreator(MimeType.UWA_WIDGET, new HtmlOutlineItemCreator());
   }

   public static void addOutlineItemCreator(String mimeType, OutlineItemCreator outlineItemCreator)
   {
      outlineItemCreators.put(mimeType, outlineItemCreator);
   }

   public static OutlineItemCreator getOutlineItemCreator(String mimeType)
   {
      if (outlineItemCreators.containsKey(mimeType))
      {
         return outlineItemCreators.get(mimeType);
      }

      return null;
   }

}
