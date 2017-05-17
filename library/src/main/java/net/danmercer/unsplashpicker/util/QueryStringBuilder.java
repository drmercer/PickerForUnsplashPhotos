package net.danmercer.unsplashpicker.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility for building query strings.
 *
 * @author Dan Mercer
 */
class QueryStringBuilder {
	private final Map<String, Object> pairs = new HashMap<>();
	private final String url;

	QueryStringBuilder() {
		this.url = null;
	}

	QueryStringBuilder(String url) {
		this.url = url;
	}

	QueryStringBuilder add(String key, Object value) {
		pairs.put(key, value);
		return this;
	}

	String build() {
		StringBuilder sb = new StringBuilder();

		// Start with URL if given
		if (url != null) {
			sb.append(url).append("?");
		}

		// Concatenate pairs together
		for (Map.Entry<String, Object> entry : pairs.entrySet()) {

			// Separate pairs with '&'
			if (sb.length() > 0) {
				sb.append("&");
			}

			sb.append(urlEncode(entry.getKey()))
					.append('=')
					.append(urlEncode(entry.getValue().toString()));
		}

		return sb.toString();
	}

	//=================================================
	//        Private static functions:

	private static String urlEncode(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedOperationException("Error encoding string: " + str, e);
		}
	}
}
