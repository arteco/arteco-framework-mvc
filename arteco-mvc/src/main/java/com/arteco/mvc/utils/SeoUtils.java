package com.arteco.mvc.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * Created by rarnau on 3/12/16.
 * Arteco Consulting Sl.
 * mailto: info@arteco-consulting.com
 */
public class SeoUtils {

	public static final String HTTP_RESP_HEADER_LAST_MODIFIED = "Last-Modified";
	public static final String HTTP_RESP_HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
	private static final String DATE_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";

	public static String toHttpHeader(Date time) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormat.format(time);
	}

	public static Date fromHttpHeader(String time) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		try {
			return dateFormat.parse(time);
		} catch (ParseException e) {
			return null;
		}
	}

	public static boolean checksetLastModified(HttpServletRequest request, HttpServletResponse response, Date lastMod) {
		if (lastMod != null) {
			response.setHeader(
					SeoUtils.HTTP_RESP_HEADER_LAST_MODIFIED,
					SeoUtils.toHttpHeader(lastMod));

			String txtSince = request.getHeader(HTTP_RESP_HEADER_IF_MODIFIED_SINCE);
			if (txtSince != null) {
				Date dateSince = fromHttpHeader(txtSince);
				if (dateSince != null) {
					boolean serve = dateSince.before(lastMod);
					if (!serve){
						response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
					}
					return serve;
				}
			}
		}
		return true;
	}

	public String cleanHtml(String text){
		if (text!=null){
			String res = Jsoup.clean(text, Whitelist.none());
			res = StringUtils.replace(res, "&nbsp;", " ");
			return res.trim();
		}
		return null;
	}

	public String absoluteUrl(HttpServletRequest request){
		if (request!=null){
			return request.getRequestURL().toString();
		}
		return "/";
	}

	public String keywords(String longText) {
		if (longText != null) {
			String[] txt = StringUtils.split(longText);
			Set<String> set = new HashSet<String>();
			for (String t : txt) {
				if (t.length() > 4) {
					set.add(t);
				}
			}
			List<String> list = new ArrayList<String>(set);
			Collections.sort(list);
			return StringUtils.join(list, ", ");
		}
		return null;
	}

	public String url(String url) {
		if (url != null && !url.startsWith("http")) {
			return "http://" + url;
		}
		return url;
	}

	public static String getUriWithouContextPath(HttpServletRequest req) {
		String uri = req.getRequestURI();
		if (req.getContextPath().length()>1){
			uri = StringUtils.remove(uri, req.getContextPath());
		}
		return removeEndSlash(uri);
	}

	public static String removeEndSlash(String path) {
		if (path.length() > 0 && path.endsWith("/")) {
			path = StringUtils.removeEnd(path, "/");
		}
		return path;
	}

}
