package com.mdu.jobinvoicing.data.local.entities;

import androidx.room.TypeConverter;

public class WorkPlanStatusConverter {
    @TypeConverter
    public static WorkPlanStatus fromString(String value) {
        return value == null ? null : WorkPlanStatus.valueOf(value);
    }

    @TypeConverter
    public static String toString(WorkPlanStatus value) {
        return value == null ? null : value.name();
    }
}
