package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.ZakYmNaklVw;

import java.util.List;

public interface ZakYmNaklVwRepoCustom {

    List<ZakYmNaklVw> findByZakIdsSumByYm(List<Long> zakIds);
}
