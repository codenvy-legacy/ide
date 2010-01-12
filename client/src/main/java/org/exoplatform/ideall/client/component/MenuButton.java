/**
 * Copyright (C) 2003-2009 eXo Platform SAS.
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

package org.exoplatform.ideall.client.component;

import org.exoplatform.gwt.commons.smartgwt.component.event.ClickHandlerImpl;
import org.exoplatform.ideall.client.Images;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.MouseDownEvent;
import com.smartgwt.client.widgets.events.MouseDownHandler;
import com.smartgwt.client.widgets.events.MouseOutEvent;
import com.smartgwt.client.widgets.events.MouseOutHandler;
import com.smartgwt.client.widgets.events.MouseOverEvent;
import com.smartgwt.client.widgets.events.MouseOverHandler;
import com.smartgwt.client.widgets.events.MouseStillDownEvent;
import com.smartgwt.client.widgets.events.MouseStillDownHandler;
import com.smartgwt.client.widgets.events.MouseUpEvent;
import com.smartgwt.client.widgets.events.MouseUpHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Anna Zhuleva</a>
 * @version $Id:
 */
public class MenuButton extends VLayout implements HasClickHandlers
{

   private final int DEFAULT_BUTTON_WIDTH = 24;

   private final int DEFAULT_BUTTON_HEIGHT = 24;

   private final int DEFAULT_IMAGE_WIDTH = 16;

   private final int DEFAULT_IMAGE_HEIGHT = 16;

   private String icon;

   private int imageWidth;

   private int imageHeigth;

   private int buttonWidth;

   private int buttonHeight;

   private String title;

   private boolean showPrompt = true;

   // states
   private boolean showOver = true;

   private boolean showDown = true;

   private VLayout backGround;

   private HLayout imageLayout;

   private Img backGroundImage;

   private Img image;

   public HandlerRegistration addClickHandler(ClickHandler clickHandler)
   {
      return super.addClickHandler(new ClickHandlerImpl(clickHandler));
   }

   public MenuButton(String icon, String title)
   {
      this.icon = icon;
      this.buttonHeight = DEFAULT_BUTTON_HEIGHT;
      this.buttonWidth = DEFAULT_BUTTON_WIDTH;
      this.imageWidth = DEFAULT_IMAGE_WIDTH;
      this.imageHeigth = DEFAULT_IMAGE_HEIGHT;
      this.title = title;

      setWidth(buttonWidth);
      setHeight(buttonHeight);
      
      backGround = createBackground();
      backGround.setCursor(Cursor.POINTER);
      
      imageLayout = createImageLayout();
      imageLayout.setCursor(Cursor.POINTER);
      
      backGround.addChild(imageLayout);
      backGround.setCursor(Cursor.POINTER);
      
      if (showPrompt && (title != null))
      {
         //imageLayout.setTitle(title);
         backGround.setTitle(title);
      }
      
      backGround.setCursor(Cursor.HELP);

      addMember(backGround);

      addButtonHandlers();
   }

   private void addButtonHandlers()
   {

      if (showOver)
      {
         addMouseOverHandler(new MouseOverHandler()
         {

            public void onMouseOver(MouseOverEvent event)
            {
               backGroundImage.setSrc(Images.MenuButton.OVER);
               backGround.redraw();
            }
         });

         addMouseOutHandler(new MouseOutHandler()
         {

            public void onMouseOut(MouseOutEvent event)
            {
               backGroundImage.setSrc(Images.MenuButton.BACKGROUND);
               backGround.redraw();
            }
         });
      }// end showOver

      if (showDown)
      {
         addMouseDownHandler(new MouseDownHandler()
         {

            public void onMouseDown(MouseDownEvent event)
            {
               backGroundImage.setSrc(Images.MenuButton.DOWN);
               backGround.redraw();
            }

         });

         addMouseUpHandler(new MouseUpHandler()
         {

            public void onMouseUp(MouseUpEvent event)
            {
               backGroundImage.setSrc(Images.MenuButton.OVER);
               backGround.redraw();
            }

         });
      } // end showSelected
   }

   private VLayout createBackground()
   {
      VLayout background = new VLayout();
      background.setWidth100();
      background.setHeight100();

      backGroundImage = new Img();
      backGroundImage.setSrc(Images.MenuButton.BACKGROUND);
      backGroundImage.setWidth(buttonWidth);
      backGroundImage.setHeight(buttonHeight);

      background.addChild(backGroundImage);
      return background;
   }

   private HLayout createImageLayout()
   {
      int margin = (int)((buttonWidth - imageWidth) / 2);

      HLayout imageLayout = new HLayout();
      imageLayout.setWidth(imageWidth);
      imageLayout.setHeight(imageHeigth);

      image = new Img();
      image.setSrc(icon);
      image.setWidth(imageWidth);
      image.setHeight(imageHeigth);
      imageLayout.addMember(image);
      imageLayout.setMargin(margin);
      return imageLayout;
   }

   public String getIcon()
   {
      return icon;
   }

   public void setIcon(String icon)
   {
      this.icon = icon;
   }

//   public int getImageWidth()
//   {
//      return imageWidth;
//   }
//
//   public void setImageWidth(int imageWidth)
//   {
//      this.imageWidth = imageWidth;
//   }
//
//   public int getImageHeigth()
//   {
//      return imageHeigth;
//   }
//
//   public void setImageHeigth(int imageHeigth)
//   {
//      this.imageHeigth = imageHeigth;
//   }
//
//   public int getButtonWidth()
//   {
//      return buttonWidth;
//   }
//
//   public void setButtonWidth(int buttonWidth)
//   {
//      this.buttonWidth = buttonWidth;
//   }
//
//   public int getButtonHeight()
//   {
//      return buttonHeight;
//   }
//
//   public void setButtonHeight(int buttonHeight)
//   {
//      this.buttonHeight = buttonHeight;
//   }

}
