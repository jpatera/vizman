package eu.japtor.vizman.backend.utils;

import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.function.ValueProvider;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.CalService;
import eu.japtor.vizman.backend.service.VzmServiceException;
import eu.japtor.vizman.ui.components.Ribbon;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.Color;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Component
public class VzmFormatUtils {

    public static final NumberFormat YEAR_FORMAT = getDecFormat(Locale.getDefault(), 4,0);
    public static final NumberFormat MONEY_FORMAT = getDecFormat(Locale.getDefault(), 10,2, true);
    public static final NumberFormat MONEY_NO_FRACT_FORMAT = getDecFormat(Locale.getDefault(), 10,0, true);
    public static final NumberFormat PROC_FORMAT = getDecFormat(Locale.getDefault(), 3, 1);
    public static final NumberFormat PROC_INT_FORMAT = getDecFormat(Locale.getDefault(), 3,0);
    public static final NumberFormat DEC_HOD_FORMAT = getDecFormat(Locale.getDefault(), 4,1);
    public static final NumberFormat DEC2_FORMAT = getDecFormat(Locale.getDefault(), 10, 2);

    public final static DateTimeFormatter shortTimeFormatter = DateTimeFormatter.ofPattern("H:mm");
    public final static DateTimeFormatter basicDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public final static DateTimeFormatter basicDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//    public final static DateTimeFormatter titleModifDateFormatter = DateTimeFormatter.ofPattern("EEEE yyyy-MM-dd HH:mm");
    public final static DateTimeFormatter titleUpdateDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public final static DateTimeFormatter monthLocalizedFormatter = DateTimeFormatter.ofPattern("LLLL");
//    public final static DateTimeFormatter monthLocalizedFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle."yyyy-MM-dd HH:mm");
    public final static DateTimeFormatter dayOfWeekLocalizedFormatter = DateTimeFormatter.ofPattern("EEEE");

    private static Map<String, Color> nameToColorMap;

    static {
        // color names.
        nameToColorMap = new HashMap<>();
        nameToColorMap.put("black", new Color(0x000000));
        nameToColorMap.put("darkgrey", new Color(0xA9A9A9));
        nameToColorMap.put("darkgoldenrod", new Color(0xB8860B));
        nameToColorMap.put("sienna", new Color(0xA0522D));
        nameToColorMap.put("dimgray", new Color(0x696969));
        nameToColorMap.put("purple", new Color(0x800080));
        nameToColorMap.put("darkgreen", new Color(0x006400));
        nameToColorMap.put("darkslateblue", new Color(0x483D8B));
        nameToColorMap.put("darkblue", new Color(0x00008B));
        nameToColorMap.put("firebrick", new Color(0xB22222));
        nameToColorMap.put("crimson", new Color(0xDC143C));

//        this.getStyle().set("margin-top", "2em");
//        this.getStyle().set("margin-bottom", "2em");
//        this.getStyle().set("background-color", "#fffcf5");
//        this.getStyle().set("background-color", "#e1dcd6");
//        this.getStyle().set("background-color", "#fcfffe");
//        this.getStyle().set("background-color", "LightYellow");
//        this.getStyle().set("background-color", "#fefefd");
    }

    static private List<LocalDate> lastTwoYearsHolis;

    @Autowired
    CalService calService;

    @PostConstruct
    void init() {
        lastTwoYearsHolis = new ArrayList<>();
        feedLastTwoYearsHolis();
    }

    private void feedLastTwoYearsHolis() {
        int currYear = LocalDate.now().getYear();
        lastTwoYearsHolis.addAll(calService.fetchCalyHolDateListByYear(currYear-1));
        lastTwoYearsHolis.addAll(calService.fetchCalyHolDateListByYear(currYear));
    }

    public static final ValueProvider<VzmFileUtils.VzmFile, String> vzmFileIconStyleProvider = file -> {
        String iconStyle;
        if (file.isVzmControledDir()) {
            iconStyle = "padding-left: 1em; width: 0.8em; height: 0.8em; color: "
                    + (file.exists() ? "MediumSeaGreen;" : "Red;");
        } else {
            iconStyle = "padding-left: 1em; width: 0.8em; height: 0.8em; color: "
                    + (file.isFile() ? "Peru;" : "DarkMagenta;");
        }
        return iconStyle;
    };

    public static final ValueProvider<VzmFileUtils.VzmFile, String> vzmFileIconNameProvider = file -> {
        String iconName;
        Icon icon;
        if (file.isVzmControledDir()) {
//            icon = VaadinIcon.FOLDER_O.create();
//            iconName = VaadinIcon.FOLDER_O.create().toString();
            if (file.exists()) {
                    iconName = "vaadin:folder";
            } else {
                iconName = "vaadin:folder-remove";
            }
        } else {
//            iconName = VaadinIcon.FILE_O.create().toString();
            if (!file.exists()) {
                iconName = "vaadin:question-circle-o";
            } else {
                if (file.isDirectory()) {
                    iconName = "vaadin:folder-o";
                }  else {
                    iconName = "vaadin:file";
                }
            }
        }
        return iconName;
    };

    public static final StringToBigDecimalConverter bigDecimalMoneyConverter =
        new StringToBigDecimalConverter("Špatný formát čísla") {
            @Override
            protected java.text.NumberFormat getFormat(Locale locale) {
                NumberFormat numberFormat = super.getFormat(locale);
                numberFormat.setGroupingUsed(true);
                numberFormat.setMinimumFractionDigits(2);
                numberFormat.setMaximumFractionDigits(2);
                return numberFormat;
            }
            @Override
            public Result<BigDecimal> convertToModel(String value, ValueContext context) {
                if (null == value) {
                    return Result.ok(null);
//                    return BigDecimal.ZERO;
                }
                value = value.replaceAll("\\s+","");
                return super.convertToNumber(value, context)
                        .map(number -> (BigDecimal) number);
            }
        };

    public static final StringToBigDecimalConverter bigDecimalPercentConverter =
        new StringToBigDecimalConverter("Špatný formát čísla") {
            @Override
            protected java.text.NumberFormat getFormat(Locale locale) {
                NumberFormat numberFormat = super.getFormat(locale);
                numberFormat.setGroupingUsed(false);
                numberFormat.setMinimumFractionDigits(1);
                numberFormat.setMaximumFractionDigits(1);
                return numberFormat;
            }
            @Override
            public Result<BigDecimal> convertToModel(String value, ValueContext context) {
                if (null == value) {
                    return Result.ok(null);
                }
                value = value.replaceAll("\\s+","");
                return super.convertToNumber(value, context)
                        .map(number -> (BigDecimal) number);
            }
        };

    public static final StringToBigDecimalConverter bigDecimalPercent2Converter =
        new StringToBigDecimalConverter("Špatný formát čísla") {
            @Override
            protected java.text.NumberFormat getFormat(Locale locale) {
                NumberFormat numberFormat = super.getFormat(locale);
                numberFormat.setGroupingUsed(false);
                numberFormat.setMinimumFractionDigits(2);
                numberFormat.setMaximumFractionDigits(2);
                return numberFormat;
            }
            @Override
            public Result<BigDecimal> convertToModel(String value, ValueContext context) {
                if (null == value) {
                    return Result.ok(null);
                }
                value = value.replaceAll("\\s+","");
                return super.convertToNumber(value, context)
                        .map(number -> (BigDecimal) number);
            }
        };

    public static final StringToBigDecimalConverter bigDecimalKurzConverter =
        new StringToBigDecimalConverter("Špatný formát čísla") {
            @Override
            protected java.text.NumberFormat getFormat(Locale locale) {
                NumberFormat numberFormat = super.getFormat(locale);
                numberFormat.setGroupingUsed(false);
                numberFormat.setMinimumFractionDigits(1);
                numberFormat.setMaximumFractionDigits(2);
                return numberFormat;
            }
            @Override
            public Result<BigDecimal> convertToModel(String value, ValueContext context) {
                if (null == value) {
                    return Result.ok(null);
                }
                value = value.replaceAll("\\s+","");
                return super.convertToNumber(value, context)
                        .map(number -> (BigDecimal) number);
            }
        };

    public static final ValidatedDecHodToStringConverter VALIDATED_DEC_HOD_TO_STRING_CONVERTER =
        new ValidatedDecHodToStringConverter("Špatný formát čísla");

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
//    }

    public static final ValidatedProcIntToStringConverter VALIDATED_PROC_INT_TO_STRING_CONVERTER =
            new ValidatedProcIntToStringConverter("Špatný formát čísla");

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


    public static class ValidatedIntegerYearConverter implements Converter<String, Integer>, HasLogger {

        private String errorMessage = "Neplatný formát roku";

//        public ValidatedIntegerYearConverter() {
//        }

        @Override
        public Result<Integer> convertToModel(String s, ValueContext valueContext) {
            try {
                int rok = Integer.valueOf(s);
                if (rok < 2000 || rok >= 2100) {
                    return Result.error(errorMessage + " (rok musí být mezi 2000-2099)");
                }
                return Result.ok(Integer.valueOf(s));
            } catch (NumberFormatException e) {
                return Result.error(errorMessage);
            } catch (Exception e) {
                getLogger().error(e.getMessage(), e);
                return Result.error(errorMessage);
            }
        }

        @Override
        public String convertToPresentation(Integer year, ValueContext valueContext) {
            return null == year ? "" : YEAR_FORMAT.format(year);
        }
    }

    public static class ValidatedIntegerYearMonthConverter implements Converter<String, YearMonth>, HasLogger {

        private String errorMessage = "Neplatný formát, očekáván RRRR-MM";

        @Override
        public Result<YearMonth> convertToModel(String str, ValueContext valueContext) {
            if (StringUtils.isEmpty(str)) {
                return Result.ok(null);
            }
            try {
                if (str.length() != 7) {
                    return Result.error(errorMessage);
                }
                int year = Integer.valueOf(str.substring(0, 4));
                int month = Integer.valueOf(str.substring(5, 7));
                if (year < 2000 || year >= 2100) {
                    return Result.error(errorMessage + " (rok musí být mezi 2000-2099)");
                }
                if (month < 1 || month > 12) {
                    return Result.error(errorMessage + " (měsíc musí být mezi 01-12)");
                }
                return Result.ok(YearMonth.of(year, month));
            } catch (NumberFormatException e) {
                return Result.error(errorMessage);
            } catch (Exception e) {
                getLogger().error(e.getMessage(), e);
                return Result.error(errorMessage);
            }
        }

        @Override
        public String convertToPresentation(YearMonth ym, ValueContext valueContext) {
            return null == ym ? "" : ym.toString();
        }
    }

    public static class ValidatedDecHodToStringConverter implements Converter<String, BigDecimal>, HasLogger {

        private String errorMessage;

        public ValidatedDecHodToStringConverter(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        @Override
        public Result<BigDecimal> convertToModel(String str, ValueContext valueContext) {
            String s = StringUtils.trimToEmpty(str);
            try {
                if (StringUtils.isBlank(s)) {
                    return Result.ok(null);
                }
                BigDecimal decHod = null;
                if (!StringUtils.isBlank(s)) {
                    String newBdStr = s.replaceAll("\\,|\\.", ".");
                    decHod = new BigDecimal(newBdStr);
                }

                if (decHod.compareTo(BigDecimal.valueOf(-1000000)) < 0
                        || decHod.compareTo(BigDecimal.valueOf(1000000)) > 0) {
                    return Result.error(errorMessage + " (číslo musí být v rozmezí -1000000 až +1000000)");
                }
                return Result.ok(decHod);
            } catch (NumberFormatException e) {
                return Result.error(errorMessage);
            } catch (Exception e) {
                getLogger().error(e.getMessage(), e);
                return Result.error(errorMessage);
            }
        }

        @Override
        public String convertToPresentation(BigDecimal decHod, ValueContext valueContext) {
            return null == decHod ? "" : DEC_HOD_FORMAT.format(decHod);
        }
    }

    public static class ValidatedProcIntToStringConverter implements Converter<String, BigDecimal>, HasLogger {

        private String errorMessage;

        public ValidatedProcIntToStringConverter(String errorMessage) {
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
                return Result.error(errorMessage);
            } catch (Exception e) {
                getLogger().error(e.getMessage(), e);
                return Result.error(errorMessage);
            }
        }

        @Override
        public String convertToPresentation(BigDecimal proc, ValueContext valueContext) {
            return null == proc ? "" : PROC_INT_FORMAT.format(proc);
        }
    }

    public static class BigDecimalMoneyPercentConverter implements Converter<String, BigDecimal>, HasLogger {

        private String errorMessage;
        private BigDecimal percentBasis;

        public BigDecimalMoneyPercentConverter(String errorMessage, BigDecimal percentBasis) {
            this.errorMessage = errorMessage;
        }

        @Override
        public Result<BigDecimal> convertToModel(String s, ValueContext valueContext) {
            try {
                if (StringUtils.isBlank(s)) {
                    return Result.ok(null);
                }

//                NumberFormat numberFormat = super.getFormat(locale);
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setGroupingUsed(true);
                numberFormat.setMinimumFractionDigits(2);
                numberFormat.setMaximumFractionDigits(2);

                BigDecimal money = null;
                if (s.endsWith("%")) {
                    String percStr = s.replace("%", "");
                    BigDecimal perc = (BigDecimal) numberFormat.parse(percStr);
                    money = percentBasis.multiply(percentBasis);
                } else {
                    money = (BigDecimal) numberFormat.parse(s);
                }

//                if (money.compareTo(BigDecimal.valueOf(-1000000)) < 0
//                        || money.compareTo(BigDecimal.valueOf(1000000)) > 0) {
//                    return Result.error(errorMessage + " (číslo musí být v rozmezí -1000000 až +1000000)");
//                }
                return Result.ok(money);
            } catch (NumberFormatException | ParseException e) {
                return Result.error(errorMessage);
            } catch (Exception e) {
                getLogger().error(e.getMessage(), e);
                return Result.error(errorMessage);
            }
        }

        @Override
        public String convertToPresentation(BigDecimal decHod, ValueContext valueContext) {
            return null == decHod ? "" : DEC_HOD_FORMAT.format(decHod);
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
                return Result.error(errorMessage);
            } catch (Exception e) {
                getLogger().error(e.getMessage(), e);
                return Result.error(errorMessage);
            }
        }

        @Override
        public String convertToPresentation(LocalDateTime dateTime, ValueContext valueContext) {
            return null == dateTime ? "" : shortTimeFormatter.format(dateTime);
        }
    }


    private static NumberFormat getDecFormat(Locale locale, int intMaxDigits, int fractDigits) {
        return getDecFormat(locale, intMaxDigits, fractDigits, false) ;
    }

    private static NumberFormat getDecFormat(Locale locale, int intMaxDigits, int fractDigits, boolean  grouping) {
        if(null == locale) {
            locale = Locale.getDefault();
        }
        NumberFormat format = NumberFormat.getInstance(locale);
        if (format instanceof DecimalFormat) {
            format.setGroupingUsed(grouping);
            format.setMaximumIntegerDigits(intMaxDigits);
            format.setMinimumFractionDigits(fractDigits);
            format.setMaximumFractionDigits(fractDigits);
        }
        return format;
    }

    public static HtmlComponent getDecHodComponent(BigDecimal number) {
        Div comp = new Div();
        String color = "black";
        if (null != number) {
            if (number.compareTo(BigDecimal.ZERO) == 0) {
                color = "darkgreen";
            } else if (number.compareTo(BigDecimal.ZERO) < 0) {
                color = "crimson";
            }
            comp.getStyle()
                    .set("color", color)
            ;
        }
        comp.setText(null == number ? "" : VzmFormatUtils.DEC_HOD_FORMAT.format(number));
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
        comp.setText(null == number ? "" : VzmFormatUtils.PROC_FORMAT.format(number));
        return comp;
    }

    public static HtmlComponent getRokComponent(Integer rok) {
        Div comp = new Div();
        comp.setText(null == rok ? "" : rok.toString());
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
        comp.setText(null == amount ? "" : VzmFormatUtils.MONEY_FORMAT.format(amount));
        return comp;
    }

    public static HtmlComponent getDuzpComponent(Fakt fakt) {
        Div comp = new Div();
        comp.getStyle().set("color", fakt.isFaktAfter() ? "crimson" : "green");
        comp.setText(null == fakt.getDateDuzp() ? "" : fakt.getDateDuzp().format(VzmFormatUtils.basicDateFormatter));
        return comp;
    }

    public static HtmlComponent getDatetimeUpdateComponent(final LocalDateTime datetimeUpdate, final String username) {
        Div comp = new Div();
        String textColor =  getColorByUpdatedRule(datetimeUpdate, username);
        if (null != textColor) {
            comp.getStyle().set("color", textColor);
        }
        comp.setText(null == datetimeUpdate ? "" : datetimeUpdate.format(VzmFormatUtils.basicDateFormatter));
        return comp;
    }

    public static HtmlComponent getEmptyComponent() {
        return new Div();
    }


    public static String getColorByUpdatedRule(final LocalDateTime datetimeUpdate, final String username) {
        if (null == datetimeUpdate || null == username || !"vancik".equalsIgnoreCase(username)) {
            return null;
        }
//        if (4 > ChronoUnit.DAYS.between(datetimeUpdate, LocalDateTime.now())) {
        long workDaysBetween = countBusinessDaysBetweenApprox(datetimeUpdate.toLocalDate(), LocalDate.now(), 20);
        if (workDaysBetween > 4) {
            return "red";
        } else if (workDaysBetween >= 0) {
            return "magenta";
        } else {
            return null;
        }
    }

    public static long countBusinessDaysBetweenApprox(final LocalDate startDate, final LocalDate endDate, int exactLimitDaysBetween) {
        int daysBetween = Period.between(startDate, endDate).getDays();
        if (daysBetween >= exactLimitDaysBetween) {
            return  daysBetween;
        } else {
//            return countBusinessDaysBetweenExact(endDate, startDate, Optional.of(lastTwoYearsHolis));
            return countBusinessDaysBetweenExact(startDate, endDate, Optional.of(lastTwoYearsHolis));
        }
    }

    public static long countBusinessDaysBetweenExact(LocalDate startDate, LocalDate endDate,
                                                     Optional<List<LocalDate>> holidays)
    {
        if (startDate == null || endDate == null || holidays == null) {
            throw new IllegalArgumentException("Invalid method argument(s) to countBusinessDaysBetween(" + startDate
                    + "," + endDate + "," + holidays + ")");
        }

        Predicate<LocalDate> isHoliday = date -> holidays.isPresent() ? holidays.get().contains(date) : false;

        Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY
                || date.getDayOfWeek() == DayOfWeek.SUNDAY;

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        long businessDays = Stream.iterate(startDate, date -> date.plusDays(1)).limit(daysBetween)
                .filter(isHoliday.or(isWeekend).negate()).count();
        return businessDays;
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

    public static HtmlComponent getItemTypeColoredTextComponent(PersonWage personWage) {
        Div comp = new Div();
        comp.getStyle().set("color", VzmFormatUtils.getItemTypeColorName(personWage.getTyp()));
        comp.setText(personWage.getTyp().name());
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

    // TODO> move to UI
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

    public static BigDecimal stringLocalToBigDecimal(String numStr) {
        try {
            return new BigDecimal(DEC2_FORMAT.parse(numStr).toString());
        } catch (ParseException e) {
            e.printStackTrace();
            throw new VzmServiceException("Chyba v převodu formátu čísla");
        }
    }
}
