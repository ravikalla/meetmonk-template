package in.ravikalla.meetmonk.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import in.ravikalla.meetmonk.web.rest.TestUtil;

public class AppointmentsTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Appointments.class);
        Appointments appointments1 = new Appointments();
        appointments1.setId(1L);
        Appointments appointments2 = new Appointments();
        appointments2.setId(appointments1.getId());
        assertThat(appointments1).isEqualTo(appointments2);
        appointments2.setId(2L);
        assertThat(appointments1).isNotEqualTo(appointments2);
        appointments1.setId(null);
        assertThat(appointments1).isNotEqualTo(appointments2);
    }
}
