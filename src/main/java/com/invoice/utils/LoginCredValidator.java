package com.invoice.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class LoginCredValidator
{
	private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
	private static final String NUMBER_INPUT = "0123456789";
	private static final String SPECIAL_CHAR = "![,]@#$%^&*()-=+{};:/<>?|\\";
	private static final String ALLCHARS = UPPER_CASE + LOWER_CASE + NUMBER_INPUT + SPECIAL_CHAR;
	private static final int PASSWORD_LENGTH = 8;

	// generation random password for new user
	private LoginCredValidator()
	{
	}

	public static String randomMixPassword()
	{
		SecureRandom random = new SecureRandom();

		List<Character> result = new ArrayList<>();

		// Force one character from each set
		result.add(UPPER_CASE.charAt(random.nextInt(UPPER_CASE.length())));
		result.add(LOWER_CASE.charAt(random.nextInt(LOWER_CASE.length())));
		result.add(NUMBER_INPUT.charAt(random.nextInt(NUMBER_INPUT.length())));
		result.add(SPECIAL_CHAR.charAt(random.nextInt(SPECIAL_CHAR.length())));

		for (int i = 4; i < PASSWORD_LENGTH; i++)
		{
			result.add(ALLCHARS.charAt(random.nextInt(ALLCHARS.length())));
		}

		// Shuffle to prevent predictable order
		Collections.shuffle(result);

		StringBuilder sb = new StringBuilder();
		for (char ch : result)
		{
			sb.append(ch);
		}

		return sb.toString();
	}

	public static String randomP()
	{
		SecureRandom random = new SecureRandom();
		int otp = 100000 + random.nextInt(900000); // generates a number between 100000–999999
		return String.valueOf(otp);
	}

	// user input pass word validations
	public static String userP(String userPassword)
	{
		boolean hasUpper = Pattern.compile("[" + Pattern.quote(UPPER_CASE) + "]").matcher(userPassword).find();
		boolean hasLower = Pattern.compile("[" + Pattern.quote(LOWER_CASE) + "]").matcher(userPassword).find();
		boolean hasDigit = Pattern.compile("[" + Pattern.quote(NUMBER_INPUT) + "]").matcher(userPassword).find();
		boolean hasSpecial = Pattern.compile("[" + Pattern.quote(SPECIAL_CHAR) + "]").matcher(userPassword).find();
		boolean isValid = hasUpper && hasLower && hasDigit && hasSpecial;
		if (isValid && userPassword.length() >= PASSWORD_LENGTH)
		{
			return "Password Accepted: " + userPassword;
		} else
		{
			return "Password Rejected " + userPassword;
		}
	}

	public static boolean isValidEmail(String email)
	{
		String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$";
		Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
		return pattern.matcher(email).matches();
	}

}