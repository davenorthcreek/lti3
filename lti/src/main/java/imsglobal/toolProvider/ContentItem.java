package imsglobal.toolProvider;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ContentItem implements JSONAware {

/**
 * Class to represent a content-item object
 *
 * @author  Stephen P Vickers <svickers@imsglobal.org>
 * @copyright  IMS Global Learning Consortium Inc
 * @date  2016
 * @version 3.0.2
 * @license http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
 */

/**
 * Media type for LTI launch links.
 */
    private static final String LTI_LINK_MEDIA_TYPE = "application/vnd.ims.lti.v1.ltilink";
    
    private String type;
    private String id;
    private String mediaType;
    private String title;
    private String text;
    private ContentItemPlacement placementAdvice;
    private URL url;

/**
 * Class constructor.
 *
 * @param string type Class type of content-item
 * @param ContentItemPlacement placementAdvice  Placement object for item (optional)
 * @param string id   URL of content-item (optional)
 */
    ContentItem(String type, ContentItemPlacement placementAdvice, String id)
    {

        setType(type);
        setPlacementAdvice(placementAdvice);
        setId(id);

    }
    
    public void setType(String type) {
    	this.type = type;
    }
    
    public void setPlacementAdvice(ContentItemPlacement placementAdvice) {
    	this.placementAdvice = placementAdvice;
    }
    
    public void setId(String id) {
    	this.id = id;
    }

/**
 * Set a URL value for the content-item.
 *
 * @param string url  URL value
 */
    public void setUrl(URL url)
    {

        this.url = url;

    }

/**
 * Set a media type value for the content-item.
 *
 * @param string mediaType  Media type value
 */
    public void setMediaType(String mediaType)
    {

        this.mediaType = mediaType;
    }

/**
 * Set a title value for the content-item.
 *
 * @param string title  Title value
 */
    public void setTitle(String title)
    {
    	this.title = title;

    }

/**
 * Set a link text value for the content-item.
 *
 * @param string text  Link text value
 */
    public void setText(String text)
    {
    	this.text = text;

    }

/**
 * Wrap the content items to form a complete application/vnd.ims.lti.v1.contentitems+json media type instance.
 *
 * @param mixed items An array of content items or a single item
 * @return string
 */
    public static String toJson(List<ContentItem> items) {
    	Map<String, String> wrapper = new HashMap<String, String>();
    	wrapper.put("@context", "http://purl.imsglobal.org/ctx/lti/v1/ContentItem");
    			
    	List<String> itemList = new ArrayList<String>();
    	for (ContentItem item : items) {
    		itemList.add(item.toJSONString());
    	}
    	String listToJson = JSONValue.toJSONString(itemList);
    	wrapper.put("@graph", listToJson);
    	return JSONValue.toJSONString(wrapper);
    }
    

/**
 * Wrap the content items to form a complete application/vnd.ims.lti.v1.contentitems+json media type instance.
 *
 * @param mixed items An array of content items or a single item
 * @return string
 */
    
    public static String toJson(ContentItem item)
    {
    	Map<String, String> wrapper = new HashMap<String, String>();
    	wrapper.put("@context", "http://purl.imsglobal.org/ctx/lti/v1/ContentItem");
    	wrapper.put("@graph", item.toJSONString());
    	return JSONValue.toJSONString(wrapper);
    	

    }

	public String toJSONString() {
		Map<String, String> obj = new HashMap<String, String>();
		if (type != null) obj.put("type", type);
		if (id != null) obj.put("id", id);
		if (mediaType != null) obj.put("mediaType", mediaType);
		if (title != null) obj.put("title", title);
		if (text != null) obj.put("text", text);
		if (url != null) obj.put("url", url.toExternalForm());
		if (placementAdvice != null) obj.put("placementAdvice", placementAdvice.toJSONString());
		return JSONValue.toJSONString(obj);
	}
}