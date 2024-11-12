package fr.webhookdiscord;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public final class WebhookDiscord extends JavaPlugin implements Listener {

    private String webhookUrl;
    private List<String> commandList;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

        commandList = getConfig().getStringList("commands");
        webhookUrl = getConfig().getString("webhook_url");

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().substring(1);  // Retirer le '/' de la commande

        // Vérifie si la commande fait partie de la liste des commandes configurées
        if (commandList.contains(command)) {
            sendWebhookNotification(event.getPlayer().getName() + " a exécuté la commande /" + command);
        }
    }

    private void sendWebhookNotification(String message) {
        try {
            // Créer une URL pour le webhook Discord
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            // Créer le message au format JSON
            String jsonPayload = "{\"content\": \"" + message + "\"}";

            // Envoyer le message
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Vérifier la réponse
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.out.println("Erreur lors de l'envoi du message : " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
