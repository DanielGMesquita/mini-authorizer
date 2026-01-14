package dev.danielmesquita.miniauthorizer.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.danielmesquita.miniauthorizer.config.CustomAuthenticationEntryPoint;
import dev.danielmesquita.miniauthorizer.config.SecurityConfig;
import dev.danielmesquita.miniauthorizer.dto.CardDTO;
import dev.danielmesquita.miniauthorizer.dto.TransactionDTO;
import dev.danielmesquita.miniauthorizer.exception.CardAlreadyExistsException;
import dev.danielmesquita.miniauthorizer.repository.CardRepository;
import dev.danielmesquita.miniauthorizer.repository.UserRepository;
import dev.danielmesquita.miniauthorizer.service.CardService;
import dev.danielmesquita.miniauthorizer.service.CustomAuthenticationProvider;
import dev.danielmesquita.miniauthorizer.service.CustomUserDetailsService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc
@Import({SecurityConfig.class, CustomAuthenticationEntryPoint.class})
public class CardControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private CardService service;

  @MockitoBean CardRepository cardRepository;

  @MockitoBean UserRepository userRepository;

  @MockitoBean CustomUserDetailsService userDetailsService;

  @MockitoBean private CustomAuthenticationProvider customAuthenticationProvider;

  @Autowired private ObjectMapper objectMapper;

  private CardDTO cardDTO;

  private TransactionDTO transactionDTO;

  private final String rightUsername = "realuser@legit.com";
  private final String rightUserPassword = "realstrongpassword";
  private final String encryptedUserPassword =
      new BCryptPasswordEncoder().encode(rightUserPassword);

  @BeforeEach
  public void setUp() {
    String existingCardNumber = "1234567890123456";
    String rightPassword = "password123";

    when(userDetailsService.loadUserByUsername(rightUsername))
        .thenReturn(
            User.builder()
                .username(rightUsername)
                .password(encryptedUserPassword)
                .roles("USER")
                .build());

    cardDTO = new CardDTO();
    cardDTO.setCardNumber(existingCardNumber);
    cardDTO.setPassword(rightPassword);

    transactionDTO = new TransactionDTO();
    transactionDTO.setCardNumber(existingCardNumber);
    transactionDTO.setPassword(rightPassword);
    transactionDTO.setValue(new BigDecimal("50.00"));
  }

  @Test
  public void createCardShouldReturnCardDTOCreated() throws Exception {
    when(service.createCard(Mockito.any(CardDTO.class))).thenReturn(cardDTO);
    String jsonBody = objectMapper.writeValueAsString(cardDTO);

    ResultActions resultActions =
        mockMvc
            .perform(
                post("/cards")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(httpBasic(rightUsername, rightUserPassword)))
            .andExpect(status().isCreated());

    resultActions.andExpect(jsonPath("$.cardNumber").value(cardDTO.getCardNumber()));
  }

  @Test
  public void createCardShouldReturnBadRequestWhenCardNumberAlreadyExists() throws Exception {
    when(service.createCard(Mockito.any(CardDTO.class)))
        .thenThrow(
            new CardAlreadyExistsException(
                "Card with number " + cardDTO.getCardNumber() + " already exists."));
    String jsonBody = objectMapper.writeValueAsString(cardDTO);

    mockMvc
        .perform(
            post("/cards")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(rightUsername, rightUserPassword)))
        .andExpect(status().isUnprocessableContent());
  }

  @Test
  public void createCardShouldReturnBadRequestWhenCardNumberIsBlank() throws Exception {
    cardDTO.setCardNumber("");
    String jsonBody = objectMapper.writeValueAsString(cardDTO);

    mockMvc
        .perform(
            post("/cards")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(rightUsername, rightUserPassword)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void transactionShouldReturnUnauthorizedWhenPasswordIsWrong() throws Exception {
    String jsonBody = objectMapper.writeValueAsString(transactionDTO);

    String wrongPassword = "reallywrongpassword";
    mockMvc
        .perform(
            post("/transactions")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(rightUsername, wrongPassword)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void transactionShouldReturnUnauthorizedWhenUsernameIsWrong() throws Exception {
    String wrongUsername = "Little Mistake";
    when(userDetailsService.loadUserByUsername(wrongUsername))
        .thenThrow(UsernameNotFoundException.class);
    String jsonBody = objectMapper.writeValueAsString(transactionDTO);

    mockMvc
        .perform(
            post("/transactions")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(wrongUsername, rightUserPassword)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void transactionShouldReturnCardDTOWhenPasswordIsRight() throws Exception {
    when(service.executeTransaction(Mockito.any(TransactionDTO.class))).thenReturn(cardDTO);
    String jsonBody = objectMapper.writeValueAsString(transactionDTO);

    ResultActions resultActions =
        mockMvc
            .perform(
                post("/transactions")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(httpBasic(rightUsername, rightUserPassword)))
            .andExpect(status().isOk());

    resultActions.andExpect(jsonPath("$.cardNumber").value(cardDTO.getCardNumber()));
  }

  @Test
  public void transactionShouldReturnBadRequestWhenValueIsNegative() throws Exception {
    transactionDTO.setValue(new BigDecimal("-10.00"));
    String jsonBody = objectMapper.writeValueAsString(transactionDTO);

    mockMvc
        .perform(
            post("/transactions")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(rightUsername, rightUserPassword)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void transactionShouldReturnBadRequestWhenValueIsNull() throws Exception {
    transactionDTO.setValue(null);
    String jsonBody = objectMapper.writeValueAsString(transactionDTO);

    mockMvc
        .perform(
            post("/transactions")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(rightUsername, rightUserPassword)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void transactionShouldReturnUnauthorizedPasswordIsBlank() throws Exception {
    String jsonBody = objectMapper.writeValueAsString(transactionDTO);

    mockMvc
        .perform(
            post("/transactions")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(rightUsername, "")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void transactionShouldReturnUnauthorizedWhenUsernameIsBlank() throws Exception {
    String jsonBody = objectMapper.writeValueAsString(transactionDTO);

    mockMvc
        .perform(
            post("/transactions")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("", rightUserPassword)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void transactionShouldReturnBadRequestWhenValueIsZero() throws Exception {
    transactionDTO.setValue(BigDecimal.ZERO);
    String jsonBody = objectMapper.writeValueAsString(transactionDTO);

    mockMvc
        .perform(
            post("/transactions")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(rightUsername, rightUserPassword)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void createCardShouldReturnUnauthorizedWhenNoCredentials() throws Exception {
    String jsonBody = objectMapper.writeValueAsString(cardDTO);

    mockMvc
        .perform(
            post("/cards")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }
}
