package net.danmercer.unsplashpicker.util;

import android.util.Pair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

/**
 * A utility for building query strings.
 *
 * @author Dan Mercer
 */
class QueryStringBuilder {
	private final List<Pair<String, Object>> pairs = new LinkedList<Pair<String, Object>>();
	private final String url;

	QueryStringBuilder() {
		this.url = null;
	}

	QueryStringBuilder(String url) {
		this.url = url;
	}

	QueryStringBuilder add(String key, Object value) {
		pairs.add(new Pair<>(key, value));
		return this;
	}

	String build() {
		StringBuilder sb = new StringBuilder();

		// Start with URL if given
		if (url != null) {
			sb.append(url).append((url.indexOf('?') != -1) ? '&' : '?');
		}

		// Concatenate pairs together
		for (Pair<String, Object> pair : pairs) {

			// Separate pairs with '&'
			if (sb.length() > 0) {
				sb.append("&");
			}

			sb.append(urlEncode(pair.first))
					.append('=')
					.append(urlEncode(pair.second.toString()));
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
