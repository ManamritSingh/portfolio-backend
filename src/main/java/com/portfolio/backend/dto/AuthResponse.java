//package com.portfolio.backend.dto;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//
//@Data
//@AllArgsConstructor
//public class AuthResponse {
//    private String token;
//}
//

package com.portfolio.backend.dto;

public class AuthResponse {

    private String jwt;

    // Add this constructor
    public AuthResponse(String jwt) {
        this.jwt = jwt;
    }

    // You might also want a no-arg constructor and getters/setters
    public AuthResponse() {}

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
