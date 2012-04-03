package org.exoplatform.ide.shell.shared;


/**
 * Interface describe shell configuration.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ShellConfiguration.java Mar 28, 2012 11:29:43 AM azatsarynnyy $
 *
 */
public interface ShellConfiguration
{

   /**
    * Returns information about user.
    * 
    * @return the user information
    */
   public UserInfo getUser();

   /**
    * Sets information about user.
    * 
    * @param userInfo the user information
    */
   public void setUser(UserInfo userInfo);

   /**
    * Returns entry point.
    * 
    * @return the entry point
    */
   public String getEntryPoint();

   /**
    * Change the entry point.
    * 
    * @param entryPoint the entry point
    */
   public void setEntryPoint(String entryPoint);

}