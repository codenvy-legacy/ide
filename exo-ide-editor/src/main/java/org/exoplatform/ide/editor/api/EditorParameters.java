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

import java.util.List;

import org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CodeMirrorParams Feb 10, 2011 9:41:09 AM evgen $
 *
 */
public interface EditorParameters
{
   /**
    * Key for {@link CodeMirrorConfiguration} object
    */
   String CONFIGURATION = "configuration";

   /**
    * Key for {@link Boolean}, set editor in read only mode
    */
   String IS_READ_ONLY = "is_read_only";

   /**
    * Key for {@link Boolean}, set show/hide line numbers
    */
   String IS_SHOW_LINE_NUMER = "is_show_line_number";

   /**
    * Key for {@link List} of hot keys
    */
   String HOT_KEY_LIST = "hot_key_list";

   /**
    * Key for content mime type
    */
   String MIME_TYPE = "mime_type";
}
