package br.edu.securitystore.platform.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/admin/database")
@PreAuthorize("hasAuthority('DATABASE_INSPECT_READ')")
public class DatabaseInspectorController {
    private static final int MAX_PAGE_SIZE = 100;
    private static final int MAX_OFFSET = 100_000;

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public DatabaseInspectorController(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/tables")
    public List<TableView> tables() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metadata = connection.getMetaData();
            List<TableView> tables = new ArrayList<>();
            try (ResultSet result = metadata.getTables(connection.getCatalog(), null, "%", new String[]{"TABLE", "VIEW"})) {
                while (result.next()) {
                    tables.add(new TableView(result.getString("TABLE_NAME"), result.getString("TABLE_TYPE")));
                }
            }
            tables.sort(Comparator.comparing(TableView::name, String.CASE_INSENSITIVE_ORDER));
            return tables;
        } catch (SQLException exception) {
            throw databaseError(exception);
        }
    }

    @GetMapping("/tables/{tableName}")
    public TableData table(@PathVariable String tableName,
                           @RequestParam(defaultValue = "0") int offset,
                           @RequestParam(defaultValue = "100") int limit) {
        if (offset < 0 || offset > MAX_OFFSET) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Offset fora do limite permitido");
        }
        if (limit < 1 || limit > MAX_PAGE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O limite deve estar entre 1 e 100 registros");
        }

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metadata = connection.getMetaData();
            String tableType = findTableType(metadata, connection.getCatalog(), tableName);
            if (tableType == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tabela não encontrada");
            }

            String quotedTable = quoteIdentifier(metadata, tableName);
            List<ColumnView> columns = columns(metadata, connection.getCatalog(), tableName);
            Set<String> primaryKeys = primaryKeys(metadata, connection.getCatalog(), tableName);
            List<ColumnView> describedColumns = columns.stream()
                    .map(column -> new ColumnView(column.name(), column.type(), column.nullable(), primaryKeys.contains(column.name())))
                    .toList();
            String sql = "SELECT * FROM " + quotedTable + " LIMIT ? OFFSET ?";
            List<Map<String, Object>> rows = new ArrayList<>(jdbcTemplate.queryForList(sql, limit + 1, offset));
            boolean hasMore = rows.size() > limit;
            if (hasMore) rows.remove(rows.size() - 1);
            return new TableData(tableName, tableType, describedColumns, rows, offset, limit, hasMore);
        } catch (SQLException exception) {
            throw databaseError(exception);
        }
    }

    private String findTableType(DatabaseMetaData metadata, String catalog, String tableName) throws SQLException {
        try (ResultSet result = metadata.getTables(catalog, null, "%", new String[]{"TABLE", "VIEW"})) {
            while (result.next()) {
                if (tableName.equals(result.getString("TABLE_NAME"))) return result.getString("TABLE_TYPE");
            }
        }
        return null;
    }

    private List<ColumnView> columns(DatabaseMetaData metadata, String catalog, String tableName) throws SQLException {
        List<ColumnView> columns = new ArrayList<>();
        try (ResultSet result = metadata.getColumns(catalog, null, tableName, "%")) {
            while (result.next()) {
                columns.add(new ColumnView(result.getString("COLUMN_NAME"), result.getString("TYPE_NAME"),
                        result.getInt("NULLABLE") != DatabaseMetaData.columnNoNulls, false));
            }
        }
        if (columns.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tabela não encontrada");
        return columns;
    }

    private Set<String> primaryKeys(DatabaseMetaData metadata, String catalog, String tableName) throws SQLException {
        Set<String> keys = new HashSet<>();
        try (ResultSet result = metadata.getPrimaryKeys(catalog, null, tableName)) {
            while (result.next()) keys.add(result.getString("COLUMN_NAME"));
        }
        return keys;
    }

    private String quoteIdentifier(DatabaseMetaData metadata, String identifier) throws SQLException {
        String quote = metadata.getIdentifierQuoteString().trim();
        if (quote.isEmpty()) return identifier;
        return quote + identifier.replace(quote, quote + quote) + quote;
    }

    private ResponseStatusException databaseError(SQLException exception) {
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível consultar o banco de dados", exception);
    }

    public record TableView(String name, String type) {}
    public record ColumnView(String name, String type, boolean nullable, boolean primaryKey) {}
    public record TableData(String name, String type, List<ColumnView> columns, List<Map<String, Object>> rows,
                            int offset, int limit, boolean hasMore) {}
}

