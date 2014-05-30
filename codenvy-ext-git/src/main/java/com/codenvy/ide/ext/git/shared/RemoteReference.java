package com.codenvy.ide.ext.git.shared;

import com.codenvy.dto.shared.DTO;

/**
 * Reference of remote repository in format: commitId referenceName.
 *
 * @author Vladyslav Zhukovskii
 */
@DTO
public interface RemoteReference {
    String getCommitId();

    void setCommitId(String commitId);

    RemoteReference withCommitId(String commitId);

    String getReferenceName();

    void setReferenceName(String referenceName);

    RemoteReference withReferenceName(String referenceName);
}
