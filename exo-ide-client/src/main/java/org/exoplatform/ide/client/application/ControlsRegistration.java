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
package org.exoplatform.ide.client.application;

import com.google.gwt.core.client.GWT;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.annotation.ClassAnnotationMap;
import org.exoplatform.ide.client.framework.annotation.DisableInTempWorkspace;
import org.exoplatform.ide.client.framework.control.ControlsFormatter;
import org.exoplatform.ide.client.framework.control.ControlsUpdatedEvent;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.workspaceinfo.CurrentWorkspaceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ControlsRegistration {

    private List<Control>           registeredControls     = new ArrayList<Control>();

    private List<String>            toolbarDefaultControls = new ArrayList<String>();

    private List<String>            statusBarControls      = new ArrayList<String>();

    private List<ControlsFormatter> controlsFormatters     = new ArrayList<ControlsFormatter>();

    /**
     *
     */
    public ControlsRegistration() {
        toolbarDefaultControls.add("");
        statusBarControls.add("");
    }

    /** @return */
    public List<Control> getRegisteredControls() {
        return registeredControls;
    }

    /** @return */
    public List<String> getToolbarDefaultControls() {
        return toolbarDefaultControls;
    }

    /** @return */
    public List<String> getStatusBarControls() {
        return statusBarControls;
    }

    /**
     * @param control
     * @param docking
     */
    public void addControl(Control< ? > control, Docking docking) {
        if (!(control instanceof IDEControl)) {
            Dialogs.getInstance().showError(IDE.ERRORS_CONSTANT.controlsRegistration() + " " + control.getClass());
            return;
        }

        registeredControls.add(control);

        switch (docking) {
            case TOOLBAR:
                addControl(control, toolbarDefaultControls, false);
                break;

            case TOOLBAR_RIGHT:
                addControl(control, toolbarDefaultControls, true);
                break;

            case STATUSBAR:
                addControl(control, statusBarControls, false);
                break;

            case STATUSBAR_RIGHT:
                addControl(control, statusBarControls, true);
                break;
        }
    }

    /**
     * @param control
     * @param controls
     * @param rightDocking
     */
    private void addControl(Control control, List<String> controls, boolean rightDocking) {
        if (rightDocking) {
            controls.add(control.getId());
        } else {
            int position = 0;
            for (String curId : controls) {
                if ("".equals(curId)) {
                    break;
                }
                position++;
            }

            if (control.hasDelimiterBefore()) {
                controls.add(position, "---");
                position++;
            }

            controls.add(position, control.getId());
        }
    }

    /** @param userRoles 
     * @param currentWorkspaceInfo */
    public void initControls(List<String> userRoles, CurrentWorkspaceInfo currentWorkspaceInfo) {
        ClassAnnotationMap annotationMap = GWT.create(ClassAnnotationMap.class);
        if (annotationMap.getClassAnnotations() != null && annotationMap.getClassAnnotations().size() > 0) {
            List<Control> allowedControls = getAllowedControlsForUser(registeredControls, userRoles, annotationMap, currentWorkspaceInfo);
            registeredControls.retainAll(allowedControls);
            removeNotAllowedControls(registeredControls);
        }

        for (Control control : registeredControls) {
            if (control instanceof IDEControl) {
                ((IDEControl)control).initialize();
            }
        }
    }

    /**
     * @param controls
     * @param userRoles
     * @param annotationMap
     * @param currentWorkspaceInfo 
     * @return
     */
    private List<Control> getAllowedControlsForUser(List<Control> controls, List<String> userRoles,
                                                    ClassAnnotationMap annotationMap, CurrentWorkspaceInfo currentWorkspaceInfo) {
        List<Control> allowedControls = new ArrayList<Control>();
        for (Control control : controls) {
            String className = control.getClass().getName();
            List<String> rolesAllowed = annotationMap.getClassAnnotations().get(className);
            if (rolesAllowed == null || checkControlAllowedForUser(userRoles, rolesAllowed)) {
                if (isControlEnableForCurrentWorkspace(rolesAllowed, currentWorkspaceInfo))
                    allowedControls.add(control);
                else {
                     control.disablePermanently();
                     allowedControls.add(control);
                }
            }
        }
        return allowedControls;
    }

    /**
     * @param userRoles
     * @param rolesAllowed
     * @return
     */
    private boolean checkControlAllowedForUser(List<String> userRoles, List<String> rolesAllowed) {
        if (rolesAllowed == null || rolesAllowed.isEmpty())
            return true;
        for (String role : rolesAllowed) {
            if (userRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * @param currentWorkspaceInfo 
     * @param userRoles
     * @param rolesAllowed
     * @return
     */
    private boolean isControlEnableForCurrentWorkspace(List<String> contolAnotatoins, CurrentWorkspaceInfo currentWorkspaceInfo) {
        if (!currentWorkspaceInfo.isTemporary())
            return true;
        return !contolAnotatoins.contains(DisableInTempWorkspace.class.getName());
    }

    /** @param allowedControls */
    private void removeNotAllowedControls(List<Control> allowedControls) {
        List<String> allowedIds = new ArrayList<String>();
        for (Control control : allowedControls) {
            allowedIds.add(control.getId());
        }
        allowedIds.add("---");
        allowedIds.add("");

        toolbarDefaultControls.retainAll(allowedIds);
        statusBarControls.retainAll(allowedIds);
    }

    /** @param formatter */
    public void addControlsFormatter(ControlsFormatter formatter) {
        controlsFormatters.add(formatter);
    }

    /**
     *
     */
    public void formatControls() {
        for (ControlsFormatter formatter : controlsFormatters) {
            formatter.format(registeredControls);
        }

        IDE.fireEvent(new ControlsUpdatedEvent(registeredControls));
    }

}
