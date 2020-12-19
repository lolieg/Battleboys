package me.lolieg.battleboys.mobs;


import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.server.v1_16_R3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

import static me.lolieg.battleboys.utils.Utility.formatString;

public class Boss extends EntityPlayer {

    public Boss(MinecraftServer minecraftserver, WorldServer worldserver, String name) {
        super(minecraftserver, worldserver, new GameProfile(UUID.randomUUID(), formatString(name)), new PlayerInteractManager(worldserver));
        setInvulnerable(true);
    }

    public boolean setSkin(UUID uuid) {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", uuid.toString().replace("-", ""))).openConnection();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                JSONObject properties = (JSONObject) ((JSONArray) jsonObject.get("properties")).get(0);
                String skin = (String) properties.get("value");
                String signature = (String) properties.get("signature");
                getProfile().getProperties().put("textures", new Property("textures", skin, signature));
                return true;
            } else {
                System.out.println("Connection could not be opened (Response code " + connection.getResponseCode() + ", " + connection.getResponseMessage() + ")");
                return false;
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
