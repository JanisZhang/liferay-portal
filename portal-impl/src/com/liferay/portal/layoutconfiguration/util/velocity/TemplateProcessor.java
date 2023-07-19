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

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletContainerUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.servlet.BufferCacheServletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.layoutconfiguration.util.PortletRenderer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Ivica Cardic
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 * @author Oliver Teichmann
 */
public class TemplateProcessor implements ColumnProcessor {

	public TemplateProcessor(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String portletId) {

		_httpServletRequest = httpServletRequest;
		_httpServletResponse = httpServletResponse;

		if (Validator.isNotNull(portletId)) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			_portlet = PortletLocalServiceUtil.getPortletById(
				themeDisplay.getCompanyId(), portletId);
		}
		else {
			_portlet = null;
		}

		_portletAjaxRender = GetterUtil.getBoolean(
			httpServletRequest.getAttribute(WebKeys.PORTLET_AJAX_RENDER));

		_portletRenderers = new TreeMap<>(_renderWeightComparator);
	}

	public Map<Integer, List<PortletRenderer>> getPortletRenderers() {
		return _portletRenderers;
	}

	@Override
	public String processColumn(String columnId) throws Exception {
		return processColumn(columnId, StringPool.BLANK);
	}

	@Override
	public String processColumn(String columnId, String classNames)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		LayoutTypePortlet layoutTypePortlet =
			themeDisplay.getLayoutTypePortlet();

		return _processColumn(
			columnId, classNames, layoutTypePortlet,
			layoutTypePortlet.getAllPortlets(columnId));
	}

	@Override
	public String processDynamicColumn(String columnId, String classNames)
		throws Exception {

		List<Portlet> portlets = new ArrayList<>();

		String portletId = ParamUtil.getString(_httpServletRequest, "p_p_id");

		if (Validator.isNotNull(portletId)) {
			Portlet portlet = PortletLocalServiceUtil.getPortletById(portletId);

			if (portlet != null) {
				portlets.add(portlet);
			}
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _processColumn(
			columnId, classNames, themeDisplay.getLayoutTypePortlet(),
			portlets);
	}

	@Override
	public String processMax() throws Exception {
		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(_httpServletResponse);

		PortletContainerUtil.renderHeaders(
			_httpServletRequest, bufferCacheServletResponse, _portlet);

		PortletContainerUtil.render(
			_httpServletRequest, bufferCacheServletResponse, _portlet);

		return bufferCacheServletResponse.getString();
	}

	private String _processColumn(
			String columnId, String classNames,
			LayoutTypePortlet layoutTypePortlet, List<Portlet> portlets)
		throws Exception {

		StringBundler sb = new StringBundler((portlets.size() * 3) + 11);

		sb.append("<div class=\"");

		if (layoutTypePortlet.isColumnCustomizable(columnId)) {
			sb.append("customizable ");
		}

		if (portlets.isEmpty()) {
			sb.append("empty ");
		}

		sb.append("portlet-dropzone ");

		if (layoutTypePortlet.isColumnDisabled(columnId) &&
			layoutTypePortlet.isCustomizable()) {

			sb.append("portlet-dropzone-disabled ");
		}

		if (Validator.isNotNull(classNames)) {
			sb.append(classNames);
		}

		sb.append("\" id=\"layout-column_");
		sb.append(columnId);
		sb.append("\">");

		for (int i = 0; i < portlets.size(); i++) {
			Portlet portlet = portlets.get(i);

			Integer columnCount = Integer.valueOf(portlets.size());
			Integer columnPos = Integer.valueOf(i);

			sb.append(
				_renderPortlet(portlet, columnId, columnCount, columnPos));
		}

		sb.append("</div>");

		return sb.toString();
	}

	private StringBundler _renderPortlet(
			Portlet portlet, String columnId, Integer columnCount,
			Integer columnPos)
		throws Exception {

		PortletRenderer portletRenderer = new PortletRenderer(
			portlet, columnId, columnCount, columnPos);

		if (_portletAjaxRender && (portlet.getRenderWeight() < 1)) {
			return portletRenderer.renderAjax(
				_httpServletRequest, _httpServletResponse);
		}

		Integer renderWeight = portlet.getRenderWeight();

		List<PortletRenderer> portletRenderers = _portletRenderers.get(
			renderWeight);

		if (portletRenderers == null) {
			portletRenderers = new ArrayList<>();

			_portletRenderers.put(renderWeight, portletRenderers);
		}

		portletRenderers.add(portletRenderer);

		StringBundler sb = new StringBundler(3);

		sb.append("[$TEMPLATE_PORTLET_");
		sb.append(portlet.getPortletId());
		sb.append("$]");

		return sb;
	}

	private static final RenderWeightComparator _renderWeightComparator =
		new RenderWeightComparator();

	private final HttpServletRequest _httpServletRequest;
	private final HttpServletResponse _httpServletResponse;
	private final Portlet _portlet;
	private final boolean _portletAjaxRender;
	private final Map<Integer, List<PortletRenderer>> _portletRenderers;

	private static class RenderWeightComparator implements Comparator<Integer> {

		@Override
		public int compare(Integer renderWeight1, Integer renderWeight2) {
			return renderWeight2.intValue() - renderWeight1.intValue();
		}

	}

}