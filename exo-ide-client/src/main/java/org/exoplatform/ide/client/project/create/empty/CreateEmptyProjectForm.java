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
package org.exoplatform.ide.client.project.create.empty;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

import java.util.Set;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class CreateEmptyProjectForm extends ViewImpl implements
   org.exoplatform.ide.client.project.create.empty.CreateEmptyProjectPresenter.Display
{

   private static final int WIDTH = 420;

   private static final int HEIGHT = 210;

   private static final String ID = CreateEmptyProjectForm.class.getName();

   private static final String PROJECT_NAME_ID = "CreateProjectFormProjectName";

   private static final String PROJECT_TYPE_ID = "CreateProjectFormProjectType";

   private static final String CREATE_BUTTON_ID = "CreateProjectFormCreateButton";

   private static final String CANCEL_BUTTON_ID = "CreateProjectFormCancelButton";

   @UiField
   TextInput projectName;

   @UiField
   SelectItem projectType;

   @UiField
   ImageButton createButton;

   @UiField
   ImageButton cancelButton;

   private static CreateProjectFormUiBinder uiBinder = GWT.create(CreateProjectFormUiBinder.class);

   interface CreateProjectFormUiBinder extends UiBinder<Widget, CreateEmptyProjectForm>
   {
   }

   public CreateEmptyProjectForm()
   {
      super(ID, ViewType.MODAL, "Create Project", null, WIDTH, HEIGHT);
      setCloseOnEscape(true);
      add(uiBinder.createAndBindUi(this));

      projectName.setName(PROJECT_NAME_ID);
      projectType.setName(PROJECT_TYPE_ID);
      createButton.setButtonId(CREATE_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   @Override
   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   @Override
   public void setProjectType(Set<String> types)
   {
      projectType.setValueMap(types.toArray(new String[0]));
   }

   @Override
   public void setProjectName(String name)
   {
      projectName.setValue(name);
   }

   @Override
   public HasValue<String> getProjectName()
   {
      return projectName;
   }

   @Override
   public HasValue<String> getProjectType()
   {
      return projectType;
   }

}
