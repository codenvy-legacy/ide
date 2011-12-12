/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.samples.client.github.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltEvent;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltHandler;
import org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationEvent;
import org.exoplatform.ide.extension.samples.client.ProjectProperties;
import org.exoplatform.ide.extension.samples.client.SamplesClientService;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.extension.samples.client.SamplesLocalizationConstant;
import org.exoplatform.ide.extension.samples.client.github.load.ProjectData;
import org.exoplatform.ide.extension.samples.client.paas.cloudbees.CloudBeesAsyncRequestCallback;
import org.exoplatform.ide.extension.samples.client.paas.cloudfoundry.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.samples.client.paas.cloudfoundry.CloudfoundryApplication;
import org.exoplatform.ide.extension.samples.client.paas.heroku.HerokuAsyncRequestCallback;
import org.exoplatform.ide.extension.samples.client.paas.login.LoggedInHandler;
import org.exoplatform.ide.extension.samples.client.paas.login.LoginCanceledHandler;
import org.exoplatform.ide.extension.samples.client.paas.openshift.OpenShiftAsyncRequestCallback;
import org.exoplatform.ide.extension.samples.client.wizard.ProjectCreationFinishedEvent;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Presenter for deploying samples imported from GitHub.
 * <p/>
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeploySamplesPresenter.java Nov 22, 2011 10:35:16 AM vereshchaka $
 */
public class DeploySamplesPresenter implements ViewClosedHandler, GithubStep<ProjectData>, VfsChangedHandler,
   ApplicationBuiltHandler
{

   public interface Display extends IsView
   {
      HasClickHandlers getFinishButton();

      HasClickHandlers getCancelButton();

      HasClickHandlers getBackButton();

      HasValue<String> getSelectPaasField();

      void setPaasValueMap(String[] values, String selected);

      void enableFinishButton(boolean enable);

      //CloudBees
      HasValue<String> getSelectCloudBeesDomainField();

      HasValue<String> getCloudBeesNameField();

      HasValue<String> getCloudBeesIdField();

      void setVisibleCloudBeesPanel(boolean visible);

      void setCloudBeesDomainsValueMap(String[] values);

      //CloudFoundry
      HasValue<String> getCloudFoundryNameField();

      HasValue<String> getCloudFoundryUrlField();

      HasValue<String> getCloudFoundryTargetField();

      void setVisibleCloudFoundryPanel(boolean visible);

      void setCloudFoundryAvailableTargets(String[] targets);

      //Heroku
      void setVisibleHerokuPanel(boolean visible);

      HasValue<String> getHerokuApplicationNameField();

      HasValue<String> getHerokuRepositoryNameField();

      //OpenShift
      void setVisibleOpenShiftPanel(boolean visible);

      HasValue<String> getOpenShiftNameField();

      HasValue<String> getOpenShitfTypeSelectionField();

      void setOpenShitfTypesValueMap(String[] values);

   }

   private static final SamplesLocalizationConstant lb = SamplesExtension.LOCALIZATION_CONSTANT;

   /**
    * Default CloudFoundry target.
    */
   public static final String DEFAULT_CLOUDFOUNDRY_TARGET = "http://api.cloudfoundry.com";

   public static final String DEFAULT_URL_PREFIX = "<name>.";

   private Display display;

   private GithubStep<ProjectData> prevStep;

   private ProjectData data;

   private String selectedPaaS;

   private VirtualFileSystemInfo vfs;

   private ProjectModel project;

   private String warUrl;

   //variables to store paas parameters
   //TODO: more convenience store for this params

   //heroku
   private String herokuAppName;

   private String herokuRemoveService;

   //openShift
   private String openShiftName;

   private String openShitfType;

   //cloudBees
   private String cloudBeesName;

   private String cloudBeesDomain;

   //cloudFoudnry
   private String cloudFoundryName;

   private String cloudFoundryTarget;

   private String cloudFoundryUrl;

   public DeploySamplesPresenter()
   {
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
   }

   private void bindDisplay()
   {
      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.fireEvent(new ProjectCreationFinishedEvent(true));
            closeView();
         }
      });

      display.getFinishButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            //if one of field are changed, the new value must be saved in projectProperties.
            //That's why, when Next button is clicked, the actual state are send to next step.
            if (selectedPaaS.equals(ProjectProperties.Paas.CLOUDFOUNDRY))
            {
               validateCloudFoundryParams();
               return;
            }

            createEmptyProject();
         }
      });

      display.getBackButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            prevStep.onReturn();
            closeView();
         }
      });

      display.getSelectPaasField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            selectedPaaS = display.getSelectPaasField().getValue();
            if (ProjectProperties.Paas.CLOUDFOUNDRY.equals(selectedPaaS))
            {
               display.setVisibleCloudBeesPanel(false);
               display.setVisibleHerokuPanel(false);
               display.setVisibleOpenShiftPanel(false);
               display.setVisibleCloudFoundryPanel(true);
               getCloudFoundryTargets();
            }
            else if (ProjectProperties.Paas.CLOUDBEES.equals(selectedPaaS))
            {
               getCloudBeesDomains();
            }
            else if (ProjectProperties.Paas.HEROKU.equals(selectedPaaS))
            {
               display.setVisibleCloudBeesPanel(false);
               display.setVisibleCloudFoundryPanel(false);
               display.setVisibleOpenShiftPanel(false);
               display.setVisibleHerokuPanel(true);
               display.enableFinishButton(true);
            }
            //            else if (ProjectProperties.Paas.OPENSHIFT.equals(selectedPaaS))
            //            {
            //               getOpenShiftTypes();
            //            }
            else
            {
               display.setVisibleCloudFoundryPanel(false);
               display.setVisibleCloudBeesPanel(false);
               display.setVisibleHerokuPanel(false);
               display.setVisibleOpenShiftPanel(false);
               display.enableFinishButton(true);
            }
         }
      });

      display.getCloudBeesNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (ProjectProperties.Paas.CLOUDBEES.equals(display.getSelectPaasField().getValue()))
            {
               String name = display.getCloudBeesNameField().getValue();
               String cloudBeesId = name + "/" + display.getSelectCloudBeesDomainField().getValue();
               display.getCloudBeesIdField().setValue(cloudBeesId);
               display.enableFinishButton(name != null && !name.isEmpty());
            }
         }
      });

      display.getSelectCloudBeesDomainField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (ProjectProperties.Paas.CLOUDBEES.equals(display.getSelectPaasField().getValue()))
            {
               String name = display.getCloudBeesNameField().getValue();
               String cloudBeesId = name + "/" + display.getSelectCloudBeesDomainField().getValue();
               display.getCloudBeesIdField().setValue(cloudBeesId);
            }
         }
      });

      display.getCloudFoundryTargetField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (ProjectProperties.Paas.CLOUDFOUNDRY.equals(display.getSelectPaasField().getValue()))
            {
               String target = display.getCloudFoundryTargetField().getValue();
               String sufix = target.substring(target.indexOf("."));
               String oldUrl = display.getCloudFoundryUrlField().getValue();
               String prefix = "<name>";
               if (!oldUrl.isEmpty() && oldUrl.contains("."))
               {
                  prefix = oldUrl.substring(0, oldUrl.indexOf("."));
               }
               String url = prefix + sufix;
               display.getCloudFoundryUrlField().setValue(url);
            }
         }
      });

      display.getCloudFoundryNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (ProjectProperties.Paas.CLOUDFOUNDRY.equals(display.getSelectPaasField().getValue()))
            {
               String name = display.getCloudFoundryNameField().getValue();
               String url = display.getCloudFoundryUrlField().getValue();
               if (name == null || name.isEmpty() || url == null || url.isEmpty())
               {
                  display.enableFinishButton(false);
               }
               else
               {
                  display.enableFinishButton(true);
               }
            }
         }
      });

      display.getCloudFoundryUrlField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (ProjectProperties.Paas.CLOUDFOUNDRY.equals(display.getSelectPaasField().getValue()))
            {
               String name = display.getCloudFoundryNameField().getValue();
               String url = display.getCloudFoundryUrlField().getValue();
               if (name == null || name.isEmpty() || url == null || url.isEmpty())
               {
                  display.enableFinishButton(false);
               }
               else
               {
                  display.enableFinishButton(true);
               }
            }
         }
      });

      display.getOpenShiftNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            String name = display.getOpenShiftNameField().getValue();
            if (name == null || name.isEmpty())
            {
               display.enableFinishButton(false);
            }
            else
            {
               display.enableFinishButton(true);
            }
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep#onOpen(java.lang.Object)
    */
   @Override
   public void onOpen(ProjectData value)
   {
      this.data = value;
      if (display == null)
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView((View)d);
         display = d;
         bindDisplay();
         String[] paasArray = getPaases(data.getType());
         display.setPaasValueMap(paasArray, paasArray[0]);
         return;
      }
      else
      {
         IDE.fireEvent(new ExceptionThrownEvent("Show Deployment Wizard View must be null"));
      }
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep#onReturn()
    */
   @Override
   public void onReturn()
   {
      //the last step
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep#setNextStep(org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep)
    */
   @Override
   public void setNextStep(GithubStep<ProjectData> step)
   {
      //has no step, it is the last step.
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep#setPreviousStep(org.exoplatform.ide.extension.samples.client.github.deploy.GithubStep)
    */
   @Override
   public void setPreviousStep(GithubStep<ProjectData> step)
   {
      this.prevStep = step;
   }

   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }

   /**
    * Get the list of targes and put them to select field.
    */
   private void getCloudFoundryTargets()
   {
      SamplesClientService.getInstance().getCloudFoundryTargets(new AsyncRequestCallback<List<String>>()
      {
         @Override
         protected void onSuccess(List<String> result)
         {
            if (result.isEmpty())
            {
               display.setCloudFoundryAvailableTargets(new String[]{DEFAULT_CLOUDFOUNDRY_TARGET});
               if (display.getCloudFoundryTargetField().getValue().isEmpty())
               {
                  display.getCloudFoundryTargetField().setValue(DEFAULT_CLOUDFOUNDRY_TARGET);
               }
            }
            else
            {
               String[] servers = result.toArray(new String[result.size()]);
               display.setCloudFoundryAvailableTargets(servers);
               if (display.getCloudFoundryTargetField().getValue().isEmpty())
               {
                  display.getCloudFoundryTargetField().setValue(servers[0]);
               }
            }
            fillCloudFoundryFields();
         }
      });
   }

   /**
    * Fill cloudfoundry fields by values stored in project properties variable
    * or by default values.
    */
   private void fillCloudFoundryFields()
   {
      String name = data.getName();
      display.getCloudFoundryNameField().setValue(name);

      final String target = display.getCloudFoundryTargetField().getValue();
      String urlSufix = target.substring(target.indexOf("."));
      final String oldUrl = display.getCloudFoundryUrlField().getValue();
      String prefix = "<name>";
      if (!oldUrl.isEmpty() && oldUrl.contains("."))
      {
         prefix = oldUrl.substring(0, oldUrl.indexOf("."));
      }
      if (urlSufix.isEmpty())
      {
         urlSufix = DEFAULT_CLOUDFOUNDRY_TARGET.substring(DEFAULT_CLOUDFOUNDRY_TARGET.indexOf("."));
      }
      String url = prefix + urlSufix;
      display.getCloudFoundryUrlField().setValue(url);

   }

   /**
    * Get the list of domains of CloudBees from server.
    * <p/>
    * Put the received values to domains select field.
    * <p/>
    * Fill other fields, if we have values (may be user entered them before)
    */
   private void getCloudBeesDomains()
   {
      SamplesClientService.getInstance().getDomains(
         new CloudBeesAsyncRequestCallback<List<String>>(IDE.eventBus(), domainsLoggedInHandler,
            domainsLoginCanceledHandler)
         {
            @Override
            protected void onSuccess(List<String> result)
            {
               display.setVisibleCloudFoundryPanel(false);
               display.setVisibleHerokuPanel(false);
               display.setVisibleOpenShiftPanel(false);
               display.setVisibleCloudBeesPanel(true);

               String[] domains = new String[result.size()];
               result.toArray(domains);
               display.setCloudBeesDomainsValueMap(domains);
               fillCloudBeesFields();
            }
         });
   }

   private LoginCanceledHandler domainsLoginCanceledHandler = new LoginCanceledHandler()
   {

      @Override
      public void onCancelLogin()
      {
         //if while receiving domains from cloudbees was clicked Cancel button in login dialog
         //than select in paas field NONE and hide all paases.
         display.setVisibleCloudFoundryPanel(false);
         display.setVisibleHerokuPanel(false);
         display.setVisibleOpenShiftPanel(false);
         display.setVisibleCloudBeesPanel(false);
         display.getSelectPaasField().setValue(ProjectProperties.Paas.NONE);
      }
   };

   private LoggedInHandler domainsLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         getCloudBeesDomains();
      }
   };

   /**
    * Fill cloudbees name and domain fields with values, that user
    * entered before (they are stored in Map in projectProperties variable).
    * <p/>
    * If no values are stored, than get the deploy name from the name of project
    * and do nothing with domain field.
    */
   private void fillCloudBeesFields()
   {
      final String deployName = data.getName();
      display.getCloudBeesNameField().setValue(deployName);

      String id = deployName + "/" + display.getSelectCloudBeesDomainField().getValue();
      display.getCloudBeesIdField().setValue(id);

      display.enableFinishButton(deployName != null && !deployName.isEmpty());
   }

   //---------------projects creation------------------------

   private void createEmptyProject()
   {
      FolderModel parent = (FolderModel)vfs.getRoot();
      ProjectModel model = new ProjectModel();
      model.setName(data.getName());
      model.setProjectType(data.getType());
      model.setParent(parent);
      try
      {
         VirtualFileSystem.getInstance().createProject(
            parent,
            new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<ProjectModel>(
               new ProjectUnmarshaller(model))
            {

               @Override
               protected void onSuccess(ProjectModel result)
               {
                  project = result;
                  cloneRepository(data, result);
                  storePaasValues();
                  closeView();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception, "Exception during creating project"));
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
      }
   }

   private void storePaasValues()
   {
      if (ProjectProperties.Paas.NONE.equals(selectedPaaS))
      {
         return;
      }
      else if (ProjectProperties.Paas.CLOUDBEES.equals(selectedPaaS))
      {
         cloudBeesName = display.getCloudBeesNameField().getValue();
         cloudBeesDomain = display.getSelectCloudBeesDomainField().getValue();
      }
      else if (ProjectProperties.Paas.CLOUDFOUNDRY.equals(selectedPaaS))
      {
         cloudFoundryName = display.getCloudFoundryNameField().getValue();
         cloudFoundryTarget = display.getCloudFoundryTargetField().getValue();
         cloudFoundryUrl = display.getCloudFoundryUrlField().getValue();
      }
      else if (ProjectProperties.Paas.HEROKU.equals(selectedPaaS))
      {
         herokuAppName = display.getHerokuApplicationNameField().getValue();
         herokuRemoveService = display.getHerokuRepositoryNameField().getValue();
      }
      else if (ProjectProperties.Paas.OPENSHIFT.equals(selectedPaaS))
      {
         openShiftName = display.getOpenShiftNameField().getValue();
         openShitfType = display.getOpenShitfTypeSelectionField().getValue();
      }
   }

   private void cloneRepository(ProjectData repo, final ProjectModel project)
   {
      String remoteUri = repo.getRepositoryUrl();
      if (!remoteUri.endsWith(".git"))
      {
         remoteUri += ".git";
      }

      try
      {
         GitClientService.getInstance().cloneRepository(vfs.getId(), project, remoteUri, null,
            new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<String>()
            {

               @Override
               protected void onSuccess(String result)
               {
                  IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.cloneSuccess(), Type.INFO));
                  IDE.fireEvent(new ProjectCreatedEvent(project));
                  IDE.fireEvent(new RefreshBrowserEvent(project.getParent()));

                  if (ProjectProperties.Paas.NONE.equals(selectedPaaS))
                  {
                     return;
                  }
                  else if (ProjectProperties.Paas.HEROKU.equals(selectedPaaS))
                  {
                     deployToHeroku();
                  }
                  //                  else if (ProjectProperties.Paas.OPENSHIFT.equals(selectedPaaS))
                  //                  {
                  //                     deployToOpenShift();
                  //                  }
                  else if (ProjectProperties.Paas.CLOUDBEES.equals(selectedPaaS))
                  {
                     if (isMavenProject())
                     {
                        buildApplication(project);
                     }
                     else
                     {
                        Dialogs.getInstance().showError(
                           "Newly created project is not maven project. You can't deploy it to CloudBees");
                     }
                  }
                  else if (ProjectProperties.Paas.CLOUDFOUNDRY.equals(selectedPaaS))
                  {
                     if (isMavenProject())
                     {
                        buildApplication(project);
                     }
                     else
                     {
                        deployToPaas();
                     }
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  handleError(exception);
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
         handleError(e);
      }
   }

   private boolean isMavenProject()
   {
      for (Item i : project.getChildren().getItems())
      {
         if (i.getItemType() == ItemType.FILE && "pom.xml".equals(i.getName()))
         {
            return true;
         }
      }

      return false;
   }

   private void handleError(Throwable t)
   {
      String errorMessage =
         (t.getMessage() != null && t.getMessage().length() > 0) ? t.getMessage() : GitExtension.MESSAGES.cloneFailed();
      IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      this.vfs = event.getVfsInfo();
   }

   /**
    * Call the server validation of CloudFoundry params (name of application).
    */
   private void validateCloudFoundryParams()
   {
      SamplesClientService.getInstance().validateCloudfoundryAction(display.getCloudFoundryTargetField().getValue(),
         display.getCloudFoundryNameField().getValue(), null,
         new CloudFoundryAsyncRequestCallback<String>(IDE.eventBus(), validationLoggedInHandler)
         {
            @Override
            protected void onSuccess(String result)
            {
               createEmptyProject();
            }
         });
   }

   private LoggedInHandler validationLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         validateCloudFoundryParams();
      }
   };

   private void deployToPaas()
   {
      if (ProjectProperties.Paas.CLOUDBEES.equals(selectedPaaS))
      {
         deployToCloudBees();
      }
      else if (ProjectProperties.Paas.CLOUDFOUNDRY.equals(selectedPaaS))
      {
         deployToCloudFoundry();
      }
   }

   private LoggedInHandler deployToCloudBeesLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         deployToCloudBees();
      }
   };

   private void deployToCloudBees()
   {
      final String applicationId = cloudBeesName + "/" + cloudBeesDomain;

      SamplesClientService.getInstance().createCloudBeesApplication(applicationId, vfs.getId(), project.getId(),
         warUrl, null,
         new CloudBeesAsyncRequestCallback<Map<String, String>>(IDE.eventBus(), deployToCloudBeesLoggedInHandler)
         {
            @Override
            protected void onSuccess(final Map<String, String> deployResult)
            {
               String output = lb.cloudBessDeploySuccess() + "<br>";
               output += lb.cloudBeesDeployApplicationInfo() + "<br>";

               Iterator<Entry<String, String>> it = deployResult.entrySet().iterator();
               while (it.hasNext())
               {
                  Entry<String, String> entry = (Entry<String, String>)it.next();
                  output += entry.getKey() + " : " + entry.getValue() + "<br>";
               }
               IDE.fireEvent(new OutputEvent(output, Type.INFO));
            }

            /**
             * @see org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback#onFailure(java.lang.Throwable)
             */
            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.fireEvent(new OutputEvent(lb.cloudBeesDeployFailure(), Type.INFO));
               super.onFailure(exception);
            }

         });
   }

   private LoggedInHandler deployToCloudFoundryLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         deployToCloudBees();
      }
   };

   private void deployToCloudFoundry()
   {
      SamplesClientService.getInstance().createCloudFoundryApplication(
         vfs.getId(),
         cloudFoundryTarget,
         cloudFoundryName,
         cloudFoundryUrl,
         project.getPath(),
         project.getId(),
         warUrl,
         new CloudFoundryAsyncRequestCallback<CloudfoundryApplication>(IDE.eventBus(),
            deployToCloudFoundryLoggedInHandler)
         {
            @Override
            protected void onSuccess(CloudfoundryApplication result)
            {
               String msg = lb.cloudFoundryDeploySuccess(result.getName());
               if ("STARTED".equals(result.getState()))
               {
                  if (result.getUris().isEmpty())
                  {
                     msg += "<br>" + lb.cloudFoundryApplicationStartedWithNoUrls();
                  }
                  else
                  {
                     msg +=
                        "<br>" + lb.cloudFoundryApplicationStartedOnUrls(result.getName(), getAppUrlsAsString(result));
                  }
               }
               IDE.fireEvent(new OutputEvent(msg, OutputMessage.Type.INFO));
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.fireEvent(new OutputEvent(lb.cloudFoundryDeployFailure(), Type.INFO));
               super.onFailure(exception);
            }
         });
   }

   /**
    * @see org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltHandler#onApplicationBuilt(org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltEvent)
    */
   @Override
   public void onApplicationBuilt(ApplicationBuiltEvent event)
   {
      IDE.removeHandler(event.getAssociatedType(), this);
      if (event.getJobStatus().getArtifactUrl() != null)
      {
         warUrl = event.getJobStatus().getArtifactUrl();
         deployToPaas();
      }
   }

   private void buildApplication(ProjectModel projectModel)
   {
      IDE.addHandler(ApplicationBuiltEvent.TYPE, this);
      IDE.fireEvent(new BuildApplicationEvent(projectModel));
   }

   private String getAppUrlsAsString(CloudfoundryApplication application)
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
         //crop unnecessary symbols
         appUris = appUris.substring(2);
      }
      return appUris;
   }

   private String[] getPaases(String type)
   {
      List<String> paas = new ArrayList<String>();
      paas.add(ProjectProperties.Paas.NONE);
      //can be deployed to CloudBees
      if ("Java Web".equals(type) || "Servlet/JSP".equals(type))
      {
         paas.add(ProjectProperties.Paas.CLOUDBEES);
      }
      //can be deployed to CloudFoundry
      if ("Rails".equals(type) || "Spring".equals(type) || "Java Web".equals(type) || "Servlet/JSP".equals(type))
      {
         paas.add(ProjectProperties.Paas.CLOUDFOUNDRY);
      }
      //can be deployed to Heroku
      if ("Rails".equals(type))
      {
         paas.add(ProjectProperties.Paas.HEROKU);
      }

      //      //can be deployed to OpenShift
      //      if ("Rails".equals(type))
      //      {
      //         paas.add(ProjectProperties.Paas.OPENSHIFT);
      //      }
      return paas.toArray(new String[paas.size()]);
   }

   private LoggedInHandler herokuLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         deployToHeroku();
      }
   };

   private void deployToHeroku()
   {
      SamplesClientService.getInstance().createHerokuApplication(herokuAppName, vfs.getId(), project.getId(),
         herokuRemoveService, new HerokuAsyncRequestCallback<String>(IDE.eventBus(), herokuLoggedInHandler)
         {
            @Override
            protected void onSuccess(String result)
            {
               IDE.fireEvent(new OutputEvent("Application deployed to Heroku successfully", OutputMessage.Type.INFO));
            }
         });
   }

   private LoggedInHandler openShiftLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         deployToOpenShift();
      }
   };

   private void deployToOpenShift()
   {
      SamplesClientService.getInstance().createOpenShitfApplication(openShiftName, vfs.getId(), project.getId(),
         openShitfType, new OpenShiftAsyncRequestCallback<String>(IDE.eventBus(), openShiftLoggedInHandler)
         {
            @Override
            protected void onSuccess(String result)
            {
               IDE.fireEvent(new OutputEvent("Application deployed to OpenShift successfully", OutputMessage.Type.INFO));
            }
         });
   }

   private void getOpenShiftTypes()
   {
      SamplesClientService.getInstance().getOpenShiftTypes(new AsyncRequestCallback<List<String>>()
      {
         @Override
         protected void onSuccess(List<String> result)
         {
            display.setOpenShitfTypesValueMap(result.toArray(new String[result.size()]));
            display.setVisibleCloudBeesPanel(false);
            display.setVisibleCloudFoundryPanel(false);
            display.setVisibleHerokuPanel(false);
            display.setVisibleOpenShiftPanel(true);
            display.enableFinishButton(false);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            super.onFailure(exception);
            display.getSelectPaasField().setValue(ProjectProperties.Paas.NONE);
            display.setVisibleCloudBeesPanel(false);
            display.setVisibleCloudFoundryPanel(false);
            display.setVisibleHerokuPanel(false);
            display.setVisibleOpenShiftPanel(false);
            display.enableFinishButton(true);
         }

      });
   }

}
