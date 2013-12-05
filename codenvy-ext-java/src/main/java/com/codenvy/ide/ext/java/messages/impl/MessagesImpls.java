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


import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.js.JsoArray;
import com.codenvy.ide.ext.java.messages.Problem;

@SuppressWarnings({"unchecked", "cast"})
public class MessagesImpls {

  private  MessagesImpls() {}

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "2fb14f08eb554544bf90cc47f1f56e470f6f9302";


  public static class ApplyProposalMessageImpl extends com.google.gwt.webworker.client.messages.MessageImpl implements com.codenvy.ide.ext.java.messages.ApplyProposalMessage {
    protected ApplyProposalMessageImpl() {}

    @Override
    public final native java.lang.String id() /*-{
      return this["id"];
    }-*/;

    public final native ApplyProposalMessageImpl setId(java.lang.String id) /*-{
      this["id"] = id;
      return this;
    }-*/;

    public final native boolean hasId() /*-{
      return this.hasOwnProperty("id");
    }-*/;

    public static native ApplyProposalMessageImpl make() /*-{
      return {
        _type: 6
      };
    }-*/;  }


  public static class CAProposalsComputedMessageImpl extends com.google.gwt.webworker.client.messages.MessageImpl implements com.codenvy.ide.ext.java.messages.CAProposalsComputedMessage {
    protected CAProposalsComputedMessageImpl() {}

    @Override
    public final native com.codenvy.ide.collections.Array<com.codenvy.ide.ext.java.messages.WorkerProposal> proposals() /*-{
      return this["proposals"];
    }-*/;

    public final native CAProposalsComputedMessageImpl setProposals(com.codenvy.ide.collections.js.JsoArray<com.codenvy.ide.ext.java.messages.WorkerProposal> proposals) /*-{
      this["proposals"] = proposals;
      return this;
    }-*/;

    public final native boolean hasProposals() /*-{
      return this.hasOwnProperty("proposals");
    }-*/;

    @Override
    public final native java.lang.String id() /*-{
      return this["id"];
    }-*/;

    public final native CAProposalsComputedMessageImpl setId(java.lang.String id) /*-{
      this["id"] = id;
      return this;
    }-*/;

    public final native boolean hasId() /*-{
      return this.hasOwnProperty("id");
    }-*/;

    public static native CAProposalsComputedMessageImpl make() /*-{
      return {
        _type: 5
      };
    }-*/;  }


  public static class ChangeImpl extends com.google.gwt.webworker.client.messages.MessageImpl implements com.codenvy.ide.ext.java.messages.Change {
    protected ChangeImpl() {}

    @Override
    public final native int offset() /*-{
      return this[0];
    }-*/;

    public final native ChangeImpl setOffset(int offset) /*-{
      this[0] = offset;
      return this;
    }-*/;

    public final native boolean hasOffset() /*-{
      return this.hasOwnProperty(0);
    }-*/;

    @Override
    public final native int length() /*-{
      return this[1];
    }-*/;

    public final native ChangeImpl setLength(int length) /*-{
      this[1] = length;
      return this;
    }-*/;

    public final native boolean hasLength() /*-{
      return this.hasOwnProperty(1);
    }-*/;

    @Override
    public final native java.lang.String text() /*-{
      return this[2];
    }-*/;

    public final native ChangeImpl setText(java.lang.String text) /*-{
      this[2] = text;
      return this;
    }-*/;

    public final native boolean hasText() /*-{
      return this.hasOwnProperty(2);
    }-*/;

    public static native ChangeImpl make() /*-{
      return [];
    }-*/;  }


  public static class ComputeCAProposalsMessageImpl extends com.google.gwt.webworker.client.messages.MessageImpl implements com.codenvy.ide.ext.java.messages.ComputeCAProposalsMessage {
    protected ComputeCAProposalsMessageImpl() {}

    @Override
    public final native java.lang.String docContent() /*-{
      return this["docContent"];
    }-*/;

    public final native ComputeCAProposalsMessageImpl setDocContent(java.lang.String docContent) /*-{
      this["docContent"] = docContent;
      return this;
    }-*/;

    public final native boolean hasDocContent() /*-{
      return this.hasOwnProperty("docContent");
    }-*/;

    @Override
    public final native int offset() /*-{
      return this["offset"];
    }-*/;

    public final native ComputeCAProposalsMessageImpl setOffset(int offset) /*-{
      this["offset"] = offset;
      return this;
    }-*/;

    public final native boolean hasOffset() /*-{
      return this.hasOwnProperty("offset");
    }-*/;

    @Override
    public final native java.lang.String fileName() /*-{
      return this["fileName"];
    }-*/;

    public final native ComputeCAProposalsMessageImpl setFileName(java.lang.String fileName) /*-{
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

    public final native ComputeCAProposalsMessageImpl setId(java.lang.String id) /*-{
      this["id"] = id;
      return this;
    }-*/;

    public final native boolean hasId() /*-{
      return this.hasOwnProperty("id");
    }-*/;

    public static native ComputeCAProposalsMessageImpl make() /*-{
      return {
        _type: 4
      };
    }-*/;  }


  public static class ConfigMessageImpl extends com.google.gwt.webworker.client.messages.MessageImpl implements com.codenvy.ide.ext.java.messages.ConfigMessage {
    protected ConfigMessageImpl() {}

    @Override
    public final native java.lang.String projectName() /*-{
      return this["projectName"];
    }-*/;

    public final native ConfigMessageImpl setProjectName(java.lang.String projectName) /*-{
      this["projectName"] = projectName;
      return this;
    }-*/;

    public final native boolean hasProjectName() /*-{
      return this.hasOwnProperty("projectName");
    }-*/;

    @Override
    public final native java.lang.String javaDocContext() /*-{
      return this["javaDocContext"];
    }-*/;

    public final native ConfigMessageImpl setJavaDocContext(java.lang.String javaDocContext) /*-{
      this["javaDocContext"] = javaDocContext;
      return this;
    }-*/;

    public final native boolean hasJavaDocContext() /*-{
      return this.hasOwnProperty("javaDocContext");
    }-*/;

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
    public final native java.lang.String packageName() /*-{
      return this["packageName"];
    }-*/;

    public final native ParseMessageImpl setPackageName(java.lang.String packageName) /*-{
      this["packageName"] = packageName;
      return this;
    }-*/;

    public final native boolean hasPackageName() /*-{
      return this.hasOwnProperty("packageName");
    }-*/;

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
    public final native Array<String> stringArguments() /*-{
      return this[3];
    }-*/;

    public final native ProblemImpl setStringArguments(JsoArray<String> stringArguments) /*-{
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
    public final native Array<Problem> problems() /*-{
      return this["problems"];
    }-*/;

    public final native ProblemsMessageImpl setProblems(JsoArray<Problem> problems) /*-{
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


  public static class ProposalAppliedMessageImpl extends com.google.gwt.webworker.client.messages.MessageImpl implements com.codenvy.ide.ext.java.messages.ProposalAppliedMessage {
    protected ProposalAppliedMessageImpl() {}

    @Override
    public final native com.codenvy.ide.ext.java.messages.Region selectionRegion() /*-{
      return this["selectionRegion"];
    }-*/;

    public final native ProposalAppliedMessageImpl setSelectionRegion(com.codenvy.ide.ext.java.messages.Region selectionRegion) /*-{
      this["selectionRegion"] = selectionRegion;
      return this;
    }-*/;

    public final native boolean hasSelectionRegion() /*-{
      return this.hasOwnProperty("selectionRegion");
    }-*/;

    @Override
    public final native com.codenvy.ide.collections.Array<com.codenvy.ide.ext.java.messages.Change> changes() /*-{
      return this["changes"];
    }-*/;

    public final native ProposalAppliedMessageImpl setChanges(com.codenvy.ide.collections.js.JsoArray<com.codenvy.ide.ext.java.messages.Change> changes) /*-{
      this["changes"] = changes;
      return this;
    }-*/;

    public final native boolean hasChanges() /*-{
      return this.hasOwnProperty("changes");
    }-*/;

    @Override
    public final native java.lang.String id() /*-{
      return this["id"];
    }-*/;

    public final native ProposalAppliedMessageImpl setId(java.lang.String id) /*-{
      this["id"] = id;
      return this;
    }-*/;

    public final native boolean hasId() /*-{
      return this.hasOwnProperty("id");
    }-*/;

    public static native ProposalAppliedMessageImpl make() /*-{
      return {
        _type: 7
      };
    }-*/;  }


  public static class RegionImpl extends com.google.gwt.webworker.client.messages.MessageImpl implements com.codenvy.ide.ext.java.messages.Region {
    protected RegionImpl() {}

    @Override
    public final native int getLength() /*-{
      return this["length"];
    }-*/;

    public final native RegionImpl setLength(int length) /*-{
      this["length"] = length;
      return this;
    }-*/;

    public final native boolean hasLength() /*-{
      return this.hasOwnProperty("length");
    }-*/;

    @Override
    public final native int getOffset() /*-{
      return this["offset"];
    }-*/;

    public final native RegionImpl setOffset(int offset) /*-{
      this["offset"] = offset;
      return this;
    }-*/;

    public final native boolean hasOffset() /*-{
      return this.hasOwnProperty("offset");
    }-*/;

    public static native RegionImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class WorkerProposalImpl extends com.google.gwt.webworker.client.messages.MessageImpl implements com.codenvy.ide.ext.java.messages.WorkerProposal {
    protected WorkerProposalImpl() {}

    @Override
    public final native java.lang.String image() /*-{
      return this[0];
    }-*/;

    public final native WorkerProposalImpl setImage(java.lang.String image) /*-{
      this[0] = image;
      return this;
    }-*/;

    public final native boolean hasImage() /*-{
      return this.hasOwnProperty(0);
    }-*/;

    @Override
    public final native java.lang.String displayText() /*-{
      return this[1];
    }-*/;

    public final native WorkerProposalImpl setDisplayText(java.lang.String displayText) /*-{
      this[1] = displayText;
      return this;
    }-*/;

    public final native boolean hasDisplayText() /*-{
      return this.hasOwnProperty(1);
    }-*/;

    @Override
    public final native boolean autoInsertable() /*-{
      return this[2];
    }-*/;

    public final native WorkerProposalImpl setAutoInsertable(boolean autoInsertable) /*-{
      this[2] = autoInsertable;
      return this;
    }-*/;

    public final native boolean hasAutoInsertable() /*-{
      return this.hasOwnProperty(2);
    }-*/;

    @Override
    public final native java.lang.String id() /*-{
      return this[3];
    }-*/;

    public final native WorkerProposalImpl setId(java.lang.String id) /*-{
      this[3] = id;
      return this;
    }-*/;

    public final native boolean hasId() /*-{
      return this.hasOwnProperty(3);
    }-*/;

    public static native WorkerProposalImpl make() /*-{
      return [];
    }-*/;  }

}