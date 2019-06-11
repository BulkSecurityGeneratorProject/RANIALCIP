package com.ranial.cip.web.rest;

import com.ranial.cip.AbstractCassandraTest;
import com.ranial.cip.CogServiceIntegrationApp;
import com.ranial.cip.domain.DATATYPE;
import com.ranial.cip.domain.DomainAttributes;
import com.ranial.cip.repository.DomainAttributesRepository;
import com.ranial.cip.service.DomainAttributesService;
import com.ranial.cip.service.dto.DomainAttributesDTO;
import com.ranial.cip.service.mapper.DomainAttributesMapper;
import com.ranial.cip.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;


import java.util.List;
import java.util.UUID;

import static com.ranial.cip.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link DomainAttributesResource} REST controller.
 */
@SpringBootTest(classes = CogServiceIntegrationApp.class)
public class DomainAttributesResourceIT extends AbstractCassandraTest {

    private static final String DEFAULT_ATTRIBUTE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ATTRIBUTE_NAME = "BBBBBBBBBB";

    private static final DATATYPE DEFAULT_ATTRIBUTE_TYPE = DATATYPE.STRING;
    private static final DATATYPE UPDATED_ATTRIBUTE_TYPE = DATATYPE.STRING;

    private static final String DEFAULT_ENTITY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ENTITY_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_ATTRIBUTE_LENGTH = 1;
    private static final Integer UPDATED_ATTRIBUTE_LENGTH = 2;

    private static final String DEFAULT_ATTRIBUTE_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_ATTRIBUTE_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ALLOW_NULL = false;
    private static final Boolean UPDATED_ALLOW_NULL = true;

    private static final Boolean DEFAULT_IS_PRIMARY = false;
    private static final Boolean UPDATED_IS_PRIMARY = true;

    private static final Boolean DEFAULT_IS_UNIQUE = false;
    private static final Boolean UPDATED_IS_UNIQUE = true;

    private static final Boolean DEFAULT_IS_INDEXED = false;
    private static final Boolean UPDATED_IS_INDEXED = true;

    private static final Boolean DEFAULT_IS_FORAIGN_KEY = false;
    private static final Boolean UPDATED_IS_FORAIGN_KEY = true;

    private static final String DEFAULT_FORAIGN_KEY_ENTITY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FORAIGN_KEY_ENTITY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FORAIGN_KEY_ATTRIBUTE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FORAIGN_KEY_ATTRIBUTE_NAME = "BBBBBBBBBB";

    @Autowired
    private DomainAttributesRepository domainAttributesRepository;

    @Autowired
    private DomainAttributesMapper domainAttributesMapper;

    @Autowired
    private DomainAttributesService domainAttributesService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc restDomainAttributesMockMvc;

    private DomainAttributes domainAttributes;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DomainAttributesResource domainAttributesResource = new DomainAttributesResource(domainAttributesService);
        this.restDomainAttributesMockMvc = MockMvcBuilders.standaloneSetup(domainAttributesResource)
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
    public static DomainAttributes createEntity() {
        DomainAttributes domainAttributes = new DomainAttributes()
            .attributeName(DEFAULT_ATTRIBUTE_NAME)
            .entityName(DEFAULT_ENTITY_NAME)
            .attributeType(DEFAULT_ATTRIBUTE_TYPE)
            .attributeLength(DEFAULT_ATTRIBUTE_LENGTH)
            .attributeDescription(DEFAULT_ATTRIBUTE_DESCRIPTION)
            .allowNull(DEFAULT_ALLOW_NULL)
            .isPrimary(DEFAULT_IS_PRIMARY)
            .isUnique(DEFAULT_IS_UNIQUE)
            .isIndexed(DEFAULT_IS_INDEXED)
            .isForaignKey(DEFAULT_IS_FORAIGN_KEY)
            .foraignKeyEntityName(DEFAULT_FORAIGN_KEY_ENTITY_NAME)
            .foraignKeyAttributeName(DEFAULT_FORAIGN_KEY_ATTRIBUTE_NAME);
        return domainAttributes;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DomainAttributes createUpdatedEntity() {
        DomainAttributes domainAttributes = new DomainAttributes()
            .attributeName(UPDATED_ATTRIBUTE_NAME)
            .entityName(UPDATED_ENTITY_NAME)
            .attributeType(UPDATED_ATTRIBUTE_TYPE)
            .attributeLength(UPDATED_ATTRIBUTE_LENGTH)
            .attributeDescription(UPDATED_ATTRIBUTE_DESCRIPTION)
            .allowNull(UPDATED_ALLOW_NULL)
            .isPrimary(UPDATED_IS_PRIMARY)
            .isUnique(UPDATED_IS_UNIQUE)
            .isIndexed(UPDATED_IS_INDEXED)
            .isForaignKey(UPDATED_IS_FORAIGN_KEY)
            .foraignKeyEntityName(UPDATED_FORAIGN_KEY_ENTITY_NAME)
            .foraignKeyAttributeName(UPDATED_FORAIGN_KEY_ATTRIBUTE_NAME);
        return domainAttributes;
    }

    @BeforeEach
    public void initTest() {
        domainAttributesRepository.deleteAll();
        domainAttributes = createEntity();
    }

    @Test
    public void createDomainAttributes() throws Exception {
        int databaseSizeBeforeCreate = domainAttributesRepository.findAll().size();

        // Create the DomainAttributes
        DomainAttributesDTO domainAttributesDTO = domainAttributesMapper.toDto(domainAttributes);
        restDomainAttributesMockMvc.perform(post("/api/domain-attributes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(domainAttributesDTO)))
            .andExpect(status().isCreated());

        // Validate the DomainAttributes in the database
        List<DomainAttributes> domainAttributesList = domainAttributesRepository.findAll();
        assertThat(domainAttributesList).hasSize(databaseSizeBeforeCreate + 1);
        DomainAttributes testDomainAttributes = domainAttributesList.get(domainAttributesList.size() - 1);
        assertThat(testDomainAttributes.getAttributeName()).isEqualTo(DEFAULT_ATTRIBUTE_NAME);
        assertThat(testDomainAttributes.getEntityName()).isEqualTo(DEFAULT_ENTITY_NAME);
        assertThat(testDomainAttributes.getAttributeType()).isEqualTo(DEFAULT_ATTRIBUTE_TYPE);
        assertThat(testDomainAttributes.getAttributeLength()).isEqualTo(DEFAULT_ATTRIBUTE_LENGTH);
        assertThat(testDomainAttributes.getAttributeDescription()).isEqualTo(DEFAULT_ATTRIBUTE_DESCRIPTION);
        assertThat(testDomainAttributes.isAllowNull()).isEqualTo(DEFAULT_ALLOW_NULL);
        assertThat(testDomainAttributes.isIsPrimary()).isEqualTo(DEFAULT_IS_PRIMARY);
        assertThat(testDomainAttributes.isIsUnique()).isEqualTo(DEFAULT_IS_UNIQUE);
        assertThat(testDomainAttributes.isIsIndexed()).isEqualTo(DEFAULT_IS_INDEXED);
        assertThat(testDomainAttributes.isIsForaignKey()).isEqualTo(DEFAULT_IS_FORAIGN_KEY);
        assertThat(testDomainAttributes.getForaignKeyEntityName()).isEqualTo(DEFAULT_FORAIGN_KEY_ENTITY_NAME);
        assertThat(testDomainAttributes.getForaignKeyAttributeName()).isEqualTo(DEFAULT_FORAIGN_KEY_ATTRIBUTE_NAME);
    }

    @Test
    public void createDomainAttributesWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = domainAttributesRepository.findAll().size();

        // Create the DomainAttributes with an existing ID
        domainAttributes.setId(UUID.randomUUID());
        DomainAttributesDTO domainAttributesDTO = domainAttributesMapper.toDto(domainAttributes);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDomainAttributesMockMvc.perform(post("/api/domain-attributes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(domainAttributesDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DomainAttributes in the database
        List<DomainAttributes> domainAttributesList = domainAttributesRepository.findAll();
        assertThat(domainAttributesList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    public void checkAttributeNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = domainAttributesRepository.findAll().size();
        // set the field null
        domainAttributes.setAttributeName(null);

        // Create the DomainAttributes, which fails.
        DomainAttributesDTO domainAttributesDTO = domainAttributesMapper.toDto(domainAttributes);

        restDomainAttributesMockMvc.perform(post("/api/domain-attributes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(domainAttributesDTO)))
            .andExpect(status().isBadRequest());

        List<DomainAttributes> domainAttributesList = domainAttributesRepository.findAll();
        assertThat(domainAttributesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkEntityNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = domainAttributesRepository.findAll().size();
        // set the field null
        domainAttributes.setEntityName(null);

        // Create the DomainAttributes, which fails.
        DomainAttributesDTO domainAttributesDTO = domainAttributesMapper.toDto(domainAttributes);

        restDomainAttributesMockMvc.perform(post("/api/domain-attributes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(domainAttributesDTO)))
            .andExpect(status().isBadRequest());

        List<DomainAttributes> domainAttributesList = domainAttributesRepository.findAll();
        assertThat(domainAttributesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkAttributeTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = domainAttributesRepository.findAll().size();
        // set the field null
        domainAttributes.setAttributeType(null);

        // Create the DomainAttributes, which fails.
        DomainAttributesDTO domainAttributesDTO = domainAttributesMapper.toDto(domainAttributes);

        restDomainAttributesMockMvc.perform(post("/api/domain-attributes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(domainAttributesDTO)))
            .andExpect(status().isBadRequest());

        List<DomainAttributes> domainAttributesList = domainAttributesRepository.findAll();
        assertThat(domainAttributesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkAllowNullIsRequired() throws Exception {
        int databaseSizeBeforeTest = domainAttributesRepository.findAll().size();
        // set the field null
        domainAttributes.setAllowNull(null);

        // Create the DomainAttributes, which fails.
        DomainAttributesDTO domainAttributesDTO = domainAttributesMapper.toDto(domainAttributes);

        restDomainAttributesMockMvc.perform(post("/api/domain-attributes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(domainAttributesDTO)))
            .andExpect(status().isBadRequest());

        List<DomainAttributes> domainAttributesList = domainAttributesRepository.findAll();
        assertThat(domainAttributesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkIsPrimaryIsRequired() throws Exception {
        int databaseSizeBeforeTest = domainAttributesRepository.findAll().size();
        // set the field null
        domainAttributes.setIsPrimary(null);

        // Create the DomainAttributes, which fails.
        DomainAttributesDTO domainAttributesDTO = domainAttributesMapper.toDto(domainAttributes);

        restDomainAttributesMockMvc.perform(post("/api/domain-attributes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(domainAttributesDTO)))
            .andExpect(status().isBadRequest());

        List<DomainAttributes> domainAttributesList = domainAttributesRepository.findAll();
        assertThat(domainAttributesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkIsUniqueIsRequired() throws Exception {
        int databaseSizeBeforeTest = domainAttributesRepository.findAll().size();
        // set the field null
        domainAttributes.setIsUnique(null);

        // Create the DomainAttributes, which fails.
        DomainAttributesDTO domainAttributesDTO = domainAttributesMapper.toDto(domainAttributes);

        restDomainAttributesMockMvc.perform(post("/api/domain-attributes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(domainAttributesDTO)))
            .andExpect(status().isBadRequest());

        List<DomainAttributes> domainAttributesList = domainAttributesRepository.findAll();
        assertThat(domainAttributesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkIsIndexedIsRequired() throws Exception {
        int databaseSizeBeforeTest = domainAttributesRepository.findAll().size();
        // set the field null
        domainAttributes.setIsIndexed(null);

        // Create the DomainAttributes, which fails.
        DomainAttributesDTO domainAttributesDTO = domainAttributesMapper.toDto(domainAttributes);

        restDomainAttributesMockMvc.perform(post("/api/domain-attributes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(domainAttributesDTO)))
            .andExpect(status().isBadRequest());

        List<DomainAttributes> domainAttributesList = domainAttributesRepository.findAll();
        assertThat(domainAttributesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkIsForaignKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = domainAttributesRepository.findAll().size();
        // set the field null
        domainAttributes.setIsForaignKey(null);

        // Create the DomainAttributes, which fails.
        DomainAttributesDTO domainAttributesDTO = domainAttributesMapper.toDto(domainAttributes);

        restDomainAttributesMockMvc.perform(post("/api/domain-attributes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(domainAttributesDTO)))
            .andExpect(status().isBadRequest());

        List<DomainAttributes> domainAttributesList = domainAttributesRepository.findAll();
        assertThat(domainAttributesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllDomainAttributes() throws Exception {
        // Initialize the database
        domainAttributes.setId(UUID.randomUUID());
        domainAttributesRepository.save(domainAttributes);

        // Get all the domainAttributesList
        restDomainAttributesMockMvc.perform(get("/api/domain-attributes"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(domainAttributes.getId().toString())))
            .andExpect(jsonPath("$.[*].attributeName").value(hasItem(DEFAULT_ATTRIBUTE_NAME.toString())))
            .andExpect(jsonPath("$.[*].entityName").value(hasItem(DEFAULT_ENTITY_NAME.toString())))
            .andExpect(jsonPath("$.[*].attributeType").value(hasItem(DEFAULT_ATTRIBUTE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].attributeLength").value(hasItem(DEFAULT_ATTRIBUTE_LENGTH)))
            .andExpect(jsonPath("$.[*].attributeDescription").value(hasItem(DEFAULT_ATTRIBUTE_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].allowNull").value(hasItem(DEFAULT_ALLOW_NULL.booleanValue())))
            .andExpect(jsonPath("$.[*].isPrimary").value(hasItem(DEFAULT_IS_PRIMARY.booleanValue())))
            .andExpect(jsonPath("$.[*].isUnique").value(hasItem(DEFAULT_IS_UNIQUE.booleanValue())))
            .andExpect(jsonPath("$.[*].isIndexed").value(hasItem(DEFAULT_IS_INDEXED.booleanValue())))
            .andExpect(jsonPath("$.[*].isForaignKey").value(hasItem(DEFAULT_IS_FORAIGN_KEY.booleanValue())))
            .andExpect(jsonPath("$.[*].foraignKeyEntityName").value(hasItem(DEFAULT_FORAIGN_KEY_ENTITY_NAME.toString())))
            .andExpect(jsonPath("$.[*].foraignKeyAttributeName").value(hasItem(DEFAULT_FORAIGN_KEY_ATTRIBUTE_NAME.toString())));
    }
    
    @Test
    public void getDomainAttributes() throws Exception {
        // Initialize the database
        domainAttributes.setId(UUID.randomUUID());
        domainAttributesRepository.save(domainAttributes);

        // Get the domainAttributes
        restDomainAttributesMockMvc.perform(get("/api/domain-attributes/{id}", domainAttributes.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(domainAttributes.getId().toString()))
            .andExpect(jsonPath("$.attributeName").value(DEFAULT_ATTRIBUTE_NAME.toString()))
            .andExpect(jsonPath("$.entityName").value(DEFAULT_ENTITY_NAME.toString()))
            .andExpect(jsonPath("$.attributeType").value(DEFAULT_ATTRIBUTE_TYPE.toString()))
            .andExpect(jsonPath("$.attributeLength").value(DEFAULT_ATTRIBUTE_LENGTH))
            .andExpect(jsonPath("$.attributeDescription").value(DEFAULT_ATTRIBUTE_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.allowNull").value(DEFAULT_ALLOW_NULL.booleanValue()))
            .andExpect(jsonPath("$.isPrimary").value(DEFAULT_IS_PRIMARY.booleanValue()))
            .andExpect(jsonPath("$.isUnique").value(DEFAULT_IS_UNIQUE.booleanValue()))
            .andExpect(jsonPath("$.isIndexed").value(DEFAULT_IS_INDEXED.booleanValue()))
            .andExpect(jsonPath("$.isForaignKey").value(DEFAULT_IS_FORAIGN_KEY.booleanValue()))
            .andExpect(jsonPath("$.foraignKeyEntityName").value(DEFAULT_FORAIGN_KEY_ENTITY_NAME.toString()))
            .andExpect(jsonPath("$.foraignKeyAttributeName").value(DEFAULT_FORAIGN_KEY_ATTRIBUTE_NAME.toString()));
    }

    @Test
    public void getNonExistingDomainAttributes() throws Exception {
        // Get the domainAttributes
        restDomainAttributesMockMvc.perform(get("/api/domain-attributes/{id}", UUID.randomUUID().toString()))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateDomainAttributes() throws Exception {
        // Initialize the database
        domainAttributes.setId(UUID.randomUUID());
        domainAttributesRepository.save(domainAttributes);

        int databaseSizeBeforeUpdate = domainAttributesRepository.findAll().size();

        // Update the domainAttributes
        DomainAttributes updatedDomainAttributes = domainAttributesRepository.findById(domainAttributes.getId()).get();
        updatedDomainAttributes
            .attributeName(UPDATED_ATTRIBUTE_NAME)
            .entityName(UPDATED_ENTITY_NAME)
            .attributeType(UPDATED_ATTRIBUTE_TYPE)
            .attributeLength(UPDATED_ATTRIBUTE_LENGTH)
            .attributeDescription(UPDATED_ATTRIBUTE_DESCRIPTION)
            .allowNull(UPDATED_ALLOW_NULL)
            .isPrimary(UPDATED_IS_PRIMARY)
            .isUnique(UPDATED_IS_UNIQUE)
            .isIndexed(UPDATED_IS_INDEXED)
            .isForaignKey(UPDATED_IS_FORAIGN_KEY)
            .foraignKeyEntityName(UPDATED_FORAIGN_KEY_ENTITY_NAME)
            .foraignKeyAttributeName(UPDATED_FORAIGN_KEY_ATTRIBUTE_NAME);
        DomainAttributesDTO domainAttributesDTO = domainAttributesMapper.toDto(updatedDomainAttributes);

        restDomainAttributesMockMvc.perform(put("/api/domain-attributes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(domainAttributesDTO)))
            .andExpect(status().isOk());

        // Validate the DomainAttributes in the database
        List<DomainAttributes> domainAttributesList = domainAttributesRepository.findAll();
        assertThat(domainAttributesList).hasSize(databaseSizeBeforeUpdate);
        DomainAttributes testDomainAttributes = domainAttributesList.get(domainAttributesList.size() - 1);
        assertThat(testDomainAttributes.getAttributeName()).isEqualTo(UPDATED_ATTRIBUTE_NAME);
        assertThat(testDomainAttributes.getEntityName()).isEqualTo(UPDATED_ENTITY_NAME);
        assertThat(testDomainAttributes.getAttributeType()).isEqualTo(UPDATED_ATTRIBUTE_TYPE);
        assertThat(testDomainAttributes.getAttributeLength()).isEqualTo(UPDATED_ATTRIBUTE_LENGTH);
        assertThat(testDomainAttributes.getAttributeDescription()).isEqualTo(UPDATED_ATTRIBUTE_DESCRIPTION);
        assertThat(testDomainAttributes.isAllowNull()).isEqualTo(UPDATED_ALLOW_NULL);
        assertThat(testDomainAttributes.isIsPrimary()).isEqualTo(UPDATED_IS_PRIMARY);
        assertThat(testDomainAttributes.isIsUnique()).isEqualTo(UPDATED_IS_UNIQUE);
        assertThat(testDomainAttributes.isIsIndexed()).isEqualTo(UPDATED_IS_INDEXED);
        assertThat(testDomainAttributes.isIsForaignKey()).isEqualTo(UPDATED_IS_FORAIGN_KEY);
        assertThat(testDomainAttributes.getForaignKeyEntityName()).isEqualTo(UPDATED_FORAIGN_KEY_ENTITY_NAME);
        assertThat(testDomainAttributes.getForaignKeyAttributeName()).isEqualTo(UPDATED_FORAIGN_KEY_ATTRIBUTE_NAME);
    }

    @Test
    public void updateNonExistingDomainAttributes() throws Exception {
        int databaseSizeBeforeUpdate = domainAttributesRepository.findAll().size();

        // Create the DomainAttributes
        DomainAttributesDTO domainAttributesDTO = domainAttributesMapper.toDto(domainAttributes);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDomainAttributesMockMvc.perform(put("/api/domain-attributes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(domainAttributesDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DomainAttributes in the database
        List<DomainAttributes> domainAttributesList = domainAttributesRepository.findAll();
        assertThat(domainAttributesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteDomainAttributes() throws Exception {
        // Initialize the database
        domainAttributes.setId(UUID.randomUUID());
        domainAttributesRepository.save(domainAttributes);

        int databaseSizeBeforeDelete = domainAttributesRepository.findAll().size();

        // Delete the domainAttributes
        restDomainAttributesMockMvc.perform(delete("/api/domain-attributes/{id}", domainAttributes.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        List<DomainAttributes> domainAttributesList = domainAttributesRepository.findAll();
        assertThat(domainAttributesList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DomainAttributes.class);
        DomainAttributes domainAttributes1 = new DomainAttributes();
        domainAttributes1.setId(UUID.randomUUID());
        DomainAttributes domainAttributes2 = new DomainAttributes();
        domainAttributes2.setId(domainAttributes1.getId());
        assertThat(domainAttributes1).isEqualTo(domainAttributes2);
        domainAttributes2.setId(UUID.randomUUID());
        assertThat(domainAttributes1).isNotEqualTo(domainAttributes2);
        domainAttributes1.setId(null);
        assertThat(domainAttributes1).isNotEqualTo(domainAttributes2);
    }

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DomainAttributesDTO.class);
        DomainAttributesDTO domainAttributesDTO1 = new DomainAttributesDTO();
        domainAttributesDTO1.setId(UUID.randomUUID());
        DomainAttributesDTO domainAttributesDTO2 = new DomainAttributesDTO();
        assertThat(domainAttributesDTO1).isNotEqualTo(domainAttributesDTO2);
        domainAttributesDTO2.setId(domainAttributesDTO1.getId());
        assertThat(domainAttributesDTO1).isEqualTo(domainAttributesDTO2);
        domainAttributesDTO2.setId(UUID.randomUUID());
        assertThat(domainAttributesDTO1).isNotEqualTo(domainAttributesDTO2);
        domainAttributesDTO1.setId(null);
        assertThat(domainAttributesDTO1).isNotEqualTo(domainAttributesDTO2);
    }
}
