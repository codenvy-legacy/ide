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
package org.exoplatform.ide.editor.java.hover;

import com.google.gwt.event.shared.HandlerManager;

import org.eclipse.jdt.client.UpdateOutlineEvent;
import org.eclipse.jdt.client.UpdateOutlineHandler;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.internal.text.JavaWordFinder;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.hover.TextHover;
import org.exoplatform.ide.editor.text.IRegion;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public abstract class AbstractJavaHover implements TextHover, UpdateOutlineHandler 
{
      
   protected CompilationUnit cUnit;
   
   /**
    * 
    */
   public AbstractJavaHover(HandlerManager eventBus)
   {
      eventBus.addHandler(UpdateOutlineEvent.TYPE, this);
   }
   /**
   * @see org.eclipse.jdt.client.UpdateOutlineHandler#onUpdateOutline(org.eclipse.jdt.client.UpdateOutlineEvent)
   */
   @Override
   public void onUpdateOutline(UpdateOutlineEvent event)
   {
      cUnit = event.getCompilationUnit();
   }
   /**
    * @see org.exoplatform.ide.editor.hover.TextHover#getHoverRegion(org.exoplatform.ide.editor.api.Editor, int)
    */
   @Override
   public IRegion getHoverRegion(Editor editor, int offset)
   {
      return JavaWordFinder.findWord(editor.getDocument(), offset);
   }

}
