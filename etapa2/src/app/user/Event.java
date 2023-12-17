package app.user;

import lombok.Getter;

public class Event {
	@Getter
	private String name;
	@Getter
	private String description;
	@Getter
	private String date;

	public Event(final String name, final String description, final String date) {
		this.name = name;
		this.description = description;
		this.date = date;
	}

}
