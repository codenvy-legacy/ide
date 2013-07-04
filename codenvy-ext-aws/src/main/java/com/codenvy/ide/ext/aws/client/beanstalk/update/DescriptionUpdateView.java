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
package com.codenvy.ide.ext.aws.client.beanstalk.update;

import com.codenvy.ide.api.mvp.View;

/**
 * The view for {@link DescriptionUpdatePresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface DescriptionUpdateView extends View<DescriptionUpdateView.ActionDelegate> {
    /** Interface which must implement presenter to process any actions. */
    public interface ActionDelegate {
        /** Perform action when update button clicked. */
        void onUpdateClicked();

        /** Perform action when cancel button clicked. */
        void onCancelClicked();

        /** Perform action when description field changed value. */
        void onDescriptionFieldChangedValue();
    }

    /**
     * Get description value for the application.
     *
     * @return description for the application.
     */
    String getDescriptionValue();

    /**
     * Enable or disable update button.
     *
     * @param enable
     *         true if enable.
     */
    void enableUpdateButton(boolean enable);

    /** Set focus in description field. */
    void focusDescriptionField();

    /**
     * Return shown state for current window.
     *
     * @return true if shown, otherwise false.
     */
    boolean isShown();

    /** Shows current dialog. */
    void showDialog();

    /** Close current dialog. */
    void close();
}
