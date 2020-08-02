package launcher;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import updater.NetworkControl;
import updater.WriterData;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.*;
import java.util.stream.Stream;

public class LauncherController implements Initializable {
    @FXML
    public ImageView remind_tick;
    public MediaView background_video;
    public ImageView remind_check;
    public ImageView input_name;
    public ImageView play_button;
    public ImageView web_site;
    public ImageView bar_settings;
    public ImageView bar_close;
    public ImageView bar_under;
    public ImageView company_logo;
    public ImageView discord_logo;
    private Media me;
    private MediaPlayer mp;
    private Thread thread;
    private JSONArray FilesArray;
    private final List<String> libraries = new ArrayList<>();
    private Process proc = null;
    List version_path_list_natives = new ArrayList();


    public static void openBrowserUrl(String url) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        if (WriterData.libraries.isEmpty()) {
            thread = new Thread(() -> {
                try {
                    GetJsonWeb();
                    OperationLister();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            thread.start();
        }

        readJson_libraries_downloads_classifiers_natives_Y("versions" + File.separator + "1.12.2-forge1.12.2-14.23.5.2847" + File.separator + "1.12.2-forge1.12.2-14.23.5.2847.json");
        readJson_twitch_natives("versions" + File.separator + "1.12.2-forge1.12.2-14.23.5.2847" + File.separator + "1.12.2-forge1.12.2-14.23.5.2847.json");

        discord_logo.setOnMouseReleased((event) -> {
            openBrowserUrl("https://discord.gg/U2MkdfC");
        });
        company_logo.setOnMouseReleased((event) -> {
            openBrowserUrl("https://www.wehoog.com");
        });
        web_site.setOnMouseReleased((event) -> {
            openBrowserUrl("https://www.ottomine.net");
        });
        remind_tick.setOnMouseReleased((event) -> {
            if (remind_tick.getOpacity() == 1.0) {
                remind_tick.setOpacity(0.0);
            } else {
                remind_tick.setOpacity(1.0);
            }
        });

        Thread loopVideo = new Thread() {
            public void run() {
                boolean loop = true;
                do {
                    String pathMedia = new File("src/resources/launcher_res/launcher_back.mp4").getAbsolutePath();
                    me = new Media(new File(pathMedia).toURI().toString());
                    mp = new MediaPlayer(me);
                    background_video.setMediaPlayer(mp);
                    mp.play();
                    try {
                        Thread.sleep(11000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (loop);
            }

        };
        loopVideo.start();
        fadeIn(discord_logo);
        fadeOut(discord_logo);
        fadeIn(company_logo);
        fadeOut(company_logo);
        fadeIn(play_button);
        fadeOut(play_button);
    }//end of initialize

    @FXML
    public void launchGame() throws InterruptedException {
        if (thread != null) thread.join();
        try {

            File librarydir = new File("libraries" + File.separator);
            ArrayList<String> names = new ArrayList<>(Arrays.asList(Objects.requireNonNull(librarydir.list())));
            names.replaceAll(s -> librarydir.getPath() + "\\" + s);

            String OperatingSystemToUse = getOS();
            String gameDirectory = "";
            String AssetsRoot = "assets";

            int Xmx = 1024;
            String JavaPath = "java";
            String versionName = "1.12.2-LiteLoader1.12.2-1.12.2-forge1.12.2-14.23.5.2847";
            String assetsIndexId = "1.12.2-LiteLoader1.12.2-1.12.2-forge1.12.2-14.23.5.2847";

            String VersionType = "release";
            String GameAssets = "assets";
            String AuthSession = "OFFLINE";

            String[] HalfArgument = generateMinecraftArguments("Dantero", versionName, gameDirectory, AssetsRoot, assetsIndexId, "0", "0", "{}", "mojang", VersionType, GameAssets, AuthSession);
            String MinecraftJar = "versions" + File.separator + "1.12.2-forge1.12.2-14.23.5.2847" + File.separator + "1.12.2-forge1.12.2-14.23.5.2847.jar";
            String FullLibraryArgument = generateLibrariesArguments(OperatingSystemToUse) + getArgsDiv(OperatingSystemToUse) + MinecraftJar;
            String mainClass = "net.minecraft.launchwrapper.Launch";
            String NativesDir = "versions" + File.separator + "1.12.2-forge1.12.2-14.23.5.2847" + File.separator + "natives" + File.separator;

            String[] cmds = {"-Xmx" + Xmx + "M", "-XX:+UseConcMarkSweepGC", "-XX:+CMSIncrementalMode", "-XX:-UseAdaptiveSizePolicy", "-Xmn128M", "-Djava.library.path=" + NativesDir, "-cp", FullLibraryArgument, mainClass};

            String[] javaPathArr = {JavaPath};
            cmds = Stream.concat(Arrays.stream(javaPathArr), Arrays.stream(cmds)).toArray(String[]::new);

            String[] finalArgs = Stream.concat(Arrays.stream(cmds), Arrays.stream(HalfArgument)).toArray(String[]::new);


            try {
                proc = Runtime.getRuntime().exec(finalArgs);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (proc != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                String line;
                BufferedReader stdError = new BufferedReader(new
                        InputStreamReader(proc.getErrorStream()));
                try {
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                    while ((line = stdError.readLine()) != null) {
                        System.out.println(line);
                    }
                    proc.destroy();
                    LauncherMain.getInstance().stop();
                    Platform.exit();
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***** Util Methods  *****/

    String generateLibrariesArguments(String OS) {
        StringBuilder cp = new StringBuilder();

        List<String> list = new ArrayList<>(getLibraries());

        List<String> sorted = new ArrayList<>(list);

        for (int i = 0; i < sorted.size(); i++) {

            if (cp.toString().contains(sorted.get(i))) continue;

            if (i == sorted.size() - 1) {

                cp.append(sorted.get(i));

            } else {
                cp.append(sorted.get(i)).append(getArgsDiv(OS));

            }
        }
        return cp.toString();
    }

    String getArgsDiv(String OS) {
        if (OS.equals("Windows")) {
            return (";");
        }
        if (OS.equals("Linux")) {
            return (":");
        }
        if (OS.equals("Mac")) {
            return (":");
        }

        return "N/A";
    }

    String[] generateMinecraftArguments(String auth_player_name, String version_name, String game_directory, String assets_root, String assets_index_name, String auth_uuid, String auth_access_token, String user_properties, String user_type, String version_type, String game_assets, String auth_session) {

        String cmdArgs = readJson_minecraftArguments("versions" + File.separator + "1.12.2-forge1.12.2-14.23.5.2847" + File.separator + "1.12.2-forge1.12.2-14.23.5.2847.json");

        cmdArgs = cmdArgs.replaceAll(" +", " ");
        String[] tempArgsSplit = cmdArgs.split(" ");
        for (int i = 0; i < tempArgsSplit.length; i++) {
            if (tempArgsSplit[i].equals("${auth_player_name}")) {
                tempArgsSplit[i] = auth_player_name;
            }
            if (tempArgsSplit[i].equals("${version_name}")) {
                tempArgsSplit[i] = version_name;
            }
            if (tempArgsSplit[i].equals("${game_directory}")) {
                tempArgsSplit[i] = game_directory;
            }
            if (tempArgsSplit[i].equals("${assets_root}")) {
                tempArgsSplit[i] = assets_root;
            }
            if (tempArgsSplit[i].equals("${assets_index_name}")) {
                tempArgsSplit[i] = assets_index_name;
            }
            if (tempArgsSplit[i].equals("${auth_uuid}")) {
                tempArgsSplit[i] = auth_uuid;
            }
            if (tempArgsSplit[i].equals("${auth_access_token}")) {
                tempArgsSplit[i] = auth_access_token;
            }
            if (tempArgsSplit[i].equals("${user_properties}")) {
                tempArgsSplit[i] = user_properties;
            }
            if (tempArgsSplit[i].equals("${user_type}")) {
                tempArgsSplit[i] = user_type;
            }
            if (tempArgsSplit[i].equals("${version_type}")) {
                tempArgsSplit[i] = version_type;
            }
            if (tempArgsSplit[i].equals("${game_assets}")) {
                tempArgsSplit[i] = game_assets;
            }
            if (tempArgsSplit[i].equals("${auth_session}")) {
                tempArgsSplit[i] = auth_session;
            }
        }
        return tempArgsSplit;
    }

    private String readJson_minecraftArguments(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            StringBuilder json = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                json.append(line).append('\n');
            }
            JSONObject jsonObject = new JSONObject(json.toString());

            return jsonObject.getString("minecraftArguments");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    private String getOS() {
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);

        if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
            return ("Mac");
        } else if (OS.indexOf("win") >= 0) {
            return ("Windows");
        } else if (OS.indexOf("nux") >= 0) {
            return ("Linux");
        } else {
            //bring support to other OS.
            //we will assume that the OS is based on linux.
            return ("Linux");
        }
    }

    private List<String> getLibraries() {
        if (WriterData.libraries.isEmpty()) {
            return libraries;
        } else {
            return WriterData.libraries;
        }
    }

    void GetJsonWeb() throws JSONException, IOException, InterruptedException {
        String JSON_string;
        URL JSONurl = new URL("https://www.ottomine.net/ottomine-files/FilesArray.json");
        try {
            InputStream is = JSONurl.openConnection().getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder JsonStringBuilder = new StringBuilder();
            while ((JSON_string = reader.readLine()) != null) {
                JsonStringBuilder.append(JSON_string + '\n');
            }
            FilesArray = new JSONArray(JsonStringBuilder.toString());
            reader.close();
        } catch (UnknownHostException | SocketException HostError) {
            do {
                NetworkControl.NetIsAvailable();
                if (NetworkControl.NetIsAvailable()) {
                    GetJsonWeb();
                }
                Thread.sleep(1000);
            } while (FilesArray == null);
        }
    }

    private void OperationLister() throws JSONException {
        for (int i = 0; i < FilesArray.length(); i++) {
            JSONObject CurrentJSONobject = (JSONObject) FilesArray.get(i);
            String CurrentJSONpath = (String) CurrentJSONobject.get("path");
            String CurrentJSONtype = (String) CurrentJSONobject.get("type");

            if (CurrentJSONtype.equals("file")) {
                if (CurrentJSONpath.startsWith("libraries")) {
                    libraries.add(CurrentJSONpath);
                }
                if (CurrentJSONpath.startsWith("mods")) {
                    libraries.add(CurrentJSONpath);
                }
            }
        }
    }

    List versionCheck() {
        List list = new ArrayList<String>();
        list.addAll(version_path_list_natives);

        List removeList = new ArrayList<String>();

        Collections.sort(list, (a, b)-> {
            if (a == null || b == null) return 0;
            File aFile = new File((String) a);
            File bFile = new File((String) b);
            String aname = aFile.getName();
            if (aname.isEmpty()) return 0;
            String aremoved = aname.substring(0, aname.lastIndexOf('.'));
            String bname = bFile.getName();
            if (bname.isEmpty()) return 0;
            String bremoved = bname.substring(0, bname.lastIndexOf('.'));
            for (String str : aremoved.split("-")) {
                if (isInteger(str)) {
                    if (Integer.parseInt(str) > 1000) {
                        aremoved = aremoved.replaceAll("-" + str, "");
                    }
                }
            }
            for (String str : bremoved.split("-")) {
                if (isInteger(str)) {
                    if (Integer.parseInt(str) > 1000) {
                        bremoved = bremoved.replaceAll("-" + str, "");
                    }
                }
            }
            int versiona = Integer.parseInt(aremoved.replaceAll("[\\D]", ""));
            int versionB = Integer.parseInt(bremoved.replaceAll("[\\D]", ""));
            String formattedvera = aremoved.replaceAll(getNatives_OS(getOS()), "").replaceAll("[A-Za-z]?", "").replaceAll("-", "");
            String formattedverb = bremoved.replaceAll(getNatives_OS(getOS()), "").replaceAll("[A-Za-z]?", "").replaceAll("-", "");
            if (!aname.replaceAll(getNatives_OS(getOS()), "").replaceAll(formattedvera, "").equals(
                    bname.replaceAll(getNatives_OS(getOS()), "").replaceAll(formattedverb, ""))) return 0;
            if (versiona == versionB) return 0;
            if (versiona > versionB){
                if (!removeList.contains(b)) {
                    removeList.add(b);
                }
                return 1;
            }
            if (versiona < versionB) {
                if (!removeList.contains(a)) removeList.add(a);
                return -1;
            }
            return 0;
        });

        List sortedList = list;
        sortedList.removeAll(removeList);

        return sortedList;
    }

    String getNatives_OS(String natives_OS) {
        try {
            if (natives_OS.equals("Linux")) {
                return natives_OS.replace("Linux", "natives-linux");
            } else if (natives_OS.equals("Windows")) {
                return natives_OS.replace("Windows", "natives-windows");
            } else if (natives_OS.equals("Mac")) {
                return natives_OS.replace("Mac", "natives-osx");
            } else {
                return "N/A";
                //I DON'T KNOW THIS OS!
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            return "N/A";
        }
    }

    boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        }catch (NumberFormatException e) {
            return false;
        }
    }

    public void readJson_twitch_natives(String path) {
        try {

            String natives_OS;
            if (getOS().equals("Linux")) {
                natives_OS = "linux";
            } else if (getOS().equals("Windows")) {
                natives_OS = "windows";
            } else if (getOS().equals("Mac")) {
                natives_OS =  "osx";
            } else {
                natives_OS = "N/A";
            }
            String content = new Scanner(new File(path)).useDelimiter("\\Z").next();

            ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
            String script_js = "var getJsonLibrariesDownloadsClassifiersNativesX=function(r,s){var a=r,e=JSON.parse(a),n=\"\",t=0;for(i=0;i<500;i++)try{n=n+e.libraries[t].classifies[s].url+\"\\n\",t+=1}catch(o){t+=1}return n},getJsonLibrariesDownloadsClassifiersNativesY=function(r,s){var a=r,e=JSON.parse(a),n=\"\",t=0;for(i=0;i<500;i++)try{n=n+e.libraries[t].classifies[s].path+\"\\n\",t+=1}catch(o){t+=1}return n},getJsonLibrariesDownloadsClassifiersNativesZ=function(r){var s=r,a=JSON.parse(s),e=\"\",n=0;for(i=0;i<500;i++)try{a.libraries[n].natives?(e=e+a.libraries[n].name+\"\\n\",n+=1):n+=1}catch(t){n+=1}return e};";

            engine.eval(script_js);

            Invocable invocable = (Invocable) engine;

            Object result = invocable.invokeFunction("getJsonLibrariesDownloadsClassifiersNativesY", content, natives_OS);

            for (String retval : result.toString().split("\n")) {
                if (!retval.isEmpty()) version_path_list_natives.add(retval);
            }
        } catch (FileNotFoundException | ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    public void readJson_libraries_downloads_classifiers_natives_Y(String path) {

        try {
            String natives_OS;
            if (getOS().equals("Linux")) {
                natives_OS = "linux";
            } else if (getOS().equals("Windows")) {
                natives_OS = "windows";
            } else if (getOS().equals("Mac")) {
                natives_OS =  "osx";
            } else {
                natives_OS = "N/A";
            }
            String content = new Scanner(new File(path)).useDelimiter("\\Z").next();
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
            try {

                String script_js = "var getJsonLibrariesDownloadsClassifiersNativesX=function(r,s){var a=r,e=JSON.parse(a),n=\"\",t=0;for(i=0;i<500;i++)try{n=n+e.libraries[t].downloads.classifiers[s].url+\"\\n\",t+=1}catch(o){t+=1}return n},getJsonLibrariesDownloadsClassifiersNativesY=function(s,r){var a=JSON.parse(s),e=\"\",t=0;for(i=0;i<a.libraries.length+1;i++)try{e+=a.libraries[t].classifies[\"windows\"].path + \"\\n\",t+=1}catch(i){t+=1}return e},getJsonLibrariesDownloadsClassifiersNativesZ=function(r){var s=r,a=JSON.parse(s),e=\"\",n=0;for(i=0;i<500;i++)try{a.libraries[n].natives?(e=e+a.libraries[n].classifies +\"\\n\",n+=1):n+=1}catch(t){n+=1}return e};";

                File file = new File("./.script.js");
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(script_js);
                bw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            engine.eval(new FileReader("./.script.js"));

            Invocable invocable = (Invocable) engine;

            Object result = invocable.invokeFunction("getJsonLibrariesDownloadsClassifiersNativesY", content, natives_OS);

            for (String retval : result.toString().split("\n")) {
                version_path_list_natives.add(retval);
            }
        } catch (FileNotFoundException | ScriptException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    /***** Util Methods  *****/

    /***** FADES   *****/

    public void fadeIn(ImageView current_fading) {
        current_fading.setOnMouseEntered((event) -> {
            if (current_fading == play_button) {
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), play_button);
                fadeIn.setFromValue(0.7);
                fadeIn.setToValue(1);
                fadeIn.play();
            } else {
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), current_fading);
                fadeIn.setFromValue(1.0);
                fadeIn.setToValue(0.0);
                fadeIn.play();
            }
        });
    }

    public void fadeOut(ImageView current_fading) {
        current_fading.setOnMouseExited((event) -> {
            if (current_fading == play_button) {
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), play_button);
                fadeIn.setFromValue(1);
                fadeIn.setToValue(0.7);
                fadeIn.play();
            } else {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), current_fading);
                fadeOut.setFromValue(0.0);
                fadeOut.setToValue(1.0);
                fadeOut.play();
            }
        });
    }

    /***** FADES   *****/
}


