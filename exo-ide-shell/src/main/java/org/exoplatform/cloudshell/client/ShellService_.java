package org.exoplatform.cloudshell.client;


import org.exoplatform.cloudshell.client.cli.CommandLine;
import org.exoplatform.cloudshell.client.cli.CommandLineParser;
import org.exoplatform.cloudshell.client.cli.GnuParser;
import org.exoplatform.cloudshell.client.cli.Option;
import org.exoplatform.cloudshell.client.cli.OptionBuilder;
import org.exoplatform.cloudshell.client.cli.Options;
import org.exoplatform.cloudshell.client.cli.ParseException;
import org.exoplatform.cloudshell.client.cli.Parser;
import org.exoplatform.cloudshell.client.cli.PosixParser;
import org.exoplatform.cloudshell.client.cli.Properties;
import org.exoplatform.cloudshell.client.cli.Util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;


public class ShellService_ {
   
   CommandLineParser parser = new PosixParser();
   

  public String getWelcome()
  {
     return "Welcome to Cloud Shell\n > ";
  }

  public String process(String s) 
  {
     String[] args = null;
     try {
      args = Util.translateCommandline(s);
      for (String string : args)
      {
         System.out.println("ShellService.process()" + string);
         
      }
     
     
     } catch (Exception e) {
      e.printStackTrace();
   }
     
//        new String[] { "-Dparam1=value1", "-Dparam2=value2", "-Dparam3", "-Dparam4=value4", "-D", "--property", "foo=bar" };
     Options options = new Options();
     Option help = new Option("h","help",false, "print this message");
     options.addOption(help);
     options.addOption(new Option("ls", "getallbook", false, "print project help information"));
     options.addOption("put", "add-new-book", false, "Add new book");
     options.addOption(OptionBuilder.withValueSeparator().hasOptionalArgs(2).create('D'));
     
     
     
     
     
     //'{"author":"My Author","title":"My Title","price":1.00,"pages":100}'

     Parser parser = new GnuParser();
     CommandLine cl;
     String s1 = new String();
   try
   {
      cl = parser.parse(options, args);
      Option[] opts = cl.getOptions();
      if (cl.hasOption("help"))
      {
         s1 = help.getDescription();
      }
      if (cl.hasOption("ls"))
      {
         RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "/rest/books");
         builder.setCallback(new RequestCallback()
         {
            
            public void onResponseReceived(Request request, Response response)
            {
               //TODO CloudShell.term.print(response.getText());
            }
            
            public void onError(Request request, Throwable exception)
            {
               exception.printStackTrace();
            }
         });
         try
         {
            builder.send();
         }
         catch (RequestException e)
         {
            e.printStackTrace();
         }
      } 
         
         if (cl.hasOption("put"))
         {
            System.out.println("ShellService.process()rrrrrrrrr" + cl.getArgs().length);
            Properties props = cl.getOptionProperties("D");
            String author = (String)props.get("author");
            String title = (String)props.get("title");
            String price = (String)props.get("price");
            String  pages = (String)props.get("pages");
            
            
            String data = "{\"author\":\"" + author + "\",\"title\":\"" + title + "\",\"price\":" + price + ",\"pages\":" + pages + "}";
            
            RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, "/rest/books");
            builder.setHeader("Content-type","application/json");
            
            builder.setRequestData(data);
            
            builder.setCallback(new RequestCallback()
            {
               
               public void onResponseReceived(Request request, Response response)
               {
                  //CloudShell.term.print(response.getText());
               }
               
               public void onError(Request request, Throwable exception)
               {
                  exception.printStackTrace();
               }
            });
            try
            {
               builder.send();
            }
            catch (RequestException e)
            {
               e.printStackTrace();
            }
            
            System.out.println("ShellService.process()" + data);
            
         }
      return s1 + "\n >";
   }
   catch (ParseException e)
   {
      e.printStackTrace();
   }

     
    

     
     System.out.println("ShellService.process()" + "Process ... " + s);
     if ("eXo".equals(s))
        return s + " it's Great Company!!!\n>";
     else if ("MS".equals(s)) 
        return s + " Sucks!!!\n>";
     else 
        return "Who are you?\n>";
  }

  public Map<String, String> complete(String s, Scheduler.ScheduledCommand completer)
  {
     return new HashMap<String, String>();
  }
  
  public String arrayToString(String[] array, String delimiter) {
     StringBuilder arTostr = new StringBuilder();
     if (array.length > 0) {
         arTostr.append(array[0]);
         for (int i=1; i<array.length; i++) {
             arTostr.append(delimiter);
             arTostr.append(array[i]);
         }
     }
     return arTostr.toString();
 }
}
