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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.api.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.git.client.GitClientBundle;
import org.exoplatform.ide.git.client.GitExtension;

/**
 * View to perform merge operation.
 * Must be pointed in Views.gwt.xml
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jul 20, 2011 3:05:46 PM anya $
 *
 */
public class MergeView extends ViewImpl implements MergePresenter.Display
{
   private static final String ID = "MergeView";
   
   private static final String MERGE_BUTTON_ID = "MergeViewMergeButton";
   
   private static final String CANCEL_BUTTON_ID = "MergeViewCancelButton";
   
   private static final int WIDTH = 430;

   private static final int HEIGHT = 250;

   interface MergeViewUiBinder extends UiBinder<Widget, MergeView>
   {
   }

   /**
    * Reference tree.
    */
   @UiField
   RefTree refTree;

   /**
    * Merge button.
    */
   @UiField
   ImageButton mergeButton;

   /**
    * Cancel button.
    */
   @UiField
   ImageButton cancelButton;

   /**
    * UI binder for this view.
    */
   private static MergeViewUiBinder uiBinder = GWT.create(MergeViewUiBinder.class);

   public MergeView()
   {
      super(ID, ViewType.MODAL, GitExtension.MESSAGES.mergeTitle(), new Image(GitClientBundle.INSTANCE.merge()), WIDTH,
         HEIGHT);
      add(uiBinder.createAndBindUi(this));
      mergeButton.setButtonId(MERGE_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.git.client.merge.MergePresenter.Display#getMergeButton()
    */
   @Override
   public HasClickHandlers getMergeButton()
   {
      return mergeButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.merge.MergePresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.merge.MergePresenter.Display#getRefTree()
    */
   @Override
   public TreeGridItem<Reference> getRefTree()
   {
      return refTree;
   }

   /**
    * @see org.exoplatform.ide.git.client.merge.MergePresenter.Display#enableMergeButton(boolean)
    */
   @Override
   public void enableMergeButton(boolean enable)
   {
      mergeButton.setEnabled(enable);
   }

   /**
    * @see org.exoplatform.ide.git.client.merge.MergePresenter.Display#getSelectedReference()
    */
   @Override
   public Reference getSelectedReference()
   {
      return refTree.getSelectedItem();
   }
}
