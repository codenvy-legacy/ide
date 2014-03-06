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
package com.codenvy.ide.ext.java.jdt.core.search;

import com.codenvy.ide.ext.java.jdt.core.IPackageFragment;

import java.util.ArrayList;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class SearchEngine {

    public interface SearchCallback {
        void searchFinished(ArrayList<TypeNameMatch> typesFound);
    }

    private final String projectId;

    private SearchCallback callback;

    private ArrayList<TypeNameMatch> typesFound;

    private int index = 0;

    private char[][] allTypes;

    private final IPackageFragment currentPackage;

    /** @param projectId */
    public SearchEngine(String projectId, IPackageFragment currentPackage) {
        this.projectId = projectId;
        this.currentPackage = currentPackage;
    }

    /**
     * @param allTypes
     * @param typesFound
     */
    public void searchAllTypeNames(char[][] allTypes, ArrayList<TypeNameMatch> typesFound, SearchCallback callback) {
        this.allTypes = allTypes;
        this.typesFound = typesFound;
        this.callback = callback;
        if (allTypes.length == 0) {
            callback.searchFinished(typesFound);
            return;
        }
        getTypes(allTypes[index]);
    }

    /** @param fqn */
    private void getTypes(char[] fqn) {
        //TODO
//      JavaCodeAssistantService.get().findClassesByPrefix(
//         String.valueOf(fqn),
//         projectId,
//         new AsyncRequestCallback<TypesList>(new AutoBeanUnmarshaller<TypesList>(JavaEditorExtension.AUTO_BEAN_FACTORY
//            .types()))
//         {
//
//            @Override
//            protected void onSuccess(TypesList result)
//            {
//               typeListReceived(result);
//            }
//
//            @Override
//            protected void onFailure(Throwable exception)
//            {
//               exception.printStackTrace();
//            }
//         });
    }

//    /** @param result */
//    private void typeListReceived(TypesList result) {
//        index++;
//        for (ShortTypeInfo typeInfo : result.getTypes()) {
//            Type type = new Type(typeInfo);
//            if (!Modifier.isPublic(typeInfo.getModifiers())) {
//                if (!currentPackage.getElementName().equals(type.getPackageFragment().getElementName()))
//                    continue;
//            }
//            typesFound.add(new JavaSearchTypeNameMatch(type, typeInfo.getModifiers()));
//        }
//        if (index < allTypes.length)
//            getTypes(allTypes[index]);
//        else
//            callback.searchFinished(typesFound);
//    }

}
