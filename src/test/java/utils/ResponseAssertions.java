package utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.amazon.ask.model.Response;
import com.amazon.ask.model.ui.SimpleCard;
import com.amazon.ask.model.ui.SsmlOutputSpeech;
import com.amazon.ask.model.ui.StandardCard;

public class ResponseAssertions {

	public static void assertSpeech(Response response, String text) {
		assertEquals("<speak>" + text + "</speak>", ((SsmlOutputSpeech) response.getOutputSpeech()).getSsml());
	}

	public static void assertSimpleCard(Response response, String title, String content) {
		SimpleCard card = (SimpleCard) response.getCard();
		assertEquals(title, card.getTitle());
		assertEquals(content, card.getContent());
	}

	public static void assertStandardCard(Response response, String title, String text) {
		StandardCard card = (StandardCard) response.getCard();
		assertEquals(title, card.getTitle());
		assertEquals(text, card.getText());
		assertEquals("https://s3-eu-west-1.amazonaws.com/alexa-revolutionary-calendar/calendar.jpg",
				card.getImage().getLargeImageUrl());
	}

	private ResponseAssertions() {
		// Not used.
	}

}
