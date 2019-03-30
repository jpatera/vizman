package eu.japtor.vizman.backend.utils;

import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Fakt;
import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.ui.components.Ribbon;
import org.apache.commons.lang3.StringUtils;

import java.awt.Color;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class VzmFormatUtils {

    public static final DecimalFormat decimalFormat = new DecimalFormat("#,##0");
    public static final NumberFormat yearFormat = getYearFormat(Locale.getDefault());
    public static final NumberFormat moneyFormat = getMoneyFormat(Locale.getDefault());
    public static final NumberFormat percentFormat = getPercentFormat(Locale.getDefault());
    public static final NumberFormat decHodFormat = getDecHodFormat(Locale.getDefault());
//    public final static NumberFormat moneyFormat = new MoneyFormat();
//    public final static NumberFormat yearFormat = new YearFormat();
    public static final StringToBigDecimalConverter bigDecimalMoneyConverter;
    public static final StringToBigDecimalConverter bigDecimalPercentConverter;
//    public static final StringToBigDecimalConverter integerYearConverter;
    public static final DecHodToStringConverter decHodToStringConverter;

    public final static DateTimeFormatter shortTimeFormatter = DateTimeFormatter.ofPattern("H:mm");
    public final static DateTimeFormatter basicDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public final static DateTimeFormatter basicDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//    public final static DateTimeFormatter titleModifDateFormatter = DateTimeFormatter.ofPattern("EEEE yyyy-MM-dd HH:mm");
    public final static DateTimeFormatter titleModifDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
        bigDecimalPercentConverter = new StringToBigDecimalConverter("Špatný formát čísla") {
            @Override
            protected java.text.NumberFormat getFormat(Locale locale) {
                NumberFormat numberFormat = super.getFormat(locale);
                numberFormat.setGroupingUsed(false);
                numberFormat.setMinimumFractionDigits(1);
                numberFormat.setMaximumFractionDigits(1);
                return numberFormat;
            }
        };
        decHodToStringConverter = new DecHodToStringConverter("Špatný formát čísla");

//        integerYearConverter = new StringToBigDecimalConverter("Špatný formát čísla") {
//            @Override
//            protected java.text.NumberFormat getFormat(Locale locale) {
//                NumberFormat numberFormat = super.getFormat(locale);
//                numberFormat.setGroupingUsed(true);
//                numberFormat.setMinimumFractionDigits(2);
//                numberFormat.setMaximumFractionDigits(2);
//                return numberFormat;
//            }
//        };
    }

    private static Map<String, Color> nameToColorMap;

    static {
        // color names.
        nameToColorMap = new HashMap<>();
        nameToColorMap.put("black", new Color(0x000000));
        nameToColorMap.put("darkgoldenrod", new Color(0xB8860B));
        nameToColorMap.put("sienna", new Color(0xA0522D));
        nameToColorMap.put("dimgray", new Color(0x696969));
        nameToColorMap.put("purple", new Color(0x800080));
        nameToColorMap.put("darkgreen", new Color(0x006400));
        nameToColorMap.put("darkslateblue", new Color(0x483D8B));
        nameToColorMap.put("darkblue", new Color(0x00008B));
        nameToColorMap.put("firebrick", new Color(0xB22222));
    }

    public static String getItemTypeColorName(ItemType itemType) {
        String colorName = "black";
        if (ItemType.KONT == itemType) {
            colorName = "sienna";
        } else if (ItemType.ZAK == itemType) {
            colorName = "purple";
        } else if (ItemType.AKV == itemType) {
            colorName = "darkgreen";
        } else if (ItemType.FAKT == itemType) {
            colorName = "darkblue";
        } else if (ItemType.SUB == itemType) {
            colorName = "crimson";
//            colorName = "firebrick";
        }
        return colorName;
    }

    public static String encodeRGB(Color color){
        if(null == color) {
            throw new IllegalArgumentException("NULL_COLOR_PARAMETER_ERROR");
        }
        return "#" + Integer.toHexString(color.getRGB()).substring(2).toUpperCase();
    }

    public static String getItemTypeColorBrighter(ItemType itemType) {
        return encodeRGB(nameToColorMap.get(getItemTypeColorName(itemType)).brighter());
    }


    public static class IntegerYearConverter implements Converter<String, Integer>, HasLogger {

        private String errorMessage;

        public IntegerYearConverter(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        @Override
        public Result<Integer> convertToModel(String s, ValueContext valueContext) {
            try {
                int rok = Integer.valueOf(s);
                if (rok < 2000 || rok >= 2100) {
                    return Result.error(errorMessage + " (rok musí být mezi 2000-2099)");
                }
                return Result.ok(Integer.valueOf(s));
            } catch (NumberFormatException e) {
                getLogger().error(e.getMessage(), e);
                return Result.error(errorMessage);
            }
        }

        @Override
        public String convertToPresentation(Integer year, ValueContext valueContext) {
            return null == year ? "" : yearFormat.format(year);
        }
    }

    public static class DecHodToStringConverter implements Converter<String, BigDecimal>, HasLogger {

        private String errorMessage;

        public DecHodToStringConverter(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        @Override
        public Result<BigDecimal> convertToModel(String s, ValueContext valueContext) {
            try {
                if (StringUtils.isBlank(s)) {
                    return Result.ok(null);
                }

//                BigDecimal decHod = new BigDecimal(s);

                DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
                df.setParseBigDecimal(true);
                BigDecimal decHod = null;
                decHod = (BigDecimal) df.parse(s);

                if (decHod.compareTo(BigDecimal.valueOf(-1000000)) < 0
                        || decHod.compareTo(BigDecimal.valueOf(1000000)) > 0) {
                    return Result.error(errorMessage + " (číslo musí být v rozmezí -1000000 až +1000000)");
                }
                return Result.ok(decHod);
            } catch (NumberFormatException | ParseException e) {
                getLogger().error(e.getMessage(), e);
                return Result.error(errorMessage);
            }
        }

        @Override
        public String convertToPresentation(BigDecimal decHod, ValueContext valueContext) {
            return null == decHod ? "" : decHodFormat.format(decHod);
        }
    }

    public static class LocalDateTimeToHhMmStringConverter implements Converter<String, LocalDateTime>, HasLogger {

        private static final String DEFAULT_ERR_MSG = "Chybný formát času (je očekáváno 'HH:mm')";
        private String errorMessage;

        public LocalDateTimeToHhMmStringConverter() {
            this(DEFAULT_ERR_MSG);
        }

        public LocalDateTimeToHhMmStringConverter(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        @Override
        public Result<LocalDateTime> convertToModel(String str, ValueContext valueContext) {
            try {
                return Result.ok(LocalDateTime.parse(str, shortTimeFormatter));
            } catch (NumberFormatException e) {
                getLogger().error(e.getMessage(), e);
                return Result.error(errorMessage);
            }
        }

        @Override
        public String convertToPresentation(LocalDateTime dateTime, ValueContext valueContext) {
            return null == dateTime ? "" : shortTimeFormatter.format(dateTime);
        }
    }


    private static NumberFormat getDecHodFormat(Locale locale) {
        if(null == locale) {
            locale = Locale.getDefault();
        }
//        DecimalFormat df = new DecimalFormat();
//            df.setGroupingUsed(false);
//            df.setMaximumFractionDigits(1);
//            df.setMinimumFractionDigits(1);
        NumberFormat format = NumberFormat.getInstance(locale);
        if (format instanceof DecimalFormat) {
            format.setGroupingUsed(false);
            format.setMinimumFractionDigits(1);
            format.setMaximumFractionDigits(1);
        }
        return format;
    }



    private static NumberFormat getPercentFormat(Locale locale) {
        if(null == locale) {
            locale = Locale.getDefault();
        }
        NumberFormat format = NumberFormat.getInstance(locale);
        if (format instanceof DecimalFormat) {
            format.setGroupingUsed(false);
            format.setMinimumFractionDigits(1);
            format.setMaximumFractionDigits(1);
        }
        return format;
    }

    private static NumberFormat getYearFormat(Locale locale) {
        if(null == locale) {
            locale = Locale.getDefault();
        }
        NumberFormat format = NumberFormat.getInstance(locale);
        if (format instanceof DecimalFormat) {
            format.setGroupingUsed(false);
            format.setMinimumFractionDigits(0);
            format.setMaximumFractionDigits(0);
        }
        return format;
    }

//    public static class NumberFormat extends DecimalFormat {
    public static NumberFormat getMoneyFormat(Locale locale) {

//        public static NumberFormat getInstance (Locale locale) {
            if(null == locale) {
                locale = Locale.getDefault();
            }
            NumberFormat format = NumberFormat.getInstance(locale);
            if (format instanceof DecimalFormat) {
                format.setGroupingUsed(true);
                format.setMinimumFractionDigits(2);
                format.setMaximumFractionDigits(2);
            }
            return format;
//        }

////        public MoneyFormat (Locale locale) {
//        public MoneyFormat (Locale locale) {
//            super();
//
//            if (locale == null) {
//                locale = Locale.getDefault();
//            }
//
//            return NumberFormat.getNumberInstance(locale);
//
////        moneyFormat = DecimalFormat.getInstance();
////        if (moneyFormat instanceof DecimalFormat) {
////            ((DecimalFormat)moneyFormat).setParseBigDecimal(true);
////        }
////            NumberFormat numberFormat = NumberFormat.getInstance(locale);
//
//            this.setGroupingUsed(true);
//            this.setMinimumFractionDigits(2);
//            this.setMaximumFractionDigits(2);
//        }
    }

//    public static class YearFormat extends DecimalFormat {
//
//        public YearFormat () {
//            super();
//            this.setGroupingUsed(false);
//            this.setMinimumFractionDigits(0);
//            this.setMaximumFractionDigits(0);
//        }
//    }

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

    public static HtmlComponent getDecHodComponent(BigDecimal number) {
//        TextField field = new TextField();
        Div comp = new Div();
        String color = "black";
        if (null != number) {
            if (number.compareTo(BigDecimal.ZERO) == 0) {
                color = "darkgreen";
            } else if (number.compareTo(BigDecimal.ZERO) < 0) {
                color = "crimson";
            }
            comp.getStyle().set("color", color);
        }
        comp.setText(null == number ? "" : VzmFormatUtils.decHodFormat.format(number));
        return comp;
    }

    public static HtmlComponent getPercentComponent(BigDecimal number) {
        Div comp = new Div();
        String color = "black";
        if (null != number) {
            if (number.compareTo(BigDecimal.ZERO) == 0) {
                color = "darkgreen";
            } else if (number.compareTo(BigDecimal.ZERO) < 0) {
                color = "crimson";
            }
            comp.getStyle().set("color", color);
        }
        comp.setText(null == number ? "" : VzmFormatUtils.percentFormat.format(number));
        return comp;
    }

    public static HtmlComponent getMoneyComponent(BigDecimal amount) {
        Div comp = new Div();
        String color = "black";
        if (null != amount) {
            if (amount.compareTo(BigDecimal.ZERO) == 0) {
                color = "darkgreen";
            } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
                color = "crimson";
            }
            comp.getStyle().set("color", color);
        }
        comp.setText(null == amount ? "" : VzmFormatUtils.moneyFormat.format(amount));
        return comp;
    }

    public static HtmlComponent getDuzpComponent(Fakt fakt) {
        Div comp = new Div();
        comp.getStyle().set("color", fakt.isFaktAfter() ? "crimson" : "green");
        comp.setText(null == fakt.getDateDuzp() ? "" : fakt.getDateDuzp().format(VzmFormatUtils.basicDateFormatter));
        return comp;
    }

    public static HtmlComponent getColoredTextComponent(Fakt fakt) {
        Div comp = new Div();
        comp.getStyle().set("color", VzmFormatUtils.getItemTypeColorName(fakt.getTyp()));
        comp.setText(fakt.getText());
        return comp;
    }

    public static HtmlComponent getColoredTextComponent(Zak zak) {
        Div comp = new Div();
        comp.getStyle().set("color", VzmFormatUtils.getItemTypeColorName(zak.getTyp()));
        comp.setText(zak.getText());
        return comp;
    }

    public static HtmlComponent getItemTypeColoredTextComponent(Fakt fakt) {
        Div comp = new Div();
        comp.getStyle().set("color", VzmFormatUtils.getItemTypeColorName(fakt.getTyp()));
        comp.setText(fakt.getTyp().name());
        return comp;
    }

    public static HtmlComponent getItemTypeColoredTextComponent(Zak zak) {
        Div comp = new Div();
        comp.getStyle().set("color", VzmFormatUtils.getItemTypeColorName(zak.getTyp()));
        comp.setText(zak.getTyp().name());
        return comp;
    }

    public static HtmlContainer buildFlagBasis(boolean forHeader) {
        Div flag = new Div();
        flag.getStyle()
                .set("height", "20px")
//                .set("min-height", "15px")
                .set("width", "20px")
//                .set("min-width", "15px")
//                .set("font-size","var(--lumo-font-size-l)")
//                .set("font-weight","600")
                .set("text-align", "center")
                .set("padding-right", "0.2em")
                .set("padding-left", "0.2em")
                .set("padding-top", forHeader ? "0.0em" : "0.1em")
                .set("padding-bottom", forHeader ? "0.3em" : "0.1em")
                .set("border", forHeader ? "1px solid black" : "0px solid transparent")
                .set("border-radius", "3px")
                ;
        return flag;
    }

    public static HtmlContainer buildGreenFlag(long countBefore, boolean shiftUp) {
        HtmlContainer flag = buildFlagBasis(shiftUp);
        flag.getElement().getStyle()
                .set("background-color", "lightgreen")
        ;
        flag.setText(Long.toString(countBefore));
        return flag;
    }

    public static HtmlContainer buildRedFlag(long countAfter, boolean shiftUp) {
        HtmlContainer flag = buildFlagBasis(shiftUp);
        flag.getElement().getStyle()
//                .set("border", "1px solid black")
                .set("background-color", "pink")
        ;
        flag.setText(Long.toString(countAfter));
        return flag;
    }

    public static HtmlContainer buildBlankFlag(boolean shiftUp) {
        return buildFlagBasis(shiftUp);
    }

    public static FlexLayout buildAvizoComponent(long greenValue, long redValue, boolean shiftUp) {
        FlexLayout zakFaktFlags = new FlexLayout();
        zakFaktFlags.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        if (greenValue + redValue == 0) {
            zakFaktFlags.add(new Ribbon());
        } else {
            zakFaktFlags.add(
                    greenValue > 0 ? VzmFormatUtils.buildGreenFlag(greenValue, shiftUp) : VzmFormatUtils.buildBlankFlag(shiftUp)
//                        , new Ribbon("0.5em")
                    , new Ribbon("0.5em")
                    //                        , new Div(VzmFormatUtils.styleRedFlag(new Span(zak.getAfterTerms().toString())))
                    , redValue > 0 ? VzmFormatUtils.buildRedFlag(redValue, shiftUp) : VzmFormatUtils.buildBlankFlag(shiftUp)
            );
        }
        return zakFaktFlags;
    }


//    public static Span styleGreyFlag(Span flag) {
//        flag.getElement().getStyle()
//                .set("height", "15px")
////                .set("min-height", "15px")
//                .set("width", "15px")
////                .set("min-width", "15px")
//                .set("padding-right","0.2em")
//                .set("padding-left","0.2em")
//                .set("padding-top","0.2em")
//                .set("padding-bottom","0.2em")
////                .set("border", "1px solid black")
//                .set("color", "black")
//                .set("background-color", "silver")
//                .set("border-radius", "3px")
//        ;
//        return flag;
//    }

//    public static Span styleRedFlag(Span flag) {
////         Style stl = text.getElement().getStyle();
////            position: absolute;
////        flag.getElement().getStyle().set("display", "inline-flex");
////            align-items: center;
////            justify-content: center;
//        flag.getElement().getStyle()
//                .set("height", "15px")
////                .set("min-height", "15px")
//                .set("width", "15px")
////                .set("min-width", "15px")
////                .set("font-size","var(--lumo-font-size-l)")
////                .set("font-weight","600")
//                .set("padding-right","0.2em")
//                .set("padding-left","0.2em")
//                .set("padding-top","0.2em")
//                .set("padding-bottom","0.2em")
//                .set("border", "1px solid black")
//        ;
//        ;
////            min-width: 8px;
////            padding: 0 6px;
////            background: var(--lumo-base-color);
//        flag.getElement().getStyle().set("color", "black");
//        flag.getElement().getStyle().set("background-color", "pink");
////            top: -10px;
////            left: -10px;
//        flag.getElement().getStyle().set("border-radius", "3px");
////            margin: 0;
////            font-size: 12px;
////            font-weight: 500;
////            box-shadow: 0 0 0 1px var(--lumo-contrast-20pct);
//        return flag;
//    }

}
