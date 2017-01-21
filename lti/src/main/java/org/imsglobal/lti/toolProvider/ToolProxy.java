package org.imsglobal.lti.toolProvider;

import java.util.List;
import java.util.Map;

import org.imsglobal.lti.toolProvider.dataConnector.DataConnector;
import org.imsglobal.lti.toolProvider.mediaType.ToolService;
import org.joda.time.DateTime;

/**
 * Class to represent an LTI Tool Proxy
 *
 * @author  Stephen P Vickers <svickers@imsglobal.org>
 * @copyright  IMS Global Learning Consortium Inc
 * @date  2016
 * @version  3.0.2
 * @license  GNU Lesser General Public License, version 3 (<http://www.gnu.org/licenses/lgpl.html>)
 * translated to Java by David Block (dave@northcreek.ca)
 */

public class ToolProxy {


/**
 * Local id of tool consumer.
 *
 * @var string id
 */
    public String id = null;

/**
 * Tool Consumer for this tool proxy.
 *
 * @var ToolConsumer consumer
 */
    private ToolConsumer consumer = null;
/**
 * Tool Consumer ID for this tool proxy.
 *
 * @var int consumerId
 */
    private int consumerId = 0;
/**
 * Consumer ID value.
 *
 * @var String id
 */
    private String recordId = null;
/**
 * Data connector object.
 *
 * @var DataConnector dataConnector
 */
    private DataConnector dataConnector = null;
/**
 * Tool Proxy document.
 *
 * @var MediaType\ToolProxy toolProxy
 */
    private org.imsglobal.lti.toolProvider.mediaType.MediaTypeToolProxy toolProxy = null;
    
    private DateTime created = null;
    private DateTime updated = null;


/**
 * Class constructor.
 *
 * @param DataConnector   dataConnector   Data connector
 * @param string                        id              Tool Proxy ID (optional, default is null)
 */
    
    public ToolProxy(DataConnector dataConnector)
    {

        this.initialize();
        this.dataConnector = dataConnector;
        this.recordId = DataConnector.getRandomString(32);

    }
    
    public ToolProxy(DataConnector dataConnector, String id)
    {

        this.initialize();
        this.dataConnector = dataConnector;
        this.load(id);

    }

/**
 * Initialise the tool proxy.
 */
    public void initialize()
    {

        this.id = null;
        this.recordId = null;
        this.toolProxy = null;
        this.created = null;
        this.updated = null;

    }

/**
 * Initialise the tool proxy.
 *
 * Pseudonym for initialize().
 */
    public void initialise()
    {

        this.initialize();

    }

/**
 * Get the tool proxy record ID.
 *
 * @return String Tool Proxy record ID value
 */
    public String getRecordId()
    {

        return this.recordId;

    }

/**
 * Sets the tool proxy record ID.
 *
 * @param String recordId  Tool Proxy record ID value
 */
    public void setRecordId(String recordId)
    {

        this.recordId = recordId;

    }

/**
 * Get tool consumer.
 *
 * @return ToolConsumer Tool consumer object for this context.
 */
    public ToolConsumer getConsumer()
    {

        if (this.consumer == null) {
            this.consumer = ToolConsumer.fromRecordId(this.consumerId, this.getDataConnector());
        }

        return this.consumer;

    }

/**
 * Set tool consumer ID.
 *
 * @param int consumerId  Tool Consumer ID for this resource link.
 */
    public void setConsumerId(int consumerId)
    {

        this.consumer = null;
        this.consumerId = consumerId;

    }

/**
 * Get the data connector.
 *
 * @return DataConnector  Data connector object
 */
    public DataConnector getDataConnector()
    {

        return this.dataConnector;

    }
    
    public DateTime getCreated() {
		return created;
	}

	public void setCreated(DateTime created) {
		this.created = created;
	}

	public DateTime getUpdated() {
		return updated;
	}

	public void setUpdated(DateTime updated) {
		this.updated = updated;
	}



//////
//////  PRIVATE METHOD
//////

/**
 * Load the tool proxy from the database.
 *
 * @param string  id        The tool proxy id value
 *
 * @return boolean True if the tool proxy was successfully loaded
 */
    private boolean load(String id)
    {

        this.initialize();
        this.id = id;
        boolean ok = this.dataConnector.loadToolProxy(this);
        //if (!ok) {
        //    this.enabled = false; // autoEnable; ? TODO figure this out
        //}

        return ok;

    }


	
	

}
