/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.api.editor;

import com.codenvy.ide.api.resources.FileType;


/**
 * Editor Registry allows to registed new Editor for given FileType.
 * This editor will be used as default to open such kind of Files.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public interface EditorRegistry
{

   /**
    * Register editor provider for file type.
    * @param fileType
    * @param provider
    */
   public void register(FileType fileType, EditorProvider provider);

   /**
    * Get default editor provide assigned for file type;
    * @param fileType resource file type 
    * @return editor provider
    */
   public EditorProvider getDefaultEditor(FileType fileType);

}