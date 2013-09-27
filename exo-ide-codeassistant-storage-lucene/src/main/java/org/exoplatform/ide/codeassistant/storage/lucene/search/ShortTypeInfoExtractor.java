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
package org.exoplatform.ide.codeassistant.storage.lucene.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.MapFieldSelector;
import org.apache.lucene.index.IndexReader;
import org.exoplatform.ide.codeassistant.jvm.bean.ShortTypeInfoBean;
import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.DataIndexFields;

import java.io.IOException;

/** Create ShortTypeInfo from lucene document. */
public class ShortTypeInfoExtractor implements ContentExtractor<ShortTypeInfo> {

    /**
     * @see org.exoplatform.ide.codeassistant.storage.lucene.search.ContentExtractor#getValue(org.apache.lucene.index.IndexReader,
     *      int)
     */
    @Override
    public ShortTypeInfo getValue(IndexReader reader, int doc) throws IOException {
        Document document =
                reader.document(doc, new MapFieldSelector(new String[]{DataIndexFields.MODIFIERS, DataIndexFields.FQN,
                                                                       DataIndexFields.ENTITY_TYPE, DataIndexFields.SIGNATURE}));

        int modifier = Integer.valueOf(document.get(DataIndexFields.MODIFIERS));

        return new ShortTypeInfoBean(document.get(DataIndexFields.FQN), modifier,
                                     document.get(DataIndexFields.ENTITY_TYPE), document.get(DataIndexFields.SIGNATURE));

    }
}
