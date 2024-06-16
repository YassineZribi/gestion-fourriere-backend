package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.UpsertUserDto;
import com.yz.pferestapi.dto.UserCriteriaRequest;
import com.yz.pferestapi.dto.UserDto;
import com.yz.pferestapi.entity.*;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.mapper.UserMapper;
import com.yz.pferestapi.repository.InstitutionRepository;
import com.yz.pferestapi.repository.RoleRepository;
import com.yz.pferestapi.repository.UserRepository;
import com.yz.pferestapi.repository.WarehouseRepository;
import com.yz.pferestapi.specification.UserSpecifications;
import com.yz.pferestapi.util.CriteriaRequestUtil;
import com.yz.pferestapi.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final InstitutionRepository institutionRepository;
    private final WarehouseRepository warehouseRepository;
    private final RoleRepository roleRepository;
    private final FileService fileService;

    public List<User> getAllUsers() {
        return userRepository.findAll();

    }

    public UserDto getEmployeeWithRecursiveSubordinates(Long id) {
        User manager = userRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Employee (Manager) not found"));

        UserDto userDto = UserMapper.toDto(manager);
        buildSubordinates(userDto);
        return userDto;
    }

    public UserDto getChiefExecutiveWithRecursiveSubordinates() {
        Institution institution = institutionRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Institution not found"));
        User chiefExecutive = institution.getChiefExecutive();
        if (chiefExecutive == null)
            throw new AppException(HttpStatus.NOT_FOUND, "The institution has not yet selected or appointed a Chief Executive Officer.");

        UserDto userDto = UserMapper.toDto(chiefExecutive);
        buildSubordinates(userDto);
        return userDto;
    }

    private void buildSubordinates(UserDto manager) {
        List<User> subordinates = userRepository.findByManagerId(manager.getId());
        for (User subordinate : subordinates) {
            UserDto subordinateDto = UserMapper.toDto(subordinate);
            buildSubordinates(subordinateDto);
            manager.addSubordinate(subordinateDto);
        }
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public List<User> findUsersByFullName(String search) {
        Specification<User> spec = Specification.where(null);

        // Get non admins
        spec = spec.and(UserSpecifications.notHasRole(RoleEnum.ADMIN.toString()));

        spec = spec.and(UserSpecifications.fullNameContains(search));

        return userRepository.findAll(spec);
    }

    public Page<User> findUsersByCriteria(UserCriteriaRequest userCriteria) {
        Specification<User> spec = Specification.where(null);

        // Get non admins
        spec = spec.and(UserSpecifications.notHasRole(RoleEnum.ADMIN.toString()));

        // Get users with specific role
        if (userCriteria.getRoleName() != null) {
            spec = spec.and(UserSpecifications.hasRole(userCriteria.getRoleName()));
        }

        if (userCriteria.getFirstName() != null) {
            spec = spec.and(UserSpecifications.firstNameContains(userCriteria.getFirstName()));
        }

        if (userCriteria.getLastName() != null) {
            spec = spec.and(UserSpecifications.lastNameContains(userCriteria.getLastName()));
        }

        if (userCriteria.getEmail() != null) {
            spec = spec.and(UserSpecifications.emailContains(userCriteria.getEmail()));
        }

        if (userCriteria.getPhoneNumber() != null) {
            spec = spec.and(UserSpecifications.phoneNumberContains(userCriteria.getPhoneNumber()));
        }

        // Get employees having a specific manager
        if (userCriteria.getManagerId() != null) {
            spec = spec.and(UserSpecifications.hasManager(userCriteria.getManagerId()));
        }

        if (userCriteria.getPosition() != null) {
            spec = spec.and(UserSpecifications.positionContains(userCriteria.getPosition()));
        }

        Sort sort = CriteriaRequestUtil.buildSortCriteria(userCriteria);

        Pageable pageable = CriteriaRequestUtil.createPageable(userCriteria, sort);

        return userRepository.findAll(spec, pageable);
    }

    public User createUser(UpsertUserDto upsertUserDto) {
        Institution institution = institutionRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Institution not found"));

        User manager = null;
        if (upsertUserDto.getManagerId() != null) {
            manager = userRepository.findById(upsertUserDto.getManagerId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Employee (Manager) not found"));
        }

        if (!upsertUserDto.getRoleName().equalsIgnoreCase(RoleEnum.MANAGER.name()) && !upsertUserDto.getRoleName().equalsIgnoreCase(RoleEnum.OPERATOR.name())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Invalid role name");
        }
        RoleEnum roleEnum = RoleEnum.valueOf(upsertUserDto.getRoleName().toUpperCase());

        if (userRepository.existsByEmail(upsertUserDto.getEmail())) {
            throw new AppException(HttpStatus.CONFLICT, "Email already exists!");
        }

        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Role not found"));

        String generatedPassword = PasswordGenerator.generate(10);

        System.out.println("generatedPassword = " + generatedPassword);
        
        var user = User.builder()
                .firstName(upsertUserDto.getFirstName())
                .lastName(upsertUserDto.getLastName())
                .email(upsertUserDto.getEmail())
                .phoneNumber(upsertUserDto.getPhoneNumber())
                .password(passwordEncoder.encode(generatedPassword))
                .role(role)
                .position(upsertUserDto.getPosition())
                .manager(manager)
                .institution(institution)
                .build();

        // TODO: send email (login: email, password: generatedPassword)

        return userRepository.save(user);
    }

    public User updateUser(UpsertUserDto upsertUserDto, Long userId) {
        if (!upsertUserDto.getRoleName().equalsIgnoreCase(RoleEnum.MANAGER.name()) && !upsertUserDto.getRoleName().equalsIgnoreCase(RoleEnum.OPERATOR.name())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Invalid role name");
        }
        RoleEnum roleEnum = RoleEnum.valueOf(upsertUserDto.getRoleName().toUpperCase());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));

        User manager = null;
        if (upsertUserDto.getManagerId() != null) {
            if (upsertUserDto.getManagerId().equals(userId)) {
                throw new AppException(HttpStatus.BAD_REQUEST, "An employee cannot be the manager of itself");
            }
            manager = userRepository.findById(upsertUserDto.getManagerId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Employee (Manager) not found"));
        }

        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Role not found"));

        Optional<User> optionalUser = userRepository.findByEmail(upsertUserDto.getEmail());

        if (optionalUser.isPresent() && !optionalUser.get().getId().equals(userId)) {
            throw new AppException(HttpStatus.CONFLICT, "Email already exists!");
        }

        user.setFirstName(upsertUserDto.getFirstName());
        user.setLastName(upsertUserDto.getLastName());
        user.setEmail(upsertUserDto.getEmail());
        user.setPhoneNumber(upsertUserDto.getPhoneNumber());
        user.setRole(role);
        user.setPosition(upsertUserDto.getPosition());
        user.setManager(manager);

        // TODO: send SMS userId email has been modified

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) throws IOException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));

        List<User> subordinates = userRepository.findByManagerId(user.getId());
        for (User subordinate : subordinates) {
            subordinate.setManager(null);
        }

        List<Warehouse> warehouses = warehouseRepository.findByManagerId(user.getId());
        for (Warehouse warehouse : warehouses) {
            warehouse.setManager(null);
        }

        userRepository.delete(user);

        String photoPath = user.getPhotoPath();
        if (photoPath != null && !photoPath.isEmpty()) {
            fileService.deleteFile(photoPath);
        }
    }
}
