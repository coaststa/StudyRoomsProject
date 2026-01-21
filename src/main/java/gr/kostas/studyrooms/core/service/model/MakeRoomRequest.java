package gr.kostas.studyrooms.core.service.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.aspectj.weaver.ast.Not;

import java.time.LocalTime;

public record MakeRoomRequest(
        @NotNull @Positive
        int capacity,

        @NotNull LocalTime openingtime,
        @NotNull LocalTime closingtime
) {
}
