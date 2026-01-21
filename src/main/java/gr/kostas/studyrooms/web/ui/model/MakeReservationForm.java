package gr.kostas.studyrooms.web.ui.model;

import gr.kostas.studyrooms.core.model.Room;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record MakeReservationForm(
        @NotNull Long roomId,
        @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime reservedFor
) {}