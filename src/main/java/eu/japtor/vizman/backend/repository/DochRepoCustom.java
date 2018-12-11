package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Doch;

import java.time.LocalDate;
import java.util.List;

public interface DochRepoCustom {

    public List<Doch> getPreviousDochRecords(final Long personId, final LocalDate beforeDDate);

}
