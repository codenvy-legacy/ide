/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.groovy;

import java.util.List;
import java.util.Vector;

import org.exoplatform.gwt.commons.client.Handlers;
import org.exoplatform.gwt.commons.rest.HTTPHeader;
import org.exoplatform.gwt.commons.rest.HTTPMethod;
import org.exoplatform.gwt.commons.smartgwt.dialogs.Dialogs;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.SimpleParameterEntry;
import org.exoplatform.ideall.client.model.configuration.Configuration;
import org.exoplatform.ideall.client.model.groovy.GroovyService;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class RestServiceOutputPreviewPresenter implements HttpMethodChangedHandler
{

   public interface Display
   {

      void closeForm();

      HasValue<String> getGroovyScriptURLField();

      HasValue<String> getHttpMethod();

      HasValue<String> getRequestBody();

      HasValue<List<SimpleParameterEntry>> getHttpHeaders();

      HasValue<List<SimpleParameterEntry>> getQueryParams();

      void setHttpHeaders(List<SimpleParameterEntry> headers);
      
      void setQueryParams(List<SimpleParameterEntry> params);

      HasClickHandlers getSendRequestButton();

      HasClickHandlers getCancelButton();

      HasClickHandlers getAddHeaderButton();

      HasClickHandlers getDeleteHeaderButton();

      HasClickHandlers getAddQueryParamButton();

      HasClickHandlers getDeleteQueryParamButton();

      void addNewHeader();

      void deleteSelectedHeader();

      void addNewQueryParam();

      void deleteSelectedQueryParam();

      void setHttpMethods(String[] methods);
      
      HasSelectionHandlers<SimpleParameterEntry> getHttHeadersListGridSelectable();
      
      HasSelectionHandlers<SimpleParameterEntry> getQuereParameterListGridSelectable();
      
      void enableDeleteHeaderButton();
      
      void disableDeleteHeaderButton();
      
      void enableDeleteQueryParameterButton();
      
      void disableDeleteQueryParameterButton();

   }

   private HandlerManager eventBus;

   private ApplicationContext context;

   private List<SimpleParameterEntry> headers = new Vector<SimpleParameterEntry>();
   
   private List<SimpleParameterEntry> queryParams = new Vector<SimpleParameterEntry>();

   private Display display;

   private Handlers handlers;

   private SimpleParameterEntry selectedHeader;
   
   private SimpleParameterEntry selectedQueryParam;

   public RestServiceOutputPreviewPresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);

   }

   public void destroy()
   {

      handlers.removeHandlers();
   }

   public void bindDisplay(Display d)
   {
      display = d;

      handlers.addHandler(HttpMethodChangedEvent.TYPE, this);
      
      display.disableDeleteHeaderButton();
      
      display.disableDeleteQueryParameterButton();
     
      display.setHttpHeaders(headers);
      
      display.setQueryParams(queryParams);

      display.getAddHeaderButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent arg0)
         {
            display.addNewHeader();
         }
      });

      
      display.getHttHeadersListGridSelectable().addSelectionHandler(new SelectionHandler<SimpleParameterEntry>()
      {
         public void onSelection(SelectionEvent<SimpleParameterEntry> event)
         {
            selectedHeader = event.getSelectedItem();
            display.enableDeleteHeaderButton();
         }
      });
      
      display.getDeleteHeaderButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            headers.remove(selectedHeader);  
            display.disableDeleteHeaderButton();
            display.deleteSelectedHeader();
         }
      });
      
      
      display.getAddQueryParamButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            display.addNewQueryParam();
         }
      });

      display.getDeleteQueryParamButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            queryParams.remove(selectedQueryParam);  
            display.disableDeleteQueryParameterButton();
            display.deleteSelectedQueryParam();
         }
      });
      
      
      display.getQuereParameterListGridSelectable().addSelectionHandler(new SelectionHandler<SimpleParameterEntry>()
         {
            public void onSelection(SelectionEvent<SimpleParameterEntry> event)
            {
               selectedQueryParam = event.getSelectedItem();
               display.enableDeleteQueryParameterButton();
            }
         });

      display.setHttpMethods(getHttpMethods());

      display.getHttpMethod().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         public void onValueChange(ValueChangeEvent<String> event)
         {
            eventBus.fireEvent(new HttpMethodChangedEvent(event.getValue()));
         }
      });
      

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            display.closeForm();
         }
      });

      display.getSendRequestButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            sendRequest();
         }
      });

      String url = context.getTestGroovyScriptURL();
      if (url == null)
      {
         url = Configuration.getInstance().getContext();
      }

      display.getGroovyScriptURLField().setValue(url);
   }

   protected void sendRequest()
   {
      if ("".equals(display.getGroovyScriptURLField().getValue()))
      {
         Dialogs.showError("URL field can not be empty!");
         return;
      }

      context.setTestGroovyScriptURL(display.getGroovyScriptURLField().getValue());
      
      GroovyService.getInstance().getOutput(display.getGroovyScriptURLField().getValue(),
         display.getHttpMethod().getValue(),headers,queryParams, display.getRequestBody().getValue());
      display.closeForm();
   }

   private String[] getHttpMethods()
   {
      return new String[]{HTTPMethod.GET, HTTPMethod.POST, HTTPMethod.DELETE, HTTPMethod.PROPFIND,
         HTTPMethod.PROPPATCH, HTTPMethod.PUT, HTTPMethod.SEARCH, HTTPMethod.HEAD, HTTPMethod.CHECKIN,
         HTTPMethod.CHECKOUT, HTTPMethod.COPY, HTTPMethod.LOCK, HTTPMethod.MOVE, HTTPMethod.UNLOCK, HTTPMethod.OPTIONS};
   }

   public void onHttpMethodChanged(HttpMethodChangedEvent event)
   {
      if (!event.getHttpMethod().equals(HTTPMethod.GET) && !event.getHttpMethod().equals(HTTPMethod.POST)) 
      {
         SimpleParameterEntry entry;
         boolean flag = false;
         for (int i = 0; i < headers.size(); i++)
         {
            entry = headers.get(i);  
            if (entry.getName().equals(HTTPHeader.X_HTTP_METHOD_OVERRIDE))
            {
               entry.setValue(event.getHttpMethod());
               headers.set(i, entry);
               flag = true;
            }
         }
         if (!flag) headers.add(new SimpleParameterEntry(HTTPHeader.X_HTTP_METHOD_OVERRIDE, event.getHttpMethod()));
      } 
      else 
      {
         for (int i = 0; i < headers.size(); i++)
         {
            SimpleParameterEntry entry = headers.get(i);  
            if (entry.getName().equals(HTTPHeader.X_HTTP_METHOD_OVERRIDE))
            {
               headers.remove(i);
               break;
            }
         }
      }
      display.setHttpHeaders(headers);
   }
   
   

      
}
