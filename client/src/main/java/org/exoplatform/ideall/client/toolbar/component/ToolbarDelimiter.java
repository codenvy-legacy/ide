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
package org.exoplatform.ideall.client.toolbar.component;

import org.exoplatform.ideall.client.Images;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ToolbarDelimiter extends Composite implements ToolbarItem
{

   public static interface Style
   {

      public final static String DELIMITER_LEFT = "exo-toolbar16Delimiter_Left";

      public final static String DELIMITER_LEFT_HIDDEN = "exo-toolbar16Delimiter_LeftHidden";

      public final static String DELIMITER_RIGHT = "exo-toolbar16Delimiter_Right";

      public final static String DELIMITER_RIGHT_HIDDEN = "exo-toolbar16Delimiter_RightHidden";

   }

   private SimplePanel simplePanel;

   private boolean rightDocking;

   private boolean visible;

   public ToolbarDelimiter(boolean rightDocking)
   {
      this.rightDocking = rightDocking;

      simplePanel = new SimplePanel();
      initWidget(simplePanel);
      setVisible();
      DOM.setInnerHTML(simplePanel.getElement(), "<img src=\"" + Images.imageUrl + "../eXoStyle/toolbar/delimeter.png"
         + "\" />");
   }

   public boolean isVisible()
   {
      return visible;
   }

   public void setVisible()
   {
      visible = true;
      simplePanel.setStyleName(rightDocking ? Style.DELIMITER_RIGHT : Style.DELIMITER_LEFT);
   }

   public void setHidden()
   {
      visible = false;
      simplePanel.setStyleName(rightDocking ? Style.DELIMITER_RIGHT_HIDDEN : Style.DELIMITER_LEFT_HIDDEN);
   }

}
