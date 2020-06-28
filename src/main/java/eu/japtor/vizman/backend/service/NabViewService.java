package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import eu.japtor.vizman.backend.entity.NabVw;
import eu.japtor.vizman.ui.components.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NabViewService {

    NabVw fetchOne(Long id);

    NabVw saveNab(NabVw nabVwToSave, Operation oper);

    boolean deleteNab(NabVw nabVw);

    List<NabVw> fetchAll();

    NabVw fetchNabByCnab(String cnab);

    List<Integer> fetchRokList();

    List<NabVw> fetchFilteredList(NabViewFilter nabViewFilter);

//    List<NabVw> fetchByFiltersDescOrder(NabListView.NabFilterParams nabFilterParams);

    Page<NabVw> fetchByNabFilter(NabViewFilter nabViewFilter, List<QuerySortOrder> sortOrders, Pageable pageable);
    long countByNabFilter(NabViewFilter nabViewFilter);


    // -----------------------------------------------


    class NabViewFilter {
        Integer rok;
        String cnab;
        String ckont;
        Boolean vz;
        String text;
        String objednatel;
        String poznamka;

        public NabViewFilter(
                Integer rok
                , String cnab
                , String ckont
                , Boolean vz
                , String text
                , String objednatel
                , String poznamka
        ) {
            this.rok = rok;
            this.cnab = cnab;
            this.ckont = ckont;
            this.vz = vz;
            this.text = text;
            this.objednatel = objednatel;
            this.poznamka = poznamka;
        }

        public static final NabViewFilter getEmpty() {
            return new NabViewFilter(null, null, null, null, null, null, null);
        }

        public Integer getRok() {
            return rok;
        }
        public void setRok(Integer rok) {
            this.rok = rok;
        }

        public String getCnab() {
            return cnab;
        }
        public void setCnab(String cnab) {
            this.cnab = cnab;
        }

        public String getCkont() {
            return ckont;
        }
        public void setCkont(String ckont) {
            this.ckont = ckont;
        }

        public Boolean getVz() {
            return vz;
        }
        public void setVz(Boolean vz) {
            this.vz = vz;
        }

        public String getText() {
            return text;
        }
        public void setText(String text) {
            this.text = text;
        }

        public String getObjednatel() {
            return objednatel;
        }
        public void setObjednatel(String objednatel) {
            this.objednatel = objednatel;
        }

        public String getPoznamka() {
            return poznamka;
        }
        public void setPoznamka(String poznamka) {
            this.poznamka = poznamka;
        }
    }

}
