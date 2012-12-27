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

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.exoplatform.ide.outline.OutlinePartPresenter.OutlinePartView;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class OutlinePartViewImpl implements OutlinePartView
{

   private SimplePanel container;

   private Label noOutline;

   @Inject
   public OutlinePartViewImpl()
   {
      //TODO extract message constant
      noOutline = new Label("An outline is not available.");
      container = new SimplePanel();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Widget asWidget()
   {
      return container;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void showNoOutline()
   {
      container.clear();
      container.add(noOutline);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AcceptsOneWidget getContainer()
   {
      return container;
   }

}
