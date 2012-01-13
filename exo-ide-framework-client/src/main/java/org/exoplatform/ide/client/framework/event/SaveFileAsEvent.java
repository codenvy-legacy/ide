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
package org.exoplatform.ide.client.framework.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * Open new dialog window for asking new file name. Save new file with new name. Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SaveFileAsEvent extends GwtEvent<SaveFileAsHandler>
{
   /**
    * Enum to configure ask for value dialog window: two buttons (Yes, Cancel) or three buttons (Yes, No, Cancel).
    */
   public enum SaveDialogType {
      YES_CANCEL, EXTENDED
   }

   public static final GwtEvent.Type<SaveFileAsHandler> TYPE = new GwtEvent.Type<SaveFileAsHandler>();

   private FileModel file;

   private boolean saveOnly = false;

   private GwtEvent<?> eventFiredOnNo;

   private GwtEvent<?> eventFiredOnCancel;

   private SaveDialogType dialogType;

   public SaveFileAsEvent()
   {
   }

   public SaveFileAsEvent(FileModel file)
   {
      this.file = file;
   }

   public SaveFileAsEvent(FileModel file, boolean saveOnly)
   {
      this.file = file;
      this.saveOnly = saveOnly;
   }

   /**
    * @param file - file to save
    * @param type - type of dialog window (two or three buttons)
    * @param eventFiredOnNo - event, which will be fired if No button will be clicked
    * @param eventFiredOnCancel - event, which will be fired if Cancel button will be clicked (if null - window will be closed
    *           without any actions)
    */
   public SaveFileAsEvent(FileModel file, SaveDialogType type, GwtEvent<?> eventFiredOnNo,
      GwtEvent<?> eventFiredOnCancel)
   {
      this.file = file;
      this.eventFiredOnNo = eventFiredOnNo;
      this.eventFiredOnCancel = eventFiredOnCancel;
      this.dialogType = type;
   }

   /**
    * @param type
    * @param eventFiredOnNo
    * @param eventFiredOnCancel
    */
   public SaveFileAsEvent(SaveDialogType type, GwtEvent<?> eventFiredOnNo, GwtEvent<?> eventFiredOnCancel)
   {
      this.eventFiredOnNo = eventFiredOnNo;
      this.eventFiredOnCancel = eventFiredOnCancel;
      this.dialogType = type;
   }

   public boolean isSaveOnly()
   {
      return saveOnly;
   }

   @Override
   protected void dispatch(SaveFileAsHandler handler)
   {
      handler.onSaveFileAs(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<SaveFileAsHandler> getAssociatedType()
   {
      return TYPE;
   }

   public FileModel getFile()
   {
      return file;
   }

   /**
    * @return the eventFiredOnCancel
    */
   public GwtEvent<?> getEventFiredOnCancel()
   {
      return eventFiredOnCancel;
   }

   /**
    * @return the eventFiredOnNo
    */
   public GwtEvent<?> getEventFiredOnNo()
   {
      return eventFiredOnNo;
   }

   /**
    * @return the dialogType
    */
   public SaveDialogType getDialogType()
   {
      return dialogType;
   }

}
