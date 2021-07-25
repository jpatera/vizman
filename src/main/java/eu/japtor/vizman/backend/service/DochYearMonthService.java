package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.DochMonthVw;
import eu.japtor.vizman.backend.entity.DochYearVw;

import java.time.YearMonth;
import java.util.LinkedList;
import java.util.List;

public interface DochYearMonthService {

    LinkedList<DochMonthVw> fetchRepDochMonthByFilter(DochFilter dochFilter);
    LinkedList<DochYearVw> fetchRepDochYearByFilter(DochFilter dochFilter);

    List<DochMonthVw> fetchRepDochMonthForPersonAndYm(Long personId, YearMonth dochYm);
    List<DochYearVw> fetchRepDochYearForPersonAndYear(Long personId, Integer dochYear);

    // ----------------------------------------------------

    class DochFilter {

        YearMonth dochYm = null;
        Integer dochYear = null;
        LinkedList<Long> personIds = new LinkedList<>();

        public YearMonth getDochYm() {
            return dochYm;
        }
        public void setDochYm(YearMonth dochYm) {
            this.dochYm = dochYm;
        }

        public Integer getDochYear() {
            return dochYear;
        }
        public void setDochYear(Integer dochYear) {
            this.dochYear = dochYear;
        }

        public LinkedList<Long> getPersonIds() {
            return personIds;
        }
        public void setPersonIds(LinkedList<Long> personIds) {
            this.personIds = personIds;
        }
    }
}
