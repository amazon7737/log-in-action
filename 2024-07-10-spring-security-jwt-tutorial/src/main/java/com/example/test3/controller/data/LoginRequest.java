package com.example.test3.controller.data;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @Size(min = 3, max = 50)
    @NotNull
    private String username;

    @Size(min = 3, max = 100)
    @NotNull
    private String password;
}
