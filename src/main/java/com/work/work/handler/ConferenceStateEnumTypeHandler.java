package com.work.work.handler;

import com.work.work.enums.ConferenceStateEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(ConferenceStateEnum.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class ConferenceStateEnumTypeHandler extends BaseTypeHandler<ConferenceStateEnum> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                    ConferenceStateEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getDbValue());
    }

    @Override
    public ConferenceStateEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String dbValue = rs.getString(columnName);
        return dbValue == null ? null : ConferenceStateEnum.fromDbValue(dbValue);
    }

    @Override
    public ConferenceStateEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String dbValue = rs.getString(columnIndex);
        return dbValue == null ? null : ConferenceStateEnum.fromDbValue(dbValue);
    }

    @Override
    public ConferenceStateEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String dbValue = cs.getString(columnIndex);
        return dbValue == null ? null : ConferenceStateEnum.fromDbValue(dbValue);
    }
}
