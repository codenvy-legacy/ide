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
package org.exoplatform.ide.client.preview.ui;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.PreviewFrame;
import org.exoplatform.ide.client.framework.ui.api.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PreviewHTMLView extends ViewImpl implements
   org.exoplatform.ide.client.preview.PreviewHTMLPresenter.Display
{

   private static final int DEFAULT_WIDTH = 500;

   private static final int DEFAULT_HEIGHT = 300;

   private FlowPanel previewPanel;
   
   private HTML previewDisabledHTML;   
   
   private PreviewFrame previewFrame;
   
   private boolean previewAvailable = true;

   public PreviewHTMLView()
   {
      super(ID, ViewType.OPERATION, "Preview", new Image(IDEImageBundle.INSTANCE.preview()), DEFAULT_WIDTH, DEFAULT_HEIGHT);

      previewPanel = new FlowPanel();     
      if (ViewType.POPUP.equals(getType()))
      {
         previewPanel.setSize("100%", "100%");
         add(previewPanel);
      }
      else
      {
         add(previewPanel, true);
      }
      
      previewDisabledHTML = new HTML();
      previewDisabledHTML.setSize("100%", "100%");
      previewDisabledHTML.setVisible(false);
      previewPanel.add(previewDisabledHTML);

      previewFrame = new PreviewFrame();
      previewFrame.setSize("100%", "100%");
      DOM.setElementAttribute(previewFrame.getElement(), "frameborder", "0");
      DOM.setStyleAttribute(previewFrame.getElement(), "border", "none");
      
      previewFrame.addLoadHandler(new LoadHandler()
      {
         @Override
         public void onLoad(LoadEvent event)
         {
            setHandler(IFrameElement.as(previewFrame.getElement()));
         }
      });
      previewPanel.add(previewFrame);
   }
   
   private native void setHandler(Element e)/*-{
      var type = "mousedown";
      var instance = this;
      if(typeof e.contentDocument != "undefined")
      {
         e.contentDocument.addEventListener(type,
            function() {
               instance.@org.exoplatform.ide.client.preview.ui.PreviewHTMLView::activate()();
            },
         false);
      }
      else
      {
         e.contentWindow.document.attachEvent("on" + type,
            function() {
               instance.@org.exoplatform.ide.client.preview.ui.PreviewHTMLView::activate()();
            }
         );
      }
   }-*/;
   

   @Override
   public void showPreview(String url)
   {
      previewFrame.setUrl(url);
   }

   @Override
   public void setPreviewAvailable(boolean available)
   {
      if (previewAvailable == available)
      {
         return;
      }
      previewAvailable = available;

      if (available)
      {
         previewDisabledHTML.setVisible(false);
         previewFrame.setVisible(true);
      }
      else
      {
         previewFrame.setVisible(false);
         previewDisabledHTML.setVisible(true);
      }
   }

   @Override
   public void setMessage(String message)
   {
      String html = "<table style=\"width:100%; height:100%;\"><tr style=\"vertical-align:top;\"><td style=\"text-align:center;\">" + message + "</td></tr></table>";
      previewDisabledHTML.setHTML(html);
   }

}
