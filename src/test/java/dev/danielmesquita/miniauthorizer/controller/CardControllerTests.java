package dev.danielmesquita.miniauthorizer.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.danielmesquita.miniauthorizer.dto.CardDTO;
import dev.danielmesquita.miniauthorizer.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CardControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private CardService service;

  @Autowired private ObjectMapper objectMapper;

  private CardDTO cardDTO;

  private String existingCardNumber = "1234567890123456";
  private String nonExistingCardNumber = "0000000000000000";
  private String rightPassword = "password123";
  private String wrongPassword = "wrongpassword";

  @BeforeEach
  public void setUp() {
    cardDTO = new CardDTO();
    cardDTO.setCardNumber(existingCardNumber);
    cardDTO.setPassword(rightPassword);
    cardDTO.setCardHolderName("Daniel Mesquita");
  }

  @Test
  public void createCardShouldReturnCardDTOCreated() throws Exception {
    Mockito.when(service.createCard(Mockito.any(CardDTO.class))).thenReturn(cardDTO);
    String jsonBody = objectMapper.writeValueAsString(cardDTO);

    ResultActions resultActions =
        mockMvc
            .perform(
                post("/cards")
                    .content(jsonBody)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());

    resultActions.andExpect(jsonPath("$.cardNumber").value(cardDTO.getCardNumber()));
  }
}
