package app.user;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Merch {
	@Setter
	private String name;
	@Setter
	private String description;
	@Setter
	private Integer price;

	public Merch(final String name, final String description, final Integer price) {
		this.name = name;
		this.description = description;
		this.price = price;
	}


}
