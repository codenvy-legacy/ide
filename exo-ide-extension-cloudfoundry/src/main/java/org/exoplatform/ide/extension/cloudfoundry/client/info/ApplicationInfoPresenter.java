/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.extension.cloudfoundry.client.info;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.git.client.GitPresenter;

/**
 * Presenter for showing application info.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ApplicationInfoPresenter.java Jun 30, 2011 5:02:31 PM vereshchaka $
 */
public class ApplicationInfoPresenter extends GitPresenter implements ApplicationInfoHandler, ViewClosedHandler {

    interface Display extends IsView {
        HasClickHandlers getOkButton();

        ListGridItem<String> getApplicationUrisGrid();

        ListGridItem<String> getApplicationServicesGrid();

        ListGridItem<String> getApplicationEnvironmentsGrid();

        void setName(String text);

        void setState(String text);

        void setInstances(String text);

        void setVersion(String text);

        void setDisk(String text);

        void setMemory(String text);

        void setStack(String text);

        void setModel(String text);
    }

    private Display display;

    private PAAS_PROVIDER paasProvider;

    /**
     * @param eventBus
     *         events handler
     */
    public ApplicationInfoPresenter() {
        IDE.addHandler(ApplicationInfoEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    /** Bind presenter with display. */
    public void bindDisplay() {
        display.getOkButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
    }

    /** @see org.exoplatform.ide.extension.cloudbees.client.info.ApplicationInfoHandler#onShowApplicationInfo(org.exoplatform.ide
     * .extension.cloudbees.client.info.ApplicationInfoEvent) */
    @Override
    public void onShowApplicationInfo(ApplicationInfoEvent event) {
        paasProvider = event.getPaasProvider();
        if (makeSelectionCheck()) {
            //showApplicationInfo(((ItemContext)selectedItems.get(0)).getProject().getId());
            showApplicationInfo(getSelectedProject().getId());
        }
    }

    private void showApplicationInfo(final String projectId) {
        try {
            AutoBean<CloudFoundryApplication> cloudFoundryApplication =
                    CloudFoundryExtension.AUTO_BEAN_FACTORY.cloudFoundryApplication();

            AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
                    new AutoBeanUnmarshaller<CloudFoundryApplication>(cloudFoundryApplication);

            CloudFoundryClientService.getInstance().getApplicationInfo(vfs.getId(), projectId, null, null, new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(
                                                                               unmarshaller, new LoggedInHandler() {
                                                                           @Override
                                                                           public void onLoggedIn(String server) {
                                                                               showApplicationInfo(projectId);
                                                                           }
                                                                       }, null, paasProvider) {
                                                                           @Override
                                                                           protected void onSuccess(CloudFoundryApplication result) {
                                                                               if (display == null) {
                                                                                   display = GWT.create(Display.class);
                                                                                   bindDisplay();
                                                                                   display.setName(result.getName());
                                                                                   display.setState(result.getState());
                                                                                   display.setInstances(
                                                                                           String.valueOf(result.getInstances()));
                                                                                   display.setVersion(result.getVersion());
                                                                                   display.setDisk(
                                                                                           String.valueOf(result.getResources().getDisk()));
                                                                                   display.setMemory(String.valueOf(
                                                                                           result.getResources().getMemory()) + "MB");
                                                                                   display.setModel(
                                                                                           String.valueOf(result.getStaging().getModel()));
                                                                                   display.setStack(
                                                                                           String.valueOf(result.getStaging().getStack()));
                                                                                   display.getApplicationUrisGrid()
                                                                                          .setValue(result.getUris());
                                                                                   display.getApplicationServicesGrid()
                                                                                          .setValue(result.getServices());
                                                                                   display.getApplicationEnvironmentsGrid()
                                                                                          .setValue(result.getEnv());
                                                                                   IDE.getInstance().openView(display.asView());
                                                                               }
                                                                           }
                                                                       });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

}
