package net.sf.persist.tests.common;

import net.sf.persist.Persist;
import net.sf.persist.ResultSetIterator;
import org.junit.*;

import java.sql.*;
import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class MockTest {
    private Connection connection;
    private DatabaseMetaData databaseMetaData;
    private ResultSet tableMetadataResultSet;
    private ResultSet columnMetadataResultSet;
    private ResultSet primaryKeysMetadataResultSet;
    private ResultSetMetaData resultSetMetaData;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @Before
    public void setUp() {
        this.connection = createMock(Connection.class);
        this.databaseMetaData = createMock(DatabaseMetaData.class);
        this.tableMetadataResultSet = createMock(ResultSet.class);
        this.columnMetadataResultSet = createMock(ResultSet.class);
        this.primaryKeysMetadataResultSet = createMock(ResultSet.class);
        this.resultSetMetaData = createMock(ResultSetMetaData.class);
        this.preparedStatement = createMock(PreparedStatement.class);
        this.resultSet = createMock(ResultSet.class);
    }

    @Test
    public void testReadIteratorResultSetIteratorClose() throws SQLException {
        expect(connection.getMetaData()).andReturn(databaseMetaData);

        expect(databaseMetaData.supportsGetGeneratedKeys()).andReturn(true);
        expect(databaseMetaData.supportsBatchUpdates()).andReturn(false);
        expect(databaseMetaData.getDatabaseProductName()).andReturn("MockDatabase");
        expect(databaseMetaData.getTables(null, null, "SIMPLE", null)).andReturn(tableMetadataResultSet);

        expect(tableMetadataResultSet.next()).andReturn(true);
        tableMetadataResultSet.close();

        expect(databaseMetaData.getColumns(null, null, "SIMPLE", "%")).andReturn(columnMetadataResultSet);

        expect(columnMetadataResultSet.next()).andReturn(true);
        expect(columnMetadataResultSet.getString(4)).andReturn("id");
        expect(columnMetadataResultSet.next()).andReturn(true);
        expect(columnMetadataResultSet.getString(4)).andReturn("string_col");
        expect(columnMetadataResultSet.next()).andReturn(true);
        expect(columnMetadataResultSet.getString(4)).andReturn("int_col");
        expect(columnMetadataResultSet.next()).andReturn(false);

        expect(databaseMetaData.getPrimaryKeys(null, null, "SIMPLE")).andReturn(primaryKeysMetadataResultSet);

        expect(connection.prepareStatement("select id,string_col,int_col from SIMPLE")).andReturn(preparedStatement);

        expect(primaryKeysMetadataResultSet.next()).andReturn(true);
        expect(primaryKeysMetadataResultSet.getString(4)).andReturn("id");
        expect(primaryKeysMetadataResultSet.next()).andReturn(false);

        expect(preparedStatement.executeQuery()).andReturn(resultSet);

        expect(resultSet.next()).andReturn(true);
        resultSet.close();

        preparedStatement.close();

        // mock setup complete

        replay(connection, databaseMetaData, tableMetadataResultSet, columnMetadataResultSet, primaryKeysMetadataResultSet, preparedStatement, resultSet);

        Persist persist = new Persist(connection);
        ResultSetIterator<Simple> iterator = persist.readIterator(Simple.class);

        // read

        iterator.close();
    }

    @Test
    public void testReadMapIteratorResultSetIteratorClose() throws SQLException {
        expect(connection.prepareStatement("SELECT * FROM SIMPLE")).andReturn(preparedStatement);

        expect(preparedStatement.executeQuery()).andReturn(resultSet);

        expect(resultSet.next()).andReturn(true);
        resultSet.close();

        preparedStatement.close();

        // mock setup complete

        replay(connection, preparedStatement, resultSet);

        Persist persist = new Persist(connection);
        ResultSetIterator<Map<String, Object>> iterator = persist.readMapIterator("SELECT * FROM SIMPLE");

        iterator.close();
    }

    @Test
    public void testLoadObjectForEnum() throws SQLException {
        expect(resultSet.getMetaData()).andReturn(resultSetMetaData);
        expect(resultSetMetaData.getColumnCount()).andReturn(1);
        expect(resultSet.getString(1)).andReturn("BLUE");

        // mock setup complete

        replay(connection, resultSetMetaData, resultSet);

        Persist persist = new Persist(connection);
        Color color = persist.loadObject(Color.class, resultSet);

        assertEquals(Color.BLUE, color);
    }

    @Test
    public void testNativeBooleanMapping() throws SQLException {
        expect(connection.getMetaData()).andReturn(databaseMetaData);

        expect(databaseMetaData.supportsGetGeneratedKeys()).andReturn(true);
        expect(databaseMetaData.supportsBatchUpdates()).andReturn(false);
        expect(databaseMetaData.getDatabaseProductName()).andReturn("MockDatabase");
        expect(databaseMetaData.getTables(null, null, "SIMPLE", null)).andReturn(tableMetadataResultSet);

        expect(tableMetadataResultSet.next()).andReturn(true);
        tableMetadataResultSet.close();

        expect(databaseMetaData.getColumns(null, null, "SIMPLE", "%")).andReturn(columnMetadataResultSet);

        expect(columnMetadataResultSet.next()).andReturn(true);
        expect(columnMetadataResultSet.getString(4)).andReturn("id");
        expect(columnMetadataResultSet.next()).andReturn(true);
        expect(columnMetadataResultSet.getString(4)).andReturn("complete");
        expect(columnMetadataResultSet.next()).andReturn(false);

        expect(databaseMetaData.getPrimaryKeys(null, null, "SIMPLE")).andReturn(primaryKeysMetadataResultSet);

        expect(primaryKeysMetadataResultSet.next()).andReturn(true);
        expect(primaryKeysMetadataResultSet.getString(4)).andReturn("id");
        expect(primaryKeysMetadataResultSet.next()).andReturn(false);

        expect(connection.prepareStatement("insert into SIMPLE(complete)values(?)")).andReturn(preparedStatement);

        preparedStatement.setBoolean(1, true);
        expect(preparedStatement.executeUpdate()).andReturn(1);
        preparedStatement.close();

        expect(connection.prepareStatement("update SIMPLE set complete=? where id=?")).andReturn(preparedStatement);

        preparedStatement.setBoolean(1, false);
        preparedStatement.setLong(2, 0);
        expect(preparedStatement.executeUpdate()).andReturn(1);
        preparedStatement.close();

        // mock setup complete

        replay(connection, databaseMetaData, tableMetadataResultSet, columnMetadataResultSet, primaryKeysMetadataResultSet, preparedStatement);

        Simple12 simple = new Simple12();
        simple.setComplete(true);

        Persist persist = new Persist(connection);

        persist.insert(simple);

        simple.setComplete(false);

        persist.update(simple);
    }
}
