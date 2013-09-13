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
package org.exoplatform.ide.extension.java.jdi.client;

/** Interface to represent the messages contained in resource bundle: DebugLocalizationConstant.properties'. */
public interface DebuggerLocalizationConstant extends com.google.gwt.i18n.client.Messages {

    @DefaultMessage("Run/Debug")
    @Key("control.launchDebuggerControlId")
    String debugAppControlId();

    @DefaultMessage("Run/Run")
    @Key("control.runAppControlId")
    String runAppControlId();

    @DefaultMessage("Show Logs...")
    @Key("control.show.logs.title")
    String showLogsControlTitle();

    @DefaultMessage("Show Application Logs...")
    @Key("control.show.logs.prompt")
    String showLogsControlPrompt();

    @DefaultMessage("Run/Stop")
    @Key("control.stopAppControlId")
    String stopAppControlId();

    @DefaultMessage("Run/Update")
    @Key("control.updateApp.id")
    String updateAppControlId();

    @DefaultMessage("Update Application")
    @Key("control.updateApp.title")
    String updateAppControlTitle();

    @DefaultMessage("Update Application")
    @Key("control.updateApp.prompt")
    String updateAppControlPrompt();

    @DefaultMessage("Run/Breakpoint Properties")
    @Key("control.showBreakpointProperties.id")
    String showBreakpointPropertiesControlId();

    @DefaultMessage("Breakpoint Properties")
    @Key("control.showBreakpointProperties.title")
    String showBreakpointPropertiesControlTitle();

    @DefaultMessage("Breakpoint Properties")
    @Key("control.showBreakpointProperties.prompt")
    String showBreakpointPropertiesControlPrompt();

    /**
     * Translated "BreakPoints".
     *
     * @return translated "BreakPoints"
     */
    @DefaultMessage("BreakPoints")
    @Key("breakPoints")
    String breakPoints();

    /**
     * Translated "Cancel".
     *
     * @return translated "Cancel"
     */
    @DefaultMessage("Cancel")
    @Key("closeButton")
    String cancelButton();

    /**
     * Translated "Connect".
     *
     * @return translated "Connect"
     */
    @DefaultMessage("Connect")
    @Key("connectButton")
    String connectButton();

    /**
     * Translated "Debug".
     *
     * @return translated "Debug"
     */
    @DefaultMessage("Debug")
    @Key("debug")
    String debug();

    @DefaultMessage("Connection debugger...")
    @Key("notStartedYet")
    String notStartedYet();

    /**
     * Translated "Disconnect".
     *
     * @return translated "Disconnect"
     */
    @DefaultMessage("Disconnect")
    @Key("disConnectButton")
    String disConnectButton();

    /**
     * Translated "Help".
     *
     * @return translated "Help"
     */
    @DefaultMessage("Help")
    @Key("helpButton")
    String helpButton();

    /**
     * Translated "Host".
     *
     * @return translated "Host"
     */
    @DefaultMessage("Host")
    @Key("host")
    String host();

    /**
     * Translated "Ok".
     *
     * @return translated "Ok"
     */
    @DefaultMessage("Ok")
    @Key("okButton")
    String okButton();

    /**
     * Translated "Port".
     *
     * @return translated "Port"
     */
    @DefaultMessage("Port")
    @Key("port")
    String port();

    @DefaultMessage("Application URL")
    @Key("appUrl")
    String appUrl();

    /**
     * Translated "VM Version".
     *
     * @return translated ""
     */
    @DefaultMessage("VM Version")
    @Key("vmVersion")
    String vmVersion();

    /**
     * Translated "VM Name".
     *
     * @return translated "VM Name"
     */
    @DefaultMessage("VM Name")
    @Key("vmName")
    String vmName();

    /**
     * Translated "Remove All BreakPoints".
     *
     * @return translated "Remove All BreakPoints"
     */
    @DefaultMessage("Remove All BreakPoints")
    @Key("removeBreakPointsButton")
    String removeBreakPointsButton();

    /**
     * Translated "Resume".
     *
     * @return translated "Resume"
     */
    @DefaultMessage("Resume")
    @Key("resumeButton")
    String resumeButton();

    /**
     * Translated "Run".
     *
     * @return translated "Run"
     */
    @DefaultMessage("Run")
    @Key("runButton")
    String runButton();

    /**
     * Translated "Variables".
     *
     * @return translated "Variables"
     */
    @DefaultMessage("Variables")
    @Key("variables")
    String variables();

    @DefaultMessage("Step Into")
    @Key("stepInto")
    String stepInto();

    @DefaultMessage("Step Over")
    @Key("stepOver")
    String stepOver();

    @DefaultMessage("Step Return")
    @Key("stepReturn")
    String stepReturn();

    @DefaultMessage("Change Value")
    @Key("changeValue")
    String changeValue();

    @DefaultMessage("Evaluate Expression")
    @Key("evaluateExpression")
    String evaluateExpression();

    @DefaultMessage("Application starting...")
    @Key("appStarting")
    String applicationStarting();

    @DefaultMessage("Application <b>{0}</b> started.")
    @Key("appStarted")
    String applicationStarted(String name);

    @DefaultMessage("Application <b>{0}</b> stopped.")
    @Key("appStoped")
    String applicationStoped(String name);

    @DefaultMessage("Application <b>{0}</b> started on {1}")
    @Key("appStarted.uris")
    String applicationStartedOnUrls(String name, String uris);

    @DefaultMessage("Updated application <b>{0}</b> on {1}.")
    @Key("appUpdated")
    String applicationUpdated(String name, String uris);

    @DefaultMessage("Update application <b>{0}</b> failed.")
    @Key("updateAppFailed")
    String updateApplicationFailed(String name);

    @DefaultMessage("Start application failed.")
    @Key("startAppFailed")
    String startApplicationFailed();

    @DefaultMessage("Stop application failed.")
    @Key("stop.application.failed")
    String stopApplicationFailed();

    @DefaultMessage("Debugger is disconnected.")
    @Key("debugger.disconnected")
    String debuggerDisconnected();

    @DefaultMessage("Failed to retrieve logs.")
    @Key("get.logs.error.message")
    String getLogsErrorMessage();

    @DefaultMessage("There is no running application.")
    @Key("no.running.application.message")
    String noRunningApplicationMessage();

    @DefaultMessage("Running time has expired")
    @Key("prolong.time.application.title")
    String prolongExpirationTimeTitle();

    @DefaultMessage("Application will be stopped in less than 2 minutes.\nDo you want to prolong the expiration time by 10 minutes?")
    @Key("prolong.time.application.question")
    String prolongExpirationTimeQuestion();

    @DefaultMessage("Failed to prolong expiration time of the application.")
    @Key("prolong.time.application.failed")
    String prolongExpirationTimeFailed();

    /*************************************************************************
     * Change value view
     *************************************************************************/

    /**
     * Translated "Change variable value".
     *
     * @return translated "Change variable value"
     */
    @DefaultMessage("Change variable value")
    @Key("view.changeValue.title")
    String changeValueViewTitle();

    /**
     * Translated "Enter an expression".
     *
     * @return translated "Enter an expression"
     */
    @DefaultMessage("Enter a new value for <b>{0}</b>:")
    @Key("view.changeValue.expressionField.title")
    String changeValueViewExpressionFieldTitle(String varName);

    /**
     * Translated "Change".
     *
     * @return translated "Change"
     */
    @DefaultMessage("Change")
    @Key("view.changeValue.changeButton.title")
    String changeValueViewChangeButtonTitle();

    /**
     * Translated "Cancel".
     *
     * @return translated "Cancel"
     */
    @DefaultMessage("Cancel")
    @Key("view.changeValue.cancelButton.title")
    String changeValueViewCancelButtonTitle();
    
    /*************************************************************************
     * JRebel value view
     *************************************************************************/

    /**
     * Translated "JRebel".
     *
     * @return translated "JRebel"
     */
    @DefaultMessage("JRebel")
    @Key("view.jRebelUserInfo.title")
    String jRebelUserInfoViewTitle();
    
    @DefaultMessage("Tell us a little about yourself to continue using JRebel.")
    @Key("view.jRebelUserInfo.label1.title")
    String jRebelUserInfoViewLabelWelcome1();
    
    @DefaultMessage("JRebel is great and Zero Turnaround is letting you use it free of charge. Help us help them!")
    @Key("view.jRebelUserInfo.label2.title")
    String jRebelUserInfoViewLabelWelcome2();
    
    @DefaultMessage("Redeploy failed. Please tell us about yourself to continue using JRebel.")
    @Key("jrebel.redeploy.failed")
    String jRebelRedeployFailed();
    
    /*************************************************************************
     * Evaluate expression view
     *************************************************************************/

    /**
     * Translated "Evaluate expression".
     *
     * @return translated "Evaluate expression"
     */
    @DefaultMessage("Evaluate expression")
    @Key("view.evaluateExpression.title")
    String evaluateExpressionViewTitle();

    /**
     * Translated "Enter an expression".
     *
     * @return translated "Enter an expression"
     */
    @DefaultMessage("Enter an expression:")
    @Key("view.evaluateExpression.expressionField.title")
    String evaluateExpressionViewExpressionFieldTitle();

    /**
     * Translated "Result".
     *
     * @return translated "Result"
     */
    @DefaultMessage("Result:")
    @Key("view.evaluateExpression.resultField.title")
    String evaluateExpressionViewResultFieldTitle();

    /**
     * Translated "Evaluate".
     *
     * @return translated "Evaluate"
     */
    @DefaultMessage("Evaluate")
    @Key("view.evaluateExpression.evaluateButton.title")
    String evaluateExpressionViewEvaluateButtonTitle();

    /**
     * Translated "Close".
     *
     * @return translated "Close"
     */
    @DefaultMessage("Close")
    @Key("view.evaluateExpression.closeButton.title")
    String evaluateExpressionViewCloseButtonTitle();

    @DefaultMessage("Error: Failed to evaluate expression.\r\nReason: {0}")
    @Key("evaluateExpressionFailed")
    String evaluateExpressionFailed(String reason);

    /**
     * **********************************************************************
     * Dialog
     * ***********************************************************************
     */
    @DefaultMessage("Debug session still active")
    @Key("dialog.close.project.title")
    String dialogCloseProjectTitle();

    @DefaultMessage(
            "You are about to close the project with a running debug session. If you press \"Yes\" the debug session will be terminated.")
    @Key("dialog.close.project.msg")
    String dialogCloseProjectMsg();


    /*************************************************************************
     * Breakpoint properties view
     *************************************************************************/

    /**
     * Translated "Breakpoint properties".
     *
     * @return translated "Breakpoint properties"
     */
    @DefaultMessage("Breakpoint properties")
    @Key("view.breakpointProperties.title")
    String breakpointPropertiesViewTitle();

    /**
     * Translated "Condition".
     *
     * @return translated "Condition"
     */
    @DefaultMessage("Condition:")
    @Key("view.breakpointProperties.conditionField.title")
    String breakpointPropertiesViewConditionFieldTitle();

    /**
     * Translated "OK".
     *
     * @return translated "OK"
     */
    @DefaultMessage("OK")
    @Key("view.breakpointProperties.OKButton.title")
    String breakpointPropertiesViewOKButtonTitle();

    /**
     * Translated "Cancel".
     *
     * @return translated "Cancel"
     */
    @DefaultMessage("Cancel")
    @Key("view.breakpointProperties.cancelButton.title")
    String breakpointPropertiesViewCancelButtonTitle();

    @DefaultMessage("Starting <b>{0}</b>")
    @Key("run.app.starting")
    String starting(String project);

    @DefaultMessage("<b>{0}</b> : started")
    @Key("run.app.started")
    String started(String project);
}
