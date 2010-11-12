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
package org.exoplatform.ide.client.module.gadget.ui;

import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.ui.LockableView;
import org.exoplatform.ide.client.module.gadget.service.GadgetMetadata;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Frame;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class GadgetPreviewPane extends LockableView
{

   /**
    * 
    */
   public static final String TITLE = "Gadget Preview";

   private String meta;

   private GadgetMetadata metadata;

   /**
    * 
    */
   public static final String ID = "gadgetpreview";

   private IDEConfiguration configuration;

   /**
    * @param eventBus
    * @param gadgetMetadata
    */
   public GadgetPreviewPane(HandlerManager eventBus, IDEConfiguration configuration, GadgetMetadata gadgetMetadata)
   {
      super(ID, eventBus, true);
      this.configuration = configuration;
      metadata = gadgetMetadata;
   }

   @Override
   public String getTitle()
   {
      return TITLE;
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

   private native String getST()/*-{
      return encodeURIComponent(gadgets.util.getUrlParameters().st);
   }-*/;

   private native String getGadgetParent()/*-{
      return encodeURIComponent(gadgets.util.getUrlParameters().parent);
   }-*/;

   private native boolean isGadget()/*-{
      return (typeof(gadgets) !== "undefined" &&  typeof(gadgets) !== "null");
   }-*/;

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
      url = configuration.getGadgetServer() + "ifr" + url; // Configuration.getInstance().getGadgetServer() + "ifr" + url;
      if (isGadget())
      {
         url = url + "&parent=" + getGadgetParent() + "&nocache=1&st=" + getST();
      }
      else
      {
         url = url + "&parent=" + Location.getHref() + "&nocache=1";
      }
      final Frame frame = new Frame(url);
      DOM.setElementAttribute(frame.getElement(), "scrolling", "no");
      DOM.setElementAttribute(frame.getElement(), "frameborder", "0");
      frame.setWidth("100%");
      frame.setHeight("100%");
      addMember(frame);

      new Timer()
      {

         @Override
         public void run()
         {
            Document doc = getIFrameDocument(IFrameElement.as(frame.getElement()));
            Element body = doc.getBody();
            setHandler(body);
         }
      }.schedule(1000);
   }

   private native void setHandler(Element e)/*-{
      
            var type = "mousedown";
      var instance = this;
      if(typeof e.contentDocument != "undefined")
      {
              e.contentDocument.addEventListener(type,function(){instance.@org.exoplatform.ide.client.module.gadget.ui.GadgetPreviewPane::activateView()();},false);
      }
      else
      {
         e.contentWindow.document.attachEvent("on" + type,function(){instance.@org.exoplatform.ide.client.module.gadget.ui.GadgetPreviewPane::activateView()();});
      }
      
//      var type = "mousedown";
//      var instance = this;     
//      if(typeof e.addEventListener != "undefined")
//      {
//         e.addEventListener(type,function(){instance.@org.exoplatform.ide.client.module.gadget.ui.GadgetPreviewPane::activateView()();},false);
//      }
//      else
//      {
//         e.attachEvent("on" + type,function(){instance.@org.exoplatform.ide.client.module.gadget.ui.GadgetPreviewPane::activateView()();});
//      }
   }-*/;

   private native Document getIFrameDocument(IFrameElement iframe)/*-{
      return iframe.contentDocument || iframe.contentWindow.document;
   }-*/;

   public String getId()
   {
      return ID;
   }

}
