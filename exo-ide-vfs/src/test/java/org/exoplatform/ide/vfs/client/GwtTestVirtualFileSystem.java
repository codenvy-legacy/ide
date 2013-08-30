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
package org.exoplatform.ide.vfs.client;

import com.google.gwt.http.client.RequestException;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Random;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.vfs.client.marshal.*;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.*;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.ACLCapability;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.QueryCapability;

import java.util.*;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class GwtTestVirtualFileSystem extends GWTTestCase {
    private static final int DELAY_TEST = 5000;

    private VirtualFileSystemInfo info;

    // private HandlerManager eventBus = new HandlerManager(null);

    @SuppressWarnings("unchecked")
    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        Map<String, Link> links = new HashMap<String, Link>();
        Link link = new LinkImpl("/rest/vfs/mock/file", Link.REL_CREATE_FILE, "*/*");
        links.put(Link.REL_CREATE_FILE, link);

        info =
                new VirtualFileSystemInfoImpl("test", true, true, "ANONIM", "ANY", Collections.EMPTY_LIST, ACLCapability.MANAGE,
                                              QueryCapability.BOTHCOMBINED, links, new FolderModel());

        new VirtualFileSystem("/", info, null);

        // VirtualFileSystem.init(info, "/");
    }

    public void testCreateFile() throws RequestException {
        final String parentId = String.valueOf(Random.nextDouble());
        String content = String.valueOf(Random.nextDouble() * Random.nextDouble());
        final long contentLength = content.length();
        Map<String, Link> links = new HashMap<String, Link>();
        Link link = new LinkImpl("/rest/vfs/mock/file/" + parentId, Link.REL_CREATE_FILE, "*/*");
        links.put(Link.REL_CREATE_FILE, link);
        FolderModel parent = new FolderModel("folder", new FolderModel(info.getRoot()), links);
        FileModel newFile = new FileModel("newFile", MimeType.TEXT_PLAIN, content, parent);

        VirtualFileSystem.getInstance().createFile(parent,
                                                   new AsyncRequestCallback<FileModel>(new FileUnmarshaller(newFile)) {
                                                       @Override
                                                       protected void onSuccess(FileModel result) {
                                                           assertNotNull(result.getId());
                                                           assertTrue(result.getItemType().equals(ItemType.FILE));
                                                           assertEquals(MimeType.TEXT_PLAIN, result.getMimeType());
                                                           assertEquals(parentId, result.getParentId());
                                                           assertEquals(contentLength, result.getLength());
                                                           finishTest();
                                                       }

                                                       @Override
                                                       protected void onFailure(Throwable exception) {
                                                           fail(exception.getMessage());
                                                       }
                                                   });

        delayTestFinish(DELAY_TEST);
    }

    public void testGetChildren() throws RequestException {
        final String parentId = String.valueOf(Random.nextDouble());
        // String content = String.valueOf(Random.nextDouble() * Random.nextDouble());
        Map<String, Link> links = new HashMap<String, Link>();
        Link link = new LinkImpl("/rest/vfs/mock/children/" + parentId, Link.REL_CREATE_FILE, "*/*");
        links.put(Link.REL_CHILDREN, link);
        FolderModel parent = new FolderModel("folder", new FolderModel(info.getRoot()), links);
        VirtualFileSystem.getInstance().getChildren(parent,
                                                    new AsyncRequestCallback<List<Item>>(
                                                            new ChildrenUnmarshaller(parent.getChildren().getItems())) {
                                                        @Override
                                                        protected void onSuccess(List<Item> result) {
                                                            assertNotNull(result);
                                                            assertEquals(1, result.size());
                                                            finishTest();
                                                        }

                                                        @Override
                                                        protected void onFailure(Throwable exception) {
                                                            fail();
                                                        }
                                                    });
        delayTestFinish(DELAY_TEST);
    }

    public void testCreateFolder() throws RequestException {
        final String parentId = String.valueOf(Random.nextDouble());
        Map<String, Link> links = new HashMap<String, Link>();
        Link link = new LinkImpl("/rest/vfs/mock/folder/" + parentId, Link.REL_CREATE_FOLDER, "*/*");
        links.put(Link.REL_CREATE_FOLDER, link);
        FolderModel parent = new FolderModel("folder", new FolderModel(info.getRoot()), links);
        VirtualFileSystem.getInstance().createFolder(parent,
                                                     new AsyncRequestCallback<FolderModel>(new FolderUnmarshaller(parent)) {
                                                         @Override
                                                         protected void onSuccess(FolderModel result) {
                                                             assertNotNull(result);
                                                             assertEquals("folder", result.getName());
                                                             finishTest();
                                                         }

                                                         @Override
                                                         protected void onFailure(Throwable exception) {
                                                             fail();
                                                         }
                                                     });
        delayTestFinish(DELAY_TEST);
    }

    @SuppressWarnings("unchecked")
    public void testCreateProject() throws RequestException {
        final String parentId = String.valueOf(Random.nextDouble());
        Map<String, Link> links = new HashMap<String, Link>();
        Link link = new LinkImpl("/rest/vfs/mock/project/" + parentId, Link.REL_CREATE_PROJECT, "*/*");
        links.put(Link.REL_CREATE_PROJECT, link);
        FolderModel parent = new FolderModel("folder", new FolderModel(info.getRoot()), links);
        ProjectModel newProject = new ProjectModel("proj", parent, "test-proj", Collections.EMPTY_LIST);
        VirtualFileSystem.getInstance().createProject(parent,
                                                      new AsyncRequestCallback<ProjectModel>(new ProjectUnmarshaller(newProject)) {
                                                          @Override
                                                          protected void onSuccess(ProjectModel result) {
                                                              assertNotNull(result);
                                                              assertEquals("proj", result.getName());
                                                              finishTest();
                                                          }

                                                          @Override
                                                          protected void onFailure(Throwable exception) {
                                                              fail();
                                                          }
                                                      });
        delayTestFinish(DELAY_TEST);
    }

    public void testGetContent() throws RequestException {
        FileModel file = new FileModel();
        file.getLinks().put(Link.REL_CONTENT,
                            new LinkImpl("/rest/vfs/mock/content/" + Random.nextInt(), Link.REL_CONTENT, "*/*"));

        VirtualFileSystem.getInstance().getContent(new AsyncRequestCallback<FileModel>(new FileContentUnmarshaller(file)) {
            @Override
            protected void onSuccess(FileModel result) {
                assertNotNull(result);
                assertEquals("Hello, world!", result.getContent());
                finishTest();
            }

            @Override
            protected void onFailure(Throwable exception) {
                fail(exception.getMessage());
            }
        });
        delayTestFinish(DELAY_TEST);
    }

    public void testUpdateContent() throws RequestException {
        final String content = Long.toString(new Date().getTime());
        FileModel file = new FileModel();
        file.setMimeType(MimeType.TEXT_PLAIN);
        file.setContent(content);
        file.getLinks().put(Link.REL_CONTENT,
                            new LinkImpl("/rest/vfs/mock/content/" + Random.nextInt(), Link.REL_CONTENT, "*/*"));
        VirtualFileSystem.getInstance().updateContent(file, new AsyncRequestCallback<FileModel>() {
            @Override
            protected void onSuccess(FileModel result) {
                assertNull(result);
                finishTest();
            }

            @Override
            protected void onFailure(Throwable exception) {
                fail(exception.getMessage());
            }
        });
        delayTestFinish(DELAY_TEST);
    }

    public void testUpdateContentLocked() throws RequestException {
        final String content = Long.toString(new Date().getTime());
        FileModel file = new FileModel();
        file.setMimeType(MimeType.TEXT_PLAIN);
        file.setContent(content);
        file.setLocked(true);
        file.setLock(new Lock("root", "100", 1000));
        file.getLinks().put(Link.REL_CONTENT, new LinkImpl("/rest/vfs/mock/content/locked-file", Link.REL_CONTENT, "*/*"));
        VirtualFileSystem.getInstance().updateContent(file, new AsyncRequestCallback<FileModel>() {
            @Override
            protected void onSuccess(FileModel result) {
                assertNull(result);
                finishTest();
            }

            @Override
            protected void onFailure(Throwable exception) {
                fail(exception.getMessage());
            }
        });
        delayTestFinish(DELAY_TEST);
    }

    public void testUpdateContentLockedFail() throws RequestException {
        final String content = Long.toString(new Date().getTime());
        FileModel file = new FileModel();
        file.setMimeType(MimeType.TEXT_PLAIN);
        file.setContent(content);
        file.setLocked(true);
        file.setLock(new Lock("root", "100111", 1000));
        file.getLinks().put(Link.REL_CONTENT, new LinkImpl("/rest/vfs/mock/content/locked-file", Link.REL_CONTENT, "*/*"));
        VirtualFileSystem.getInstance().updateContent(file, new AsyncRequestCallback<FileModel>() {
            @Override
            protected void onSuccess(FileModel result) {
                fail();
            }

            @Override
            protected void onFailure(Throwable exception) {
                finishTest();
            }
        });
        delayTestFinish(DELAY_TEST);
    }

    public void testDeleteContent() throws RequestException {
        FileModel file = new FileModel();
        file.setMimeType(MimeType.TEXT_PLAIN);
        file.getLinks().put(Link.REL_DELETE,
                            new LinkImpl("/rest/vfs/mock/delete/" + Random.nextInt(), Link.REL_DELETE, "*/*"));
        VirtualFileSystem.getInstance().delete(file, new AsyncRequestCallback<String>() {
            @Override
            protected void onSuccess(String result) {
                assertNull(result);
                finishTest();
            }

            @Override
            protected void onFailure(Throwable exception) {
                fail(exception.getMessage());
            }
        });
        delayTestFinish(DELAY_TEST);
    }

    public void testDeleteLockedFail() throws RequestException {
        final String content = Long.toString(new Date().getTime());
        FileModel file = new FileModel();
        file.setMimeType(MimeType.TEXT_PLAIN);
        file.setContent(content);
        file.setLocked(true);
        file.setLock(new Lock("root", "100111", 1000));
        file.getLinks().put(Link.REL_CONTENT, new LinkImpl("/rest/vfs/mock/content/locked-file", Link.REL_CONTENT, "*/*"));
        VirtualFileSystem.getInstance().updateContent(file, new AsyncRequestCallback<FileModel>() {
            @Override
            protected void onSuccess(FileModel result) {
                fail();
            }

            @Override
            protected void onFailure(Throwable exception) {

                finishTest();
            }
        });
        delayTestFinish(DELAY_TEST);
    }

    public void testCopy() throws RequestException {
        FileModel file = new FileModel();
        file.getLinks().put(Link.REL_COPY, new LinkImpl("/rest/vfs/mock/copy/" + Random.nextInt(), Link.REL_COPY, "*/*"));
        VirtualFileSystem.getInstance().copy(file, "destination", new AsyncRequestCallback<StringBuilder>() {
            @Override
            protected void onSuccess(StringBuilder result) {
                finishTest();
            }

            @Override
            protected void onFailure(Throwable exception) {
                fail();
            }
        });
    }

    public void testMove() throws RequestException {
        FileModel file = new FileModel();
        file.getLinks().put(Link.REL_MOVE, new LinkImpl("/rest/vfs/mock/move/" + Random.nextInt(), Link.REL_MOVE, "*/*"));
        VirtualFileSystem.getInstance().move(file, "destination", "lockToken", new AsyncRequestCallback<ItemWrapper>() {
            @Override
            protected void onSuccess(ItemWrapper result) {
                finishTest();
            }

            @Override
            protected void onFailure(Throwable exception) {
                fail();
            }
        });
    }

    @Override
    public String getModuleName() {
        return "org.exoplatform.ide.vfs.IDEVFS";
    }

}
