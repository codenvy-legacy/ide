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
package org.exoplatform.ideall.plugin.gadget.ui;

import org.exoplatform.ideall.client.framework.ui.TabPanel;
import org.exoplatform.ideall.gadget.GadgetMetadata;

import com.google.gwt.event.shared.HandlerManager;
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

   private String meta;

   private GadgetMetadata metadata;

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
      metadata = gadgetMetadata;
      //      meta = parseMetadata(gadgetMetadata);
   }

   @Override
   public String getTitle()
   {
      return title;
   }

   //   /**
   //    * @param metadata
   //    * @return
   //    */
   //   private String parseMetadata(GadgetMetadata metadata)
   //   {
   //      String src =
   //         "{specUrl: \"" + URL.decode(metadata.getUrl()) + "\",height:" + String.valueOf(metadata.getHeight())
   //            + ",title:\"" + metadata.getTitle() + "\",width:" + String.valueOf(metadata.getWidth()) + ",secureToken:\""
   //            + metadata.getSecurityToken() + "\",view:\"home\",server:\"" + Configuration.getInstance().getGadgetServer() + "\"}";
   //      return src;
   //   }

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
      String url = metadata.getIframeUrl();
      url = url.replace("?container=", "?container=default");
      url = url.replace("&view=", "&view=canvas");
      //TODO Configuration!!!
      url = ""; // Configuration.getInstance().getGadgetServer() + "ifr" + url;
      Frame frame = new Frame(url + "&nocache=1");
      DOM.setElementAttribute(frame.getElement(), "scrolling", "no");
      DOM.setElementAttribute(frame.getElement(), "frameborder", "0");
      frame.setWidth("100%");
      frame.setHeight("100%");
      addMember(frame);

      //      return frame;

      //      Frame frame = new Frame(GWT.getModuleBaseURL() + "gadgets/gadgetcontainer.html#" +meta);
      //      frame.setWidth("100%");
      //      frame.setHeight("100%");
      //      addMember(frame);
      //      DOM.setElementAttribute(frame.getElement(), "id", "framegadget");
      //      DOM.setElementAttribute(frame.getElement(), "name", "framegadget");
      //      DOM.setElementAttribute(frame.getElement(), "frameborder", "0");
   }

   @Override
   public String getId()
   {
      return ID;
   }

}
