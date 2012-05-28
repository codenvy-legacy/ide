/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.googleappengine.client.project;

import com.google.gwt.uibinder.client.UiField;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.extension.googleappengine.client.cron.CronGrid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 23, 2012 3:22:35 PM anya $
 * 
 */
public class CronTabPane extends Composite
{
   private static final String UPDATE_CRON_BUTTON_ID = "ideCronTabPaneUpdateCronButton";

   private static CronTabPaneUiBinder uiBinder = GWT.create(CronTabPaneUiBinder.class);

   interface CronTabPaneUiBinder extends UiBinder<Widget, CronTabPane>
   {
   }

   @UiField
   ImageButton updateCronButton;

   @UiField
   CronGrid cronGrid;

   public CronTabPane()
   {
      initWidget(uiBinder.createAndBindUi(this));

      updateCronButton.setButtonId(UPDATE_CRON_BUTTON_ID);
   }

   /**
    * @return the updateCronButton
    */
   public ImageButton getUpdateCronButton()
   {
      return updateCronButton;
   }

   public CronGrid getCronGrid()
   {
      return cronGrid;
   }
}
