# DSA Digital Signature Tool

A desktop application built with Java and JavaFX that implements the **Digital Signature Algorithm (DSA)**. This tool allows users to securely sign texts and files, verify their authenticity, and manage cryptographic keys.

## Features

* **Key Generation:** Automatically generates secure DSA parameters (p, q, h) and key pairs (private key `a`, public key `b`).
* **Text & File Signing:** Generate a digital signature for any text input or external file of any format.
* **Autonomous Verification:** Signature files (`.sig`) now automatically store all necessary verification data (s1, s2, p, q, h, and public key b). Reviewers can verify documents simply by loading the signature file.
* **Hashing:** Uses `SHA-256` for secure message digesting.
* **Standalone Executable:** Packaged as a `.exe` Windows installer, requiring no Java installation for end-users.

## Technologies Used

* **Java:** Core logic, `java.math.BigInteger` for large prime calculations, and `java.security` for secure random generation and hashing.
* **JavaFX:** Graphical User Interface (GUI).
* **Maven:** Dependency management and build automation.
* **jpackage & WiX Toolset:** Used for compiling the standalone Windows installer.

## Prerequisites

To compile and run this project from source, you will need:
* Java Development Kit (JDK) 14 or higher.
* Maven installed.
* JavaFX dependencies (managed via `pom.xml`).
* WiX Toolset v3.11+ (only required if building the `.exe` installer).

## Installation and Usage

**For End Users:**
Download the latest `DSASigner-1.0.exe` from the Releases tab and run the installer. No Java installation or setup is required.

**For Developers:**
1. Clone the repository:
   ```bash
   git clone 
   ```
2. Navigate to the project directory:
   ```bash
   cd your-repo-name
   ```
3. Update Maven dependencies and run the application. The main entry point is `org.krypto.Main`.
4. To build the `.exe` installer manually, compile your project into a `.jar` artifact and run the following command in your terminal:
   ```powershell
   jpackage --type exe --input . --name "DSASigner" --main-jar your-artifact-name.jar --main-class org.krypto.Main --win-shortcut --win-menu --win-dir-chooser
   ```

## Project Structure

* `org.gui.DSAGUI` - The main JavaFX application providing the user interface layout.
* `org.gui.DSAController` - Handles GUI events, file operations, and connects the UI with the cryptographic logic.
* `org.krypto.Main` - The isolated entry point required for `jpackage` execution.
* `org.krypto.DSALogic` - Contains the core cryptographic math (parameter generation, signing, verifying).
* `org.krypto.SystemParams` / `KeyPair` / `Signature` - Data models storing cryptographic components.

## License

This project is open-source and available under the [MIT License](LICENSE).
