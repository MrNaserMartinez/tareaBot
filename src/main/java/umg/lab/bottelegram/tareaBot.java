package umg.lab.bottelegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class tareaBot extends TelegramLongPollingBot {

        @Override
        public String getBotUsername() {
            return "lucariobeta_bot";
        }

        @Override
        public String getBotToken() {
            return "7045254699:AAE0i7lE99P_UDypt9PJ4v9L4dS4H-TbHVE";
        }

        @Override
        public void onUpdateReceived(Update update) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                long chatId = update.getMessage().getChatId();
                System.out.println("User id: " + chatId + " Message: " + messageText);

                if (messageText.equals("/info")) {
                    sendText(chatId, "Carnet: 0905-23-3623\nNombre: Naser Daniel Martinez Morales\nSemestre: 4to Semestre");
                } else if (messageText.equals("/progra")) {
                    sendText(chatId, "La clase de programación es interesante y desafiante.");
                } else if (messageText.equals("/hola")) {
                    String nombreUsuario = update.getMessage().getFrom().getFirstName();
                    LocalDateTime ahora = LocalDateTime.now();
                    DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy, HH:mm:ss");
                    String fechaFormateada = ahora.format(formatoFecha);
                    sendText(chatId, "Hola " + nombreUsuario + ", hoy es " + fechaFormateada);
                } else if (messageText.startsWith("/cambio")) {
                    try {
                        String[] partes = messageText.split(" ");
                        double euros = Double.parseDouble(partes[1]);
                        double tipoCambio = 8.91; // Este es un valor de ejemplo, investiga el valor actual.
                        double quetzales = euros * tipoCambio;
                        sendText(chatId, euros + " Euros son " + quetzales + " Quetzales.");
                    } catch (Exception e) {
                        sendText(chatId, "Por favor ingresa un valor válido para el cambio.");
                    }
                } else if (messageText.startsWith("/grupal")) {
                    String mensaje = messageText.substring(8).trim();
                    List<Long> listaChats = List.of(6597569075L, 6688363556L, 1533824724L); // Reemplaza con los IDs de tus compañeros
                    for (Long id : listaChats) {
                        sendText(id, mensaje);
                    }
                } else {
                    sendUnknownCommandResponse(chatId);
                }
            }
        }

        private void sendUnknownCommandResponse(long chatId) {
            String response = "Hola, no reconozco ese comando. Por favor, ingresa cualquiera de los siguientes comandos:\n"
                    + "/info - Obtener tu información personal\n"
                    + "/progra - Comentarios sobre la clase de programación\n"
                    + "/hola - Recibir un saludo con la fecha y hora actual\n"
                    + "/cambio [cantidad] - Convertir euros a quetzales\n"
                    + "/grupal [mensaje] - Enviar un mensaje a tus compañeros";
            sendText(chatId, response);
        }

        public void sendText(Long who, String what) {
            SendMessage sm = SendMessage.builder()
                    .chatId(who.toString())
                    .text(what).build();
            try {
                execute(sm);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }