package eu.japtor.vizman.backend.entity;

import jdk.nashorn.internal.objects.annotations.Function;

import java.util.HashMap;
import java.util.Map;

public class ItemNames {

    static Map<ItemType, Map<GrammarShapes, String>> itemNameMaps = new HashMap<>();
    static Map<ItemType, GrammarGender> itemGenderMap = new HashMap<>();

    static {
        Map<GrammarShapes, String>itemUnknownNameMap = new HashMap<>();
        itemUnknownNameMap.put(GrammarShapes.NOM_S, "Položka");
        itemUnknownNameMap.put(GrammarShapes.NOM_P, "Položky");
        itemUnknownNameMap.put(GrammarShapes.GEN_S, "Položky");
        itemUnknownNameMap.put(GrammarShapes.GEN_P, "Položek");
        itemUnknownNameMap.put(GrammarShapes.ACCU_S, "Položku");
        itemUnknownNameMap.put(GrammarShapes.ACCU_P, "Položky");
        itemNameMaps.put(ItemType.UNKNOWN, itemUnknownNameMap);
        itemGenderMap.put(ItemType.UNKNOWN, GrammarGender.FEMININE);

        Map<GrammarShapes, String>itemKontNameMap = new HashMap<>();
        itemKontNameMap.put(GrammarShapes.NOM_S, "Kontrakt");
        itemKontNameMap.put(GrammarShapes.NOM_P, "Kontrakty");
        itemKontNameMap.put(GrammarShapes.GEN_S, "Kontraktu");
        itemKontNameMap.put(GrammarShapes.GEN_P, "Kontraktů");
        itemKontNameMap.put(GrammarShapes.ACCU_S, "Kontrakt");
        itemKontNameMap.put(GrammarShapes.ACCU_P, "Kontrakty");
        itemNameMaps.put(ItemType.KONT, itemKontNameMap);
        itemGenderMap.put(ItemType.KONT, GrammarGender.MASCULINE);

        Map<GrammarShapes, String>itemZakNameMap = new HashMap<>();
        itemZakNameMap.put(GrammarShapes.NOM_S, "Zakázka");
        itemZakNameMap.put(GrammarShapes.NOM_P, "Zakázky");
        itemZakNameMap.put(GrammarShapes.GEN_S, "Zakázky");
        itemZakNameMap.put(GrammarShapes.GEN_P, "Zakázek");
        itemZakNameMap.put(GrammarShapes.ACCU_S, "Zakázku");
        itemZakNameMap.put(GrammarShapes.ACCU_P, "Zakázky");
        itemNameMaps.put(ItemType.ZAK, itemZakNameMap);
        itemGenderMap.put(ItemType.ZAK, GrammarGender.FEMININE);

        Map<GrammarShapes, String>itemZakrNameMap = new HashMap<>();
        itemZakrNameMap.put(GrammarShapes.NOM_S, "Rozpracovanost");
        itemZakrNameMap.put(GrammarShapes.NOM_P, "Rozpracovanost");
        itemZakrNameMap.put(GrammarShapes.GEN_S, "Rozpracovanosti");
        itemZakrNameMap.put(GrammarShapes.GEN_P, "Rozpracovaností");
        itemZakrNameMap.put(GrammarShapes.ACCU_S, "Rozpracovanost");
        itemZakrNameMap.put(GrammarShapes.ACCU_P, "Rozpracovanost");
        itemNameMaps.put(ItemType.ZAKR, itemZakrNameMap);
        itemGenderMap.put(ItemType.ZAKR, GrammarGender.FEMININE);

        Map<GrammarShapes, String>itemSubNameMap = new HashMap<>();
        itemSubNameMap.put(GrammarShapes.NOM_S, "Subdodávka");
        itemSubNameMap.put(GrammarShapes.NOM_P, "Subdodávky");
        itemSubNameMap.put(GrammarShapes.GEN_S, "Subdodávky");
        itemSubNameMap.put(GrammarShapes.GEN_P, "Subdodávek");
        itemSubNameMap.put(GrammarShapes.ACCU_S, "Subdodávku");
        itemSubNameMap.put(GrammarShapes.ACCU_P, "Subdodávky");
        itemNameMaps.put(ItemType.SUB, itemSubNameMap);
        itemGenderMap.put(ItemType.SUB, GrammarGender.FEMININE);

        Map<GrammarShapes, String>itemAkvNameMap = new HashMap<>();
        itemAkvNameMap.put(GrammarShapes.NOM_S, "Akvizice");
        itemAkvNameMap.put(GrammarShapes.NOM_P, "Akvizice");
        itemAkvNameMap.put(GrammarShapes.GEN_S, "Akvizice");
        itemAkvNameMap.put(GrammarShapes.GEN_P, "Akvizic");
        itemAkvNameMap.put(GrammarShapes.ACCU_S, "Akvizici");
        itemAkvNameMap.put(GrammarShapes.ACCU_P, "Akvizice");
        itemNameMaps.put(ItemType.AKV, itemAkvNameMap);
        itemGenderMap.put(ItemType.AKV, GrammarGender.FEMININE);

        Map<GrammarShapes, String>itemRezNameMap = new HashMap<>();
        itemRezNameMap.put(GrammarShapes.NOM_S, "Režie");
        itemRezNameMap.put(GrammarShapes.NOM_P, "Režie");
        itemRezNameMap.put(GrammarShapes.GEN_S, "Režie");
        itemRezNameMap.put(GrammarShapes.GEN_P, "Režií");
        itemRezNameMap.put(GrammarShapes.ACCU_S, "Riežii");
        itemRezNameMap.put(GrammarShapes.ACCU_P, "Režie");
        itemNameMaps.put(ItemType.REZ, itemRezNameMap);
        itemGenderMap.put(ItemType.REZ, GrammarGender.FEMININE);

        Map<GrammarShapes, String>itemFaktNameMap = new HashMap<>();
        itemFaktNameMap.put(GrammarShapes.NOM_S, "Dílčí plnění");
        itemFaktNameMap.put(GrammarShapes.NOM_P, "Dílčí plnění");
        itemFaktNameMap.put(GrammarShapes.GEN_S, "Dílčího plnění");
        itemFaktNameMap.put(GrammarShapes.GEN_P, "Dílčích plnění");
        itemFaktNameMap.put(GrammarShapes.ACCU_S, "Dílčí plnění");
        itemFaktNameMap.put(GrammarShapes.ACCU_P, "Dílčí plnění");
        itemNameMaps.put(ItemType.FAKT, itemFaktNameMap);
        itemGenderMap.put(ItemType.FAKT, GrammarGender.NEUTER);

        Map<GrammarShapes, String>itemKlientNameMap = new HashMap<>();
        itemKlientNameMap.put(GrammarShapes.NOM_S, "Klient");
        itemKlientNameMap.put(GrammarShapes.NOM_P, "Klienti");
        itemKlientNameMap.put(GrammarShapes.GEN_S, "Klienta");
        itemKlientNameMap.put(GrammarShapes.GEN_P, "Klientů");
        itemKlientNameMap.put(GrammarShapes.ACCU_S, "Klienta");
        itemKlientNameMap.put(GrammarShapes.ACCU_P, "Klienty");
        itemNameMaps.put(ItemType.KLI, itemKlientNameMap);
        itemGenderMap.put(ItemType.KLI, GrammarGender.MASCULINE);

        Map<GrammarShapes, String>itemWageNameMap = new HashMap<>();
        itemWageNameMap.put(GrammarShapes.NOM_S, "Mzda");
        itemWageNameMap.put(GrammarShapes.NOM_P, "Mzdy");
        itemWageNameMap.put(GrammarShapes.GEN_S, "Mzdy");
        itemWageNameMap.put(GrammarShapes.GEN_P, "Mezd");
        itemWageNameMap.put(GrammarShapes.ACCU_S, "Mzdu");
        itemWageNameMap.put(GrammarShapes.ACCU_P, "Mzdy");
        itemNameMaps.put(ItemType.WAGE, itemWageNameMap);
        itemGenderMap.put(ItemType.WAGE, GrammarGender.FEMININE);

        Map<GrammarShapes, String>itemPersonNameMap = new HashMap<>();
        itemPersonNameMap.put(GrammarShapes.NOM_S, "Uživatel");
        itemPersonNameMap.put(GrammarShapes.NOM_P, "Uživatelé");
        itemPersonNameMap.put(GrammarShapes.GEN_S, "Uživatele");
        itemPersonNameMap.put(GrammarShapes.GEN_P, "Uživatelů");
        itemPersonNameMap.put(GrammarShapes.ACCU_S, "Uživatele");
        itemPersonNameMap.put(GrammarShapes.ACCU_P, "Uživatele");
        itemNameMaps.put(ItemType.PERSON, itemPersonNameMap);
        itemGenderMap.put(ItemType.PERSON, GrammarGender.MASCULINE);

        Map<GrammarShapes, String>itemNabNameMap = new HashMap<>();
        itemNabNameMap.put(GrammarShapes.NOM_S, "Nabídka");
        itemNabNameMap.put(GrammarShapes.NOM_P, "Nabídky");
        itemNabNameMap.put(GrammarShapes.GEN_S, "Nabídky");
        itemNabNameMap.put(GrammarShapes.GEN_P, "Nabídek");
        itemNabNameMap.put(GrammarShapes.ACCU_S, "Nabídku");
        itemNabNameMap.put(GrammarShapes.ACCU_P, "Nabídky");
        itemNameMaps.put(ItemType.NAB, itemNabNameMap);
        itemGenderMap.put(ItemType.NAB, GrammarGender.FEMININE);

        Map<GrammarShapes, String>itemRoleNameMap = new HashMap<>();
        itemRoleNameMap.put(GrammarShapes.NOM_S, "Role");
        itemRoleNameMap.put(GrammarShapes.NOM_P, "Role");
        itemRoleNameMap.put(GrammarShapes.GEN_S, "Role");
        itemRoleNameMap.put(GrammarShapes.GEN_P, "Rolí");
        itemRoleNameMap.put(GrammarShapes.ACCU_S, "Roli");
        itemRoleNameMap.put(GrammarShapes.ACCU_P, "Role");
        itemNameMaps.put(ItemType.ROLE, itemRoleNameMap);
        itemGenderMap.put(ItemType.ROLE, GrammarGender.FEMININE);

        Map<GrammarShapes, String>itemSvatNameMap = new HashMap<>();
        itemSvatNameMap.put(GrammarShapes.NOM_S, "Svátek");
        itemSvatNameMap.put(GrammarShapes.NOM_P, "Svátky");
        itemSvatNameMap.put(GrammarShapes.GEN_S, "Svátku");
        itemSvatNameMap.put(GrammarShapes.GEN_P, "Svátků");
        itemSvatNameMap.put(GrammarShapes.ACCU_S, "Svátek");
        itemSvatNameMap.put(GrammarShapes.ACCU_P, "Svátky");
        itemNameMaps.put(ItemType.SVAT, itemSvatNameMap);
        itemGenderMap.put(ItemType.SVAT, GrammarGender.MASCULINE);


        //        Map<GrammarShapes, String>itemFaktNameMap = new HashMap<>();
//        itemFaktNameMap.put(GrammarShapes.NOM_S, "Fakturace");
//        itemFaktNameMap.put(GrammarShapes.NOM_P, "Fakturace");
//        itemFaktNameMap.put(GrammarShapes.GEN_S, "Fakturace");
//        itemFaktNameMap.put(GrammarShapes.GEN_P, "Fakturací");
//        itemFaktNameMap.put(GrammarShapes.ACCU_S, "Fakturaci");
//        itemFaktNameMap.put(GrammarShapes.ACCU_P, "Fakturace");
//        itemNameMaps.put(ItemType.FAKT, itemFaktNameMap);
//        itemGenderMap.put(ItemType.FAKT, GrammarGender.FEMININE);
    }

    @Function
    public static Map<GrammarShapes, String> getItemNameMap(ItemType type) {
        return itemNameMaps.get(type);
    }

    @Function
    public static GrammarGender getItemGender(ItemType type) {
        GrammarGender gender = null == type ? itemGenderMap.get(ItemType.UNKNOWN) : itemGenderMap.get(type);
        return null != gender ? gender : itemGenderMap.get(ItemType.UNKNOWN);
    }

    @Function
    public static String getNomS(ItemType type) {
        Map<GrammarShapes, String> grammarShapes = itemNameMaps.get(type);
        String name = null == grammarShapes ? null : grammarShapes.get(GrammarShapes.NOM_S);
        return null != name ? name : itemNameMaps.get(ItemType.UNKNOWN).get(GrammarShapes.NOM_S);
    }

    @Function
    public static String getNomP(ItemType type) {
        Map<GrammarShapes, String> grammarShapes = itemNameMaps.get(type);
        String name = null == grammarShapes ? null : grammarShapes.get(GrammarShapes.NOM_P);
        return null != name ? name : itemNameMaps.get(ItemType.UNKNOWN).get(GrammarShapes.NOM_P);
    }

    @Function
    public static String getGenS(ItemType type) {
        Map<GrammarShapes, String> grammarShapes = itemNameMaps.get(type);
        String name = null == grammarShapes ? null : grammarShapes.get(GrammarShapes.GEN_S);
        return null != name ? name : itemNameMaps.get(ItemType.UNKNOWN).get(GrammarShapes.GEN_S);
    }

    @Function
    public static String getGenP(ItemType type) {
        Map<GrammarShapes, String> grammarShapes = itemNameMaps.get(type);
        String name = null == grammarShapes ? null : grammarShapes.get(GrammarShapes.GEN_P);
        return null != name ? name : itemNameMaps.get(ItemType.UNKNOWN).get(GrammarShapes.GEN_P);
    }

    @Function
    public static String getAccuS(ItemType type) {
        Map<GrammarShapes, String> grammarShapes = itemNameMaps.get(type);
        String name = null == grammarShapes ? null : grammarShapes.get(GrammarShapes.ACCU_S);
        return null != name ? name : itemNameMaps.get(ItemType.UNKNOWN).get(GrammarShapes.ACCU_S);
    }

    @Function
    public static String getAccuP(ItemType type) {
        Map<GrammarShapes, String> grammarShapes = itemNameMaps.get(type);
        String name = null == grammarShapes ? null : grammarShapes.get(GrammarShapes.ACCU_P);
        return null != name ? name : itemNameMaps.get(ItemType.UNKNOWN).get(GrammarShapes.ACCU_P);
    }
}