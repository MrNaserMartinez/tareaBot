package umg.lab.bottelegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import umg.lab.dao.UserDaoCuestionario;
import umg.lab.model.Respuesta;
import umg.lab.service.UserService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotCuestionario extends TelegramLongPollingBot {
    private final Map<Long, Integer> indicePregunta = new HashMap<>();
    private final Map<Long, String> seccionActiva = new HashMap<>();
    private final Map<String, String[]> preguntas = new HashMap<>();
    private final Map<Long, String> respuestasPendientes = new HashMap<>();
    private final UserDaoCuestionario userDaoCuestionario;

    // Constructor con la configuración de la base de datos
    public BotCuestionario() {
        // Configura la conexión a la base de datos
        Connection connection;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tb_respuestas", "root", "trabajosmysql12");

            userDaoCuestionario = new UserDaoCuestionario(connection);
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar a la base de datos", e);
        }

        // Inicializa los cuestionarios con las preguntas.
        preguntas.put("SECTION_1", new String[]{"🤦‍♂️1.1- Estas aburrido?", "😂😂 1.2- Te bañaste hoy?", "🤡🤡 Pregunta 1.3"});
        preguntas.put("SECTION_2", new String[]{"Pregunta 2.1", "2.2 Cuantos anios tienes?", "Pregunta 2.3"});
        preguntas.put("SECTION_3", new String[]{"Pregunta 3.1", "Pregunta 3.2", "Pregunta 3.3"});
        preguntas.put("SECTION_4", new String[]{"4.1 ¿cómo te llamas?", "4.2 Cuántos años tienes?", "4.3 qué comida te gusta?", "4.4 que fino, eres hombre o mujer?", "4.5 a que chido, y tienes pareja?", "4.6 el admin busca una si eres mujer, aceptas? y por que no?"});
    }

    @Override
    public String getBotUsername() {
        return "botilin_bot";
    }

    @Override
    public String getBotToken() {
        return "7054353997:AAGJIEGPdszEd1D32ENvXJnG7jBujoOK5n0";
    }

    @Override
    public void onUpdateReceived(Update actualizacion) {
        if (actualizacion.hasMessage() && actualizacion.getMessage().hasText()) {
            String messageText = actualizacion.getMessage().getText();
            long chatId = actualizacion.getMessage().getChatId();

            if (messageText.equals("/menu")) {
                sendMenu(chatId);
            } else if (respuestasPendientes.containsKey(chatId)) {
                validarEdad(chatId, messageText);
            } else if (seccionActiva.containsKey(chatId)) {
                manejaCuestionario(chatId, messageText);
            } else {
                sendWelcomeMessage(chatId);
            }
        } else if (actualizacion.hasCallbackQuery()) {
            String callbackData = actualizacion.getCallbackQuery().getData();
            long chatId = actualizacion.getCallbackQuery().getMessage().getChatId();
            inicioCuestionario(chatId, callbackData);
        }
    }

    private void sendMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Selecciona una sección:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Crea los botones del menú
        rows.add(crearFilaBoton("Sección 1", "SECTION_1"));
        rows.add(crearFilaBoton("Sección 2", "SECTION_2"));
        rows.add(crearFilaBoton("Sección 3", "SECTION_3"));
        rows.add(crearFilaBoton("Sección 4", "SECTION_4"));

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private List<InlineKeyboardButton> crearFilaBoton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button);
        return row;
    }

    private void inicioCuestionario(long chatId, String section) {
        seccionActiva.put(chatId, section);
        indicePregunta.put(chatId, 0);
        enviarPregunta(chatId);
    }

    private void enviarPregunta(long chatId) {
        String seccion = seccionActiva.get(chatId);
        int index = indicePregunta.get(chatId);
        String[] questions = preguntas.get(seccion);

        if (index < questions.length) {
            sendText(chatId, questions[index]);
            if (seccion.equals("SECTION_4") && index == 1) {
                respuestasPendientes.put(chatId, "VALIDANDO_EDAD");
            }
        } else {
            sendText(chatId, "¡Has completado el cuestionario! si quieres volver a comenzar usa /menu");
            seccionActiva.remove(chatId);
            indicePregunta.remove(chatId);
        }
    }

    private void validarEdad(long chatId, String response) {
        try {
            int edad = Integer.parseInt(response);
            if (edad < 0 || edad > 120) {
                sendText(chatId, "Por favor ingresa una edad válida.");
            } else {
                sendText(chatId, "Gracias por proporcionar tu edad.");
                respuestasPendientes.remove(chatId);
                manejaCuestionario(chatId, response);
            }
        } catch (NumberFormatException e) {
            sendText(chatId, "Por favor ingresa un número válido para la edad.");
        }
    }

    private void manejaCuestionario(long chatId, String response) {
        String section = seccionActiva.get(chatId);
        int index = indicePregunta.get(chatId);

        // Guardar la respuesta en la base de datos
        saveRespuesta(chatId, section, index, response);

        sendText(chatId, "Tu respuesta fue: " + response);
        indicePregunta.put(chatId, index + 1);

        enviarPregunta(chatId);
    }

    private void saveRespuesta(long chatId, String section, int questionIndex, String response) {
        Respuesta respuesta = new Respuesta();
        respuesta.setSeccion(section);
        respuesta.setTelegramId(chatId);
        respuesta.setPreguntaId(questionIndex);
        respuesta.setRespuestaTexto(response);

        try {
            userDaoCuestionario.guardarRespuesta(respuesta);
        } catch (SQLException e) {
            e.printStackTrace();
            sendText(chatId, "Ocurrió un error al guardar la respuesta.");
        }
    }

    private void sendText(Long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendWelcomeMessage(long chatId) {
        String welcomeText = "Hola! Bienvenido a nuestro bot de cuestionarios. " +
                "Para comenzar, por favor utiliza el comando /menu para seleccionar una sección.";
        sendText(chatId, welcomeText);
    }
}
