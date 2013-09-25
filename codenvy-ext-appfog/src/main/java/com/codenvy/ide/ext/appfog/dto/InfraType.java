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
package com.codenvy.ide.ext.appfog.dto;

import com.codenvy.ide.ext.appfog.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.appfog.shared.Infra;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public enum InfraType {
    //TODO maybe add description to infras?
    aws("aws"),
    eu_aws("eu-aws"),
    ap_aws("ap-aws"),
    rs("rs"),
    hp("hp");

    private final String value;

    private InfraType(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public Infra getInfra() {
        DtoClientImpls.InfraImpl infra = DtoClientImpls.InfraImpl.make();
        infra.setName(value);
        infra.setProvider(value);
        return infra;
    }

    public static InfraType fromValue(String value) {
        for (InfraType v : InfraType.values()) {
            if (v.value.equals(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Invalid value '" + value + "' ");
    }
}