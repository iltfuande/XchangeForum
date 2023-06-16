package com.terokhin.graduate.model.enity;

import com.terokhin.graduate.model.dto.user.UserDto;
import com.terokhin.graduate.security.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@Entity
@Table(name = "user", schema = "public")
@ToString
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(max = 50)
    @NotNull
    @Column
    private String email;

    @Length(max = 100)
    @NotNull
    @Column
    private String password;

    @Length(max = 50)
    @NotNull
    @Column
    private String nickname;

    @Length(max = 50)
    @NotNull
    @Column
    private String firstName;

    @Length(max = 50)
    @NotNull
    @Column
    private String lastName;

    @NotNull
    @Column
    private Instant timeCreated;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private boolean banned;

    @Column
    private boolean deleted;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return nickname;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !banned;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !deleted;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public UserDto mapUserDto(User user, ModelMapper modelMapper) {
        return modelMapper.map(user, UserDto.class);
    }
}
