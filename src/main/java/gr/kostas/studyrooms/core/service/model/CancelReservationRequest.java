package gr.kostas.studyrooms.core.service.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CancelReservationRequest(@NotNull @Positive Long id) {}
