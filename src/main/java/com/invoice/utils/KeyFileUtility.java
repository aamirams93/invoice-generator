package com.invoice.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/* currently this classs not using anywhere  when we need to change key then it will the genrate the key*/

public class KeyFileUtility
{

	private static final String ALGORITHM = "RSA";
	private static final int KEY_SIZE = 2048;
	private static final String PUBLIC_KEY_FILE = "public.key";
	private static final String PRIVATE_KEY_FILE = "private.key";

	private KeyFileUtility()
	{
	}

	public static void rsaAlgo()
	{
		try
		{
			// 1. GENERATE the RSA Key Pair
			KeyPair keyPair = generateKeyPair();
			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();

			System.out.println("--- Step 1 & 2: Key Pair Generated ---");

			// 2. WRITE the Public Key to a file
			writeKeyToFile(publicKey.getEncoded(), PUBLIC_KEY_FILE, "PUBLIC");

			// 3. WRITE the Private Key to a file (Secret!)
			writeKeyToFile(privateKey.getEncoded(), PRIVATE_KEY_FILE, "PRIVATE");

			System.out.println("\nPublic Key saved to: " + PUBLIC_KEY_FILE);
			System.out.println("Private Key saved to: " + PRIVATE_KEY_FILE);

			// 4. LOAD/Extract the Private Key from its file
			PrivateKey loadedPrivateKey = loadPrivateKeyFromFile(PRIVATE_KEY_FILE);

			System.out.println("\n--- Step 4: Private Key Loaded from File ---");
			System.out.println("Loaded Private Key Algorithm: " + loadedPrivateKey.getAlgorithm());
			System.out.println("Loaded Private Key Format: " + loadedPrivateKey.getFormat());

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Generates a new RSA KeyPair.
	 */
	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException
	{
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
		keyGen.initialize(KEY_SIZE);
		return keyGen.generateKeyPair();
	}

	/**
	 * Writes the raw key bytes (Base64 encoded) to a file, wrapped with PEM
	 * headers.
	 */
	public static void writeKeyToFile(byte[] keyBytes, String filename, String type) throws IOException
	{
		String base64Key = Base64.getEncoder().encodeToString(keyBytes);

		try (FileWriter writer = new FileWriter(filename))
		{
			writer.write(String.format("-----BEGIN %s KEY-----\n", type));
			// Write the key in blocks (e.g., 64 characters per line) for standard PEM
			// format
			writer.write(base64Key.replaceAll("(.{64})", "$1\n"));
			writer.write(String.format("\n-----END %s KEY-----\n", type));
		}
	}

	/**
	 * Loads the Private Key from a file and reconstructs the PrivateKey object.
	 */
	public static PrivateKey loadPrivateKeyFromFile(String filename)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
	{

		// 1. Read file content and remove headers/footers/newlines
		String keyContent = Files.readString(Paths.get(filename));
		keyContent = keyContent.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
				.replaceAll("\\s", ""); // Remove all whitespace/newlines

		// 2. Base64 Decode the key content
		byte[] keyBytes = Base64.getDecoder().decode(keyContent);

		// 3. Define the specification for the key (PKCS8 for Private Key)
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);

		// 4. Generate the PrivateKey object
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		return keyFactory.generatePrivate(spec);
	}
}
