package in.ravikalla.meetmonk.service;

import in.ravikalla.meetmonk.domain.Appointments;
import in.ravikalla.meetmonk.repository.AppointmentsRepository;
import in.ravikalla.meetmonk.service.dto.AppointmentsDTO;
import in.ravikalla.meetmonk.service.mapper.AppointmentsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Appointments}.
 */
@Service
@Transactional
public class AppointmentsService {

    private final Logger log = LoggerFactory.getLogger(AppointmentsService.class);

    private final AppointmentsRepository appointmentsRepository;

    private final AppointmentsMapper appointmentsMapper;

    public AppointmentsService(AppointmentsRepository appointmentsRepository, AppointmentsMapper appointmentsMapper) {
        this.appointmentsRepository = appointmentsRepository;
        this.appointmentsMapper = appointmentsMapper;
    }

    /**
     * Save a appointments.
     *
     * @param appointmentsDTO the entity to save.
     * @return the persisted entity.
     */
    public AppointmentsDTO save(AppointmentsDTO appointmentsDTO) {
        log.debug("Request to save Appointments : {}", appointmentsDTO);
        Appointments appointments = appointmentsMapper.toEntity(appointmentsDTO);
        appointments = appointmentsRepository.save(appointments);
        return appointmentsMapper.toDto(appointments);
    }

    /**
     * Get all the appointments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AppointmentsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Appointments");
        return appointmentsRepository.findAll(pageable)
            .map(appointmentsMapper::toDto);
    }

    /**
     * Get all the appointments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<AppointmentsDTO> findAllWithEagerRelationships(Pageable pageable) {
        return appointmentsRepository.findAllWithEagerRelationships(pageable).map(appointmentsMapper::toDto);
    }
    

    /**
     * Get one appointments by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AppointmentsDTO> findOne(Long id) {
        log.debug("Request to get Appointments : {}", id);
        return appointmentsRepository.findOneWithEagerRelationships(id)
            .map(appointmentsMapper::toDto);
    }

    /**
     * Delete the appointments by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Appointments : {}", id);
        appointmentsRepository.deleteById(id);
    }
}
