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
package org.exoplatform.ide.extension.cloudbees.client.info;

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
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudbees.shared.ApplicationInfo;
import org.exoplatform.ide.git.client.GitPresenter;

import java.util.*;
import java.util.Map.Entry;

/**
 * Presenter for showing application info.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ApplicationInfoPresenter.java Jun 30, 2011 5:02:31 PM vereshchaka $
 */
public class ApplicationInfoPresenter extends GitPresenter implements ApplicationInfoHandler, ViewClosedHandler {

    interface Display extends IsView {
        HasClickHandlers getOkButton();

        ListGridItem<Entry<String, String>> getApplicationInfoGrid();
    }

    private Display display;

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
        if (event.getAppInfo() != null) {
            showAppInfo(event.getAppInfo());
        } else if (makeSelectionCheck()) {
            //String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
            //showApplicationInfo(projectId);
            showApplicationInfo(getSelectedProject().getId());
        }
    }

    private void showApplicationInfo(final String projectId) {
        try {
            AutoBean<ApplicationInfo> autoBean = CloudBeesExtension.AUTO_BEAN_FACTORY.applicationInfo();
            CloudBeesClientService.getInstance().getApplicationInfo(
                    null,
                    vfs.getId(),
                    projectId,
                    new CloudBeesAsyncRequestCallback<ApplicationInfo>(new AutoBeanUnmarshaller<ApplicationInfo>(autoBean),
                                                                       new LoggedInHandler() {
                                                                           @Override
                                                                           public void onLoggedIn() {
                                                                               showApplicationInfo(projectId);
                                                                           }
                                                                       }, null) {
                        @Override
                        protected void onSuccess(ApplicationInfo result) {
                            showAppInfo(result);
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void showAppInfo(ApplicationInfo appInfo) {
        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
            IDE.getInstance().openView(display.asView());
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridId(), appInfo.getId());
        map.put(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridTitle(), appInfo.getTitle());
        map.put(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridServerPool(), appInfo.getServerPool());
        map.put(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridStatus(), appInfo.getStatus());
        map.put(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridContainer(), appInfo.getContainer());
        map.put(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridIdleTimeout(), appInfo.getIdleTimeout());
        map.put(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridMaxMemory(), appInfo.getMaxMemory());
        map.put(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridSecurityMode(), appInfo.getSecurityMode());
        map.put(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridClusterSize(), appInfo.getClusterSize());
        map.put(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridUrl(), appInfo.getUrl());

        Iterator<Entry<String, String>> it = map.entrySet().iterator();
        List<Entry<String, String>> valueList = new ArrayList<Map.Entry<String, String>>();
        while (it.hasNext()) {
            valueList.add(it.next());
        }
        display.getApplicationInfoGrid().setValue(valueList);
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
