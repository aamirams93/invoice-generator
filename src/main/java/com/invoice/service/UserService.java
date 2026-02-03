package com.invoice.service;

import java.util.List;

import com.invoice.binding.SuperUser;
import com.invoice.binding.UserAddingData;
import com.invoice.binding.UserData;

public interface UserService
{
	public boolean saveUser(UserAddingData userData, String ip);

	public boolean saveSuperUser(SuperUser userData, String ip);

	public List<UserData> getAllUsers();

	public String getUserById(String email);

	public boolean changeStatus(Integer userId, boolean accStatus);

	public UserData getUserByEmail(String emailId);

	public void generateEmailOtp(UserData email);

	public void logoutUser(String jwtToken);

	public void logLoginSuccess(String email, String ip);

	public boolean isLoginBlocked(String email);

}
