package com.yz.pferestapi.entity.report;

import com.yz.pferestapi.entity.Operation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationRow {
    private Long number;
    private Integer year;
    private Instant dateTime;

    public static <T extends Operation, U extends OperationRow> U convert(T operation, U operationRow) {
        operationRow.setNumber(operation.getNumber());
        operationRow.setYear(operation.getYear());
        operationRow.setDateTime(operation.getDateTime());
        return operationRow;
    }
}
