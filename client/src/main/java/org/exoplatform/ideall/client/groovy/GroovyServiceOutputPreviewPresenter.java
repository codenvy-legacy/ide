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
package org.exoplatform.ideall.client.groovy;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPMethod;
import org.exoplatform.gwtframework.commons.wadl.Method;
import org.exoplatform.gwtframework.commons.wadl.Param;
import org.exoplatform.gwtframework.commons.wadl.ParamStyle;
import org.exoplatform.gwtframework.commons.wadl.Resource;
import org.exoplatform.gwtframework.commons.wadl.WadlApplication;
import org.exoplatform.gwtframework.ui.client.dialogs.Dialogs;
import org.exoplatform.ideall.client.component.WadlParameterEntry;
import org.exoplatform.ideall.client.component.WadlParameterEntryListGrid;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.SimpleParameterEntry;
import org.exoplatform.ideall.client.model.groovy.GroovyService;
import org.exoplatform.ideall.client.operation.output.OutputEvent;
import org.exoplatform.ideall.client.operation.output.OutputMessage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class GroovyServiceOutputPreviewPresenter
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
      
      void setSendRequestButtonDisabled(boolean value);
      
      void setPaths(String[] paths);
      
      void setMethods(LinkedHashMap<String, String> methods);
      
      void setMethodFieldValue(String value);
      
   }
   
   private static final String REPLACEMENT_REGEX = "\\{[^/]+}";
   
   private static final String PATH_REGEX = "[^/]+";

   private HandlerManager eventBus;

   private ApplicationContext context;

   private Display display;

   private Handlers handlers;

   private WadlApplication wadlApplication;

   private List<SimpleParameterEntry> headers = new Vector<SimpleParameterEntry>();

   private List<SimpleParameterEntry> queryParams = new Vector<SimpleParameterEntry>();

   private ArrayList<Resource> resourceArray = new ArrayList<Resource>();
   
   private ArrayList<String> methodArray = new ArrayList<String>();
   
   private Resource resource;
   
   public GroovyServiceOutputPreviewPresenter(HandlerManager eventBus, ApplicationContext context, WadlApplication wadlApplication)
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
            if (! pathExists(display.getPathField().getValue()))
            {
               Dialogs.getInstance().showError("Path doesn't exist. Try to select past from the list");
               return;
            }
            display.closeForm();
            sendRequest();
         }
      });

      display.getPathField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (event.getValue() != null && ! "".equals(event.getValue()))
            {
               display.setSendRequestButtonDisabled(false);
               if (pathExists(event.getValue()))
               {
                  setMethodsOnPath(event.getValue());
                  setResourceInfo(event.getValue(), display.getMethodField().getValue());
               } 
            }
            else
            {
               display.setSendRequestButtonDisabled(true);
            }
         }
      });
      
      display.getMethodField().addValueChangeHandler(new ValueChangeHandler<String>()
         {

            public void onValueChange(ValueChangeEvent<String> event)
            {
               setResourceInfo(display.getPathField().getValue(), event.getValue());
            }
         });
      
      Resource res = new Resource();
      res.setPath(wadlApplication.getResources().getBase());

      for (Resource r : wadlApplication.getResources().getResource())
      {
         res.getMethodOrResource().add(r);
      }
      
      initResources(res);
      
      display.setPaths(getPathArray());

   }
   
   protected void sendRequest()
   {
      try
      {
         queryParams = getQueryParams();
         headers = getHeadersParams();
         
         String base = wadlApplication.getResources().getBase();
         String fullPath = base.substring(base.lastIndexOf("/")) 
                         + display.getPathField().getValue();

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

   /**
    * Fills query and header parameters in form
    * 
    * @param resource resource
    * @param method method
    */
   private void fillParameters(Resource resource, Method method)
   {
      display.getParametersQueryListGrid().setValue(new ArrayList<WadlParameterEntry>());
      display.getParametersHeaderListGrid().setValue(new ArrayList<WadlParameterEntry>());

      List<WadlParameterEntry> itemsQuery = new ArrayList<WadlParameterEntry>();
      List<WadlParameterEntry> itemsHeader = new ArrayList<WadlParameterEntry>();
      
      
      if (! "GET".equals(method.getName()) && ! "POST".equals(method.getName()))
      {
         itemsHeader.add(new WadlParameterEntry(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "", method.getName()));
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

   /**
    * Set resource info by path to resource and method name
    * 
    * @param path path to resource
    * @param methodName name of method
    */
   private void setResourceInfo(String path, String methodName)
   {
      resource = findResource(path, methodName);
      
      Method method = findMethod(resource, methodName);
      
      fillParameters(resource, method);
      
      if (HTTPMethod.GET.equals(method.getName())
               || HTTPMethod.DELETE.equals(method.getName())
               || HTTPMethod.HEAD.equals(method.getName())
               || HTTPMethod.OPTIONS.equals(method.getName()))
      {
         display.setBodyDisabled(true);
      }
      else
      {
         display.setBodyDisabled(false);
      }

      if (method.getRequest() != null)
      {
         if (method.getRequest().getRepresentation().size() != 0)
         {
            display.getRequestMediaTypeField().setValue(method.getRequest().getRepresentation().get(0).getMediaType());
         }
         else
         {
            display.getRequestMediaTypeField().setValue("");
         }
      }
      else
      {
         display.getRequestMediaTypeField().setValue("");
      }

      if (method.getResponse() != null)
      {
         if (method.getResponse().getRepresentationOrFault().size() != 0)
         {
            display.getResponseMediaTypeField().setValue(
               method.getResponse().getRepresentationOrFault().get(0).getMediaType());
         }
         else
         {
            display.getResponseMediaTypeField().setValue("");
         }
      }
      else
      {
         display.getResponseMediaTypeField().setValue("");
      }

   }
   
   /**
    * Searches resource in resourceArray.
    * 
    * @param path path to resource
    * @param method method of resource
    * @return {@link Resource} if found, null if not found
    */
   private Resource findResource(String path, String method)
   {
      for (Resource res : resourceArray)
      {
         if (path.equals(res.getPath()) 
                  || path.matches(getPathRegex(res)))
          for (Object obj : res.getMethodOrResource())
          {
             if ((obj instanceof Method) && ((Method)obj).getName().equals(method))
                return res;
          }
      }
      return null;
   }
   
   /**
    * Replaces all path parameters with regex string
    * in order to match input path with existing paths
    * 
    * @param resource resource
    * @return String
    */
   private String getPathRegex(Resource resource)
   {
      return resource.getPath().replaceAll(REPLACEMENT_REGEX, PATH_REGEX);
   }
   
   /**
    * Extracts paths from resource array and put them into String array.
    * 
    * @return Array of {@link String}
    */
   private String[] getPathArray() 
   {
      String[] pathArray = new String[resourceArray.size()];
      for (int i = 0; i < resourceArray.size(); i++)
      {
         pathArray[i] = resourceArray.get(i).getPath();
      }
      return pathArray;
   }
   
   /**
    * Finds method in resource by name.
    * 
    * @param resource resource
    * @param methodName name of method to find
    * @return {@link Method} if found, null if not found
    */
   private Method findMethod(Resource resource, String methodName)
   {
      for (Object obj : resource.getMethodOrResource())
      {
         if ((obj instanceof Method) && methodName.equals(((Method)obj).getName()))
         {
            return (Method) obj;
         }
      }
      return null;
   }
   
   /**
    * Finds methods, corresponding to path
    * and initializes the method field.
    * 
    * If old method field value belongs to new method array,
    * then it stays, otherwise first value from method array
    * sets to method field
    * 
    * @param path path to resource
    */
   private void setMethodsOnPath(String path)
   {
      String oldMethodName = display.getMethodField().getValue();
      
      methodArray.clear();
      for (Resource resource : resourceArray)
      {
         if (path.equals(resource.getPath())
                  || path.matches(getPathRegex(resource)))
            for (Object obj : resource.getMethodOrResource())
            {
               if (obj instanceof Method)
               {
                  methodArray.add(((Method)obj).getName());
               }
            }
      }
      
      LinkedHashMap<String, String> methods = new LinkedHashMap<String, String>();
      for (int i = 0; i < methodArray.size(); i++)
      {
         methods.put(methodArray.get(i), methodArray.get(i));
      }
      display.setMethods(methods);
      
      for (String methodName : methodArray)
         if (oldMethodName.equals(methodName))
         {
            display.setMethodFieldValue(oldMethodName);
            return;
         }
      display.setMethodFieldValue(methodArray.get(0));
   }
   
   /**
    * Finds recursively all resources in resource and 
    * add them to resourceArray
    * 
    * @param resource resource
    */
   private void initResources(Resource resource)
   {
      if (!resource.getPath().equals(wadlApplication.getResources().getBase()))
      {
         resourceArray.add(resource);
      }
      for (Object res : resource.getMethodOrResource())
      {
         if (res instanceof Resource)
            initResources((Resource)res);
      }
   }
   
   /**
    * Returns true if path exists in opened file.
    *  
    * @param path path to search
    * 
    * @return boolean
    */
   private boolean pathExists(String path)
   {
      for (Resource resource : resourceArray)
      {
         if (path.equals(resource.getPath()) || path.matches(getPathRegex(resource)))
         {
            return true;
         }
      }
      return false;
   }
}
