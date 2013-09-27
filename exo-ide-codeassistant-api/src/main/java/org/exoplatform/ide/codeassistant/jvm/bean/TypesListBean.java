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
package org.exoplatform.ide.codeassistant.jvm.bean;

import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypesList;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class TypesListBean implements TypesList {

    private List<ShortTypeInfo> types;

    public TypesListBean() {
    }

    public TypesListBean(List<ShortTypeInfo> types) {
        this.types = types;
    }

    @Override
    public void setTypes(List<ShortTypeInfo> types) {
        this.types = types;
    }

    @Override
    public List<ShortTypeInfo> getTypes() {
        if (types != null)
            return types;
        return Collections.emptyList();
    }

}
