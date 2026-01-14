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
import dev.danielmesquita.miniauthorizer.enums.TransactionStatus;
import dev.danielmesquita.miniauthorizer.exception.CardAlreadyExistsException;
import dev.danielmesquita.miniauthorizer.exception.ResourceNotFoundException;
import dev.danielmesquita.miniauthorizer.exception.TransactionException;
import dev.danielmesquita.miniauthorizer.repository.CardRepository;
import dev.danielmesquita.miniauthorizer.repository.UserRepository;
import dev.danielmesquita.miniauthorizer.service.CardService;
import dev.danielmesquita.miniauthorizer.service.CustomAuthenticationProvider;
import dev.danielmesquita.miniauthorizer.service.CustomUserDetailsService;
import java.math.BigDecimal;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Import({SecurityConfig.class, CustomAuthenticationEntryPoint.class})
public class CardControllerIntegrationTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private CardService service;

  @MockitoBean CardRepository cardRepository;

  @MockitoBean UserRepository userRepository;

  @MockitoBean CustomUserDetailsService userDetailsService;

  @MockitoBean private CustomAuthenticationProvider customAuthenticationProvider;

  @Autowired private ObjectMapper objectMapper;

  private CardDTO cardDTO;

  private final String rightPassword = "password123";
  private final String encryptedPassword = new BCryptPasswordEncoder().encode(rightPassword);

  private final String rightUsername = "realuser@legit.com";
  private final String rightUserPassword = "realstrongpassword";
  private final String encryptedUserPassword =
      new BCryptPasswordEncoder().encode(rightUserPassword);

  @BeforeEach
  public void setUp() {
    when(userDetailsService.loadUserByUsername(rightUsername))
        .thenReturn(
            User.builder()
                .username(rightUsername)
                .password(encryptedUserPassword)
                .roles("USER")
                .build());

    cardDTO = new CardDTO();
    cardDTO.setCardNumber("1234567890123456");
    cardDTO.setPassword(rightPassword);
    cardDTO.setCardHolderName("Daniel Mesquita");
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
  public void createCardShouldReturnUnprocessableWhenCardAlreadyExists() throws Exception {
    when(service.createCard(Mockito.any(CardDTO.class)))
        .thenThrow(new CardAlreadyExistsException("Card already exists"));
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
  public void createCardShouldReturnBadRequestWhenPasswordIsBlank() throws Exception {
    cardDTO.setPassword("");
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
  public void getCardBalanceShouldReturnOkWhenCardExists() throws Exception {
    when(service.getBalance(cardDTO.getCardNumber())).thenReturn(new BigDecimal("500.00"));
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/cards/" + cardDTO.getCardNumber())
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(rightUsername, rightUserPassword)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", Matchers.is(500.00)));
  }

  @Test
  public void getCardBalanceShouldReturnNotFoundWhenCardDoesNotExist() throws Exception {
    when(service.getBalance("0000000000000000"))
        .thenThrow(new ResourceNotFoundException("Card not found"));
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/cards/0000000000000000")
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(rightUsername, rightUserPassword)))
        .andExpect(status().isNotFound());
  }

  @Test
  public void getCardBalanceShouldReturnUnauthorizedWhenNoCredentials() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/cards/" + cardDTO.getCardNumber())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void transactionShouldReturnUnprocessableWhenInsufficientBalance() throws Exception {
    when(service.executeTransaction(Mockito.any(TransactionDTO.class)))
        .thenThrow(new TransactionException(TransactionStatus.SALDO_INSUFICIENTE));
    TransactionDTO transactionDTO = new TransactionDTO();
    transactionDTO.setCardNumber(cardDTO.getCardNumber());
    transactionDTO.setPassword(rightPassword);
    transactionDTO.setValue(new BigDecimal("1000.00"));
    String jsonBody = objectMapper.writeValueAsString(transactionDTO);
    mockMvc
        .perform(
            post("/transactions")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(rightUsername, rightUserPassword)))
        .andExpect(status().isUnprocessableContent());
  }

  @Test
  public void transactionShouldReturnUnprocessableWhenCardDoesNotExist() throws Exception {
    when(service.executeTransaction(Mockito.any(TransactionDTO.class)))
        .thenThrow(new TransactionException(TransactionStatus.CARTAO_INEXISTENTE));
    TransactionDTO transactionDTO = new TransactionDTO();
    transactionDTO.setCardNumber("0000000000000000");
    transactionDTO.setPassword(rightPassword);
    transactionDTO.setValue(new BigDecimal("10.00"));
    String jsonBody = objectMapper.writeValueAsString(transactionDTO);
    mockMvc
        .perform(
            post("/transactions")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(rightUsername, rightUserPassword)))
        .andExpect(status().isUnprocessableContent());
  }

  @Test
  public void transactionShouldReturnUnprocessableWhenPasswordIsWrong() throws Exception {
    when(service.executeTransaction(Mockito.any(TransactionDTO.class)))
        .thenThrow(new TransactionException(TransactionStatus.SENHA_INVALIDA));
    TransactionDTO transactionDTO = new TransactionDTO();
    transactionDTO.setCardNumber(cardDTO.getCardNumber());
    transactionDTO.setPassword("wrongpassword");
    transactionDTO.setValue(new BigDecimal("10.00"));
    String jsonBody = objectMapper.writeValueAsString(transactionDTO);
    mockMvc
        .perform(
            post("/transactions")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(rightUsername, rightUserPassword)))
        .andExpect(status().isUnprocessableContent());
  }

  @Test
  public void transactionShouldReturnBadRequestWhenCardNumberIsBlank() throws Exception {
    TransactionDTO transactionDTO = new TransactionDTO();
    transactionDTO.setCardNumber("");
    transactionDTO.setPassword(rightPassword);
    transactionDTO.setValue(new BigDecimal("10.00"));
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
  public void errorResponseShouldReturnCustomErrorStructure() throws Exception {
    when(service.getBalance("0000000000000000"))
        .thenThrow(new ResourceNotFoundException("Card not found"));
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/cards/0000000000000000")
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(rightUsername, rightUserPassword)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.timeStamp").exists())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Card not found"))
        .andExpect(jsonPath("$.path").exists());
  }

  @Test
  public void allEndpointsShouldRequireAuthentication() throws Exception {
    String jsonBody = objectMapper.writeValueAsString(cardDTO);
    mockMvc
        .perform(
            post("/cards")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/cards/" + cardDTO.getCardNumber())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
    TransactionDTO transactionDTO = new TransactionDTO();
    transactionDTO.setCardNumber(cardDTO.getCardNumber());
    transactionDTO.setPassword(rightPassword);
    transactionDTO.setValue(new BigDecimal("10.00"));
    String txJson = objectMapper.writeValueAsString(transactionDTO);
    mockMvc
        .perform(
            post("/transactions")
                .content(txJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }
}
