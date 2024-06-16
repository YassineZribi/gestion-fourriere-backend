package com.yz.pferestapi.bootstrap;

import com.yz.pferestapi.entity.Role;
import com.yz.pferestapi.entity.RoleEnum;
import com.yz.pferestapi.entity.User;
import com.yz.pferestapi.repository.RoleRepository;
import com.yz.pferestapi.repository.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Seeder2ForAdminCreation implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    public Seeder2ForAdminCreation(
            RoleRepository roleRepository,
            UserRepository  userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createAdmin();
    }

    private void createAdmin() {
        String email = "admin.admin@gmail.com";

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.ADMIN);
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalRole.isEmpty() || optionalUser.isPresent()) {
            return;
        }

        var user = User.builder()
                .firstName("Admin")
                .lastName("Admin")
                .email(email)
                .phoneNumber("+21622585016")
                .password(passwordEncoder.encode("1234"))
                .role(optionalRole.get())
                .position("Admin")
                .build();

        userRepository.save(user);
    }
}
