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

package com.liferay.portal.layoutconfiguration.util.velocity;

import com.liferay.petra.string.StringPool;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivica Cardic
 * @author Brian Wing Shun Chan
 */
public class InitColumnProcessor implements ColumnProcessor {

	public List<String> getColumns() {
		return _columns;
	}

	@Override
	public String processColumn(String columnId) {
		_columns.add(columnId);

		return StringPool.BLANK;
	}

	@Override
	public String processColumn(String columnId, String classNames) {
		_columns.add(columnId);

		return StringPool.BLANK;
	}

	@Override
	public String processDynamicColumn(String columnId, String classNames)
		throws Exception {

		_columns.add(columnId);

		return StringPool.BLANK;
	}

	@Override
	public String processMax() {
		return StringPool.BLANK;
	}

	private final List<String> _columns = new ArrayList<>();

}