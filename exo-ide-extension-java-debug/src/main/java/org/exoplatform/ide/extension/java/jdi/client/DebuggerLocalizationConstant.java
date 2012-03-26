package org.exoplatform.ide.extension.java.jdi.client;


/**
 * Interface to represent the messages contained in resource bundle:
 * 	/home/vetal/eXo/eXoProjects/ide/trunk/exo-ide-extension-java-debug/src/main/resources/org/exoplatform/ide/extension/java/jdi/client/DebugLocalizationConstant.properties'.
 */
public interface DebuggerLocalizationConstant extends com.google.gwt.i18n.client.Messages {
  
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
  @Key("variabels")
  String variabels();
}
