package com.construct.constructAthens.security;

import com.construct.constructAthens.Employees.Employee;
import com.construct.constructAthens.Employees.EmployeeService;
import com.construct.constructAthens.security.entity.AuthRequest;
import com.construct.constructAthens.security.entity.UserInfo;
import com.construct.constructAthens.security.entity.UserInfoDto;
import com.construct.constructAthens.security.services.JwtService;
import com.construct.constructAthens.security.services.UserInfoService;
import com.construct.constructAthens.security.services.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserInfoService service;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private EmployeeService empService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @GetMapping("/getAll")
    public ResponseEntity<Object> getAllUsers() {
        List<UserInfoDto> usersDTO = service.getAllUsersDTO();
        return ResponseEntity.ok(usersDTO);
    }
    @PostMapping("/addNewUser")
    public String addNewUser(@RequestBody UserInfo userInfo) {
        return service.addUser(userInfo);
    }

    @PostMapping("/generateToken")
    public ResponseEntity<Object> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                Employee emp=empService.getEmployeeByUsername(authRequest.getUsername());
                String token = jwtService.generateToken(userDetails.getUsername(), userDetails.getAuthorities(), emp.getId().toString());
                return ResponseEntity.ok(token);
            } else {
                throw new BadCredentialsException("Invalid credentials");
            }
        } catch (BadCredentialsException e) {
            // This will be caught by the CustomExceptionHandler
            throw e;
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUserById(@PathVariable UUID id) {
        Optional<UserInfo> userOptional = service.getUserById(id);

        if (userOptional.isPresent()) {
            service.deleteUserById(id);
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
