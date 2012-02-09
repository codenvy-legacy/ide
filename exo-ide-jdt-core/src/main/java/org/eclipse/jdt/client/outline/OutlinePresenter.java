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
package org.eclipse.jdt.client.outline;

import com.google.gwt.core.client.GWT;

import org.eclipse.jdt.client.UpdateOutlineEvent;
import org.eclipse.jdt.client.UpdateOutlineHandler;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;

/**
 * Presenter for Java Outline View.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Feb 6, 2012 4:29:06 PM anya $
 * 
 */
public class OutlinePresenter implements UpdateOutlineHandler, ViewClosedHandler
{
   interface Display extends IsView
   {
      void updateOutline(CompilationUnit cUnit);
   }

   /**
    * Display.
    */
   private Display display;

   public OutlinePresenter()
   {
      display = GWT.create(Display.class);
      IDE.getInstance().openView(display.asView());

      IDE.addHandler(UpdateOutlineEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * @see org.eclipse.jdt.client.UpdateOutlineHandler#onUpdateOutline(org.eclipse.jdt.client.UpdateOutlineEvent)
    */
   @Override
   public void onUpdateOutline(UpdateOutlineEvent event)
   {
      display.updateOutline(event.getCompilationUnit());
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }
}
