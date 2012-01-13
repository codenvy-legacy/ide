/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.extension.gadget.server.opensocial.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 19, 2010 $
 *
 */
/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 19, 2010 $
 * 
 */
public class Person
{
   /**
    * A general statement about the person.
    */
   private String aboutMe;

   /**
    * An online accounts held by this Person.
    */
   private List<Account> accounts;

   /**
    * Person's favorite activities.
    */
   private List<String> activities;

   /**
    * A physical mailing addresses for this Person.
    */
   private List<Address> addresses;

   /**
    * The age of this person.
    */
   private Integer age;

   /**
    * The wedding anniversary of this person.
    */
   private Date anniversary;

   /**
    * A collection of AppData keys and values.
    */
   private Map<String, ? extends Object> appData;

   /**
    * The birthday of this person.
    */
   private Date birthday;

   /**
    * Person's body characteristics.
    */
   private String bodyType;

   /**
    * Person's favorite books.
    */
   private List<String> books;

   /**
    * Person's favorite cars.
    */
   private List<String> cars;

   /**
    * Description of the person's children.
    */
   private List<String> children;

   /**
    * Indicates whether the user and this Person have established a bi-directionally asserted connection of some kind on the
    * Service Provider's service.
    */
   private boolean connected;

   /**
    * The name of this Person, suitable for display to end-users.
    */
   private String displayName;

   /**
    * Person's drinking status.
    */
   private String drinker;

   /**
    * E-mail address for this Person.
    */
   private List<String> emails;

   /**
    * Person's ethnicity.
    */
   private String ethnicity;

   /**
    * Person's thoughts on fashion.
    */
   private String fashion;

   /**
    * Person's favorite food.
    */
   private List<String> food;

   /**
    * The gender of this person.
    */
   private Gender gender;

   /**
    * Describes when the person is happiest.
    */
   private String happiestWhen;

   /**
    * Indicating whether the user has application installed.
    */
   private boolean hasApp;

   /**
    * Person's favorite heroes.
    */
   private List<String> heroes;

   /**
    * Person's thoughts on humor.
    */
   private String humor;

   /**
    * Unique identifier for the Person.
    */
   private String id;

   /**
    * Instant messaging address for this Person.
    */
   private List<String> ims;

   /**
    * Person's interests, hobbies or passions.
    */
   private List<String> interests;

   /**
    * Person's favorite jobs, or job interests and skills.
    */
   private String jobInterests;

   /**
    * List of the languages that the person speaks as ISO 639-1 codes.
    */
   private List<String> languagesSpoken;

   /**
    * Description of the person's living arrangement.
    */
   private String livingArrangement;

   /**
    * Person's statement about who or what they are looking for, or what they are interested in meeting people for.
    */
   private List<String> lookingFor;

   /**
    * The living address.
    */
   private Address location;

   /**
    * Person's favorite movies.
    */
   private List<String> movies;

   /**
    * Person's favorite music.
    */
   private List<String> music;

   /**
    * The broken-out components and fully formatted version of the person's real name.
    */
   private Name name;

   /**
    * Person's current network status.
    */
   private Enum<NetworkState> networkPresence;

   /**
    * The casual way to address this Person in real life.
    */
   private String nickname;

   /**
    * Notes about this person, with an unspecified meaning or usage.
    */
   private String note;

   /**
    * A current or past organizational affiliation of this Person.
    */
   private List<Organization> organizations;

   /**
    * Description of the person's pets.
    */
   private List<String> pets;

   /**
    * Phone numbers for this Person.
    */
   private List<String> phoneNumbers;

   /**
    * URL of a photo of this person.
    */
   private List<String> photos;

   /**
    * Person's political views.
    */
   private List<String> politicalViews;

   /**
    * The preferred username of this person on sites that ask for a username.
    */
   private String preferredUsername;

   /**
    * URL of a person's profile song.
    */
   private String profileSong;

   /**
    * URL of a person's profile video.
    */
   private String profileVideo;

   /**
    * Person's profile URL, specified as a string. This URL must be fully qualified.
    */
   private String profileUrl;

   /**
    * The date this Person was first added to the user's address book or friends list.
    */
   private Date published;

   /**
    * Person's favorite quotes.
    */
   private List<String> quotes;

   /**
    * A bi-directionally asserted relationship type that was established between the user and this person by the Service Provider.
    */
   private List<String> relationships;

   /**
    * Person's relationship status.
    */
   private String relationshipStatus;

   /**
    * Person's relgion or religious views.
    */
   private String religion;

   /**
    * Person's comments about romance.
    */
   private String romance;

   /**
    * What the person is scared of.
    */
   private String scaredOf;

   /**
    * Person's sexual orientation.
    */
   private String sexualOrientation;

   /**
    * Person's smoking status.
    */
   private String smoker;

   /**
    * Person's favorite sports.
    */
   private List<String> sports;

   /**
    * A user-defined category label for this person.
    */
   private List<String> tags;

   /**
    * The offset from UTC of this Person's current time zone, as of the time this response was returned.
    */
   private Long utcOffset;

   /**
    * Person's turn offs.
    */
   private List<String> turnOffs;

   /**
    * Person's turn ons.
    */
   private List<String> turnOns;

   /**
    * Person's favorite TV shows.
    */
   private List<String> tvShows;

   /**
    * The most recent date the details of this Person were updated.
    */
   private Date updated;

   /**
    * URL of a web page relating to this Person.
    */
   private List<String> urls;

   /**
    * Person's photo thumbnail URL, specified as a string.
    */
   private String thumbnailURL;

   /**
    * @return the aboutMe
    */
   public String getAboutMe()
   {
      return aboutMe;
   }

   /**
    * @param aboutMe the aboutMe to set
    */
   public void setAboutMe(String aboutMe)
   {
      this.aboutMe = aboutMe;
   }

   /**
    * @return the accounts
    */
   public List<Account> getAccounts()
   {
      if (accounts == null)
      {
         accounts = new ArrayList<Account>();
      }
      return accounts;
   }

   /**
    * @param accounts the accounts to set
    */
   public void setAccounts(List<Account> accounts)
   {
      this.accounts = accounts;
   }

   /**
    * @return the activities
    */
   public List<String> getActivities()
   {
      if (activities == null)
      {
         activities = new ArrayList<String>();
      }
      return activities;
   }

   /**
    * @param activities the activities to set
    */
   public void setActivities(List<String> activities)
   {
      this.activities = activities;
   }

   /**
    * @return the addresses
    */
   public List<Address> getAddresses()
   {
      if (addresses == null)
      {
         addresses = new ArrayList<Address>();
      }
      return addresses;
   }

   /**
    * @param addresses the addresses to set
    */
   public void setAddresses(List<Address> addresses)
   {
      this.addresses = addresses;
   }

   /**
    * @return the age
    */
   public Integer getAge()
   {
      return age;
   }

   /**
    * @param age the age to set
    */
   public void setAge(Integer age)
   {
      this.age = age;
   }

   /**
    * @return the anniversary
    */
   public Date getAnniversary()
   {
      return anniversary;
   }

   /**
    * @param anniversary the anniversary to set
    */
   public void setAnniversary(Date anniversary)
   {
      this.anniversary = anniversary;
   }

   /**
    * @return the appData
    */
   public Map<String, ? extends Object> getAppData()
   {
      if (appData == null)
      {
         appData = new HashMap<String, Object>();
      }
      return appData;
   }

   /**
    * @param appData the appData to set
    */
   public void setAppData(Map<String, ? extends Object> appData)
   {
      this.appData = appData;
   }

   /**
    * @return the birthday
    */
   public Date getBirthday()
   {
      return birthday;
   }

   /**
    * @param birthday the birthday to set
    */
   public void setBirthday(Date birthday)
   {
      this.birthday = birthday;
   }

   /**
    * @return the bodyType
    */
   public String getBodyType()
   {
      return bodyType;
   }

   /**
    * @param bodyType the bodyType to set
    */
   public void setBodyType(String bodyType)
   {
      this.bodyType = bodyType;
   }

   /**
    * @return the books
    */
   public List<String> getBooks()
   {
      if (books == null)
      {
         books = new ArrayList<String>();
      }
      return books;
   }

   /**
    * @param books the books to set
    */
   public void setBooks(List<String> books)
   {
      this.books = books;
   }

   /**
    * @return the cars
    */
   public List<String> getCars()
   {
      if (cars == null)
      {
         cars = new ArrayList<String>();
      }
      return cars;
   }

   /**
    * @param cars the cars to set
    */
   public void setCars(List<String> cars)
   {
      this.cars = cars;
   }

   /**
    * @return the children
    */
   public List<String> getChildren()
   {
      if (children == null)
      {
         children = new ArrayList<String>();
      }
      return children;
   }

   /**
    * @param children the children to set
    */
   public void setChildren(List<String> children)
   {
      this.children = children;
   }

   /**
    * @return the connected
    */
   public boolean isConnected()
   {
      return connected;
   }

   /**
    * @param connected the connected to set
    */
   public void setConnected(boolean connected)
   {
      this.connected = connected;
   }

   /**
    * @return the displayName
    */
   public String getDisplayName()
   {
      return displayName;
   }

   /**
    * @param displayName the displayName to set
    */
   public void setDisplayName(String displayName)
   {
      this.displayName = displayName;
   }

   /**
    * @return the drinker
    */
   public String getDrinker()
   {
      return drinker;
   }

   /**
    * @param drinker the drinker to set
    */
   public void setDrinker(String drinker)
   {
      this.drinker = drinker;
   }

   /**
    * @return the emails
    */
   public List<String> getEmails()
   {
      return emails;
   }

   /**
    * @param emails the emails to set
    */
   public void setEmails(List<String> emails)
   {
      this.emails = emails;
   }

   /**
    * @return the ethnicity
    */
   public String getEthnicity()
   {
      return ethnicity;
   }

   /**
    * @param ethnicity the ethnicity to set
    */
   public void setEthnicity(String ethnicity)
   {
      this.ethnicity = ethnicity;
   }

   /**
    * @return the fashion
    */
   public String getFashion()
   {
      return fashion;
   }

   /**
    * @param fashion the fashion to set
    */
   public void setFashion(String fashion)
   {
      this.fashion = fashion;
   }

   /**
    * @return the food
    */
   public List<String> getFood()
   {
      if (food == null)
      {
         food = new ArrayList<String>();
      }
      return food;
   }

   /**
    * @param food the food to set
    */
   public void setFood(List<String> food)
   {
      this.food = food;
   }

   /**
    * @return the gender
    */
   public Gender getGender()
   {
      return gender;
   }

   /**
    * @param gender the gender to set
    */
   public void setGender(Gender gender)
   {
      this.gender = gender;
   }

   /**
    * @return the happiestWhen
    */
   public String getHappiestWhen()
   {
      return happiestWhen;
   }

   /**
    * @param happiestWhen the happiestWhen to set
    */
   public void setHappiestWhen(String happiestWhen)
   {
      this.happiestWhen = happiestWhen;
   }

   /**
    * @return the hasApp
    */
   public boolean isHasApp()
   {
      return hasApp;
   }

   /**
    * @param hasApp the hasApp to set
    */
   public void setHasApp(boolean hasApp)
   {
      this.hasApp = hasApp;
   }

   /**
    * @return the heroes
    */
   public List<String> getHeroes()
   {
      if (heroes == null)
      {
         heroes = new ArrayList<String>();
      }
      return heroes;
   }

   /**
    * @param heroes the heroes to set
    */
   public void setHeroes(List<String> heroes)
   {
      this.heroes = heroes;
   }

   /**
    * @return the humor
    */
   public String getHumor()
   {
      return humor;
   }

   /**
    * @param humor the humor to set
    */
   public void setHumor(String humor)
   {
      this.humor = humor;
   }

   /**
    * @return the id
    */
   public String getId()
   {
      return id;
   }

   /**
    * @param id the id to set
    */
   public void setId(String id)
   {
      this.id = id;
   }

   /**
    * @return the ims
    */
   public List<String> getIms()
   {
      return ims;
   }

   /**
    * @param ims the ims to set
    */
   public void setIms(List<String> ims)
   {
      this.ims = ims;
   }

   /**
    * @return the interests
    */
   public List<String> getInterests()
   {
      if (interests == null)
      {
         interests = new ArrayList<String>();
      }
      return interests;
   }

   /**
    * @param interests the interests to set
    */
   public void setInterests(List<String> interests)
   {
      this.interests = interests;
   }

   /**
    * @return the jobInterests
    */
   public String getJobInterests()
   {
      return jobInterests;
   }

   /**
    * @param jobInterests the jobInterests to set
    */
   public void setJobInterests(String jobInterests)
   {
      this.jobInterests = jobInterests;
   }

   /**
    * @return the languagesSpoken
    */
   public List<String> getLanguagesSpoken()
   {
      if (languagesSpoken == null)
      {
         languagesSpoken = new ArrayList<String>();
      }
      return languagesSpoken;
   }

   /**
    * @param languagesSpoken the languagesSpoken to set
    */
   public void setLanguagesSpoken(List<String> languagesSpoken)
   {
      this.languagesSpoken = languagesSpoken;
   }

   /**
    * @return the livingArrangement
    */
   public String getLivingArrangement()
   {
      return livingArrangement;
   }

   /**
    * @param livingArrangement the livingArrangement to set
    */
   public void setLivingArrangement(String livingArrangement)
   {
      this.livingArrangement = livingArrangement;
   }

   /**
    * @return the lookingFor
    */
   public List<String> getLookingFor()
   {
      if (lookingFor == null)
      {
         lookingFor = new ArrayList<String>();
      }
      return lookingFor;
   }

   /**
    * @param lookingFor the lookingFor to set
    */
   public void setLookingFor(List<String> lookingFor)
   {
      this.lookingFor = lookingFor;
   }

   /**
    * @return the location
    */
   public Address getLocation()
   {
      return location;
   }

   /**
    * @param location the location to set
    */
   public void setLocation(Address location)
   {
      this.location = location;
   }

   /**
    * @return the movies
    */
   public List<String> getMovies()
   {
      if (movies == null)
      {
         movies = new ArrayList<String>();
      }
      return movies;
   }

   /**
    * @param movies the movies to set
    */
   public void setMovies(List<String> movies)
   {
      this.movies = movies;
   }

   /**
    * @return the music
    */
   public List<String> getMusic()
   {
      if (music == null)
      {
         music = new ArrayList<String>();
      }
      return music;
   }

   /**
    * @param music the music to set
    */
   public void setMusic(List<String> music)
   {
      this.music = music;
   }

   /**
    * @return the name
    */
   public Name getName()
   {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(Name name)
   {
      this.name = name;
   }

   /**
    * @return the networkPresence
    */
   public Enum<NetworkState> getNetworkPresence()
   {
      return networkPresence;
   }

   /**
    * @param networkPresence the networkPresence to set
    */
   public void setNetworkPresence(Enum<NetworkState> networkPresence)
   {
      this.networkPresence = networkPresence;
   }

   /**
    * @return the nickname
    */
   public String getNickname()
   {
      return nickname;
   }

   /**
    * @param nickname the nickname to set
    */
   public void setNickname(String nickname)
   {
      this.nickname = nickname;
   }

   /**
    * @return the note
    */
   public String getNote()
   {
      return note;
   }

   /**
    * @param note the note to set
    */
   public void setNote(String note)
   {
      this.note = note;
   }

   /**
    * @return the organizations
    */
   public List<Organization> getOrganizations()
   {
      if (organizations == null)
      {
         organizations = new ArrayList<Organization>();
      }
      return organizations;
   }

   /**
    * @param organizations the organizations to set
    */
   public void setOrganizations(List<Organization> organizations)
   {
      this.organizations = organizations;
   }

   /**
    * @return the pets
    */
   public List<String> getPets()
   {
      return pets;
   }

   /**
    * @param pets the pets to set
    */
   public void setPets(List<String> pets)
   {
      this.pets = pets;
   }

   /**
    * @return the phoneNumbers
    */
   public List<String> getPhoneNumbers()
   {
      return phoneNumbers;
   }

   /**
    * @param phoneNumbers the phoneNumbers to set
    */
   public void setPhoneNumbers(List<String> phoneNumbers)
   {
      if (phoneNumbers == null)
      {
         phoneNumbers = new ArrayList<String>();
      }
      this.phoneNumbers = phoneNumbers;
   }

   /**
    * @return the photos
    */
   public List<String> getPhotos()
   {
      if (photos == null)
      {
         photos = new ArrayList<String>();
      }

      return photos;
   }

   /**
    * @param photos the photos to set
    */
   public void setPhotos(List<String> photos)
   {
      this.photos = photos;
   }

   /**
    * @return the politicalViews
    */
   public List<String> getPoliticalViews()
   {
      if (politicalViews == null)
      {
         politicalViews = new ArrayList<String>();
      }
      return politicalViews;
   }

   /**
    * @param politicalViews the politicalViews to set
    */
   public void setPoliticalViews(List<String> politicalViews)
   {
      this.politicalViews = politicalViews;
   }

   /**
    * @return the preferredUsername
    */
   public String getPreferredUsername()
   {
      return preferredUsername;
   }

   /**
    * @param preferredUsername the preferredUsername to set
    */
   public void setPreferredUsername(String preferredUsername)
   {
      this.preferredUsername = preferredUsername;
   }

   /**
    * @return the profileSong
    */
   public String getProfileSong()
   {
      return profileSong;
   }

   /**
    * @param profileSong the profileSong to set
    */
   public void setProfileSong(String profileSong)
   {
      this.profileSong = profileSong;
   }

   /**
    * @return the profileVideo
    */
   public String getProfileVideo()
   {
      return profileVideo;
   }

   /**
    * @param profileVideo the profileVideo to set
    */
   public void setProfileVideo(String profileVideo)
   {
      this.profileVideo = profileVideo;
   }

   /**
    * @return the profileUrl
    */
   public String getProfileUrl()
   {
      return profileUrl;
   }

   /**
    * @param profileUrl the profileUrl to set
    */
   public void setProfileUrl(String profileUrl)
   {
      this.profileUrl = profileUrl;
   }

   /**
    * @return the published
    */
   public Date getPublished()
   {
      return published;
   }

   /**
    * @param published the published to set
    */
   public void setPublished(Date published)
   {
      this.published = published;
   }

   /**
    * @return the quotes
    */
   public List<String> getQuotes()
   {
      if (quotes == null)
      {
         quotes = new ArrayList<String>();
      }
      return quotes;
   }

   /**
    * @param quotes the quotes to set
    */
   public void setQuotes(List<String> quotes)
   {
      this.quotes = quotes;
   }

   /**
    * @return the relationships
    */
   public List<String> getRelationships()
   {
      if (relationships == null)
      {
         relationships = new ArrayList<String>();
      }
      return relationships;
   }

   /**
    * @param relationships the relationships to set
    */
   public void setRelationships(List<String> relationships)
   {
      this.relationships = relationships;
   }

   /**
    * @return the relationshipStatus
    */
   public String getRelationshipStatus()
   {
      return relationshipStatus;
   }

   /**
    * @param relationshipStatus the relationshipStatus to set
    */
   public void setRelationshipStatus(String relationshipStatus)
   {
      this.relationshipStatus = relationshipStatus;
   }

   /**
    * @return the religion
    */
   public String getReligion()
   {
      return religion;
   }

   /**
    * @param religion the religion to set
    */
   public void setReligion(String religion)
   {
      this.religion = religion;
   }

   /**
    * @return the romance
    */
   public String getRomance()
   {
      return romance;
   }

   /**
    * @param romance the romance to set
    */
   public void setRomance(String romance)
   {
      this.romance = romance;
   }

   /**
    * @return the scaredOf
    */
   public String getScaredOf()
   {
      return scaredOf;
   }

   /**
    * @param scaredOf the scaredOf to set
    */
   public void setScaredOf(String scaredOf)
   {
      this.scaredOf = scaredOf;
   }

   /**
    * @return the sexualOrientation
    */
   public String getSexualOrientation()
   {
      return sexualOrientation;
   }

   /**
    * @param sexualOrientation the sexualOrientation to set
    */
   public void setSexualOrientation(String sexualOrientation)
   {
      this.sexualOrientation = sexualOrientation;
   }

   /**
    * @return the smoker
    */
   public String getSmoker()
   {
      return smoker;
   }

   /**
    * @param smoker the smoker to set
    */
   public void setSmoker(String smoker)
   {
      this.smoker = smoker;
   }

   /**
    * @return the sports
    */
   public List<String> getSports()
   {
      if (sports == null)
      {
         sports = new ArrayList<String>();
      }
      return sports;
   }

   /**
    * @param sports the sports to set
    */
   public void setSports(List<String> sports)
   {
      this.sports = sports;
   }

   /**
    * @return the tags
    */
   public List<String> getTags()
   {
      if (tags == null)
      {
         tags = new ArrayList<String>();
      }
      return tags;
   }

   /**
    * @param tags the tags to set
    */
   public void setTags(List<String> tags)
   {
      this.tags = tags;
   }

   /**
    * @return the utcOffset
    */
   public Long getUtcOffset()
   {
      return utcOffset;
   }

   /**
    * @param utcOffset the utcOffset to set
    */
   public void setUtcOffset(Long utcOffset)
   {
      this.utcOffset = utcOffset;
   }

   /**
    * @return the turnOffs
    */
   public List<String> getTurnOffs()
   {
      if (turnOffs == null)
      {
         turnOffs = new ArrayList<String>();
      }
      return turnOffs;
   }

   /**
    * @param turnOffs the turnOffs to set
    */
   public void setTurnOffs(List<String> turnOffs)
   {
      this.turnOffs = turnOffs;
   }

   /**
    * @return the turnOns
    */
   public List<String> getTurnOns()
   {
      if (turnOns == null)
      {
         turnOns = new ArrayList<String>();
      }
      return turnOns;
   }

   /**
    * @param turnOns the turnOns to set
    */
   public void setTurnOns(List<String> turnOns)
   {
      this.turnOns = turnOns;
   }

   /**
    * @return the tvShows
    */
   public List<String> getTvShows()
   {
      if (tvShows == null)
      {
         tvShows = new ArrayList<String>();
      }
      return tvShows;
   }

   /**
    * @param tvShows the tvShows to set
    */
   public void setTvShows(List<String> tvShows)
   {
      this.tvShows = tvShows;
   }

   /**
    * @return the updated
    */
   public Date getUpdated()
   {
      return updated;
   }

   /**
    * @param updated the updated to set
    */
   public void setUpdated(Date updated)
   {
      this.updated = updated;
   }

   /**
    * @return the urls
    */
   public List<String> getUrls()
   {
      if (urls == null)
      {
         urls = new ArrayList<String>();
      }
      return urls;
   }

   /**
    * @param urls the urls to set
    */
   public void setUrls(List<String> urls)
   {
      this.urls = urls;
   }

   /**
    * @return the thumbnailURL
    */
   public String getThumbnailURL()
   {
      return thumbnailURL;
   }

   /**
    * @param thumbnailURL the thumbnailURL to set
    */
   public void setThumbnailURL(String thumbnailURL)
   {
      this.thumbnailURL = thumbnailURL;
   }
}
