/*
 * CODENVY CONFIDENTIAL
 *__________________
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
package com.codenvy.ide.ext.java.messages.impl;



@SuppressWarnings({"unchecked", "cast"})
public class MessagesImpls {

  private  MessagesImpls() {}

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "c07134267e9ca1a38b4f62f1201245bf77236619";


  public static class ConfigMessageImpl extends com.google.gwt.webworker.client.messages.MessageImpl implements com.codenvy.ide.ext.java.messages.ConfigMessage {
    protected ConfigMessageImpl() {}

    @Override
    public final native java.lang.String restContext() /*-{
      return this["restContext"];
    }-*/;

    public final native ConfigMessageImpl setRestContext(java.lang.String restContext) /*-{
      this["restContext"] = restContext;
      return this;
    }-*/;

    public final native boolean hasRestContext() /*-{
      return this.hasOwnProperty("restContext");
    }-*/;

    @Override
    public final native java.lang.String vfsId() /*-{
      return this["vfsId"];
    }-*/;

    public final native ConfigMessageImpl setVfsId(java.lang.String vfsId) /*-{
      this["vfsId"] = vfsId;
      return this;
    }-*/;

    public final native boolean hasVfsId() /*-{
      return this.hasOwnProperty("vfsId");
    }-*/;

    @Override
    public final native java.lang.String wsName() /*-{
      return this["wsName"];
    }-*/;

    public final native ConfigMessageImpl setWsName(java.lang.String wsName) /*-{
      this["wsName"] = wsName;
      return this;
    }-*/;

    public final native boolean hasWsName() /*-{
      return this.hasOwnProperty("wsName");
    }-*/;

    @Override
    public final native java.lang.String projectId() /*-{
      return this["projectId"];
    }-*/;

    public final native ConfigMessageImpl setProjectId(java.lang.String projectId) /*-{
      this["projectId"] = projectId;
      return this;
    }-*/;

    public final native boolean hasProjectId() /*-{
      return this.hasOwnProperty("projectId");
    }-*/;

    public static native ConfigMessageImpl make() /*-{
      return {
        _type: 1
      };
    }-*/;  }


  public static class ParseMessageImpl extends com.google.gwt.webworker.client.messages.MessageImpl implements com.codenvy.ide.ext.java.messages.ParseMessage {
    protected ParseMessageImpl() {}

    @Override
    public final native java.lang.String source() /*-{
      return this["source"];
    }-*/;

    public final native ParseMessageImpl setSource(java.lang.String source) /*-{
      this["source"] = source;
      return this;
    }-*/;

    public final native boolean hasSource() /*-{
      return this.hasOwnProperty("source");
    }-*/;

    @Override
    public final native java.lang.String fileName() /*-{
      return this["fileName"];
    }-*/;

    public final native ParseMessageImpl setFileName(java.lang.String fileName) /*-{
      this["fileName"] = fileName;
      return this;
    }-*/;

    public final native boolean hasFileName() /*-{
      return this.hasOwnProperty("fileName");
    }-*/;

    @Override
    public final native java.lang.String id() /*-{
      return this["id"];
    }-*/;

    public final native ParseMessageImpl setId(java.lang.String id) /*-{
      this["id"] = id;
      return this;
    }-*/;

    public final native boolean hasId() /*-{
      return this.hasOwnProperty("id");
    }-*/;

    public static native ParseMessageImpl make() /*-{
      return {
        _type: 2
      };
    }-*/;  }


  public static class ProblemImpl extends com.google.gwt.webworker.client.messages.MessageImpl implements com.codenvy.ide.ext.java.messages.Problem {
    protected ProblemImpl() {}

    @Override
    public final native java.lang.String originatingFileName() /*-{
      return this[0];
    }-*/;

    public final native ProblemImpl setOriginatingFileName(java.lang.String originatingFileName) /*-{
      this[0] = originatingFileName;
      return this;
    }-*/;

    public final native boolean hasOriginatingFileName() /*-{
      return this.hasOwnProperty(0);
    }-*/;

    @Override
    public final native java.lang.String message() /*-{
      return this[1];
    }-*/;

    public final native ProblemImpl setMessage(java.lang.String message) /*-{
      this[1] = message;
      return this;
    }-*/;

    public final native boolean hasMessage() /*-{
      return this.hasOwnProperty(1);
    }-*/;

    @Override
    public final native int id() /*-{
      return this[2];
    }-*/;

    public final native ProblemImpl setId(int id) /*-{
      this[2] = id;
      return this;
    }-*/;

    public final native boolean hasId() /*-{
      return this.hasOwnProperty(2);
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<java.lang.String> stringArguments() /*-{
      return this[3];
    }-*/;

    public final native ProblemImpl setStringArguments(com.codenvy.ide.json.js.JsoArray<java.lang.String> stringArguments) /*-{
      this[3] = stringArguments;
      return this;
    }-*/;

    public final native boolean hasStringArguments() /*-{
      return this.hasOwnProperty(3);
    }-*/;

    @Override
    public final native int severity() /*-{
      return this[4];
    }-*/;

    public final native ProblemImpl setSeverity(int severity) /*-{
      this[4] = severity;
      return this;
    }-*/;

    public final native boolean hasSeverity() /*-{
      return this.hasOwnProperty(4);
    }-*/;

    @Override
    public final native int startPosition() /*-{
      return this[5];
    }-*/;

    public final native ProblemImpl setStartPosition(int startPosition) /*-{
      this[5] = startPosition;
      return this;
    }-*/;

    public final native boolean hasStartPosition() /*-{
      return this.hasOwnProperty(5);
    }-*/;

    @Override
    public final native int endPosition() /*-{
      return this[6];
    }-*/;

    public final native ProblemImpl setEndPosition(int endPosition) /*-{
      this[6] = endPosition;
      return this;
    }-*/;

    public final native boolean hasEndPosition() /*-{
      return this.hasOwnProperty(6);
    }-*/;

    @Override
    public final native int line() /*-{
      return this[7];
    }-*/;

    public final native ProblemImpl setLine(int line) /*-{
      this[7] = line;
      return this;
    }-*/;

    public final native boolean hasLine() /*-{
      return this.hasOwnProperty(7);
    }-*/;

    @Override
    public final native int column() /*-{
      return this[8];
    }-*/;

    public final native ProblemImpl setColumn(int column) /*-{
      this[8] = column;
      return this;
    }-*/;

    public final native boolean hasColumn() /*-{
      return this.hasOwnProperty(8);
    }-*/;

    public static native ProblemImpl make() /*-{
      return [];
    }-*/;  }


  public static class ProblemsMessageImpl extends com.google.gwt.webworker.client.messages.MessageImpl implements com.codenvy.ide.ext.java.messages.ProblemsMessage {
    protected ProblemsMessageImpl() {}

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.java.messages.Problem> problems() /*-{
      return this["problems"];
    }-*/;

    public final native ProblemsMessageImpl setProblems(com.codenvy.ide.json.js.JsoArray<com.codenvy.ide.ext.java.messages.Problem> problems) /*-{
      this["problems"] = problems;
      return this;
    }-*/;

    public final native boolean hasProblems() /*-{
      return this.hasOwnProperty("problems");
    }-*/;

    @Override
    public final native java.lang.String id() /*-{
      return this["id"];
    }-*/;

    public final native ProblemsMessageImpl setId(java.lang.String id) /*-{
      this["id"] = id;
      return this;
    }-*/;

    public final native boolean hasId() /*-{
      return this.hasOwnProperty("id");
    }-*/;

    public static native ProblemsMessageImpl make() /*-{
      return {
        _type: 3
      };
    }-*/;  }

}