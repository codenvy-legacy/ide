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
package org.exoplatform.ide.client.debug;

import java.util.List;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.util.ImageFactory;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ShowImagesView extends ViewImpl implements org.exoplatform.ide.client.debug.ShowImagesPresenter.Display
{

   public static final String ID = "ideShowImagesView";

   private static final int WIDTH = 450;

   private static final int HEIGHT = 200;

   private static ShowImagesViewUiBinder uiBinder = GWT.create(ShowImagesViewUiBinder.class);

   interface ShowImagesViewUiBinder extends UiBinder<Widget, ShowImagesView>
   {
   }

   @UiField
   ImageButton closeButton;

   @UiField
   Grid imagesGrid;

   public ShowImagesView()
   {
      super(ID, "information", "Images", new Image(IDEImageBundle.INSTANCE.about()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
   }

   @Override
   public HasClickHandlers getOkButton()
   {
      return closeButton;
   }

   @Override
   public void updateImageList()
   {
      List<String> imageNames = ImageFactory.getImageNames();
      imagesGrid.resize(imageNames.size() + 1, 3);

      int row = 0;
      for (String imageName : imageNames)
      {
         Image image = ImageFactory.getImage(imageName);
         Image disabledImage = ImageFactory.getDisabledImage(imageName);

         imagesGrid.getRowFormatter().getElement(row).getStyle().setProperty("height", "20px");

         imagesGrid.setWidget(row, 0, image);
         imagesGrid.getCellFormatter().getElement(row, 0).getStyle().setProperty("width", "20px");
         imagesGrid.setWidget(row, 1, disabledImage);
         imagesGrid.getCellFormatter().getElement(row, 1).getStyle().setProperty("width", "20px");
         imagesGrid.setText(row, 2, imageName);

         row++;
      }

      String div = "<div style=\"width:1px; height:1px; \"></div>";
      
      imagesGrid.setHTML(row, 0, div);
      imagesGrid.setHTML(row, 1, div);
      imagesGrid.setHTML(row, 2, div);

   }

}
