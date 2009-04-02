  <%@page contentType="text/html; charset=UTF-8"%>
  <%@page import="com.socialjava.TinyFBClient"%> 
  <%@page import="java.util.TreeMap"%> 
  <%@page import="com.sun.jersey.api.client.ClientResponse"%> 
  <%@page import="org.json.JSONObject"%>
  <%@page import="org.json.JSONArray"%>

<fb:dashboard>  </fb:dashboard>
<%
	String sessionKey = request.getParameter("fb_sig_session_key"); // Session Key passed as request parameter
	String currentUsersFriends = "";
	if (sessionKey == null) { // If there is not session key, they user not logged in or this is in the profile tab
%>
<hr>
<p><b>Please authorize the application </b></p>
<form requirelogin=1><input type=submit
	value='Click to authorize' /></form>
<%
	} else { // user is logged on
		TinyFBClient fb = new TinyFBClient(
				"Application ID",
				"Secret Key", sessionKey);
		TreeMap<String, String> tm = new TreeMap<String, String>();
		int status;
		ClientResponse c;

		// get friends and display one at random
		tm.put("method", "friends.get");
		fb.setSession(sessionKey);
		currentUsersFriends = fb.call(tm);
		JSONArray resultArray = new JSONArray(currentUsersFriends);
		int numFriends = resultArray.length();
		int rand = (int) (Math.random() * numFriends);
		int randomFriend = resultArray.getInt(rand);
%>
<b>You have <%=numFriends%> friends! </b>
<br>
One of them is:
<br>
<fb:profile-pic uid="<%=randomFriend%>" size="thumb" linked="true" />
<br>
<fb:name uid="<%=randomFriend%>" useyou="false" linked="true"
	capitalize="true" />
<br>
<%
	// Send a notification
		tm.clear();
		tm.put("to_ids", "");
		tm.put("notification", "Thanks for trying TinyClientExample.");
		c = fb.getResponse("notifications.send", tm);
		status = c.getStatus();
%>
<%
	// Publish a feed
		JSONArray imageArray = new JSONArray();
		JSONObject templateData = new JSONObject();
		JSONObject obj1 = new JSONObject();
		JSONObject imageData = new JSONObject();
		imageData.put("href", "http://www.socialjava.com");
		imageData.put("src",
				"http://images.brightkeep.s3.amazonaws.com/dolls.jpg");
		imageArray.put(imageData);
		templateData.put("images", imageArray); // to populate template_data
		tm.clear();
		tm.put("template_data", templateData.toString());
		tm.put("template_bundle_id", "98170005808");
		tm.put("story_size", "2");
%>
<%
	c = fb.getResponse("feed.publishUserAction", tm);
	}// end user is logged in
%>
<hr>
<A href="http://www.socialjava.com">How to use TinyFBClient</A>|
<A href="http://www.facebook.com/group.php?gid=8299674623">Support</A>|
<A href="http://www.socialjava.com/CarmenDelessio.pdf">Carmen's Resume</A>|
<hr>

<h1> What is TinyClientExample?</h1>
 TinyClientExample illustrates the use of the <A href="http://www.socialjava.com">Java TinyFBClient</A> to communicate with Facebook's REST Server.
<br>This app <A href="http://wiki.developers.facebook.com/index.php/Notifications.send">sends a notification</A>, 
<A href="http://wiki.developers.facebook.com/index.php/Feed.publishUserAction">publishes to the newsFeed</A>,
, and 
<A href="http://wiki.developers.facebook.com/index.php/Friends.get">gets a list of friends</A>
<br>
<br>
 <h1> How do you use TinyFBClient?</h1> 
 Create a new TinyFBClient, create a TreeMap,load Facebook API Parameters, and call the Facebook API method. The example below gets a list of friends.
 <A href="http://www.socialjava.com">(more)</A>
 <pre>
        TinyFBClient fb = new TinyFBClient("APPLICATION ID","SECRET KEY");
       	TreeMap&lt;String, String&gt; tm = new TreeMap&lt;String, String&gt;();
        tm.put(&quot;uid&quot;, &quot;725391757&quot;);
        String friendList = fb.call(&quot;friends.get&quot;, tm);
 </pre> 

 <h1> Why did you make TinyFBClient?</h1> 
 There are several good <A href="http://wiki.developers.facebook.com/index.php/Java">Java Libraries for Facebook</A> development. 
  I've been using <A href="http://code.google.com/p/facebook-java-api/">Face-Java-api</A> and it is a robust and mature library.
  <br>
  But I kept wishing that I could just set up the parameters documented in the <A href="http://wiki.developers.facebook.com/index.php/API">Facebook API</A> and call a Facebook method.
  TinyFBClient does that.  
  <br>The name TinyFBClient is inspired by Tiny C.
  <br>

  <br>

 
    
