package gr.kostas.studyrooms.core.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
        name = "reservation",
        indexes = {
                @Index(name = "idx_reservation_status", columnList = "status"),
                @Index(name = "idx_reservation_student", columnList = "student_id"),
                @Index(name = "idx_reservation_sent_at", columnList = "sent"),
                @Index(name = "idx_reservation_reserved_for", columnList = "reserve"),
                @Index(name = "idx_reservation_roomId", columnList = "roomId")
        }
)
public final class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="id")
    private long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private ReservationStatus status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_ticket_student"))
    private Person student;


    @CreationTimestamp
    @Column(name = "sent", nullable = false, updatable = false)
    private Instant sent;
    
    @Column(name = "reserve")
    private Instant reserve;

    @Column(name = "roomId")
    private long roomId;

    public Reservation(long id, ReservationStatus status, Person student,
                       Instant sent, Instant reserve,long roomId) {
        this.id = id;
        this.status = status;
        this.student = student;
        this.sent = sent;
        this.reserve = reserve;
        this.roomId = roomId;
    }

    public Reservation() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Person getStudent() {
        return student;
    }

    public void setStudent(Person student) {
        this.student = student;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }



    public Instant getSent() {
        return sent;
    }

    public void setSent(Instant sent) {
        this.sent = sent;
    }

    public Instant getReserve() {
        return reserve;
    }

    public void setReserve(Instant reserve) {
        this.reserve = reserve;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Reservation{");
        sb.append("id=").append(id);
        sb.append(", status=").append(status);
        sb.append(", student=").append(student);
        sb.append(", sent=").append(sent);
        sb.append(", reserve=").append(reserve);
        sb.append('}');
        return sb.toString();
    }
}
