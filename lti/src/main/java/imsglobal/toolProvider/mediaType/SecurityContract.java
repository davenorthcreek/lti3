package imsglobal.toolProvider.mediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import imsglobal.profile.ServiceDefinition;
import imsglobal.toolProvider.Context;
import imsglobal.toolProvider.ToolProvider;

public class SecurityContract {

/**
 * Class to represent an LTI Security Contract document
 *
 * @author  Stephen P Vickers <svickers@imsglobal.org>
 * @copyright  IMS Global Learning Consortium Inc
 * @date  2016
 * @version  3.0.0
 * @license  GNU Lesser General Public License, version 3 (<http://www.gnu.org/licenses/lgpl.html>)
 */
	
	private Map<String, String> tcContexts;
	private Map<String, ToolService> toolServices;
	private String sharedSecret;

/**
 * Class constructor.
 *
 * @param ToolProvider toolProvider  Tool Provider instance
 * @param string secret Shared secret
 */
    public SecurityContract(ToolProvider toolProvider, String secret)
    {

        tcContexts = new HashMap<String, String>();  //toolConsumer contexts from JSON-LD spec
        for (JSONContext context : toolProvider.getConsumer().getProfile().getContexts()) {
    		tcContexts.putAll(context.getTerms());
    		//PHP: $tcContexts = array_merge(get_object_vars($context), $tcContexts);
    		//context here is a JSON-LD Context, it contains rewrite rules and ontology refs for a JSON schema
        }

        this.setSharedSecret(secret);
        
        toolServices = new HashMap<String, ToolService>();
        for (ToolService requiredService : toolProvider.getRequiredServices()) {
            for (String format : requiredService.getFormats()) {
                ServiceDefinition service = toolProvider.findService(format, requiredService.getActions());
                if ((service != null) && toolServices.containsKey(service.getId())) {
                    String id = service.getId();
                    String part1 = StringUtils.substringBefore(id, ":");
                    String part2 = StringUtils.substringAfter(id, ":");
                    if (StringUtils.isNotEmpty(part2)) {
                        if (tcContexts.containsKey(part1)) {
                            id = tcContexts.get(part1) + part2;
                        }
                    }
                    ToolService toolService = new ToolService();
                    toolService.setType("RestServiceProfile");
                    toolService.setService(id);
                    toolService.setActions(requiredService.getActions());
                    toolServices.put(service.getId(), toolService);
                }
            }
        }
        for (ToolService optionalService : toolProvider.getOptionalServices()) {
            for (String format : optionalService.getFormats()) {
                ServiceDefinition service = toolProvider.findService(format, optionalService.getActions());
                if ((service != null) && toolServices.containsKey(service.getId())) {
                    String id = service.getId();
                    String part1 = StringUtils.substringBefore(id, ":");
                    String part2 = StringUtils.substringAfter(id, ":");
                    if (StringUtils.isNotEmpty(part2)) {
                        if (tcContexts.containsKey(part1)) {
                            id = tcContexts.get(part1) + part2;
                        }
                    }
                    ToolService toolService = new ToolService();
                    toolService.setType("RestServiceProfile");
                    toolService.setService(id);
                    toolService.setActions(optionalService.getActions());
                    toolServices.put(service.getId(), toolService);
                }
            }
        }
    }

	public String getSharedSecret() {
		return sharedSecret;
	}
	
	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}
    
    
}
