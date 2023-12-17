package app;

import app.audio.Collections.Album;
import app.audio.Collections.Playlist;
import app.audio.Collections.Podcast;
import app.audio.Files.AudioFile;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.audio.LibraryEntry;

import app.user.User;
import fileio.input.*;
import lombok.Getter;

import java.util.*;

/**
 * The type Admin.
 */
public final class Admin {
    private static List<User> users = new ArrayList<>();
    private static List<Song> songs = new ArrayList<>();
    private static List<Podcast> podcasts = new ArrayList<>();
    @Getter
    private static List<Album> albums = new ArrayList<>();
    private static List<Playlist> playlists = new ArrayList<>();
    private static int timestamp = 0;
    private static final int LIMIT = 5;

    private Admin() {
    }

    /**
     * Sets users.
     *
     * @param userInputList the user input list
     */
    public static void setUsers(final List<UserInput> userInputList) {
        users = new ArrayList<>();
        for (UserInput userInput : userInputList) {
            users.add(new User(userInput.getUsername(), userInput.getAge(), userInput.getCity()));
        }
    }

    /**
     * Sets songs.
     *
     * @param songInputList the song input list
     */
    public static void setSongs(final List<SongInput> songInputList) {
		songs = new ArrayList<>();
		for (SongInput songInput : songInputList) {
			songs.add(new Song(songInput.getName(),
					songInput.getDuration(), songInput.getAlbum(),
					songInput.getTags(), songInput.getLyrics(), songInput.getGenre(),
					songInput.getReleaseYear(), songInput.getArtist()));
		}
	}

    /**
     * Sets podcasts.
     *
     * @param podcastInputList the podcast input list
     */
    public static void setPodcasts(final List<PodcastInput> podcastInputList) {
        podcasts = new ArrayList<>();
        for (PodcastInput podcastInput : podcastInputList) {
            List<Episode> episodes = new ArrayList<>();
            for (EpisodeInput episodeInput : podcastInput.getEpisodes()) {
                episodes.add(new Episode(episodeInput.getName(),
                        episodeInput.getDuration(),
                        episodeInput.getDescription()));
            }
            podcasts.add(new Podcast(podcastInput.getName(), podcastInput.getOwner(), episodes));
        }
    }

    /**
     * Gets songs.
     *
     * @return the songs
     */
    public static List<Song> getSongs() {
        return new ArrayList<>(songs);
    }

    /**
     * Gets podcasts.
     *
     * @return the podcasts
     */
    public static List<Podcast> getPodcasts() {
        return new ArrayList<>(podcasts);
    }

    /**
     * Gets playlists.
     *
     * @return the playlists
     */
    public static List<Playlist> getPlaylists() {
        List<Playlist> currPlaylists = new ArrayList<>();

        for (User user : users) {
			currPlaylists.addAll(user.getPlaylists());
        }

        return currPlaylists;
    }

    /**
     * Gets user.
     *
     * @param username the username
     * @return the user
     */
    public static User getUser(final String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

	/**
     * Gets podcast by name.
     *
     * @param name the name
     * @return the podcast
     */
    public static Podcast getPodcast(final String name) {
        for (Podcast podcast : podcasts) {
            if (podcast.getName().equals(name)) {
                return podcast;
            }
        }
        return null;
    }

    /**
     * Update timestamp.
     *
     * @param newTimestamp the new timestamp
     */
    public static void updateTimestamp(final int newTimestamp) {
        int elapsed = newTimestamp - timestamp;
        timestamp = newTimestamp;
        if (elapsed == 0) {
            return;
        }

        for (User user : users) {
			// Do the time simulation only if the user is online
            if (user.isActive()) {
				user.simulateTime(elapsed);
			}
        }
    }

    /**
     * Gets top 5 songs.
     *
     * @return the top 5 songs
     */
    public static List<String> getTop5Songs() {
        List<Song> sortedSongs = new ArrayList<>(songs);
		// Sort the songs by the number of likes
        sortedSongs.sort(Comparator.comparingInt(Song::getLikes).reversed());
        List<String> topSongs = new ArrayList<>();
        int count = 0;
        for (Song song : sortedSongs) {
            if (count >= LIMIT) {
                break;
            }
            topSongs.add(song.getName());
            count++;
        }
        return topSongs;
    }

    /**
     * Gets top 5 playlists.
     *
     * @return the top 5 playlists
     */
    public static List<String> getTop5Playlists() {
        List<Playlist> sortedPlaylists = new ArrayList<>(getPlaylists());
        sortedPlaylists.sort(Comparator.comparingInt(Playlist::getFollowers)
                .reversed()
                .thenComparing(Playlist::getTimestamp, Comparator.naturalOrder()));
        List<String> topPlaylists = new ArrayList<>();
        int count = 0;
        for (Playlist playlist : sortedPlaylists) {
            if (count >= LIMIT) {
                break;
            }
            topPlaylists.add(playlist.getName());
            count++;
        }
        return topPlaylists;
    }

    /**
     * Reset.
     */
    public static void reset() {
        users = new ArrayList<>();
        songs = new ArrayList<>();
        podcasts = new ArrayList<>();
        albums = new ArrayList<>();
        playlists = new ArrayList<>();
        timestamp = 0;
    }

    /**
     * Gets online users.
     *
     * @return the users
     */
    public static List<String> getOnlineUsers() {
        List<String> onlineUsers = new ArrayList<>();
        for (User user : users) {
            if (user.isActive() && user.getUserType().equals("user")) {
                onlineUsers.add(user.getUsername());
            }
        }
        return onlineUsers;
    }

    /**
     * Adds new users
     */
    public static void addUser(final String username, final String type,
							   final Integer age, final String city) {
        if (type == null) {
            users.add(new User(username, age, city));
        } else {
            users.add(new User.AddSuperUser(username, age, city, type));
        }
    }

    /**
     * Adds new album
     * @param name
     * @param artist
     * @param releaseYear
     * @param songInputs
     * @param description
     * @return message for output
     */
    public static String addAlbum(final String name, final String artist, final int releaseYear,
                                  final ArrayList<SongInput> songInputs, final String description) {
        ArrayList<Song> songsAdded = new ArrayList<>();

        for (SongInput songInput : songInputs) {
            Song song = new Song(songInput.getName(), songInput.getDuration(), songInput.getAlbum(),
                    songInput.getTags(), songInput.getLyrics(), songInput.getGenre(),
                    songInput.getReleaseYear(), songInput.getArtist());

            for (Song addedSong : songsAdded) {
                if (addedSong.getName().equals(song.getName())
						&& addedSong.getArtist().equals(song.getArtist())
						&& addedSong.getReleaseYear()
						.equals(song.getReleaseYear())) {

                    return artist + " has the same song at least twice in this album.";
                }
            }
            songsAdded.add(song);
        }
        Album newAlbum = new Album(name, artist, timestamp, releaseYear, songsAdded, description);
		albums.add(newAlbum);

        User albumOwner = getUser(artist);
        ArrayList<Album> userAlbums = albumOwner.getAlbums();
        userAlbums.add(newAlbum);
        albumOwner.setAlbums(userAlbums);

        songs.addAll(songsAdded);
        return artist + " has added new album successfully.";
    }

	/**
	 * Checks if an album exists for a specific user.
	 *
	 * @param album The name of the album to check.
	 * @param username The username of the user to check.
	 * @return true if the album exists for the user, false otherwise.
	 */
    public static boolean albumExistsForUser(final String album, final String username) {
        for (Album iterator : albums) {
            if (iterator.getName().equals(album) && iterator.getOwner().equals(username)) {
                return true;
            }
        }
        return false;
    }

	/**
	 * Checks if a podcast exists for a specific user.
	 *
	 * @param podcastName The name of the podcast to check.
	 * @param username The username of the user to check.
	 * @return true if the podcast exists for the user, false otherwise.
	 */
    public static boolean podcastExistsForUser(final String podcastName, final String username) {
        for (Podcast iterator : podcasts) {
            if (iterator.getName().equals(podcastName) && iterator.getOwner().equals(username)) {
                return true;
            }
        }
        return false;
    }

	/**
	 * Adds a new podcast to the platform.
	 *
	 * @param name The name of the podcast.
	 * @param username The username of the user who owns the podcast.
	 * @param episodes The list of episodes to be added to the podcast.
	 * @return A string message indicating the success or failure of the operation.
	 */
    public static String addPodcast(final String name, final String username,
									final ArrayList<EpisodeInput> episodes) {
        ArrayList<Episode> episodesAdded = new ArrayList<>();

        for (EpisodeInput episodeInput : episodes) {
            Episode episode = new Episode(episodeInput.getName(),
                    episodeInput.getDuration(),
                    episodeInput.getDescription());

            for (Episode addedEpisode : episodesAdded) {
                if (addedEpisode.getName().equals(episode.getName())) {
                    return username + "  has the same episode in this podcast.";
                }
            }
            episodesAdded.add(episode);
        }
        Podcast newPodcast = new Podcast(name, username, episodesAdded);
        podcasts.add(newPodcast);

        User podcastOwner = getUser(username);

		assert podcastOwner != null;
		ArrayList<Podcast> userPodcasts = podcastOwner.getPodcasts();
        userPodcasts.add(newPodcast);
        podcastOwner.setPodcasts(userPodcasts);

        return username + " has added new podcast successfully.";
    }

	/**
	 * Removes a podcast from the platform.
	 *
	 * @param name The name of the podcast to be removed.
	 * @param username The username of the user who owns the podcast.
	 * @return A string message indicating the success or failure of the operation.
	 */
    public static String removePodcast(final String name, final String username) {
        User userExists = Admin.getUser(username);
        Podcast podcastExists = Admin.getPodcast(name);

        if (podcastExists == null) {
			return username + " doesn't have a podcast with the given name.";
		}

        if (userExists == null) {
			return username + " does not exist.";
		}

        if (!userExists.getUserType().equals("host")) {
			return username + " is not a host.";
		}

        ArrayList<String> episodes = new ArrayList<>();

        for (Episode episode : podcastExists.getEpisodes()) {
			episodes.add(episode.getName());
		}

        for (User user : users) {
			if (user.isPlaying() != null
					&& episodes.contains(user.isPlaying().getName())) {
				return username + " can't delete this podcast.";
			}
		}

        ArrayList<Podcast> userPodcasts = userExists.getPodcasts();
        userPodcasts.remove(podcastExists);
        userExists.setPodcasts(userPodcasts);
        podcasts.remove(podcastExists);

        return username + " deleted the podcast successfully.";

    }

	/**
	 * Gets the top 5 albums based on the total number of likes of their songs.
	 *
	 * @return A list of the names of the top 5 albums.
	 */
    public static List<String> getTop5Albums() {
        List<Map.Entry<Album, Integer>> albumLikes = new ArrayList<>();

        for (Album album : albums) {
            int totalLikes = 0;
            for (Song song : album.getSongs()) {
                totalLikes += song.getLikes();
            }
            albumLikes.add(new AbstractMap.SimpleEntry<>(album, totalLikes));
        }

        albumLikes.sort(Map.Entry.<Album, Integer>comparingByValue(Comparator.reverseOrder())
                .thenComparing(e -> e.getKey().getName()));

        List<String> topAlbums = new ArrayList<>();

        for (int i = 0; i < Math.min(LIMIT, albumLikes.size()); i++) {
            topAlbums.add(albumLikes.get(i).getKey().getName());
        }

        return topAlbums;
    }

	/**
	 * Checks if a song by a specific artist is currently being played by any user.
	 *
	 * @param owner The username of the artist to check.
	 * @return true if a song by the artist is being played, false otherwise.
	 */
    public static boolean isBeingPlayed(final String owner) {
        for (User user : users) {
            if (user.isPlaying() != null && user.isPlaying().matchesArtist(owner)) {
                return true;
            }
        }
        return false;
    }

	/**
	 * Checks if any playlist owned by a specific user is
	 * currently being listened to by any user.
	 *
	 * @param userToBeDeleted The username of the user to check.
	 * @return true if a playlist owned by the user is being listened to, false otherwise.
	 */
    public static boolean isPlaylistBeingListenedByUser(final String userToBeDeleted) {
        User userToDelete = Admin.getUser(userToBeDeleted);
        List<String> allPlayingPlaylists = new ArrayList<>();

        for (User user : users) {
            if (user.isPlaying() != null && user.getCurrentPlaylist() != null) {
				allPlayingPlaylists.add(user.getCurrentPlaylist());
			}
        }

        ArrayList<String> userToDeletePlaylistsAsString = new ArrayList<>();

		assert userToDelete != null;
		for (Playlist playlist :userToDelete.getPlaylists()) {
			userToDeletePlaylistsAsString.add(playlist.getName());
		}

		return !Collections.disjoint(allPlayingPlaylists, userToDeletePlaylistsAsString);
	}

	/**
	 * Deletes a user from the platform.
	 *
	 * @param username The username of the user to be deleted.
	 * @return A string message indicating the success or failure of the operation.
	 */
    public static String  deleteUser(final String username) {
        User toBeDeleted = Admin.getUser(username);

		// Checking all the conditions for deleting a user
        if (toBeDeleted == null) {
			return "The username " + username + " doesn't exist.";
		}

		// Check if artist has a song being played
        if (isBeingPlayed(username)) {
			return username + " can't be deleted.";
		}

		// Check if host has a podcast being played
        if (podcastBeingPlayed(username)) {
			return username + " can't be deleted.";
		}

		// Check if somebody is viewing the artist's or host's profile
        if (isBeingWatched(username)) {
			return username + " can't be deleted.";
		}

		// Check if songs from any album owned by the artist are being played
		// from a user's podcast
        if (isPlaylistBeingListenedByUser(username)) {
			return username + " can't be deleted.";
		}

		// Removing the user from the database
        users.remove(toBeDeleted);

		// Removing the songs/podcasts, albums of the deleted user
        if (toBeDeleted.getUserType().equals("artist")) {
            songs.removeIf(song -> song.getArtist().equals(username));
            albums.removeIf(album -> album.getOwner().equals(username));
            Admin.removeFromLiked(username);
        }
        if (toBeDeleted.getUserType().equals("host")) {
            podcasts.removeIf(podcast -> podcast.getOwner().equals(username));
        }

		// Decrease followers of all playlists followed by the user
        if (toBeDeleted.getUserType().equals("user")) {
            for (Playlist playlist : toBeDeleted.getFollowedPlaylists()) {
                playlist.decreaseFollowers();
            }

            for (User user : users) {
				// Remove songs from the liked songs list if the deleted
				// user is an artist
                ArrayList<Song> likedSongs = user.getLikedSongs();
                if (likedSongs != null) {
					likedSongs.removeIf(song -> song.getArtist().equals(username));
					user.setLikedSongs(likedSongs);
				}

                ArrayList<Playlist> followedPlaylists = user.getFollowedPlaylists();

				// Remove songs from the followed playlists list if the deleted
				// user is an artist
                if (followedPlaylists != null) {
					followedPlaylists.removeIf(playlist -> playlist.getOwner().equals(username));
				}

                user.setFollowedPlaylists(followedPlaylists);
            }
			// Remove user's playlists from the database
            playlists.removeIf(playlist -> playlist.getOwner().equals(username));
		}

        return username + " was successfully deleted.";
    }

	/**
	 * Checks if any podcast owned by a specific user is currently being played by any user.
	 *
	 * @param username The username of the user to check.
	 * @return true if a podcast owned by the user is being played, false otherwise.
	 */
    private static boolean podcastBeingPlayed(final String username) {
		// Check if username has a podcast being played in any user's player
        User user = getUser(username);

		assert user != null;
		if (!user.getUserType().equals("host")) {
			return false;
		}

        ArrayList<Podcast> userPodcasts = user.getPodcasts();
        ArrayList<String> episodes = new ArrayList<>();

        for (Podcast podcast : userPodcasts) {
			for (Episode episode : podcast.getEpisodes()) {
				episodes.add(episode.getName());
			}
		}

        for (User allUsers : users) {
			if (allUsers.isPlaying() != null && episodes.contains(allUsers.isPlaying().getName())) {
				return true;
			}
		}

        return false;
    }

	/**
	 * Removes all songs of a specific artist from the liked songs list of all users.
	 *
	 * @param username The username of the artist whose songs are to be removed from the liked songs list.
	 */
    private static void removeFromLiked(final String username) {
		// Removing all songs of the artist from the liked songs list of all users
        for (User user : users) {
            ArrayList<Song> likedSongs = user.getLikedSongs();

            if (likedSongs != null) {
				likedSongs.removeIf(song -> song.getArtist().equals(username));
			}
            user.setLikedSongs(likedSongs);
        }
    }

	/**
	 * Checks if any user is currently viewing the profile of a specific artist or host.
	 *
	 * @param username The username of the artist or host to check.
	 * @return true if the artist's or host's profile is being viewed by any user, false otherwise.
	 */
    private static boolean isBeingWatched(final String username) {
		// Checking if the artist's or host's profile is being viewed by any user
        for (User user : users) {
            if (User.getCurrentArtist() != null && User.getCurrentArtist().equals(username)
					&& (user.getCurrentPage().equals("Artist")
					|| user.getCurrentPage().equals("Host"))) {
				return true;
			}
        }
        return false;
    }

	/**
	 * Retrieves all usernames from the platform.
	 *
	 * @return An ArrayList of all usernames.
	 */
    public static ArrayList<String> getAllUsernames() {
        ArrayList<User> allUsers = getAllUsers();
        ArrayList<String> usernames = new ArrayList<>();

        for (User user : allUsers) {
            usernames.add(user.getUsername());
        }

        return usernames;
    }

	/**
	 * Retrieves all users from the platform.
	 *
	 * @return An ArrayList of all users.
	 */
    public static ArrayList<User> getAllUsers() {
		// Adding all the users to the list in order of user type
        ArrayList<User> allUsers = new ArrayList<>();

        for (User user : users) {
			if (user.getUserType().equals("user")) {
				allUsers.add(user);
			}
		}

        for (User user : users) {
			if (user.getUserType().equals("artist")) {
				allUsers.add(user);
			}
		}

        for (User user : users) {
			if (user.getUserType().equals("host")) {
				allUsers.add(user);
			}
		}
        return allUsers;
    }

	/**
	 * Retrieves all hosts from the platform.
	 *
	 * @return A list of LibraryEntry objects representing all hosts.
	 */
    public static List<LibraryEntry> getHosts() {
        List<LibraryEntry> hosts = new ArrayList<>();
        for (User user : users) {
            if (user.getUserType().equals("host")) {
                hosts.add(new LibraryEntry.ArtistEntry(user.getUsername()));
            }
        }
        return hosts;
    }

	/**
	 * Retrieves all artists from the platform.
	 *
	 * @return A list of LibraryEntry objects representing all artists.
	 */
    public static List<LibraryEntry> getArtists() {
        List<LibraryEntry> artists = new ArrayList<>();
        for (User user : users) {
            if (user.getUserType().equals("artist")) {
                artists.add(new LibraryEntry.ArtistEntry(user.getUsername()));
            }
        }
        return artists;
    }

	/**
	 * Checks if a user is an artist or a host.
	 *
	 * @param username The username of the user to check.
	 * @return 1 if the user is an artist, 2 if the user is a host, 0 otherwise.
	 */
    public static short isArtistOrHost(final String username) {
		// Checking user type
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getUserType().equals("artist")) {
				return 1;
			} else if (user.getUsername().equals(username) && user.getUserType().equals("host")) {
				return 2;
			}
        }
        return 0;
    }

	/**
	 * Checks if an album can be deleted from the platform.
	 *
	 * @param album The album to check.
	 * @return true if the album can be deleted, false otherwise.
	 */
    public static boolean canDeleteAlbum(final Album album) {

        for (User user : getAllUsers()) {
            // Check if the user is currently playing a song from the album
            AudioFile playingAudioFile = user.isPlaying();
            if (playingAudioFile != null) {
                String playingSong = playingAudioFile.getName();

                List<String> albumToString = new ArrayList<>();
                for (Song song : album.getSongs()) {
                    albumToString.add(song.getName());
                }

                if (albumToString.contains(playingSong)) {
                    return false;
                }
            }
            // Check if any song from the album is in any of the user's playlists
            for (Playlist playlist : user.getPlaylists()) {
                for (Song song : playlist.getSongs()) {
                    if (album.getSongs().contains(song)) {
                        return false;
                    }
                }
            }

			// Check if any song from the album is in any of the user's loaded playlists
            Playlist currPlaylist = null;
            if (user.getPlayer().getType() != null
					&& user.getPlayer().getType().equals("playlist")) {
                String currPlaylistName = user.getCurrentPlaylist();

                for (Playlist playlist : playlists) {
                    if (playlist.getName().equals(currPlaylistName)) {
                        currPlaylist = playlist;
                        System.out.println(playlist.getName());
                        break;
                    }
                }
                if (currPlaylist != null) {
                    List<Song> playlistSongs = currPlaylist.getSongs();
                    List<Song> albumSongs = album.getSongs();
                    for (Song playlistSong : playlistSongs) {
                        if (albumSongs.contains(playlistSong)) {
                            return false;
                        }
                    }
                }
            }


        }

        // If no match is found after checking all users, it's safe to delete the album
        return true;
	}

	/**
	 * Clears an album from the platform.
	 *
	 * @param albumToRemove The album to be removed.
	 */
    public static void clearAlbum(final Album albumToRemove) {
		// Removing the songs from the album from the songs list
        for (Song song : albumToRemove.getSongs()) {
            songs.remove(song);
        }

		// Removing the album from all user's liked songs list
        for (User user : users) {
            user.getLikedSongs().removeIf(song -> albumToRemove.getSongs().contains(song));
        }

		// Removing the album from the database
        albums.remove(albumToRemove);
    }

	/**
	 * Retrieves the top 5 artists based on the total number of likes of their songs.
	 *
	 * @return A list of the names of the top 5 artists.
	 */
    public static List<String> getTop5Artists() {
        List<Map.Entry<User, Integer>> artistLikes = new ArrayList<>();

        for (User user : users) {
            if (user.getUserType().equals("artist")) {
                int totalLikes = 0;
                for (Album album : user.getAlbums()) {
                    for (Song song : album.getSongs()) {
                        totalLikes += song.getLikes();
                    }
                }
				artistLikes.add(new AbstractMap.SimpleEntry<>(user, totalLikes));
            }
        }

		// Sorting liked albums by total sum of likes
		// of all songs on the album
        artistLikes.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        List<String> topArtists = new ArrayList<>();

		// Adding the top 5 artists to the list
        for (int i = 0; i < Math.min(LIMIT, artistLikes.size()); i++) {
            topArtists.add(artistLikes.get(i).getKey().getUsername());
        }

		return topArtists;
    }
}
