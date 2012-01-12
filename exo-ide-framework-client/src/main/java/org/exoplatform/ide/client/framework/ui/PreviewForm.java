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
package org.exoplatform.ide.client.framework.ui;

import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.DOM;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class PreviewForm extends ViewImpl
{

   public static final String ID = "Preview";

   private PreviewFrame frame;

   /**
    * @param eventBus
    */
   public PreviewForm()
   {
      super(ID, ViewType.OPERATION, ID);
      //      setHeight100();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getTitle()
   {
      return "Preview";
   }

   /**
    * @param file
    * @return 
    */
   public void showPreview(String href)
   {
      if (frame != null)
      {
         frame.removeFromParent();
      }
      frame = new PreviewFrame();
      frame.setUrl(href);
      DOM.setElementAttribute(frame.getElement(), "frameborder", "0");

      frame.getElement().setId("eXo-IDE-preview-frame");
      frame.getElement().setAttribute("name", "eXo-IDE-preview-frame");
      frame.setStyleName("");
      frame.setWidth("100%");
      frame.setHeight("100%");

      frame.addLoadHandler(new LoadHandler()
      {

         @Override
         public void onLoad(LoadEvent event)
         {
            setHandler(IFrameElement.as(frame.getElement()));
         }
      });
      add(frame);
   }

   private native void setHandler(Element e)/*-{
                                            var type = "mousedown";
                                            var instance = this;
                                            if(typeof e.contentDocument != "undefined")
                                            {
                                            e.contentDocument.addEventListener(type,function(){instance.@org.exoplatform.ide.client.framework.ui.PreviewForm::activate()();},false);
                                            }
                                            else
                                            {
                                            e.contentWindow.document.attachEvent("on" + type,function(){instance.@org.exoplatform.ide.client.framework.ui.PreviewForm::activate()();});
                                            }
                                            }-*/;

   public String getId()
   {
      return ID;
   }

}
