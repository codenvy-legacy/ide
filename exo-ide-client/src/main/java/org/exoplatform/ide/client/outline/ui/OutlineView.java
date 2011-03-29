/*
 * Copyright (C) 2010 eXo Platform SAS.
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

import java.util.List;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.gwt.impl.ViewImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

/**
 * Form for displaying code outline.
 * 
 * Contains OutlineTreeGrid.
 * 
 * If file or text editor doesn't have outline,
 * this form must be closed.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class OutlineView extends ViewImpl implements org.exoplatform.ide.client.outline.OutlinePresenter.Display
{
   private static final String OUTLINE_TREE_GRID_ID = "ideOutlineTreeGrid";

   private static Image OUTLINE_TAB_ICON = new Image(IDEImageBundle.INSTANCE.outline());

   private FlowPanel outlinePanel;

   private OutlineTreeGrid<TokenBeenImpl> treeGrid;

   private HTML outlineDisabledHTML;

   private boolean outlineAvailable = false;

   public OutlineView()
   {
      super(ID, "information", "Outline", OUTLINE_TAB_ICON);

      outlinePanel = new FlowPanel();
      add(outlinePanel, true);

      outlineDisabledHTML =
         new HTML(
            "<table style=\"width:100%; height:100%;\"><tr style=\"vertical-align:top;\"><td style=\"text-align:center;\">An outline is not available.</td></tr></table>");
      outlineDisabledHTML.setSize("100%", "100%");
      outlinePanel.add(outlineDisabledHTML);

      treeGrid = new OutlineTreeGrid<TokenBeenImpl>(OUTLINE_TREE_GRID_ID);
      treeGrid.setSize("100%", "100%");
      treeGrid.setVisible(false);
      DOM.setStyleAttribute(treeGrid.getElement(), "zIndex", "0");
      outlinePanel.add(treeGrid);
   }

   public TreeGridItem<TokenBeenImpl> getOutlineTree()
   {
      return treeGrid;
   }

   public void selectToken(TokenBeenImpl token)
   {
      if (token != null)
      {
         treeGrid.selectToken(token);
      }
   }

   public List<TokenBeenImpl> getSelectedTokens()
   {
      return treeGrid.getSelectedTokens();
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
         outlineDisabledHTML.setVisible(false);
         treeGrid.setVisible(true);
      }
      else
      {
         treeGrid.setVisible(false);
         outlineDisabledHTML.setVisible(true);
      }
   }

   //   public void setFocus()
   //   {
   //      activate();
   //   }

}
