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

package com.liferay.wiki.web.internal.importer;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.wiki.importer.WikiImporter;

import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Iván Zaera
 */
@Component(immediate = true, service = WikiImporterRegistry.class)
public class WikiImporterRegistry {

	public Collection<String> getImporters() {
		return _serviceTrackerMap.keySet();
	}

	public String getProperty(String importer, String key) {
		return String.valueOf(_serviceTrackerMap.getService(importer));
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, WikiImporter.class, null,
			(serviceReference, emitter) -> emitter.emit(
				(String)serviceReference.getProperty("importer")),
			new ServiceTrackerCustomizer<WikiImporter, WikiImporter>() {

				@Override
				public WikiImporter addingService(
					ServiceReference<WikiImporter> serviceReference) {

					return bundleContext.getService(serviceReference);
				}

				@Override
				public void modifiedService(
					ServiceReference<WikiImporter> serviceReference,
					WikiImporter wikiImporter) {

					removedService(serviceReference, wikiImporter);

					addingService(serviceReference);
				}

				@Override
				public void removedService(
					ServiceReference<WikiImporter> serviceReference,
					WikiImporter wikiImporter) {

					bundleContext.ungetService(serviceReference);
				}

			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private ServiceTrackerMap<String, WikiImporter> _serviceTrackerMap;

}