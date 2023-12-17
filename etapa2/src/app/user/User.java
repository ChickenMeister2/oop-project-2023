package app.user;

import app.Admin;
import app.audio.Collections.*;
import app.audio.Files.AudioFile;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.audio.LibraryEntry;
import app.player.Player;
import app.player.PlayerStats;
import app.searchBar.Filters;
import app.searchBar.SearchBar;
import app.utils.Enums;
import fileio.input.CommandInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


/**
 * The type User.
 */
public class User {
    private static final int LIMIT = 5;
    private static final int LOWERLIMIT = 0;
    private static final int YEARLOWERLIMIT = 1900;
    private static final int YEARUPPERLIMIT = 2023;
    private static final int MONTHUPPERLIMIT = 12;
    private static final int DAYUPPERLIMIT = 31;
    private static final int DAYUPPERLIMITFEB = 28;
    private static final int FEB = 2;

    @Getter
    private String username;
    @Getter
    private int age;
    @Getter
    @Setter
    private boolean active;
    @Getter
    private String city;
    @Getter
    private ArrayList<Playlist> playlists;
    @Getter
    @Setter
    private ArrayList<Song> likedSongs;
    @Getter
    @Setter
    private ArrayList<Playlist> followedPlaylists;
    @Getter
    private ArrayList<Merch> merchList;
    @Getter
    private ArrayList<Event> eventList;
    @Getter
    private ArrayList<Announcement> announcements;
    @Getter
    @Setter
    private String userType;
    @Getter
    @Setter
    private ArrayList<Album> albums;
    @Getter
    @Setter
    private ArrayList<Podcast> podcasts;
    @Getter
    @Setter
    private String currentPage;
    @Getter
    @Setter
    private static String currentArtist;
    @Getter
    @Setter
    private String currentPlaylist;
    @Getter
    private final Player player;
    private final SearchBar searchBar;
    private boolean lastSearched;


    /**
     * Instantiates a new User.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public User(final String username, final int age, final String city) {
        this.username = username;
        this.age = age;
        this.city = city;
        playlists = new ArrayList<>();
        likedSongs = new ArrayList<>();
        followedPlaylists = new ArrayList<>();
        eventList = new ArrayList<>();
        merchList = new ArrayList<>();
        podcasts = new ArrayList<>();
        announcements = new ArrayList<>();
        albums = new ArrayList<>();
        player = new Player();
        searchBar = new SearchBar(username);
        lastSearched = false;
        active = true;
        currentPage = "Home";
        userType = "user";    }

    // This class adds a new user that's a host or an artist
    public static class AddSuperUser extends User {
        public AddSuperUser(final String username, final int age, final String city, final String userType) {
            super(username, age, city);
            setUserType(userType);
        }
    }

    /**
     * Search array list.
     *
     * @param filters the filters
     * @param type    the type
     * @return the array list
     */
    public ArrayList<String> search(final Filters filters, final String type) {
            searchBar.clearSelection();
        player.stop();
        lastSearched = true;

        ArrayList<String> results = new ArrayList<>();
        List<LibraryEntry> libraryEntries = searchBar.search(filters, type);
        for (LibraryEntry libraryEntry : libraryEntries) {
            results.add(libraryEntry.getName());
        }
        return results;

    }

    /**
     * Select string.
     *
     * @param itemNumber the item number
     * @return the string
     */
    public String select(final int itemNumber) {
        if (!lastSearched) {
            return "Please conduct a search before making a selection.";
        }

        LibraryEntry selected = searchBar.select(itemNumber);

        if (selected == null) {
            return "The selected ID is too high.";
        }

        return "Successfully selected %s.".formatted(selected.getName());
    }

    /**
     * Select user string.
     *
     * @param itemNumber the item number
     * @return the string
     */
    public String selectUser(final int itemNumber) {
        if (!lastSearched) {
            return "Please conduct a search before making a selection.";
        }

        lastSearched = false;
        LibraryEntry selected = searchBar.select(itemNumber);

        if (selected == null) {
            return "The selected ID is too high.";
        }
        searchBar.clearResults();

        return selected.getName();
    }

    /**
     * Load string.
     *
     * @return the string
     */
    public String load() {
        if (searchBar.getLastSelected() == null) {
            return "Please select a source before attempting to load.";
        }

        if (!searchBar.getLastSearchType().equals("song")
            && ((AudioCollection) searchBar.getLastSelected()).getNumberOfTracks() == 0) {
            return "You can't load an empty audio collection!";
        }

        player.setSource(searchBar.getLastSelected(), searchBar.getLastSearchType());

        if (searchBar.getLastSearchType().equals("playlist")) {
            currentPlaylist = searchBar.getLastSelected().getName();
        } else {
            currentPlaylist = null;
        }

        searchBar.clearSelection();

        player.pause();

        return "Playback loaded successfully.";
    }

    /**
     * Play pause string.
     *
     * @return the string
     */
    public String playPause() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before attempting to pause or resume playback.";
        }

        player.pause();

        if (player.getPaused()) {
            return "Playback paused successfully.";
        } else {
            return "Playback resumed successfully.";
        }
    }

    /**
     * Is playing audio file.
     *
     * @return the audio file
     */
    public AudioFile isPlaying() {
        if (player.getCurrentAudioFile() != null) {
            return player.getCurrentAudioFile();
        }
        return null;
    }

    /**
     * Repeat string.
     *
     * @return the string
     */
    public String repeat() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before setting the repeat status.";
        }

        Enums.RepeatMode repeatMode = player.repeat();
        String repeatStatus = "";

        switch (repeatMode) {
            case NO_REPEAT -> {
                repeatStatus = "no repeat";
            }
            case REPEAT_ONCE -> {
                repeatStatus = "repeat once";
            }
            case REPEAT_ALL -> {
                repeatStatus = "repeat all";
            }
            case REPEAT_INFINITE -> {
                repeatStatus = "repeat infinite";
            }
            case REPEAT_CURRENT_SONG -> {
                repeatStatus = "repeat current song";
            }
            default -> {
                repeatStatus = "";
            }
        }

        return "Repeat mode changed to %s.".formatted(repeatStatus);
    }

    /**
     * Shuffle string.
     *
     * @param seed the seed
     * @return the string
     */
    public String shuffle(final Integer seed) {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before using the shuffle function.";
        }

        if (!player.getType().equals("playlist") && !player.getType().equals("album")) {
            return "The loaded source is not a playlist or an album.";
        }

        player.shuffle(seed);

        if (player.getShuffle()) {
            return "Shuffle function activated successfully.";
        }
        return "Shuffle function deactivated successfully.";
    }

    /**
     * Forward string.
     *
     * @return the string
     */
    public String forward() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before attempting to forward.";
        }

        if (!player.getType().equals("podcast")) {
            return "The loaded source is not a podcast.";
        }

        player.skipNext();

        return "Skipped forward successfully.";
    }

    /**
     * Backward string.
     *
     * @return the string
     */
    public String backward() {
        if (player.getCurrentAudioFile() == null) {
            return "Please select a source before rewinding.";
        }

        if (!player.getType().equals("podcast")) {
            return "The loaded source is not a podcast.";
        }

        player.skipPrev();

        return "Rewound successfully.";
    }

    /**
     * Like string.
     *
     * @return the string
     */
    public String like() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before liking or unliking.";
        }

        if (!player.getType().equals("song") && !player.getType().equals("playlist")
                && !player.getType().equals("album")) {
            return "Loaded source is not a song.";
        }

        if (!active) {
            return username + " is offline.";
        }

        Song song = (Song) player.getCurrentAudioFile();

        if (likedSongs.contains(song)) {
            likedSongs.remove(song);
            song.dislike();

            return "Unlike registered successfully.";
        }

        likedSongs.add(song);
        song.like();
        return "Like registered successfully.";
    }

    /**
     * Next string.
     *
     * @return the string
     */
    public String next() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before skipping to the next track.";
        }

        player.next();

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before skipping to the next track.";
        }

        return "Skipped to next track successfully. The current track is %s."
                .formatted(player.getCurrentAudioFile().getName());
    }

    /**
     * Prev string.
     *
     * @return the string
     */
    public String prev() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before returning to the previous track.";
        }

        player.prev();

        return "Returned to previous track successfully. The current track is %s."
                .formatted(player.getCurrentAudioFile().getName());
    }

    /**
     * Create playlist string.
     *
     * @param name      the name
     * @param timestamp the timestamp
     * @return the string
     */
    public String createPlaylist(final String name, final int timestamp) {
        if (playlists.stream().anyMatch(playlist -> playlist.getName().equals(name))) {
            return "A playlist with the same name already exists.";
        }

        playlists.add(new Playlist(name, username, timestamp));

        return "Playlist created successfully.";
    }

    /**
     * Add remove in playlist string.
     *
     * @param id the id
     * @return the string
     */
    public String addRemoveInPlaylist(final int id) {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before adding to or removing from the playlist.";
        }

        if (player.getType().equals("podcast")) {
            return "The loaded source is not a song.";
        }

        if (id > playlists.size()) {
            return "The specified playlist does not exist.";
        }

        Playlist playlist = playlists.get(id - 1);

        if (playlist.containsSong((Song) player.getCurrentAudioFile())) {
            playlist.removeSong((Song) player.getCurrentAudioFile());
            return "Successfully removed from playlist.";
        }

        playlist.addSong((Song) player.getCurrentAudioFile());
        return "Successfully added to playlist.";
    }

    /**
     * Switch playlist visibility string.
     *
     * @param playlistId the playlist id
     * @return the string
     */
    public String switchPlaylistVisibility(final Integer playlistId) {
        if (playlistId > playlists.size()) {
            return "The specified playlist ID is too high.";
        }

        Playlist playlist = playlists.get(playlistId - 1);
        playlist.switchVisibility();

        if (playlist.getVisibility() == Enums.Visibility.PUBLIC) {
            return "Visibility status updated successfully to public.";
        }

        return "Visibility status updated successfully to private.";
    }

    /**
     * Show playlists array list.
     *
     * @return the array list
     */
    public ArrayList<PlaylistOutput> showPlaylists() {
        ArrayList<PlaylistOutput> playlistOutputs = new ArrayList<>();
        for (Playlist playlist : playlists) {
            playlistOutputs.add(new PlaylistOutput(playlist));
        }

        return playlistOutputs;
    }

    /**
     * Follow string.
     *
     * @return the string
     */
    public String follow() {
        LibraryEntry selection = searchBar.getLastSelected();
        String type = searchBar.getLastSearchType();

        if (selection == null) {
            return "Please select a source before following or unfollowing.";
        }

        if (!type.equals("playlist")) {
            return "The selected source is not a playlist.";
        }

        Playlist playlist = (Playlist) selection;

        if (playlist.getOwner().equals(username)) {
            return "You cannot follow or unfollow your own playlist.";
        }

        if (followedPlaylists.contains(playlist)) {
            followedPlaylists.remove(playlist);
            playlist.decreaseFollowers();

            return "Playlist unfollowed successfully.";
        }

        followedPlaylists.add(playlist);
        playlist.increaseFollowers();


        return "Playlist followed successfully.";
    }

    /**
     * Gets player stats.
     *
     * @return the player stats
     */
    public PlayerStats getPlayerStats() {
        return player.getStats();
    }

    /**
     * Show preferred songs array list.
     *
     * @return the array list
     */
    public ArrayList<String> showPreferredSongs() {
        ArrayList<String> results = new ArrayList<>();
        for (AudioFile audioFile : likedSongs) {
            results.add(audioFile.getName());
        }

        return results;
    }

    /**
     * Gets preferred genre.
     *
     * @return the preferred genre
     */
    public String getPreferredGenre() {
        String[] genres = {"pop", "rock", "rap"};
        int[] counts = new int[genres.length];
        int mostLikedIndex = -1;
        int mostLikedCount = 0;

        for (Song song : likedSongs) {
            for (int i = 0; i < genres.length; i++) {
                if (song.getGenre().equals(genres[i])) {
                    counts[i]++;
                    if (counts[i] > mostLikedCount) {
                        mostLikedCount = counts[i];
                        mostLikedIndex = i;
                    }
                    break;
                }
            }
        }

        String preferredGenre = mostLikedIndex != -1 ? genres[mostLikedIndex] : "unknown";
        return "This user's preferred genre is %s.".formatted(preferredGenre);
    }

    /**
     * Simulate time.
     *
     * @param time the time
     */
    public void simulateTime(final int time) {
        player.simulatePlayer(time);
    }

    /**
     * Switch user active string.
     *
     * @param user the user
     * @return the message
     */
    public String switchUserActive(final User user) {
        if (user == null) {
            return null;
        }
        user.setActive(!user.isActive());

        return user.getUsername() + " has changed status successfully.";
    }

    /**
     * Add event to database.
     *
     * @param user the user
     * @param commandInput the command input
     * @return the message
     */
    public static String addEvent(final User user, final CommandInput commandInput) {
        if (user == null) {
            return "The username " + commandInput.getUsername() + " doesn't exist.";
        }

        if (!user.getUserType().equals("artist")) {
            return user.getUsername() + " is not an artist.";
        }

        if (!checkDate(commandInput.getDate())) {
            return "Event for " + user.getUsername() + " does not have a valid date.";
        }

        Event event = new Event(commandInput.getName(),
                commandInput.getDescription(), commandInput.getDate());

        if (user.eventList.contains(event)) {
            return user.getUsername() + " has another event with the same date";
        }

        user.eventList.add(event);
        return user.getUsername() + " has added new event successfully.";

    }

    /**
     * Remove event from database.
     * @param user the user
     * @param commandInput the command input
     * @return the message
     */
    public static String removeEvent(final User user, final CommandInput commandInput) {
        if (user == null) {
            return "The username " + commandInput.getUsername() + " doesn't exist.";
        }

        if (!user.getUserType().equals("artist")) {
            return user.getUsername() + " is not a artist.";
        }

        Event eventToRemove = null;

        // Searching for the to be removed event in artist's event list
        for (Event event : user.getEventList()) {
            if (event.getName().equals(commandInput.getName())) {
                eventToRemove = event;
                break;
            }
        }

        // If the event was not found, then it does not exist
        if (eventToRemove == null) {
            return user.getUsername() + " doesn't have an event with the given name.";
        }

        // Removing the event from the artist's event list
        user.getEventList().remove(eventToRemove);
        return user.getUsername() + " deleted the event successfully.";
    }

    /**
     * Remove announcement from database.
     * @param user the user
     * @param commandInput the command input
     * @return the message
     */
    public static String removeAnnouncement(final User user, final CommandInput commandInput) {
        if (user == null) {
            return "The username " + commandInput.getUsername() + " doesn't exist.";
        }

        if (!user.getUserType().equals("host")) {
            return user.getUsername() + " is not a host.";
        }

        Announcement announcementToRemove = null;

        // Searching for the announcement to be removed event in artist's event list
        for (Announcement announcement : user.getAnnouncements()) {
            if (announcement.getName().equals(commandInput.getName())) {
                announcementToRemove = announcement;
                break;
            }
        }

        if (announcementToRemove == null) {
            return user.getUsername() + " has no announcement with the given name.";
        }

        user.getAnnouncements().remove(announcementToRemove);
        return user.getUsername() + " has successfully deleted the announcement.";
    }

    /**
     * Remove album from database.
     * @param user the user
     * @param commandInput the command input
     * @return the message
     */
    public String removeAlbum(final User user, final CommandInput commandInput) {
        if (user == null) {
            return "The username " + commandInput.getUsername() + " doesn't exist.";
        }

        if (!user.getUserType().equals("artist")) {
            return user.getUsername() + " is not an artist.";
        }

        // Searching for the album to be removed in artist's album list
        Album albumToRemove = null;
        for (Album album : user.getAlbums()) {
            if (album.getName().equals(commandInput.getName())) {
                albumToRemove = album;
                break;
            }
        }

        if (albumToRemove == null) {
            return user.getUsername() + " doesn't have an album with the given name.";
        }

        // Check if the album can be removed
        if (!Admin.canDeleteAlbum(albumToRemove)) {
            return commandInput.getUsername() + " can't delete this album.";
        }

        Admin.clearAlbum(albumToRemove);

        return user.getUsername() + " deleted the album successfully.";
    }

    /**
     * Add merch to database.
     * @param user the user
     * @param commandInput the command input
     * @return the message
     */
    public static String addMerch(final User user, final CommandInput commandInput) {
        if (user == null) {
            return "The username " + commandInput.getUsername() + " doesn't exist.";
        }

        if (!user.getUserType().equals("artist")) {
            return user.getUsername() + " is not an artist.";
        }

        if (commandInput.getPrice() < 0) {
            return "Price for merchandise can not be negative.";
        }

        // Creating new merchandise object
        Merch merch = new Merch(commandInput.getName(),
                commandInput.getDescription(), commandInput.getPrice());

        // Checking for duplicate merchandise
        for (Merch existingMerch : user.getMerchList()) {
            if (existingMerch.getName().equals(merch.getName())) {
                return user.getUsername() + " has merchandise with the same name.";
            }
        }

        user.merchList.add(merch);

        return user.getUsername() + " has added new merchandise successfully.";

    }

    /**
     * Add announcement for host.
     * @param user the user
     * @param commandInput the command input
     * @return the message
     */
    public static String addAnnouncement(final User user, final CommandInput commandInput) {
        if (user == null) {
            return "The username " + commandInput.getUsername() + " doesn't exist.";
        }

        if (!user.getUserType().equals("host")) {
            return user.getUsername() + " is not a host.";
        }

        Announcement announcement = new Announcement(commandInput.getName(),
                commandInput.getDescription());

        // Check if the announcement exists
        for (Announcement existingAnnouncement : user.getAnnouncements()) {
            if (existingAnnouncement.getName().equals(announcement.getName())) {
                return user.getUsername() + " has already added an announcement with this name.";
            }
        }

        user.announcements.add(announcement);

        return user.getUsername() + " has successfully added new announcement.";
    }

    /**
     * Date validator for event.
     * @param date the date
     * @return true if date is valid, false otherwise
     */
    public static boolean checkDate(final String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        try {
            LocalDate localDate = LocalDate.parse(date, formatter);
            int year = localDate.getYear();
            int month = localDate.getMonthValue();
            int day = localDate.getDayOfMonth();

            if (year < YEARLOWERLIMIT || year > YEARUPPERLIMIT) {
                return false;
            }
            if (month > MONTHUPPERLIMIT) {
                return false;
            }
            if (day > DAYUPPERLIMIT || (month == FEB && day > DAYUPPERLIMITFEB)) {
                return false;
            }
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    /**
     * Print current user's homepage
     * @return the homepage
     */
    public String homePage() {

        StringBuilder sb = new StringBuilder();
        ArrayList<Song> orderedLikeSongs = new ArrayList<>(likedSongs);

        // Sorting liked songs by number of likes
        orderedLikeSongs.sort(new Comparator<Song>() {
            @Override
            public int compare(final Song s1, final Song s2) {
                return Integer.compare(s2.getLikes(), s1.getLikes());
            }
        });

        int i = LOWERLIMIT;

        // Adding the first 5 liked songs to the homepage
        sb.append("Liked songs:\n\t[");
        for (Song song : orderedLikeSongs) {
            if (i == LIMIT) {
                break;
            }
            sb.append(song.getName()).append(", ");
            i++;
        }

        // Removing whitespaces and comma from the end of the string
        if (i > LOWERLIMIT) {
            sb.setLength(sb.length() - 2);
        }

        i = LOWERLIMIT;

        // Adding the first 5 followed playlists to the homepage
        sb.append("]\n\nFollowed playlists:\n\t[");
        for (Playlist playlist : followedPlaylists) {
            if (i == LIMIT) {
                break;
            }
            sb.append(playlist.getName()).append(", ");
            i++;
        }

        // Removing whitespaces and comma from the end of the string
        if (i > LOWERLIMIT) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Print current user's liked content page
     * @return the liked content page
     */
    public String likedContentPage() {
        StringBuilder sb = new StringBuilder();

        // Building user's liked content page
        sb.append("Liked songs:\n\t[");
        for (Song song : likedSongs) {
            sb.append(song.getName()).append(" - ").append(song.getArtist()).append(", ");
        }

        if (!likedSongs.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }

        sb.append("]\n\nFollowed playlists:\n\t[");
        for (Playlist playlist : followedPlaylists) {
            sb.append(playlist.getName()).append(" - ").append(playlist.getOwner()).append(", ");
        }

        if (!followedPlaylists.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]");

        return sb.toString();
    }

    /**
     * Print current user's artist page
     * @return the artist page
     */
    public static String artistPage() {
        User searched = null;

        for (User searchedArtist : Admin.getAllUsers()) {
            if (searchedArtist.getUsername().equals(currentArtist)) {
                searched = searchedArtist;
                break;
            }
        }

        StringBuilder sb = new StringBuilder();

        // Building the artist page
        sb.append("Albums:\n\t[");
        for (Album album : searched.getAlbums()) {
            sb.append(album.getName()).append(", ");
        }
        if (!searched.getAlbums().isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]\n\n");

        sb.append("Merch:\n\t[");
        for (Merch merch : searched.getMerchList()) {
            sb.append(merch.getName()).append(" - ").append(merch.getPrice()).append(":\n\t")
                    .append(merch.getDescription()).append(", ");
        }
        if (!searched.getMerchList().isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]\n\n");

        sb.append("Events:\n\t[");
        for (Event event : searched.getEventList()) {
            sb.append(event.getName()).append(" - ").append(event.getDate()).append(":\n\t")
                    .append(event.getDescription()).append(", ");
        }
        if (!searched.getEventList().isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]");

        return sb.toString();
    }

    /**
     * Print current user's host page
     * @return the host page
     */
    public static String hostPage() {
        User searched = null;
        for (User searchedHost : Admin.getAllUsers()) {
            if (searchedHost.getUsername().equals(currentArtist)) {
                searched = searchedHost;
                break;
            }
        }

        StringBuilder sb = new StringBuilder();

        // Building the host page
        sb.append("Podcasts:\n\t[");
        for (Podcast podcast : searched.getPodcasts()) {
            sb.append(podcast.getName()).append(":\n\t[");
            for (Episode episode : podcast.getEpisodes()) {
                sb.append(episode.getName()).append(" - ")
                        .append(episode.getDescription()).append(", ");
            }
            if (!podcast.getEpisodes().isEmpty()) {
                sb.setLength(sb.length() - 2);
            }
            sb.append("]\n, ");
        }
        if (!searched.getPodcasts().isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]\n\n");

        sb.append("Announcements:\n\t[");
        for (Announcement announcement : searched.getAnnouncements()) {
            sb.append(announcement.getName()).append(":\n\t")
                    .append(announcement.getDescription()).append("\n, ");
        }
        if (!searched.getAnnouncements().isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]");

        return sb.toString();

    }

    /**
     * Change current user's page
     * @return the accessed page
     */
    public String changePage(final String username, final String nextPage) {
        int valid = 1;
        // Checking if command is valid
        if (Objects.requireNonNull(nextPage).equals("Home")) {
            currentPage = "Home";
        } else if (Objects.requireNonNull(nextPage).equals("LikedContent")) {
            currentPage = "LikedContent";
        } else {
            valid = 0;
        }
        if (valid == 1) {
            return username + " accessed " + currentPage + " successfully.";
        } else {
            return username + " is trying to access a non-existent page.";
        }
    }

 }
