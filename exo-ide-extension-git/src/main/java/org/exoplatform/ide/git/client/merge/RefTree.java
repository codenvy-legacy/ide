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
package org.exoplatform.ide.git.client.merge;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TreeItem;

import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.ide.git.client.GitClientBundle;

/**
 * Tree to display references (local and remote branches, tags).
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 20, 2011 2:37:48 PM anya $
 * 
 */
public class RefTree extends org.exoplatform.gwtframework.ui.client.component.Tree<Reference>
{
   /**
    * Tree node with local branches.
    */
   private TreeItem localBranches;

   /**
    * Tree node with remote branches.
    */
   private TreeItem remoteBranches;

   private static final String REF_TREE_ID = "MergeViewRefTree";

   /**
    * Tree node with tags
    */
   private TreeItem tags;

   public RefTree()
   {
      getElement().setId(REF_TREE_ID);
      localBranches = createNode(new Image(GitClientBundle.INSTANCE.localBanches()), "Local Branches");
      remoteBranches = createNode(new Image(GitClientBundle.INSTANCE.remoteBranches()), "Remote Branches");
      tags = createNode(null, "Tags");

      tree.addItem(localBranches);
      tree.addItem(remoteBranches);
      // TODO when tags are ready tree.addItem(tags);
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.component.Tree#doUpdateValue()
    */
   @Override
   public void doUpdateValue()
   {
      if (value == null)
         return;

      if (Reference.RefType.LOCAL_BRANCH.equals(value.getRefType()))
      {
         localBranches.addItem(createNode(new Image(GitClientBundle.INSTANCE.branch()), value));
      }
      else if (Reference.RefType.REMOTE_BRANCH.equals(value.getRefType()))
      {
         remoteBranches.addItem(createNode(new Image(GitClientBundle.INSTANCE.branch()), value));
      }
      else if (Reference.RefType.TAG.equals(value.getRefType()))
      {
         tags.addItem(createNode(null, value));
      }
   }

   /**
    * Create tree node for the pointed reference.
    * 
    * @param image node's image
    * @param reference reference (branch or tag)
    * @return {@link TreeItem}
    */
   protected TreeItem createNode(Image image, Reference reference)
   {
      HorizontalPanel panel = new HorizontalPanel();
      panel.setSpacing(3);
      Label title = new Label();
      title.setValue(reference.getDisplayName());

      if (image != null)
      {
         panel.add(image);
      }
      panel.add(title);

      TreeItem treeItem = new TreeItem(panel);
      treeItem.setUserObject(reference);
      return treeItem;
   }

   protected TreeItem createNode(Image image, String text)
   {
      HorizontalPanel panel = new HorizontalPanel();
      panel.setSpacing(3);
      Label title = new Label();
      title.setValue(text);

      if (image != null)
      {
         panel.add(image);
      }
      panel.add(title);

      TreeItem treeItem = new TreeItem(panel);
      return treeItem;
   }

   /**
    * Get selected reference.
    * 
    * @return {@link Reference}
    */
   public Reference getSelectedItem()
   {
      if (tree.getSelectedItem() != null)
      {
         return ((Reference)tree.getSelectedItem().getUserObject());
      }
      return null;
   }
}
