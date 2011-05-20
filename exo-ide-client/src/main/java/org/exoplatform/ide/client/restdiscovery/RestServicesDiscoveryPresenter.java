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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.wadl.Method;
import org.exoplatform.gwtframework.commons.wadl.Param;
import org.exoplatform.gwtframework.commons.wadl.Resource;
import org.exoplatform.gwtframework.commons.wadl.WadlApplication;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.discovery.DiscoveryService;
import org.exoplatform.ide.client.framework.discovery.RestService;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.groovy.client.service.wadl.WadlService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

      String ID = "ideResrServicesDiscoveryView";

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

   private HandlerManager eventBus;

   private Display display;

   private RestService currentRestService;

   private String restContext;

   private Map<String, RestService> services = new TreeMap<String, RestService>();

   public RestServicesDiscoveryPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(ShowRestServicesDiscoveryEvent.TYPE, this);
      eventBus.addHandler(InitializeServicesEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
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
            IDE.getInstance().closeView(Display.ID);
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
            groupName = "Header";
            break;
         case QUERY :
            groupName = "Query";
            break;
         case PLAIN :
            groupName = "Plain";
            break;
         case TEMPLATE :
            groupName = "Path";
            break;
         case MATRIX :
            groupName = "Matrix";
            break;
      }

      groupName += " param";

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
      Iterator<String> keyIter = groups.keySet().iterator();
      while (keyIter.hasNext())
      {
         final String groupName = keyIter.next();
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

      WadlService.getInstance().getWadl(url, new AsyncRequestCallback<WadlApplication>()
      {
         @Override
         protected void onSuccess(WadlApplication result)
         {
            display.getTreeGrid().setPaths(currentRestService,
               result.getResources().getResource().get(0).getMethodOrResource());
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            eventBus.fireEvent(new ExceptionThrownEvent(exception, "Service is not deployed."));
         }
      });
   }

   private void loadRestServices()
   {
      DiscoveryService.getInstance().getRestServices(new AsyncRequestCallback<List<RestService>>()
      {
         @Override
         protected void onSuccess(List<RestService> result)
         {
            refreshRestServices(result);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            eventBus.fireEvent(new ExceptionThrownEvent(exception, "Service is not deployed."));
         }
      });
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
         e.printStackTrace();
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
