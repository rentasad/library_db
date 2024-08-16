package rentasad.library.db.helpers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.mockito.Mockito;

public class InsertHelperTest
{

	@AllArgsConstructor
	public static class Data1
	{
		@DBPersisted
		int x;
		@DBPersisted("my_s")
		String s;
		Boolean b;
	}

	@AllArgsConstructor
	@DBPersisted("DATA")
	public static class Data2
	{

	}

	private InsertHelper<Data1> helper1;
	private InsertHelper<Data2> helper2;
	private InsertHelper<Data1> helper3;
	private InsertHelper<Data1> helper4;

	private Data1 obj1;

	@BeforeEach
	void setupObjects()
	{
		obj1 = new Data1(1, "y", null);
		helper1 = new InsertHelper<>(obj1);

		final Data2 obj2 = new Data2();
		helper2 = new InsertHelper<>(obj2);

		helper3 = new InsertHelper<Data1>(new Data1(3, null, Boolean.TRUE), true, InsertHelper.PersistFields.ALL);
		helper4 = new InsertHelper<Data1>(new Data1(4, null, Boolean.FALSE), false, InsertHelper.PersistFields.ALL);
	}

	@Nested
	class GetTableNameMethod
	{

		@Test
		void isClassName()
		{
			assertEquals("Data1", helper1.getTableName());
		}

		@Test
		void canBeDefinedWithAnnotation()
		{
			assertEquals("DATA", helper2.getTableName());
		}
	}

	@Nested
	class GetInsertStatementMethod
	{

		@Test
		void containsRelevantColumns()
		{
			final String insertStatement = helper1.getInsertStatement();

			assertEquals("INSERT INTO Data1 (x,my_s) VALUES (?,?)", insertStatement);
		}

		@Test
		void containsAllNonNullColumnsIfSpecified()
		{

			final String insertStatement = helper3.getInsertStatement();

			assertEquals("INSERT INTO Data1 (x,b) VALUES (?,?)", insertStatement);
		}

		@Test
		void containsAllColumnsIfSpecifiedWithNull()
		{

			final String insertStatement = helper4.getInsertStatement();

			assertEquals("INSERT INTO Data1 (x,my_s,b) VALUES (?,?,?)", insertStatement);
		}
	}

	@Nested
	class GetValuesMethod
	{

		@Test
		void returnsRelevantValues()
		{
			final @NonNull Queue<Object> values = helper1.getValues();

			assertAll(() -> assertEquals(2, values.size()), () -> assertEquals(1, values.poll()), () -> assertEquals("y", values.poll()));
		}

		@Test
		void returnsNonNullValuesOnly()
		{
			final @NonNull Queue<Object> values = helper3.getValues();

			assertAll(() -> assertEquals(2, values.size()), () -> assertEquals(3, values.poll()), () -> assertEquals(Boolean.TRUE, values.poll()));
		}

		@Test
		void canContainNullIfAllowed()
		{
			final @NonNull Queue<Object> values = helper4.getValues();

			assertAll(() -> assertEquals(3, values.size()), () -> assertEquals(4, values.poll()), () -> assertNull(values.poll()), () -> assertEquals(Boolean.FALSE, values.poll()));
		}

		@Test
		void autoReleasesReferences()
		{
			assertNull(helper4.lastValues);

			helper4.getInsertStatement();

			assertNotNull(helper4.lastValues);

			helper4.getValues();

			assertNull(helper4.lastValues);
		}

		@Test
		void ignoresValueMutationsAfterCreatingInsertStatement()
		{

			helper1.getInsertStatement();

			obj1.s = "new";

			final @NonNull Queue<Object> values = helper1.getValues();

			assertAll(() -> assertEquals(2, values.size()), () -> assertEquals(1, values.poll()), () -> assertEquals("y", values.poll()));
		}
	}

	// TODO Test database methods

	@AllArgsConstructor
	public static class DataWithChar {
		@DBPersisted
		Character c;
	}

	@Test
	void setValuesInStatementShouldConvertCharacterToString() throws SQLException
	{
		// Arrange
		DataWithChar data = new DataWithChar('a');
		InsertHelper<DataWithChar> helper = new InsertHelper<>(data);
		String insertStatement = helper.getInsertStatement();

		PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);

		// Act
		helper.setValuesInStatement(preparedStatement);

		// Assert
		Mockito.verify(preparedStatement, Mockito.times(1)).setObject(Mockito.anyInt(), Mockito.eq("a"), Mockito.anyInt());
	}

}
