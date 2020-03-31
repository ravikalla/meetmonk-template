package in.ravikalla.meetmonk.repository;

import in.ravikalla.meetmonk.domain.Appointments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Appointments entity.
 */
@Repository
public interface AppointmentsRepository extends JpaRepository<Appointments, Long>, JpaSpecificationExecutor<Appointments> {

    @Query(value = "select distinct appointments from Appointments appointments left join fetch appointments.users",
        countQuery = "select count(distinct appointments) from Appointments appointments")
    Page<Appointments> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct appointments from Appointments appointments left join fetch appointments.users")
    List<Appointments> findAllWithEagerRelationships();

    @Query("select appointments from Appointments appointments left join fetch appointments.users where appointments.id =:id")
    Optional<Appointments> findOneWithEagerRelationships(@Param("id") Long id);

}
