/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.gadgets;

import org.exoplatform.ideall.client.model.configuration.Configuration;
import org.exoplatform.ideall.client.model.gadget.GadgetMetadata;
import org.exoplatform.ideall.client.operation.TabPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Frame;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class GadgetPreviewPane extends TabPanel
{

   /**
    * 
    */
   public static final String title = "Gadget Preview";

   /**
    * 
    */
   public static final String ID = "gadgetpreview";

   /**
    * @param eventBus
    * @param gadgetMetadata
    */
   public GadgetPreviewPane(HandlerManager eventBus, GadgetMetadata gadgetMetadata)
   {
      super(eventBus, true);
      setGadgetMetadata(getGadgetScript(gadgetMetadata), Configuration.getInstance().getGadgetServer());
   }

   @Override
   public String getTitle()
   {
      return title;
   }

   /**
    * @param metadata
    * @return
    */
   private String getGadgetScript(GadgetMetadata metadata)
   {
      String src =
         "{specUrl: \"" + URL.decode(metadata.getUrl()) + "\",height:" + String.valueOf(metadata.getHeight())
            + ",title:\"" + metadata.getTitle() + "\",width:" + String.valueOf(metadata.getWidth()) + ",secureToken:\""
            + metadata.getSecurityToken() + "\",view:\"home\"}";
      return src;
   }

   @Override
   public void onCloseTab()
   {
      destroy();
      super.onCloseTab();
   }

   @Override
   public void onOpenTab()
   {
      super.onOpenTab();
      showGadget();
   }

   /**
    * Create iframe. 
    * Gadget will be load here.
    * 
    */
   private void showGadget()
   {
      Frame frame = new Frame(GWT.getModuleBaseURL() + "gadgets/gadgetcontainer.html");
      frame.setWidth("100%");
      frame.setHeight("100%");
      addMember(frame);
      DOM.setElementAttribute(frame.getElement(), "id", "framegadget");
      DOM.setElementAttribute(frame.getElement(), "name", "framegadget");
      DOM.setElementAttribute(frame.getElement(), "frameborder", "0");
   }

   /**
    * Set value in document. 
    * Then iframe with gadget inside onLoad read this value.
    * 
    * @param script
    * @param server
    */
   public native void setGadgetMetadata(String script, String server) /*-{
         var m = eval('(' + script + ')');
         $wnd.metadata = {};
         $wnd.metadata.url = m.specUrl;
         $wnd.metadata.height = m.height;
         $wnd.metadata.width = m.width;
   //       $wnd.metadata.secureToken = 'john.doe:john.doe:appid:cont:url:0';
         $wnd.metadata.secureToken = m.secureToken;
         $wnd.metadata.server = server;
         $wnd.metadata.view = m.view;
       }-*/;

   @Override
   public String getId()
   {
      return ID;
   }
}
