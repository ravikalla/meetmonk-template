package in.ravikalla.meetmonk.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import in.ravikalla.meetmonk.domain.Appointments;
import in.ravikalla.meetmonk.domain.*; // for static metamodels
import in.ravikalla.meetmonk.repository.AppointmentsRepository;
import in.ravikalla.meetmonk.service.dto.AppointmentsCriteria;
import in.ravikalla.meetmonk.service.dto.AppointmentsDTO;
import in.ravikalla.meetmonk.service.mapper.AppointmentsMapper;

/**
 * Service for executing complex queries for {@link Appointments} entities in the database.
 * The main input is a {@link AppointmentsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link AppointmentsDTO} or a {@link Page} of {@link AppointmentsDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AppointmentsQueryService extends QueryService<Appointments> {

    private final Logger log = LoggerFactory.getLogger(AppointmentsQueryService.class);

    private final AppointmentsRepository appointmentsRepository;

    private final AppointmentsMapper appointmentsMapper;

    public AppointmentsQueryService(AppointmentsRepository appointmentsRepository, AppointmentsMapper appointmentsMapper) {
        this.appointmentsRepository = appointmentsRepository;
        this.appointmentsMapper = appointmentsMapper;
    }

    /**
     * Return a {@link List} of {@link AppointmentsDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<AppointmentsDTO> findByCriteria(AppointmentsCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Appointments> specification = createSpecification(criteria);
        return appointmentsMapper.toDto(appointmentsRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link AppointmentsDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AppointmentsDTO> findByCriteria(AppointmentsCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Appointments> specification = createSpecification(criteria);
        return appointmentsRepository.findAll(specification, page)
            .map(appointmentsMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AppointmentsCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Appointments> specification = createSpecification(criteria);
        return appointmentsRepository.count(specification);
    }

    /**
     * Function to convert {@link AppointmentsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Appointments> createSpecification(AppointmentsCriteria criteria) {
        Specification<Appointments> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Appointments_.id));
            }
            if (criteria.getSubject() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSubject(), Appointments_.subject));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Appointments_.description));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), Appointments_.date));
            }
            if (criteria.getDuration() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDuration(), Appointments_.duration));
            }
            if (criteria.getNotes() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNotes(), Appointments_.notes));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildSpecification(criteria.getUserId(),
                    root -> root.join(Appointments_.users, JoinType.LEFT).get(User_.id)));
            }
        }
        return specification;
    }
}
