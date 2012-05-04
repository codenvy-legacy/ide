package org.exoplatform.ide.extension.java.jdi.client;

/**
 * Interface to represent the messages contained in resource bundle:
 * 	/home/vetal/eXo/eXoProjects/ide/trunk/exo-ide-extension-java-debug/src/main/resources/org/exoplatform/ide/extension/java/jdi/client/DebugLocalizationConstant.properties'.
 */
public interface DebuggerLocalizationConstant extends com.google.gwt.i18n.client.Messages
{

   @DefaultMessage("Project/Debug")
   @Key("control.buildProject.id")
   String launchDebuggerControlId();

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
   @Key("cancelButton")
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

   @DefaultMessage("Application <b>{0}</b> started")
   @Key("appStarted")
   String applicationStarted(String name);

   @DefaultMessage("Application <b>{0}</b> stoped")
   @Key("appStoped")
   String applicationStoped(String name);

   @DefaultMessage("Application <b>{0}</b> started on {1}")
   @Key("appStarted.uris")
   String applicationStartedOnUrls(String name, String uris);

   @DefaultMessage("Start application failed.")
   @Key("startAppFailed")
   String startApplicationFailed();

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
    * Translated "Change variable value".
    * 
    * @return translated "Change variable value"
    */
   @DefaultMessage("Enter an expression:")
   @Key("view.changeValue.expressionField.title")
   String expressionFieldTitle();

   /**
    * Translated "Change".
    * 
    * @return translated "Change"
    */
   @DefaultMessage("Change")
   @Key("view.changeValue.changeButton.title")
   String changeButtonTitle();

   /**
    * Translated "Cancel".
    * 
    * @return translated "Cancel"
    */
   @DefaultMessage("Cancel")
   @Key("view.changeValue.cancelButton.title")
   String cancelButtonTitle();

}
