package org.exoplatform.ide.client.projectExplorer;

import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import elemental.html.DragEvent;

import org.exoplatform.ide.AppContext;
import org.exoplatform.ide.client.projectExplorer.ProjectExplorerPresenter.Display;
import org.exoplatform.ide.client.projectExplorer.ProjectExplorerPresenter.Listener;
import org.exoplatform.ide.resources.model.Resource;
import org.exoplatform.ide.tree.Tree;
import org.exoplatform.ide.tree.TreeNodeElement;

public class ProjectTreeView implements Display, IsWidget
{

   protected Tree<Resource> tree;

   protected Listener presenterListener;

   @Inject
   public ProjectTreeView(AppContext appContext)
   {
      tree = Tree.create(appContext);
   }

   @Override
   public String getSelectedFileName()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public Widget asWidget()
   {
      HTML h = new HTML();
      h.getElement().appendChild((Node)tree.getView().getElement());
      return h;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void setItems(Resource resource)
   {
      tree.getModel().setRoot(resource);
      tree.renderTree();
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void registerListener(final Listener listener)
   {
      presenterListener = listener;
      tree.setTreeEventHandler(new org.exoplatform.ide.tree.Tree.Listener<Resource>()
      {

         @Override
         public void onNodeAction(TreeNodeElement<Resource> node)
         {
            listener.onNodeAction(node.getData());
         }

         @Override
         public void onNodeClosed(TreeNodeElement<Resource> node)
         {
         }

         @Override
         public void onNodeContextMenu(int mouseX, int mouseY, TreeNodeElement<Resource> node)
         {
         }

         @Override
         public void onNodeDragStart(TreeNodeElement<Resource> node, DragEvent event)
         {
         }

         @Override
         public void onNodeDragDrop(TreeNodeElement<Resource> node, DragEvent event)
         {
         }

         @Override
         public void onNodeExpanded(TreeNodeElement<Resource> node)
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
   }
}
