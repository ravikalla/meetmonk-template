package in.ravikalla.meetmonk.service.mapper;

import in.ravikalla.meetmonk.domain.*;
import in.ravikalla.meetmonk.service.dto.AppointmentsDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Appointments} and its DTO {@link AppointmentsDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface AppointmentsMapper extends EntityMapper<AppointmentsDTO, Appointments> {


    @Mapping(target = "removeUser", ignore = true)

    default Appointments fromId(Long id) {
        if (id == null) {
            return null;
        }
        Appointments appointments = new Appointments();
        appointments.setId(id);
        return appointments;
    }
}
