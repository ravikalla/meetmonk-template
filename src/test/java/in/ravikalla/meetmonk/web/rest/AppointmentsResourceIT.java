package in.ravikalla.meetmonk.web.rest;

import in.ravikalla.meetmonk.MeetmonkApp;
import in.ravikalla.meetmonk.domain.Appointments;
import in.ravikalla.meetmonk.domain.User;
import in.ravikalla.meetmonk.repository.AppointmentsRepository;
import in.ravikalla.meetmonk.service.AppointmentsService;
import in.ravikalla.meetmonk.service.dto.AppointmentsDTO;
import in.ravikalla.meetmonk.service.mapper.AppointmentsMapper;
import in.ravikalla.meetmonk.web.rest.errors.ExceptionTranslator;
import in.ravikalla.meetmonk.service.dto.AppointmentsCriteria;
import in.ravikalla.meetmonk.service.AppointmentsQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static in.ravikalla.meetmonk.web.rest.TestUtil.sameInstant;
import static in.ravikalla.meetmonk.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link AppointmentsResource} REST controller.
 */
@SpringBootTest(classes = MeetmonkApp.class)
public class AppointmentsResourceIT {

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Duration DEFAULT_DURATION = Duration.ofHours(6);
    private static final Duration UPDATED_DURATION = Duration.ofHours(12);
    private static final Duration SMALLER_DURATION = Duration.ofHours(5);

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    @Autowired
    private AppointmentsRepository appointmentsRepository;

    @Mock
    private AppointmentsRepository appointmentsRepositoryMock;

    @Autowired
    private AppointmentsMapper appointmentsMapper;

    @Mock
    private AppointmentsService appointmentsServiceMock;

    @Autowired
    private AppointmentsService appointmentsService;

    @Autowired
    private AppointmentsQueryService appointmentsQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restAppointmentsMockMvc;

    private Appointments appointments;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AppointmentsResource appointmentsResource = new AppointmentsResource(appointmentsService, appointmentsQueryService);
        this.restAppointmentsMockMvc = MockMvcBuilders.standaloneSetup(appointmentsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Appointments createEntity(EntityManager em) {
        Appointments appointments = new Appointments()
            .subject(DEFAULT_SUBJECT)
            .description(DEFAULT_DESCRIPTION)
            .date(DEFAULT_DATE)
            .duration(DEFAULT_DURATION)
            .notes(DEFAULT_NOTES);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        appointments.getUsers().add(user);
        return appointments;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Appointments createUpdatedEntity(EntityManager em) {
        Appointments appointments = new Appointments()
            .subject(UPDATED_SUBJECT)
            .description(UPDATED_DESCRIPTION)
            .date(UPDATED_DATE)
            .duration(UPDATED_DURATION)
            .notes(UPDATED_NOTES);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        appointments.getUsers().add(user);
        return appointments;
    }

    @BeforeEach
    public void initTest() {
        appointments = createEntity(em);
    }

    @Test
    @Transactional
    public void createAppointments() throws Exception {
        int databaseSizeBeforeCreate = appointmentsRepository.findAll().size();

        // Create the Appointments
        AppointmentsDTO appointmentsDTO = appointmentsMapper.toDto(appointments);
        restAppointmentsMockMvc.perform(post("/api/appointments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(appointmentsDTO)))
            .andExpect(status().isCreated());

        // Validate the Appointments in the database
        List<Appointments> appointmentsList = appointmentsRepository.findAll();
        assertThat(appointmentsList).hasSize(databaseSizeBeforeCreate + 1);
        Appointments testAppointments = appointmentsList.get(appointmentsList.size() - 1);
        assertThat(testAppointments.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testAppointments.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAppointments.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testAppointments.getDuration()).isEqualTo(DEFAULT_DURATION);
        assertThat(testAppointments.getNotes()).isEqualTo(DEFAULT_NOTES);
    }

    @Test
    @Transactional
    public void createAppointmentsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = appointmentsRepository.findAll().size();

        // Create the Appointments with an existing ID
        appointments.setId(1L);
        AppointmentsDTO appointmentsDTO = appointmentsMapper.toDto(appointments);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAppointmentsMockMvc.perform(post("/api/appointments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(appointmentsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Appointments in the database
        List<Appointments> appointmentsList = appointmentsRepository.findAll();
        assertThat(appointmentsList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = appointmentsRepository.findAll().size();
        // set the field null
        appointments.setDate(null);

        // Create the Appointments, which fails.
        AppointmentsDTO appointmentsDTO = appointmentsMapper.toDto(appointments);

        restAppointmentsMockMvc.perform(post("/api/appointments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(appointmentsDTO)))
            .andExpect(status().isBadRequest());

        List<Appointments> appointmentsList = appointmentsRepository.findAll();
        assertThat(appointmentsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDurationIsRequired() throws Exception {
        int databaseSizeBeforeTest = appointmentsRepository.findAll().size();
        // set the field null
        appointments.setDuration(null);

        // Create the Appointments, which fails.
        AppointmentsDTO appointmentsDTO = appointmentsMapper.toDto(appointments);

        restAppointmentsMockMvc.perform(post("/api/appointments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(appointmentsDTO)))
            .andExpect(status().isBadRequest());

        List<Appointments> appointmentsList = appointmentsRepository.findAll();
        assertThat(appointmentsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAppointments() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList
        restAppointmentsMockMvc.perform(get("/api/appointments?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appointments.getId().intValue())))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllAppointmentsWithEagerRelationshipsIsEnabled() throws Exception {
        AppointmentsResource appointmentsResource = new AppointmentsResource(appointmentsServiceMock, appointmentsQueryService);
        when(appointmentsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restAppointmentsMockMvc = MockMvcBuilders.standaloneSetup(appointmentsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restAppointmentsMockMvc.perform(get("/api/appointments?eagerload=true"))
        .andExpect(status().isOk());

        verify(appointmentsServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllAppointmentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        AppointmentsResource appointmentsResource = new AppointmentsResource(appointmentsServiceMock, appointmentsQueryService);
            when(appointmentsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restAppointmentsMockMvc = MockMvcBuilders.standaloneSetup(appointmentsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restAppointmentsMockMvc.perform(get("/api/appointments?eagerload=true"))
        .andExpect(status().isOk());

            verify(appointmentsServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getAppointments() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get the appointments
        restAppointmentsMockMvc.perform(get("/api/appointments/{id}", appointments.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(appointments.getId().intValue()))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }


    @Test
    @Transactional
    public void getAppointmentsByIdFiltering() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        Long id = appointments.getId();

        defaultAppointmentsShouldBeFound("id.equals=" + id);
        defaultAppointmentsShouldNotBeFound("id.notEquals=" + id);

        defaultAppointmentsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAppointmentsShouldNotBeFound("id.greaterThan=" + id);

        defaultAppointmentsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAppointmentsShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllAppointmentsBySubjectIsEqualToSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where subject equals to DEFAULT_SUBJECT
        defaultAppointmentsShouldBeFound("subject.equals=" + DEFAULT_SUBJECT);

        // Get all the appointmentsList where subject equals to UPDATED_SUBJECT
        defaultAppointmentsShouldNotBeFound("subject.equals=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    public void getAllAppointmentsBySubjectIsNotEqualToSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where subject not equals to DEFAULT_SUBJECT
        defaultAppointmentsShouldNotBeFound("subject.notEquals=" + DEFAULT_SUBJECT);

        // Get all the appointmentsList where subject not equals to UPDATED_SUBJECT
        defaultAppointmentsShouldBeFound("subject.notEquals=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    public void getAllAppointmentsBySubjectIsInShouldWork() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where subject in DEFAULT_SUBJECT or UPDATED_SUBJECT
        defaultAppointmentsShouldBeFound("subject.in=" + DEFAULT_SUBJECT + "," + UPDATED_SUBJECT);

        // Get all the appointmentsList where subject equals to UPDATED_SUBJECT
        defaultAppointmentsShouldNotBeFound("subject.in=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    public void getAllAppointmentsBySubjectIsNullOrNotNull() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where subject is not null
        defaultAppointmentsShouldBeFound("subject.specified=true");

        // Get all the appointmentsList where subject is null
        defaultAppointmentsShouldNotBeFound("subject.specified=false");
    }
                @Test
    @Transactional
    public void getAllAppointmentsBySubjectContainsSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where subject contains DEFAULT_SUBJECT
        defaultAppointmentsShouldBeFound("subject.contains=" + DEFAULT_SUBJECT);

        // Get all the appointmentsList where subject contains UPDATED_SUBJECT
        defaultAppointmentsShouldNotBeFound("subject.contains=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    public void getAllAppointmentsBySubjectNotContainsSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where subject does not contain DEFAULT_SUBJECT
        defaultAppointmentsShouldNotBeFound("subject.doesNotContain=" + DEFAULT_SUBJECT);

        // Get all the appointmentsList where subject does not contain UPDATED_SUBJECT
        defaultAppointmentsShouldBeFound("subject.doesNotContain=" + UPDATED_SUBJECT);
    }


    @Test
    @Transactional
    public void getAllAppointmentsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where description equals to DEFAULT_DESCRIPTION
        defaultAppointmentsShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the appointmentsList where description equals to UPDATED_DESCRIPTION
        defaultAppointmentsShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where description not equals to DEFAULT_DESCRIPTION
        defaultAppointmentsShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the appointmentsList where description not equals to UPDATED_DESCRIPTION
        defaultAppointmentsShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultAppointmentsShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the appointmentsList where description equals to UPDATED_DESCRIPTION
        defaultAppointmentsShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where description is not null
        defaultAppointmentsShouldBeFound("description.specified=true");

        // Get all the appointmentsList where description is null
        defaultAppointmentsShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllAppointmentsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where description contains DEFAULT_DESCRIPTION
        defaultAppointmentsShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the appointmentsList where description contains UPDATED_DESCRIPTION
        defaultAppointmentsShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where description does not contain DEFAULT_DESCRIPTION
        defaultAppointmentsShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the appointmentsList where description does not contain UPDATED_DESCRIPTION
        defaultAppointmentsShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllAppointmentsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where date equals to DEFAULT_DATE
        defaultAppointmentsShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the appointmentsList where date equals to UPDATED_DATE
        defaultAppointmentsShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where date not equals to DEFAULT_DATE
        defaultAppointmentsShouldNotBeFound("date.notEquals=" + DEFAULT_DATE);

        // Get all the appointmentsList where date not equals to UPDATED_DATE
        defaultAppointmentsShouldBeFound("date.notEquals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where date in DEFAULT_DATE or UPDATED_DATE
        defaultAppointmentsShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the appointmentsList where date equals to UPDATED_DATE
        defaultAppointmentsShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where date is not null
        defaultAppointmentsShouldBeFound("date.specified=true");

        // Get all the appointmentsList where date is null
        defaultAppointmentsShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where date is greater than or equal to DEFAULT_DATE
        defaultAppointmentsShouldBeFound("date.greaterThanOrEqual=" + DEFAULT_DATE);

        // Get all the appointmentsList where date is greater than or equal to UPDATED_DATE
        defaultAppointmentsShouldNotBeFound("date.greaterThanOrEqual=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where date is less than or equal to DEFAULT_DATE
        defaultAppointmentsShouldBeFound("date.lessThanOrEqual=" + DEFAULT_DATE);

        // Get all the appointmentsList where date is less than or equal to SMALLER_DATE
        defaultAppointmentsShouldNotBeFound("date.lessThanOrEqual=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDateIsLessThanSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where date is less than DEFAULT_DATE
        defaultAppointmentsShouldNotBeFound("date.lessThan=" + DEFAULT_DATE);

        // Get all the appointmentsList where date is less than UPDATED_DATE
        defaultAppointmentsShouldBeFound("date.lessThan=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where date is greater than DEFAULT_DATE
        defaultAppointmentsShouldNotBeFound("date.greaterThan=" + DEFAULT_DATE);

        // Get all the appointmentsList where date is greater than SMALLER_DATE
        defaultAppointmentsShouldBeFound("date.greaterThan=" + SMALLER_DATE);
    }


    @Test
    @Transactional
    public void getAllAppointmentsByDurationIsEqualToSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where duration equals to DEFAULT_DURATION
        defaultAppointmentsShouldBeFound("duration.equals=" + DEFAULT_DURATION);

        // Get all the appointmentsList where duration equals to UPDATED_DURATION
        defaultAppointmentsShouldNotBeFound("duration.equals=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDurationIsNotEqualToSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where duration not equals to DEFAULT_DURATION
        defaultAppointmentsShouldNotBeFound("duration.notEquals=" + DEFAULT_DURATION);

        // Get all the appointmentsList where duration not equals to UPDATED_DURATION
        defaultAppointmentsShouldBeFound("duration.notEquals=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDurationIsInShouldWork() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where duration in DEFAULT_DURATION or UPDATED_DURATION
        defaultAppointmentsShouldBeFound("duration.in=" + DEFAULT_DURATION + "," + UPDATED_DURATION);

        // Get all the appointmentsList where duration equals to UPDATED_DURATION
        defaultAppointmentsShouldNotBeFound("duration.in=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDurationIsNullOrNotNull() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where duration is not null
        defaultAppointmentsShouldBeFound("duration.specified=true");

        // Get all the appointmentsList where duration is null
        defaultAppointmentsShouldNotBeFound("duration.specified=false");
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDurationIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where duration is greater than or equal to DEFAULT_DURATION
        defaultAppointmentsShouldBeFound("duration.greaterThanOrEqual=" + DEFAULT_DURATION);

        // Get all the appointmentsList where duration is greater than or equal to UPDATED_DURATION
        defaultAppointmentsShouldNotBeFound("duration.greaterThanOrEqual=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDurationIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where duration is less than or equal to DEFAULT_DURATION
        defaultAppointmentsShouldBeFound("duration.lessThanOrEqual=" + DEFAULT_DURATION);

        // Get all the appointmentsList where duration is less than or equal to SMALLER_DURATION
        defaultAppointmentsShouldNotBeFound("duration.lessThanOrEqual=" + SMALLER_DURATION);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDurationIsLessThanSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where duration is less than DEFAULT_DURATION
        defaultAppointmentsShouldNotBeFound("duration.lessThan=" + DEFAULT_DURATION);

        // Get all the appointmentsList where duration is less than UPDATED_DURATION
        defaultAppointmentsShouldBeFound("duration.lessThan=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDurationIsGreaterThanSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where duration is greater than DEFAULT_DURATION
        defaultAppointmentsShouldNotBeFound("duration.greaterThan=" + DEFAULT_DURATION);

        // Get all the appointmentsList where duration is greater than SMALLER_DURATION
        defaultAppointmentsShouldBeFound("duration.greaterThan=" + SMALLER_DURATION);
    }


    @Test
    @Transactional
    public void getAllAppointmentsByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where notes equals to DEFAULT_NOTES
        defaultAppointmentsShouldBeFound("notes.equals=" + DEFAULT_NOTES);

        // Get all the appointmentsList where notes equals to UPDATED_NOTES
        defaultAppointmentsShouldNotBeFound("notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByNotesIsNotEqualToSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where notes not equals to DEFAULT_NOTES
        defaultAppointmentsShouldNotBeFound("notes.notEquals=" + DEFAULT_NOTES);

        // Get all the appointmentsList where notes not equals to UPDATED_NOTES
        defaultAppointmentsShouldBeFound("notes.notEquals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where notes in DEFAULT_NOTES or UPDATED_NOTES
        defaultAppointmentsShouldBeFound("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES);

        // Get all the appointmentsList where notes equals to UPDATED_NOTES
        defaultAppointmentsShouldNotBeFound("notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where notes is not null
        defaultAppointmentsShouldBeFound("notes.specified=true");

        // Get all the appointmentsList where notes is null
        defaultAppointmentsShouldNotBeFound("notes.specified=false");
    }
                @Test
    @Transactional
    public void getAllAppointmentsByNotesContainsSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where notes contains DEFAULT_NOTES
        defaultAppointmentsShouldBeFound("notes.contains=" + DEFAULT_NOTES);

        // Get all the appointmentsList where notes contains UPDATED_NOTES
        defaultAppointmentsShouldNotBeFound("notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        // Get all the appointmentsList where notes does not contain DEFAULT_NOTES
        defaultAppointmentsShouldNotBeFound("notes.doesNotContain=" + DEFAULT_NOTES);

        // Get all the appointmentsList where notes does not contain UPDATED_NOTES
        defaultAppointmentsShouldBeFound("notes.doesNotContain=" + UPDATED_NOTES);
    }


    @Test
    @Transactional
    public void getAllAppointmentsByUserIsEqualToSomething() throws Exception {
        // Get already existing entity
        Set<User> users = appointments.getUsers();
        appointmentsRepository.saveAndFlush(appointments);
        for (User user : users) {
        Long userId = user.getId();

        // Get all the appointmentsList where user equals to userId
        defaultAppointmentsShouldBeFound("userId.equals=" + userId);

        // Get all the appointmentsList where user equals to userId + 1
        defaultAppointmentsShouldNotBeFound("userId.equals=" + (userId + 1));
        }
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAppointmentsShouldBeFound(String filter) throws Exception {
        restAppointmentsMockMvc.perform(get("/api/appointments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appointments.getId().intValue())))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));

        // Check, that the count call also returns 1
        restAppointmentsMockMvc.perform(get("/api/appointments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAppointmentsShouldNotBeFound(String filter) throws Exception {
        restAppointmentsMockMvc.perform(get("/api/appointments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAppointmentsMockMvc.perform(get("/api/appointments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingAppointments() throws Exception {
        // Get the appointments
        restAppointmentsMockMvc.perform(get("/api/appointments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAppointments() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        int databaseSizeBeforeUpdate = appointmentsRepository.findAll().size();

        // Update the appointments
        Appointments updatedAppointments = appointmentsRepository.findById(appointments.getId()).get();
        // Disconnect from session so that the updates on updatedAppointments are not directly saved in db
        em.detach(updatedAppointments);
        updatedAppointments
            .subject(UPDATED_SUBJECT)
            .description(UPDATED_DESCRIPTION)
            .date(UPDATED_DATE)
            .duration(UPDATED_DURATION)
            .notes(UPDATED_NOTES);
        AppointmentsDTO appointmentsDTO = appointmentsMapper.toDto(updatedAppointments);

        restAppointmentsMockMvc.perform(put("/api/appointments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(appointmentsDTO)))
            .andExpect(status().isOk());

        // Validate the Appointments in the database
        List<Appointments> appointmentsList = appointmentsRepository.findAll();
        assertThat(appointmentsList).hasSize(databaseSizeBeforeUpdate);
        Appointments testAppointments = appointmentsList.get(appointmentsList.size() - 1);
        assertThat(testAppointments.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testAppointments.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAppointments.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testAppointments.getDuration()).isEqualTo(UPDATED_DURATION);
        assertThat(testAppointments.getNotes()).isEqualTo(UPDATED_NOTES);
    }

    @Test
    @Transactional
    public void updateNonExistingAppointments() throws Exception {
        int databaseSizeBeforeUpdate = appointmentsRepository.findAll().size();

        // Create the Appointments
        AppointmentsDTO appointmentsDTO = appointmentsMapper.toDto(appointments);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppointmentsMockMvc.perform(put("/api/appointments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(appointmentsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Appointments in the database
        List<Appointments> appointmentsList = appointmentsRepository.findAll();
        assertThat(appointmentsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteAppointments() throws Exception {
        // Initialize the database
        appointmentsRepository.saveAndFlush(appointments);

        int databaseSizeBeforeDelete = appointmentsRepository.findAll().size();

        // Delete the appointments
        restAppointmentsMockMvc.perform(delete("/api/appointments/{id}", appointments.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Appointments> appointmentsList = appointmentsRepository.findAll();
        assertThat(appointmentsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
