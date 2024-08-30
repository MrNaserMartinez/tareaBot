package umg.lab.service;

import umg.lab.model.Respuesta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class RespuestaService {
    private final Connection connection;

    public RespuestaService(Connection connection) {
        this.connection = connection;
    }

    public void guardarRespuesta(Respuesta respuesta) throws SQLException {
        String sql = "INSERT INTO tb_respuestas (seccion, telegram_id, pregunta_id, respuesta_texto, fecha_respuesta) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, respuesta.getSeccion());
            stmt.setLong(2, respuesta.getTelegramId());
            stmt.setInt(3, respuesta.getPreguntaId());
            stmt.setString(4, respuesta.getRespuestaTexto());
            stmt.setString(5, respuesta.getFechaRespuesta().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            stmt.executeUpdate();
        }
    }
}