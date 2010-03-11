/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.wadl;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPMethod;
import org.exoplatform.gwtframework.commons.wadl.Method;
import org.exoplatform.gwtframework.commons.wadl.Param;
import org.exoplatform.gwtframework.commons.wadl.ParamStyle;
import org.exoplatform.gwtframework.commons.wadl.Resource;
import org.exoplatform.gwtframework.commons.wadl.WadlApplication;
import org.exoplatform.ideall.client.component.WadlParameterEntry;
import org.exoplatform.ideall.client.component.WadlParameterEntryListGrid;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.SimpleParameterEntry;
import org.exoplatform.ideall.client.model.groovy.GroovyService;
import org.exoplatform.ideall.client.operation.output.OutputEvent;
import org.exoplatform.ideall.client.operation.output.OutputMessage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class WadlServiceGetPresenter
{
   public interface Display
   {

      void closeForm();

      HasClickHandlers getSendRequestButton();

      HasClickHandlers getCancelButton();

      HasValue<String> getPathField();

      HasValue<String> getMethodField();

      HasValue<String> getRequestMediaTypeField();

      HasValue<String> getResponseMediaTypeField();

      WadlParameterEntryListGrid getParametersQueryListGrid();

      HasValue<String> getRequestBody();

      WadlParameterEntryListGrid getParametersHeaderListGrid();
      
      void setBodyDisabled(boolean value);
      
      void setPaths(String[] paths);
      
      void setMethods(String[] methods);
      
   }

   private HandlerManager eventBus;

   private ApplicationContext context;

   private Display display;

   private Handlers handlers;

   private WadlApplication wadlApplication;

   private List<SimpleParameterEntry> headers = new Vector<SimpleParameterEntry>();

   private List<SimpleParameterEntry> queryParams = new Vector<SimpleParameterEntry>();

   private boolean isMaySendRequest = false;
   
   private ArrayList<String> pathArray = new ArrayList<String>();
   
   private ArrayList<Resource> resourceArray = new ArrayList<Resource>();
   
   private ArrayList<String> methodArray = new ArrayList<String>();
   
   public WadlServiceGetPresenter(HandlerManager eventBus, ApplicationContext context, WadlApplication wadlApplication)
   {
      this.eventBus = eventBus;
      this.context = context;
      this.wadlApplication = wadlApplication;

      handlers = new Handlers(eventBus);

   }

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

      display.getSendRequestButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            if (isMaySendRequest)
            {
               display.closeForm();
               sendRequest();
            }
         }
      });

      display.getPathField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (isPathExists(event.getValue()))
               onPathValueChanged(event.getValue());
         }
      });
      
      display.getMethodField().addValueChangeHandler(new ValueChangeHandler<String>()
         {

            public void onValueChange(ValueChangeEvent<String> event)
            {
               //eventBus.fireEvent(new HttpMethodChangedEvent(event.getValue()));
               setResourceInfo(display.getPathField().getValue(), event.getValue());
            }
         });
      
      Resource res = new Resource();
      res.setPath(wadlApplication.getResources().getBase());

      for (Resource r : wadlApplication.getResources().getResource())
      {
         res.getMethodOrResource().add(r);
      }
      
      initResourcesAndPaths(res);
      
      display.setPaths(pathArray.toArray(new String[pathArray.size()]));

   }

   protected void sendRequest()
   {
      try
      {
         queryParams = getQueryParams();
         headers = getHeadersParams();

         String fullPath = "/rest" + display.getPathField().getValue();

         GroovyService.getInstance().getOutput(fullPath, display.getMethodField().getValue(), headers,
            queryParams, display.getRequestBody().getValue());
      }
      catch (IllegalArgumentException e)
      {
         eventBus.fireEvent(new OutputEvent(e.getMessage(), OutputMessage.Type.ERROR));
      }
   }

   private List<SimpleParameterEntry> getHeadersParams() //throws IllegalWadlArgumentException
   {

      List<SimpleParameterEntry> headersParam = new ArrayList<SimpleParameterEntry>();
      for (WadlParameterEntry p : display.getParametersHeaderListGrid().getValue())
      {
         if (!p.getValue().equals(""))
         {
            headersParam.add(p);
         }
         else
         {
            throw new IllegalArgumentException("Parameter " + p.getName() + ", not have value");
         }
      }

      return headersParam;
   }

   private List<SimpleParameterEntry> getQueryParams()
   {
      List<SimpleParameterEntry> query = new ArrayList<SimpleParameterEntry>();//display.getParametersQueryListGrid().getValue();
      for (WadlParameterEntry p : display.getParametersQueryListGrid().getValue())
      {
         if (!p.getValue().equals(""))
         {
            query.add(p);
         }
         else
            throw new IllegalArgumentException("Parameter " + p.getName() + ", not have value");
      }

      return query;
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   private void fillPathParameters(Resource resource)
   {
      display.getParametersQueryListGrid().setValue(new ArrayList<WadlParameterEntry>());
      display.getParametersHeaderListGrid().setValue(new ArrayList<WadlParameterEntry>());

      List<WadlParameterEntry> itemsQuery = new ArrayList<WadlParameterEntry>();
      List<WadlParameterEntry> itemsHeader = new ArrayList<WadlParameterEntry>();
      
      if (resource.getMethodOrResource().get(0) instanceof Method)
      {
         Method m = (Method)resource.getMethodOrResource().get(0);
         if (! "GET".equals(m.getName()) && ! "POST".equals(m.getName()))
        	 itemsHeader.add(new WadlParameterEntry(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "", m.getName()));
      }

      if (resource.getParam().size() != 0)
      {

         for (Param p : resource.getParam())
         {

            if (p.getStyle() == ParamStyle.QUERY)
            {
               itemsQuery.add(new WadlParameterEntry(p.getName(), p.getType().getLocalName(), ""));
            }

            if (p.getStyle() == ParamStyle.HEADER)
            {
               itemsHeader.add(new WadlParameterEntry(p.getName(), p.getType().getLocalName(), ""));
            }
         }
      }

      //for child Method element 
      if (resource.getMethodOrResource().size() != 0)
      {

         for (Object j : resource.getMethodOrResource())
         {
            if (j instanceof Method)
            {
               Method m = (Method)j;

               if (m.getRequest() != null)
               {

                  for (Param par : m.getRequest().getParam())
                  {
                     if (par.getStyle() == ParamStyle.QUERY)
                     {
                        itemsQuery.add(new WadlParameterEntry(par.getName(), par.getType().getLocalName(), ""));
                     }
                     else
                     {
                    	itemsHeader.add(new WadlParameterEntry(par.getName(), par.getType().getLocalName(), ""));
                     }
                  }
               }
            }
         }
      }

      display.getParametersHeaderListGrid().setValue(itemsHeader);
      display.getParametersHeaderListGrid().getFields()[0].setCanEdit(false);
      display.getParametersHeaderListGrid().getFields()[1].setCanEdit(false);

      display.getParametersQueryListGrid().setValue(itemsQuery);
      display.getParametersQueryListGrid().getFields()[0].setCanEdit(false);
      display.getParametersQueryListGrid().getFields()[1].setCanEdit(false);

   }

   private void initResourcesAndPaths(Resource resource)
   {
      if (!resource.getPath().equals(wadlApplication.getResources().getBase()))
      {
         pathArray.add(resource.getPath());
         resourceArray.add(resource);
      }
      for (Object res : resource.getMethodOrResource())
      {
         if (res instanceof Resource)
            initResourcesAndPaths((Resource)res);
      }
   }
   
   private boolean isPathExists(String path)
   {
      for (Resource resource : resourceArray)
         if (path.equals(resource.getPath()))
            return true;
      
      return false;
   }
   
   private void onPathValueChanged(String path)
   {
      methodArray.clear();
      for (Resource resource : resourceArray)
      {
         if (path.equals(resource.getPath()))
            for (Object obj : resource.getMethodOrResource())
            {
               if (obj instanceof Method)
               {
                  methodArray.add(((Method)obj).getName());
               }
            }
      }
      display.setMethods(methodArray.toArray(new String[methodArray.size()]));
      display.getMethodField().setValue(methodArray.get(0));
      
      setResourceInfo(path, display.getMethodField().getValue());
   }
   
   private void setResourceInfo(String path, String method)
   {
      Resource resource = findResource(path, method);
      
      fillPathParameters(resource);

      if (resource.getMethodOrResource().get(0) instanceof Method)
      {
         Method m = (Method)resource.getMethodOrResource().get(0);
         
         //you can add check for other methods, where request body is disabled
         if (HTTPMethod.GET.equals(display.getMethodField().getValue())
             || HTTPMethod.DELETE.equals(display.getMethodField().getValue())
             || HTTPMethod.HEAD.equals(display.getMethodField().getValue())
             || HTTPMethod.OPTIONS.equals(display.getMethodField().getValue()))
          display.setBodyDisabled(true);
         else
          display.setBodyDisabled(false);

         if (m.getRequest() != null)
         {
            if (m.getRequest().getRepresentation().size() != 0)
            {
               display.getRequestMediaTypeField().setValue(m.getRequest().getRepresentation().get(0).getMediaType());
            }
            else
               display.getRequestMediaTypeField().setValue("");
         }
         else
            display.getRequestMediaTypeField().setValue("");

         if (m.getResponse() != null)
         {
            if (m.getResponse().getRepresentationOrFault().size() != 0)
            {
               display.getResponseMediaTypeField().setValue(
                  m.getResponse().getRepresentationOrFault().get(0).getMediaType());
            }
            else
               display.getResponseMediaTypeField().setValue("");
         }
         else
            display.getResponseMediaTypeField().setValue("");
         
         isMaySendRequest = true;
      }
   }
   
   private Resource findResource(String path, String method)
   {
      for (Resource res : resourceArray)
      {
         if (path.equals(res.getPath()))
            for (Object obj : res.getMethodOrResource()) 
            {
               if ((obj instanceof Method) && ((Method)obj).getName().equals(method) )
                  return res;
            }
      }
      return null;
   }
}
