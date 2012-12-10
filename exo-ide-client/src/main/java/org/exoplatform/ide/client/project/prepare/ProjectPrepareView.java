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
package org.exoplatform.ide.client.project.prepare;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ProjectPrepareView extends ViewImpl implements
   ProjectPreparePresenter.Display
{
   private static ProjectPrepareViewUiBinder uiBinder = GWT.create(ProjectPrepareViewUiBinder.class);

   interface ProjectPrepareViewUiBinder extends UiBinder<Widget, ProjectPrepareView>
   {
   }

   public static final String ID = "ideProjectPrepareView";

   public static final String TITLE = "Select project type";

   /**
    * Initial width of this view
    */
   private static final int WIDTH = 350;

   /**
    * Initial height of this view
    */
   private static final int HEIGHT = 100;

   @UiField
   ImageButton okButton;

   @UiField
   ImageButton cancelButton;

   @UiField
   SelectItem projectTypeField;

   public ProjectPrepareView()
   {
      super(ID, "modal", TITLE, new Image(IDEImageBundle.INSTANCE.projectExplorer()), WIDTH, HEIGHT, false);
      setCloseOnEscape(false);
      setCanBeClosed(false);
      add(uiBinder.createAndBindUi(this));
   }

   @Override
   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   @Override
   public HasValue<String> getProjectTypeField()
   {
      return projectTypeField;
   }

   @Override
   public void setProjectTypeValues(String[] types)
   {
      projectTypeField.setValueMap(types);
   }
}
