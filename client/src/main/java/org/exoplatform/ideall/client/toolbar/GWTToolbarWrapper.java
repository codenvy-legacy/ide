/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.toolbar;

import org.exoplatform.ideall.client.solution.toolbar.GWTToolbarForm;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.layout.Layout;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GWTToolbarWrapper extends Layout
{

   public GWTToolbarWrapper(HandlerManager eventBus)
   {
      setHeight(32);

      final GWTToolbarForm toolbar = new GWTToolbarForm(eventBus);
      addMember(toolbar);

      addResizedHandler(new ResizedHandler()
      {
         public void onResized(ResizedEvent event)
         {
            toolbar.setWidth(getWidth() + "px");
            simpleHack3rdParentWidth(toolbar.getElement(), getWidth());
         }
      });

   }

   /**
    * In case using Smartgwt over GWT there is some bug.
    * Layout has many nodes in his DOM structure. After updating of the width of layout not all his children will update their width.
    * The simple way is search third parent of toolbar and update his width. 
    * 
    * @param menuElement
    * @param width
    */
   protected void simpleHack3rdParentWidth(Element menuElement, int width)
   {
      try
      {
         Element element = menuElement;
         Element p1 = DOM.getParent(element);
         Element p2 = DOM.getParent(p1);
         Element p3 = DOM.getParent(p2);
         DOM.setStyleAttribute(p3, "width", "" + width + "px");
      }
      catch (Exception exc)
      {
      }
   }

}
