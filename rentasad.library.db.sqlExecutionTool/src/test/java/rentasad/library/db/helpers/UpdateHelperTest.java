package rentasad.library.db.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UpdateHelperTest {
	private Connection connection;
	private PreparedStatement preparedStatement;
	private Customer customer;

	@BeforeEach
	void setUp() throws SQLException {
		connection = Mockito.mock(Connection.class);
		preparedStatement = Mockito.mock(PreparedStatement.class);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

		customer = new Customer();
		customer.setId(1);
		customer.setName("Jane Doe");
		customer.setAge(25);
	}

	@Test
	void testUpdate() throws SQLException, IllegalAccessException, SQLException
	{
		UpdateHelper.update(connection, customer);

		verify(connection).prepareStatement("UPDATE customer_table SET customer_name = ?, customer_age = ? WHERE customer_id = ?");
		verify(preparedStatement).setObject(1, "Jane Doe");
		verify(preparedStatement).setObject(2, 25);
		verify(preparedStatement).setObject(3, 1);
		verify(preparedStatement).executeUpdate();
	}

	@Test
	void testNoPrimaryKey() {
		class NoPrimaryKeyCustomer {
			@DBPersisted("customer_name")
			private String name;
		}

		NoPrimaryKeyCustomer noPrimaryKeyCustomer = new NoPrimaryKeyCustomer();
		noPrimaryKeyCustomer.name = "No PK";

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
			UpdateHelper.update(connection, noPrimaryKeyCustomer);
		});

		assertEquals("No primary key field found in class " + NoPrimaryKeyCustomer.class.getName(), thrown.getMessage());
	}

	// Example usage:
	@DBPersisted("customer_table")
	class Customer {
		@PrimaryKey
		@DBPersisted("customer_id")
		private int id;
		@DBPersisted("customer_name")
		private String name;
		@DBPersisted("customer_age")
		private int age;

		// Getters and setters...
		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}
	}

}
