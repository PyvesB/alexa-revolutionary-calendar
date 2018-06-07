package io.github.pyvesb.alexarevolutionarycalendar.handlers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.InputBuilder.buildEndedInput;

import org.junit.jupiter.api.Test;

import com.amazon.ask.model.Response;

import utils.InputBuilder;
import utils.UnexpectedEmptyOptional;

class SessionEndedRequestHandlerTest {

	private final SessionEndedRequestHandler underTest = new SessionEndedRequestHandler();

	@Test
	void shouldHandleLaunchRequests() {
		assertTrue(underTest.canHandle(buildEndedInput()));
	}

	@Test
	void shouldNotHandleOtherRequests() {
		assertFalse(underTest.canHandle(InputBuilder.buildIntentInput("RevolutionaryDateWithSlot")));
	}

	@Test
	void shouldReturnEmptyResponse() {
		Response response = underTest.handle(buildEndedInput()).orElseThrow(UnexpectedEmptyOptional::new);

		assertNull(response.getShouldEndSession());
		assertNull(response.getOutputSpeech());
		assertNull(response.getReprompt());
		assertNull(response.getCard());
	}

}
