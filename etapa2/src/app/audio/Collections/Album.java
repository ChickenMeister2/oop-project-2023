package app.audio.Collections;

import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import lombok.Getter;
import java.util.ArrayList;

@Getter
public final class Album extends AudioCollection {
	private ArrayList<Song> songs;
	private Integer likeCount;
	private Integer timestamp;
	private Integer releaseYear;
	private String description;

	/**
	 * Instantiates a new Album.
	 *
	 * @param name  the name
	 * @param owner the owner
	 */

	/**
	 * Instantiates a new Playlist.
	 *
	 * @param name      the name
	 * @param owner     the owner
	 * @param timestamp the timestamp
	 */
	public Album(final String name, final String owner, final int timestamp,
				 final int releaseYear, final ArrayList<Song> songs,
				 final String description) {
		super(name, owner);
		this.songs = songs;
		this.likeCount = 0;
		this.timestamp = timestamp;
		this.releaseYear = releaseYear;
		this.description = description;
	}


	/**
	 * Contains song boolean.
	 *
	 * @param song the song
	 * @return the boolean
	 */
	public boolean containsSong(final Song song) {
		return songs.contains(song);
	}

	/**
	 * Add song.
	 *
	 * @param song the song
	 */
	public void addSong(final Song song) {
		songs.add(song);
	}

	/**
	 * Remove song.
	 *
	 * @param song the song
	 */
	public void removeSong(final Song song) {
		songs.remove(song);
	}

	/**
	 * Remove song.
	 *
	 * @param index the index
	 */
	public void removeSong(final int index) {
		songs.remove(index);
	}

	@Override
	public int getNumberOfTracks() {
		return songs.size();
	}

	@Override
	public AudioFile getTrackByIndex(final int index) {
		return songs.get(index);
	}
}
