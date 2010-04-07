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
package org.exoplatform.ideall.client.operation.preview;

import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.configuration.Configuration;
import org.exoplatform.ideall.client.operation.TabPanel;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class PreviewForm extends TabPanel
{

   private static String TAB_ID = "Preview";

   private ApplicationContext context;

   private HTMLPane htmlPane;

   /**
    * @param eventBus
    */
   public PreviewForm(HandlerManager eventBus, ApplicationContext context)
   {
      super(eventBus, true);
      this.context = context;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getTitle()
   {
      return "<span>" + Canvas.imgHTML(Images.MainMenu.PREVIEW_HTML) + "&nbsp;Preview</span>";
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
      addMember(htmlPane);

      //String fileURL = Configuration.getInstance().getContext() + "/jcr" + path;
      String iframe =
         "<iframe src=\"" + href + "\" frameborder=0 width=\"100%\" height=\"100%\" style=\"overflow:visible;\">";
      iframe += "<p>Your browser does not support iframes.</p>";
      iframe += "</iframe>";
      htmlPane.setContents(iframe);
   }

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

   @Override
   public String getId()
   {
      return "Preview";
   }

}
