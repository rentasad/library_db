package rentasad.library.db.helpers.testClasses;

import rentasad.library.db.helpers.InsertHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PositionErrorDbAccess
{
		private final Connection   mySqlConnection;

		public PositionErrorDbAccess(Connection mySqlConnection)
		{
			this.mySqlConnection = mySqlConnection;
		}

		public void add(PositionError positionError) throws SQLException
	{
		new InsertHelper<>(positionError).insertIntoDatabase(mySqlConnection);
	}

	public void deleteByPositionId(Long positionId) throws SQLException
	{
		String query = "DELETE FROM position_errors WHERE position_id = ?";
		try (PreparedStatement ps = this.mySqlConnection.prepareStatement(query))
		{
			ps.setLong(1, positionId);
			ps.executeUpdate();
		}

	}

}

