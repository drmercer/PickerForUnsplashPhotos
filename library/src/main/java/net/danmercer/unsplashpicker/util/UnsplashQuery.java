package net.danmercer.unsplashpicker.util;

/**
 * @author Dan Mercer
 */
public class UnsplashQuery {
	private final String appID;
	private int pageNumber = 1;
	private int perPage = 0;

	public UnsplashQuery(String appID) {
		this.appID = appID;
	}

	public UnsplashQuery nextPage() {
		pageNumber++;
		return this;
	}

	public String toURL() {
		return new QueryStringBuilder("https://api.unsplash.com/photos")
				.add("client_id", appID)
				.add("page", pageNumber)
				.build();
	}
}
