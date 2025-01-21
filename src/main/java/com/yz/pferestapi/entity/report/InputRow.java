package com.yz.pferestapi.entity.report;

import com.yz.pferestapi.entity.Company;
import com.yz.pferestapi.entity.Individual;
import com.yz.pferestapi.entity.Input;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class InputRow extends OperationRow {
    private String status;
    private String ownerName;
    private String ownerIdentity;

    public InputRow(Long number, Integer year, Instant dateTime, String ownerName, String ownerIdentity) {
        super(number, year, dateTime);
        this.ownerName = ownerName;
        this.ownerIdentity = ownerIdentity;
    }

    public static InputRow convert(Input input) {
        InputRow convertedInputRow = OperationRow.convert(input, new InputRow());
        convertedInputRow.setStatus(input.getStatus().name());
        if (input.getOwner() instanceof Individual individual) {
            convertedInputRow.setOwnerName(individual.getFirstName()+ " " + individual.getLastName());
            convertedInputRow.setOwnerIdentity(individual.getNationalId());
        }
        if (input.getOwner() instanceof Company company) {
            convertedInputRow.setOwnerName(company.getName());
            convertedInputRow.setOwnerIdentity(company.getTaxId());
        }
        return convertedInputRow;
    }
}
