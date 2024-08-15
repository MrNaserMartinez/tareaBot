package umg.lab;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import umg.lab.bottelegram.botito;
import umg.lab.bottelegram.tareaBot;

public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            tareaBot ana = new tareaBot();
            botsApi.registerBot(ana);
            System.out.println("Esto es un bot funcionando . . . . ");
        }
        catch (Exception ex) {
            System.out.println("Error"+ex.getMessage());
        }

    }
}