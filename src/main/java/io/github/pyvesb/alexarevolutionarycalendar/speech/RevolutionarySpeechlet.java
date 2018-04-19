package io.github.pyvesb.alexarevolutionarycalendar.speech;

import java.time.Clock;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.ui.Image;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.StandardCard;

import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;
import io.github.pyvesb.alexarevolutionarycalendar.date.FrenchObjectOfTheDay;
import io.github.pyvesb.alexarevolutionarycalendar.date.RevolutionaryDateProvider;

/**
 * Class used to process speechlet requests issued by Alexa users.
 * 
 * @author Pyves
 *
 */
public class RevolutionarySpeechlet implements SpeechletV2 {

	private static final Logger LOGGER = LogManager.getLogger(RevolutionarySpeechlet.class);
	private static final String[] DATE_PLACEHOLDERS = new String[] { "WEEKDAY", "DAY", "MONTH", "YEAR", "TYPE", "OBJECT" };

	private final RevolutionaryDateProvider revolutionaryDateProvider;

	public RevolutionarySpeechlet() {
		// When users ask for a specific date, Alexa will provide an ISO8601 date computed with the right timezone. When
		// they just ask for the date of the day (e.g. "give me the date"), there is no simple way to infer their
		// timezone. Default to Paris. See https://forums.developer.amazon.com/questions/8857/get-timezone.html
		this(Clock.system(ZoneId.of("Europe/Paris")));
	}

	RevolutionarySpeechlet(Clock clock) {
		revolutionaryDateProvider = new RevolutionaryDateProvider(clock);
	}

	@Override
	public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> envelope) {
		LOGGER.info("Session started (session={}, locale={})", envelope.getSession().getSessionId(),
				envelope.getRequest().getLocale());
	}

	@Override
	public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> envelope) {
		Locale locale = envelope.getRequest().getLocale();
		LOGGER.info("Launch (session={}, locale={})", envelope.getSession().getSessionId(), locale);
		return getAskResponse("launch", "card-history", locale);
	}

	@Override
	public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> envelope) {
		Intent intent = envelope.getRequest().getIntent();
		String sessionId = envelope.getSession().getSessionId();
		String intentName = (intent == null) ? null : intent.getName();
		Locale locale = envelope.getRequest().getLocale();
		LOGGER.info("Received intent (session={}, name={}, locale={})", sessionId, intentName, locale);

		if ("RevolutionaryDateWithSlot".equals(intentName)) {
			return handleIntentWithSlot(sessionId, intent, locale);
		} else if ("RevolutionaryDateOfTheDay".equals(intentName)) {
			return getDateResponse("date-of-the-day", revolutionaryDateProvider.provideCurrentDate(locale), locale);
		} else if ("AMAZON.HelpIntent".equals(intentName)) {
			return getAskResponse("help", "card-examples", locale);
		} else if ("AMAZON.CancelIntent".equals(intentName) || "AMAZON.StopIntent".equals(intentName)) {
			return getCancelStopResponse(locale);
		}
		return getAskResponse("unsupported", "card-examples", locale);
	}

	@Override
	public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> envelope) {
		LOGGER.info("Session ended (session={}, locale={})", envelope.getSession().getSessionId(),
				envelope.getRequest().getLocale());
	}

	/**
	 * Creates an Ask response with a simple card and reprompt included.
	 * 
	 * @param speechTextKey key to get message that will be spoken to the user.
	 * @param cardContentKey key to get message that will be shown on the card.
	 * @param locale the locale to be used to construct the response.
	 * @return the resulting simple card and speech text.
	 */
	private SpeechletResponse getAskResponse(String speechTextKey, String cardContentKey, Locale locale) {
		ResourceBundle messages = ResourceBundle.getBundle("messages", locale);
		String speechText = messages.getString(speechTextKey);
		PlainTextOutputSpeech outputSpeech = getPlainTextOutputSpeech(speechText);
		Reprompt reprompt = getReprompt(outputSpeech);
		SimpleCard card = getSimpleCard(cardContentKey, messages);
		return SpeechletResponse.newAskResponse(outputSpeech, reprompt, card);
	}

	/**
	 * Creates a Tell response saying goodbye.
	 * 
	 * @param locale the locale to be used to construct the response.
	 * @return the resulting speech text.
	 */
	private SpeechletResponse getCancelStopResponse(Locale locale) {
		ResourceBundle messages = ResourceBundle.getBundle("messages", locale);
		PlainTextOutputSpeech speech = getPlainTextOutputSpeech(messages.getString("cancel-stop"));
		return SpeechletResponse.newTellResponse(speech);
	}

	/**
	 * Handles an intent which is expected to contain a date slot.
	 * 
	 * @param sessionId the id of the session in which this intent was requested
	 * @param intent the request intent
	 * @param locale the locale to be used to construct the response.
	 * @return either a date response if the handling was successful or else an ask response.
	 */
	private SpeechletResponse handleIntentWithSlot(String sessionId, Intent intent, Locale locale) {
		Slot dateSlot = intent.getSlots().get("date");
		String dateValue = null;
		if (dateSlot != null) {
			dateValue = dateSlot.getValue();
			Optional<FrenchRevolutionaryCalendarDate> parsedDate = revolutionaryDateProvider
					.parseISO8601CalendarDate(dateValue, locale);
			if (parsedDate.isPresent()) {
				LOGGER.info("Parsed date (session={}, date={}, locale={})", sessionId, dateValue, locale);
				return getDateResponse("date-with-slot", parsedDate.get(), locale);
			}
		}
		LOGGER.warn("Unparsable date (session={}, date={}, locale={})", sessionId, dateValue, locale);
		return getAskResponse("error", "card-examples", locale);
	}

	/**
	 * Creates a Tell response with a standard card and date information.
	 * 
	 * @param responseKey the key to get a localised message containing date placeholders.
	 * @param date the revolutionary date to include in the response.
	 * @param locale the locale to be used to construct the response.
	 * @return the resulting standard card and speech text.
	 */
	private SpeechletResponse getDateResponse(String responseKey, FrenchRevolutionaryCalendarDate date, Locale locale) {
		ResourceBundle messages = ResourceBundle.getBundle("messages", locale);

		boolean french = Locale.FRENCH.getLanguage().equals(locale.getLanguage());
		String speechText = StringUtils.replaceEach(messages.getString(responseKey), DATE_PLACEHOLDERS,
				new String[] { date.getWeekdayName(), getDayOfMonthOrdinal(date, french), date.getMonthName(),
						Integer.toString(date.year), date.getObjectTypeName(), getReadableObjectOfTheDay(date, french) });
		PlainTextOutputSpeech speech = getPlainTextOutputSpeech(speechText);

		String cardText = date.getWeekdayName() + ", " + date.dayOfMonth + " " + date.getMonthName() + " " + date.year + "\n"
				+ date.getObjectOfTheDay();
		StandardCard card = getStandardCard(cardText, messages);

		return SpeechletResponse.newTellResponse(speech, card);
	}

	/**
	 * Creates an ordinal string representing the day of month for a given date. The returned string is not necessarily
	 * linguistically correct but should be the minimal form needed to be correctly interpreted by Alexa.
	 * 
	 * @param date the date to get the day of month from.
	 * @param french true if the language to use is French.
	 * @return a string representing a number that can be interpreted as an ordinal by Alexa.
	 */
	private String getDayOfMonthOrdinal(FrenchRevolutionaryCalendarDate date, boolean french) {
		if (french) {
			return date.dayOfMonth == 1 ? "premier" : Integer.toString(date.dayOfMonth);
		}
		// Alexa is clever enough to correctly process ordinals such as "2th".
		return date.dayOfMonth + "th";
	}

	/**
	 * Creates a string representing the object of the day for a given date. The returned string contains an article so
	 * that it can naturally be read out by Alexa.
	 * 
	 * @param date the date to get the object of the day from.
	 * @param french true if the language to use is French.
	 * @return a string containing the object of that day and an article, e.g. "the Apple".
	 */
	private String getReadableObjectOfTheDay(FrenchRevolutionaryCalendarDate date, boolean french) {
		return french ? FrenchObjectOfTheDay.values()[date.getDayInYear() - 1].getArticle() + date.getObjectOfTheDay()
				: "the " + date.getObjectOfTheDay();
	}

	/**
	 * Creates a simple card object.
	 * 
	 * @param contentKey body of the card.
	 * @param messages contains all strings for the locale of the request.
	 * @return the display card to be sent along with the voice response.
	 */
	private SimpleCard getSimpleCard(String contentKey, ResourceBundle messages) {
		SimpleCard card = new SimpleCard();
		card.setTitle(messages.getString("card-title"));
		card.setContent(messages.getString(contentKey));
		return card;
	}

	/**
	 * Creates a standard card object.
	 * 
	 * @param text body of the card.
	 * @param messages contains all strings for the locale of the request.
	 * @return the display card to be sent along with the voice response.
	 */
	private StandardCard getStandardCard(String text, ResourceBundle messages) {
		StandardCard card = new StandardCard();
		card.setTitle(messages.getString("card-title"));
		card.setText(text);
		Image image = new Image();
		image.setLargeImageUrl("https://s3-eu-west-1.amazonaws.com/alexa-revolutionary-calendar/calendar.jpg");
		card.setImage(image);
		return card;
	}

	/**
	 * Retrieves an OutputSpeech object when given a plain text string.
	 * 
	 * @param text the text that should be spoken out to the user.
	 * @return an instance of SpeechOutput.
	 */
	private PlainTextOutputSpeech getPlainTextOutputSpeech(String text) {
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(text);
		return speech;
	}

	/**
	 * Returns a reprompt object. Used in Ask responses where to make the user able to respond to speech.
	 * 
	 * @param outputSpeech an OutputSpeech object that will be said once and repeated if necessary.
	 * @return Reprompt instance.
	 */
	private Reprompt getReprompt(OutputSpeech outputSpeech) {
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(outputSpeech);
		return reprompt;
	}

}
