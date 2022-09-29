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

package com.liferay.layout.page.template.admin.web.internal.exporter;

import com.liferay.layout.page.template.exporter.PortletConfigurationExporter;
import com.liferay.layout.page.template.exporter.tracker.PortletConfigurationExporterServiceTracker;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Jürgen Kappler
 */
@Component(service = PortletConfigurationExporterTracker.class)
public class PortletConfigurationExporterTracker {

	public PortletConfigurationExporter getPortletConfigurationExporter(
		String portletName) {

		return _portletConfigurationExporterServiceTracker.
			getPortletConfigurationExporter(portletName);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_portletConfigurationExporterServiceTracker =
			new PortletConfigurationExporterServiceTracker(bundleContext);

		_portletConfigurationExporterServiceTracker.openSingleValueMap();
	}

	@Deactivate
	protected void deactivate() {
		_portletConfigurationExporterServiceTracker.close();
	}

	private PortletConfigurationExporterServiceTracker
		_portletConfigurationExporterServiceTracker;

}