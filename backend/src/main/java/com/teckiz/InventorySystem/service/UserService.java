package com.teckiz.InventorySystem.service;

import com.teckiz.InventorySystem.dto.LoginRequest;
import com.teckiz.InventorySystem.dto.RegisterRequest;
import com.teckiz.InventorySystem.dto.Response;
import com.teckiz.InventorySystem.dto.UserDTO;
import com.teckiz.InventorySystem.entity.User;

public interface UserService {
    Response registerUser(RegisterRequest registerRequest);
    Response loginUser(LoginRequest loginRequest);
    Response getAllUsers();
    User getCurrentLoggedInUser();
    Response updateUser(Long id, UserDTO userDTO);
    Response deleteUser(Long id);
    Response getUserTransactions(Long id);
}
