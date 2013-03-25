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
package com.codenvy.ide.outline;

import com.codenvy.ide.api.ui.perspective.PartPresenter;
import com.codenvy.ide.api.ui.perspective.PropertyListener;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.outline.CodeBlock;
import com.codenvy.ide.api.outline.OutlineModel;
import com.codenvy.ide.api.outline.OutlineModel.OutlineModelListener;
import com.codenvy.ide.api.outline.OutlinePresenter;
import com.codenvy.ide.editor.EditorPartPresenter;
import com.codenvy.ide.editor.TextEditorPartPresenter;
import com.codenvy.ide.text.TextUtilities;
import com.codenvy.ide.text.store.LineInfo;
import com.codenvy.ide.texteditor.TextEditorViewImpl;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.selection.SelectionModel.CursorListener;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.Tree.Listener;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import elemental.html.DragEvent;


/**
 * Default implementation of {@link OutlinePresenter}
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class OutlineImpl implements OutlinePresenter
{

   public interface OutlineView extends IsWidget
   {
      void renderTree();

      void rootChanged(CodeBlock newRoot);

      void setTreeEventHandler(Listener<CodeBlock> listener);

      void selectAndExpand(CodeBlock block);
   }

   private OutlineView view;

   private TextEditorViewImpl editor;

   private final OutlineModel model;

   private CodeBlockDataAdapter dataAdapter;

   private CodeBlock blockToSync;

   private boolean thisCursorMove;

   /**
    * 
    */
   public OutlineImpl(Resources resources, OutlineModel model,
      TextEditorPartView editor, TextEditorPartPresenter editorPresenter)
   {
      this.model = model;
      this.editor = (TextEditorViewImpl)editor;
      dataAdapter = new CodeBlockDataAdapter();
      view = new OutlineViewImpl(resources, dataAdapter, model.getRenderer());
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
            view.renderTree();
         }

         @Override
         public void rootChanged(CodeBlock newRoot)
         {
            view.rootChanged(newRoot);
         }

      });
   }

   /**
    * 
    */
   private void bind()
   {
      view.setTreeEventHandler(new Listener<CodeBlock>()
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
            Tree.iterateDfs(model.getRoot(), dataAdapter, new Tree.Visitor<CodeBlock>()
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
                  view.selectAndExpand(blockToSync);
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
   public void go(AcceptsOneWidget container)
   {
      container.setWidget(view);
   }

}
