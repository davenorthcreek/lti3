package org.imsglobal.lti.toolProvider.dataConnector;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.imsglobal.lti.LTIUtil;
import org.imsglobal.lti.toolProvider.ConsumerNonce;
import org.imsglobal.lti.toolProvider.Context;
import org.imsglobal.lti.toolProvider.ResourceLink;
import org.imsglobal.lti.toolProvider.ResourceLinkShare;
import org.imsglobal.lti.toolProvider.ResourceLinkShareKey;
import org.imsglobal.lti.toolProvider.ToolConsumer;
import org.imsglobal.lti.toolProvider.ToolProvider;
import org.imsglobal.lti.toolProvider.User;
import org.imsglobal.lti.toolProvider.mediaType.ConsumerProfile;
import org.imsglobal.lti.toolProvider.mediaType.JSONContext;
import org.imsglobal.lti.toolProvider.mediaType.MediaTypeToolProxy;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * Class to represent a data connector for a JDBC database connection.
 *
 * @author      Stephen P Vickers
 * @version     1.1.01 (18-Jun-13)
 */

public class JDBC extends DataConnector {
	
	private Connection conn;

	/**
	 * Constructs a data connector object using the specified database table name
	 * prefix and JDBC database connection.
	 *
	 * @param conn    database connection
	 * @param prefix  table name prefix
	 */
	  public JDBC(Connection conn, String prefix) {
		  super(conn, prefix);
		  this.conn = (Connection)this.db;
	  }


	///
	///  ToolConsumer methods
	///

	/**
	 * Load tool consumer object.
	 *
	 * @param consumer  ToolConsumer object
	 *
	 * @return <code>true</code> if the tool consumer object was successfully loaded
	 */
	  @Override
	  public boolean loadToolConsumer(ToolConsumer consumer) {

	    boolean ok;
	    String parameter = "";
	    String sql = "";
	    String key256 = null;
	    if (consumer.getRecordId() > 0) {
		    sql = "SELECT consumer_pk, name, consumer_key256, consumer_key, secret, lti_version, " +
	        "consumer_name, consumer_version, consumer_guid, " +
	        "profile, tool_proxy, settings, protected, enabled, " +
	        "enable_from, enable_until, last_access, created, updated " +
	        "FROM " + this.prefix + DataConnector.CONSUMER_TABLE_NAME + " " +
	        "WHERE consumer_pk = ?";
		    parameter = String.valueOf(consumer.getRecordId());
	    } else {
	    	key256 = DataConnector.getConsumerKey(consumer.getKey());
	    	sql = "SELECT consumer_pk, name, consumer_key256, consumer_key, secret, lti_version, " +
                    "consumer_name, consumer_version, consumer_guid, " +
                    "profile, tool_proxy, settings, protected, enabled, " +
                    "enable_from, enable_until, last_access, created, updated " +
                    "FROM " + this.prefix + DataConnector.CONSUMER_TABLE_NAME + " " +
                    "WHERE consumer_key256 = ?";
	    	parameter = key256;
	    }
	    try {
	      PreparedStatement stmt = this.conn.prepareStatement(sql);
	      stmt.setString(1, parameter);
	      ResultSet rs = stmt.executeQuery();
	      ok = rs.next();
	      if (ok) {
	    	  if (StringUtils.isEmpty(key256) || 
	    			  StringUtils.isEmpty(rs.getString("consumer_key")) || 
	    			  consumer.getKey().equals(rs.getString("consumer_key"))) {
				consumer.setRecordId(rs.getInt("consumer_pk"));
	    		consumer.setName(rs.getString("name"));
	    		consumer.setKey(StringUtils.isEmpty(rs.getString("consumer_key")) ? rs.getString("consumer_key256") : rs.getString("consumer_key"));
				consumer.setSecret(rs.getString("secret"));
				consumer.setLtiVersion(rs.getString("lti_version"));
				consumer.setConsumerName(rs.getString("consumer_name"));
				consumer.setConsumerVersion(rs.getString("consumer_version"));
				consumer.setConsumerGuid(rs.getString("consumer_guid"));
				String jsonProfile = rs.getString("profile");
				consumer.setProfile(setupProfile(jsonProfile));
				consumer.setToolProxy(rs.getString("tool_proxy"));
				String settings = rs.getString("settings");
				consumer.setSettings(unserialize(settings));
				consumer.setThisprotected(rs.getInt("protected") == 1);
				consumer.setEnabled(rs.getInt("enabled") == 1);
				consumer.setEnableFrom(null);
				if (rs.getTimestamp("enable_from") != null) {
				  consumer.setEnableFrom(new DateTime(rs.getTimestamp("enable_from")));
				}
				consumer.setEnableUntil(null);
				if (rs.getTimestamp("enable_until") != null) {
				  consumer.setEnableUntil(new DateTime(rs.getTimestamp("enable_until")));
				}
				consumer.setLastAccess(null);
				if (rs.getDate("last_access") != null) {
				  consumer.setLastAccess(new DateTime(rs.getTimestamp("last_access")));
				}
				consumer.setCreated(new DateTime(rs.getTimestamp("created")));
				consumer.setUpdated(new DateTime(rs.getTimestamp("updated")));
	    	  }
	      }
	    } catch (SQLException e) {
	      ok = false;
	    }

	    return ok;

	  }


	private ConsumerProfile setupProfile(String jsonProfile) {
		ConsumerProfile profile = new ConsumerProfile();
		JSONContext context = new JSONContext();
		JSONParser parser = new JSONParser();
		try {
			context.parse((JSONObject)parser.parse(jsonProfile));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		profile.setContext(context);
		return profile;
	}

	/**
	 * Save tool consumer object.
	 *
	 * @param consumer  ToolConsumer object
	 *
	 * @return <code>true</code> if the tool consumer object was successfully saved
	 */
	  @Override
	  public boolean saveToolConsumer(ToolConsumer consumer) {

	    boolean ok;
        String key = consumer.getKey();
        String key256 = DataConnector.getConsumerKey(key);
        if (key.equals(key256)) {
            key = null;
        }
        int protect = (consumer.isThisprotected()) ? 1 : 0;
        int enabled = (consumer.isEnabled())? 1 : 0;
        String profile = (consumer.getProfile() != null) ? consumer.getProfile().toJson() : null;
        String settingsValue = serialize(consumer.getSettings());
	    DateTime now = DateTime.now();
	    DateTime from = null;
	    if (consumer.getEnableFrom() != null) {
	      from = consumer.getEnableFrom();
	    }
	    DateTime until = null;
	    if (consumer.getEnableUntil() != null) {
	      until = consumer.getEnableUntil();
	    }
	    DateTime last = null;
	    if (consumer.getLastAccess() != null) {
	      last = consumer.getLastAccess();
	    }
	    String sql;
	    if (consumer.getCreated() == null) {
	      sql = "INSERT INTO " + this.prefix + DataConnector.CONSUMER_TABLE_NAME + " " +
	            "(consumer_key256, consumer_key, name, secret, lti_version, consumer_name, " + //6
	    		"consumer_version, consumer_guid, profile, tool_proxy, settings, protected, " + //6
	            "enabled, enable_from, enable_until, last_access, created, updated) " + //6
	            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";//18
	    } else {
	      sql = "UPDATE " + this.prefix + DataConnector.CONSUMER_TABLE_NAME + " " +
	            "SET consumer_key256 = ?, consumer_key = ?, name = ?, secret = ?, lti_version = ?, " +
	            "consumer_name = ?, consumer_version = ?, consumer_guid = ?, profile = ?, tool_proxy = ?, " +
	            "settings = ?, protected = ?, enabled = ?, enable_from = ?, enable_until = ?, " +
	            "last_access = ?, updated = ? " +
	            "WHERE consumer_key = ?";
	    }
	    try {
	      PreparedStatement stmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

	      stmt.setString(1, key256);
	      stmt.setString(2, key);
	      stmt.setString(3, consumer.getName());
	      stmt.setString(4, consumer.getSecret());
	      stmt.setString(5, consumer.getLtiVersion());
	      stmt.setString(6, consumer.getConsumerName());
	      stmt.setString(7, consumer.getConsumerVersion());
	      stmt.setString(8, consumer.getConsumerGuid());
	      stmt.setString(9, profile);
	      stmt.setString(10, consumer.getToolProxy());
	      stmt.setString(11, settingsValue);
	      stmt.setInt(12, protect);
	      stmt.setInt(13, enabled);
	      stmt.setTimestamp(14, new Timestamp(from.getMillis()));
	      stmt.setTimestamp(15, new Timestamp(until.getMillis()));
	      stmt.setDate(16, new Date(last.getMillis()));
	      Timestamp time = new Timestamp(DateTime.now().getMillis());
	      stmt.setTimestamp(17, time);  // created or updated
	      if (consumer.getCreated() == null) {
	    	  stmt.setTimestamp(18, time);  // updated
	      } else {
	  	      stmt.setString(18, consumer.getKey());
	  	  }
	      
	      ok = stmt.executeUpdate() == 1;
	      if (ok) {
	        if (consumer.getCreated() == null) {
	          consumer.setCreated(now);
	          ResultSet rs = stmt.getGeneratedKeys();
      		  if (rs.next()) {
      			int last_inserted_id = rs.getInt(1);
      			consumer.setRecordId(last_inserted_id);
      		  }
	        }
	        consumer.setUpdated(now);
	      }
	    } catch (SQLException e) {
	      ok = false;
	    }

	    return ok;

	  }

	/**
	 * Delete tool consumer object.
	 *
	 * @param consumer  ToolConsumer object
	 *
	 * @return <code>true</code> if the tool consumer object was successfully deleted
	 */
	  @Override
	  public boolean deleteToolConsumer(ToolConsumer consumer) {

	    boolean ok;

	    try {
	// Delete any nonce values for this consumer
	      String sql = "DELETE FROM " + this.prefix + DataConnector.NONCE_TABLE_NAME + " WHERE consumer_pk = ?";
	      PreparedStatement stmt = this.conn.prepareStatement(sql);
	      stmt.setInt(1, consumer.getRecordId());
	      stmt.executeUpdate();

	// Delete any outstanding share keys for contexts for this consumer
	      sql = "DELETE sk FROM " + this.prefix + DataConnector.RESOURCE_LINK_SHARE_KEY_TABLE_NAME + " sk " +
	      "INNER JOIN " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + 
	      " rl ON sk.resource_link_pk = rl.resource_link_pk " +
	      "WHERE rl.consumer_pk = ?";
	      stmt = this.conn.prepareStatement(sql);
	      stmt.setInt(1, consumer.getRecordId());
	      stmt.executeUpdate();
	      
	// Delete any outstanding share keys for resource links for contexts in this consumer
	      sql = "DELETE sk FROM " + this.prefix + DataConnector.RESOURCE_LINK_SHARE_KEY_TABLE_NAME + " sk " +
	      "INNER JOIN " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + 
	      " rl ON sk.resource_link_pk = rl.resource_link_pk " +
	      "INNER JOIN " + this.prefix + DataConnector.CONTEXT_TABLE_NAME + 
	      " c ON rl.context_pk = c.context_pk " +
	      "WHERE c.consumer_pk = ?";
	      stmt = this.conn.prepareStatement(sql);
	      stmt.setInt(1, consumer.getRecordId());
	      stmt.executeUpdate();

	// Delete any users in contexts for this consumer
	      sql = "DELETE u FROM " + this.prefix + DataConnector.USER_RESULT_TABLE_NAME + " u " +
	      "INNER JOIN " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " rl " + 
	      "ON u.resource_link_pk = rl.resource_link_pk " +
	      "WHERE rl.consumer_key = ?";
	      stmt = this.conn.prepareStatement(sql);
	      stmt.setInt(1, consumer.getRecordId());
	      stmt.executeUpdate();

	   // Delete any users in resource links for this consumer
	        sql = "DELETE u " +
	                       "FROM " + this.prefix + DataConnector.USER_RESULT_TABLE_NAME + " u " +
	                       "INNER JOIN " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " rl ON u.resource_link_pk = rl.resource_link_pk " +
	                       "WHERE rl.consumer_pk = ?";
	        stmt = this.conn.prepareStatement(sql);
		    stmt.setInt(1, consumer.getRecordId());
		    stmt.executeUpdate();


	// Delete any users in resource links for contexts in this consumer
	        sql = "DELETE u " +
	                       "FROM " + this.prefix + DataConnector.USER_RESULT_TABLE_NAME + " u " +
	                       "INNER JOIN " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " rl ON u.resource_link_pk = rl.resource_link_pk " +
	                       "INNER JOIN " + this.prefix + DataConnector.CONTEXT_TABLE_NAME + " c ON rl.context_pk = c.context_pk " +
	                       "WHERE c.consumer_pk = ?";
	        stmt = this.conn.prepareStatement(sql);
		      stmt.setInt(1, consumer.getRecordId());
		      stmt.executeUpdate();


	// Update any resource links for which this consumer is acting as a primary resource link
	        sql = "UPDATE " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " prl " +
	                       "INNER JOIN " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " rl ON prl.primary_resource_link_pk = rl.resource_link_pk " +
	                       "SET prl.primary_resource_link_pk = NULL, prl.share_approved = NULL " +
	                       "WHERE rl.consumer_pk = ?";
	        stmt = this.conn.prepareStatement(sql);
		      stmt.setInt(1, consumer.getRecordId());
		      stmt.executeUpdate();

	// Update any resource links for contexts in which this consumer is acting as a primary resource link
	        sql = "UPDATE " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " prl " +
	                       "INNER JOIN " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " rl ON prl.primary_resource_link_pk = rl.resource_link_pk " +
	                       "INNER JOIN " + this.prefix + DataConnector.CONTEXT_TABLE_NAME + " c ON rl.context_pk = c.context_pk " +
	                       "SET prl.primary_resource_link_pk = NULL, prl.share_approved = NULL " +
	                       "WHERE c.consumer_pk = ?";
	        stmt = this.conn.prepareStatement(sql);
		      stmt.setInt(1, consumer.getRecordId());
		      ok = stmt.executeUpdate() >= 1;

	// Delete any resource links for this consumer
	        sql = "DELETE rl " +
	                       "FROM " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " rl " +
	                       "WHERE rl.consumer_pk = ?";
	        stmt = this.conn.prepareStatement(sql);
		      stmt.setInt(1, consumer.getRecordId());
		      stmt.executeUpdate();

	// Delete any resource links for contexts in this consumer
	        sql = "DELETE rl " +
	                       "FROM " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " rl " +
	                       "INNER JOIN " + this.prefix + DataConnector.CONTEXT_TABLE_NAME + " c ON rl.context_pk = c.context_pk " +
	                       "WHERE c.consumer_pk = ?";
	        stmt = this.conn.prepareStatement(sql);
		      stmt.setInt(1, consumer.getRecordId());
		      stmt.executeUpdate();

	// Delete any contexts for this consumer
	        sql = "DELETE c " +
	                       "FROM " + this.prefix + DataConnector.CONTEXT_TABLE_NAME + " c " +
	                       "WHERE c.consumer_pk = ?";
	        stmt = this.conn.prepareStatement(sql);
		      stmt.setInt(1, consumer.getRecordId());
		      stmt.executeUpdate();


	// Delete consumer
	        sql = "DELETE c " +
	                       "FROM " + this.prefix + DataConnector.CONSUMER_TABLE_NAME + " c " +
	                       "WHERE c.consumer_pk = ?";
	        stmt = this.conn.prepareStatement(sql);
		      stmt.setInt(1, consumer.getRecordId());
		      ok = stmt.executeUpdate() == 1;

	      if (ok) {
	        consumer.initialise();
	      }

	    } catch (SQLException e) {
	      ok = false;
	    }

	    return ok;

	  }

	/**
	 * Load tool consumer objects.
	 *
	 * @return array of all defined ToolConsumer objects
	 */
	  @Override
	  public List<ToolConsumer> getToolConsumers() {

	    List<ToolConsumer> consumers = new ArrayList<ToolConsumer>();

	    String sql = "SELECT consumer_pk, consumer_key, consumer_key, name, secret, lti_version, " + 
	    		     "consumer_name, consumer_version, consumer_guid, " +
	    		     "profile, tool_proxy, settings, " +
	                 "protected, enabled, enable_from, enable_until, last_access, created, updated " +
	                 "FROM " + this.prefix + DataConnector.CONSUMER_TABLE_NAME + " " +
	                 "ORDER BY name";
	    try {
	      PreparedStatement stmt = this.conn.prepareStatement(sql);
	      ResultSet rs = stmt.executeQuery();
	      while (rs.next()) {
	        ToolConsumer consumer = new ToolConsumer(rs.getString("consumer_key"), this, false);
	        consumer.setRecordId(rs.getInt("consumer_pk"));
	        consumer.setName(rs.getString("name"));
	        consumer.setSecret(rs.getString("secret"));
	        consumer.setLtiVersion(rs.getString("lti_version"));
	        consumer.setConsumerName(rs.getString("consumer_name"));
	        consumer.setConsumerVersion(rs.getString("consumer_version"));
	        consumer.setConsumerGuid(rs.getString("consumer_guid"));
	        String jsonProfile = rs.getString("profile");
			consumer.setProfile(setupProfile(jsonProfile));
			consumer.setToolProxy(rs.getString("tool_proxy"));
			String settings = rs.getString("settings");
			consumer.setSettings(unserialize(settings));
	        consumer.setThisprotected(rs.getInt("protected") == 1);
	        consumer.setEnabled(rs.getInt("enabled") == 1);
	        
	        consumer.setEnableFrom(null);
	        if (rs.getTimestamp("enable_from") != null) {
	          consumer.setEnableFrom(new DateTime(rs.getTimestamp("enable_from")));
	        }
	        consumer.setEnableUntil(null);
	        if (rs.getTimestamp("enable_until") != null) {
	          consumer.setEnableUntil(new DateTime(rs.getTimestamp("enable_until")));
	        }
	        consumer.setLastAccess(null);
	        if (rs.getDate("last_access") != null) {
	          consumer.setLastAccess(new DateTime(rs.getTimestamp("last_access")));
	        }
	        consumer.setCreated(new DateTime(rs.getTimestamp("created")));
	        consumer.setUpdated(new DateTime(rs.getTimestamp("updated")));
	        consumers.add(consumer);
	      }
	    } catch (SQLException e) {
	      consumers.clear();
	    }

	    return consumers;

	  }

///
///  ToolProxy methods
///

///
//    Load the tool proxy from the database
///
    public boolean loadToolProxy(MediaTypeToolProxy toolProxy)
    {

        return false;

    }

///
//   Save the tool proxy to the database
///
    public boolean saveToolProxy(MediaTypeToolProxy toolProxy)
    {

        return false;

    }

///
//    Delete the tool proxy from the database
///
    public boolean deleteToolProxy(MediaTypeToolProxy toolProxy)
    {

        return false;

    }
    
    ///
    ///  Context methods
    ///
    
    /**
     * Load context object.
     *
     * @param Context context Context object
     *
     * @return boolean True if the context object was successfully loaded
     */
        public boolean loadContext(Context context)
        {

            boolean ok = false;
            String sql = "";
            
            if (context.getRecordId() > 0) {
                sql = "SELECT context_pk, consumer_pk, lti_context_id, settings, created, updated " +
                               "FROM " + this.prefix + DataConnector.CONTEXT_TABLE_NAME + " " +
                               "WHERE (context_pk = ?)";
            } else {
                sql = "SELECT context_pk, consumer_pk, lti_context_id, settings, created, updated " +
                               "FROM " + this.prefix + DataConnector.CONTEXT_TABLE_NAME + " " +
                               "WHERE (consumer_pk = ?) AND (lti_context_id = ?)";
                               
            }
            try {
      	      PreparedStatement stmt = this.conn.prepareStatement(sql);
      	      if (context.getRecordId() > 0) {
      	    	  stmt.setInt(1, context.getRecordId());
      	      } else {
		    	  stmt.setInt(1, context.getConsumer().getRecordId());
		    	  stmt.setString(2, context.getLtiContextId());
		      }
      	      ResultSet rs = stmt.executeQuery();
      	      while (rs.next()) {
                context.setRecordId(rs.getInt("context_pk"));
                context.setConsumerId(rs.getInt("consumer_pk"));
                context.setLtiContextId(rs.getString("lti_context_id"));
                Map<String, List<String>> settings = unserialize(rs.getString("settings"));
                context.setSettings(settings);
    	        context.setCreated(new DateTime(rs.getTimestamp("created")));
    	        context.setUpdated(new DateTime(rs.getTimestamp("updated")));
                ok = true;
              }
            } catch (SQLException e) {
            	e.printStackTrace();
            	ok = false;
            }

            return ok;

        }

    /**
     * Save context object.
     *
     * @param Context context Context object
     *
     * @return boolean True if the context object was successfully saved
     */
        public boolean saveContext(Context context)
        {
        	boolean ok = false;
            DateTime now = DateTime.now();
            String settingsValue = serialize(context.getSettings());
            int id = context.getRecordId();
            int consumer_pk = context.getConsumer().getRecordId();
            try {
            	String sql = "";
            	PreparedStatement stmt;
	            if (id == 0) {
	                sql = "INSERT INTO " + this.prefix + DataConnector.CONTEXT_TABLE_NAME + " (consumer_pk, lti_context_id, " +
	                               "settings, created, updated) " +
	                               "VALUES (?, ?, ?, ?, ?)";
	                stmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	                stmt.setInt(1, consumer_pk);
	                stmt.setString(2, context.getLtiContextId());
	                stmt.setString(3, settingsValue);
	                stmt.setTimestamp(4, new Timestamp(now.getMillis()));
	      	        stmt.setTimestamp(5, new Timestamp(now.getMillis()));
	            } else {
	                sql = "UPDATE " + this.prefix + DataConnector.CONTEXT_TABLE_NAME + " SET " +
	                               "lti_context_id = ?, settings = ?, " +
	                               "updated = ?" +
	                               "WHERE (consumer_pk = ?) AND (context_pk = ?)";
	                stmt = this.conn.prepareStatement(sql);
	                stmt.setString(1, context.getLtiContextId());
	                stmt.setString(2, settingsValue);
	                stmt.setTimestamp(3, new Timestamp(now.getMillis()));
	                stmt.setInt(4, consumer_pk);
	                stmt.setInt(5, id);
	            }
	            ok = stmt.executeUpdate() >= 1;
	            
              
                if (ok) {
                	if (id == 0) {
                		ResultSet rs = stmt.getGeneratedKeys();
                		if (rs.next()) {
                			int last_inserted_id = rs.getInt(1);
                			context.setRecordId(last_inserted_id);
                			context.setCreated(now);
                		}
                	}
                	context.setUpdated(now);
                }
              } catch (SQLException e) {
              	e.printStackTrace();
              	ok = false;
              }

            return ok;

        }

    /**
     * Delete context object.
     *
     * @param Context context Context object
     *
     * @return boolean True if the Context object was successfully deleted
     */
        public boolean deleteContext(Context context)
        {
        	String sql;
        	PreparedStatement stmt;
        	boolean ok = false;
        	try {
	    // Delete any outstanding share keys for resource links for this context
	            sql = "DELETE sk " +
	                           "FROM " + this.prefix + DataConnector.RESOURCE_LINK_SHARE_KEY_TABLE_NAME + " sk " +
	                           "INNER JOIN " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " rl ON sk.resource_link_pk = rl.resource_link_pk " +
	                           "WHERE rl.context_pk = ?";
	            stmt = this.conn.prepareStatement(sql);
		        stmt.setInt(1, context.getRecordId());
		        stmt.executeUpdate();
		        
	    // Delete any users in resource links for this context
	            sql = "DELETE u " +
	                           "FROM " + this.prefix + DataConnector.USER_RESULT_TABLE_NAME + " u " +
	                           "INNER JOIN " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " rl ON u.resource_link_pk = rl.resource_link_pk " +
	                           "WHERE rl.context_pk = ?";
	            stmt = this.conn.prepareStatement(sql);
	   	        stmt.setInt(1, context.getRecordId());
	   	        stmt.executeUpdate();
	               	        
	    // Update any resource links for which this consumer is acting as a primary resource link
	            sql = "UPDATE " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " prl " +
	                           "INNER JOIN " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " rl ON prl.primary_resource_link_pk = rl.resource_link_pk " +
	                           "SET prl.primary_resource_link_pk = null, prl.share_approved = null " +
	                           "WHERE rl.context_pk = ?";
	            stmt = this.conn.prepareStatement(sql);
		        stmt.setInt(1, context.getRecordId());
		        stmt.executeUpdate();
		        
	    // Delete any resource links for this consumer
	            sql = "DELETE rl " +
	                           "FROM " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " rl " +
	                           "WHERE rl.context_pk = ?";
	            stmt = this.conn.prepareStatement(sql);
		        stmt.setInt(1, context.getRecordId());
		        stmt.executeUpdate();
		        
	    // Delete context
	            sql = "DELETE c " +
	                           "FROM " + this.prefix + DataConnector.CONTEXT_TABLE_NAME + " c " +
	                           "WHERE c.context_pk = ?";
	            stmt = this.conn.prepareStatement(sql);
		        stmt.setInt(1, context.getRecordId());
		        stmt.executeUpdate();
        	} catch (SQLException e) {
        		e.printStackTrace();
        		ok = false;
        	}
            if (ok) {
                context.initialize();
            }

            return ok;

        }



	///
	///  ResourceLink methods
	///

	/**
	 * Load resource link object.
	 *
	 * @param resourceLink  ResourceLink object
	 *
	 * @return <code>true</code> if the resource link object was successfully loaded
	 */
	  @Override
	  public boolean loadResourceLink(ResourceLink resourceLink) {

	    boolean ok = false;
	    String sql;
	    PreparedStatement stmt;
	    try {
		    if (resourceLink.getRecordId() > 0) {
	            sql = "SELECT resource_link_pk, context_pk, consumer_pk, lti_resource_link_id, settings, primary_resource_link_pk, share_approved, created, updated " +
	                           "FROM " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " " +
	                           "WHERE (resource_link_pk = ?)";
	            stmt = this.conn.prepareStatement(sql);
	            stmt.setInt(1, resourceLink.getRecordId());
	        } else if (resourceLink.getContext() != null) {
	            sql = "SELECT resource_link_pk, context_pk, consumer_pk, lti_resource_link_id, settings, primary_resource_link_pk, share_approved, created, updated " +
	                           "FROM " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " " +
	                           "WHERE (context_pk = ?) AND (lti_resource_link_id = ?)";
	            stmt = this.conn.prepareStatement(sql);
	            stmt.setInt(1, resourceLink.getRecordId());
	            stmt.setString(2, resourceLink.getId());
	        } else {
	            sql = "SELECT r.resource_link_pk, r.context_pk, r.consumer_pk, r.lti_resource_link_id, r.settings, r.primary_resource_link_pk, r.share_approved, r.created, r.updated " +
	                           "FROM " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " r LEFT OUTER JOIN " +
	                           this.prefix + DataConnector.CONTEXT_TABLE_NAME + " c ON r.context_pk = c.context_pk " +
	                           " WHERE ((r.consumer_pk = ?) OR (c.consumer_pk = ?)) AND (lti_resource_link_id = ?)";
	            stmt = this.conn.prepareStatement(sql);
	            stmt.setInt(1, resourceLink.getConsumer().getRecordId());
	            stmt.setInt(2, resourceLink.getConsumer().getRecordId());
	            stmt.setString(3, resourceLink.getId());
	        }

	      ResultSet rs = stmt.executeQuery();
	      ok = rs.next();
	      if (ok) {
	    	  resourceLink.setRecordId(rs.getInt("resource_link_pk"));
	    	  resourceLink.setContextId(rs.getInt("context_pk"));
	    	  resourceLink.setConsumerId(rs.getInt("consumer_pk"));
	    	  resourceLink.setLtiResourceLinkId(rs.getString("lti_resource_link_id"));
	    	  Map<String, List<String>> settings = unserialize(rs.getString("settings"));
              resourceLink.setSettings(settings);
	    	  resourceLink.setPrimaryResourceLinkId(rs.getString("primary_resource_link_pk"));
	    	  resourceLink.setShareApproved(rs.getInt("share_approved") != 0);
	    	  resourceLink.setCreated(new DateTime(rs.getTimestamp("created")));
  	          resourceLink.setUpdated(new DateTime(rs.getTimestamp("updated")));
	    	  
	      }
	    } catch (SQLException e) {
	      ok = false;
	    }

	    return ok;

	  }

	/**
	 * Save resource link object.
	 *
	 * @param resourceLink  ResourceLink object
	 *
	 * @return <code>true</code> if the resource link object was successfully saved
	 */
	  @Override
	  public boolean saveResourceLink(ResourceLink resourceLink) {

	    boolean ok;
	    Integer approved = null;
	    if (resourceLink.isShareApproved() == null) {
	    	approved = null;
	    } else if (resourceLink.isShareApproved()) {
	    	approved = 1;
	    } else {
	    	approved = 0;
	    }
	    String primaryResourceLinkId = null;
        if (StringUtils.isNotEmpty(resourceLink.getPrimaryResourceLinkId())) {
            primaryResourceLinkId = resourceLink.getPrimaryResourceLinkId();
        }
        DateTime now = DateTime.now();
        String settingsValue = serialize(resourceLink.getSettings());
        Integer consumerId = null;
        Integer contextId = null;
        if (resourceLink.getContext() != null) {
            contextId = resourceLink.getContext().getRecordId();
        } else if (resourceLink.getContextId() > 0) {
            contextId = resourceLink.getContextId();
        } else {
            consumerId = resourceLink.getConsumer().getRecordId();
        }
        int id = resourceLink.getRecordId();
	    String sql;
	    PreparedStatement stmt;
	    try {
			     
		    if (id == 0) {
		    	sql = "INSERT INTO " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " " +
		            "(consumer_pk, context_pk, lti_resource_link_id, settings, " +
		            "primary_resource_link_pk, share_approved, created, updated) " +
		            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		    	stmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		    	if (consumerId == null) {
		    		stmt.setNull(1, java.sql.Types.INTEGER);
		    	} else {
		    		stmt.setInt(1, consumerId);
		    	}
		    	if (contextId == null) {
		    		stmt.setNull(2, java.sql.Types.INTEGER);
		    	} else {
		    		stmt.setInt(2, contextId);
		    	}
			    stmt.setString(3, resourceLink.getLtiResourceLinkId());
			    stmt.setString(4, settingsValue);
			    stmt.setString(5, primaryResourceLinkId);
		        stmt.setInt(6, approved);
	            stmt.setTimestamp(7, new Timestamp(now.getMillis()));
	  	        stmt.setTimestamp(8, new Timestamp(now.getMillis()));
		    } else if (contextId != null) {
		    	sql = "UPDATE " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " " +
		            "SET consumer_pk = ?, lti_resource_link_id = ?, settings = ?, " +
		            "primary_resource_link_pk = ?, share_approved = ?, updated = ? " +
		            "WHERE (context_pk = ?) AND (resource_link_pk = ?)";
		    	stmt = this.conn.prepareStatement(sql);
		    	if (consumerId == null) {
		    		//I think this will usually be the case here if I follow the above logic
		    		stmt.setNull(1, java.sql.Types.INTEGER);
		    	} else {
		    		stmt.setInt(1, consumerId);
		    	}
			    stmt.setString(2, resourceLink.getLtiResourceLinkId());
			    stmt.setString(3, settingsValue);
			    stmt.setString(4, primaryResourceLinkId);
		        stmt.setInt(5, approved);
	            stmt.setTimestamp(6, new Timestamp(now.getMillis()));
	            stmt.setInt(7, contextId);
	            stmt.setInt(8, id);
		    } else {
		    	sql = "UPDATE " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " " +
			            "SET context_pk = ?, lti_resource_link_id = ?, settings = ?, " +
			            "primary_resource_link_pk = ?, share_approved = ?, updated = ? " +
			            "WHERE (consumer_pk = ?) AND (resource_link_pk = ?)";
			    stmt = this.conn.prepareStatement(sql);
		    	stmt.setNull(1, java.sql.Types.INTEGER); //will always be null
			    stmt.setString(2, resourceLink.getLtiResourceLinkId());
			    stmt.setString(3, settingsValue);
			    stmt.setString(4, primaryResourceLinkId);
		        stmt.setInt(5, approved);
	            stmt.setTimestamp(6, new Timestamp(now.getMillis()));
	            stmt.setInt(7, consumerId);
	            stmt.setInt(8, id);
		    }
	 
	        ok = stmt.executeUpdate() == 1;
	        if (ok) {
	        	if (resourceLink.getCreated() == null) {
	        		resourceLink.setCreated(DateTime.now());
	        		ResultSet rs = stmt.getGeneratedKeys();
            		if (rs.next()) {
            			int last_inserted_id = rs.getInt(1);
            			resourceLink.setRecordId(last_inserted_id);
            		}
	        	}
	            resourceLink.setUpdated(DateTime.now());
	        }
	    } catch (SQLException e) {
	      ok = false;
	    }

	    return ok;

	  }

	/**
	 * Delete resource link object.
	 *
	 * @param resourceLink  ResourceLink object
	 *
	 * @return <code>true</code> if the resourceLink object was successfully deleted
	 */
	  @Override
	  public boolean deleteResourceLink(ResourceLink resourceLink) {

	    boolean ok = true;

	    try {
	// Delete any outstanding share keys for resource links for this consumer
	      String sql = "DELETE FROM " + this.prefix + DataConnector.RESOURCE_LINK_SHARE_KEY_TABLE_NAME + " " +
	                   "WHERE (resource_link_pk = ?)";
	      PreparedStatement stmt = this.conn.prepareStatement(sql);
	      stmt.setInt(1, resourceLink.getRecordId());
	      stmt.executeUpdate();

	// Delete users
	      if (ok) {
	        sql = "DELETE FROM " + this.prefix + DataConnector.USER_RESULT_TABLE_NAME + " " +
	               "WHERE (resource_link_pk = ?)";
	        stmt = this.conn.prepareStatement(sql);
	        stmt.setInt(1, resourceLink.getRecordId());
	        stmt.executeUpdate();
	      }

	// Update any resource links for which this is the primary resource link
	      if (ok) {
	        sql = "UPDATE " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " " +
	               "SET primary_resource_link_pk = NULL " +
	               "WHERE (primary_resource_link_pk = ?)";
	        stmt = this.conn.prepareStatement(sql);
	        stmt.setInt(1, resourceLink.getRecordId());
	        stmt.executeUpdate();
	      }

	// Delete resource link
	      if (ok) {
	        sql = "DELETE FROM " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " " +
	        		"WHERE (resource_link_pk = ?)";
	        stmt = this.conn.prepareStatement(sql);
	        stmt.setInt(1, resourceLink.getRecordId());
	        ok = stmt.executeUpdate() == 1;
	      }

	      if (ok) {
	        resourceLink.initialise();
	      }

	    } catch (SQLException e) {
	      ok = false;
	    }

	    return ok;

	  }

	/**
	 * Get array of user objects.
	 *
	 * @param resourceLink  ResourceLink object
	 * @param localOnly     <code>true</code> if only users for the resource link are to be returned (excluding users sharing this resource link)
	 * @param scope         Scope value to use for user IDs
	 *
	 * @return array of User objects
	 */
	  @Override
	  public Map<String,User> getUserResultSourcedIDsResourceLink(ResourceLink resourceLink, boolean localOnly, int scope) {

	    Map<String,User>users = new HashMap<String,User>();

	    String sql;
	    if (localOnly) {
	    	sql = "SELECT u.user_pk, u.lti_result_sourcedid, u.lti_user_id, u.created, u.updated " +
            "FROM " + this.prefix + DataConnector.USER_RESULT_TABLE_NAME + " AS u "  +
            "INNER JOIN " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " AS rl "  +
            "ON u.resource_link_pk = rl.resource_link_pk " +
            "WHERE (rl.resource_link_pk = ?) AND (rl.primary_resource_link_pk IS NULL)";
	    } else {
	    	sql = "SELECT u.user_pk, u.lti_result_sourcedid, u.lti_user_id, u.created, u.updated " +
            "FROM " + this.prefix + DataConnector.USER_RESULT_TABLE_NAME + " AS u "  +
            "INNER JOIN " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " AS rl "  +
            "ON u.resource_link_pk = rl.resource_link_pk " +
            "WHERE ((rl.resource_link_pk = ?) AND (rl.primary_resource_link_pk IS NULL)) OR " +
            "((rl.primary_resource_link_pk = ?) AND (share_approved = 1))";
	    }
	    try {
	      PreparedStatement stmt = this.conn.prepareStatement(sql);
	      stmt.setInt(1, resourceLink.getRecordId());
	      if (!localOnly) {
	    	  stmt.setInt(2, resourceLink.getRecordId());
	      }
	      ResultSet rs = stmt.executeQuery();
	      while (rs.next()) {
	        User user = User.fromResourceLink(resourceLink, rs.getString("lti_user_id"));
	        user.setRecordId(rs.getInt("user_pk"));
	        user.setLtiResultSourcedId(rs.getString("lti_result_sourcedid"));
	        user.setCreated(new DateTime(rs.getTimestamp("created")));
	        user.setUpdated(new DateTime(rs.getTimestamp("updated")));
	        users.put(user.getId(scope), user);
	      }
	    } catch (SQLException e) {
	      users.clear();
	    }

	    return users;

	  }

	/**
	 * Get shares defined for a resource link.
	 *
	 * @param resourceLink  ResourceLink object
	 *
	 * @return array of resourceLinkShare objects
	 */
	  @Override
	  public List<ResourceLinkShare> getSharesResourceLink(ResourceLink resourceLink) {

	    List<ResourceLinkShare> shares = new ArrayList<ResourceLinkShare>();

	    String sql = "SELECT consumer_pk, resource_link_pk, share_approved " +
	                 "FROM " + this.prefix + DataConnector.RESOURCE_LINK_TABLE_NAME + " " +
	                 "WHERE (primary_resource_link_pk = ?) " +
	                 "ORDER BY consumer_pk";
	    try {
	      PreparedStatement stmt = this.conn.prepareStatement(sql);
	      stmt.setInt(1, resourceLink.getRecordId());
	      ResultSet rs = stmt.executeQuery();
	      while (rs.next()) {
	        ResourceLinkShare share = new ResourceLinkShare();
	        share.setResourceLinkId(rs.getString("resource_link_pk"));
	        share.setApproved(rs.getInt("share_approved") == 1);
	        if (rs.wasNull()) {
	        	share.setApproved(null);
	        }
	        shares.add(share);
	      }
	    } catch (SQLException e) {
	      shares.clear();
	    }

	    return shares;

	  }


	///
	///  ConsumerNonce methods
	///

	/**
	 * Load nonce object.
	 *
	 * @param nonce  Nonce object
	 *
	 * @return <code>true</code> if the nonce object was successfully loaded
	 */
	  @Override
	  public boolean loadConsumerNonce(ConsumerNonce nonce) {

	    boolean ok;

	    try {
	// Delete any expired nonce values
	    	DateTime now = DateTime.now();
	    	String sql = "DELETE FROM " + this.prefix + DataConnector.NONCE_TABLE_NAME + " WHERE expires <= ?";
	    	PreparedStatement stmt = this.conn.prepareStatement(sql);
	    	stmt.setTimestamp(1, new Timestamp(now.getMillis()));
	     	stmt.executeUpdate();
	// Load the nonce
	     	sql = "SELECT value AS T FROM " + this.prefix + DataConnector.NONCE_TABLE_NAME + " WHERE (consumer_pk = ?) AND (value = ?)";
	     	stmt = this.conn.prepareStatement(sql);
	     	stmt.setInt(1, nonce.getConsumer().getRecordId());
	     	stmt.setString(2, nonce.getValue());
	     	ResultSet rs = stmt.executeQuery();
	     	ok = rs.next();
	    } catch (SQLException e) {
	    	ok = false;
	    }

	    return ok;

	  }

	/**
	 * Save nonce object.
	 *
	 * @param nonce  Nonce object
	 *
	 * @return <code>true</code> if the nonce object was successfully saved
	 */
	  @Override
	  public boolean saveConsumerNonce(ConsumerNonce nonce) {

	    boolean ok;

	    String sql = "INSERT INTO " + this.prefix + DataConnector.NONCE_TABLE_NAME + " (consumer_pk, value, expires) VALUES (?, ?, ?)";
	    try {
	      PreparedStatement stmt = this.conn.prepareStatement(sql);
	      stmt.setInt(1, nonce.getConsumer().getRecordId());
	      stmt.setString(2, nonce.getValue());
	      stmt.setTimestamp(3, new Timestamp(nonce.getExpires().getMillis()));
	      ok = stmt.executeUpdate() == 1;
	    } catch (SQLException e) {
	      ok = false;
	    }

	    return ok;

	  }


	///
	///  ResourceLinkShareKey methods
	///

	/**
	 * Load resource link share key object.
	 *
	 * @param shareKey Resource link share key object
	 *
	 * @return <code>true</code> if the resource link share key object was successfully loaded
	 */
	  @Override
	  public boolean loadResourceLinkShareKey(ResourceLinkShareKey shareKey) {

	    boolean ok;

	    try {
	// Clear expired share keys
	      Timestamp now = new Timestamp(System.currentTimeMillis());
	      String sql = "DELETE FROM " + this.prefix + DataConnector.RESOURCE_LINK_SHARE_KEY_TABLE_NAME + " WHERE expires <= ?";
	      PreparedStatement stmt = this.conn.prepareStatement(sql);
	      stmt.setTimestamp(1, now);
	      stmt.executeUpdate();

	// Load share key
	      sql = "SELECT resource_link_pk, auto_approve, expires " +
	            "FROM " + this.prefix + DataConnector.RESOURCE_LINK_SHARE_KEY_TABLE_NAME + " " +
	            "WHERE share_key_id = ?";
	      stmt = this.conn.prepareStatement(sql);
	      stmt.setString(1, shareKey.getId());
	      ResultSet rs = stmt.executeQuery();
	      ok = rs.next();
	      if (ok) {
	        shareKey.setResourceLinkId(rs.getInt("resource_link_pk"));
	        shareKey.setAutoApprove(rs.getInt("auto_approve") == 1);
	        shareKey.setExpires(new DateTime(rs.getTimestamp("expires")));
	      }
	    } catch (SQLException e) {
	      ok = false;
	    }

	    return ok;

	  }

	/**
	 * Save resource link share key object.
	 *
	 * @param shareKey  Resource link share key object
	 *
	 * @return <code>true</code> if the resource link share key object was successfully saved
	 */
	  @Override
	  public boolean saveResourceLinkShareKey(ResourceLinkShareKey shareKey) {

	    boolean ok;

	    int approve = 0;
	    if (shareKey.isAutoApprove()) {
	      approve = 1;
	    }
	    String sql;
	    sql = "INSERT INTO " + this.prefix + DataConnector.RESOURCE_LINK_SHARE_KEY_TABLE_NAME + " " +
	          "(share_key_id, resource_link_pk, auto_approve, expires) " +
	          "VALUES (?, ?, ?, ?)";
	    try {
	      PreparedStatement stmt = this.conn.prepareStatement(sql);
	      stmt.setString(1, shareKey.getId());
	      stmt.setInt(2, shareKey.getResourceLinkId());
	      stmt.setInt(3, approve);
	      stmt.setTimestamp(4, new Timestamp(shareKey.getExpires().getMillis()));
	      ok = stmt.executeUpdate() == 1;
	    } catch (SQLException e) {
	      ok = false;
	    }

	    return ok;

	  }

	/**
	 * Delete resource link share key object.
	 *
	 * @param  shareKey  Resource link share key object
	 *
	 * @return <code>true</code> if the resource link share key object was successfully deleted
	 */
	  @Override
	  public boolean deleteResourceLinkShareKey(ResourceLinkShareKey shareKey) {

	    boolean ok;

	    String sql = "DELETE FROM " + this.prefix + DataConnector.RESOURCE_LINK_SHARE_KEY_TABLE_NAME + " WHERE share_key_id = ?";
	    try {
	      PreparedStatement stmt = this.conn.prepareStatement(sql);
	      stmt.setString(1, shareKey.getId());
	      ok = stmt.executeUpdate() == 1;
	      if (ok) {
	        shareKey.initialise();
	      }
	    } catch (SQLException e) {
	      ok = false;
	    }

	    return ok;

	  }


	///
	///  User methods
	///

	/**
	 * Load user object.
	 *
	 * @param user  User object
	 *
	 * @return <code>true</code> if the user object was successfully loaded
	 */
	  @Override
	  public boolean loadUser(User user) {

	    boolean ok;
	    String sql;
	    PreparedStatement stmt;
	    try {
		    if (user.getRecordId() != 0) {
		    	sql = "SELECT user_pk, resource_link_pk, lti_user_id, lti_result_sourcedid, created, updated " +
		    			"FROM " + this.prefix + DataConnector.USER_RESULT_TABLE_NAME + " " +
		                "WHERE (user_pk = ?)";
		    	stmt = this.conn.prepareStatement(sql);
		    	stmt.setInt(1, user.getRecordId());
		  	} else {
		  		sql = "SELECT user_pk, resource_link_pk, lti_user_id, lti_result_sourcedid, created, updated " +
		  				"FROM " + this.prefix + DataConnector.USER_RESULT_TABLE_NAME + " " +
		  				"WHERE (resource_link_pk = ?) AND (lti_user_id = ?)";
		  		stmt = this.conn.prepareStatement(sql);
		  		stmt.setInt(1, user.getResourceLink().getRecordId());
		  		stmt.setString(2, user.getId(ToolProvider.ID_SCOPE_ID_ONLY));
		  	}

	        ResultSet rs = stmt.executeQuery();
	        ok = rs.next();
	        if (ok) {
	        	user.setRecordId(rs.getInt("user_pk"));
	        	user.setResourceLinkId(rs.getInt("resource_link_pk"));
	        	user.setLtiUserId(rs.getString("lti_user_id"));
		        user.setLtiResultSourcedId(rs.getString("lti_result_sourcedid"));
		        user.setCreated(new DateTime(rs.getTimestamp("created")));
		        user.setUpdated(new DateTime(rs.getTimestamp("updated")));
	        }
	    } catch (SQLException e) {
	      ok = false;
	    }

	    return ok;

	  }

	/**
	 * Save user object.
	 *
	 * @param user  User object
	 *
	 * @return <code>true</code> if the user object was successfully saved
	 */
	  @Override
	  public boolean saveUser(User user) {

	    boolean ok;

	    Timestamp time = new Timestamp(DateTime.now().getMillis());
	    String sql;
	    PreparedStatement stmt;
	    try {
		    if (user.getCreated() == null) {
		      sql = "INSERT INTO " + this.prefix + DataConnector.USER_RESULT_TABLE_NAME + " (resource_link_pk, " +
		            "lti_user_id, lti_result_sourcedid, created, updated) " +
		            "VALUES (?, ?, ?, ?, ?)";
		      stmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		      stmt.setInt(1, user.getResourceLink().getRecordId());
		      stmt.setString(2, user.getId(ToolProvider.ID_SCOPE_ID_ONLY));
		      stmt.setString(3, user.getLtiResultSourcedId());
		      stmt.setTimestamp(4, time);
		      stmt.setTimestamp(5, time);
		    } else {
		      sql = "UPDATE " + this.prefix + DataConnector.USER_RESULT_TABLE_NAME + " " +
		             "SET lti_result_sourcedid = ?, updated = ? " +
		             "WHERE user_pk = ?";
		      stmt = this.conn.prepareStatement(sql);
		      stmt.setString(1, user.getLtiResultSourcedId());
		      stmt.setTimestamp(2, time);
		      stmt.setInt(3, user.getRecordId());
		    }
	      
		    ok = stmt.executeUpdate() == 1;
		    if (ok) {
		    	if (user.getCreated() == null) {
		    		user.setCreated(DateTime.now());
		    		ResultSet rs = stmt.getGeneratedKeys();
            		if (rs.next()) {
            			int last_inserted_id = rs.getInt(1);
            			user.setRecordId(last_inserted_id);
            		}
		    	}
		        user.setUpdated(DateTime.now());
		    }
	    } catch (SQLException e) {
	      ok = false;
	    }
	
	    return ok;

	  }

	/**
	 * Delete user object.
	 *
	 * @param user  User object
	 *
	 * @return <code>true</code> if the user object was successfully deleted
	 */
	  @Override
	  public boolean deleteUser(User user) {

	    boolean ok;

	    String sql = "DELETE FROM " + this.prefix + DataConnector.USER_RESULT_TABLE_NAME + " " +
	                 "WHERE (user_pk = ?)";
	    try {
	      PreparedStatement stmt = this.conn.prepareStatement(sql);
	      stmt.setInt(1, user.getRecordId());
	      ok = stmt.executeUpdate() == 1;

	      if (ok) {
	        user.initialise();
	      }

	    } catch (SQLException e) {
	      ok = false;
	    }

	    return ok;

	  }
	  
	  private Map<String, List<String>> unserialize(String settingsValue) {
		  Map<String, String> settings = new HashMap<String, String>();
		  Gson gson = new Gson();
		  if ((settingsValue != null) && (settingsValue.length() > 0)) {
			  try {
				  settings = gson.fromJson(settingsValue, new TypeToken<Map<String, String>>() {}.getType());
			  } catch (JsonSyntaxException e) {
				  e.printStackTrace();
			  }
		  }
		  Map<String, List<String>> s2 = new HashMap<String, List<String>>();
		  for (String k : settings.keySet()) {
			  LTIUtil.setParameter(s2, k, settings.get(k));
		  }
		  return s2;
	  }
	  
	  private String serialize(Map<String, List<String>> settings) {
		  Gson gson = new Gson();
		  String settingsValue = null;
		  if (settings.size() > 0) {
		      settingsValue = gson.toJson(settings);
		  }
		  return settingsValue;
	  }


}
