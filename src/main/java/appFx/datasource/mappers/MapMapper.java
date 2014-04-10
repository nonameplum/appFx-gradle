package appFx.datasource.mappers;

import appFx.datasource.helpers.DatasourceMap;
import javafx.beans.property.SimpleObjectProperty;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapMapper implements ResultSetMapper<DatasourceMap> {

    @Override
    public DatasourceMap map(int index, ResultSet r, StatementContext ctx) throws SQLException {
//        Map<String, Object> obj = new LinkedHashMap();
        DatasourceMap datasourceMap = new DatasourceMap();
        for (int i = 1; i <= r.getMetaData().getColumnCount(); i++) {
            Object rsObj = r.getObject(i);
            datasourceMap.put(r.getMetaData().getColumnLabel(i), new SimpleObjectProperty<>(rsObj));
        }
        return datasourceMap;
    }
}
