 
var online = navigator.onLine;

jQT = new $.jQTouch({                
            icon: 'css/img/exo.png',
            addGlossToIcon: false,
            statusBar: 'black'
        });

    var members = [];
   
    //Inject HTML for all companies
    function addCompanies(companies){            
      $.each(companies, function(index,company) {        
            $("#companies").append(tmpl("company_li_template", company));      
            //if company doesn't exist, create a div
            if (!($("#" + company.id)).length) {
                $('body').append(tmpl("company_menu_template", company));
            }       
            addMembers(company);  
      });
    }
    
    //Inject HMTL for members
    function addMembers(company){                              
        $.each(company.contacts, function(index, contact){
            $("#"+company.companyId+" ul:first").append(tmpl("member_li_template", contact));                  
            $('body').append(tmpl("member_profile_template", contact));   
        });  
    }
   
    //Company Object
    function companyObj(name, id){        
        this.companyName = name;
        this.contacts = new Array;
        this.companyId = "comp"+id;
    }

    //Member Object
    function memberObj(member, memberId){        
        this.fullName = member.firstName + " " + member.lastName;
        this.url = member.siteStandardProfileRequest.url;
        this.memberId = "member"+memberId;
        this.status = member.currentStatus;          
        this.location = member.location.name;   
        this.picture = member.pictureUrl;
        var current = new Array;
        var past = new Array; 
      
        if(!(this.picture))
            this.picture = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADwAAAA8CAAAAAAfl4auAAAAAXNCSVQI5gpbmQAAAAlwSFlzAAALEgAACxIB0t1+/AAAABx0RVh0U29mdHdhcmUAQWRvYmUgRmlyZXdvcmtzIENTNXG14zYAAAAVdEVYdENyZWF0aW9uIFRpbWUAMTIvMS8xMENHbswAAAIKSURBVHja7dTLcqJAGIbhuf/LSdVUsppkYiYak4qcRJCGFqQVEATkIBGdDOJhoCXanWzzLL+qt6o3/f94/4Lv+Dv+YpwX/n4qzgIvKHhBRh+HYZrt+P6GLs6tKDuKrJwqduKsInZo4tDNatyIIjbSZZ1BHr/pKUbPieMUJRiUEseBEWOMhDh2RhFmRB7bcIGBMXmsBhiVIgY+BpDHljLHKBTx0MMMyeOp5GKkiDwezDAD8tgXHYxIHkeChemTx+vnKeZlTf4lGWjWQJbiP8+6ek13RnOGWFarYBiqG5a0u8rRSzuhO73O3XWrx261ru/mlHc7nLZvrn4Wrm7a0wVVnHvT+RxxnV+/Oxwq/oUZEcfJzLRVx/8PFZYEcR7aEE4KwA8PXGBZSNeT83Hi6GA0QpOSujgAZjnI8jjefBTHUILjSUW8g+Bh0Hje3DTGdl+b1EAn2fLkyjYWmQUel605wahBmqYLub7rPe8kDist2tPV4tHAQBgmwuLVAJqnNC1Vavt4C/KbemyK4yayDIxTEqrF3ivUG8mNu7SqxGueH50DMRKqxA4La7RLhNUxXjOyehmo4O1j7L6CBsoZIn+MOa5chhSelvs4eJQvkXAdtI/VbnUeEOn1dvG6JYhE+lW3aRm7j/0PCGfc22U8/CMIPAWu9DAo44dn7iy20dPtNk46AqCjbPGd5P0fcldSSIqTeoAAAAAASUVORK5CYII=";
          
         if(typeof member.positions.values == "undefined"){       
             this.company ="No current position.";
             this.current = "";
             this.past = "";
         }
         else {
            if(typeof member.positions.values[0].company.name == "undefined")       
                this.company ="No current position.";
            else 
                this.company = member.positions.values[0].company.name;
            
           this.current=positionsToHTML(member)[0];
           this.past=positionsToHTML(member)[1];
        }
           
    }

    //Turns the array of positions into an HTML string
    function positionsToHTML(member){
        var pos = new Array;  
        pos[0] = ""; pos[1]=""; 
          
        $.each(member.positions.values, function(index, position){
            if(position.isCurrent)
                pos[0] += "<li>"+position.title+ " at "+position.company.name + "</li>\n";
            else
                pos[1] += "<li>"+position.title+ " at "+position.company.name + "</li>\n";
        }); 
          
         if(pos[0].length)
             pos[0] = "<div class='title'>Current Positions</div><ul>"+pos[0]+"</ul>";
         if(pos[1].length)
             pos[1] = "<div class='title'>Previous Positions</div><ul>"+pos[1]+"</ul>";                 
         
         return pos;
    }     

    //Returns -1 if the company doesnt exist yet
    function companyCheck(allCompanies, newCompany) {
        var ok = -1;
        $.each(allCompanies, function(i, company) {
            if (company.companyName == newCompany)
                ok = i;
        });
        return ok;
    }

    //Sort companies by name
    function sortByCompany(a, b) {
        var x = a.companyName.toLowerCase();
        var y = b.companyName.toLowerCase();
        return ((x < y) ? -1 : ((x > y) ? 1 : 0));
    }

  //Sorts the contacts by company, and sorts companies
  function processCompany(contacts) {   
      
        var companies = new Array;  
        $.each(contacts, function(index, contact){
            var check = companyCheck(companies, contact.company);
                 
            if (check == -1){
                var index = companies.push(new companyObj(contact.company, index+1));
                companies[index-1].contacts[0] = contact;
            }  
            else{
                companies[check].contacts.push(contact);
            }    
        });             
        companies.sort(sortByCompany);
        addCompanies(companies);
  }
 
  //Creates the a contact object
  function processConnections(connections){ 
    
    var contacts = new Array();   
    $.each(connections.values, function(index, member){
      var contact = new memberObj(member, index);
      contacts.push(contact);   
      dataService.createContact(contact);      
    });
    processCompany(contacts);
  }

  //On linkedIn authentication, call api
  function onLinkedInAuth() {          
          IN.API.Connections("me")          
            .fields("firstName", "lastName", "siteStandardProfileRequest", "positions", "picture-url", "current-status", "location")
            .result(processConnections)          
            .error(function error(e) { /* do nothing */ });
  }
   
  function onLinkedInLoad() {
    if(online)
        IN.Event.on(IN, "auth", onLinkedInAuth);
    else
    { $("linkedin").html("");}
  }
  
  $(document).ready(function () {
  
     dataService.startDatabase();
  
    console.log("online?"+online);
    
     if(online){      
        $('input[name=online]').attr('checked', false); 
     }
     else {
         $('input[name=online]').attr('checked', true);
         dataService.getContacts(processCompany);
    }         
  });

