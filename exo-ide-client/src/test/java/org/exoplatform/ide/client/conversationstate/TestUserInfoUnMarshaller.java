package org.exoplatform.ide.client.conversationstate;

import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.model.conversation.marshal.UserInfoUnmarshaller;
import org.exoplatform.ide.testframework.http.MockResponse;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class TestUserInfoUnMarshaller extends GWTTestCase
{

   private static String JSON =
      "{\"roles\":[\"users\",\"administrators\"],\"userId\":\"root\",\"groups\":[\"/ide/administrators\",\"/ide/users\"]}";

   @Override
   public String getModuleName()
   {
      return "org.exoplatform.ide.IDEGwtTest";
   }

   public void testUserInfoUnmarshall()
   {
      UserInfo userInfo = new UserInfo();

      UserInfoUnmarshaller unmarshaller = new UserInfoUnmarshaller(userInfo);
      unmarshaller.unmarshal(new MockResponse(JSON));
      assertEquals("root", userInfo.getName());
      assertTrue(userInfo.getGroups().contains("/ide/administrators"));
      assertTrue(userInfo.getGroups().contains("/ide/users"));
      assertEquals(2, userInfo.getGroups().size());
      assertTrue(userInfo.getRoles().contains("administrators"));
      assertTrue(userInfo.getRoles().contains("users"));
      assertEquals(2, userInfo.getRoles().size());
   }

}
