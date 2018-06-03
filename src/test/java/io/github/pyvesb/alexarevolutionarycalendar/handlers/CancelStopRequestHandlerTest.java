package io.github.pyvesb.alexarevolutionarycalendar.handlers;

import static java.util.Locale.UK;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.InputBuilder.buildIntentInput;
import static utils.InputBuilder.buildLaunchInput;
import static utils.ResponseAssertions.assertSpeech;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.amazon.ask.model.Response;

import utils.UnexpectedEmptyOptional;

class CancelStopRequestHandlerTest {

	private final CancelStopIntentHandler underTest = new CancelStopIntentHandler();

	@ParameterizedTest
	@ValueSource(strings = { "AMAZON.CancelIntent", "AMAZON.StopIntent" })
	void shouldHandleIntentRequestsWithCancelOrStopIntentName(String intentName) {
		assertTrue(underTest.canHandle(buildIntentInput(intentName, UK)));
	}

	@Test
	void shouldNotHandleIntentRequestsWithDifferentName() {
		assertFalse(underTest.canHandle(buildIntentInput("AMAZON.HelpIntent", UK)));
	}

	@Test
	void shouldNotHandleOtherRequests() {
		assertFalse(underTest.canHandle(buildLaunchInput(UK)));
	}

	@Test
	@Tag("en_GB-locale")
	void shouldReturnCancelStopResponse() {
		Response response = underTest.handle(buildIntentInput("AMAZON.CancelIntent", UK))
				.orElseThrow(UnexpectedEmptyOptional::new);

		assertTrue(response.getShouldEndSession());
		assertSpeech(response, "Okay. See you soon!");
		assertNull(response.getCard());
	}

}
