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
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.ExternalizationTools;
import org.exoplatform.ide.codeassistant.storage.lucene.DataIndexFields;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/** Create TypeInfo from lucene document. */
public class TypeInfoExtractor implements ContentExtractor<TypeInfo> {

    /** @see org.exoplatform.ide.codeassistant.storage.lucene.search.ContentExtractor#getValue(int) */
    @Override
    public TypeInfo getValue(IndexReader reader, int doc) throws IOException {

        Document document = reader.document(doc, new MapFieldSelector(new String[]{DataIndexFields.TYPE_INFO}));
        byte[] contentField = document.getBinaryValue(DataIndexFields.TYPE_INFO);
        return ExternalizationTools.readExternal(new ByteArrayInputStream(contentField));
    }

}
