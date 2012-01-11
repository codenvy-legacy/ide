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
package org.exoplatform.ide.client.restdiscovery;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.copy.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.copy.HTTPMethod;
import org.exoplatform.gwtframework.commons.util.Log;
import org.exoplatform.gwtframework.commons.wadl.IllegalWADLException;
import org.exoplatform.gwtframework.commons.wadl.Method;
import org.exoplatform.gwtframework.commons.wadl.Param;
import org.exoplatform.gwtframework.commons.wadl.Resource;
import org.exoplatform.gwtframework.commons.wadl.WadlApplication;
import org.exoplatform.gwtframework.commons.wadl.WadlProcessor;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.discovery.RestDiscoveryService;
import org.exoplatform.ide.client.framework.discovery.RestService;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.model.discovery.marshal.RestServicesUnmarshaller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 22, 2010 9:39:28 AM evgen $
 *
 */
public class RestServicesDiscoveryPresenter implements ShowRestServicesDiscoveryHandler, InitializeServicesHandler,
   ViewClosedHandler
{

   public interface Display extends IsView
   {

      HasClickHandlers getOkButton();

      UntypedTreeGrid getTreeGrid();

      ListGridItem<ParamExt> getParametersListGrid();

      HasValue<String> getPathField();

      HasValue<String> getRequestTypeField();

      HasValue<String> getResponseTypeField();

      void setResponseFieldVisible(boolean visible);

      void setResponseFieldEnabled(boolean enabled);

      void setRequestFieldVisible(boolean visible);

      void setRequestFieldEnabled(boolean enabled);

      void setParametersListGridVisible(boolean visible);

      void setParametersListGridEnabled(boolean enabled);

   }

   private Display display;

   private RestService currentRestService;

   private String restContext;

   private Map<String, RestService> services = new TreeMap<String, RestService>();

   public RestServicesDiscoveryPresenter()
   {
      IDE.addHandler(ShowRestServicesDiscoveryEvent.TYPE, this);
      IDE.addHandler(InitializeServicesEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);

      IDE.getInstance().addControl(new RestServicesDiscoveryControl());
   }

   /**
    * @see org.exoplatform.ide.client.restdiscovery.event.ShowRestServicesDiscoveryHandler#onShowRestServicesDiscovery(org.exoplatform.ide.client.restdiscovery.event.ShowRestServicesDiscoveryEvent)
    */
   public void onShowRestServicesDiscovery(ShowRestServicesDiscoveryEvent event)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bindDisplay();
         loadRestServices();
      }
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   private void bindDisplay()
   {
      display.getOkButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getTreeGrid().addOpenHandler(new OpenHandler<Object>()
      {
         public void onOpen(OpenEvent<Object> event)
         {
            if (event.getTarget() instanceof RestService)
            {
               RestService service = (RestService)event.getTarget();
               if (currentRestService == service)
                  return;

               if (services.containsKey(service.getFullPath()))
                  updateResourceWadl(service);
            }
         }
      });

      display.getTreeGrid().addSelectionHandler(new SelectionHandler<Object>()
      {
         public void onSelection(SelectionEvent<Object> event)
         {
            if (event.getSelectedItem() instanceof Method)
            {
               updateMethodInfo((Method)event.getSelectedItem());
            }
            else
            {
               if (event.getSelectedItem() instanceof RestService)
               {
                  display.getPathField().setValue(((RestService)event.getSelectedItem()).getFullPath());
               }
               else if (event.getSelectedItem() instanceof Resource)
               {
                  display.getPathField().setValue(((Resource)event.getSelectedItem()).getPath());
               }
               clearMethodInfo();
            }
         }
      });
   }

   /**
    * Hide method info
    */
   private void clearMethodInfo()
   {
      display.getRequestTypeField().setValue("");
      display.getResponseTypeField().setValue("");
      display.getParametersListGrid().setValue(new ArrayList<ParamExt>());
      display.setParametersListGridVisible(false);
      display.setRequestFieldVisible(false);
      display.setResponseFieldVisible(false);
      //      dispaly.setPathFieldVisible(false);
   }

   /**
    * Update method info
    * @param method
    */
   private void updateMethodInfo(Method method)
   {
      //      dispaly.setPathFieldVisible(true);
      display.getPathField().setValue(method.getHref());

      if (method.getRequest() != null)
      {
         if (!method.getRequest().getRepresentation().isEmpty())
         {
            display.setRequestFieldVisible(true);
            display.setRequestFieldEnabled(true);
            display.getRequestTypeField().setValue(method.getRequest().getRepresentation().get(0).getMediaType());
         }
         else
         {
            display.getRequestTypeField().setValue("n/a");
            display.setRequestFieldVisible(true);
            display.setRequestFieldEnabled(false);
         }
         display.setParametersListGridVisible(true);
         display.setParametersListGridEnabled(!method.getRequest().getParam().isEmpty());
         List<ParamExt> paramsExt = convertParamList(method.getRequest().getParam());
         display.getParametersListGrid().setValue(paramsExt);
      }
      else
      {
         display.getRequestTypeField().setValue("n/a");
         display.getParametersListGrid().setValue(new ArrayList<ParamExt>());
         display.setParametersListGridVisible(true);
         display.setParametersListGridEnabled(false);
         display.setRequestFieldVisible(true);
         display.setRequestFieldEnabled(false);
      }

      if (method.getResponse() != null && !method.getResponse().getRepresentationOrFault().isEmpty())
      {
         display.setResponseFieldVisible(true);
         display.setResponseFieldEnabled(true);
         display.getResponseTypeField().setValue(method.getResponse().getRepresentationOrFault().get(0).getMediaType());
      }
      else
      {
         display.getResponseTypeField().setValue("n/a");
         display.setResponseFieldVisible(true);
         display.setResponseFieldEnabled(false);
      }
   }

   private String getParamGroup(Param param)
   {
      String groupName = "";
      switch (param.getStyle())
      {
         case HEADER :
            groupName = org.exoplatform.ide.client.IDE.PREFERENCES_CONSTANT.restServicesDiscoveryParamHeader();
            break;
         case QUERY :
            groupName = org.exoplatform.ide.client.IDE.PREFERENCES_CONSTANT.restServicesDiscoveryParamQuery();
            break;
         case PLAIN :
            groupName = org.exoplatform.ide.client.IDE.PREFERENCES_CONSTANT.restServicesDiscoveryParamPlain();
            break;
         case TEMPLATE :
            groupName = org.exoplatform.ide.client.IDE.PREFERENCES_CONSTANT.restServicesDiscoveryParamPath();
            break;
         case MATRIX :
            groupName = org.exoplatform.ide.client.IDE.PREFERENCES_CONSTANT.restServicesDiscoveryParamMatrix();
            break;
      }

      groupName += " " + org.exoplatform.ide.client.IDE.PREFERENCES_CONSTANT.restServicesDiscoveryParam();;

      return groupName;
   }

   private List<ParamExt> convertParamList(List<Param> params)
   {

      HashMap<String, List<Param>> groups = new LinkedHashMap<String, List<Param>>();

      for (Param param : params)
      {
         String groupName = getParamGroup(param);

         List<Param> paramsFromGroup = groups.get(groupName);

         if (paramsFromGroup == null)
         {
            paramsFromGroup = new ArrayList<Param>();
            groups.put(groupName, paramsFromGroup);
         }

         paramsFromGroup.add(param);
      }

      List<ParamExt> paramsExtList = new ArrayList<ParamExt>();
      Set<String> keys = groups.keySet();
      for (String groupName : keys)
      {
         paramsExtList.add(new ParamExt(groupName));
         List<Param> paramsToAdd = groups.get(groupName);
         for (Param param : paramsToAdd)
         {
            paramsExtList.add(new ParamExt(param));
         }
      }

      return paramsExtList;
   }

   /**
    * @param target
    */
   private void updateResourceWadl(RestService target)
   {
      currentRestService = target;
      String url = restContext;

      if (target.getPath().startsWith("/"))
      {
         url += target.getFullPath();
      }
      else
      {
         url += "/" + target.getFullPath();
      }

      RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL.encode(url));
      builder.setHeader(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.OPTIONS);
      try
      {
         builder.sendRequest(null, new RequestCallback()
         {
            public void onError(Request request, Throwable exception)
            {
               IDE.fireEvent(new ExceptionThrownEvent(exception, org.exoplatform.ide.client.IDE.ERRORS_CONSTANT
                  .restServicesDiscoveryGetWadlFailure()));
            }

            public void onResponseReceived(Request request, Response response)
            {
               //http status 200 IE interprets as 1223 :(
               if (200 == response.getStatusCode() || 1223 == response.getStatusCode())
               {
                  try
                  {
                     WadlApplication application = new WadlApplication();
                     WadlProcessor.unmarshal(application, response.getText());
                     display.getTreeGrid().setPaths(currentRestService,
                        application.getResources().getResource().get(0).getMethodOrResource());

                  }
                  catch (IllegalWADLException e)
                  {
                     IDE.fireEvent(new ExceptionThrownEvent(e));
                  }
                  catch (Exception e)
                  {

                     IDE.fireEvent(new ExceptionThrownEvent(new Exception(
                        org.exoplatform.ide.client.IDE.ERRORS_CONSTANT.restServicesDiscoveryGetWadlFailure())));
                  }
               }
               else
               {
               }
            }
         });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void loadRestServices()
   {
      try
      {
         RestDiscoveryService.getInstance().getRestServices(
            new AsyncRequestCallback<List<RestService>>(new RestServicesUnmarshaller(new ArrayList<RestService>()))
            {
               @Override
               protected void onSuccess(List<RestService> result)
               {
                  refreshRestServices(result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception, org.exoplatform.ide.client.IDE.ERRORS_CONSTANT
                     .restServicesDiscoveryGetRestServicesFailure()));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.discovery.event.RestServicesReceivedHandler#onRestServicesReceived(org.exoplatform.ide.client.framework.discovery.event.RestServicesReceivedEvent)
    */
   private void refreshRestServices(List<RestService> restServices)
   {
      services.clear();
      for (RestService rs : restServices)
      {
         if (!rs.getPath().endsWith("/"))
            rs.setPath(rs.getPath() + "/");
         services.put(rs.getPath(), rs);
      }

      Map<String, RestService> list2Tree = list2Tree(services.values());
      try
      {
         display.getTreeGrid().setRootValue(list2Tree.values().iterator().next(), services.keySet());
      }
      catch (Exception e)
      {
         Log.info("Exception>" + e.getMessage());
      }
   }

   private Map<String, RestService> list2Tree(Collection<RestService> services)
   {
      TreeMap<String, RestService> ser = new TreeMap<String, RestService>();
      for (RestService rs : services)
      {
         String paths[] = rs.getPath().split("/");
         if (paths.length > 1)
         {
            if (rs.getPath().endsWith("/"))
            {
               paths[paths.length - 1] += "/";
            }
            String pa = paths[0];
            if (pa.isEmpty())
            {
               pa = "/";
            }

            RestService ts = null;
            for (int i = 0; i < paths.length; i++)
            {
               String s = paths[i];
               if (s.isEmpty())
               {
                  s = "/";
               }
               if (ts == null)
               {
                  if (ser.containsKey(s))
                  {
                     ts = ser.get(s);
                  }
                  else
                  {
                     RestService restService = new RestService("/" + s);
                     ser.put(s, restService);
                     ts = restService;
                  }
               }
               else
               {
                  if (ts.getChildServices().containsKey(s))
                  {
                     ts = ts.getChildServices().get(s);
                  }
                  else
                  {
                     RestService restService = new RestService("/" + s);
                     ts.getChildServices().put(s, restService);
                     ts = restService;
                  }
               }
            }

         }
         else
         {
            if (!ser.containsKey("/"))
               ser.put("/", new RestService("REST"));
         }
      }
      return ser;
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   public void onInitializeServices(InitializeServicesEvent event)
   {
      restContext = event.getApplicationConfiguration().getContext();
      if (restContext.endsWith("/"))
      {
         restContext = restContext.substring(0, restContext.length());
      }
   }

}
