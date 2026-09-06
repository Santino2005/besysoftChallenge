package com.besysoft.model.person;

import com.besysoft.common.errorHandler.person.InvalidSellerException;

import java.math.BigDecimal;

public class Seller extends Person {

    private BigDecimal salary;

    public Seller(String code, String name, BigDecimal salary) {
        super(code, name);
        updateSalary(salary);
    }

    public BigDecimal salary() {
        return salary;
    }

    public void updateSalary(BigDecimal salary) {
        if (salary == null || salary.signum() < 0) {
            throw new InvalidSellerException(
                    "salary",
                    "El sueldo no puede ser nulo ni negativo."
            );
        }

        this.salary = salary;
    }
}
