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
package com.codenvy.ide.wizard.template;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.template.Template;
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link TemplatePagePresenter}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface TemplatePageView extends View<TemplatePageView.ActionDelegate> {
    /** Needs for delegate some function into TemplatePage view. */
    public interface ActionDelegate {
        /**
         * Performs any actions appropriate in response to the user having selected template for creating project.
         *
         * @param template
         */
        void onTemplateSelected(Template template);
    }

    /**
     * Sets available templates.
     *
     * @param templates
     */
    void setTemplates(JsonArray<Template> templates);
}