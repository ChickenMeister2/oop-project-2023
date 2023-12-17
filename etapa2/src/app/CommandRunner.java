package app;

import app.audio.Collections.Album;
import app.audio.Collections.PlaylistOutput;
import app.audio.Collections.Podcast;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.player.PlayerStats;
import app.searchBar.Filters;
import app.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The type Command runner.
 */
public class CommandRunner {
    /**
     * The Object mapper.
     */


    private static CommandRunner instance; // instance for singleton

    public static CommandRunner getInstance() {
        if (instance == null) {
            instance = new CommandRunner();
        }
        return instance;
    }

    private static ObjectMapper objectMapper = new ObjectMapper();

    private CommandRunner() {
    }

    /**
     * Search object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public  ObjectNode search(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        Filters filters = new Filters(commandInput.getFilters());
        ArrayList<String> results = new ArrayList<>();
        String type = commandInput.getType();
        String message = null;

		assert user != null;
		if (user.isActive()) {
            results = user.search(filters, type);
            message = "Search returned " + results.size() + " results";
        } else {
			message =  user.getUsername() + " is offline.";
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        objectNode.put("results", objectMapper.valueToTree(results));

        return objectNode;
    }

    /**
     * Select object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public  ObjectNode select(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());

        String message = user.select(commandInput.getItemNumber());

        String userToSearch = user.selectUser(commandInput.getItemNumber());

        StringBuilder newMessage = new StringBuilder(message);

        if (Admin.isArtistOrHost(userToSearch) == 1) {
            newMessage.deleteCharAt(newMessage.length() - 1);
            newMessage.append("'s page.");
            User.setCurrentArtist(userToSearch);
            user.setCurrentPage("Artist");
        } else if (Admin.isArtistOrHost(userToSearch) == 2) {
            newMessage.deleteCharAt(newMessage.length() - 1);
            newMessage.append("'s page.");
            User.setCurrentArtist(userToSearch);
            user.setCurrentPage("Host");
        }


        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        if (Admin.isArtistOrHost(userToSearch) == 0) {
            objectNode.put("message", message);
        } else {
            objectNode.put("message", newMessage.toString());
        }

        return objectNode;
    }

    /**
     * Load object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public ObjectNode load(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.load();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Play pause object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public ObjectNode playPause(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.playPause();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Repeat object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public ObjectNode repeat(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.repeat();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Shuffle object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public ObjectNode shuffle(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        Integer seed = commandInput.getSeed();
        String message = user.shuffle(seed);

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Forward object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public ObjectNode forward(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.forward();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Backward object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public ObjectNode backward(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.backward();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Like object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public ObjectNode like(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.like();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Next object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public ObjectNode next(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.next();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Prev object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public ObjectNode prev(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.prev();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Create playlist object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public ObjectNode createPlaylist(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.createPlaylist(commandInput.getPlaylistName(),
                                             commandInput.getTimestamp());

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Add remove in playlist object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public ObjectNode addRemoveInPlaylist(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.addRemoveInPlaylist(commandInput.getPlaylistId());

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Switch visibility object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public ObjectNode switchVisibility(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.switchPlaylistVisibility(commandInput.getPlaylistId());

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Show playlists object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public ObjectNode showPlaylists(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ArrayList<PlaylistOutput> playlists = user.showPlaylists();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(playlists));

        return objectNode;
    }

    /**
     * Follow object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public ObjectNode follow(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.follow();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Status object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public ObjectNode status(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        PlayerStats stats = user.getPlayerStats();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("stats", objectMapper.valueToTree(stats));

        return objectNode;
    }

    /**
     * Show liked songs object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public ObjectNode showLikedSongs(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ArrayList<String> songs = user.showPreferredSongs();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(songs));

        return objectNode;
    }

    /**
     * Gets preferred genre.
     *
     * @param commandInput the command input
     * @return the preferred genre
     */
    public ObjectNode getPreferredGenre(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String preferredGenre = user.getPreferredGenre();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(preferredGenre));

        return objectNode;
    }

    /**
     * Gets top 5 songs.
     *
     * @param commandInput the command input
     * @return the top 5 songs
     */
    public ObjectNode getTop5Songs(final CommandInput commandInput) {
        List<String> songs = Admin.getTop5Songs();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(songs));

        return objectNode;
    }

    /**
     * Gets top 5 playlists.
     *
     * @param commandInput the command input
     * @return the top 5 playlists
     */
    public ObjectNode getTop5Playlists(final CommandInput commandInput) {
        List<String> playlists = Admin.getTop5Playlists();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(playlists));

        return objectNode;
    }

    /**
     * Switches the connection status of a user.
     *
     * @param commandInput the command input containing the username of the user whose connection
     *                    status is to be switched.
     * @return the objectNode
     */
    public ObjectNode switchConnectionStatus(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = null;

        if (user == null) {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        } else if (!user.getUserType().equals("user")) {
            message = commandInput.getUsername() + " is not a normal user.";
        } else {
            message = user.switchUserActive(user);
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Retrieves all online users from the platform.
     *
     * @param commandInput the command input.
     * @return the objectNode
     */
    public ObjectNode getOnlineUsers(final CommandInput commandInput) {
        List<String> onlineUsers = Admin.getOnlineUsers();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(onlineUsers));
        return objectNode;
    }

    /**
     * Adds a new user to the platform.
     *
     * @param commandInput the command input containing the username, type, age, and city of the
     *                     user to be added.
     * @return the objectNode
     */
    public ObjectNode addUser(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = null;
        if (user != null) {
            message = "The username " + commandInput.getUsername() + " is already taken.";
        } else {
            Admin.addUser(commandInput.getUsername(), commandInput.getType(),
                    commandInput.getAge(), commandInput.getCity());
            message = "The username " + commandInput.getUsername() + " has been added "
                    + "successfully.";
        }
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Adds a new album to the platform.
     *
     * @param commandInput the command input containing the album name, the username of the artist
     *                    who owns the album, the release year of the album, the list of songs on
     *                    the album, and the description of the album.
     * @return the objectNode
     */
    public ObjectNode addAlbum(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = null;

        if (user == null) {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        } else if (user.getUserType() != null && !user.getUserType().equals("artist")) {
            message = commandInput.getUsername() + " is not an artist.";
        } else if (user.getUserType() != null) {
            if (Admin.albumExistsForUser(commandInput.getName(), commandInput.getUsername())) {
                message = commandInput.getUsername() + " has another album with the same name.";
            } else {
                message = Admin.addAlbum(commandInput.getName(), commandInput.getUsername(),
                        commandInput.getReleaseYear(), commandInput.getSongs(),
                        commandInput.getDescription());
            }
        }
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
	}

    /**
     * Retrieves all albums owned by a specific user from the platform.
     *
     * @param commandInput The command input containing the username of the user whose albums are to be retrieved.
     * @return the objectNode
     */
    public ObjectNode showAlbums(final CommandInput commandInput) {
        List<Album> albums = Admin.getAlbums();
        ObjectNode objectNode = objectMapper.createObjectNode();
        ArrayNode resultNode = objectMapper.createArrayNode();

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        for (Album album : albums) {
            if (album.getOwner().equalsIgnoreCase(commandInput.getUsername())) {
                ObjectNode albumNode = objectMapper.createObjectNode();
                List<String> songNames = new ArrayList<>();

                albumNode.put("name", album.getName());

                for (Song song : album.getSongs()) {
                    songNames.add(song.getName());
                }

                albumNode.put("songs", objectMapper.valueToTree(songNames));
                resultNode.add(albumNode);
            }
        }

        objectNode.set("result", resultNode);

        return objectNode;
    }

    /**
     * Prints the current page of a specific user.
     *
     * @param commandInput the command input containing the username of the user whose current
     *                     page is to be printed.
     * @return the objectNode
     */
    public ObjectNode printCurrentPage(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = null;

		assert user != null;
		if (user.isActive()) {
            if (Objects.requireNonNull(user.getCurrentPage()).equals("Home")) {
                message = user.homePage();
            } else if (Objects.requireNonNull(user.getCurrentPage()).equals("LikedContent")) {
                message = user.likedContentPage();
            } else if (Objects.requireNonNull(user.getCurrentPage()).equals("Artist")) {
                message = User.artistPage();
            } else if (Objects.requireNonNull(user.getCurrentPage()).equals("Host")) {
                message = User.hostPage();
            }
        } else {
            message = commandInput.getUsername() + " is offline.";
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Adds a new podcast to the platform.
     *
     * @param commandInput the command input containing the podcast name, the username of the host
     *                    who owns the podcast, and the list of episodes on the podcast.
     * @return the objectNode
     */
    public ObjectNode addPodcast(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = null;

        if (user == null) {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        } else if (user.getUserType() != null && !user.getUserType().equals("host")) {
            message = commandInput.getUsername() + " is not a host.";
        } else if (user.getUserType() != null) {
            if (Admin.podcastExistsForUser(commandInput.getName(), commandInput.getUsername())) {
                message = commandInput.getUsername() + " has another podcast with the same name.";
            } else {
                message = Admin.addPodcast(commandInput.getName(), commandInput.getUsername(),
                        commandInput.getEpisodes());
            }
        }
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Removes a podcast from the platform.
     *
     * @param commandInput The command input containing the name of the podcast to be removed and the username of the user who owns the podcast.
     * @return the objectNode
     */
    public ObjectNode removePodcast(final CommandInput commandInput) {
        String message = Admin.removePodcast(commandInput.getName(), commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Retrieves the top 5 albums based on the total number of likes of their songs.
     *
     * @param commandInput The command input.
     * @return the objectNode
     */
    public ObjectNode getTop5Albums(final CommandInput commandInput) {
        List<String> albums = Admin.getTop5Albums();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(albums));
        return objectNode;
    }

    /**
     * Changes the current page of a specific user.
     *
     * @param commandInput the command input containing the username of the user whose current
     *                     page is to be changed and the name of the new page.
     * @return the objectNode
     */
    public ObjectNode changePage(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.changePage(commandInput.getUsername(), commandInput.getNextPage());
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;

    }

    /**
     * Deletes a user from the platform.
     *
     * @param commandInput the command input containing the username of the user to be deleted.
     * @return the objectNode
     */
    public ObjectNode deleteUser(final CommandInput commandInput) {

        String message = Admin.deleteUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Retrieves all usernames from the platform.
     *
     * @param commandInput the command input.
     * @return the objectNode
     */
    public ObjectNode getAllUsers(final CommandInput commandInput) {
        List<String> usernames = Admin.getAllUsernames();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(usernames));
        return objectNode;

    }

    /**
     * Adds a new event to the platform.
     *
     * @param commandInput the command input containing the event details.
     * @return the objectNode
     */
    public ObjectNode addEvent(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = User.addEvent(user, commandInput);
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Adds a new merch to the platform.
     *
     * @param commandInput the command input containing the merch details.
     * @return the objectNode
     */
    public ObjectNode addMerch(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = User.addMerch(user, commandInput);
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Adds a new announcement to the platform.
     *
     * @param commandInput the command input containing the announcement details.
     * @return the objectNode
     */
    public ObjectNode addAnnouncement(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.addAnnouncement(user, commandInput);
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Removes an announcement from the platform.
     *
     * @param commandInput the command input containing the announcement details.
     * @return the objectNode
     */
    public ObjectNode removeAnnouncement(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = User.removeAnnouncement(user, commandInput);
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Retrieves all podcasts owned by a specific user from the platform.
     *
     * @param commandInput the command input containing the username of the user whose podcasts
     *                     are to be retrieved.
     * @return the objectNode
     */
    public ObjectNode showPodcasts(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        List<Podcast> podcasts = user.getPodcasts();
        ObjectNode objectNode = objectMapper.createObjectNode();
        ArrayNode resultNode = objectMapper.createArrayNode();

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        for (Podcast podcast : podcasts) {
            ObjectNode podcastNode = objectMapper.createObjectNode();
            List<String> episodeNames = new ArrayList<>();

            podcastNode.put("name", podcast.getName());

            for (Episode episode : podcast.getEpisodes()) {
                episodeNames.add(episode.getName());
            }

            podcastNode.put("episodes", objectMapper.valueToTree(episodeNames));
            resultNode.add(podcastNode);
        }


        objectNode.set("result", resultNode);
        return objectNode;
    }

    /**
     * Removes an album from the platform.
     *
     * @param commandInput the command input containing the album details.
     * @return the ObjectNode
     */
    public ObjectNode removeAlbum(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = null;

        if (user == null) {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        } else {
            message = user.removeAlbum(user, commandInput);
        }
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Removes an event from the platform.
     *
     * @param commandInput the command input containing the event details.
     * @return the objectNode
     */
    public ObjectNode removeEvent(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message = User.removeEvent(user, commandInput);
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Retrieves the top 5 artists based on the total number of likes of their songs.
     *
     * @param commandInput The command input.
     * @return the objectNode
     */
    public ObjectNode getTop5Artists(final CommandInput commandInput) {
        List<String> results = Admin.getTop5Artists();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(results));
        return objectNode;
    }
}
