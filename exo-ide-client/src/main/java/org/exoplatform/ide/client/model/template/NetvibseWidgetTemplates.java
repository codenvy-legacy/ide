/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.client.model.template;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * @version $Id: $
*/
public class NetvibseWidgetTemplates
{
   public static final String W3C =
      " <?xml version=\"1.0\" encoding=\"utf-8\"?>"
         + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
         + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:widget=\"http://www.netvibes.com/ns\">\n"
         + "    <head>\n"
         + "        <!-- Widget Infos -->\n"
         + "        <title>Sample blog post widget</title>\n"
         + "        <meta name=\"author\" content=\"Exposition Libraries\" />\n"
         + "        <meta name=\"description\" content=\"Displays latest post from a feed\" />\n"
         + "        <meta name=\"apiVersion\" content=\"1.2\" />\n"
         + "        <meta name=\"debugMode\" content=\"true\" />\n"

         + "        <!-- UWA Environment -->\n"
         + "        <link rel=\"stylesheet\" type=\"text/css\" href=\"http://uwa.preview.netvibes.com/css/lib/UWA/standalone.css\" />\n"
         + "        <script type=\"text/javascript\"> var UWA_SERVER = 'http://uwa.preview.netvibes.com'; </script>\n"
         + "        <script type=\"text/javascript\" src=\"http://uwa.preview.netvibes.com/js/c/UWA/UWA_Standalone.js?v=preview3\"></script>\n"

         + "        <!-- Widget Preferences -->\n"
         + "        <widget:preferences>\n"
         + "          <preference type=\"text\" name=\"feed_url\" label=\"RSS/Atom feed to use\" defaultValue=\"http://feeds.feedburner.com/NetvibesDevBlog\" />\n\n"

         + "        </widget:preferences>\n\n"

         + "        <!-- Widget Styles -->\n"
         + "        <style type=\"text/css\">\n"
         + "        </style>\n\n"

         + "        <!-- Widget Source -->\n"
         + "        <script type=\"text/javascript\">\n"
         + "        //<![CDATA[\n\n"

         + "            /*\n"
         + "                We create the global MyWidget object (it could be any other name).\n"
         + "                This object will be used to store variables and function.\n"
         + "            */\n"
         + "            var MyWidget = {\n\n"

         + "                /*\n"
         + "                    The onLoad() function is the first one, triggered by widget.onLoad.\n"
         + "                */\n"
         + "                onLoad: function() {\n"
         + "                    UWA.Data.getFeed(widget.getValue('feed_url'), MyWidget.displayFeed);\n"
         + "                },\n\n"

         + "                /*\n"
         + "                    The displayFeed() function is call be UWA.Data.getFeed when feed is loaded.\n"
         + "                */\n"
         + "                displayFeed: function(feed) {\n\n"

         + "                    widget.setTitle('<a href=\"' + feed.htmlUrl + '\">' + feed.title + '</a>');\n"
         + "                    widget.setIcon(feed.htmlUrl);\n"

         + "                    var latestPost = feed.items[0],\n"
         + "                       contentHtml = '<h2><a href=\"' + latestPost.link + '\">' + latestPost.title + '</a></h2>'\n"
         + "                                    + 'Posted on ' + latestPost.date + '<br />'\n"
         + "                                    + latestPost.content\n"
         + "                                    + '<em>Read all the previous posts by '\n"
         + "                                   + '<a href=\"' + feed.htmlUrl + '\">visiting the blog</a>!</em>';\n\n"

         + "                    widget.setBody(contentHtml);\n" + "                }\n" + "            }\n\n"

         + "            /*\n"
         + "                widget.onLoad() is the very first function triggered when the widget is loaded.\n"
         + "                Here, we make it trigger the MyWidget.onLoad() method.\n" + "            */\n"
         + "            widget.onLoad = MyWidget.onLoad;\n" + "        //]]>\n\n"

         + "        </script>\n" + "    </head>\n" + "    <body>\n" + "        <p>Loading...</p>\n" + "    </body>\n"
         + "</html>\n";

   public static final String TABVIEW =
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
         + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
         + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:widget=\"http://www.netvibes.com/ns\">\n"
         + "    <head>\n"
         + "        <!-- Widget Infos -->\n"
         + "        <title>Sample tabbed widget</title>\n"
         + "        <meta name=\"author\" content=\"Exposition Libraries\" />\n"
         + "        <meta name=\"description\" content=\"Sample code for a UWA widget with tabs\" />\n"
         + "        <meta name=\"apiVersion\" content=\"1.0\" />\n"
         + "        <meta name=\"autoRefresh\" content=\"20\" />\n\n"

         + "        <meta name=\"debugMode\" content=\"true\" />\n\n"

         + "        <!-- UWA Environment -->\n"
         + "        <link rel=\"stylesheet\" type=\"text/css\" href=\"http://uwa.preview.netvibes.com/css/lib/UWA/standalone.css\" />\n"
         + "        <script type=\"text/javascript\"> var UWA_SERVER = 'http://uwa.preview.netvibes.com'; </script>\n"
         + "        <script type=\"text/javascript\" src=\"http://uwa.preview.netvibes.com/js/c/UWA/UWA_Standalone.js?v=preview3\"></script>\n"
         + "        <script type=\"text/javascript\" src=\"http://uwa.preview.netvibes.com/js/c/UWA/UWA_Controls_TabView.js?v=preview3\"></script>\n"

         + "        <!-- Widget Preferences -->\n\n"

         + "        <widget:preferences>\n"
         + "            <preference name=\"feedUrl\" label=\"Feed URL\" type=\"hidden\" defaultValue=\"http://feedproxy.google.com/NetvibesDevNetBlog\" />\n"
         + "            <preference name=\"feedLimit\" type=\"range\" label=\"Number of items to display\" defaultValue=\"10\" step=\"1\" min=\"1\" max=\"20\" />\n"
         + "        </widget:preferences>\n\n"

         + "        <!-- Widget Styles -->\n"
         + "        <style type=\"text/css\">\n"
         + "            .astronomy img {\n"
         + "                border: none;\n"
         + "                -ms-interpolation-mode: bicubic;\n"
         + "            }\n\n"

         + "            .astronomy a, .astronomy a:hover {\n"
         + "                border: none;\n"
         + "            }\n"
         + "          </style>\n\n"

         + "        <!-- Widget Source -->\n"
         + "        <script type=\"text/javascript\">\n"
         + "        //<![CDATA[\n\n"

         + "            /*\n"
         + "                We create the global MyWidget object (it could be any other name).\n"
         + "                This object will be used to store variables and function.\n"
         + "            */\n"
         + "            var MyWidget = {\n\n"

         + "                imageHtml: null,\n\n"

         + "                feed: null,\n\n"

         + "                /*\n"
         + "                    The onLoad() function is the first one, triggered by widget.onLoad.\n"
         + "                    Its use is to display a \"Loading\" message, then call the next method.\n"
         + "                */\n"
         + "                onLoad: function() {\n"

         + "                    widget.body.empty();\n"

         + "                    widget.createElement('p', {\n"
         + "                        text: 'Loading...'\n"
         + "                    }).inject(widget.body);\n\n"

         + "                    MyWidget.buildTabs();\n"
         + "                    widget.callback('onUpdateBody');\n"
         + "                },\n"

         + "                /*\n"
         + "                    The buildTabs function is where the tabl component is loaded and displayed.\n"
         + "                    Tabs are build using the TabView controler.\n"
         + "                */\n"
         + "                buildTabs: function() {\n\n"

         + "                    var tabs = new UWA.Controls.TabView();\n"
         + "                    MyWidget.tabs = tabs;\n\n"

         + "                    tabs.addTab('tab1', {text: 'Some text'});\n"
         + "                    tabs.addTab('tab2', {text: 'An image'});\n"
         + "                    tabs.addTab('tab3', {text: 'A feed'});\n"
         + "                    tabs.observe('activeTabChange', MyWidget.onActiveTabChanged);\n"
         + "                    tabs.selectTab(widget.getValue('activeTab') || 'tab');\n\n"

         + "                    widget.body.empty();\n"
         + "                    tabs.inject(widget.body);\n"
         + "                },\n"

         + "                /*\n"
         + "                    onActiveTabChanged() is triggered on the 'activeTabChange' event,\n"
         + "                    which means each time the user clicks a hidden tab.\n"
         + "                    The TabView controler displays the clicked tab, and\n"
         + "                    it's then up to the developer to decide what happens.\n"
         + "                */\n"
         + "                onActiveTabChanged: function(name, data) {\n\n"

         + "                    var tabs = MyWidget.tabs;\n"
         + "                    widget.setValue('activeTab', name);\n\n"

         + "                    switch (name) {\n"
         + "                        case 'tab1':\n"
         + "                            MyWidget.displayText();\n"
         + "                            break;\n"
         + "                        case 'tab2':\n"
         + "                            // if the image's HTML page has already been loaded, just display the image\n"
         + "                            // if not, then load it (and then display it)\n"
         + "                            (MyWidget.imageHtml) ? MyWidget.displayImage(MyWidget.imageHtml) : MyWidget.retrieveImage();\n"
         + "                            break;\n"
         + "                        case 'tab3':\n"
         + "                            // if the feed has already been loaded, just display it\n"
         + "                            // if not, then load it (and then display it)\n"
         + "                            (MyWidget.feed) ? MyWidget.displayFeed(MyWidget.feed) : MyWidget.retrieveFeed();\n"
         + "                        break;\n"
         + "                    }\n"
         + "                },\n\n"

         + "                /*\n"
         + "                    Our first tab just displays some simple HTML.\n"
         + "                    One part of the HTML is built using a simple string, converted to HTML using setHTML().\n"
         + "                    The second part of the HTML is built with the DOM, and added to the tab's HTML content using appendChild().\n"
         + "                */\n"
         + "                displayText: function() {\n"

         + "                    var htmlString = '<p>This is just some demo text.</p>'\n"
         + "                                   + '<p>Of course, you can use <b>any</b> <acronym title=\"Hypertext Mark-up Language\">HTML</acronym> <a href=\"http://www.w3.org/MarkUp/\">tag</a> you need.</p>'\n"
         + "                                   + '<p>This widget demoes tabs. In the two other tabs, you\\'ll see...</p>';\n"

         + "                    var content = widget.createElement('div', {\n"
         + "                       html: htmlString\n"
         + "                    });\n"

         + "                    var ul = widget.createElement('ul').inject(content);\n"
         + "                    widget.createElement('li', {text: 'an image'}).inject(ul);\n"
         + "                    widget.createElement('li', {text: 'a feed'}).inject(ul);\n"

         + "                    MyWidget.tabs.setContent('tab1', content);\n"
         + "                },\n\n"

         + "                /*\n"
         + "                    Ajax request to retrieve the full HTML code for the APOD page.\n"
         + "                */\n"
         + "                retrieveImage: function() {\n"
         + "                    UWA.Data.getText('http://antwrp.gsfc.nasa.gov/apod/astropix.html', MyWidget.displayImage);\n"
         + "                },\n"

         + "                /*\n"
         + "                    Displaying the APOD image.\n"
         + "                    Since we start with an HTML page, we have to search through the code to fetch the various infos.\n"
         + "                    This is done using RegExp.\n"
         + "                */\n"
         + "                displayImage: function(html) {\n"

         + "                    // Section heavily inspired by the Astronomy Picture of the Day sample widget\n"
         + "                    // http://dev.netvibes.com/doc/uwa/examples/apod\n"

         + "                    if (html) {\n"
         + "                       MyWidget.imageHtml = html;\n"
         + "                    }\n"

         + "                    var matchesPicturePath = html.match(/IMG SRC=\"([^\"]*)/);\n"

         + "                    if (matchesPicturePath) {\n"

         + "                        var content = widget.createElement('div', {\n"
         + "                            'class': 'astronomy'\n"
         + "                       });\n\n"

         + "                        var picturePath = matchesPicturePath[1];\n"
         + "                        var matchesDescription = html.match(/<b>([^<>]*)<\\/b>.*<br>/);\n"
         + "                        var description = matchesDescription[1] || '?';\n"
         + "                        var imageWidth = widget.body.getDimensions().width;\n"
         + "                        var contentHtml = '<a href=\"http://antwrp.gsfc.nasa.gov/apod/\" target=\"_blank\" title=\"'+ description +'\">'\n"
         + "                                        + '    <img src=\"http://antwrp.gsfc.nasa.gov/apod/'+ picturePath +'\" width=\"'+ imageWidth +'\" alt=\"' + description + '\" />'\n"
         + "                                        + '</a>';\n\n"

         + "                        content.setHTML(contentHtml);\n\n"

         + "                        MyWidget.tabs.getTabContent('tab2').setStyle('padding', 0);\n"
         + "                        MyWidget.tabs.setContent('tab2', content);\n\n"

         + "                    } else {\n"
         + "                        MyWidget.tabs.setContent('tab2', 'No Image Found');\n" + "                    }\n"
         + "                },\n\n"

         + "                /*\n" + "                    Ajax call to retrieve a feed.\n"
         + "                    Since we are using the getFeed() function, the feed will use the JSON Feed format.\n"
         + "                    http://dev.netvibes.com/doc/uwa/documentation/json_feed_format\n"
         + "                */\n" + "                retrieveFeed: function() {\n"
         + "                    UWA.Data.getFeed(widget.getValue('feedUrl'), MyWidget.displayFeed)\n"
         + "                },\n\n"

         + "                /*\n" + "                    Display the JSON Feed format.\n" + "                */\n"
         + "                displayFeed: function(feed) {\n\n"

         + "                    // Section heavily inspired by the RSSReader sample widget\n"
         + "                    // http://dev.netvibes.com/doc/uwa/examples/rssreader\n\n"

         + "                    if (feed) {\n" + "                        MyWidget.feed = feed;\n"
         + "                    }\n\n"

         + "                    // Update Preference feedLimit max value\n"
         + "                    widget.getPreference('feedLimit').max = feed.items.length;\n\n"

         + "                    var feedLimit = widget.getInt('feedLimit');\n"
         + "                    var feedList = widget.createElement('ul', {\n"
         + "                        'class': 'nv-feedList'\n" + "                    });\n\n"

         + "                    for (var i = 0; i < feedLimit; i++) {\n\n"

         + "                      var item = feed.items[i];\n"
         + "                      var li = widget.createElement('li');\n"
         + "                      var a = widget.createElement('a', {\n"
         + "                            text: item.title,\n" + "                            href: item.link\n"
         + "                      });\n\n"

         + "                       var desc = item.content.stripTags().truncate(255);\n"
         + "                       new UWA.Controls.ToolTip(a, desc, 250)\n\n"

         + "                       li.appendChild(a);\n" + "                       feedList.appendChild(li);\n"
         + "                     }\n\n"

         + "                     MyWidget.tabs.setContent('tab3', feedList);\n" + "                 }\n"
         + "             };\n\n"

         + "             /*\n"
         + "                 widget.onLoad() is the very first function triggered when the widget is loaded.\n"
         + "                 Here, we make it trigger the MyWidget.onLoad() method.\n" + "             */\n"
         + "             widget.onLoad = MyWidget.onLoad;\n\n"

         + "         //]]>\n" + "         </script>\n" + "     </head>\n" + "     <body>\n"
         + "         <p>Loading...</p>\n" + "     </body>\n\n"

         + " </html>";

   public static final String CHART =
      "<?xml version=\"1.0\" encoding=\"utf-8\"?><!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd>\n"
         + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:widget=\"http://www.netvibes.com/ns\">\n"
         + "    <head>/n"
         + "        <!-- Widget Infos -->\n"
         + "        <title>Sample Chart widget</title>\n"
         + "        <meta name=\"author\" content=\"Exposition Libraries\" />\n"
         + "        <meta name=\"description\" content=\"Displays latest post from a feed\" />"
         + "        <meta name=\"apiVersion\" content=\"1.2\" />\n"
         + "        <meta name=\"debugMode\" content=\"true\" />\n\n"

         + "        <!-- UWA Environment -->\n"
         + "        <link rel=\"stylesheet\" type=\"text/css\" href=\"http://uwa.preview.netvibes.com/css/lib/UWA/standalone.css\" />\n"
         + "        <script type=\"text/javascript\"> var UWA_SERVER = 'http://uwa.preview.netvibes.com'; </script>\n"
         + "        <script type=\"text/javascript\" src=\"http://uwa.preview.netvibes.com/js/c/UWA/UWA_Standalone.js?v=preview3\"></script>\n"
         + "        <script type=\"text/javascript\" src=\"http://uwa.preview.netvibes.com/js/c/UWA/UWA_Controls_Chart.js?v=preview3\"></script>\n"
         + "        <script type=\"text/javascript\" src=\"http://uwa.preview.netvibes.com/js/c/UWA/raphael.js?v=preview3\"></script>\n\n"

         + "        <!-- Widget Preferences -->\n\n"

         + "        <widget:preferences>\n"
         + "        </widget:preferences>\n\n"

         + "        <!-- Widget Styles -->\n"
         + "        <style type=\"text/css\">\n"
         + "        </style>\n\n"

         + "        <!-- Widget Source -->\n"
         + "        <script type=\"text/javascript\">\n"
         + "        //<![CDATA[\n\n"

         + "          widget.onLoad = function() {\n"

         + "              var widgetDim = widget.body.getDimensions();\n"
         + "              widget.body.empty();\n"

         + "              //\n"
         + "              // Float data example\n"
         + "              //\n"

         + "              var chartFloatData = [[\"Jan\",0.25], [\"Feb\",-0.25], [\"Mar\",0.33], [\"Mar\",-0.33], [\"Mar\",0.75], [\"Mar\",-0.75]]\n"
         + "              var chartFloat = new UWA.Controls.Chart({\n"
         + "                  height: widgetDim.width / 2,\n"
         + "                  width: widgetDim.width,\n"
         + "                  effect: 'elastic',\n"
         + "                  type: 'bar',\n"
         + "                  data: chartFloatData,\n"
         + "              });\n"

         + "              //\n"
         + "              // Empty data example\n"
         + "              //\n"

         + "              var chartEmptyData = [[\"Jan\",0], [\"Feb\",0], [\"Mar\",0], [\"Mar\",0], [\"Mar\",0], [\"Mar\",0]];\n"
         + "              var chartEmpty = new UWA.Controls.Chart({\n"
         + "                  height: widgetDim.width / 2,\n"
         + "                  width: widgetDim.width,\n"
         + "                  effect: 'elastic',\n"
         + "                  type: 'bar',\n"
         + "                  data: chartEmptyData\n"
         + "              });\n\n"

         + "              //\n"
         + "              // Multiple data Set example\n"
         + "              //\n"

         + "              var chartMultiSetData = [\n"
         + "                {\n"
         + "                    options: {\n"
         + "                        type: 'line',\n"
         + "                        opacity: 1\n"
         + "                    },\n"
         + "                    values: [[\"Jan\",0], [\"Feb\",3], [\"Mar\",6], [\"Avr\",3], [\"Mai\",9], [\"Jun\",2], [\"Jul\",5], [\"Aug\",2], [\"Sept\",5]]\n"
         + "                },\n"
         + "                {\n"
         + "                    options: {\n"
         + "                        join: false,\n"
         + "                        color: '#92D146',\n"
         + "                        legend: \"Quarter 1\",\n"
         + "                        type: 'bar',\n"
         + "                        effect: 'backOut'\n"
         + "                    },\n"
         + "                    values: [[\"Jan\",1], [\"Feb\",2], [\"Mar\",3]]\n"
         + "                },\n"
         + "                {\n"
         + "                    options: {\n"
         + "                        join: true,\n"
         + "                        color: '90-#D31200-#EE9B93',\n"
         + "                        opacity: 1,\n"
         + "                        legend: \"Quarter 2\",\n"
         + "                        type: 'bar',\n"
         + "                        effect: 'elastic'\n"
         + "                    },\n"
         + "                    values: [[\"Avr\",4], [\"Mai\",5], [\"Jun\",6]]\n"
         + "                },\n"
         + "                {\n"
         + "                    options: {\n"
         + "                        join: true,\n"
         + "                        color: '#4081D0',\n"
         + "                        legend: \"Quarter 3\", \n"
         + "                        type: 'bar',\n"
         + "                        effect: 'bounce'\n"
         + "                    },\n"
         + "                    values: [[\"Jul\",7], [\"Aug\",8], [\"Sept\",9]]\n"
         + "                }\n"
         + "              ];\n"

         + "              var chartMultiSet = new UWA.Controls.Chart({\n"
         + "                  height: widgetDim.width / 2,\n"
         + "                  width: widgetDim.width,\n"
         + "                  data: chartMultiSetData\n"
         + "              });\n\n"

         + "              //\n"
         + "              // Big Data example\n"
         + "              //\n"
         + "              var chartBigData = [[\"Jan\",10333], [\"Feb\",-2200], [\"Mar\",5255], [\"Mar\",-1150]];\n"
         + "             var chartBig = new UWA.Controls.Chart({\n"
         + "                  height: widgetDim.width / 2,\n"
         + "                  width: widgetDim.width,\n"
         + "                  type: 'bar line',\n"
         + "                  effect: 'bounce',\n"
         + "                  data: chartBigData,\n"
         + "                  legend: \"Quarter 3\",\n"
         + "              });\n"

         + "              widget.addBody(UWA.createElement('h2', {text: 'Float'}));\n"
         + "              widget.addBody(chartFloat.getContent());\n\n"

         + "              widget.addBody(UWA.createElement('h2', {text: 'Empty'}));\n\n"
         + "              widget.addBody(chartEmpty.getContent());\n\n"

         + "              widget.addBody(UWA.createElement('h2', {text: 'Small Numbers'}));\n"
         + "              widget.addBody(chartMultiSet.getContent());\n\n"

         + "              widget.addBody(UWA.createElement('h2', {text: 'Big Numbers'}));\n"
         + "              widget.addBody(chartBig.getContent());\n"
         + "          }\n\n"

         + "          widget.onRefresh = widget.onResize = widget.onLoad;\n\n"

         + "        //]]>\n"

         + "        </script>\n"
         + "    </head>\n"
         + "    <body>\n"
         + "        <p>Loading...</p>\n"
         + "    </body>\n"
         + "</html>";

   public static final String FLASH =
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
         + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
         + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:widget=\"http://www.netvibes.com/ns\">\n"
         + "    <head>\n"
         + "        <!-- Widget Infos -->\n"
         + "        <title>Sample Flash widget</title>\n"
         + "        <meta name=\"author\" content=\"Exposition Libraries\" />\n"
         + "        <meta name=\"description\" content=\"Sample code for a UWA widget with Flash\" />\n"
         + "        <meta name=\"apiVersion\" content=\"1.0\" />\n"
         + "        <meta name=\"autoRefresh\" content=\"20\" />\n"
         + "        <meta name=\"debugMode\" content=\"true\" />\n\n"

         + "        <!-- UWA Environment -->\n"
         + "        <link rel=\"stylesheet\" type=\"text/css\" href=\"http://uwa.preview.netvibes.com/css/lib/UWA/standalone.css\" />\n"
         + "        <script type=\"text/javascript\"> var UWA_SERVER = 'http://uwa.preview.netvibes.com'; </script>\n"
         + "        <script type=\"text/javascript\" src=\"http://uwa.preview.netvibes.com/js/c/UWA/UWA_Standalone.js?v=preview3\"></script>\n"
         + "        <script type=\"text/javascript\" src=\"http://uwa.preview.netvibes.com/js/c/UWA/UWA_Controls_Flash.js?v=preview3\"></script>\n\n"

         + "        <!-- Widget Preferences -->\n\n"

         + "        <widget:preferences>\n"
         + "            <preference type=\"text\" name=\"title\" label=\"Title\"/>\n"
         + "            <preference type=\"text\" name=\"flashUrl\" label=\"Flash URL (swf)\" defaultValue=\"http://uwa.preview.netvibes.com/modules/img/fire.swf\"/>\n"
         + "            <preference type=\"text\" name=\"width\" label=\"Original width\" defaultValue=\"320\"/>\n"
         + "            <preference type=\"text\" name=\"height\" label=\"Original height\" defaultValue=\"240\"/>\n"
         + "            <preference type=\"text\" name=\"flashVars\" label=\"Flash variables\" defaultValue=\"\" />\n"
         + "        </widget:preferences>\n\n"

         + "        <!-- Widget Styles -->\n\n"

         + "        <style type=\"text/css\">\n"
         + "        </style>\n\n"

         + "        <!-- Widget Source -->\n"
         + "        <script type=\"text/javascript\">\n"
         + "        //<![CDATA[\n\n"

         + "            /*\n"
         + "                We create the global MyWidget object (it could be any other name).\n"
         + "                This object will be used to store variables and function.\n"
         + "            */\n"
         + "            var MyWidget = {\n\n"

         + "                /*\n"
         + "                    The onLoad() function is the first one, triggered by widget.onLoad.\n"
         + "                    Its use is to display a \"Loading\" message, then call the next method.\n"
         + "                */\n"
         + "                onLoad: function() {\n\n"

         + "                    var widgetDimensions = widget.body.getDimensions(),\n"
         + "                        widgetWidth = (widgetDimensions.width > 0 ? widgetDimensions.width : widget.getInt('width')),\n"
         + "                        widgetHeight = (widgetDimensions.height > 0 ? widgetDimensions.height : widget.getInt('height')),\n"
         + "                        widgetTitle = widget.getValue('title');\n\n"

         + "                    // Update Title if available\n"
         + "                    if (widgetTitle && widgetTitle.length > 0) {\n"
         + "                        widget.setTitle(widgetTitle);\n" + "                    }\n\n"

         + "                    // Create Flash Controls\n"
         + "                    this.flash = new UWA.Controls.Flash({\n" + "                        resize: true,\n"
         + "                        url: widget.getValue('flashUrl'),\n"
         + "                        flashVars: widget.getValue('flashVars'),\n"
         + "                        width: widgetWidth,\n" + "                        heigh: widgetHeight\n"
         + "                   });\n\n"

         + "                    widget.body.setStyle('padding', 0);\n"
         + "                    widget.setBody(this.flash.getContent());\n" + "                },\n"

         + "                onResize: function() {\n" + "                    this.flash.onResize();\n"
         + "                }\n" + "            }\n\n"

         + "            /*\n"
         + "                widget.onLoad() is the very first function triggered when the widget is loaded.\n"
         + "                Here, we make it trigger the MyWidget.onLoad() method.\n" + "            */\n"
         + "            widget.onLoad = MyWidget.onLoad;\n" + "            widget.onResize = MyWidget.onResize;\n\n"

         + "        //]]>\n" + "        </script>\n" + "    </head>\n" + "    <body>\n\n"

         + "        <p>Loading ...</p>\n" + "    </body>\n" + "</html>\n";

}
