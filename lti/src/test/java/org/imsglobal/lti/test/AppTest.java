package org.imsglobal.lti.test;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.imsglobal.lti.toolProvider.ToolConsumer;
import org.imsglobal.lti.toolProvider.ToolProvider;
import org.imsglobal.lti.toolProvider.dataConnector.DataConnector;
import org.imsglobal.lti.toolProvider.dataConnector.JDBC;
import org.imsglobal.lti.toolProvider.test.TestToolProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;



/**
 * Unit test for simple App.
 */
public class AppTest {
	
	
	private Connection conn;
	private ToolConsumer tc;
	private TestToolProvider ttp;
	
	@Before
	public void setUp() {
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser("dblock");
		dataSource.setPassword("test_db");
		dataSource.setServerName("svr");
		dataSource.setDatabaseName("lti");
		try {
			conn = dataSource.getConnection();
			DataConnector dataConn = new JDBC(conn, null);
			tc = new ToolConsumer("northcreek.ca", dataConn);
			tc.setName("Testing");
			tc.setSecret("secret");
			tc.setEnabled(true);
			tc.save();
			
			//sample baseString from LTI testing site
			//changes:  removed POST from prefix
			//			changed & to ? to turn it into a get call
			//			"oauth_consumer_key"-> "northcreek.ca"
			//	        removed timestamp so it can be overwritten below
			String startUri = "http%3A%2F%2Fltiapps.net%2Ftest%2Ftp.php?context_id%3DS3294476%26" +
			"context_label%3DST101%26context_title%3DTelecommuncations%2520101%26context_type%3DCourseSection%26" +
			"custom_context_memberships_url%3Dhttp%253A%252F%252Fltiapps.net%252Ftest%252Ftc-memberships.php%252F" +
			"context%252F665d24ae7fa71b5b7422a1e48426a42b%26custom_context_setting_url%3Dhttp%253A%252F%252Fltiapps.net%252F" +
			"test%252Ftc-settings.php%252Fcontext%252F665d24ae7fa71b5b7422a1e48426a42b%26" +
			"custom_lineitem_url%3Dhttp%253A%252F%252Fltiapps.net%252Ftest%252Ftc-outcomes2.php%252F665d24ae7fa71b5b7422a1e48426a42b%252FS3294476%252F" +
			"lineitems%252FdyJ86SiwwA9%26custom_lineitems_url%3Dhttp%253A%252F%252Fltiapps.net%252Ftest%252Ftc-outcomes2.php%252F665d24ae7fa71b5b7422a1e48426a42b%252FS3294476%252Flineitems%26custom_link_memberships_url%3Dhttp%253A%252F%252Fltiapps.net%252Ftest%252Ftc-memberships.php%252Flink%252F665d24ae7fa71b5b7422a1e48426a42b%26custom_link_setting_url%3Dhttp%253A%252F%252Fltiapps.net%252Ftest%252Ftc-settings.php%252Flink%252F665d24ae7fa71b5b7422a1e48426a42b%26custom_result_url%3Dhttp%253A%252F%252Fltiapps.net%252Ftest%252Ftc-outcomes2.php%252F665d24ae7fa71b5b7422a1e48426a42b%252FS3294476%252Flineitems%252FdyJ86SiwwA9%252Fresults%252F29123%26custom_results_url%3Dhttp%253A%252F%252Fltiapps.net%252Ftest%252Ftc-outcomes2.php%252F665d24ae7fa71b5b7422a1e48426a42b%252FS3294476%252Flineitems%252FdyJ86SiwwA9%252Fresults%26custom_system_setting_url%3Dhttp%253A%252F%252Fltiapps.net%252Ftest%252Ftc-settings.php%252Fsystem%252F665d24ae7fa71b5b7422a1e48426a42b%26custom_tc_profile_url%3Dhttp%253A%252F%252Fltiapps.net%252Ftest%252Ftc-profile.php%252F665d24ae7fa71b5b7422a1e48426a42b%26ext_ims_lis_basic_outcome_url%3Dhttp%253A%252F%252Fltiapps.net%252Ftest%252Ftc-ext-outcomes.php%26ext_ims_lis_memberships_id%3D665d24ae7fa71b5b7422a1e48426a42b%253A%253A%253A4jflkkdf9s%26ext_ims_lis_memberships_url%3Dhttp%253A%252F%252Fltiapps.net%252Ftest%252Ftc-ext-memberships.php%26ext_ims_lis_resultvalue_sourcedids%3Ddecimal%26ext_ims_lti_tool_setting_id%3D665d24ae7fa71b5b7422a1e48426a42b%253A%253A%253Ad94gjklf954kj%26ext_ims_lti_tool_setting_url%3Dhttp%253A%252F%252Fltiapps.net%252Ftest%252Ftc-ext-setting.php%26launch_presentation_css_url%3Dhttp%253A%252F%252Fltiapps.net%252Ftest%252Fcss%252Ftc.css%26launch_presentation_document_target%3Dframe%26launch_presentation_locale%3Den-GB%26launch_presentation_return_url%3Dhttp%253A%252F%252Fltiapps.net%252Ftest%252Ftc-return.php%26lis_course_offering_sourcedid%3DDD-ST101%26lis_course_section_sourcedid%3DDD-ST101%253AC1%26lis_outcome_service_url%3Dhttp%253A%252F%252Fltiapps.net%252Ftest%252Ftc-outcomes.php%26lis_person_contact_email_primary%3Djbaird%2540uni.ac.uk%26lis_person_name_family%3DBaird%26lis_person_name_full%3DJohn%2520Logie%2520Baird%26lis_person_name_given%3DJohn%26lis_person_sourcedid%3Dsis%253A942a8dd9%26lis_result_sourcedid%3D665d24ae7fa71b5b7422a1e48426a42b%253A%253A%253AS3294476%253A%253A%253A29123%253A%253A%253AdyJ86SiwwA9%26lti_message_type%3Dbasic-lti-launch-request%26lti_version%3DLTI-1p0%26" +
			"oauth_callback%3Dabout%253Ablank%26oauth_consumer_key%3Dnorthcreek.ca%26" + 
			"oauth_nonce%3D8fcb42eb8b4289efa3b7151dbef922d7%26" + 
			"oauth_signature_method%3DHMAC-SHA1%26" + 
			"oauth_timestamp%3D" + (System.currentTimeMillis()/1000L) + "%26" + 
			"oauth_version%3D1.0%26" + 
			"resource_link_description%3DWill%2520ET%2520phone%2520home%252C%2520or%2520not%253B%2520click%2520to%2520discover%2520more.%26" + 
			"resource_link_id%3D429785226%26resource_link_title%3DPhone%2520home%26roles%3DInstructor%26" + 
			"tool_consumer_info_product_family_code%3Djisc%26tool_consumer_info_version%3D1.2%26tool_consumer_instance_contact_email%3Dvle%2540uni.ac.uk%26" + 
			"tool_consumer_instance_description%3DA%2520Higher%2520Education%2520establishment%2520in%2520a%2520land%2520far%252C%2520far%2520away.%26" + 
			"tool_consumer_instance_guid%3Dvle.uni.ac.uk%26tool_consumer_instance_name%3DUniversity%2520of%2520JISC%26" + 
			"tool_consumer_instance_url%3Dhttps%253A%252F%252Fvle.uni.ac.uk%252F%26user_id%3D29123%26" +
			"user_image%3Dhttp%253A%252F%252Fltiapps.net%252Ftest%252Fimages%252Flti.gif";
			
			String reqUri = URLDecoder.decode(startUri, "UTF-8");
			
			URL url = new URL(reqUri);
			Map<String, List<String>> params = splitQuery(url);
			
			MockHttpServletRequest req = new MockHttpServletRequest("GET", reqUri);
			
			//sign the parameters so there is an oauth signature parameter
			params = tc.signParameters(url.getProtocol() + "://" + url.getHost() + url.getPath(),
					"basic-lti-launch-request", 
					ToolProvider.LTI_VERSION1, 
					"GET", params);

			//add all the parameters to the request
			for (String key : params.keySet()) {
				for (String value : params.get(key)) {
					req.addParameter(key, value);
				}
			}

			HttpServletResponse resp = new MockHttpServletResponse();
			ttp = new TestToolProvider(dataConn, req, resp);
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	@After
	public void tearDown() {
		tc.delete();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	@Test
	public void testFoundDatabase() {
		System.out.println("Should be something in the database!!!!");
	}
	*/
	
	@Test
	public void testHasProvider() {
		ttp.handleRequest();
	}
	
	
	private static Map<String, List<String>> splitQuery(URL url) throws UnsupportedEncodingException {
		  final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
		  final String[] pairs = url.getQuery().split("&");
		  for (String pair : pairs) {
		    final int idx = pair.indexOf("=");
		    final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
		    if (!query_pairs.containsKey(key)) {
		      query_pairs.put(key, new LinkedList<String>());
		    }
		    final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
		    query_pairs.get(key).add(value);
		  }
		  return query_pairs;
		}
}
