
/*jslint */
/*global openDatabase: true, $: true */
"use strict";

var dataService = (function () {
    //Load the database
    var db;       
  
    function errorHandler(transaction, error) {
        console.log('Oops. Error was ' + error.message + ' (Code ' + error.code + ')');
        return true;
    }
    
    return {
                                  
        //Initialise the database
        startDatabase: function () {
                
            var shortName = 'linkedin-contacts',
                version = '1.0',
                displayName = 'Contacts',
                maxSize = 65536;
                    
            db = openDatabase(shortName, version, displayName, maxSize);
                    
            db.transaction(
                function (transaction) {
                    transaction.executeSql(
                        'CREATE TABLE IF NOT EXISTS contacts ' +
                        ' (fullName TEXT NOT NULL, ' +
                        ' url TEXT NOT NULL, ' +
                        ' memberId TEXT NOT NULL, ' +
                        ' location TEXT NOT NULL,' +
                        ' status TEXT NOT NULL,' +
                        ' picture TEXT NOT NULL,' +
                        ' company TEXT NOT NULL,' +
                        ' position TEXT,' +
                        ' past TEXT,' +
                        ' current TEXT,' +
                        ' PRIMARY KEY(fullName, company));',
                        [], null, errorHandler);                       
                }
            );
        },
        
        
        //Create a category entry in the database
        createContact: function (entry) {
            
            var noPicture = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADwAAAA8CAAAAAAfl4auAAAAAXNCSVQI5gpbmQAAAAlwSFlzAAALEgAACxIB0t1+/AAAABx0RVh0U29mdHdhcmUAQWRvYmUgRmlyZXdvcmtzIENTNXG14zYAAAAVdEVYdENyZWF0aW9uIFRpbWUAMTIvMS8xMENHbswAAAIKSURBVHja7dTLcqJAGIbhuf/LSdVUsppkYiYak4qcRJCGFqQVEATkIBGdDOJhoCXanWzzLL+qt6o3/f94/4Lv+Dv+YpwX/n4qzgIvKHhBRh+HYZrt+P6GLs6tKDuKrJwqduKsInZo4tDNatyIIjbSZZ1BHr/pKUbPieMUJRiUEseBEWOMhDh2RhFmRB7bcIGBMXmsBhiVIgY+BpDHljLHKBTx0MMMyeOp5GKkiDwezDAD8tgXHYxIHkeChemTx+vnKeZlTf4lGWjWQJbiP8+6ek13RnOGWFarYBiqG5a0u8rRSzuhO73O3XWrx261ru/mlHc7nLZvrn4Wrm7a0wVVnHvT+RxxnV+/Oxwq/oUZEcfJzLRVx/8PFZYEcR7aEE4KwA8PXGBZSNeT83Hi6GA0QpOSujgAZjnI8jjefBTHUILjSUW8g+Bh0Hje3DTGdl+b1EAn2fLkyjYWmQUel605wahBmqYLub7rPe8kDist2tPV4tHAQBgmwuLVAJqnNC1Vavt4C/KbemyK4yayDIxTEqrF3ivUG8mNu7SqxGueH50DMRKqxA4La7RLhNUxXjOyehmo4O1j7L6CBsoZIn+MOa5chhSelvs4eJQvkXAdtI/VbnUeEOn1dvG6JYhE+lW3aRm7j/0PCGfc22U8/CMIPAWu9DAo44dn7iy20dPtNk46AqCjbPGd5P0fcldSSIqTeoAAAAAASUVORK5CYII=";
  
          
            if(online){
                db.transaction(
                    function (transaction) {
                        transaction.executeSql(
                             'INSERT OR REPLACE INTO contacts (fullName, url, memberId, location, status, picture, company, position, past, current) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);',
                          [entry.fullName, entry.url, entry.memberId, entry.location, entry.status, noPicture, entry.company, entry.position, entry.past , entry.current], null, errorHandler
                    );
                }
            );
            return false;
            }
        },
        
             
        //get all child categories of 'path' from database
        getContacts: function (callback) {
            
            db.transaction(
                function (transaction) {
                    transaction.executeSql(
                        'SELECT * FROM contacts', [],
                        function (transaction, result) {                        
                            var contacts = [], i, row;
                            for (i = 0; i < result.rows.length; i += 1) {
                                row = result.rows.item(i);
                                contacts.push(row);
                            }                     
                            callback(contacts);
                        }, errorHandler);
                }
            );
        },
        
        
        // Delete table from database
      dropTable: function () {
         db.transaction(
             function(transaction) {
                 transaction.executeSql("DROP TABLE contacts", [], null, errorHandler);
             }
         );
     },
              
      
    };
}());

