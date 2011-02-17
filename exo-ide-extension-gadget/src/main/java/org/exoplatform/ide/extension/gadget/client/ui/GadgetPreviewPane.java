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
package org.exoplatform.ide.extension.gadget.client.ui;

import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.ui.LockableView;
import org.exoplatform.ide.client.framework.ui.PreviewFrame;
import org.exoplatform.ide.extension.gadget.client.service.GadgetMetadata;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.HasLoadHandlers;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
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
      final PreviewFrame frame = new PreviewFrame(url);
      DOM.setElementAttribute(frame.getElement(), "scrolling", "no");
      DOM.setElementAttribute(frame.getElement(), "frameborder", "0");
      frame.setWidth("100%");
      frame.setHeight("100%");
      frame.setStyleName("");

      frame.addLoadHandler(new LoadHandler()
      {

         @Override
         public void onLoad(LoadEvent event)
         {
            setHandler(IFrameElement.as(frame.getElement()));
         }
      });

      addMember(frame);
   }

   private native void setHandler(Element e)/*-{
      var type = "mousedown";
      var instance = this;
      if(typeof e.contentDocument != "undefined")
      {
      e.contentDocument.addEventListener(type,function(){instance.@org.exoplatform.ide.extension.gadget.client.ui.GadgetPreviewPane::activateView()();},false);
      }
      else if (typeof e.contentWindow != "undefined")
      {
      e.contentWindow.document.attachEvent("on" + type,function(){instance.@org.exoplatform.ide.extension.gadget.client.ui.GadgetPreviewPane::activateView()();});
      }
   }-*/;

   public String getId()
   {
      return ID;
   }

}
