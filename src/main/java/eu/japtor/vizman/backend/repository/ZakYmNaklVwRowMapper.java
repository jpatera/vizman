package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.ZakYmNaklVw;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;

public class ZakYmNaklVwRowMapper implements RowMapper<ZakYmNaklVw> {

    @Override
    public ZakYmNaklVw mapRow(ResultSet resultSet, int i) throws SQLException {

        ZakYmNaklVw zakn = new ZakYmNaklVw();
        zakn.setId(resultSet.getLong("id"));
        zakn.setZakId(resultSet.getLong("zak_id"));
        zakn.setPersonIdTrans(resultSet.getLong("person_id"));
        Integer ymPruh = resultSet.getInt("ym_pruh");
        zakn.setYmPruh(YearMonth.of(ymPruh / 100, ymPruh % 100));
        zakn.setWorkPruh(resultSet.getBigDecimal("work_pruh"));
        zakn.setWorkP8(resultSet.getBigDecimal("work_P8"));
        zakn.setKoefP8(resultSet.getBigDecimal("koef_P8"));
        zakn.setSazba(resultSet.getBigDecimal("sazba"));
        zakn.setNaklMzda(resultSet.getBigDecimal("nakl_mzda"));
        zakn.setNaklPojist(resultSet.getBigDecimal("nakl_pojist"));
        zakn.setNaklRezie(resultSet.getBigDecimal("nakl_rezie"));
        zakn.setCkont(resultSet.getString("ckont"));
        zakn.setCzak(resultSet.getInt("czak"));
        zakn.setTextKont(resultSet.getString("text_kont"));
        zakn.setTextZak(resultSet.getString("text_zak"));
        return zakn;
    }
}
