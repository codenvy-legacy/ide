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
package org.exoplatform.ide.extension.java.jdi.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.ChangeValueEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.ChangeValueHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.UpdateVariableValueInTreeEvent;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerInfo;
import org.exoplatform.ide.extension.java.jdi.shared.UpdateVariableRequest;
import org.exoplatform.ide.extension.java.jdi.shared.Value;
import org.exoplatform.ide.extension.java.jdi.shared.Variable;

/**
 * Presenter for change value view.
 * The view must implement {@link ChangeValuePresenter.Display} interface and pointed in Views.gwt.xml file.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ChangeValuePresenter.java Apr 28, 2012 9:47:01 AM azatsarynnyy $
 *
 */
public class ChangeValuePresenter implements ChangeValueHandler, ViewClosedHandler
{

   public interface Display extends IsView
   {
      /**
       * Get change button handler.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getChangeButton();

      /**
       * Get cancel button handler.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getCancelButton();

      /**
       * Get expression field value.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getExpression();

      /**
       * Change the enable state of the change button.
       * 
       * @param isEnable enabled or not
       */
      void setChangeButtonEnable(boolean isEnable);

      /**
       * Give focus to expression field.
       */
      void focusInExpressionField();
   }

   /**
    * The display.
    */
   private Display display;

   /**
    * Variable whose value need to change.
    */
   private Variable variable;

   /**
    * Connected debugger information.
    */
   private DebuggerInfo debuggerInfo;

   public ChangeValuePresenter()
   {
      IDE.addHandler(ChangeValueEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * Bind display (view) with presenter.
    */
   public void bindDisplay()
   {
      display.getChangeButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doChangeValue();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getExpression().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean isExpressionFieldNotEmpty = (event.getValue() != null && !event.getValue().trim().isEmpty());
            display.setChangeButtonEnable(isExpressionFieldNotEmpty);
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.java.jdi.client.events.ChangeValueHandler#onChangeValue(org.exoplatform.ide.extension.java.jdi.client.events.ChangeValueEvent)
    */
   @Override
   public void onChangeValue(ChangeValueEvent event)
   {
      variable = event.getVariable();
      debuggerInfo = event.getDebuggerInfo();

      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();

         IDE.getInstance().openView(display.asView());

         display.setChangeButtonEnable(false);
         display.focusInExpressionField();
      }
   }

   /**
    * Changes the variable value.
    */
   private void doChangeValue()
   {
      UpdateVariableRequest request =
         new UpdateVariableRequestImpl(variable.getVariablePath(), display.getExpression().getValue());
      try
      {
         DebuggerClientService.getInstance().setValue(debuggerInfo.getId(), request, new AsyncRequestCallback<String>()
         {

            @Override
            protected void onSuccess(String result)
            {
               updateVariableValueInList();
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.eventBus().fireEvent(new ExceptionThrownEvent(exception));
            }
         });
      }
      catch (RequestException e)
      {
         IDE.eventBus().fireEvent(new ExceptionThrownEvent(e));
      }

      IDE.getInstance().closeView(display.asView().getId());
   }

   /**
    * Updates variable value in variables panel.
    */
   private void updateVariableValueInList()
   {
      AutoBean<Value> autoBean = DebuggerExtension.AUTO_BEAN_FACTORY.create(Value.class);
      AutoBeanUnmarshaller<Value> unmarshaller = new AutoBeanUnmarshaller<Value>(autoBean);
      try
      {
         DebuggerClientService.getInstance().getValue(debuggerInfo.getId(), variable,
            new AsyncRequestCallback<Value>(unmarshaller)
            {

               @Override
               protected void onSuccess(Value result)
               {
                  if (result != null)
                  {
                     IDE.fireEvent(new UpdateVariableValueInTreeEvent(variable, result));
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.eventBus().fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.eventBus().fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

}
