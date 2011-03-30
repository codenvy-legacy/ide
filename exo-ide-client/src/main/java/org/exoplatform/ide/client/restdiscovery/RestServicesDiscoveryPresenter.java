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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.wadl.Method;
import org.exoplatform.gwtframework.commons.wadl.Param;
import org.exoplatform.gwtframework.commons.wadl.WadlApplication;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.discovery.DiscoveryService;
import org.exoplatform.ide.client.framework.discovery.RestService;
import org.exoplatform.ide.client.restdiscovery.event.ShowRestServicesDiscoveryEvent;
import org.exoplatform.ide.client.restdiscovery.event.ShowRestServicesDiscoveryHandler;
import org.exoplatform.ide.extension.groovy.client.service.wadl.WadlService;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 22, 2010 9:39:28 AM evgen $
 *
 */
public class RestServicesDiscoveryPresenter implements ShowRestServicesDiscoveryHandler, InitializeServicesHandler
{

   public interface Display
   {

      HasClickHandlers getOkButton();

      UntypedTreeGrid getTreeGrid();

      ListGridItem<ParamExt> getParametersListGrid();

      HasValue<String> getPathField();

      void setRequestType(String value);

      void setResponseType(String value);

      void closeView();

      void setResponseFieldVisible(boolean b);

      void setResponseFieldEnabled(boolean enabled);

      void setRequestFieldVisible(boolean b);

      void setRequestFieldEnabled(boolean enabled);

      void setParametersListGridVisible(boolean b);

      void setParametersListGridEnabled(boolean enabled);

      void setPathFieldVisible(boolean visible);
   }

   private HandlerManager eventBus;

   private Display dispaly;

   private RestService currentRestService;

   private String restContext;

   private Map<String, RestService> services = new TreeMap<String, RestService>();

   public RestServicesDiscoveryPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(ShowRestServicesDiscoveryEvent.TYPE, this);
      eventBus.addHandler(InitializeServicesEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.restdiscovery.event.ShowRestServicesDiscoveryHandler#onShowRestServicesDiscovery(org.exoplatform.ide.client.restdiscovery.event.ShowRestServicesDiscoveryEvent)
    */
   public void onShowRestServicesDiscovery(ShowRestServicesDiscoveryEvent event)
   {
      if (dispaly != null)
      {
         dispaly.closeView();
      }

      Display d = new RestServicesDiscoveryForm(eventBus);

      bindDisplay(d);

      DiscoveryService.getInstance().getRestServices(new AsyncRequestCallback<List<RestService>>()
      {

         @Override
         protected void onSuccess(List<RestService> result)
         {
            restServicesReceived(result);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            eventBus.fireEvent(new ExceptionThrownEvent("Service is not deployed."));
         }
      });
   }

   /**
    * @param d
    */
   private void bindDisplay(Display d)
   {

      dispaly = d;

      dispaly.getOkButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            dispaly.closeView();
            dispaly = null;
         }
      });

      dispaly.getTreeGrid().addOpenHandler(new OpenHandler<Object>()
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

      dispaly.getTreeGrid().addSelectionHandler(new SelectionHandler<Object>()
      {

         public void onSelection(SelectionEvent<Object> event)
         {
            if (event.getSelectedItem() instanceof Method)
            {
               updateMethodInfo((Method)event.getSelectedItem());
            }
            else
            {
               clearMethodInfo();
            }
         }
      });
   }

   /**
    * 
    */
   private void clearMethodInfo()
   {
      dispaly.setRequestType("");
      dispaly.setResponseType("");
      dispaly.getParametersListGrid().setValue(new ArrayList<ParamExt>());
      dispaly.setParametersListGridVisible(false);
      dispaly.setRequestFieldVisible(false);
      dispaly.setResponseFieldVisible(false);
      dispaly.setPathFieldVisible(false);
   }

   /**
    * @param selectedItem
    */
   private void updateMethodInfo(Method method)
   {
      dispaly.setPathFieldVisible(true);
      dispaly.getPathField().setValue(method.getHref());

      if (method.getRequest() != null)
      {
         if (!method.getRequest().getRepresentation().isEmpty())
         {
            dispaly.setRequestFieldVisible(true);
            dispaly.setRequestFieldEnabled(true);
            dispaly.setRequestType(method.getRequest().getRepresentation().get(0).getMediaType());
         }
         else
         {
            dispaly.setRequestType("n/a");
            dispaly.setRequestFieldVisible(true);
            dispaly.setRequestFieldEnabled(false);
         }
         dispaly.setParametersListGridVisible(true);
         dispaly.setParametersListGridEnabled(!method.getRequest().getParam().isEmpty());
         List<ParamExt> paramsExt = convertParamList(method.getRequest().getParam());
         dispaly.getParametersListGrid().setValue(paramsExt);

      }
      else
      {
         dispaly.setRequestType("n/a");
         dispaly.getParametersListGrid().setValue(new ArrayList<ParamExt>());
         dispaly.setParametersListGridVisible(true);
         dispaly.setParametersListGridEnabled(false);
         dispaly.setRequestFieldVisible(true);
         dispaly.setRequestFieldEnabled(false);
      }

      if (method.getResponse() != null && !method.getResponse().getRepresentationOrFault().isEmpty())
      {
         dispaly.setResponseFieldVisible(true);
         dispaly.setResponseFieldEnabled(true);
         dispaly.setResponseType(method.getResponse().getRepresentationOrFault().get(0).getMediaType());
      }
      else
      {
         dispaly.setResponseType("n/a");
         dispaly.setResponseFieldVisible(true);
         dispaly.setResponseFieldEnabled(false);
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
            dispaly.getTreeGrid().setPaths(currentRestService,
               result.getResources().getResource().get(0).getMethodOrResource());
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            eventBus.fireEvent(new ExceptionThrownEvent("Service is not deployed."));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.framework.discovery.event.RestServicesReceivedHandler#onRestServicesReceived(org.exoplatform.ide.client.framework.discovery.event.RestServicesReceivedEvent)
    */
   private void restServicesReceived(List<RestService> restServices)
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
         dispaly.getTreeGrid().setRootValue(list2Tree.values().iterator().next(), services.keySet());
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
