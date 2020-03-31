package in.ravikalla.meetmonk.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class AppointmentsMapperTest {

    private AppointmentsMapper appointmentsMapper;

    @BeforeEach
    public void setUp() {
        appointmentsMapper = new AppointmentsMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 2L;
        assertThat(appointmentsMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(appointmentsMapper.fromId(null)).isNull();
    }
}
