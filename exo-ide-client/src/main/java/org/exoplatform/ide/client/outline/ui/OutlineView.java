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
package org.exoplatform.ide.client.outline.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class OutlineView extends ViewImpl implements
   org.exoplatform.ide.client.outline.OutlinePresenter.Display
{

   private static final String ID = "ideOutlineView";

   /**
    * Initial width of this view
    */
   private static final int WIDTH = 250;

   /**
    * Initial height of this view
    */
   private static final int HEIGHT = 450;

   private static final String REFRESHING_MARK =
      " <span title=" + org.exoplatform.ide.client.IDE.IDE_LOCALIZATION_CONSTANT.outlineTitleRefreshingMarkTitle()
         + " style='position: relative; top: 2px' width='20px;'><img width='15px;' src='"
         + UIHelper.getGadgetImagesURL() + GWTLoader.LOADER_PROGRESSIMAGE + "'></img></span>";
   
   private static OutlineViewUiBinder uiBinder = GWT.create(OutlineViewUiBinder.class);

   interface OutlineViewUiBinder extends UiBinder<Widget, OutlineView>
   {
   }

   @UiField
   HTMLPanel outlineDisabledPanel;

   private boolean outlineAvailable = false;

//   @UiField
//   Border outlineTreeGridPanel;

   @UiField
   OutlineTreeGrid outlineTreeGrid;

   private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.outlineTitle();
   
   public OutlineView()
   {
      super(ID, "information", TITLE, new Image(IDEImageBundle.INSTANCE.outline()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
   }

   @Override
   public TreeGridItem<TokenBeenImpl> getOutlineTree()
   {
      return outlineTreeGrid;
   }

   @Override
   public void selectToken(TokenBeenImpl token)
   {
      if (token != null)
      {
         outlineTreeGrid.selectToken(token);
      }
   }

   @Override
   public List<TokenBeenImpl> getSelectedTokens()
   {
      return outlineTreeGrid.getSelectedTokens();
   }

   @Override
   public void setOutlineAvailable(boolean available)
   {
      if (outlineAvailable == available)
      {
         return;
      }

      outlineAvailable = available;

      if (available)
      {
         outlineDisabledPanel. setVisible(false);
         //outlineTreeGridPanel.setVisible(true);
         outlineTreeGrid.setVisible(true);
      }
      else
      {
         outlineTreeGrid.setVisible(false);
         //outlineTreeGridPanel.setVisible(false);
         outlineDisabledPanel.setVisible(true);
      }
   }

   @Override
   public void deselectAllTokens()
   {
      outlineTreeGrid.deselectAllTokens();
   }

   /**
    * Add refrehing outline mark in outline panel title;
    * @see org.exoplatform.ide.client.outline.OutlinePresenter.Display#setRefreshingMarkInTitle()
    */
   public void setRefreshingMarkInTitle()
   {
      if (!asView().getTitle().contains(REFRESHING_MARK))
      {
         asView().setTitle(asView().getTitle() + REFRESHING_MARK);
      }
   }

   /**
    * Remove refrehing outline mark from outline panel title;
    * @see org.exoplatform.ide.client.outline.OutlinePresenter.Display#removeRefreshingMarkFromTitle()
    */
   public void removeRefreshingMarkFromTitle()
   {
      asView().setTitle(asView().getTitle().replace(REFRESHING_MARK, ""));
   }

   public void clearOutlineTree()
   {
      TokenBeenImpl emptyToken = new TokenBeenImpl();
      emptyToken.setSubTokenList(new ArrayList<TokenBeenImpl>());
      
      getOutlineTree().setValue(emptyToken);
   }

}
