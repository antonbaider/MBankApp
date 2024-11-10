package com.mthree.mbank.mapper;

import com.mthree.mbank.dto.account.AccountDTO;
import com.mthree.mbank.dto.auth.RegisterRequest;
import com.mthree.mbank.dto.user.UserDTO;
import com.mthree.mbank.entity.Account;
import com.mthree.mbank.entity.User;
import com.mthree.mbank.entity.UserProfile;
import com.mthree.mbank.entity.enums.Role;
import com.mthree.mbank.entity.enums.Status;
import com.mthree.mbank.entity.enums.UserType;
import javax.annotation.processing.Generated;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-10T17:28:22+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (OpenLogic)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toUserDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO.UserDTOBuilder userDTO = UserDTO.builder();

        userDTO.firstName( userProfileFirstName( user ) );
        userDTO.lastName( userProfileLastName( user ) );
        userDTO.email( userProfileEmail( user ) );
        userDTO.phone( userProfilePhone( user ) );
        userDTO.ssn( userProfileSsn( user ) );
        userDTO.username( userProfileUsername( user ) );
        userDTO.role( mapRoleToString( user.getRole() ) );

        return userDTO.build();
    }

    @Override
    public User toUser(RegisterRequest registerRequest, PasswordEncoder passwordEncoder) {
        if ( registerRequest == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.profile( toUserProfile( registerRequest, passwordEncoder ) );

        user.role( Role.ROLE_USER );
        user.status( Status.ACTIVE );
        user.type( UserType.STANDARD );

        return user.build();
    }

    @Override
    public AccountDTO toAccountDTO(Account account) {
        if ( account == null ) {
            return null;
        }

        AccountDTO.AccountDTOBuilder accountDTO = AccountDTO.builder();

        accountDTO.cardNumber( account.getCardNumber() );
        accountDTO.expirationDate( account.getExpirationDate() );
        accountDTO.currency( account.getCurrency() );
        accountDTO.balance( account.getBalance() );

        return accountDTO.build();
    }

    private String userProfileFirstName(User user) {
        if ( user == null ) {
            return null;
        }
        UserProfile profile = user.getProfile();
        if ( profile == null ) {
            return null;
        }
        String firstName = profile.getFirstName();
        if ( firstName == null ) {
            return null;
        }
        return firstName;
    }

    private String userProfileLastName(User user) {
        if ( user == null ) {
            return null;
        }
        UserProfile profile = user.getProfile();
        if ( profile == null ) {
            return null;
        }
        String lastName = profile.getLastName();
        if ( lastName == null ) {
            return null;
        }
        return lastName;
    }

    private String userProfileEmail(User user) {
        if ( user == null ) {
            return null;
        }
        UserProfile profile = user.getProfile();
        if ( profile == null ) {
            return null;
        }
        String email = profile.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }

    private String userProfilePhone(User user) {
        if ( user == null ) {
            return null;
        }
        UserProfile profile = user.getProfile();
        if ( profile == null ) {
            return null;
        }
        String phone = profile.getPhone();
        if ( phone == null ) {
            return null;
        }
        return phone;
    }

    private String userProfileSsn(User user) {
        if ( user == null ) {
            return null;
        }
        UserProfile profile = user.getProfile();
        if ( profile == null ) {
            return null;
        }
        String ssn = profile.getSsn();
        if ( ssn == null ) {
            return null;
        }
        return ssn;
    }

    private String userProfileUsername(User user) {
        if ( user == null ) {
            return null;
        }
        UserProfile profile = user.getProfile();
        if ( profile == null ) {
            return null;
        }
        String username = profile.getUsername();
        if ( username == null ) {
            return null;
        }
        return username;
    }
}
