package com.yz.pferestapi.entity.report;

import com.yz.pferestapi.entity.Output;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class OutputRow extends OperationRow {
    private Double totalPaymentAmount;
    private Integer receiptNumber;

    public OutputRow(Long number, Integer year, Instant dateTime, Double totalPaymentAmount, Integer receiptNumber) {
        super(number, year, dateTime);
        this.totalPaymentAmount = totalPaymentAmount;
        this.receiptNumber = receiptNumber;
    }

    public static OutputRow convert(Output output) {
        OutputRow convertedOutputRow = OperationRow.convert(output, new OutputRow());
        convertedOutputRow.setTotalPaymentAmount(output.getTotalPaymentAmount());
        convertedOutputRow.setReceiptNumber(output.getReceiptNumber());
        return convertedOutputRow;
    }
}
