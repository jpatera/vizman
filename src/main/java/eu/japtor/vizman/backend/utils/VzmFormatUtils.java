package eu.japtor.vizman.backend.utils;

import com.vaadin.flow.data.converter.StringToBigDecimalConverter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class VzmFormatUtils {

//    final static NumberFormat moneyFormat = new MoneyFormat(Locale.getDefault());
    public final static NumberFormat moneyFormat = new MoneyFormat();
    public static final StringToBigDecimalConverter bigDecimalMoneyConverter;
    public final static DateTimeFormatter basicDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public final static DateTimeFormatter basicDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public final static DateTimeFormatter titleModifDateFormatter = DateTimeFormatter.ofPattern("EEEE yyyy-MM-dd HH:mm");

    static {
        bigDecimalMoneyConverter = new StringToBigDecimalConverter("Špatný formát čísla") {
            @Override
            protected java.text.NumberFormat getFormat(Locale locale) {
                NumberFormat numberFormat = super.getFormat(locale);
                numberFormat.setGroupingUsed(true);
                numberFormat.setMinimumFractionDigits(2);
                numberFormat.setMaximumFractionDigits(2);
                return numberFormat;
            }
        };
    }


    public static class MoneyFormat extends DecimalFormat {

//        public MoneyFormat (Locale locale) {
        public MoneyFormat () {
            super();
//        moneyFormat = DecimalFormat.getInstance();
//        if (moneyFormat instanceof DecimalFormat) {
//            ((DecimalFormat)moneyFormat).setParseBigDecimal(true);
//        }

//            NumberFormat numberFormat = NumberFormat.getInstance(locale);

            this.setGroupingUsed(true);
            this.setMinimumFractionDigits(2);
            this.setMaximumFractionDigits(2);
        }
    }

//    public static class MoneyCellRenderer
//            extends ComponentRenderer<COMPONENT extends Component, SOURCE> extends Renderer<SOURCE>(
//
//            public MoneyCellRenderer(SerializableSupplier<COMPONENT> componentSupplier, SerializableBiConsumer<COMPONENT, SOURCE> itemConsumer> componentFunction) {
//                super();
//            }
//            zak -> {
//                Div comp = new Div();
////            if (ItemType.KONT == kontZak.getTyp()) {
//////            comp.getStyle().set("color", "darkmagenta");
//////            return new Emphasis(kontZak.getHonorar().toString());
////                comp.getElement().appendChild(ElementFactory.createEmphasis(kontZak.getHonorar().toString()));
////                comp.getStyle()
//////                    .set("color", "red")
//////                    .set("text-indent", "1em");
//////                        .set("padding-right", "1em")
////                ;
////            } else {
//                if ((null != zak) && (zak.getHonorar().compareTo(BigDecimal.ZERO) < 0)) {
//                    comp.getStyle()
//                            .set("color", "red")
//        //                            .set("text-indent", "1em")
//                    ;
//            }
//    //                comp.getElement().appendChild(ElementFactory.createSpan(numFormat.format(kontZak.getHonorar())));
//            comp.setText(VzmFormatUtils.moneyFormat.format(zak.getHonorar()));
//    //            }
//            return comp;
//    });


}
