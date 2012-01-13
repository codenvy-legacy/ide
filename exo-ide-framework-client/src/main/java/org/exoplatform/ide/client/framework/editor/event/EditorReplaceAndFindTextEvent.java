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
package org.exoplatform.ide.client.framework.editor.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class EditorReplaceAndFindTextEvent extends GwtEvent<EditorReplaceAndFindTextHandler>
{

   public static final GwtEvent.Type<EditorReplaceAndFindTextHandler> TYPE =
      new GwtEvent.Type<EditorReplaceAndFindTextHandler>();

   private String findText;

   private String replaceText;

   private boolean caseSensitive;

   private String fileId;

   /**
    * @param findText text to find
    * @param replaceText text to replace
    * @param caseSensitive is case sensitive
    * @param fileId path to file
    */
   public EditorReplaceAndFindTextEvent(String findText, String replaceText, boolean caseSensitive, String fileId)
   {
      this.findText = findText;
      this.replaceText = replaceText;
      this.caseSensitive = caseSensitive;
      this.fileId = fileId;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(EditorReplaceAndFindTextHandler handler)
   {
      handler.onEditorReplaceAndFindText(this);
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<EditorReplaceAndFindTextHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @return the findText
    */
   public String getFindText()
   {
      return findText;
   }

   /**
    * @return the replaceText
    */
   public String getReplaceText()
   {
      return replaceText;
   }

   /**
    * @return the caseSensitive
    */
   public boolean isCaseSensitive()
   {
      return caseSensitive;
   }

   /**
    * @return the path
    */
   public String getFileId()
   {
      return fileId;
   }

}
