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
package com.codenvy.ide.ext.java.jdi.client;

/**
 * Interface to represent the messages contained in resource bundle: 'JavaRuntimeLocalizationConstant.properties'.
 *
 * @author Artem Zatsarynnyy
 */
public interface JavaRuntimeLocalizationConstant extends com.google.gwt.i18n.client.Messages {
    /* Actions */
    @Key("control.debugApp.id")
    String debugAppActionId();

    @Key("control.debugApp.text")
    String debugAppActionText();

    @Key("control.debugApp.description")
    String debugAppActionDescription();

    @Key("control.showBreakpointProperties.id")
    String showBreakpointPropertiesControlId();

    @Key("control.showBreakpointProperties.title")
    String showBreakpointPropertiesControlTitle();

    @Key("control.showBreakpointProperties.prompt")
    String showBreakpointPropertiesControlPrompt();

    @Key("breakpoints")
    String breakpoints();

    @Key("cancelButton")
    String cancelButton();

    @Key("connectButton")
    String connectButton();

    @Key("debug")
    String debug();

    @Key("disconnectButton")
    String disconnectButton();

    @Key("helpButton")
    String helpButton();

    @Key("host")
    String host();

    @Key("okButton")
    String okButton();

    @Key("port")
    String port();

    @Key("vmVersion")
    String vmVersion();

    @Key("vmName")
    String vmName();

    @Key("removeBreakpointsButton")
    String removeBreakpointsButton();

    @Key("resumeButton")
    String resumeButton();

    @Key("runButton")
    String runButton();

    @Key("variables")
    String variables();

    @Key("stepInto")
    String stepInto();

    @Key("stepOver")
    String stepOver();

    @Key("stepReturn")
    String stepReturn();

    @Key("changeValue")
    String changeValue();

    @Key("evaluateExpression")
    String evaluateExpression();

    @Key("debugger.connected")
    String debuggerConnected(String address);

    @Key("debugger.disconnected")
    String debuggerDisconnected(String address);

    /**
     * **********************************************************************
     * Change value view
     * **********************************************************************
     */

    @Key("view.changeValue.title")
    String changeValueViewTitle();

    @Key("view.changeValue.expressionField.title")
    String changeValueViewExpressionFieldTitle(String varName);

    @Key("view.changeValue.changeButton.title")
    String changeValueViewChangeButtonTitle();

    @Key("view.changeValue.cancelButton.title")
    String changeValueViewCancelButtonTitle();

    /**
     * **********************************************************************
     * Evaluate expression view
     * **********************************************************************
     */

    @Key("view.evaluateExpression.title")
    String evaluateExpressionViewTitle();

    @Key("view.evaluateExpression.expressionField.title")
    String evaluateExpressionViewExpressionFieldTitle();

    @Key("view.evaluateExpression.resultField.title")
    String evaluateExpressionViewResultFieldTitle();

    @Key("view.evaluateExpression.evaluateButton.title")
    String evaluateExpressionViewEvaluateButtonTitle();

    @Key("view.evaluateExpression.closeButton.title")
    String evaluateExpressionViewCloseButtonTitle();

    @Key("evaluateExpressionFailed")
    String evaluateExpressionFailed(String reason);

    /**
     * **********************************************************************
     * Re-launch debugger view
     * **********************************************************************
     */
    @Key("notStartedYet")
    String notStartedYet();

    /**
     * **********************************************************************
     * Breakpoint properties view
     * **********************************************************************
     */

    @Key("view.breakpointProperties.title")
    String breakpointPropertiesViewTitle();

    @Key("view.breakpointProperties.conditionField.title")
    String breakpointPropertiesViewConditionFieldTitle();

    @Key("view.breakpointProperties.OKButton.title")
    String breakpointPropertiesViewOKButtonTitle();

    @Key("view.breakpointProperties.cancelButton.title")
    String breakpointPropertiesViewCancelButtonTitle();
}