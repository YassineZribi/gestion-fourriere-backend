package com.yz.pferestapi.bootstrap;

import com.yz.pferestapi.entity.Employee;
import com.yz.pferestapi.entity.Role;
import com.yz.pferestapi.entity.RoleEnum;
import com.yz.pferestapi.repository.EmployeeRepository;
import com.yz.pferestapi.repository.RoleRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Seeder2ForAdminCreation implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;

    private final PasswordEncoder passwordEncoder;


    public Seeder2ForAdminCreation(
            RoleRepository roleRepository,
            EmployeeRepository  employeeRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createAdmin();
    }

    private void createAdmin() {
        String email = "admin.admin@gmail.com";

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.ADMIN);
        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(email);

        if (optionalRole.isEmpty() || optionalEmployee.isPresent()) {
            return;
        }

        Employee employee = new Employee();
        employee.setFirstName("Admin");
        employee.setLastName("Admin");
        employee.setEmail(email);
        employee.setPhoneNumber("+21622585016");
        employee.setPassword(passwordEncoder.encode("1234"));
        employee.setRole(optionalRole.get());
        employee.setPosition("Admin");

        employeeRepository.save(employee);
    }
}
