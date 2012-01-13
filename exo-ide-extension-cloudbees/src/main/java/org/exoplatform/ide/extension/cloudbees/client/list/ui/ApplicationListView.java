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
package org.exoplatform.ide.extension.cloudbees.client.list.ui;

import com.google.gwt.event.dom.client.HasClickHandlers;

import com.google.gwt.uibinder.client.UiField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientBundle;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.client.list.ApplicationListPresenter.Display;
import org.exoplatform.ide.extension.cloudbees.client.list.HasApplicationListActions;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Sep 21, 2011 evgen $
 * 
 */
public class ApplicationListView extends ViewImpl implements Display
{

   private static ApplicationListViewUiBinder uiBinder = GWT.create(ApplicationListViewUiBinder.class);

   interface ApplicationListViewUiBinder extends UiBinder<Widget, ApplicationListView>
   {
   }

   @UiField
   ImageButton okButton;

   @UiField
   ApplicationListGrid applicationGrid;

   public ApplicationListView()
   {
      super(ID, ViewType.POPUP, CloudBeesExtension.LOCALIZATION_CONSTANT.appListViewTitle(), new Image(
         CloudBeesClientBundle.INSTANCE.appList()), 775, 230);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.list.ApplicationListPresenter.Display#getOkButton()
    */
   @Override
   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.list.ApplicationListPresenter.Display#getAppListGrid()
    */
   @Override
   public HasApplicationListActions getAppListGrid()
   {
      return applicationGrid;
   }

}
