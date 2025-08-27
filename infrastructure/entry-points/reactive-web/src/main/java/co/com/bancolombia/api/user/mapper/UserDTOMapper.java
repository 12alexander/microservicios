package co.com.bancolombia.api.user.mapper;

import co.com.bancolombia.api.user.dto.UserRequestDTO;
import co.com.bancolombia.api.user.dto.UserResponseDTO;
import co.com.bancolombia.model.user.User;

public class UserDTOMapper {

    private UserDTOMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static User toDomain(UserRequestDTO request){
        return User.builder()
                .name(request.getName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .address(request.getAddress())
                .phone(request.getPhone())
                .emailAddress(request.getEmailAddress())
                .baseSalary(request.getBaseSalary())
                .build();
    }

    public static UserResponseDTO toResponse(User user){
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .address(user.getAddress())
                .phone(user.getPhone())
                .emailAddress(user.getEmailAddress())
                .baseSalary(user.getBaseSalary())
                .build();
    }

}
