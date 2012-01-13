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
package org.exoplatform.ide.extension.groovy.client.launch_service;

import com.google.gwt.http.client.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPMethod;
import org.exoplatform.gwtframework.commons.wadl.Method;
import org.exoplatform.gwtframework.commons.wadl.Param;
import org.exoplatform.gwtframework.commons.wadl.ParamStyle;
import org.exoplatform.gwtframework.commons.wadl.Resource;
import org.exoplatform.gwtframework.commons.wadl.WadlApplication;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.groovy.client.event.UndeployGroovyScriptSandboxEvent;
import org.exoplatform.ide.extension.groovy.client.service.RestServiceOutput;
import org.exoplatform.ide.extension.groovy.client.service.SimpleParameterEntry;
import org.exoplatform.ide.extension.groovy.client.service.groovy.GroovyService;
import org.exoplatform.ide.extension.groovy.client.service.groovy.event.RestServiceOutputReceivedEvent;
import org.exoplatform.ide.extension.groovy.client.service.wadl.WadlService;
import org.exoplatform.ide.vfs.client.model.FileModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class LaunchRestServicePresenter implements PreviewWadlOutputHandler, EditorActiveFileChangedHandler,
   ConfigurationReceivedSuccessfullyHandler, ViewClosedHandler
{

   public interface Display extends IsView
   {

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

      void setRequestMediaType(LinkedHashMap<String, String> requestMediaType);

      void setRequestMediaTypeFieldValue(String value);

      void setResponseMediaType(LinkedHashMap<String, String> responseMediaType);

      void setResponseMediaTypeFieldValue(String value);

      void setPathFieldValue(String value);
   }

   private static final String REPLACEMENT_REGEX = "\\{[^/]+}";

   private static final String PATH_REGEX = "[^/]+";

   private Display display;

   private WadlApplication wadlApplication;

   private List<SimpleParameterEntry> headers = new Vector<SimpleParameterEntry>();

   private List<SimpleParameterEntry> queryParams = new Vector<SimpleParameterEntry>();

   private ArrayList<Resource> resourceArray = new ArrayList<Resource>();

   private HashMap<String, String> methodArray = new LinkedHashMap<String, String>();

   private Resource resource;

   private Method currentMethod;

   private List<Method> listMethods;

   private final boolean isSend = true;

   private String currentRequestMediaType;

   private String currentResponseMediaType;

   private boolean undeployOnCancel;

   private FileModel activeFile;

   private IDEConfiguration configuration;

   /**
    * 
    */
   public LaunchRestServicePresenter()
   {
      IDE.getInstance().addControl(new LaunchRestServiceCommand(), Docking.TOOLBAR_RIGHT);

      IDE.addHandler(PreviewWadlOutputEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyHandler#onConfigurationReceivedSuccessfully(org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyEvent)
    */
   @Override
   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      configuration = event.getConfiguration();
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   /**
    * @see org.exoplatform.ide.extension.groovy.client.launch_service.PreviewWadlOutputHandler#onPreviewWadlOutput(org.exoplatform.ide.extension.groovy.client.launch_service.PreviewWadlOutputEvent)
    */
   @Override
   public void onPreviewWadlOutput(PreviewWadlOutputEvent event)
   {
      if (display != null)
      {
         return;
      }

      undeployOnCancel = event.isUndeployOnCansel();
      String content = activeFile.getContent();
      int indStart = content.indexOf("\"");
      int indEnd = content.indexOf("\"", indStart + 1);
      String path = content.substring(indStart + 1, indEnd);
      if (!path.startsWith("/"))
      {
         path = "/" + path;
      }

      String url = configuration.getContext() + path;
      WadlService.getInstance().getWadl(url, new AsyncRequestCallback<WadlApplication>()
      {
         @Override
         protected void onSuccess(WadlApplication result)
         {
            wadlApplication = result;
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            ExceptionThrownEvent exc = new ExceptionThrownEvent(exception, "Service is not deployed.");
            exc.setException(exception);
            IDE.fireEvent(exc);
         }
      });
   }

   public void bindDisplay()
   {
      display.getShowUrlButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            String url = wadlApplication.getResources().getBase();
            if (display.getPathField().getValue() != null)
            {
               url += display.getPathField().getValue();
            }

            new RestServiceURLPresenter(url);
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            if (undeployOnCancel)
            {
               IDE.fireEvent(new UndeployGroovyScriptSandboxEvent());
            }

            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getSendRequestButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            if (!pathExists(display.getPathField().getValue()))
            {
               Dialogs.getInstance().showError(
                  "Path is unexisted.<br> Check that there are no symbol '/' used as path parameter value.<br>"
                     + "Also you could select required path from the 'Path' list.");
               return;
            }
            if (!validatePathParams())
               return;
            // display.closeForm();
            sendRequest();
         }
      });

      display.getPathField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         public void onValueChange(ValueChangeEvent<String> event)
         {
            onPathFieldChanged(event.getValue());
         }
      });

      display.getMethodField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         public void onValueChange(ValueChangeEvent<String> event)
         {
            resource = findResource(display.getPathField().getValue(), event.getValue());
            listMethods = findMethod(resource, event.getValue());
            if (listMethods.size() != 0)
            {
               currentMethod = listMethods.get(0);
            }
            else
            {
               currentMethod = null;
            }
            setResourceInfo();
         }
      });

      display.getRequestMediaTypeField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         public void onValueChange(ValueChangeEvent<String> event)
         {
            currentRequestMediaType = event.getValue();
            setResponseMediaType(event.getValue());
         }

      });

      display.getResponseMediaTypeField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         public void onValueChange(ValueChangeEvent<String> event)
         {
            currentResponseMediaType = event.getValue();
            fillParameters(currentResponseMediaType);
         }

      });

      resourceArray.clear();
      Resource res = new Resource();
      res.setPath(wadlApplication.getResources().getBase());

      for (Resource r : wadlApplication.getResources().getResource())
      {
         res.getMethodOrResource().add(r);
      }

      initResources(res);

      String[] pathArr = getPathArray();
      display.setPaths(pathArr);
      if (pathArr.length > 1)
      {
         display.setPathFieldValue(pathArr[1]);
         onPathFieldChanged(pathArr[1]);
      }
      else
      {
         display.setPathFieldValue(pathArr[0]);
         onPathFieldChanged(pathArr[0]);
      }
   }

   private void onPathFieldChanged(String path)
   {
      if (path != null && !"".equals(path))
      {
         display.setSendRequestButtonDisabled(false);

         if (pathExists(path))
         {
            setMethodsOnPath(path);

            String oldPath = resource == null ? null : resource.getPath();
            String oldMethodName = currentMethod == null ? null : currentMethod.getName();

            resource = findResource(path, display.getMethodField().getValue());

            listMethods = findMethod(resource, display.getMethodField().getValue());

            if (listMethods.size() != 0)
            {
               currentMethod = listMethods.get(0);
            }
            else
            {
               currentMethod = null;
            }

            // check if it is need to change resource info
            // If value if path field changed, but path and method stayed the same
            // there is no need to set resource info again
            if (!(oldPath != null && oldPath.equals(resource.getPath()) && oldMethodName != null && oldMethodName
               .equals(currentMethod.getName())))
            {
               setResourceInfo();
            }
         }
      }
      else
      {
         resource = null;
         currentMethod = null;
         display.setSendRequestButtonDisabled(true);
         display.setMethods(new LinkedHashMap<String, String>());
         setResourceInfo();
      }
   }

   private void setResponseMediaType(String requestMediaType)
   {
      display.setResponseMediaTypeFieldValue("");
      LinkedHashMap<String, String> responseMediaType = new LinkedHashMap<String, String>();

      for (Method m : listMethods)
      {
         String response = getMethodResponse(m);
         if (response != null)
         {
            String request = getMethodRequestMediaType(m);
            if (request != null)
            {
               if (request.equals(requestMediaType) || "".equals(requestMediaType))
               {
                  responseMediaType.put(response, response);
               }
            }

         }
      }

      if (responseMediaType.size() != 0)
      {
         display.setResponseMediaType(responseMediaType);
         display.setResponseMediaTypeFieldValue(responseMediaType.keySet().iterator().next());
         currentResponseMediaType = responseMediaType.keySet().iterator().next();
         fillParameters(currentResponseMediaType);
      }
      else
      {
         display.setResponseMediaTypeFieldValue("");
         currentResponseMediaType = "";
      }
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
      ArrayList<String> parameters;

      parameters = getParams(entered, patterns);

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
    * <p>
    * Returns parameter values for str.
    * </p>
    * 
    * <p>
    * Extracts substrings, which contained in str between patterns.
    * </p>
    * 
    * E.g. to get paramater values from path <code>/rest/hello/{name}/world</code>, you must pass as <code>str</code> your path
    * and as <code>patterns</code> such array:
    * 
    * <pre>
    * {&quot;/rest/hello/&quot;, &quot;/world&quot;}
    * </pre>
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

      int p = 0;
      for (int i = 0; i < patterns.length - 1; i++)
      {
         p += str.indexOf(patterns[i]) + patterns[i].length();
         params.add(str.substring(str.indexOf(patterns[i], p) + patterns[i].length(), str.indexOf(patterns[i + 1], p)));
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
         String methoPath = display.getPathField().getValue();
         String[] parts = methoPath.split("/");
         String encodedPath = "";
         // Encode path segments:
         for (String part : parts)
         {
            encodedPath += (part.isEmpty()) ? "" : URL.encodePathSegment(part) + "/";
         }

         String fullPath = (base.endsWith("/")) ? base + encodedPath : base + "/" + encodedPath;

         GroovyService.getInstance().getOutput(fullPath, display.getMethodField().getValue(), headers, queryParams,
            display.getRequestBody().getValue(), new AsyncRequestCallback<RestServiceOutput>()
            {
               @Override
               protected void onSuccess(RestServiceOutput result)
               {
                  IDE.fireEvent(new RestServiceOutputReceivedEvent(result));
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  if (exception instanceof ServerException)
                  {
                     RestServiceOutputReceivedEvent event = new RestServiceOutputReceivedEvent(this.getResult());
                     event.setException(exception);
                     IDE.fireEvent(event);
                     return;
                  }

                  super.onFailure(exception);
               }
            });

         IDE.getInstance().closeView(display.asView().getId());
      }
      catch (IllegalArgumentException e)
      {
         Dialogs.getInstance().showError(e.getMessage());
      }
   }

   private List<SimpleParameterEntry> getHeadersParams()
   {

      List<SimpleParameterEntry> headersParam = new ArrayList<SimpleParameterEntry>();
      if (display.getParametersHeaderListGrid().getValue() != null)
      {
         for (WadlParameterEntry p : display.getParametersHeaderListGrid().getValue())
         {
            if (p.isSend())
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
         }
      }

      if (!currentRequestMediaType.equals(""))
      {
         SimpleParameterEntry contentType = new SimpleParameterEntry(HTTPHeader.CONTENT_TYPE, currentRequestMediaType);
         headersParam.add(contentType);
      }
      return headersParam;
   }

   private List<SimpleParameterEntry> getQueryParams()
   {
      List<SimpleParameterEntry> query = new ArrayList<SimpleParameterEntry>();// display.getParametersQueryListGrid().getValue();

      if (display.getParametersQueryListGrid().getValue() != null)
      {
         for (WadlParameterEntry p : display.getParametersQueryListGrid().getValue())
         {
            if (p.isSend())
            {
               query.add(p);
            }
         }
      }
      return query;
   }

   /**
    * Fills query and header parameters in form
    * 
    * @param String responseType
    */
   private void fillParameters(String responseType)
   {
      display.getParametersQueryListGrid().setValue(new ArrayList<WadlParameterEntry>());
      display.getParametersHeaderListGrid().setValue(new ArrayList<WadlParameterEntry>());

      if ("".equals(responseType))
      {
         return;
      }

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
      Method m = getMethodByNemeAndId();
      if (m.getRequest() != null)
      {
         for (Param par : m.getRequest().getParam())
         {
            if (par.getStyle() == ParamStyle.QUERY)
            {
               itemsQuery.add(new WadlParameterEntry(isSend, par.getName(), par.getType().getLocalName(), "", par
                  .getDefault()));
            }
            else
            {
               itemsHeader.add(new WadlParameterEntry(isSend, par.getName(), par.getType().getLocalName(), "", par
                  .getDefault()));
            }
         }
      }

      display.getParametersHeaderListGrid().setValue(itemsHeader);

      display.getParametersQueryListGrid().setValue(itemsQuery);

   }

   private Method getMethodByNemeAndId()
   {
      String keyMethod = display.getMethodField().getValue();
      for (Method m : listMethods)
      {
         if (m.getName().equals(keyMethod))
         {
            String requestType = getMethodRequestMediaType(m);
            String responseType = getMethodResponse(m);
            if (currentRequestMediaType.equals(requestType) && currentResponseMediaType.equals(responseType))
            {
               return m;
            }
         }
      }

      return null;
   }

   /**
    * Get Method response media type
    * 
    * @param m
    * @return response media type, if method not have response media type, returns null
    */
   private String getMethodResponse(Method m)
   {
      if (m.getResponse() != null)
      {
         if (m.getResponse().getRepresentationOrFault().size() != 0)
         {
            return m.getResponse().getRepresentationOrFault().get(0).getMediaType();
         }
      }
      return null;
   }

   /**
    * Get Method request media type
    * 
    * @param m {@link Method}
    * @return request media type, if method not have request media type, return empty string
    */
   private String getMethodRequestMediaType(Method m)
   {
      if (m.getRequest() != null)
      {
         if (m.getRequest().getRepresentation().size() != 0)
         {
            return m.getRequest().getRepresentation().get(0).getMediaType();
         }
         else
         {
            return "";
         }
      }
      else
      {
         return "";
      }
   }

   private void setRequestMediaType()
   {
      LinkedHashMap<String, String> requestMediaType = new LinkedHashMap<String, String>();

      for (Method m : listMethods)
      {
         if (m.getName().equals(display.getMethodField().getValue()))
         {
            String request = getMethodRequestMediaType(m);
            requestMediaType.put(request, request);
         }

      }

      if (requestMediaType.size() != 0)
      {
         display.setRequestMediaType(requestMediaType);
         display.setRequestMediaTypeFieldValue(requestMediaType.keySet().iterator().next());
         currentRequestMediaType = requestMediaType.keySet().iterator().next();
      }
      else
      {
         currentRequestMediaType = "";
         display.getRequestMediaTypeField().setValue("");
      }
   }

   /**
    * Set resource info by path to resource and method name
    * 
    * @param path path to resource
    * @param methodName name of method
    */
   private void setResourceInfo()
   {

      if (currentMethod == null)
      {
         display.getRequestMediaTypeField().setValue("");
         display.getResponseMediaTypeField().setValue("");
         return;
      }

      if (currentMethod.getName().equals(HTTPMethod.GET) || currentMethod.getName().equals(HTTPMethod.DELETE)
         || currentMethod.getName().equals(HTTPMethod.HEAD) || currentMethod.getName().equals(HTTPMethod.OPTIONS))
      {
         display.setBodyTabDisabled();
      }
      else
      {
         display.setBodyTabEnabled();
      }
      setRequestMediaType();
      setResponseMediaType(currentRequestMediaType);

      fillParameters(currentResponseMediaType);
      // }
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
    * Replaces all path parameters with regex string in order to match input path with existing paths
    * 
    * @param resource resource
    * @return String
    */
   private String getPathRegex(Resource resource)
   {
      if (resource.getPath().contains(": ."))
      {
         String nReg = resource.getPath().substring(0, resource.getPath().lastIndexOf("/"));

         String reg = nReg.replaceAll(REPLACEMENT_REGEX, PATH_REGEX);

         // get path param list i.e. /{pathPatamList: .+}
         String[] pathParam = display.getPathField().getValue().split(reg);

         // if no path parameters list return
         if (pathParam.length <= 1)
         {
            return resource.getPath().replaceAll(REPLACEMENT_REGEX, PATH_REGEX) + "[/]{0,1}$";
         }

         // Remove first "/"
         String exp = pathParam[1].substring(1);

         // Split path parameter list
         String[] arr = exp.split("/");
         reg += "/";
         for (int i = 0; i < arr.length; i++)
         {
            reg += PATH_REGEX + "/";
         }

         return reg + "{0,1}$";
      }

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
   private List<Method> findMethod(Resource resource, String methodName)
   {
      List<Method> listMethod = new ArrayList<Method>();
      for (Object obj : resource.getMethodOrResource())
      {
         if ((obj instanceof Method) && methodName.equals(((Method)obj).getName()))
         {
            listMethod.add((Method)obj);
         }
      }
      return listMethod;
   }

   /**
    * Finds methods, corresponding to path and initializes the method field.
    * 
    * If old method field value belongs to new method array, then it stays, otherwise first value from method array sets to method
    * field
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
                  methodArray.put(((Method)obj).getName(), ((Method)obj).getId());
               }
            }
      }

      LinkedHashMap<String, String> methods = new LinkedHashMap<String, String>();
      for (String key : methodArray.keySet())
      {
         methods.put(key, key);
      }

      display.setMethods(methods);
      if (oldMethodName == null)
         return;

      // checks is it need to change method field value
      for (String methodName : methodArray.keySet())
      {
         if (oldMethodName.equals(methodName))
         {
            display.setMethodFieldValue(oldMethodName);
            return;
         }
      }
      display.setMethodFieldValue(methodArray.keySet().iterator().next());
   }

   /**
    * Finds recursively all resources in resource and add them to resourceArray
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
