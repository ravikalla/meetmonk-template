package in.ravikalla.meetmonk.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import in.ravikalla.meetmonk.web.rest.TestUtil;

public class AppointmentsDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppointmentsDTO.class);
        AppointmentsDTO appointmentsDTO1 = new AppointmentsDTO();
        appointmentsDTO1.setId(1L);
        AppointmentsDTO appointmentsDTO2 = new AppointmentsDTO();
        assertThat(appointmentsDTO1).isNotEqualTo(appointmentsDTO2);
        appointmentsDTO2.setId(appointmentsDTO1.getId());
        assertThat(appointmentsDTO1).isEqualTo(appointmentsDTO2);
        appointmentsDTO2.setId(2L);
        assertThat(appointmentsDTO1).isNotEqualTo(appointmentsDTO2);
        appointmentsDTO1.setId(null);
        assertThat(appointmentsDTO1).isNotEqualTo(appointmentsDTO2);
    }
}
