package it.unisannio.studenti.qualitag.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * This class contains unit tests for the GmailService class.
 */
public class GmailServiceTest {

  @Mock
  private Gmail gmailMock;

  @Mock
  private Gmail.Users usersMock;

  @Mock
  private Gmail.Users.Messages messagesMock;

  @Mock
  private Gmail.Users.Messages.Send sendMock;

  @InjectMocks
  private GmailService gmailService;

  /**
   * Set up the test environment.
   */
  @BeforeEach
  public void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);

    // Mock Gmail service behavior
    when(gmailMock.users()).thenReturn(usersMock);
    when(usersMock.messages()).thenReturn(messagesMock);
    when(messagesMock.send(anyString(), any(Message.class))).thenReturn(sendMock);
    when(sendMock.execute()).thenReturn(new Message().setId("12345"));

    // Use reflection to inject the mock Gmail instance
    var serviceField = GmailService.class.getDeclaredField("service");
    serviceField.setAccessible(true);
    serviceField.set(gmailService, gmailMock);
  }

  @Test
  public void testSendMail() throws Exception {
    // Define test email details
    String subject = "Test Email";
    String recipient = "test@example.com";
    String body = "This is a test message.";

    // Call the method
    gmailService.sendMail(subject, recipient, body);

    // Verify interactions
    verify(messagesMock, times(1)).send(eq("qualitag.project@gmail.com"), any(Message.class));
    verify(sendMock, times(1)).execute();
  }

  @Test
  public void testMimeMessageEncoding() throws Exception {
    // Set up mail properties and session
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    // Create a MimeMessage
    MimeMessage email = new MimeMessage(session);
    email.setFrom(new InternetAddress("qualitag.project@gmail.com"));
    email.addRecipient(jakarta.mail.Message.RecipientType.TO,
        new InternetAddress("test@example.com"));
    email.setSubject("Test Subject");
    email.setText("Test Body");

    // Encode the e-mail message
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    email.writeTo(buffer);
    String encodedEmail = Base64.encodeBase64URLSafeString(buffer.toByteArray());

    // Assert that encoding produces a valid Base64 string
    assert !encodedEmail.isEmpty();
  }

  @Test
  public void testSendMailHandlesGoogleJsonResponseException() throws Exception {
    // Simulate GoogleJsonResponseException (403 Forbidden error)
    GoogleJsonError error = new GoogleJsonError();
    error.setCode(403);
    error.setMessage("Access forbidden");

    HttpResponseException.Builder builder =
        new HttpResponseException.Builder(403, "Forbidden", new HttpHeaders());

    GoogleJsonResponseException exception = new GoogleJsonResponseException(builder, error);

    when(sendMock.execute()).thenThrow(exception);

    // Define test email details
    String subject = "Test Email";
    String recipient = "test@example.com";
    String body = "This is a test message.";

    // Call sendMail and verify exception handling (no exception should propagate)
    gmailService.sendMail(subject, recipient, body);

    // Verify the Gmail API was called once
    verify(messagesMock, times(1)).send(eq("qualitag.project@gmail.com"), any(Message.class));
  }

  @Test
  public void testSendMailThrowsExceptionOnNon403Error() throws Exception {

    // Simulate GoogleJsonResponseException with error code 500 (Internal Server Error)
    GoogleJsonError error = new GoogleJsonError();
    error.setCode(500);
    error.setMessage("Internal Server Error");

    HttpResponseException.Builder builder =
        new HttpResponseException.Builder(500, "Internal Server Error", new HttpHeaders());

    GoogleJsonResponseException exception = new GoogleJsonResponseException(builder, error);

    when(sendMock.execute()).thenThrow(exception);

    // Define test email details
    String subject = "Test Email";
    String recipient = "test@example.com";
    String body = "This is a test message.";

    // Assert that sendMail throws the exception (since it's not 403)
    assertThrows(GoogleJsonResponseException.class, () -> {
      gmailService.sendMail(subject, recipient, body);
    });

    // Verify that the Gmail API was still called
    verify(messagesMock, times(1)).send(eq("qualitag.project@gmail.com"), any(Message.class));
  }
}
