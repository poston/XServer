package org.xserver.component.core.interfaces;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The manager class manage all valid interface
 * @author  postonzhang
 * @since 2016-04-08.
 */
public class InterfaceResolverManager {
	private static final Logger log = LoggerFactory.getLogger(InterfaceResolverManager.class);
	private List<InterfaceResolver> interfaceResolvers;

	private Map<String, InterfaceMeta> interfaceMetaInfos = new HashMap<String, InterfaceMeta>();

	public List<InterfaceResolver> getInterfaceResolvers() {
		return interfaceResolvers;
	}

	public void setInterfaceResolvers(List<InterfaceResolver> interfaceResolvers) {
		this.interfaceResolvers = interfaceResolvers;
	}

	public Map<String, InterfaceMeta> getInterfaceMetaResolver() {
		for (InterfaceResolver resolver : interfaceResolvers) {
			log.info("interfaceResolver[{}] resolve paths", resolver.getClass().getSimpleName());
			interfaceMetaInfos.putAll(resolver.interfaceContextResolver());
		}

		return interfaceMetaInfos;
	}
}
