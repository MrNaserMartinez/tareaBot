package umg.lab.dao;

import umg.lab.model.Respuesta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDaoCuestionario {
    private final Connection connection;

    public UserDaoCuestionario(Connection connection) {
        this.connection = connection;
    }

    public void guardarRespuesta(Respuesta respuesta) throws SQLException {
        String sql = "INSERT INTO tb_respuestas (seccion, telegram_id, pregunta_id, respuesta_texto) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, respuesta.getSeccion());
            stmt.setLong(2, respuesta.getTelegramId());
            stmt.setInt(3, respuesta.getPreguntaId());
            stmt.setString(4, respuesta.getRespuestaTexto());
            stmt.executeUpdate();
        }
    }
}
