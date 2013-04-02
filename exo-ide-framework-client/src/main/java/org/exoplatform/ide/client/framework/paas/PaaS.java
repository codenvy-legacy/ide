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
package org.exoplatform.ide.client.framework.paas;

import com.google.gwt.user.client.ui.Image;

import org.exoplatform.ide.client.framework.project.ProjectType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 24, 2012 12:37:38 PM anya $
 */
public class PaaS {
    /** Id of the PaaS. */
    private String id;

    /** Title of the PaaS. */
    private String title;

    /** PaaS image (enabled). */
    private Image imageEnabled;

    /** PaaS image (disabled). */
    private Image imageDisabled;

    /** PaaS provides application's template or not. */
    private boolean providesTemplate;

    private HasPaaSActions paaSActions;

    /** List of project types, supported by the PaaS (can be deployed). */
    private List<ProjectType> supportedProjectTypes = new ArrayList<ProjectType>();

    public PaaS(String id, String title, Image imageEnabled, Image imageDisabled, List<ProjectType> supportedProjectTypes) {
        this(id, title, imageEnabled, imageDisabled, supportedProjectTypes, null);
    }

    public PaaS(String id, String title, Image imageEnabled, Image imageDisabled, List<ProjectType> supportedProjectTypes,
                HasPaaSActions paaSActions) {
        this(id, title, imageEnabled, imageDisabled, supportedProjectTypes, false, paaSActions);
    }

    public PaaS(String id, String title, Image imageEnabled, Image imageDisabled, List<ProjectType> supportedProjectTypes,
                boolean needsTemplate,
                HasPaaSActions paaSActions) {
        this.id = id;
        this.title = title;
        this.imageEnabled = imageEnabled;
        this.imageDisabled = imageDisabled;
        this.providesTemplate = needsTemplate;
        this.supportedProjectTypes = supportedProjectTypes;
        this.paaSActions = paaSActions;
    }

    /** @return {@link String} PaaS id */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *         the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /** @return the title */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *         the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return the enabled image */
    public Image getImageEnabled() {
        return imageEnabled;
    }

    /**
     * @param image
     *         the enabled image to set
     */
    public void setImageEnabled(Image image) {
        this.imageEnabled = image;
    }

    /** @return the enabled image */
    public Image getImageDisabled() {
        return imageDisabled;
    }

    /**
     * @param image
     *         the disabled image to set
     */
    public void setImageDisabled(Image image) {
        this.imageDisabled = image;
    }

    /** @return the supportedProjectTypes */
    public List<ProjectType> getSupportedProjectTypes() {
        return supportedProjectTypes;
    }

    /**
     * @param supportedProjectTypes
     *         the supportedProjectTypes to set
     */
    public void setSupportedProjectTypes(List<ProjectType> supportedProjectTypes) {
        this.supportedProjectTypes = supportedProjectTypes;
    }

    /** @return the paaSActions */
    public HasPaaSActions getPaaSActions() {
        return paaSActions;
    }

    /**
     * @param paaSActions
     *         the paaSActions to set
     */
    public void setPaaSActions(HasPaaSActions paaSActions) {
        this.paaSActions = paaSActions;
    }

    /** @return the providesTemplate */
    public boolean isProvidesTemplate() {
        return providesTemplate;
    }

    /**
     * @param providesTemplate
     *         the providesTemplate to set
     */
    public void setProvidesTemplate(boolean providesTemplate) {
        this.providesTemplate = providesTemplate;
    }
}
