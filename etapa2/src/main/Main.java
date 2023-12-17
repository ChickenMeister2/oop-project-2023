package main;

import app.Admin;
import app.CommandRunner;
import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.input.CommandInput;
import fileio.input.LibraryInput;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().startsWith("library")) {
                continue;
            }

            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        LibraryInput library = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH
                                                               + "library/library.json"),
                                                               LibraryInput.class);
        CommandInput[] commands = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH
                                                                  + filePath1),
                                                                  CommandInput[].class);
        ArrayNode outputs = objectMapper.createArrayNode();

        Admin.setUsers(library.getUsers());
        Admin.setSongs(library.getSongs());
        Admin.setPodcasts(library.getPodcasts());

        // Singleton pattern for CommandRunner
        CommandRunner runner = CommandRunner.getInstance();

        for (CommandInput command : commands) {
            Admin.updateTimestamp(command.getTimestamp());

            String commandName = command.getCommand();

            switch (commandName) {
                case "search" -> outputs.add(runner.search(command));
                case "select" -> outputs.add(runner.select(command));
                case "load" -> outputs.add(runner.load(command));
                case "playPause" -> outputs.add(runner.playPause(command));
                case "repeat" -> outputs.add(runner.repeat(command));
                case "shuffle" -> outputs.add(runner.shuffle(command));
                case "forward" -> outputs.add(runner.forward(command));
                case "backward" -> outputs.add(runner.backward(command));
                case "like" -> outputs.add(runner.like(command));
                case "next" -> outputs.add(runner.next(command));
                case "prev" -> outputs.add(runner.prev(command));
                case "createPlaylist" -> outputs.add(runner.createPlaylist(command));
                case "addRemoveInPlaylist" ->
                        outputs.add(runner.addRemoveInPlaylist(command));
                case "switchVisibility" -> outputs.add(runner.switchVisibility(command));
                case "showPlaylists" -> outputs.add(runner.showPlaylists(command));
                case "follow" -> outputs.add(runner.follow(command));
                case "status" -> outputs.add(runner.status(command));
                case "showPreferredSongs" -> outputs.add(runner.showLikedSongs(command));
                case "getPreferredGenre" -> outputs.add(runner.getPreferredGenre(command));
                case "getTop5Songs" -> outputs.add(runner.getTop5Songs(command));
                case "getTop5Playlists" -> outputs.add(runner.getTop5Playlists(command));
                case "switchConnectionStatus" ->
                        outputs.add(runner.switchConnectionStatus(command));
                case "getOnlineUsers" -> outputs.add(runner.getOnlineUsers(command));
                case "addUser" -> outputs.add(runner.addUser(command));
                case "addAlbum" -> outputs.add(runner.addAlbum(command));
                case "addPodcast" -> outputs.add(runner.addPodcast(command));
                case "showAlbums" -> outputs.add(runner.showAlbums(command));
                case "printCurrentPage" -> outputs.add(runner.printCurrentPage(command));
                case "removePodcast" -> outputs.add(runner.removePodcast(command));
                case "getTop5Albums" -> outputs.add(runner.getTop5Albums(command));
                case "changePage" -> outputs.add(runner.changePage(command));
                case "deleteUser" -> outputs.add(runner.deleteUser(command));
                case "getAllUsers" -> outputs.add(runner.getAllUsers(command));
                case "addEvent" -> outputs.add(runner.addEvent(command));
                case "removeEvent" -> outputs.add(runner.removeEvent(command));
                case "addMerch" -> outputs.add(runner.addMerch(command));
                case "addAnnouncement" -> outputs.add(runner.addAnnouncement(command));
                case "removeAnnouncement" -> outputs.add(runner.removeAnnouncement(command));
                case "showPodcasts" -> outputs.add(runner.showPodcasts(command));
                case "removeAlbum" -> outputs.add(runner.removeAlbum(command));
                case "getTop5Artists" -> outputs.add(runner.getTop5Artists(command));
                default -> System.out.println("Invalid command " + commandName);
            }
        }


        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), outputs);

        Admin.reset();
    }
}
