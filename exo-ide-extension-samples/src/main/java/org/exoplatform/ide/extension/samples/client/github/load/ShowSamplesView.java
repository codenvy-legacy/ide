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
package org.exoplatform.ide.extension.samples.client.github.load;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;

import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: GithubSamplesView.java Aug 30, 2011 12:13:08 PM vereshchaka $
 *
 */
public class ShowSamplesView extends ViewImpl implements ShowSamplesPresenter.Display
{
   
   private static final String ID = "GithubSamplesView";
   
   private static final String TITLE = SamplesExtension.LOCALIZATION_CONSTANT.showSamplesTitle();
   
   private static final int HEIGHT = 345;

   private static final int WIDTH = 550;
   
   /**
    * 
    */
   interface GithubSamplesViewUiBinder extends UiBinder<Widget, ShowSamplesView>
   {
   }
   
   /**
    * UIBinder instance
    */
   private static GithubSamplesViewUiBinder uiBinder = GWT.create(GithubSamplesViewUiBinder.class);
   
   @UiField
   SamplesListGrid samplesListGrid;
   
   @UiField
   ImageButton nextButton;
   
   @UiField
   ImageButton cancelButton;
   
   public ShowSamplesView()
   {
      super(ID, ViewType.POPUP, TITLE, null, WIDTH, HEIGHT, false);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.exoplatform.ide.client.ShowSamplesPresenter.samples.GithubSamplesPresenter.Display#getNextButton()
    */
   @Override
   public HasClickHandlers getNextButton()
   {
      return nextButton;
   }

   /**
    * @see org.exoplatform.ide.client.ShowSamplesPresenter.samples.GithubSamplesPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.client.ShowSamplesPresenter.samples.GithubSamplesPresenter.Display#enableNextButton(boolean)
    */
   @Override
   public void enableNextButton(boolean enable)
   {
      nextButton.setEnabled(enable);
   }

   /**
    * @see org.exoplatform.ide.client.ShowSamplesPresenter.samples.GithubSamplesPresenter.Display#getSamplesListGridAlt()
    */
   @Override
   public ListGridItem<ProjectData> getSamplesListGrid()
   {
      return samplesListGrid;
   }

   /**
    * @see org.exoplatform.ide.client.ShowSamplesPresenter.samples.GithubSamplesPresenter.Display#getSelectedItems()
    */
   @Override
   public List<ProjectData> getSelectedItems()
   {
      return samplesListGrid.getSelectedItems();
   }
}
