package gr.kostas.studyrooms.core.service.model;

import gr.kostas.studyrooms.core.model.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CompleteReservationRequest(
        @NotNull @Positive Long id,
        @NotNull ReservationStatus reservationStatus
        ) {}