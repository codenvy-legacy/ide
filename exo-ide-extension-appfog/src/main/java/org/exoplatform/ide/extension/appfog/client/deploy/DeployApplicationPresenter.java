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
package org.exoplatform.ide.extension.appfog.client.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.job.JobManager;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.paas.DeployResultHandler;
import org.exoplatform.ide.client.framework.paas.HasPaaSActions;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.template.ProjectTemplate;
import org.exoplatform.ide.client.framework.template.TemplateService;
import org.exoplatform.ide.extension.appfog.client.AppfogAsyncRequestCallback;
import org.exoplatform.ide.extension.appfog.client.AppfogClientService;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;
import org.exoplatform.ide.extension.appfog.client.AppfogLocalizationConstant;
import org.exoplatform.ide.extension.appfog.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.appfog.client.marshaller.InfrasUnmarshaller;
import org.exoplatform.ide.extension.appfog.client.marshaller.TargetsUnmarshaller;
import org.exoplatform.ide.extension.appfog.shared.AppfogApplication;
import org.exoplatform.ide.extension.appfog.shared.InfraDetail;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class DeployApplicationPresenter implements ProjectBuiltHandler, HasPaaSActions, VfsChangedHandler
{
   interface Display
   {
      HasValue<String> getNameField();

      HasValue<String> getUrlField();

      HasValue<String> getServerField();

      HasValue<String> getInfraField();

      /**
       * Set the list of servers to ServerSelectField.
       *
       * @param servers
       */
      void setServerValues(String[] servers);

      void setInfraValues(String[] infras);

      Composite getView();
   }

   private static final AppfogLocalizationConstant lb = AppfogExtension.LOCALIZATION_CONSTANT;

   private VirtualFileSystemInfo vfs;

   private Display display;

   private String server;

   private String name;

   private String url;

   private String infra;

   /**
    * Public url to war file of application.
    */
   private String warUrl;

   private String projectName;

   private ProjectModel project;

   private DeployResultHandler deployResultHandler;

   public DeployApplicationPresenter()
   {
      IDE.addHandler(VfsChangedEvent.TYPE, this);
   }

   public void bindDisplay()
   {
      display.getNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            name = event.getValue();
         }
      });

      display.getUrlField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            url = event.getValue();
         }
      });

      display.getServerField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            server = display.getServerField().getValue();
            // if url set automatically, than try to create url using server and name
            String target = display.getServerField().getValue();
            String sufix = target.substring(target.indexOf("."));
            String oldUrl = display.getUrlField().getValue();
            String prefix = "<name>";
            if (!oldUrl.isEmpty() && oldUrl.contains("."))
            {
               prefix = oldUrl.substring(0, oldUrl.indexOf("."));
            }
            String url = prefix + sufix;
            display.getUrlField().setValue(url);
         }
      });

      display.getInfraField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            infra = display.getInfraField().getValue();
         }
      });
   }

   @Override
   public void onProjectBuilt(ProjectBuiltEvent event)
   {
      IDE.removeHandler(event.getAssociatedType(), this);
      if (event.getBuildStatus().getDownloadUrl() != null)
      {
         warUrl = event.getBuildStatus().getDownloadUrl();
         createApplication();
      }
   }

   // ----Implementation------------------------

   private void buildApplication()
   {
      IDE.addHandler(ProjectBuiltEvent.TYPE, this);
      IDE.fireEvent(new BuildProjectEvent(project));
   }

   private void createApplication()
   {
      LoggedInHandler createAppHandler = new LoggedInHandler()
      {
         @Override
         public void onLoggedIn()
         {
            createApplication();
         }
      };

      try
      {
         JobManager.get().showJobSeparated();
         AutoBean<AppfogApplication> appfogApplication =
            AppfogExtension.AUTO_BEAN_FACTORY.appfogApplication();

         AutoBeanUnmarshaller<AppfogApplication> unmarshaller =
            new AutoBeanUnmarshaller<AppfogApplication>(appfogApplication);

         // Application will be started after creation (IDE-1618)
         AppfogClientService.getInstance().create(server, name, null, url, 0, 0, false, vfs.getId(),
            project.getId(), warUrl, infra, //TODO !
            new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, createAppHandler, null, server)
            {
               @Override
               protected void onSuccess(AppfogApplication result)
               {
                  warUrl = null;
                  String msg = lb.applicationCreatedSuccessfully(result.getName());
                  if ("STARTED".equals(result.getState()))
                  {
                     if (result.getUris().isEmpty())
                     {
                        msg += "<br>" + lb.applicationStartedWithNoUrls();
                     }
                     else
                     {
                        msg += "<br>" + lb.applicationStartedOnUrls(result.getName(), getAppUrlsAsString(result));
                     }
                  }
                  deployResultHandler.onDeployFinished(true);
                  IDE.fireEvent(new OutputEvent(msg, OutputMessage.Type.INFO));
                  IDE.fireEvent(new RefreshBrowserEvent(project));
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  deployResultHandler.onDeployFinished(false);
                  IDE.fireEvent(new OutputEvent(lb.applicationCreationFailed(), OutputMessage.Type.INFO));
                  super.onFailure(exception);
               }
            });
      }
      catch (RequestException e)
      {
         deployResultHandler.onDeployFinished(false);
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private String getAppUrlsAsString(AppfogApplication application)
   {
      String appUris = "";
      for (String uri : application.getUris())
      {
         if (!uri.startsWith("http"))
         {
            uri = "http://" + uri;
         }
         appUris += ", " + "<a href=\"" + uri + "\" target=\"_blank\">" + uri + "</a>";
      }
      if (!appUris.isEmpty())
      {
         // crop unnecessary symbols
         appUris = appUris.substring(2);
      }
      return appUris;
   }

   private void getServers()
   {
      try
      {
         AppfogClientService.getInstance().getTargets(
            new AsyncRequestCallback<List<String>>(new TargetsUnmarshaller(new ArrayList<String>()))
            {
               @Override
               protected void onSuccess(List<String> result)
               {
                  if (result.isEmpty())
                  {
                     display.setServerValues(new String[]{AppfogExtension.DEFAULT_SERVER});
                     display.getServerField().setValue(AppfogExtension.DEFAULT_SERVER);
                  }
                  else
                  {
                     String[] servers = result.toArray(new String[result.size()]);
                     display.setServerValues(servers);
                     display.getServerField().setValue(servers[0]);
                  }
                  display.getNameField().setValue(projectName);
                  // don't forget to init values, that are stored, when
                  // values in form fields are changed.
                  name = projectName;
                  server = display.getServerField().getValue();
                  infra = display.getInfraField().getValue();
                  String urlSufix = server.substring(server.indexOf("."));
                  display.getUrlField().setValue(name + urlSufix);
                  url = display.getUrlField().getValue();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void getInfras()
   {
      try
      {
         AppfogClientService.getInstance().infras(null, vfs.getId(), project.getId(),
            new AsyncRequestCallback<List<InfraDetail>>(new InfrasUnmarshaller(new ArrayList<InfraDetail>()))
            {

               @Override
               protected void onSuccess(List<InfraDetail> result)
               {
                  if (result.isEmpty())
                  {
                     display.setInfraValues(new String[]{"Test value"}); //TODO !
                     display.getInfraField().setValue("Test value");     //TODO !
                  }
                  else
                  {
                     InfraDetail[] infras = result.toArray(new InfraDetail[result.size()]);
                     List<String> infraStrings = new ArrayList<String>(infras.length);
                     for (InfraDetail infra : infras)
                     {
                        infraStrings.add(infra.getName());
                     }

                     display.setInfraValues(infraStrings.toArray(new String[infraStrings.size()]));
                     display.getServerField().setValue(infraStrings.get(0));
                  }
                  name = projectName;
                  server = display.getServerField().getValue();
                  infra = display.getInfraField().getValue();
                  String urlSufix = server.substring(server.indexOf("."));
                  display.getUrlField().setValue(name + urlSufix);
                  url = display.getUrlField().getValue();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   public void performValidation()
   {
      LoggedInHandler validateHandler = new LoggedInHandler()
      {
         @Override
         public void onLoggedIn()
         {
            performValidation();
         }
      };

      try
      {
         AppfogClientService.getInstance().validateAction("create", server, name, null, url, vfs.getId(), null,
            0, 0, true, new AppfogAsyncRequestCallback<String>(null, validateHandler, null, server)
         {
            @Override
            protected void onSuccess(String result)
            {
               beforeDeploy();
            }
         });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      this.vfs = event.getVfsInfo();
   }

   @Override
   public void deploy(ProjectTemplate projectTemplate, DeployResultHandler deployResultHandler)
   {
      this.deployResultHandler = deployResultHandler;
      // TODO validate
      createProject(projectTemplate);
   }

   private void beforeDeploy()
   {
      try
      {
         VirtualFileSystem.getInstance().getChildren(project,
            new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>()))
            {

               @Override
               protected void onSuccess(List<Item> result)
               {
                  project.getChildren().setItems(result);
                  for (Item i : result)
                  {
                     if (i.getItemType() == ItemType.FILE && "pom.xml".equals(i.getName()))
                     {
                        buildApplication();
                        return;
                     }
                  }
                  createApplication();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception, "Can't receive project children "
                     + project.getName()));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   @Override
   public Composite getDeployView(String projectName, ProjectType projectType)
   {
      this.projectName = projectName;
      if (display == null)
      {
         display = GWT.create(Display.class);
      }
      bindDisplay();
      getServers();
      getInfras();
      return display.getView();
   }

   private void createProject(ProjectTemplate projectTemplate)
   {
      final Loader loader = new GWTLoader();
      loader.setMessage(lb.creatingProject());
      loader.show();
      try
      {
         TemplateService.getInstance().createProjectFromTemplate(vfs.getId(), vfs.getRoot().getId(), projectName,
            projectTemplate.getName(),
            new AsyncRequestCallback<ProjectModel>(new ProjectUnmarshaller(new ProjectModel()))
            {

               @Override
               protected void onSuccess(ProjectModel result)
               {
                  loader.hide();
                  project = result;
                  deployResultHandler.onProjectCreated(project);
                  beforeDeploy();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  loader.hide();
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         loader.hide();
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   @Override
   public void deploy(ProjectModel project, DeployResultHandler deployResultHandler)
   {
      this.project = project;
      this.deployResultHandler = deployResultHandler;
      beforeDeploy();
   }

   @Override
   public boolean validate()
   {
      return display.getNameField().getValue() != null && !display.getNameField().getValue().isEmpty()
         && display.getUrlField().getValue() != null && !display.getUrlField().getValue().isEmpty();
//         && display.getInfraField().getValue() != null && !display.getInfraField().getValue().isEmpty();
   }
}
