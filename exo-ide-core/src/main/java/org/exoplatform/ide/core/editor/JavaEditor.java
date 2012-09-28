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

import org.exoplatform.ide.AppContext;
import org.exoplatform.ide.editor.DocumentProvider;
import org.exoplatform.ide.editor.EditorInitException;
import org.exoplatform.ide.editor.EditorInput;
import org.exoplatform.ide.texteditor.BaseTextEditor;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class JavaEditor extends BaseTextEditor
{

   /**
    * @param appContext
    * @param documentProvider
    */
   public JavaEditor(AppContext appContext, DocumentProvider documentProvider)
   {
      super(appContext, documentProvider);
   }

   /**
    * @see org.exoplatform.ide.texteditor.BaseTextEditor#init(org.exoplatform.ide.editor.EditorInput)
    */
   @Override
   public void init(EditorInput input) throws EditorInitException
   {
      super.init(input);
   }
}
