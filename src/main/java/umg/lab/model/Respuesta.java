package umg.lab.model;

import java.time.LocalDateTime;

public class Respuesta {
    private String seccion;
    private long telegramId;
    private int preguntaId;
    private String respuestaTexto;
    private LocalDateTime fechaRespuesta;

    public Respuesta(String seccion, long telegramId, int preguntaId, String respuestaTexto, LocalDateTime fechaRespuesta) {
        this.seccion = seccion;
        this.telegramId = telegramId;
        this.preguntaId = preguntaId;
        this.respuestaTexto = respuestaTexto;
        this.fechaRespuesta = fechaRespuesta;
    }

    public Respuesta() {

    }

    // Getters y setters
    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(long telegramId) {
        this.telegramId = telegramId;
    }

    public int getPreguntaId() {
        return preguntaId;
    }

    public void setPreguntaId(int preguntaId) {
        this.preguntaId = preguntaId;
    }

    public String getRespuestaTexto() {
        return respuestaTexto;
    }

    public void setRespuestaTexto(String respuestaTexto) {
        this.respuestaTexto = respuestaTexto;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }
}