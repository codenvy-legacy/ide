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
package org.exoplatform.ide.client.project.deploy;

import com.google.gwt.user.client.ui.FlowPanel;

import com.google.gwt.user.client.ui.Composite;

import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeployProjectToPaasView.java Dec 1, 2011 4:45:59 PM vereshchaka $
 * 
 */
public class DeployProjectToPaasView extends ViewImpl implements DeployProjectToPaasPresenter.Display
{
   private static final String ID = "DeployNewProjectView";

   private static final String TITLE = "Deploy project to PaaS";

   private static final int HEIGHT = 345;

   private static final int WIDTH = 550;

   interface CreateProjectViewUiBinder extends UiBinder<Widget, DeployProjectToPaasView>
   {
   }

   @UiField
   SelectItem selectPaasField;

   @UiField
   ImageButton finishButton;

   @UiField
   ImageButton backButton;

   @UiField
   ImageButton cancelButton;

   @UiField
   FlowPanel paasPanel;

   /**
    * UIBinder instance
    */
   private static CreateProjectViewUiBinder uiBinder = GWT.create(CreateProjectViewUiBinder.class);

   public DeployProjectToPaasView()
   {
      super(ID, ViewType.POPUP, TITLE, null, WIDTH, HEIGHT, false);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.exoplatform.ide.client.project.deploy.DeployProjectToPaasPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.client.project.deploy.DeployProjectToPaasPresenter.Display#getBackButton()
    */
   @Override
   public HasClickHandlers getBackButton()
   {
      return backButton;
   }

   /**
    * @see org.exoplatform.ide.client.project.deploy.DeployProjectToPaasPresenter.Display#getFinishButton()
    */
   @Override
   public HasClickHandlers getFinishButton()
   {
      return finishButton;
   }

   /**
    * @see org.exoplatform.ide.client.project.deploy.DeployProjectToPaasPresenter.Display#getSelectPaasField()
    */
   @Override
   public HasValue<String> getSelectPaasField()
   {
      return selectPaasField;
   }

   /**
    * @see org.exoplatform.ide.client.project.deploy.DeployProjectToPaasPresenter.Display#setPaasValueMap(java.lang.String[])
    */
   @Override
   public void setPaasValueMap(String[] values)
   {
      selectPaasField.setValueMap(values);
   }

   /**
    * @see org.exoplatform.ide.client.project.deploy.DeployProjectToPaasPresenter.Display#setPaas(com.google.gwt.user.client.ui.Composite)
    */
   @Override
   public void setPaas(Composite composite)
   {
      if (paasPanel.getWidgetCount() > 0)
      {
         paasPanel.remove(0);
      }
      paasPanel.add(composite);
   }

   /**
    * @see org.exoplatform.ide.client.project.deploy.DeployProjectToPaasPresenter.Display#hidePaas()
    */
   @Override
   public void hidePaas()
   {
      if (paasPanel.getWidgetCount() > 0)
      {
         paasPanel.remove(0);
      }
   }

}
