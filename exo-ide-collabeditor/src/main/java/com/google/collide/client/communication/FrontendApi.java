// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.collide.client.communication;

import com.codenvy.ide.dtogen.client.RoutableDtoClientImpl;
import com.codenvy.ide.dtogen.client.ServerErrorImpl;
import com.codenvy.ide.dtogen.shared.ClientToServerDto;
import com.codenvy.ide.dtogen.shared.RoutableDto;
import com.codenvy.ide.dtogen.shared.ServerToClientDto;
import com.codenvy.ide.json.client.Jso;
import com.codenvy.ide.json.shared.JsonCollections;
import com.codenvy.ide.json.shared.JsonStringMap;
import com.codenvy.ide.json.shared.JsonStringMap.IterationCallback;
import com.google.collide.client.bootstrap.BootstrapSession;
import com.google.collide.client.status.StatusManager;
import com.google.collide.dto.*;
import com.google.collide.dto.shared.JsonFieldConstants;

import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.client.framework.websocket.FrontendApi.ApiCallback;
import org.exoplatform.ide.client.framework.websocket.FrontendApi.RequestResponseApi;
import org.exoplatform.ide.client.framework.websocket.FrontendApi.SendApi;
import org.exoplatform.ide.client.framework.websocket.events.ReplyHandler;

/**
 * The EventBus APIs for the Collide server.
 * <p/>
 * See {@package com.google.collide.dto} for data objects.
 */
public class FrontendApi {

    protected class ApiImpl<REQ extends ClientToServerDto, RESP extends ServerToClientDto>
            implements RequestResponseApi<REQ, RESP>, SendApi<REQ> {
        private final String address;

        protected ApiImpl(String address) {
            this.address = address;
        }

        @Override
        public void send(REQ msg) {
            RoutableDtoClientImpl messageImpl = (RoutableDtoClientImpl)msg;
            addCustomFields(messageImpl);
            pushChannel.send(address, messageImpl.serialize());
        }

        @Override
        public void send(REQ msg, final ApiCallback<RESP> callback) {
            RoutableDtoClientImpl messageImpl = (RoutableDtoClientImpl)msg;
            addCustomFields(messageImpl);
            pushChannel.send(address, messageImpl.serialize(), new ReplyHandler() {
                @Override
                public void onReply(String message) {
                    Jso jso = Jso.deserialize(message);

                    //Log.info(getClass(), message);

                    if (RoutableDto.SERVER_ERROR == jso.getIntField(RoutableDto.TYPE_FIELD)) {
                        ServerErrorImpl serverError = (ServerErrorImpl)jso;
                        callback.onFail(serverError.getFailureReason());
                        return;
                    }

                    ServerToClientDto messageDto = (ServerToClientDto)jso;

                    @SuppressWarnings("unchecked")
                    RESP resp = (RESP)messageDto;
                    callback.onMessageReceived(resp);
                }
            });
        }

        private void addCustomFields(final RoutableDtoClientImpl messageImpl) {
            customHeaders.iterate(new IterationCallback<String>() {
                @Override
                public void onIteration(String header, String value) {
                    messageImpl.<Jso>cast().addField(header, value);
                }
            });
        }
    }

    // ///////////////////////////////
    // BEGIN AVAILABLE FRONTEND APIS
    // ///////////////////////////////

  /*
   * IMPORTANT!
   * 
   * By convention (and ignore the entries that ignore this convention :) ) we try to have
   * GetDto/ResponseDto pairs for each unique servlet path. This helps us guard against
   * client/frontend API version skew via a simple hash of all of the DTO messages.
   * 
   * So if you add a new Servlet Path, please also add a new Get/Response Dto pair.
   */

    public final RequestResponseApi<ClientToServerDocOp, ServerToClientDocOps> MUTATE_FILE =
            makeApi(Utils.getWorkspaceName() + "/collab_editor/documents/mutate");

    /**
     * Lets a client re-synchronize with the server's version of a file after being offline or missing
     * a doc op broadcast.
     */
    public final RequestResponseApi<RecoverFromMissedDocOps, RecoverFromMissedDocOpsResponse>
            RECOVER_FROM_MISSED_DOC_OPS = makeApi(Utils.getWorkspaceName() + "/collab_editor/documents/recoverMissedDocop");

    /** Get the contents of a file and provisions an edit session so that it can be edited. */
    public final RequestResponseApi<GetFileContents, GetFileContentsResponse> GET_FILE_CONTENTS =
            makeApi(Utils.getWorkspaceName() + "/collab_editor/documents/open");

    public final RequestResponseApi<GetEditSessionCollaborators, GetEditSessionCollaboratorsResponse> GET_FILE_COLLABORATORS =
            makeApi(Utils.getWorkspaceName() + "/collab_editor/documents/collaborators");

//  /**
//   * Get a subdirectory. Just the subtree rooted at that path. No associated meta data.
//   */
//  public final RequestResponseApi<GetDirectory, GetDirectoryResponse> GET_DIRECTORY =
//      makeApi("tree/get");
//
//  /** Sends an ADD_FILE, ADD_DIR, COPY, MOVE, or DELETE tree mutation. */
//  public final RequestResponseApi<WorkspaceTreeUpdate, EmptyMessage>
//      MUTATE_WORKSPACE_TREE = makeApi("tree/mutate");

//  /**
//   * Send a keep-alive for the client in a workspace.
//   */
//  public final SendApi<KeepAlive> KEEP_ALIVE = makeApi("participants/keepAlive");

    /** Send a message that user closed file. */
    public final SendApi<CloseEditor> CLOSE_EDITOR = makeApi(Utils.getWorkspaceName() + "/collab_editor/documents/close");

    /** Send a message that user closed file. */
    public final SendApi<FileOperationNotification> FILE_OPERATION_NOTIFY = makeApi(Utils.getWorkspaceName() + "/collab_editor/communication/notify/fileoperation");

    public final RequestResponseApi<GetOpenendFilesInWorkspace, GetOpenedFilesInWorkspaceResponse> GET_ALL_FILES =
            makeApi(Utils.getWorkspaceName() + "/collab_editor/documents/all");

    /** Gets the list of workspace participants. */
    public final RequestResponseApi<GetWorkspaceParticipants, GetWorkspaceParticipantsResponse>
            GET_WORKSPACE_PARTICIPANTS = makeApi(Utils.getWorkspaceName() + "/collab_editor/participants/list");

//  /** Requests that we get updated information about a workspace's run targets. */
//  public final SendApi<UpdateWorkspaceRunTargets> UPDATE_WORKSPACE_RUN_TARGETS =
//      makeApi("workspace/updateRunTarget");
//
//  /** Requests workspace state like the last opened files and run targets. */
//  public final RequestResponseApi<GetWorkspaceMetaData, GetWorkspaceMetaDataResponse>
//      GET_WORKSPACE_META_DATA = makeApi("workspace/getMetaData");

//  /**
//   * Retrieves code errors for a file.
//   */
//  public final RequestResponseApi<CodeErrorsRequest, CodeErrors> GET_CODE_ERRORS =
//      makeApi("todo/implementMe");
//
//  /**
//   * Retrieves code parsing results.
//   */
//  public final RequestResponseApi<CodeGraphRequest, CodeGraphResponse> GET_CODE_GRAPH =
//      makeApi("todo/implementMe");
//  /**
//   * Log an exception to the server and potentially receive an unobfuscated response.
//   */
//  public final RequestResponseApi<LogFatalRecord, LogFatalRecordResponse> LOG_REMOTE =
//      makeApi("todo/implementMe");

//  // TODO: this may want to move to browser channel instead, for
//  // search-as-you-type streaming. No sense to it yet until we have a real
//  // backend, though.
//  public final RequestResponseApi<Search, SearchResponse> SEARCH = makeApi("todo/implementMe");

    // /////////////////////////////
    // END AVAILABLE FRONTEND APIS
    // /////////////////////////////

    /** Creates a FrontendApi and initializes it. */
    public static FrontendApi create(PushChannel pushChannel, StatusManager statusManager) {
        FrontendApi frontendApi = new FrontendApi(pushChannel, statusManager);
        frontendApi.initCustomFields();
        return frontendApi;
    }

    private final JsonStringMap<String> customHeaders = JsonCollections.createMap();
    private final PushChannel   pushChannel;
    private final StatusManager statusManager;

    public FrontendApi(PushChannel pushChannel, StatusManager statusManager) {
        this.pushChannel = pushChannel;
        this.statusManager = statusManager;
    }

    private void initCustomFields() {
        customHeaders.put(
                JsonFieldConstants.SESSION_USER_ID, BootstrapSession.getBootstrapSession().getUserId());
    }

    /**
     * Makes an API given the URL.
     *
     * @param <REQ>
     *         the request object
     * @param <RESP>
     *         the response object
     */
    protected <
            REQ extends ClientToServerDto, RESP extends ServerToClientDto> ApiImpl<REQ, RESP> makeApi(
            String url) {
        return new ApiImpl<REQ, RESP>(url);
    }
}
