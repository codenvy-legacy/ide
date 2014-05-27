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
package com.codenvy.ide.ext.java.jdi.client.debug;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.parts.base.BaseActionDelegate;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.debug.Breakpoint;
import com.codenvy.ide.ext.java.jdi.shared.Variable;
import com.codenvy.ide.ext.java.jdi.shared.Location;
import com.google.gwt.user.client.ui.ToggleButton;

import javax.validation.constraints.NotNull;
import java.util.List;

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

        /** Performs any actions appropriate in response to the user having pressed the expand button in variables tree. */
        void onExpandVariablesTree();

        /**
         * Performs any actions appropriate in response to the user having selected variable in variables tree.
         *
         * @param variable
         *         variable that is selected
         */
        void onSelectedVariableElement(@NotNull Variable variable);
    }

    /**
     * Sets additional information for variables.
     *
     * @param absentInformation
     *         availability status for variables
     * @param location
     *         Information about the location of the resource
     */
    public void setVariablesInfo(boolean absentInformation, Location location);

    /**
     * Sets variables.
     *
     * @param variables
     *         available variables
     */
    void setVariables(@NotNull List<Variable> variables);

    /**
     * Sets breakpoints.
     *
     * @param breakPoints
     *         available breakpoints
     */
    void setBreakpoints(@NotNull Array<Breakpoint> breakPoints);

    /**
     * Sets java virtual machine name and version.
     *
     * @param name
     *         virtual machine name
     */
    void setVMName(@NotNull String name);

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

    /** Change state for StepIntoButton. */
    boolean resetStepIntoButton(boolean state);

    /**
     * Sets whether Step over button is enabled.
     *
     * @param isEnable
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableStepOverButton(boolean isEnable);

    /** Change state for StepOverButton. */
    boolean resetStepOverButton(boolean state);

    /**
     * Sets whether Step return button is enabled.
     *
     * @param isEnable
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableStepReturnButton(boolean isEnable);

    /** Change  state for StepReturnButton. */
    boolean resetStepReturnButton(boolean state);

    /**
     * Sets whether Change value button is enabled.
     *
     * @param button
     *         the instance of button widget
     * @param state
     *         the new state of button
     */
    public boolean setButtonState(ToggleButton button, boolean state);

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
     *         title of view
     */
    void setTitle(@NotNull String title);

    /** Update contents for selected variable. */
    void updateSelectedVariable();

    /**
     * Add elements into selected variable.
     *
     * @param variables
     *         variable what need to add into
     */
    void setVariablesIntoSelectedVariable(@NotNull List<Variable> variables);
}