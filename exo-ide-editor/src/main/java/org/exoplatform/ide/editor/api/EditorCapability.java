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
package org.exoplatform.ide.editor.api;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: EditoCapability Feb 9, 2011 4:30:38 PM evgen $
 *
 */
public enum EditorCapability {

   /**
    * Editor supports code indentation
    */
   FORMAT_SOURCE,

   /**
    * Editor supports line numbering that is displaying line numbers at the left field of code
    */
   SHOW_LINE_NUMBERS,

   /**
    *  you can use method goToPosition() to set cursor in the any position in the current editor
    */
   GO_TO_POSITION,

   /**
    * Editor can delete current line
    */
   DELETE_CURRENT_LINE,

   /**
    * Editor support find and replace feature
    */
   FIND_AND_REPLACE,

   /**
    * Editor and opened file type support autocompletion feature
    */
   CAN_BE_AUTOCOMPLETED,

   /**
    * Editor and opened file type support outline feature
    */
   CAN_BE_OUTLINED,

   /**
    * Editor and opened file type support validation feature
    */
   CAN_BE_VALIDATED
}
