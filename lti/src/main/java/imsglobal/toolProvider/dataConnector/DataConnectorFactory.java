package imsglobal.toolProvider.dataConnector;

public class DataConnectorFactory {
	
	private static DataConnector dc;

	public static DataConnector getDataConnector() {
		Object obj = new Object();
		if (dc == null) {
			dc = new DataConnector(obj, null);
		}
		return dc;
	}
	
	/**
	 * Create data connector object.
	 * 
	 * This should happen in the factory!!!!!!!!
	 *
	 * A data connector provides access to persistent storage for the different objects.
	 *
	 * Names of tables may be given a prefix to allow multiple versions to share the same schema.  A separate sub-class is defined for
	 * each different database connection - the class to use is determined by inspecting the database object passed, but this can be overridden
	 * (for example, to use a bespoke connector) by specifying a type.  If no database is passed then this class is used which acts as a dummy
	 * connector with no persistence.
	 *
	 * @param string  $dbTableNamePrefix  Prefix for database table names (optional, default is none)
	 * @param object  $db                 A database connection object or string (optional, default is no persistence)
	 * @param string  $type               The type of data connector (optional, default is based on $db parameter)
	 *
	 * @return DataConnector Data connector object
	 */
	    public static DataConnector getDataConnector(String dbTableNamePrefix, Object db, String type)
	    {
	    	if (dbTableNamePrefix == null) {
	    		dbTableNamePrefix = "";
	    	}
	    	if (db != null && (type == null || type == "")) {
	    		type = db.getClass().toString();
	    	}
	        //if (($type === "pdo") && ($db->getAttribute(PDO::ATTR_DRIVER_NAME) === "sqlite")) {
	        //    $type .= "_sqlite";
	        //}
	        if (type != null && type != "") {
	            type = "DataConnector_" + type;
	        } else {
	            type = "DataConnector";
	        }
	        //this is PHP Reflection!
	        dc = new DataConnector(db, dbTableNamePrefix);
	        //$type = "\\IMSGlobal\\LTI\\ToolProvider\\DataConnector\\{$type}";
	        //$dataConnector = new $type($db, $dbTableNamePrefix);

	        return dc;

	    }

}
