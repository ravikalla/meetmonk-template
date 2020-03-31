package in.ravikalla.meetmonk.web.rest;

import in.ravikalla.meetmonk.service.AppointmentsService;
import in.ravikalla.meetmonk.web.rest.errors.BadRequestAlertException;
import in.ravikalla.meetmonk.service.dto.AppointmentsDTO;
import in.ravikalla.meetmonk.service.dto.AppointmentsCriteria;
import in.ravikalla.meetmonk.service.AppointmentsQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link in.ravikalla.meetmonk.domain.Appointments}.
 */
@RestController
@RequestMapping("/api")
public class AppointmentsResource {

    private final Logger log = LoggerFactory.getLogger(AppointmentsResource.class);

    private static final String ENTITY_NAME = "appointments";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AppointmentsService appointmentsService;

    private final AppointmentsQueryService appointmentsQueryService;

    public AppointmentsResource(AppointmentsService appointmentsService, AppointmentsQueryService appointmentsQueryService) {
        this.appointmentsService = appointmentsService;
        this.appointmentsQueryService = appointmentsQueryService;
    }

    /**
     * {@code POST  /appointments} : Create a new appointments.
     *
     * @param appointmentsDTO the appointmentsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new appointmentsDTO, or with status {@code 400 (Bad Request)} if the appointments has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/appointments")
    public ResponseEntity<AppointmentsDTO> createAppointments(@Valid @RequestBody AppointmentsDTO appointmentsDTO) throws URISyntaxException {
        log.debug("REST request to save Appointments : {}", appointmentsDTO);
        if (appointmentsDTO.getId() != null) {
            throw new BadRequestAlertException("A new appointments cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AppointmentsDTO result = appointmentsService.save(appointmentsDTO);
        return ResponseEntity.created(new URI("/api/appointments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /appointments} : Updates an existing appointments.
     *
     * @param appointmentsDTO the appointmentsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appointmentsDTO,
     * or with status {@code 400 (Bad Request)} if the appointmentsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the appointmentsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/appointments")
    public ResponseEntity<AppointmentsDTO> updateAppointments(@Valid @RequestBody AppointmentsDTO appointmentsDTO) throws URISyntaxException {
        log.debug("REST request to update Appointments : {}", appointmentsDTO);
        if (appointmentsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        AppointmentsDTO result = appointmentsService.save(appointmentsDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, appointmentsDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /appointments} : get all the appointments.
     *

     * @param pageable the pagination information.

     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of appointments in body.
     */
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentsDTO>> getAllAppointments(AppointmentsCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Appointments by criteria: {}", criteria);
        Page<AppointmentsDTO> page = appointmentsQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * {@code GET  /appointments/count} : count all the appointments.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
    */
    @GetMapping("/appointments/count")
    public ResponseEntity<Long> countAppointments(AppointmentsCriteria criteria) {
        log.debug("REST request to count Appointments by criteria: {}", criteria);
        return ResponseEntity.ok().body(appointmentsQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /appointments/:id} : get the "id" appointments.
     *
     * @param id the id of the appointmentsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the appointmentsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/appointments/{id}")
    public ResponseEntity<AppointmentsDTO> getAppointments(@PathVariable Long id) {
        log.debug("REST request to get Appointments : {}", id);
        Optional<AppointmentsDTO> appointmentsDTO = appointmentsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(appointmentsDTO);
    }

    /**
     * {@code DELETE  /appointments/:id} : delete the "id" appointments.
     *
     * @param id the id of the appointmentsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<Void> deleteAppointments(@PathVariable Long id) {
        log.debug("REST request to delete Appointments : {}", id);
        appointmentsService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
