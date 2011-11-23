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
package org.exoplatform.ide.client.restdiscovery.ui;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.Border;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.restdiscovery.ParamExt;
import org.exoplatform.ide.client.restdiscovery.UntypedTreeGrid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * View implementation for org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class RestServicesDiscoveryView extends ViewImpl implements
   org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display
{
   
   /**
    * ID of this view.
    */
   private static final String ID = "ideResrServicesDiscoveryView";   

   /**
    * Initial width of this view
    */
   private static final int INITIAL_WIDTH = 500;

   /**
    * Initial height of this view
    */
   private static final int INITIAL_HEIGHT = 330;
   
   private static final String TITLE = IDE.PREFERENCES_CONSTANT.restServicesDiscoveryTitle();

   /**
    * UIBinder instance
    */
   private static RestServicesDiscoveryViewUiBinder uiBinder = GWT.create(RestServicesDiscoveryViewUiBinder.class);

   interface RestServicesDiscoveryViewUiBinder extends UiBinder<Widget, RestServicesDiscoveryView>
   {
   }

   /**
    * Ok button
    */
   @UiField
   ImageButton okButton;

   /**
    * Rest services tree
    */
   @UiField
   RestServiceTree treeGrid;

   /**
    * Rest service parameters table
    */
   @UiField
   RestServiceParameterListGrid parametersListGrid;

   /**
    * Text field for displaying Path to selected Rest Service
    */
   @UiField
   TextInput pathField;

   /**
    * Text field that displays type of Request
    */
   @UiField
   TextInput requestTypeField;

   /**
    * Test field that displays type of Response
    */
   @UiField
   TextInput responseTypeField;

   /**
    * Border over service parameters table 
    */
   @UiField
   Border parametersListGridContainer;

   /**
    * Creates new instance of this View
    */
   public RestServicesDiscoveryView()
   {
      super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.restServicesDiscovery()),
         INITIAL_WIDTH, INITIAL_HEIGHT);
      setCloseOnEscape(true);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * Get Ok button
    * 
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#getOkButton()
    */
   @Override
   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

   /**
    * Get Rest services tree
    * 
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#getTreeGrid()
    */
   @Override
   public UntypedTreeGrid getTreeGrid()
   {
      return treeGrid;
   }

   /**
    * Get Service parameters table
    * 
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#getParametersListGrid()
    */
   @Override
   public ListGridItem<ParamExt> getParametersListGrid()
   {
      return parametersListGrid;
   }

   /**
    * Get text field for displaying path to selected rest service 
    * 
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#getPathField()
    */
   @Override
   public HasValue<String> getPathField()
   {
      return pathField;
   }

   /**
    * Sets visibility of Response type field
    * 
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#setResponseFieldVisible(boolean)
    */
   @Override
   public void setResponseFieldVisible(boolean visible)
   {
      responseTypeField.setVisible(visible);
   }

   /**
    * Sets enabling of Response type field
    * 
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#setResponseFieldEnabled(boolean)
    */
   @Override
   public void setResponseFieldEnabled(boolean enabled)
   {
      responseTypeField.setEnabled(enabled);
   }

   /**
    * Sets visibility of Request type field
    * 
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#setRequestFieldVisible(boolean)
    */
   @Override
   public void setRequestFieldVisible(boolean visible)
   {
      requestTypeField.setVisible(visible);
   }

   /**
    * Sets enabling of Request type field
    * 
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#setRequestFieldEnabled(boolean)
    */
   @Override
   public void setRequestFieldEnabled(boolean enabled)
   {
      requestTypeField.setEnabled(enabled);
   }

   /**
    * Sets visibility of border over service parameters table
    *  
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#setParametersListGridVisible(boolean)
    */
   @Override
   public void setParametersListGridVisible(boolean visible)
   {
      parametersListGridContainer.setVisible(visible);
   }

   /**
    * Sets enabling of Service parameters table
    * 
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#setParametersListGridEnabled(boolean)
    */
   @Override
   public void setParametersListGridEnabled(boolean enabled)
   {
   }

   /**
    * Get Request type text field
    * 
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#getRequestTypeField()
    */
   @Override
   public HasValue<String> getRequestTypeField()
   {
      return requestTypeField;
   }

   /**
    * Get Response type text field
    * 
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#getResponseTypeField()
    */
   @Override
   public HasValue<String> getResponseTypeField()
   {
      return responseTypeField;
   }

}
