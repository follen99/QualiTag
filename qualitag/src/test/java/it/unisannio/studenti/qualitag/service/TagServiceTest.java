package it.unisannio.studenti.qualitag.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.unisannio.studenti.qualitag.dto.tag.TagCreateDto;
import it.unisannio.studenti.qualitag.exception.TagValidationException;
import it.unisannio.studenti.qualitag.mapper.TagMapper;
import it.unisannio.studenti.qualitag.model.Artifact;
import it.unisannio.studenti.qualitag.model.Tag;
import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.ArtifactRepository;
import it.unisannio.studenti.qualitag.repository.TagRepository;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import it.unisannio.studenti.qualitag.security.model.CustomUserDetails;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class TagServiceTest {

  @Mock
  private TagRepository tagRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  ArtifactRepository artifactRepository;
  @Mock
  private TagMapper tagMapper;

  @InjectMocks
  private TagService tagService;

  private Tag tag1, tag2;
  private Artifact artifact;
  private User user;

  private TagCreateDto tagCreateDto;

  /**
   * Sets up the test environment.
   */
  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    //Initialize the objects
    tag1 = new Tag("tag1", "user1Id", "#fff8de");
    tag1.setTagId("tag1Id");
    tag2 = new Tag("tag2", "user1Id", "#295f98");
    tag2.setTagId("tag2Id");

    user = new User("user1", "user1@example.com",
        "password1", "Jane", "Doe");
    user.setUserId("user1Id");
    user.setTagIds(new ArrayList<>(Arrays.asList("tag1Id", "tag2Id")));

    artifact = new Artifact("artifactName",
        "projectId", "teamId", "filePath");
    artifact.setArtifactId("artifactId");
    artifact.setTags(new ArrayList<>(Arrays.asList("tagId1", "tagId2")));

    //Initialize the DTO
    tagCreateDto = new TagCreateDto("tag3", "user1Id", "#fff8de");

    //Initiliaze authorization details
    // Mock SecurityContextHolder to provide an authenticated user
    Authentication authentication = mock(Authentication.class);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getName()).thenReturn("user1");
    when(authentication.getPrincipal()).thenReturn(new CustomUserDetails(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }

  /**
   * Tests a successful execution of the createTag method.
   *
   * @throws TagValidationException    if the tag is invalid
   * @throws NoSuchMethodException     if the method does not exist
   * @throws InvocationTargetException if the method cannot be invoked
   * @throws IllegalAccessException    if the method cannot be accessed
   */
  @Test
  public void testCreateTag()
      throws TagValidationException,
      NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    //Arrange
    Tag newTag = new Tag("tag3", "user1Id", "#fff8de");
    TagService tagServiceSpy = Mockito.spy(tagService);

    when(userRepository.findByUserId("user1Id")).thenReturn(user);
    when(userRepository.existsByUsername("user1")).thenReturn(true);
    when(userRepository.existsById("user1Id")).thenReturn(true);
    when(userRepository.save(user)).thenReturn(user);
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(tagRepository.save(any(Tag.class))).thenReturn(newTag);
    when(tagRepository.findTagByTagId("tag1Id")).thenReturn(tag1);
    when(tagRepository.findTagByTagId("tag2Id")).thenReturn(tag2);

    Method validateTagMethod = TagService.class.getDeclaredMethod(
        "validateTag", TagCreateDto.class);
    validateTagMethod.setAccessible(true);

    TagCreateDto validatedTagCreateDto =
        (TagCreateDto) validateTagMethod.invoke(tagServiceSpy, tagCreateDto);

    when(tagMapper.toEntity(validatedTagCreateDto)).thenReturn(newTag);

    //Act
    ResponseEntity<?> response = tagService.createTag(tagCreateDto);

    //Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("Tag added successfully",
        ((Map<String, Object>) response.getBody()).get("msg"));
  }


}
