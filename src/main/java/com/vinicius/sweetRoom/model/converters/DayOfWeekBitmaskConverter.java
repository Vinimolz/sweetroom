package com.vinicius.sweetRoom.model.converters;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class DayOfWeekBitmaskConverter implements AttributeConverter<List<DayOfWeek>, Integer> {

    @Override
    public Integer convertToDatabaseColumn(List<DayOfWeek> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return 0;
        }
        int mask = 0;
        for (DayOfWeek day : attribute) {
            mask |= (1 << (day.getValue() - 1));
        }
        return mask;
    }

    @Override
    public List<DayOfWeek> convertToEntityAttribute(Integer dbData) {
        if (dbData == null || dbData == 0) {
            return new ArrayList<>();
        }
        List<DayOfWeek> days = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            int bit = 1 << (day.getValue() - 1);
            if ((dbData & bit) != 0) {
                days.add(day);
            }
        }
        return days;
    }
}
