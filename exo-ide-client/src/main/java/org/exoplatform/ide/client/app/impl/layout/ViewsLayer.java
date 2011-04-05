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

package org.exoplatform.ide.client.app.impl.layout;

import org.exoplatform.ide.client.app.impl.Layer;
import org.exoplatform.ide.client.framework.ui.gwt.ViewEx;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ViewsLayer extends Layer
{

   public class ViewContainer extends AbsolutePanel
   {

      public ViewContainer(ViewEx view)
      {
         DOM.setStyleAttribute(getElement(), "width", "100px");
         DOM.setStyleAttribute(getElement(), "height", "100px");
         DOM.setStyleAttribute(getElement(), "overflow", "hidden");

         if (view instanceof Widget)
         {
            add((Widget)view);
         }
      }

   }

   public ViewsLayer()
   {
      super("views");
   }

   public Widget addView(ViewEx view)
   {
      ViewContainer container = new ViewContainer(view);
      add(container);
      return container;
   }

}
