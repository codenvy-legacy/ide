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
package org.eclipse.jdt.client;

import com.google.gwt.event.shared.HandlerManager;

import org.eclipse.jdt.client.codeassistant.api.IProblemLocation;
import org.eclipse.jdt.client.internal.text.quickassist.IQuickAssistInvocationContext;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.editor.text.IDocument;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class QuickAssistPresenter implements IQuickAssistInvocationContext, EditorActiveFileChangedHandler
{

   
   
   /**
    * 
    */
   public QuickAssistPresenter(HandlerManager eventBus)
   {
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      // TODO Auto-generated method stub
   }

   /**
    * @see org.eclipse.jdt.client.internal.text.quickassist.IQuickAssistInvocationContext#getOffset()
    */
   @Override
   public int getOffset()
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /**
    * @see org.eclipse.jdt.client.internal.text.quickassist.IQuickAssistInvocationContext#getLength()
    */
   @Override
   public int getLength()
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /**
    * @see org.eclipse.jdt.client.internal.text.quickassist.IQuickAssistInvocationContext#getDocument()
    */
   @Override
   public IDocument getDocument()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.client.internal.text.quickassist.IQuickAssistInvocationContext#getProblemsAtOffset()
    */
   @Override
   public IProblemLocation[] getProblemsAtOffset()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.client.internal.text.quickassist.IQuickAssistInvocationContext#isUpdatedOffset()
    */
   @Override
   public boolean isUpdatedOffset()
   {
      // TODO Auto-generated method stub
      return false;
   }

}
