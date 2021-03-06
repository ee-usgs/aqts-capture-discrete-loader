package gov.usgs.wma.waterdata;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

@Component
public class ObservationDao {
    private static final Logger LOG = LoggerFactory.getLogger(ObservationDao.class);

    @Autowired
    @Qualifier("jdbcTemplateObservation")
    protected JdbcTemplate jdbcTemplate;

    @Value("classpath:sql/deleteDiscreteGroundWater.sql")
    protected Resource deleteQuery;

    @Value("classpath:sql/insertDiscreteGroundWater.sql")
    protected Resource insertQuery;

    @Transactional
    public int deleteDiscreteGroundWater(String monitoringLocationIdentifier) {
        int rowsDeletedCount = 0;
        try {
            String sql = new String(FileCopyUtils.copyToByteArray(deleteQuery.getInputStream()));
            rowsDeletedCount = jdbcTemplate.update(
                    sql,
                    monitoringLocationIdentifier
            );
        } catch (IOException e) {
            LOG.error("Unable to get SQL statement", e);
            throw new RuntimeException(e);
        }
        return rowsDeletedCount;
    }

    @Transactional
    public int insertDiscreteGroundWater(List<DiscreteGroundWater> discreteGroundWaterList) {
        int rowsInsertedCount = 0;
        try {
            String sql = new String(FileCopyUtils.copyToByteArray(insertQuery.getInputStream()));
            int [] rowsInsertedCounts = jdbcTemplate.batchUpdate(
                    sql,
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setString(1, discreteGroundWaterList.get(i).getFieldVisitIdentifier());
                            ps.setTimestamp(2, discreteGroundWaterList.get(i).getFieldVisitTime());
                            ps.setString(3, discreteGroundWaterList.get(i).getParmCd());
                            ps.setTimestamp(4, discreteGroundWaterList.get(i).getFieldVisitTime());
                            ps.setTimestamp(5, discreteGroundWaterList.get(i).getFieldVisitTime());
                            ps.setString(6, discreteGroundWaterList.get(i).getFieldVisitValue());
                            ps.setString(7, discreteGroundWaterList.get(i).getDatum());
                            ps.setString(8, discreteGroundWaterList.get(i).getCollectionAgency());
                            ps.setString(9, discreteGroundWaterList.get(i).getDateTimeAccuracyCode());
                            ps.setString(10, discreteGroundWaterList.get(i).getDateTimeAccuracyText());
                            ps.setString(11, discreteGroundWaterList.get(i).getUncertainty());
                            ps.setString(12, discreteGroundWaterList.get(i).getNwisMethodCode());
                            ps.setString(13, discreteGroundWaterList.get(i).getMonitoringMethod());
                            ps.setString(14, discreteGroundWaterList.get(i).getApprovalLevel());
                            ps.setString(15, discreteGroundWaterList.get(i).getApprovalLevelDescription());
                            ps.setString(16, discreteGroundWaterList.get(i).getReadingQualifiers());
                            ps.setString(17, discreteGroundWaterList.get(i).getAgencyCode());
                            ps.setString(18, discreteGroundWaterList.get(i).getLocationIdentifier());
                        }
                        @Override
                        public int getBatchSize() {
                            return discreteGroundWaterList.size();
                        }
                    }
            );
            rowsInsertedCount = Arrays.stream(rowsInsertedCounts).sum();
        } catch (IOException e) {
            LOG.error("Unable to get SQL statement", e);
            throw new RuntimeException(e);
        }
        return rowsInsertedCount;
    }
}
