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
package com.codenvy.ide.ext.java.jdi.client.debug;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.java.jdi.shared.BreakPoint;
import com.codenvy.ide.ext.java.jdi.shared.Variable;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.part.base.BaseActionDelegate;

/**
 * The view of {@link DebuggerPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface DebuggerView extends View<DebuggerView.ActionDelegate> {
    /** Needs for delegate some function into Debugger view. */
    public interface ActionDelegate extends BaseActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Resume button. */
        void onResumeButtonClicked();

        /** Performs any actions appropriate in response to the user having pressed the Remove all breakpoints button. */
        void onRemoveAllBreakpointsButtonClicked();

        /** Performs any actions appropriate in response to the user having pressed the Disconnect button. */
        void onDisconnectButtonClicked();

        /** Performs any actions appropriate in response to the user having pressed the Step into button. */
        void onStepIntoButtonClicked();

        /** Performs any actions appropriate in response to the user having pressed the Step over button. */
        void onStepOverButtonClicked();

        /** Performs any actions appropriate in response to the user having pressed the Step return button. */
        void onStepReturnButtonClicked();

        /** Performs any actions appropriate in response to the user having pressed the Change value button. */
        void onChangeValueButtonClicked();

        /** Performs any actions appropriate in response to the user having pressed the Evaluate expression button. */
        void onEvaluateExpressionButtonClicked();

        /**
         * Returns selected variable.
         *
         * @param variable
         */
        void onSelectedVariable(Variable variable);
    }

    /**
     * Sets variables.
     *
     * @param variables
     */
    void setVariables(JsonArray<Variable> variables);

    /**
     * Sets breakpoints.
     *
     * @param breakPoints
     */
    void setBreakPoints(JsonArray<BreakPoint> breakPoints);

    /**
     * Sets java virtual machine name and version.
     *
     * @param name
     */
    void setVMName(String name);

    /**
     * Sets whether Resume button is enabled.
     *
     * @param isEnable
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableResumeButton(boolean isEnable);

    /**
     * Sets whether Remove all breakpoints button is enabled.
     *
     * @param isEnable
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableRemoveAllBreakpointsButton(boolean isEnable);

    /**
     * Sets whether Disconnect button is enabled.
     *
     * @param isEnable
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableDisconnectButton(boolean isEnable);

    /**
     * Sets whether Step into button is enabled.
     *
     * @param isEnable
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableStepIntoButton(boolean isEnable);

    /**
     * Sets whether Step over button is enabled.
     *
     * @param isEnable
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableStepOverButton(boolean isEnable);

    /**
     * Sets whether Step return button is enabled.
     *
     * @param isEnable
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableStepReturnButton(boolean isEnable);

    /**
     * Sets whether Change value button is enabled.
     *
     * @param isEnable
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableChangeValueButtonEnable(boolean isEnable);

    /**
     * Sets whether Evaluate expression button is enabled.
     *
     * @param isEnable
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableEvaluateExpressionButtonEnable(boolean isEnable);

    /**
     * Sets title.
     *
     * @param title
     */
    void setTitle(String title);
}