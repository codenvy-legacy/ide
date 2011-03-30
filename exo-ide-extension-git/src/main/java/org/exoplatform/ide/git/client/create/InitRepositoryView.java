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
package org.exoplatform.ide.git.client.create;

import com.google.gwt.resources.client.ImageResource;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.component.CheckboxItem;
import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
import org.exoplatform.ide.client.framework.ui.gwt.impl.ViewImpl;
import org.exoplatform.ide.git.client.GitClientBundle;

/**
 * UI for initializing the repository.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 24, 2011 10:35:37 AM anya $
 *
 */
public class InitRepositoryView extends ViewImpl implements org.exoplatform.ide.git.client.create.InitRepositoryPresenter.Display
{

   public static final String ID = "ideInitRepositoryView";

   public static final String TYPE = "modal";

   public static final String TITLE = "Initialize local repository";
   
  /*Elements IDs*/
   
   private static final String INIT_BUTTON_ID = "ideInitRepositoryViewInitButton";
   
   private static final String CANCEL_BUTTON_ID = "ideInitRepositoryViewCancelButton";
   
   private static final String WORKDIR_FIELD_ID = "ideInitRepositoryViewWorkDirField";
   
   private static final String BARE_FIELD_ID = "ideInitRepositoryViewBareField";
   
   /*Elements titles*/
   
   private static final String INIT_BUTTON_TITLE = "Init";
   
   private static final String CANCEL_BUTTON_TITLE = "Cancel";
   
   private static final String WORKDIR_FIELD_TITLE = "Work directory";
   
   private static final String BARE_FIELD_TITLE = "Bare repository";
   
   private static final int BUTTON_HEIGHT = 22;
   
   private static final int BUTTON_WIDTH = 90;
   
   private TextField workdirField;
   
   private CheckboxItem bareField;
   
   private IButton initButton;
   
   private IButton cancelButton;

   public InitRepositoryView()
   {
      super(ID, TYPE, TITLE, null, 470, 180);
      setWidth("100%");
      setHeight("100%");
      
      VerticalPanel mainLayout = new VerticalPanel();
      mainLayout.setWidth("100%");
      mainLayout.setHeight("100%");
      mainLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
      mainLayout.setSpacing(7);
      
      workdirField = new TextField(WORKDIR_FIELD_ID, WORKDIR_FIELD_TITLE);
      workdirField.setTitleOrientation(TitleOrientation.TOP);
      workdirField.setWidth(420);
      workdirField.setHeight(22);
      mainLayout.add(workdirField);
     
      bareField = new CheckboxItem(BARE_FIELD_ID, BARE_FIELD_TITLE);
      mainLayout.add(bareField);
      
      addButtonsLayout(mainLayout);
      
      add(mainLayout);
   }

   /**
    * Add buttons to the pointed panel.
    * 
    * @param panel
    */
   private void addButtonsLayout(VerticalPanel panel)
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setHeight(BUTTON_HEIGHT+"px");
      buttonsLayout.setSpacing(5);
      
      initButton = createButton(INIT_BUTTON_ID, INIT_BUTTON_TITLE, GitClientBundle.INSTANCE.ok(), GitClientBundle.INSTANCE.okDisabled());
      cancelButton = createButton(CANCEL_BUTTON_ID, CANCEL_BUTTON_TITLE, GitClientBundle.INSTANCE.cancel(), GitClientBundle.INSTANCE.cancelDisabled());
      
      buttonsLayout.add(initButton);
      buttonsLayout.add(cancelButton);
      
      panel.add(buttonsLayout);
      panel.setCellHorizontalAlignment(buttonsLayout, HasHorizontalAlignment.ALIGN_CENTER);
   }
   
   /**
    * Creates button.
    * 
    * @param id button's id
    * @param title button's title
    * @param icon button's normal icon
    * @param disabledIcon button's icon in disabled state
    * @return {@link IButton}
    */
   private IButton createButton(String id, String title, ImageResource icon, ImageResource disabledIcon)
   {
      IButton button = new IButton(title);
      button.setID(id);
      button.setIcon(icon.getURL(), disabledIcon.getURL());
      button.setHeight(BUTTON_HEIGHT);
      button.setWidth(BUTTON_WIDTH);
      return button;
   }

   /**
    * @see org.exoplatform.ide.git.client.create.InitRepositoryPresenter.Display#getBareValue()
    */
   @Override
   public HasValue<Boolean> getBareValue()
   {
      return bareField;
   }

   /**
    * @see org.exoplatform.ide.git.client.create.InitRepositoryPresenter.Display#getWorkDirValue()
    */
   @Override
   public HasValue<String> getWorkDirValue()
   {
      return workdirField;
   }

   /**
    * @see org.exoplatform.ide.git.client.create.InitRepositoryPresenter.Display#getInitButton()
    */
   @Override
   public HasClickHandlers getInitButton()
   {
      return initButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.create.InitRepositoryPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }
}
