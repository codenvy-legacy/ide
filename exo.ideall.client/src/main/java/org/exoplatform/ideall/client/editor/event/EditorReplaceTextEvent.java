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
package org.exoplatform.ideall.client.editor.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class EditorReplaceTextEvent extends GwtEvent<EditorReplaceTextHandler>
{
   public static final GwtEvent.Type<EditorReplaceTextHandler> TYPE = new GwtEvent.Type<EditorReplaceTextHandler>();
   
   private String findText;
   private String replaceText;
   private boolean caseSensitive;
   private String path;
   private boolean replaceAll;
   
   /**
    * @param findText text to find
    * @param replaceText text to replace
    * @param caseSensitive is case sensitive
    * @param fromStart find from start
    * @param path path to file
    */
   public EditorReplaceTextEvent(String findText, String replaceText, boolean caseSensitive, String path){
      this.findText = findText;
      this.replaceText = replaceText;
      this.caseSensitive = caseSensitive;
      this.path = path;
      this.replaceAll = false;
   }
   
   /**
    * @param findText text to find
    * @param replaceText text to replace
    * @param caseSensitive is case sensitive
    * @param path path to file
    * @param replaceAll to replace all or first found
    */
   public EditorReplaceTextEvent(String findText, String replaceText, boolean caseSensitive, String path, boolean replaceAll){
      this.findText = findText;
      this.replaceText = replaceText;
      this.caseSensitive = caseSensitive;
      this.path = path;
      this.replaceAll = replaceAll;
   }
   
   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(EditorReplaceTextHandler handler)
   {
      handler.onEditorReplaceText(this);
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<EditorReplaceTextHandler> getAssociatedType()
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
   public String getPath()
   {
      return path;
   }

   /**
    * @return the replaceAll
    */
   public boolean isReplaceAll()
   {
      return replaceAll;
   }
}
