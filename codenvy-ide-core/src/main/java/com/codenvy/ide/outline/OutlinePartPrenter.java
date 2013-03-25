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
package com.codenvy.ide.outline;

import com.codenvy.ide.api.ui.part.AbstractPartPresenter;

import com.codenvy.ide.api.outline.OutlinePart;
import com.codenvy.ide.core.event.ActivePartChangedEvent;
import com.codenvy.ide.core.event.ActivePartChangedHandler;
import com.codenvy.ide.editor.TextEditorPartPresenter;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;


/**
 * Part presenter for Outline.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
@Singleton
public class OutlinePartPrenter extends AbstractPartPresenter implements ActivePartChangedHandler, OutlinePart
{

   public interface OutlinePartView extends IsWidget
   {
      AcceptsOneWidget getContainer();

      void showNoOutline();
   }

   private final OutlinePartView view;

   private TextEditorPartPresenter activePart;

   /**
    * 
    */
   @Inject
   public OutlinePartPrenter(OutlinePartView view, EventBus eventBus)
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
   public void go(AcceptsOneWidget container)
   {
      container.setWidget(view);
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
