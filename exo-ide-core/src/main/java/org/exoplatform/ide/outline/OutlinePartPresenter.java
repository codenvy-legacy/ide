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
package org.exoplatform.ide.outline;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.core.event.ActivePartChangedEvent;
import org.exoplatform.ide.core.event.ActivePartChangedHandler;
import org.exoplatform.ide.editor.TextEditorPartPresenter;
import org.exoplatform.ide.part.AbstractPartPresenter;

/**
 * Part presenter for Outline.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class OutlinePartPresenter extends AbstractPartPresenter implements ActivePartChangedHandler
{

   public interface OutlinePartView extends IsWidget
   {
      HasWidgets getContainer();

      void showNoOutline();
   }

   private final OutlinePartView view;

   private TextEditorPartPresenter activePart;

   /**
    * 
    */
   @Inject
   public OutlinePartPresenter(OutlinePartView view, EventBus eventBus)
   {
      this.view = view;
      eventBus.addHandler(ActivePartChangedEvent.TYPE, this);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getTitle()
   {
      return "Outline";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ImageResource getTitleImage()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getTitleToolTip()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void go(HasWidgets container)
   {
      container.add(view.asWidget());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onActivePartChanged(ActivePartChangedEvent event)
   {
      if (event.getActivePart() instanceof TextEditorPartPresenter)
      {
         if (activePart != event.getActivePart())
         {
            activePart = (TextEditorPartPresenter)event.getActivePart();
            if (activePart.getOutline() != null)
            {
               view.getContainer().clear();
               activePart.getOutline().go(view.getContainer());
            }
            else
            {
               view.showNoOutline();
            }
         }
      }
   }

}
