package io.github.mac_genius.lobbymanager.Commands.Cloak;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Created by Mac on 10/30/2015.
 */
public class FetchProfile {
    private String name;
    private UUID uuid;
    private GameProfile profile;
    private Player player;

    public FetchProfile(String name, Player player) {
        this.name = name;
        this.player = player;
        uuid = fetchUuid();
        profile = new GameProfile(uuid, name);
        retrieveProperties();
    }

    public GameProfile getProfile() {
        return profile;
    }

    public UUID fetchUuid() {
        try {
            URL api = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            URLConnection connection = api.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setDefaultUseCaches(false);
            String json = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8"))).readLine();
            JSONParser parser = new JSONParser();
            Object o = parser.parse(json);
            JSONObject object = (JSONObject) o;
            String uuid = (String) object.get("id");
            return UUID.fromString(uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32));
        } catch (IOException | ParseException | NullPointerException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.DARK_PURPLE + "[Cloak] " + ChatColor.WHITE + "You couldn't be cloaked!");
            return null;
        }
    }

    public void retrieveProperties() {
        try {
            URL api = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replaceAll("-", "") + "?unsigned=false");
            URLConnection connection = api.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setDefaultUseCaches(false);
            String json = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8"))).readLine();
            JSONParser parser = new JSONParser();
            Object o = parser.parse(json);
            JSONArray propeties = (JSONArray) ((JSONObject) o).get("properties");
            for (int i = 0; i < propeties.size(); i++) {
                JSONObject object = (JSONObject) propeties.get(i);
                String name = (String) object.get("name");
                String value = (String) object.get("value");
                String signature = object.containsKey("signature") ? (String) object.get("signature") : null;
                profile.getProperties().put(name, new Property(name, value, signature));
            }
        } catch (IOException | ParseException | NullPointerException e) {
            e.printStackTrace();
        }
    }
}
