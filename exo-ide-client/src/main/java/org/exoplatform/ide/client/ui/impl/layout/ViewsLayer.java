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

package org.exoplatform.ide.client.ui.impl.layout;

import org.exoplatform.gwtframework.ui.client.Resizeable;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.ui.impl.Layer;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RequiresResize;
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

   public class ViewContainer extends AbsolutePanel implements Resizeable
   {

      private View view;

      public ViewContainer(View view)
      {
         this.view = view;
         setSize("100px", "100px");
         getElement().getStyle().setOverflow(Overflow.HIDDEN);

         if (view instanceof Widget)
         {
            add((Widget)view);
         }
      }

      @Override
      public void resize(int width, int height)
      {
         setSize(width + "px", height + "px");
         if (view instanceof Resizeable)
         {
            ((Resizeable)view).resize(width, height);
         }
         else if (view instanceof RequiresResize)
         {
            ((RequiresResize)view).onResize();
         }
      }

   }

   public ViewsLayer()
   {
      super("views");
   }

   public Widget addView(View view)
   {
      ViewContainer container = new ViewContainer(view);
      add(container, -1000, -1000);
      return container;
   }

}
