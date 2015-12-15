package net.sf.openrocket.file.openrocket.savers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.sf.openrocket.rocketcomponent.DeploymentConfiguration;
import net.sf.openrocket.rocketcomponent.FlightConfiguration;
import net.sf.openrocket.rocketcomponent.FlightConfigurationID;
import net.sf.openrocket.rocketcomponent.FlightConfigurableParameterSet;
import net.sf.openrocket.rocketcomponent.RecoveryDevice;
import net.sf.openrocket.rocketcomponent.Rocket;


public class RecoveryDeviceSaver extends MassObjectSaver {
	
	@Override
	protected void addParams(net.sf.openrocket.rocketcomponent.RocketComponent c, List<String> elements) {
		super.addParams(c, elements);
		
		RecoveryDevice dev = (RecoveryDevice) c;
		
		if (dev.isCDAutomatic())
			elements.add("<cd>auto</cd>");
		else
			elements.add("<cd>" + dev.getCD() + "</cd>");
		elements.add(materialParam(dev.getMaterial()));
		
		// NOTE:  Default config must be BEFORE overridden config for proper backward compatibility later on
		DeploymentConfiguration defaultConfig = dev.getDeploymentConfigurations().getDefault();
		elements.addAll(deploymentConfiguration(defaultConfig, false));
		
		Rocket rocket = c.getRocket();
		
		// DEBUG
		//System.err.println("printing deployment info for: "+dev.getName());
		//dev.getDeploymentConfigurations().printDebug();
		// DEBUG 
		
		FlightConfigurableParameterSet<FlightConfiguration> configList = rocket.getConfigSet(); 
		for (FlightConfigurationID fcid : configList.getSortedConfigurationIDs()) {
			//System.err.println("checking FlightConfiguration:"+fcid.getShortKey()+ " save?");
			
			if (dev.getDeploymentConfigurations().isDefault(fcid)) {
				//System.err.println("    >> skipping: fcid="+fcid.getShortKey());
				continue;
			}else if( dev.getDeploymentConfigurations().containsKey(fcid)){
				// only print configurations which override the default.
				//System.err.println("    >> printing data.");
				DeploymentConfiguration deployConfig = dev.getDeploymentConfigurations().get(fcid);
				elements.add("<deploymentconfiguration configid=\"" + fcid.key + "\">");
				elements.addAll(deploymentConfiguration(deployConfig, true));
				elements.add("</deploymentconfiguration>");
			}
		}
	}
	
	private List<String> deploymentConfiguration(DeploymentConfiguration config, boolean indent) {
		List<String> elements = new ArrayList<String>(3);
		elements.add((indent ? "  " : "") + "<deployevent>" + config.getDeployEvent().name().toLowerCase(Locale.ENGLISH).replace("_", "") + "</deployevent>");
		elements.add((indent ? "  " : "") + "<deployaltitude>" + config.getDeployAltitude() + "</deployaltitude>");
		elements.add((indent ? "  " : "") + "<deploydelay>" + config.getDeployDelay() + "</deploydelay>");
		return elements;
		
	}
}
