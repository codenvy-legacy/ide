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


// GENERATED SOURCE. DO NOT EDIT.
package com.codenvy.ide.ext.java.jdi.dto.client;



@SuppressWarnings({"unchecked", "cast"})
public class DtoClientImpls {

  private  DtoClientImpls() {}

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "e0934a80cd2f31eb9cb0a8bbf30c6da40e470921";


  public static class StepEventImpl extends DebuggerEventImpl implements com.codenvy.ide.ext.java.jdi.shared.StepEvent {
    protected StepEventImpl() {}

    @Override
    public final native com.codenvy.ide.ext.java.jdi.shared.Location getLocation() /*-{
      return this["location"];
    }-*/;

    public final native StepEventImpl setLocation(com.codenvy.ide.ext.java.jdi.shared.Location location) /*-{
      this["location"] = location;
      return this;
    }-*/;

    public final native boolean hasLocation() /*-{
      return this.hasOwnProperty("location");
    }-*/;

    public static native StepEventImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class ValueImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.java.jdi.shared.Value {
    protected ValueImpl() {}

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.java.jdi.shared.Variable> getVariables() /*-{
      return this["variables"];
    }-*/;

    public final native ValueImpl setVariables(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.java.jdi.shared.Variable> variables) /*-{
      this["variables"] = variables;
      return this;
    }-*/;

    public final native boolean hasVariables() /*-{
      return this.hasOwnProperty("variables");
    }-*/;

    @Override
    public final native java.lang.String getValue() /*-{
      return this["value"];
    }-*/;

    public final native ValueImpl setValue(java.lang.String value) /*-{
      this["value"] = value;
      return this;
    }-*/;

    public final native boolean hasValue() /*-{
      return this.hasOwnProperty("value");
    }-*/;

    public static native ValueImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class LocationImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.java.jdi.shared.Location {
    protected LocationImpl() {}

    @Override
    public final native java.lang.String getClassName() /*-{
      return this["className"];
    }-*/;

    public final native LocationImpl setClassName(java.lang.String className) /*-{
      this["className"] = className;
      return this;
    }-*/;

    public final native boolean hasClassName() /*-{
      return this.hasOwnProperty("className");
    }-*/;

    @Override
    public final native int getLineNumber() /*-{
      return this["lineNumber"];
    }-*/;

    public final native LocationImpl setLineNumber(int lineNumber) /*-{
      this["lineNumber"] = lineNumber;
      return this;
    }-*/;

    public final native boolean hasLineNumber() /*-{
      return this.hasOwnProperty("lineNumber");
    }-*/;

    public static native LocationImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class DebuggerEventImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.java.jdi.shared.DebuggerEvent {
    protected DebuggerEventImpl() {}

    @Override
    public final native int getType() /*-{
      return this["type"];
    }-*/;

    public final native DebuggerEventImpl setType(int type) /*-{
      this["type"] = type;
      return this;
    }-*/;

    public final native boolean hasType() /*-{
      return this.hasOwnProperty("type");
    }-*/;

    public static native DebuggerEventImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class DebuggerEventListImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.java.jdi.shared.DebuggerEventList {
    protected DebuggerEventListImpl() {}

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.java.jdi.shared.DebuggerEvent> getEvents() /*-{
      return this["events"];
    }-*/;

    public final native DebuggerEventListImpl setEvents(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.java.jdi.shared.DebuggerEvent> events) /*-{
      this["events"] = events;
      return this;
    }-*/;

    public final native boolean hasEvents() /*-{
      return this.hasOwnProperty("events");
    }-*/;

    public static native DebuggerEventListImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class BreakPointEventListImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.java.jdi.shared.BreakPointEventList {
    protected BreakPointEventListImpl() {}

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.java.jdi.shared.BreakPointEvent> getEvents() /*-{
      return this["events"];
    }-*/;

    public final native BreakPointEventListImpl setEvents(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.java.jdi.shared.BreakPointEvent> events) /*-{
      this["events"] = events;
      return this;
    }-*/;

    public final native boolean hasEvents() /*-{
      return this.hasOwnProperty("events");
    }-*/;

    public static native BreakPointEventListImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class BreakPointEventImpl extends DebuggerEventImpl implements com.codenvy.ide.ext.java.jdi.shared.BreakPointEvent {
    protected BreakPointEventImpl() {}

    @Override
    public final native com.codenvy.ide.ext.java.jdi.shared.BreakPoint getBreakPoint() /*-{
      return this["breakPoint"];
    }-*/;

    public final native BreakPointEventImpl setBreakPoint(com.codenvy.ide.ext.java.jdi.shared.BreakPoint breakPoint) /*-{
      this["breakPoint"] = breakPoint;
      return this;
    }-*/;

    public final native boolean hasBreakPoint() /*-{
      return this.hasOwnProperty("breakPoint");
    }-*/;

    public static native BreakPointEventImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class BreakPointImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.java.jdi.shared.BreakPoint {
    protected BreakPointImpl() {}

    @Override
    public final native com.codenvy.ide.ext.java.jdi.shared.Location getLocation() /*-{
      return this["location"];
    }-*/;

    public final native BreakPointImpl setLocation(com.codenvy.ide.ext.java.jdi.shared.Location location) /*-{
      this["location"] = location;
      return this;
    }-*/;

    public final native boolean hasLocation() /*-{
      return this.hasOwnProperty("location");
    }-*/;

    @Override
    public final native boolean isEnabled() /*-{
      return this["isEnabled"];
    }-*/;

    public final native BreakPointImpl setIsEnabled(boolean isEnabled) /*-{
      this["isEnabled"] = isEnabled;
      return this;
    }-*/;

    public final native boolean hasIsEnabled() /*-{
      return this.hasOwnProperty("isEnabled");
    }-*/;

    @Override
    public final native java.lang.String getCondition() /*-{
      return this["condition"];
    }-*/;

    public final native BreakPointImpl setCondition(java.lang.String condition) /*-{
      this["condition"] = condition;
      return this;
    }-*/;

    public final native boolean hasCondition() /*-{
      return this.hasOwnProperty("condition");
    }-*/;

    public static native BreakPointImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class ApplicationInstanceImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.java.jdi.shared.ApplicationInstance {
    protected ApplicationInstanceImpl() {}

    @Override
    public final native int getLifetime() /*-{
      return this["lifetime"];
    }-*/;

    public final native ApplicationInstanceImpl setLifetime(int lifetime) /*-{
      this["lifetime"] = lifetime;
      return this;
    }-*/;

    public final native boolean hasLifetime() /*-{
      return this.hasOwnProperty("lifetime");
    }-*/;

    @Override
    public final native java.lang.String getDebugHost() /*-{
      return this["debugHost"];
    }-*/;

    public final native ApplicationInstanceImpl setDebugHost(java.lang.String debugHost) /*-{
      this["debugHost"] = debugHost;
      return this;
    }-*/;

    public final native boolean hasDebugHost() /*-{
      return this.hasOwnProperty("debugHost");
    }-*/;

    @Override
    public final native int getDebugPort() /*-{
      return this["debugPort"];
    }-*/;

    public final native ApplicationInstanceImpl setDebugPort(int debugPort) /*-{
      this["debugPort"] = debugPort;
      return this;
    }-*/;

    public final native boolean hasDebugPort() /*-{
      return this.hasOwnProperty("debugPort");
    }-*/;

    @Override
    public final native java.lang.String getStopURL() /*-{
      return this["stopURL"];
    }-*/;

    public final native ApplicationInstanceImpl setStopURL(java.lang.String stopURL) /*-{
      this["stopURL"] = stopURL;
      return this;
    }-*/;

    public final native boolean hasStopURL() /*-{
      return this.hasOwnProperty("stopURL");
    }-*/;

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native ApplicationInstanceImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native java.lang.String getHost() /*-{
      return this["host"];
    }-*/;

    public final native ApplicationInstanceImpl setHost(java.lang.String host) /*-{
      this["host"] = host;
      return this;
    }-*/;

    public final native boolean hasHost() /*-{
      return this.hasOwnProperty("host");
    }-*/;

    @Override
    public final native int getPort() /*-{
      return this["port"];
    }-*/;

    public final native ApplicationInstanceImpl setPort(int port) /*-{
      this["port"] = port;
      return this;
    }-*/;

    public final native boolean hasPort() /*-{
      return this.hasOwnProperty("port");
    }-*/;

    public static native ApplicationInstanceImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class BreakPointListImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.java.jdi.shared.BreakPointList {
    protected BreakPointListImpl() {}

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.java.jdi.shared.BreakPoint> getBreakPoints() /*-{
      return this["breakPoints"];
    }-*/;

    public final native BreakPointListImpl setBreakPoints(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.java.jdi.shared.BreakPoint> breakPoints) /*-{
      this["breakPoints"] = breakPoints;
      return this;
    }-*/;

    public final native boolean hasBreakPoints() /*-{
      return this.hasOwnProperty("breakPoints");
    }-*/;

    public static native BreakPointListImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class DebuggerInfoImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.java.jdi.shared.DebuggerInfo {
    protected DebuggerInfoImpl() {}

    @Override
    public final native java.lang.String getVmName() /*-{
      return this["vmName"];
    }-*/;

    public final native DebuggerInfoImpl setVmName(java.lang.String vmName) /*-{
      this["vmName"] = vmName;
      return this;
    }-*/;

    public final native boolean hasVmName() /*-{
      return this.hasOwnProperty("vmName");
    }-*/;

    @Override
    public final native java.lang.String getVmVersion() /*-{
      return this["vmVersion"];
    }-*/;

    public final native DebuggerInfoImpl setVmVersion(java.lang.String vmVersion) /*-{
      this["vmVersion"] = vmVersion;
      return this;
    }-*/;

    public final native boolean hasVmVersion() /*-{
      return this.hasOwnProperty("vmVersion");
    }-*/;

    @Override
    public final native java.lang.String getId() /*-{
      return this["id"];
    }-*/;

    public final native DebuggerInfoImpl setId(java.lang.String id) /*-{
      this["id"] = id;
      return this;
    }-*/;

    public final native boolean hasId() /*-{
      return this.hasOwnProperty("id");
    }-*/;

    @Override
    public final native java.lang.String getHost() /*-{
      return this["host"];
    }-*/;

    public final native DebuggerInfoImpl setHost(java.lang.String host) /*-{
      this["host"] = host;
      return this;
    }-*/;

    public final native boolean hasHost() /*-{
      return this.hasOwnProperty("host");
    }-*/;

    @Override
    public final native int getPort() /*-{
      return this["port"];
    }-*/;

    public final native DebuggerInfoImpl setPort(int port) /*-{
      this["port"] = port;
      return this;
    }-*/;

    public final native boolean hasPort() /*-{
      return this.hasOwnProperty("port");
    }-*/;

    public static native DebuggerInfoImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class FieldImpl extends VariableImpl implements com.codenvy.ide.ext.java.jdi.shared.Field {
    protected FieldImpl() {}

    @Override
    public final native boolean isFinal() /*-{
      return this["isFinal"];
    }-*/;

    public final native FieldImpl setIsFinal(boolean isFinal) /*-{
      this["isFinal"] = isFinal;
      return this;
    }-*/;

    public final native boolean hasIsFinal() /*-{
      return this.hasOwnProperty("isFinal");
    }-*/;

    @Override
    public final native boolean isStatic() /*-{
      return this["isStatic"];
    }-*/;

    public final native FieldImpl setIsStatic(boolean isStatic) /*-{
      this["isStatic"] = isStatic;
      return this;
    }-*/;

    public final native boolean hasIsStatic() /*-{
      return this.hasOwnProperty("isStatic");
    }-*/;

    @Override
    public final native boolean isTransient() /*-{
      return this["isTransient"];
    }-*/;

    public final native FieldImpl setIsTransient(boolean isTransient) /*-{
      this["isTransient"] = isTransient;
      return this;
    }-*/;

    public final native boolean hasIsTransient() /*-{
      return this.hasOwnProperty("isTransient");
    }-*/;

    @Override
    public final native boolean isVolatile() /*-{
      return this["isVolatile"];
    }-*/;

    public final native FieldImpl setIsVolatile(boolean isVolatile) /*-{
      this["isVolatile"] = isVolatile;
      return this;
    }-*/;

    public final native boolean hasIsVolatile() /*-{
      return this.hasOwnProperty("isVolatile");
    }-*/;

    public static native FieldImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class UpdateVariableRequestImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.java.jdi.shared.UpdateVariableRequest {
    protected UpdateVariableRequestImpl() {}

    @Override
    public final native com.codenvy.ide.ext.java.jdi.shared.VariablePath getVariablePath() /*-{
      return this["variablePath"];
    }-*/;

    public final native UpdateVariableRequestImpl setVariablePath(com.codenvy.ide.ext.java.jdi.shared.VariablePath variablePath) /*-{
      this["variablePath"] = variablePath;
      return this;
    }-*/;

    public final native boolean hasVariablePath() /*-{
      return this.hasOwnProperty("variablePath");
    }-*/;

    @Override
    public final native java.lang.String getExpression() /*-{
      return this["expression"];
    }-*/;

    public final native UpdateVariableRequestImpl setExpression(java.lang.String expression) /*-{
      this["expression"] = expression;
      return this;
    }-*/;

    public final native boolean hasExpression() /*-{
      return this.hasOwnProperty("expression");
    }-*/;

    public static native UpdateVariableRequestImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class VariablePathImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.java.jdi.shared.VariablePath {
    protected VariablePathImpl() {}

    @Override
    public final native com.codenvy.ide.json.JsonArray<java.lang.String> getPath() /*-{
      return this["path"];
    }-*/;

    public final native VariablePathImpl setPath(com.codenvy.ide.json.JsonArray<java.lang.String> path) /*-{
      this["path"] = path;
      return this;
    }-*/;

    public final native boolean hasPath() /*-{
      return this.hasOwnProperty("path");
    }-*/;

    public static native VariablePathImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class VariableImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.java.jdi.shared.Variable {
    protected VariableImpl() {}

    @Override
    public final native com.codenvy.ide.ext.java.jdi.shared.VariablePath getVariablePath() /*-{
      return this["variablePath"];
    }-*/;

    public final native VariableImpl setVariablePath(com.codenvy.ide.ext.java.jdi.shared.VariablePath variablePath) /*-{
      this["variablePath"] = variablePath;
      return this;
    }-*/;

    public final native boolean hasVariablePath() /*-{
      return this.hasOwnProperty("variablePath");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.java.jdi.shared.Variable> getVariables() /*-{
      return this["variables"];
    }-*/;

    public final native VariableImpl setVariables(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.java.jdi.shared.Variable> variables) /*-{
      this["variables"] = variables;
      return this;
    }-*/;

    public final native boolean hasVariables() /*-{
      return this.hasOwnProperty("variables");
    }-*/;

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native VariableImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native java.lang.String getValue() /*-{
      return this["value"];
    }-*/;

    public final native VariableImpl setValue(java.lang.String value) /*-{
      this["value"] = value;
      return this;
    }-*/;

    public final native boolean hasValue() /*-{
      return this.hasOwnProperty("value");
    }-*/;

    @Override
    public final native java.lang.String getType() /*-{
      return this["type"];
    }-*/;

    public final native VariableImpl setType(java.lang.String type) /*-{
      this["type"] = type;
      return this;
    }-*/;

    public final native boolean hasType() /*-{
      return this.hasOwnProperty("type");
    }-*/;

    @Override
    public final native boolean primitive() /*-{
      return this["primitive"];
    }-*/;

    public final native VariableImpl setPrimitive(boolean primitive) /*-{
      this["primitive"] = primitive;
      return this;
    }-*/;

    public final native boolean hasPrimitive() /*-{
      return this.hasOwnProperty("primitive");
    }-*/;

    public static native VariableImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class StackFrameDumpImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.java.jdi.shared.StackFrameDump {
    protected StackFrameDumpImpl() {}

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.java.jdi.shared.Variable> getLocalVariables() /*-{
      return this["localVariables"];
    }-*/;

    public final native StackFrameDumpImpl setLocalVariables(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.java.jdi.shared.Variable> localVariables) /*-{
      this["localVariables"] = localVariables;
      return this;
    }-*/;

    public final native boolean hasLocalVariables() /*-{
      return this.hasOwnProperty("localVariables");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.java.jdi.shared.Field> getFields() /*-{
      return this["fields"];
    }-*/;

    public final native StackFrameDumpImpl setFields(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.java.jdi.shared.Field> fields) /*-{
      this["fields"] = fields;
      return this;
    }-*/;

    public final native boolean hasFields() /*-{
      return this.hasOwnProperty("fields");
    }-*/;

    public static native StackFrameDumpImpl make() /*-{
      return {

      };
    }-*/;  }

}