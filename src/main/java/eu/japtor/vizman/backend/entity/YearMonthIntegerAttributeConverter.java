package eu.japtor.vizman.backend.entity;

import javax.persistence.AttributeConverter;
import java.time.YearMonth;

public class YearMonthIntegerAttributeConverter implements AttributeConverter<YearMonth, Integer> {

    @Override
    public Integer convertToDatabaseColumn(
            YearMonth attribute) {
        return (attribute.getYear() * 100) +
                attribute.getMonth().getValue();
    }

    @Override
    public YearMonth convertToEntityAttribute(
            Integer dbData) {
        int year = dbData / 100;
        int month = dbData % 100;
        return YearMonth.of(year, month);
    }
}
