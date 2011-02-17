/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.extension.netvibes.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.extension.netvibes.client.event.DeployUwaWidgetEvent;
import org.exoplatform.ide.extension.netvibes.client.event.DeployUwaWidgetHandler;
import org.exoplatform.ide.extension.netvibes.client.model.Categories;
import org.exoplatform.ide.extension.netvibes.client.model.DeployWidget;
import org.exoplatform.ide.extension.netvibes.client.model.Languages;
import org.exoplatform.ide.extension.netvibes.client.model.Regions;
import org.exoplatform.ide.extension.netvibes.client.service.deploy.DeployWidgetService;
import org.exoplatform.ide.extension.netvibes.client.service.deploy.callback.WidgetCategoryCallback;
import org.exoplatform.ide.extension.netvibes.client.service.deploy.callback.WidgetDeployCallback;

import java.util.LinkedHashMap;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 29, 2010 $
 *
 */
public class DeployUwaWidgetPresenter implements DeployUwaWidgetHandler
{
   interface Display
   {
      /**
       * Close view.
       */
      void closeForm();

      /**
       * @return {@link HasClickHandlers} deploy button
       */
      HasClickHandlers getDeployButton();

      /**
       * @return {@link HasClickHandlers} cancel button
       */
      HasClickHandlers getCancelButton();

      /**
       * @return {@link HasClickHandlers} button to go to the next step
       */
      HasClickHandlers getNextStepButton();

      /**
       * @return {@link HasClickHandlers} button to go to the previous step
       */
      HasClickHandlers getPrevStepButton();

      /**
       * @return {@link TextFieldItem} url field
       */
      TextFieldItem getUrlValue();

      /**
       * @return {@link TextFieldItem} secret key field
       */
      TextFieldItem getSecretKey();

      /**
       * @return {@link TextFieldItem} api key field
       */
      TextFieldItem getApiKey();

      /**
       * @return {@link TextFieldItem} user's login field
       */
      TextFieldItem getLogin();

      /**
       * @return {@link TextFieldItem} user's password field
       */
      TextFieldItem getPassword();

      /**
       * @return {@link TextFieldItem} widget's title field
       */
      TextFieldItem getWigdetTitle();

      /**
       * @return {@link TextFieldItem} widget's description field
       */
      TextFieldItem getDescription();

      /**
       * @return {@link TextFieldItem} widget's version field
       */
      TextFieldItem getVersion();

      /**
       * @return {@link TextFieldItem} widget's keyword field
       */
      TextFieldItem getKeywords();

      /**
       * @return {@link TextFieldItem} widget's thumbnail field
       */
      TextFieldItem getThumbnail();

      /**
       * @return {@link TextFieldItem} widget's main language field
       */
      HasValue<String> getLanguage();

      /**
       * @return {@link TextFieldItem} widget's category field
       */
      HasValue<String> getCategory();

      /**
       * @return {@link TextFieldItem} widget's region field
       */
      HasValue<String> getRegion();

      /**
       * Show or hide layout with main information.
       * 
       * @param isShow show or not
       */
      void displayMainInfo(boolean isShow);

      /**
       * Show or hide layout with detailed information.
       * 
       * @param isShow show or not
       */
      void displayDetailsInfo(boolean isShow);

      /**
       * Show or hide layout with private information.
       * 
       * @param isShow
       */
      void displayPrivacyInfo(boolean isShow);

      /**
       * Update the next button state: visible/hidden, enabled/disabled.
       * 
       * @param isVisible is visible or not
       * @param isEnabled is enabled or ont
       */
      void updateNextButtonState(boolean isVisible, boolean isEnabled);

      /**
       * Update the previous button state: visible/hidden, enabled/disabled.
       * 
       * @param isVisible is visible or not
       * @param isEnabled is enabled or ont
       */
      void updatePrevButtonState(boolean isVisible, boolean isEnabled);

      /**
       * Update the deploy button state: visible/hidden, enabled/disabled.
       * 
       * @param isVisible is visible or not
       * @param isEnabled is enabled or ont
       */
      void updateDeployButtonState(boolean isVisible, boolean isEnabled);

      /**
       * Set categories values to display.
       * 
       * @param values categories
       */
      void setCategoryValueMap(LinkedHashMap<String, String> values);

      /**
       * Set regions values to display.
       * 
       * @param values regions
       */
      void setRegionValueMap(LinkedHashMap<String, String> values);

      /**
       * Set languages values to display.
       * 
       * @param values languages
       */
      void setLanguageValueMap(LinkedHashMap<String, String> values);

      /**
       * Checks whether details fields have valid values.
       * 
       * @return boolean valid or not
       */
      boolean isValidDetailsFields();
   }

   /**
    * Event bus.
    */
   public HandlerManager eventBus;

   /**
    * Handlers for this presenter.
    */
   private Handlers handlers;

   /**
    * Display.
    */
   private Display display;

   /**
    * The current step of the getting information about widget.
    */
   private int currentStep = 0;

   /**
    * Available categories.
    */
   private Categories categories;

   /**
    * @param eventBus 
    */
   public DeployUwaWidgetPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      eventBus.addHandler(DeployUwaWidgetEvent.TYPE, this);
   }

   /**
    * @param d 
    */
   public void bindDisplay(Display d)
   {
      display = d;

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            display.closeForm();
         }
      });

      display.getNextStepButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            doNextStep();
         }
      });

      display.getPrevStepButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            doPrevStep();
         }
      });

      display.getDeployButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            doDeploy();
         }
      });

      display.getUrlValue().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean isValid = checkValidUrl(event.getValue());
            display.updateNextButtonState(true, isValid);
         }
      });

      display.getWigdetTitle().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean isNotEmpty = checkRequiredDetailsFullFilled();
            display.updateNextButtonState(true, isNotEmpty);
         }

      });

      display.getDescription().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean isNotEmpty = checkRequiredDetailsFullFilled();
            display.updateNextButtonState(true, isNotEmpty);
         }
      });

      display.getRegion().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean isNotEmpty = checkRequiredDetailsFullFilled();
            display.updateNextButtonState(true, isNotEmpty);
         }
      });

      display.getCategory().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean isNotEmpty = checkRequiredDetailsFullFilled();
            display.updateNextButtonState(true, isNotEmpty);
         }
      });

      display.getLanguage().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean isNotEmpty = checkRequiredDetailsFullFilled();
            display.updateNextButtonState(true, isNotEmpty);
         }
      });

      display.getApiKey().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean isFullFilled = checkPrivacyFieldsFullFilled();
            display.updateDeployButtonState(true, isFullFilled);
         }

      });

      display.getSecretKey().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean isFullFilled = checkPrivacyFieldsFullFilled();
            display.updateDeployButtonState(true, isFullFilled);
         }
      });

      display.getLogin().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean isFullFilled = checkPrivacyFieldsFullFilled();
            display.updateDeployButtonState(true, isFullFilled);
         }
      });

      display.getPassword().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean isFullFilled = checkPrivacyFieldsFullFilled();
            display.updateDeployButtonState(true, isFullFilled);
         }
      });
   }

   /**
    * Check whether all private information is full filled by user.
    * 
    * @return boolean full filled or not
    */
   private boolean checkPrivacyFieldsFullFilled()
   {
      return (display.getApiKey().getValue() != null && !display.getApiKey().getValue().isEmpty()
         && display.getSecretKey().getValue() != null && !display.getSecretKey().getValue().isEmpty()
         && display.getLogin().getValue() != null && !display.getLogin().getValue().isEmpty()
         && display.getPassword().getValue() != null && !display.getPassword().getValue().isEmpty());
   }

   /**
    * Check whether all required information is full filled by user.
    * 
    * @return boolean full filled or not
    */
   private boolean checkRequiredDetailsFullFilled()
   {
      return (display.getWigdetTitle().getValue() != null && display.getDescription().getValue() != null
         && display.getWigdetTitle().getValue().length() > 0 && display.getDescription().getValue().length() > 0 && display
         .isValidDetailsFields());
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.event.DeployUwaWidgetHandler#onDeployUwaWidget(org.exoplatform.ide.client.module.netvibes.event.DeployUwaWidgetEvent)
    */
   public void onDeployUwaWidget(DeployUwaWidgetEvent event)
   {
      bindDisplay(new DeployUwaWidgetForm(eventBus));
      currentStep = 1;
      display.displayMainInfo(true);
      display.updateDeployButtonState(false, false);
      display.updateNextButtonState(true, false);
      display.updatePrevButtonState(false, false);

      display.setLanguageValueMap(Languages.getLanguagesMap());
      display.setRegionValueMap(Regions.getRegionsMap());
   }

   /**
    * Do the next step operation.
    */
   private void doNextStep()
   {
      currentStep++;
      if (currentStep == 2)
      {
         getCategories();
         display.displayMainInfo(false);
         display.displayDetailsInfo(true);
         display.updateDeployButtonState(false, false);
         display.updatePrevButtonState(true, true);
         boolean isNotEmpty = checkRequiredDetailsFullFilled();
         display.updateNextButtonState(true, isNotEmpty);
      }
      else if (currentStep == 3)
      {
         display.displayDetailsInfo(false);
         display.displayPrivacyInfo(true);
         display.updateNextButtonState(false, false);
         display.updatePrevButtonState(true, true);
         boolean isNotEmpty = checkPrivacyFieldsFullFilled();
         display.updateDeployButtonState(true, isNotEmpty);
      }
   }

   /**
    * Do previous step operation.
    */
   private void doPrevStep()
   {
      currentStep--;
      if (currentStep == 1)
      {
         display.displayMainInfo(true);
         display.displayDetailsInfo(false);
         display.updateDeployButtonState(false, false);
         display.updateNextButtonState(true, true);
         display.updatePrevButtonState(false, false);
      }
      else if (currentStep == 2)
      {
         getCategories();
         display.displayPrivacyInfo(false);
         display.displayDetailsInfo(true);
         display.updateDeployButtonState(false, false);
         display.updateNextButtonState(true, true);
         boolean isNotEmpty = checkRequiredDetailsFullFilled();
         display.updateNextButtonState(true, isNotEmpty);
      }
   }

   /**
    * Validates the URL.
    * 
    * @param url url
    * @return true if valid
    */
   private boolean checkValidUrl(String url)
   {
      if (url != null && url.length() > 0)
      {
         //TODO
         return true;
      }
      return false;
   }

   /**
    * Destroy presenter, remove handlers.
    */
   public void destroy()
   {
      handlers.removeHandlers();
   }

   private void getCategories()
   {
      if (categories == null || categories.getCategoryMap().size() < 0)
      {
         DeployWidgetService.getInstance().getCategories(new WidgetCategoryCallback()
         {
            
            @Override
            public void onResponseReceived(Request request, Response response)
            {
               categories = this.getCategories();
               display.setCategoryValueMap(categories.getCategoryMap());
            }
            
            @Override
            public void handleError(Throwable exception)
            {
               String message = "Can not get widget's categories.";
               message += (exception == null) ? "" : "Possible reason: <br>" + exception.getMessage();

               Dialogs.getInstance().showError(message);
            }
         });
      }
      else
      {
         display.setCategoryValueMap(categories.getCategoryMap());
      }
   }

   /**
    * Do deploy operation. 
    * Fill the data bean for deploy and call service method.
    */
   private void doDeploy()
   {
      DeployWidget widget = new DeployWidget();
      widget.setApiKey(display.getApiKey().getValue());
      widget.setCategoryId(display.getCategory().getValue());
      String categoryName =
         (display.getCategory().getValue() != null) ? categories.getCategoryMap().get(display.getCategory().getValue())
            : "";
      widget.setCategoryName(categoryName);
      widget.setDescription(display.getDescription().getValue());
      widget.setKeywords(display.getKeywords().getValue());
      widget.setMainLanguage(display.getLanguage().getValue());
      widget.setRegion(display.getRegion().getValue());
      widget.setSecretKey(display.getSecretKey().getValue());
      widget.setThumbnail(display.getThumbnail().getValue());
      widget.setUrl(display.getUrlValue().getValue());
      widget.setVersion(display.getVersion().getValue());
      widget.setTitle(display.getWigdetTitle().getValue());

      DeployWidgetService.getInstance().deploy(widget, display.getLogin().getValue(), display.getPassword().getValue(),
         new WidgetDeployCallback()
         {
            @Override
            public void onResponseReceived(Request request, Response response)
            {
               display.closeForm();

               OutputMessage.Type responseType =
                  this.getDeployResult().isSuccess() ? OutputMessage.Type.INFO : OutputMessage.Type.ERROR;
               String message =
                  this.getDeployResult().isSuccess() ? "<b>" + this.getDeployWidget().getUrl() + "</b>"
                     + " deployed successfully." : this.getDeployResult().getMessage();
               eventBus.fireEvent(new OutputEvent(message, responseType));
            }
            
            @Override
            public void handleError(Throwable exception)
            {
               eventBus.fireEvent(new ExceptionThrownEvent("Can't deploye widget"));
            }
         });
   }

}
