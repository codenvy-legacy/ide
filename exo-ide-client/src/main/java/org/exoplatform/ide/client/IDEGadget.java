/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client;

import org.exoplatform.gwtframework.commons.util.BrowserResolver;
import org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser;

import com.google.gwt.gadgets.client.DynamicHeightFeature;
import com.google.gwt.gadgets.client.Gadget;
import com.google.gwt.gadgets.client.NeedsDynamicHeight;
import com.google.gwt.gadgets.client.UserPreferences;
import com.google.gwt.gadgets.client.Gadget.ModulePrefs;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Created by The eXo Platform SAS .
 * @author <a href="mailto:dmitry.ndp@exoplatform.com.ua">Dmytro Nochevnov</a>
 * @version $Id: $
*/
@ModulePrefs(title = "IDE", author = "eXo Platform", author_email = "info@exoplatform.com.ua", height = 500, description = "IDE")
public class IDEGadget extends Gadget<UserPreferences> implements NeedsDynamicHeight
{
   private DynamicHeightFeature dynamicHeightFeature;

   @Override
   protected void init(UserPreferences preferences)
   {
      final VerticalPanel idePanel = new VerticalPanel();
      
      RootPanel.get().add(idePanel);
      if (BrowserResolver.CURRENT_BROWSER == Browser.CHROME)
      {
         Utils.expandGadgetHeight();
      }
      else
      {
         Integer h = getFixHeight();
         if (h != null)
         {
            idePanel.setHeight(h + "px");
            dynamicHeightFeature.adjustHeight();
         }
      }
      
      new IDE();
   }

   public void initializeFeature(DynamicHeightFeature feature)
   {
      this.dynamicHeightFeature = feature;
   }

   //Fix gadget height work only in Gatein
   //get height of parent element in the DOM.  
   private static native String expandGadgetHeight() /*-{
       var y = $wnd.parent.document.getElementById("UIGadgetPortlet").parentNode;
       return y.style.height;
    }-*/;

   private Integer getFixHeight()
   {
      String height = expandGadgetHeight();
      Integer newHeight;
      if (height.contains("px"))
      {
         newHeight = Integer.parseInt(height.replace("px", "")) - 30;
         return newHeight;
      }
      else
      {
         return null;
      }
   }

}
