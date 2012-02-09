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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.event.ShowAstEvent;
import org.eclipse.jdt.client.event.ShowAstHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 20, 2012 1:28:01 PM evgen $
 */
public class AstPresenter implements ShowAstHandler, ViewClosedHandler, UpdateOutlineHandler
{

   public interface Display extends IsView
   {
      String id = "AstView";

      void drawAst(CompilationUnit cUnit);
   }

   private Display display;

   private CompilationUnit unit;

   /**
    * 
    */
   public AstPresenter(HandlerManager eventBus)
   {
      eventBus.addHandler(ShowAstEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(UpdateOutlineEvent.TYPE, this);

   }

   /** @see org.eclipse.jdt.client.event.ShowAstHandler#onShowAst(org.eclipse.jdt.client.event.ShowAstEvent) */
   @Override
   public void onShowAst(ShowAstEvent event)
   {

      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
      }
      if (unit != null)
         display.drawAst(unit);
   }

   /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent) */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
         display = null;
   }

   /**
    * @see org.eclipse.jdt.client.UpdateOutlineHandler#onUpdateOutline(org.eclipse.jdt.client.UpdateOutlineEvent)
    */
   @Override
   public void onUpdateOutline(UpdateOutlineEvent event)
   {
      unit = event.getCompilationUnit();
   }

}
