package io.github.pyvesb.alexarevolutionarycalendar.date;

import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ca.rmen.lfrc.FrenchRevolutionaryCalendarDate;

class RevolutionaryDateProviderTest {

	private RevolutionaryDateProvider underTest;

	@Nested
	class ParseISO8601CalendarDateTest {

		@BeforeEach
		void setUp() {
			underTest = new RevolutionaryDateProvider(null);
		}

		@Test
		void shouldProvideRevolutionaryDateForISO8601CalendarDate() {
			Optional<FrenchRevolutionaryCalendarDate> actualDate1 = underTest.parseISO8601CalendarDate("2018-03-29", FRENCH);

			assertTrue(actualDate1.isPresent());
			assertEquals(new FrenchRevolutionaryCalendarDate(Locale.FRENCH, 226, 7, 9, 0, 0, 0), actualDate1.get());

			Optional<FrenchRevolutionaryCalendarDate> actualDate2 = underTest.parseISO8601CalendarDate("1792-09-22", FRENCH);

			assertTrue(actualDate2.isPresent());
			assertEquals(new FrenchRevolutionaryCalendarDate(Locale.FRENCH, 1, 1, 1, 0, 0, 0), actualDate2.get());
		}

		@Test
		void shouldNotProvideRevolutionaryDateIfISO8601CalendarDateBeforeRevolutionaryCalendarStart() {
			assertFalse(underTest.parseISO8601CalendarDate("1792-09-21", FRENCH).isPresent());
		}

		@Test
		void shouldNotProvideRevolutionaryDateIfISO8601CalendarDateAfterRevolutionaryCalendarEnd() {
			assertFalse(underTest.parseISO8601CalendarDate("3001-01-01", FRENCH).isPresent());
		}

		@ParameterizedTest
		@ValueSource(strings = { "2018-13-24", "2018-06-35", "2018-06-00", "2018-00-35", "0018-00-35", "2018-010-35" })
		void shouldNotProvideRevolutionaryDateForInvalidCalendarDates(String date) {
			assertFalse(underTest.parseISO8601CalendarDate(date, FRENCH).isPresent());
		}

		// See https://developer.amazon.com/docs/custom-skills/slot-type-reference.html#date for input examples.
		@ParameterizedTest
		@ValueSource(strings = { "2018-W48", "2018-W12-WE", "2018-02", "2018-SP", "1999", "200X" })
		void shouldNotProvideRevolutionaryDateForOtherISO8601DateFormats(String date) {
			assertFalse(underTest.parseISO8601CalendarDate(date, FRENCH).isPresent());
		}

		@Test
		void shouldNotProvideRevolutionaryDateIfInputNull() {
			assertFalse(underTest.parseISO8601CalendarDate(null, FRENCH).isPresent());
		}
	}

	@Nested
	class ProvideCurrentDateTest {

		@BeforeEach
		void setUp() {
			underTest = new RevolutionaryDateProvider(Clock.fixed(Instant.ofEpochMilli(1522358369934L), ZoneId.of("UTC")));
		}

		@Test
		void shouldProvideCurrentRevolutionaryDate() {
			FrenchRevolutionaryCalendarDate actualDate = underTest.provideCurrentDate(ENGLISH);

			assertEquals(226, actualDate.year);
			assertEquals(7, actualDate.month);
			assertEquals(9, actualDate.dayOfMonth);
		}
	}

}
