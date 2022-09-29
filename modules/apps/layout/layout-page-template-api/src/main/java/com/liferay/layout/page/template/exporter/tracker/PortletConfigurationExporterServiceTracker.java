/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.layout.page.template.exporter.tracker;

import com.liferay.layout.page.template.exporter.PortletConfigurationExporter;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import org.osgi.framework.BundleContext;

/**
 * @author Janis Zhang
 */
public class PortletConfigurationExporterServiceTracker {

	public void close() {
		_portletConfigurationExporterServiceTrackerMap.close();
	}

	public void openSingleValueMap() {
		_portletConfigurationExporterServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				_bundleContext, PortletConfigurationExporter.class, null,
				ServiceReferenceMapperFactory.create(
					_bundleContext,
					(portletConfigurationExporter, emitter) -> emitter.emit(
						portletConfigurationExporter.getPortletName())));
	}

	public PortletConfigurationExporterServiceTracker(
		BundleContext bundleContext) {

		_bundleContext = bundleContext;
	}

	public PortletConfigurationExporter getPortletConfigurationExporter(
		String portletName) {

		return _portletConfigurationExporterServiceTrackerMap.getService(
			portletName);
	}

	private volatile ServiceTrackerMap
		<String, PortletConfigurationExporter>
			_portletConfigurationExporterServiceTrackerMap;

	private final BundleContext _bundleContext;

}