package in.ravikalla.meetmonk.service.dto;
import java.time.ZonedDateTime;
import java.time.Duration;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A DTO for the {@link in.ravikalla.meetmonk.domain.Appointments} entity.
 */
public class AppointmentsDTO implements Serializable {

    private Long id;

    private String subject;

    private String description;

    @NotNull
    private ZonedDateTime date;

    @NotNull
    private Duration duration;

    private String notes;


    private Set<UserDTO> users = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Set<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(Set<UserDTO> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AppointmentsDTO appointmentsDTO = (AppointmentsDTO) o;
        if (appointmentsDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), appointmentsDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "AppointmentsDTO{" +
            "id=" + getId() +
            ", subject='" + getSubject() + "'" +
            ", description='" + getDescription() + "'" +
            ", date='" + getDate() + "'" +
            ", duration='" + getDuration() + "'" +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}
