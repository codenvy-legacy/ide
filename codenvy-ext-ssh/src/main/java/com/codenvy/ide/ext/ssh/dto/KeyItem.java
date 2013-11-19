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
package com.codenvy.ide.ext.ssh.dto;

import com.codenvy.dto.shared.DTO;


/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@DTO
public interface KeyItem {
    String getHost();
    
    KeyItem withHost(String host);

    String getPublicKeyUrl();
    
    KeyItem withPublicKeyUrl(String publicKeyUrl);

    String getRemoteKeyUrl();
    
    KeyItem withRemoteKeyUrl(String remoteKeyURL);
}