# DSA Digital Signature Tool

A desktop application built with Java and JavaFX that implements the **Digital Signature Algorithm (DSA)**. This tool allows users to securely sign texts and files, verify their authenticity, and manage cryptographic keys.

## Features

* **Key Generation:** Automatically generates secure DSA parameters (p, q, h) and key pairs (private key `a`, public key `b`).
* **Text Signing & Verification:** Input any text, generate a digital signature, and verify its integrity.
* **File Operations:** Sign and verify external files of any format.
* **Signature Management:** Save generated signatures (`.sig` or `.txt`) to your local drive and load them later for verification.
* **Hashing:** Uses `SHA-256` for secure message digesting.

## Technologies Used

* **Java:** Core logic, `java.math.BigInteger` for large prime calculations, and `java.security` for secure random generation and hashing.
* **JavaFX:** Graphical User Interface (GUI).
* **Maven:** Dependency management and build automation.

## Prerequisites

To run this project, you will need:
* Java Development Kit (JDK) 11 or higher.
* Maven installed.
* JavaFX dependencies (managed via `pom.xml`).

## How to Run

1. Clone the repository:
   ```bash
   git clone 
   ```
2. Navigate to the project directory:
   ```bash
   cd your-repo-name
   ```
3. Update Maven dependencies and run the application. The main entry point is `org.gui.DSAGUI`.
   If using an IDE like IntelliJ IDEA or Eclipse, simply run the `DSAGUI.java` class.

## Project Structure

* `org.gui.DSAGUI` - The main JavaFX application providing the user interface.
* `org.krypto.DSALogic` - Contains the core cryptographic math (parameter generation, signing, verifying).
* `org.krypto.SystemParams` / `KeyPair` / `Signature` - Data models storing cryptographic components.

## License

This project is open-source and available under the [MIT License](LICENSE).
