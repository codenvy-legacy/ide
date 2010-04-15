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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
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
public class GroovyServiceOutputPreviewPresenter
{
   public interface Display
   {

      void closeForm();

      HasClickHandlers getShowUrlButton();

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

      void setShowUrlButtonDisabled(boolean value);

      void setPaths(String[] paths);

      void setMethods(LinkedHashMap<String, String> methods);

      void setMethodFieldValue(String value);

      void setBodyTabEnabled();

      void setBodyTabDisabled();

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

   private Method method;

   private final boolean isSend = true;

   public GroovyServiceOutputPreviewPresenter(HandlerManager eventBus, ApplicationContext context,
      WadlApplication wadlApplication)
   {
      this.eventBus = eventBus;
      this.context = context;
      this.wadlApplication = wadlApplication;

      handlers = new Handlers(eventBus);

   }

   public void bindDisplay(Display d)
   {
      display = d;

      display.getShowUrlButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            String url = wadlApplication.getResources().getBase();
            if (display.getPathField().getValue() != null)
            {
               url += display.getPathField().getValue();
            }
            new GetRestServiceURLForm(eventBus, url);
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
            if (!pathExists(display.getPathField().getValue()))
            {
               Dialogs.getInstance().showError(
                  "Path doesn't exist.<br>" + "May be you try to pass symbol <b>/</b> as parameter value.<br><br>"
                     + "Try to select pass from the list");
               return;
            }
            if (!validatePathParams())
               return;
            //display.closeForm();
            sendRequest();
         }
      });

      display.getPathField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (event.getValue() != null && !"".equals(event.getValue()))
            {
               display.setSendRequestButtonDisabled(false);
               if (pathExists(event.getValue()))
               {
                  setMethodsOnPath(event.getValue());

                  String oldPath = resource == null ? null : resource.getPath();
                  String oldMethodName = method == null ? null : method.getName();

                  resource = findResource(event.getValue(), display.getMethodField().getValue());
                  method = findMethod(resource, display.getMethodField().getValue());

                  //check if it is need to change resource info
                  //If value if path field changed, but path and method stayed the same
                  // there is no need to set resource info again
                  if (!(oldPath != null && oldPath.equals(resource.getPath()) && oldMethodName != null && oldMethodName
                     .equals(method.getName())))
                  {
                     setResourceInfo();
                  }
               }
            }
            else
            {
               resource = null;
               method = null;
               display.setSendRequestButtonDisabled(true);
               display.setMethods(new LinkedHashMap<String, String>());
               setResourceInfo();
            }
         }
      });

      display.getMethodField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         public void onValueChange(ValueChangeEvent<String> event)
         {
            resource = findResource(display.getPathField().getValue(), event.getValue());
            method = findMethod(resource, event.getValue());
            setResourceInfo();
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

   /**
    * Validates path parameter values.
    * 
    * @return true if validation passed and false otherwise
    */
   private boolean validatePathParams()
   {
      String entered = display.getPathField().getValue();
      String path = resource.getPath();
      String[] patterns = path.split(REPLACEMENT_REGEX);
      ArrayList<String> parameters = getParams(entered, patterns);
      for (String param : parameters)
      {
         if (param.contains("?"))
         {
            Dialogs.getInstance().showError(
               "Parameter value <b>" + param + "</b> can not contain symbol <b>?</b><br><br>"
                  + "Set query parameter values in specified form");
            return false;
         }
         else if (param.contains("\\"))
         {
            Dialogs.getInstance().showError("Parameter value <b>" + param + "</b> can not contain symbol <b>\\</b>");
            return false;
         }
      }

      return true;
   }

   /**
    * <p>Returns parameter values for str.</p>
    * 
    * <p>Extracts substrings, which contained in str between patterns.</p>
    * 
    * E.g. to get paramater values from path <code>/rest/hello/{name}/world</code>,
    * you must pass as <code>str</code> your path and as <code>patterns</code> such array:
    * 
    * <pre>
    * {
    *  "/rest/hello/",
    *  "/world"
    * } 
    * <pre>
    * 
    * @param str path
    * @param patterns array of patterns
    * @return {@link ArrayList}
    */
   private ArrayList<String> getParams(String str, String[] patterns)
   {
      ArrayList<String> params = new ArrayList<String>();

      if (str.indexOf(patterns[0]) > 0)
         params.add(str.substring(0, str.indexOf(patterns[0])));

      for (int i = 0; i < patterns.length - 1; i++)
      {
         params.add(str.substring(str.indexOf(patterns[i]) + patterns[i].length(), str.indexOf(patterns[i + 1])));
      }

      String lastPattern = patterns[patterns.length - 1];

      if (str.length() > str.indexOf(lastPattern) + lastPattern.length())
         params.add(str.substring(str.indexOf(lastPattern) + lastPattern.length(), str.length()));

      return params;
   }

   protected void sendRequest()
   {
      try
      {
         queryParams = getQueryParams();
         headers = getHeadersParams();

         String base = wadlApplication.getResources().getBase();
         String fullPath = base.substring(base.lastIndexOf("/")) + display.getPathField().getValue();
         display.closeForm();

         GroovyService.getInstance().getOutput(fullPath, display.getMethodField().getValue(), headers, queryParams,
            display.getRequestBody().getValue());
      }
      catch (IllegalArgumentException e)
      {
         //eventBus.fireEvent(new OutputEvent(e.getMessage(), OutputMessage.Type.ERROR));
         Dialogs.getInstance().showError(e.getMessage());
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
            throw new IllegalArgumentException("Checked parameter '" + p.getName() + "' should have the value!");
         }
      }

      return headersParam;
   }

   private List<SimpleParameterEntry> getQueryParams()
   {
      List<SimpleParameterEntry> query = new ArrayList<SimpleParameterEntry>();//display.getParametersQueryListGrid().getValue();
      for (WadlParameterEntry p : display.getParametersQueryListGrid().getValue())
      {
         if (p.isSend())
         {
            query.add(p);
         }
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

      if (resource == null || method == null)
         return;

      List<WadlParameterEntry> itemsQuery = new ArrayList<WadlParameterEntry>();
      List<WadlParameterEntry> itemsHeader = new ArrayList<WadlParameterEntry>();

      for (Param p : resource.getParam())
      {

         if (p.getStyle() == ParamStyle.QUERY)
         {
            itemsQuery.add(new WadlParameterEntry(isSend, p.getName(), p.getType().getLocalName(), "", p.getDefault()));
         }

         if (p.getStyle() == ParamStyle.HEADER)
         {
            if (!p.getName().equals(HTTPHeader.OVERWRITE))
            {
               itemsHeader.add(new WadlParameterEntry(isSend, p.getName(), p.getType().getLocalName(), "", p
                  .getDefault()));
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

               if (m.getRequest() != null && m.getName().equals(display.getMethodField().getValue()))
               {

                  for (Param par : m.getRequest().getParam())
                  {
                     if (par.getStyle() == ParamStyle.QUERY)
                     {
                        itemsQuery.add(new WadlParameterEntry(isSend, par.getName(), par.getType().getLocalName(), "",
                           par.getDefault()));
                     }
                     else
                     {
                        itemsHeader.add(new WadlParameterEntry(isSend, par.getName(), par.getType().getLocalName(), "",
                           par.getDefault()));
                     }
                  }
               }
            }
         }
      }

      display.getParametersHeaderListGrid().setValue(itemsHeader);

      display.getParametersQueryListGrid().setValue(itemsQuery);

   }

   /**
    * Set resource info by path to resource and method name
    * 
    * @param path path to resource
    * @param methodName name of method
    */
   private void setResourceInfo()
   {
      fillParameters(resource, method);

      if (method == null)
      {
         display.getRequestMediaTypeField().setValue("");
         display.getResponseMediaTypeField().setValue("");
         return;
      }

      if (method.getName().equals(HTTPMethod.GET) || method.getName().equals(HTTPMethod.DELETE)
         || method.getName().equals(HTTPMethod.HEAD) || method.getName().equals(HTTPMethod.OPTIONS))
      {
         display.setBodyTabDisabled();
      }
      else
      {
         display.setBodyTabEnabled();
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
         if (path.equals(res.getPath()) || path.matches(getPathRegex(res)))
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
      return resource.getPath().replaceAll(REPLACEMENT_REGEX, PATH_REGEX) + "[/]{0,1}$";
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
            return (Method)obj;
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
         if (path.equals(resource.getPath()) || path.matches(getPathRegex(resource)))
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

      //checks is it need to change method field value
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
