/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.ext.jenkins.client.build;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.part.base.BaseActionDelegate;

/**
 * The view of {@link BuildApplicationView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface BuildApplicationView extends View<BuildApplicationView.ActionDelegate> {
    /** Needs for delegate some function into BuildApplication view. */
    public interface ActionDelegate extends BaseActionDelegate {
    }

    /**
     * Shows message.
     *
     * @param text
     */
    void showMessageInOutput(String text);

    /** Starts animation. */
    void startAnimation();

    /** Stops animation. */
    void stopAnimation();

    /**
     * Sets title.
     *
     * @param title
     */
    void setTitle(String title);
}