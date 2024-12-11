package it.unisannio.studenti.qualitag.service;

import static com.google.api.services.gmail.GmailScopes.GMAIL_SEND;
import static jakarta.mail.Message.RecipientType.TO;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.codec.binary.Base64;

/**
 * This class is used to send e-mails using the Gmail API.
 */
public class GmailService {

  private static final String TEST_EMAIL = "qualitag.project@gmail.com";
  private static final String FROM_EMAIL = "qualitag.project@gmail.com";
  private final Gmail service;

  /**
   * Constructor that initializes the Gmail service.
   *
   * @throws Exception if an error occurs while initializing the service.
   */
  public GmailService() throws Exception {
    // Initialize HTTP transport and JSON factory
    NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

    // Build the Gmail service
    GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    service = new Gmail.Builder(httpTransport, jsonFactory,
        getCredentials(httpTransport, jsonFactory))
        .setApplicationName("QualiTag Project")
        .build();
  }

  /**
   * Creates a new Gmail service instance.
   *
   * @param httpTransport the HTTP transport.
   * @param jsonFactory   the JSON factory.
   * @return the Gmail service instance.
   * @throws IOException if an error occurs while loading the credentials.
   */
  private static Credential getCredentials(final NetHttpTransport httpTransport,
      GsonFactory jsonFactory)
      throws IOException {
    // Load client secrets from the credentials file
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,
        new InputStreamReader(GmailService.class.getResourceAsStream(
            "/credentials/credentials_email_service.json")));

    // Build the authorization flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, jsonFactory, clientSecrets, Set.of(GMAIL_SEND))
        .setDataStoreFactory(new FileDataStoreFactory(
            Paths.get("qualitag/src/main/resources/credentials/tokens").toFile()))
        .setAccessType("offline")
        .build();

    // Set up a local server receiver to handle the authorization
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }

  /**
   * Sends an e-mail message.
   *
   * @param subject the e-mail subject.
   * @param to      the recipient e-mail address.
   * @param message the e-mail message.
   * @throws Exception if an error occurs while sending the e-mail.
   */
  public void sendMail(String subject, String to, String message) throws Exception {
    // Set up mail properties and session
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    // Create a new e-mail message
    MimeMessage email = new MimeMessage(session);
    email.setFrom("QualiTag Project <" + FROM_EMAIL + ">");
    email.addRecipient(TO, new InternetAddress(to));
    email.setSubject(subject);
    email.setText(message);

    // Encode the e-mail message
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    email.writeTo(buffer);
    byte[] rawMessageBytes = buffer.toByteArray();
    String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
    Message msg = new Message();
    msg.setRaw(encodedEmail);

    try {
      // Send the e-mail using the Gmail API
      msg = service.users().messages().send(FROM_EMAIL, msg).execute();
      System.out.println("Message sent: " + msg.toPrettyString());
    } catch (GoogleJsonResponseException e) {
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == 403) {
        System.err.println("Unable to send message: " + e.getDetails());
      } else {
        throw e;
      }
    }
  }

  /**
   * Main method used to test the Gmail service.
   *
   * @param args the command-line arguments.
   * @throws Exception if an error occurs while sending the e-mail.
   */
  public static void main(String[] args) throws Exception {
    new GmailService().sendMail(" Test e-mail",
        TEST_EMAIL,
        "This is a test e-mail.");
  }
}