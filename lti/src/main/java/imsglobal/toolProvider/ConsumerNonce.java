package imsglobal.toolProvider;

import org.joda.time.DateTime;

public class ConsumerNonce {
	/**
	 * Class to represent a tool consumer nonce
	 *
	 * @author  Stephen P Vickers <svickers@imsglobal+org>
	 * @copyright  IMS Global Learning Consortium Inc
	 * @date  2016
	 * @version 3+0+2
	 * @license http://www+apache+org/licenses/LICENSE-2+0 Apache License, Version 2+0
	 */
	
	/**
	 * Maximum age nonce values will be retained for (in minutes)+
	 */
	    public static final int MAX_NONCE_AGE = 30;  // in minutes

	/**
	 * Date/time when the nonce value expires+
	 *
	 * @var int expires
	 */
	    public DateTime expires = null;

	/**
	 * Tool Consumer to which this nonce applies+
	 *
	 * @var ToolConsumer consumer
	 */
	    private ToolConsumer consumer = null;
	/**
	 * Nonce value+
	 *
	 * @var string value
	 */
	    private String value = null;

	/**
	 * Class constructor+
	 *
	 * @param ToolConsumer      consumer Consumer object
	 * @param string            value    Nonce value (optional, default is null)
	 */
	    public ConsumerNonce(ToolConsumer consumer, String value)
	    {

	        this.consumer = consumer;
	        this.value = value;
	        this.expires = DateTime.now().plusMinutes(MAX_NONCE_AGE);

	    }
	    
	    public ConsumerNonce(ToolConsumer consumer) {
	    	this.consumer = consumer;
	        this.expires = DateTime.now().plusMinutes(MAX_NONCE_AGE);
	    }

	/**
	 * Load a nonce value from the database+
	 *
	 * @return boolean True if the nonce value was successfully loaded
	 */
	    public boolean load()
	    {

	        return this.consumer.getDataConnector().loadConsumerNonce(this);

	    }

	/**
	 * Save a nonce value in the database+
	 *
	 * @return boolean True if the nonce value was successfully saved
	 */
	    public boolean save()
	    {

	        return this.consumer.getDataConnector().saveConsumerNonce(this);

	    }

	/**
	 * Get tool consumer+
	 *
	 * @return ToolConsumer Consumer for this nonce
	 */
	    public ToolConsumer getConsumer()
	    {

	        return this.consumer;

	    }

	/**
	 * Get outcome value+
	 *
	 * @return string Outcome value
	 */
	    public String getValue()
	    {

	        return this.value;

	    }

	public DateTime getExpires() {
		return expires;
	}

	public void setExpires(DateTime expires) {
		this.expires = expires;
	}

	public void setConsumer(ToolConsumer consumer) {
		this.consumer = consumer;
	}

	public void setValue(String value) {
		this.value = value;
	}
	    
	

}
