package org.csbf.ecomie.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.csbf.ecomie.constant.Role;
import org.csbf.ecomie.utils.commons.BaseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_users")
public class UserEntity extends BaseEntity implements UserDetails {

    private String firstName;

    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    /** PROFILE INFO */
    @Column(nullable = true)
    private String phoneNumber;

    @Column(nullable = true)
    private String country;

    @Column(nullable = true)
    private String region;

    @Column(nullable = true)
    private String city;

    @Column(nullable = true)
    private String language;

    @Column(nullable = true)
    private String profilePictureFileName;
    /** END */

    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean accountEnabled;

    private String emailVerificationToken;

    private Boolean accountBlocked;

    private Boolean accountSoftDeleted;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.accountBlocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {return this.accountEnabled;}

    public boolean toggleEnable() {return !this.accountEnabled;}

    public boolean toggleBlock() {return !this.accountBlocked;}

    public boolean toggleSoftDelete() {return !this.accountSoftDeleted;}

}
