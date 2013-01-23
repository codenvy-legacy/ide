/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.eclipse.jdt.client.refactoring.rename;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: RefactoringRenameEvent.java Jan 17, 2013 4:51:38 PM azatsarynnyy $
 *
 */
public class RefactoringRenameEvent extends GwtEvent<RefactoringRenameHandler>
{

   private FileModel file;

   public static final GwtEvent.Type<RefactoringRenameHandler> TYPE = new Type<RefactoringRenameHandler>();

   public RefactoringRenameEvent()
   {
   }

   /**
    * 
    * @param file
    */
   public RefactoringRenameEvent(FileModel file)
   {
      this.file = file;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<RefactoringRenameHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(RefactoringRenameHandler handler)
   {
      handler.onRename(this);
   }

   /**
    * Returns file to rename.
    * 
    * @return a file to rename
    */
   public FileModel getFile()
   {
      return file;
   }

}
