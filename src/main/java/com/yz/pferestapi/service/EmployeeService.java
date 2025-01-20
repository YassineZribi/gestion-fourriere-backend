package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.EmployeeCriteriaRequest;
import com.yz.pferestapi.dto.EmployeeDto;
import com.yz.pferestapi.dto.UpsertEmployeeDto;
import com.yz.pferestapi.entity.*;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.mapper.EmployeeMapper;
import com.yz.pferestapi.repository.*;
import com.yz.pferestapi.specification.EmployeeSpecifications;
import com.yz.pferestapi.util.CriteriaRequestUtil;
import com.yz.pferestapi.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;
    private final RoleRepository roleRepository;
    private final WarehouseRepository warehouseRepository;
    private final FileService fileService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();

    }

    public EmployeeDto getEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Employee (User) not found"));
        return EmployeeMapper.toDto(employee);
    }

    public Page<EmployeeDto> findEmployeesByCriteria(EmployeeCriteriaRequest employeeCriteria) {
        Specification<Employee> spec = Specification.where(null);

        spec = UserService.filter(employeeCriteria, spec);

        // Get employees having a specific manager
        if (employeeCriteria.getManagerId() != null) {
            spec = spec.and(EmployeeSpecifications.hasManager(employeeCriteria.getManagerId()));
        }

        if (employeeCriteria.getPosition() != null) {
            spec = spec.and(EmployeeSpecifications.positionContains(employeeCriteria.getPosition()));
        }

        Sort sort = CriteriaRequestUtil.buildSortCriteria(employeeCriteria);

        Pageable pageable = CriteriaRequestUtil.createPageable(employeeCriteria, sort);

        Page<Employee> employeePage = employeeRepository.findAll(spec, pageable);
        return employeePage.map(EmployeeMapper::toDto);
    }

    public List<EmployeeDto> findEmployeesByFullName(String search) {
        Specification<Employee> spec = UserService.findAllByFullName(search);

        List<Employee> employees = employeeRepository.findAll(spec);
        return employees.stream().map(EmployeeMapper::toDto).toList();
    }

    public EmployeeDto createEmployee(UpsertEmployeeDto upsertEmployeeDto) {
        Institution institution = institutionRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Institution not found"));

        Employee manager = null;
        if (upsertEmployeeDto.getManagerId() != null) {
            manager = employeeRepository.findById(upsertEmployeeDto.getManagerId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Employee (Manager) not found"));
        }

        if (!upsertEmployeeDto.getRoleName().equalsIgnoreCase(RoleEnum.MANAGER.name()) && !upsertEmployeeDto.getRoleName().equalsIgnoreCase(RoleEnum.OPERATOR.name())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Invalid role name");
        }
        RoleEnum roleEnum = RoleEnum.valueOf(upsertEmployeeDto.getRoleName().toUpperCase());

        if (userRepository.existsByEmailIgnoreCase(upsertEmployeeDto.getEmail())) {
            throw new AppException(HttpStatus.CONFLICT, "Email already exists!");
        }

        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Role not found"));

        String generatedPassword = PasswordGenerator.generate(10);

        System.out.println("generatedPassword = " + generatedPassword);

        Employee employee = EmployeeMapper.toEntity(upsertEmployeeDto, role, manager);
        employee.setPassword(passwordEncoder.encode(generatedPassword));
        employee.setInstitution(institution);

        // TODO: send email (login: email, password: generatedPassword)
        // Envoi de l'email avec les informations de connexion
        String to = employee.getEmail();
        String subject = "Création de compte";
        String text = String.format("Bonjour %s,\n\nVotre compte a été créé avec succès. "
                        + "Voici vos informations de connexion :\nEmail: %s\nMot de passe: %s",
                employee.getFirstName(), employee.getEmail(), generatedPassword);

        emailService.send(to, subject, text);

        Employee createdEmployee = employeeRepository.save(employee);
        return EmployeeMapper.toDto(createdEmployee);
    }

    public EmployeeDto updateEmployee(UpsertEmployeeDto upsertEmployeeDto, Long employeeId) {
        if (!upsertEmployeeDto.getRoleName().equalsIgnoreCase(RoleEnum.MANAGER.name()) && !upsertEmployeeDto.getRoleName().equalsIgnoreCase(RoleEnum.OPERATOR.name())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Invalid role name");
        }
        RoleEnum roleEnum = RoleEnum.valueOf(upsertEmployeeDto.getRoleName().toUpperCase());

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Employee (User) not found"));

        Employee manager = null;
        if (upsertEmployeeDto.getManagerId() != null) {
            if (upsertEmployeeDto.getManagerId().equals(employeeId)) {
                throw new AppException(HttpStatus.BAD_REQUEST, "An employee cannot be the manager of itself");
            }
            manager = employeeRepository.findById(upsertEmployeeDto.getManagerId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Employee (Manager) not found"));
        }

        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Role not found"));

        if (userRepository.existsByEmailIgnoreCaseAndIdNot(upsertEmployeeDto.getEmail(), employeeId)) {
            throw new AppException(HttpStatus.CONFLICT, "Email already exists!");
        }

        Employee emp = EmployeeMapper.toEntity(upsertEmployeeDto, employee, role, manager);

        // TODO: send SMS userId email has been modified

        Employee updatedEmployee = employeeRepository.save(emp);
        return EmployeeMapper.toDto(updatedEmployee);
    }

    @Transactional
    public void deleteEmployee(Long id) throws IOException {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Employee not found"));

        List<Employee> subordinates = employeeRepository.findByManagerId(employee.getId());
        for (Employee subordinate : subordinates) {
            subordinate.setManager(null);
        }

        List<Warehouse> warehouses = warehouseRepository.findByManagerId(employee.getId());
        for (Warehouse warehouse : warehouses) {
            warehouse.setManager(null);
        }

        employeeRepository.delete(employee);

        String photoPath = employee.getPhotoPath();
        if (photoPath != null && !photoPath.isEmpty()) {
            fileService.deleteFile(photoPath);
        }
    }
}
