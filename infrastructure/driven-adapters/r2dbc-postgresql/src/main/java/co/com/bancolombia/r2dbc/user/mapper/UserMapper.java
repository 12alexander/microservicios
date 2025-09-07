package co.com.bancolombia.r2dbc.user.mapper;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.r2dbc.role.mapper.RoleMapper;
import co.com.bancolombia.r2dbc.user.data.UserData;

import java.time.LocalDateTime;

public class UserMapper {
    private UserMapper(){
        throw new IllegalStateException("Utility class");
    }

    public static UserData toDataForCreation(User user){
        LocalDateTime now = LocalDateTime.now();
        return UserData.builder()
                .id(user.getId())
                .name(user.getName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .address(user.getAddress())
                .phone(user.getPhone())
                .emailAddress(user.getEmailAddress())
                .baseSalary(user.getBaseSalary())
                .idRol(user.getIdRol())
                .password(user.getPassword())
                .creationDate(now)
                .updateDate(now)
                .build();
    }

    public static UserData toDataForUpdate(User user){
        LocalDateTime now = LocalDateTime.now();
        return UserData.builder()
                .id(user.getId())
                .name(user.getName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .address(user.getAddress())
                .phone(user.getPhone())
                .emailAddress(user.getEmailAddress())
                .baseSalary(user.getBaseSalary())
                .idRol(user.getIdRol())
                .password(user.getPassword())
                .updateDate(now)
                .build();
    }

    public static User toDomain(UserData userData){
        return User.builder()
                .id(userData.getId())
                .name(userData.getName())
                .lastName(userData.getLastName())
                .birthDate(userData.getBirthDate())
                .address(userData.getAddress())
                .phone(userData.getPhone())
                .emailAddress(userData.getEmailAddress())
                .baseSalary(userData.getBaseSalary())
                .idRol(userData.getIdRol())
                .password(userData.getPassword())
                .role(userData.getRole() != null ? RoleMapper.toDomain(userData.getRole()) : null)
                .build();
    }
}