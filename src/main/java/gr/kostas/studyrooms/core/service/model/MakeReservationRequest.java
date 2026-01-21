package gr.kostas.studyrooms.core.service.model;

import gr.kostas.studyrooms.core.model.Room;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record MakeReservationRequest (
    @NotNull @Positive Long studentId,
    @NotNull  Instant reservedfor,
    @NotNull Room room
) {}

