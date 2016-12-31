package imsglobal.toolProvider;

import org.joda.time.DateTime;

public class Outcome {
	/**
	 * Class to represent an outcome
	 *
	 * @author  Stephen P Vickers <svickers@imsglobal.org>
	 * @copyright  IMS Global Learning Consortium Inc
	 * @date  2016
	 * @version 3.0.2
	 * @license http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
	 */


	/**
	 * Language value.
	 *
	 * @var string language
	 */
	    private String language = null;
	/**
	 * Outcome status value.
	 *
	 * @var string status
	 */
	    private String status = null;
	/**
	 * Outcome date value.
	 *
	 * @var string date
	 */
	    private DateTime date;
	/**
	 * Outcome type value.
	 *
	 * @var string type
	 */
	    private String type = null;
	/**
	 * Outcome data source value.
	 *
	 * @var string dataSource
	 */
	    private String dataSource = null;
    /**
     * @deprecated Use property from User object instead
     * <p>
     * Result sourcedId.
     */
      @Deprecated
      private String sourcedId = null;
      
	/**
	 * Outcome value.
	 *
	 * @var string value
	 */
	    private String value = null;

	    
	    
    /**
     * Construct an empty outcome object.
     * <p>
     * The language defaults to <code>en-US</code>, and a type of <code>decimal</code>
     * and the current date/time are used.
     */
      public Outcome() {
        this(null, null);
      }

    /**
     * Construct an outcome object using the specified value.
     * <p>
     * The language defaults to <code>en-US</code>, and a type of <code>decimal</code>
     * and the current date/time are used.
     *
     * @param value      outcome value, may be null
     */
      public Outcome(String value) {
	        this.value = value;
	        this.language = "en-US";
	        this.date = DateTime.now();
	        this.type = "decimal";

      }

    /**
     * @deprecated use <code>{@link Outcome#Outcome(String)}</code> instead.
     * <p>
     * Construct an outcome object using the specified sourcedId and value.
     * <p>
     * The language defaults to <code>en-US</code>, and a type of <code>decimal</code>
     * and the current date/time are used.
     *
     * @param sourcedId  sourcedId value for the user/context
     * @param value      outcome value, may be null
     */
      @Deprecated
      public Outcome(String sourcedId, String value) {
    	  
        this.value = value;
        this.language = "en-US";
        this.date = DateTime.now();
        this.type = "decimal";
        this.sourcedId = sourcedId;

      }

	/**
	 * Get the outcome value.
	 *
	 * @return string Outcome value
	 */
	    public String getValue()
	    {

	        return this.value;

	    }

	/**
	 * Set the outcome value.
	 *
	 * @param string value  Outcome value
	 */
	    public void setValue(String value)
	    {

	        this.value = value;

	    }

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * @deprecated Use property from User object instead
	 *
	 * Returns the result sourcedId value.
	 *
	 * @return result sourcedId value
	 */
	@Deprecated
	public String getSourcedId() {
		return this.sourcedId;
	}


	/**
	 * Returns a string representation of this outcome.
	 * <p>
	 * The string representation consists of the outcome's type and value.
	 *
	 * @return  a string representation of this outcome
	 */
	  @Override
	  public String toString() {
	
	    StringBuilder oValue = new StringBuilder();
	    oValue.append(Outcome.class.getName()).append("\n");
	    oValue.append("  type: ").append(this.getType()).append("\n");
	    oValue.append("  value: ").append(this.getValue()).append("\n");
	
	    return oValue.toString();
	
	  }

}
