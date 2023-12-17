package app.user;

import lombok.Getter;

public class Announcement {
	@Getter
	private String name;
	@Getter
	private String description;

	public Announcement(final String name, final String description) {
		this.name = name;
		this.description = description;
	}

}
