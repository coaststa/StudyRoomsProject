package gr.kostas.studyrooms.web.ui.model;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record MakeRoomForm(
        @NotNull int capacity,
        @NotNull LocalTime openingtime,
        @NotNull LocalTime closingtime
) { }
