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
package org.exoplatform.ide.extension.aws.server.ec2;

import org.exoplatform.ide.extension.aws.shared.ec2.SecurityGroupInfo;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SecurityGroupInfoImpl implements SecurityGroupInfo {
    private String id;
    private String name;
    private String ownerId;
    private String description;

    public SecurityGroupInfoImpl(String id, String name, String ownerId, String description) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.description = description;
    }

    public SecurityGroupInfoImpl() {
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getOwnerId() {
        return ownerId;
    }

    @Override
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SecurityGroupInfoImpl{" +
               "id='" + id + '\'' +
               ", name='" + name + '\'' +
               ", ownerId='" + ownerId + '\'' +
               ", description='" + description + '\'' +
               '}';
    }
}
