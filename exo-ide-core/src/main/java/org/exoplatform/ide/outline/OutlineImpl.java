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
package org.exoplatform.ide.outline;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import elemental.html.DragEvent;

import org.exoplatform.ide.Resources;
import org.exoplatform.ide.editor.EditorPartPresenter;
import org.exoplatform.ide.editor.TextEditorPartPresenter;
import org.exoplatform.ide.outline.OutlineModel.OutlineModelListener;
import org.exoplatform.ide.part.PartPresenter;
import org.exoplatform.ide.part.PropertyListener;
import org.exoplatform.ide.text.TextUtilities;
import org.exoplatform.ide.text.store.LineInfo;
import org.exoplatform.ide.texteditor.api.TextEditorPartDisplay;
import org.exoplatform.ide.texteditor.selection.SelectionModel.CursorListener;
import org.exoplatform.ide.tree.NodeRenderer;
import org.exoplatform.ide.tree.Tree;
import org.exoplatform.ide.tree.Tree.Listener;
import org.exoplatform.ide.tree.TreeNodeElement;

/**
 * Default implementation of {@link OutlinePresenter}
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class OutlineImpl implements OutlinePresenter
{

   public interface Display extends IsWidget
   {
      void renderTree();

      void rootChanged(CodeBlock newRoot);

      void setTreeEventHandler(Listener<CodeBlock> listener);

      void selectAndExpand(CodeBlock block);
   }

   private Display display;

   private TextEditorPartDisplay editor;

   private final OutlineModel model;

   private CodeBlockDataAdapter dataAdapter;

   private CodeBlock blockToSync;

   private boolean thisCursorMove;

   /**
    * 
    */
   public OutlineImpl(Resources resources, OutlineModel model, NodeRenderer<CodeBlock> renderer,
      TextEditorPartDisplay editor, TextEditorPartPresenter editorPresenter)
   {
      this.model = model;
      this.editor = editor;
      dataAdapter = new CodeBlockDataAdapter();
      display = new OutlineView(resources, dataAdapter, renderer);
      editorPresenter.addPropertyListener(new PropertyListener()
      {

         @Override
         public void propertyChanged(PartPresenter source, int propId)
         {
            if (EditorPartPresenter.PROP_INPUT == propId)
            {
               bind();
            }
         }
      });
      model.setListener(new OutlineModelListener()
      {

         @Override
         public void rootUpdated()
         {
            display.renderTree();
         }

         @Override
         public void rootChanged(CodeBlock newRoot)
         {
            display.rootChanged(newRoot);
         }

      });
   }

   /**
    * 
    */
   private void bind()
   {
      display.setTreeEventHandler(new Listener<CodeBlock>()
      {

         @Override
         public void onNodeAction(TreeNodeElement<CodeBlock> node)
         {
            thisCursorMove = true;
            CodeBlock data = node.getData();
            editor.getSelection().setCursorPosition(data.getOffset());
         }

         @Override
         public void onNodeClosed(TreeNodeElement<CodeBlock> node)
         {
         }

         @Override
         public void onNodeContextMenu(int mouseX, int mouseY, TreeNodeElement<CodeBlock> node)
         {
         }

         @Override
         public void onNodeDragStart(TreeNodeElement<CodeBlock> node, DragEvent event)
         {
         }

         @Override
         public void onNodeDragDrop(TreeNodeElement<CodeBlock> node, DragEvent event)
         {
         }

         @Override
         public void onNodeExpanded(TreeNodeElement<CodeBlock> node)
         {
         }

         @Override
         public void onRootContextMenu(int mouseX, int mouseY)
         {
         }

         @Override
         public void onRootDragDrop(DragEvent event)
         {
         }
      });

      editor.getSelection().getCursorListenerRegistrar().add(new CursorListener()
      {

         @Override
         public void onCursorChange(LineInfo lineInfo, int column, boolean isExplicitChange)
         {
            if (thisCursorMove)
            {
               thisCursorMove = false;
               return;
            }
            if(model.getRoot() == null)
            {
               return;
            }
            int number = lineInfo.number();
            final int offset = TextUtilities.getOffset(editor.getDocument(), number, column);
            blockToSync = null;
            Tree.iterateDfs(model.getRoot(), dataAdapter, new org.exoplatform.ide.tree.Tree.Visitor<CodeBlock>()
            {

               @Override
               public boolean shouldVisit(CodeBlock node)
               {
                  if (offset + 1 > node.getOffset() && offset - 1 < node.getOffset() + node.getLength())
                  {
                     return true;
                  }
                  else
                     return false;
               }

               @Override
               public void visit(CodeBlock node, boolean willVisitChildren)
               {
                  blockToSync = node;
               }

            });
            if (blockToSync != null)
            {
               if (!CodeBlock.ROOT_TYPE.equals(blockToSync.getType()))
               {
                  display.selectAndExpand(blockToSync);
                  return;
               }
            }
         }
      });

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void go(HasWidgets container)
   {
      container.add(display.asWidget());
   }

}
