/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.operation.preview;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.LockableView;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.smartgwt.client.widgets.HTMLPane;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class PreviewForm extends LockableView
{

   private static final String TAB_ID = "Preview";

   private HTMLPane htmlPane;

   private Image image;

   /**
    * @param eventBus
    */
   public PreviewForm(HandlerManager eventBus)
   {
      super(TAB_ID, eventBus, true);
      image = new Image(IDEImageBundle.INSTANCE.preview());
      setHeight100();
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
    * @return the image
    */
   public Image getImage()
   {
      return image;
   }

   /**
    * @param file
    * @return 
    */
   public void showPreview(String href)
   {
      if (htmlPane != null)
      {
         htmlPane.removeFromParent();
         htmlPane.destroy();
      }
      htmlPane = new HTMLPane();

      //String fileURL = Configuration.getInstance().getContext() + "/jcr" + path;
      String iframe =
         "<iframe name=\"eXo-IDE-preview-frame\" id=\"eXo-IDE-preview-frame\" src=\"" + href
            + "\" frameborder=0 width=\"100%\" height=\"100%\" style=\"overflow:visible;\">";
      iframe += "<p>Your browser does not support iframes.</p>";
      iframe += "</iframe>";

      htmlPane.setContents(iframe);
      //      addChild(htmlPane);
      addMember(htmlPane);

      new Timer()
      {

         @Override
         public void run()
         {
            setHandler(IFrameElement.as(Document.get().getElementById("eXo-IDE-preview-frame")));
         }
      }.schedule(1500);

   }

   private native void setHandler(Element e)/*-{
      var type = "mousedown";
      var instance = this;
      if(typeof e.contentDocument != "undefined")
      {
              e.contentDocument.addEventListener(type,function(){instance.@org.exoplatform.ide.client.operation.preview.PreviewForm::activateView()();},false);
      }
      else
      {
         e.contentWindow.document.attachEvent("on" + type,function(){instance.@org.exoplatform.ide.client.operation.preview.PreviewForm::activateView()();});
      }
//      if(typeof e.addEventListener != "undefined")
//      {
//      e.addEventListener(type,function(){instance.@org.exoplatform.ide.client.operation.preview.PreviewForm::activateView()();},false);
//      }
//      else
//      {
//      e.attachEvent("on" + type,function(){instance.@org.exoplatform.ide.client.operation.preview.PreviewForm::activateView()();});
//      }
   }-*/;

   private native Document getIFrameDocument(IFrameElement iframe)/*-{
      return iframe.contentDocument || iframe.contentWindow.document;
   }-*/;

   @Override
   public void onOpenTab()
   {
      super.onOpenTab();
   }

   @Override
   public void onCloseTab()
   {
      super.onCloseTab();
   }

   public String getId()
   {
      return TAB_ID;
   }

}
