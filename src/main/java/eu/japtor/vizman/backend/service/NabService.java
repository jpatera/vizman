package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import eu.japtor.vizman.backend.entity.Nab;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.views.NabListView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NabService {

    Nab fetchOne(Long id);

    Nab saveNab(Nab nabToSave, Operation oper);

    boolean deleteNab(Nab nab);

    List<Nab> fetchAll();

    Nab fetchNabByCnab(String cnab);

//    List<Nab> fetchByFiltersDescOrder(NabListView.NabFilterParams nabFilterParams);

    Page<Nab> fetchByNabFilter(NabFilter nabFilter, List<QuerySortOrder> sortOrders, Pageable pageable);
    long countByNabFilter(NabFilter nabFilter);

    // -----------------------------------------------


    class NabFilter {
        Integer rok;
        String cnab;
        String ckont;
        Boolean vz;
        String text;
        String objednatel;
        String poznamka;

        public NabFilter(
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

        public static final NabFilter getEmpty() {
            return new NabFilter(null, null, null, null, null, null, null);
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
