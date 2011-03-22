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
package org.exoplatform.ide.client.outline;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;

import org.exoplatform.gwtframework.editor.api.TextEditor;
import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.View;
import org.exoplatform.ide.client.framework.ui.ViewType;
import org.exoplatform.ide.client.framework.ui.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.editor.api.Editor;

import java.util.List;

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
public class OutlineForm extends View implements OutlinePresenter.Display
{
   private static final String OUTLINE_TREE_GRID_ID = "ideOutlineTreeGrid";

   public static final String ID = "ideOutlineForm";

   private Image OUTLINE_TAB_ICON = new Image(IDEImageBundle.INSTANCE.outline());

   private HandlerManager eventBus;

   private OutlinePresenter presenter;

   private OutlineTreeGrid<Token> treeGrid;

   public OutlineForm(HandlerManager bus, Editor activeTextEditor, File activeFile)
   {
      super(ID, bus);
      setTitle("Outline");
      setType(ViewType.OUTLINE);
      setImage(OUTLINE_TAB_ICON);

      eventBus = bus;

      createTreeGrid();

      presenter = new OutlinePresenter(eventBus, activeTextEditor, activeFile);
      presenter.bindDisplay(this);

   }

   private void createTreeGrid()
   {
      treeGrid = new OutlineTreeGrid<Token>(OUTLINE_TREE_GRID_ID);
      ScrollPanel treeWrapper = new ScrollPanel(treeGrid);
      treeWrapper.setSize("100%", "100%");
      addMember(treeWrapper);
   }

   public TreeGridItem<Token> getOutlineTree()
   {
      return treeGrid;
   }

   public void selectToken(Token token)
   {
      if (token != null)
      {
         treeGrid.selectToken(token);
      }
   }

   /**
    * @see com.smartgwt.client.widgets.BaseWidget#onDraw()
    */
   @Override
   protected void onDraw()
   {
      eventBus.fireEvent(new ViewOpenedEvent(ID));
      super.onDraw();
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.View#onDestroy()
    */
   @Override
   protected void onDestroy()
   {
      presenter.destroy();
      super.onDestroy();
   }

   public List<Token> getSelectedTokens()
   {
      return treeGrid.getSelectedTokens();
   }

   public void setFocus()
   {
      focus();
   }
}
