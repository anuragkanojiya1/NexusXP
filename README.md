# NexusXP

Welcome to the NexusXP Project! This project combines the immersive power of AR/VR with blockchain technology using Mantle. The game is developed in Jetpack Compose and includes features such as collectible items, real-time score tracking, and blockchain integration for managing in-game rewards and purchases.

## Features

- **AR/VR Gameplay**: A player-controlled model navigates an augmented environment to collect items and earn scores.
- **Blockchain Integration**: Mantle blockchain is used for:
  - Tracking player scores.
  - Unlocking and purchasing skins or items.
  - Managing game rewards.
- **Jetpack Compose**: A modern UI toolkit for creating a seamless and dynamic user interface.
- **Collision Detection**: Proximity-based detection for collecting in-game items.
- **Continuous Movement Controls**: Intuitive button controls for navigating the player model.
- **Key Encryption**: User's private key is stored using Encryption in android which secure the credential of the user from any malicious source.

---

## Mantle Blockchain Smart Contract Functions

This project incorporates the following Solidity smart contract functions (Contract Address:```0x855ca462005f7DacC1E5c9ea29D43A2f84B58bda```):

- addItem: Adds a new item/skin to the game with specified name, unlock score, and price.

- updateScore: Updates the score for a player.

- unlockItem: Unlocks an item for a player based on their score.

- buyItem: Allows a player to purchase an item using funds.

- getAllItems: Retrieves all available items/skins in the game.

- withdraw: Withdraws funds collected from item sales.

---

## Project Setup

### Prerequisites

1. **Android Studio**: Download and install Android Studio from [here](https://developer.android.com/studio).
2. **Android sdk**: Install Android sdk in Android studio to run the app on your physical device. Ensure all dependencies are synced by running `Sync Now` in Android Studio after opening the project.

---

## Setting up Android SDK and Emulator

1. **Install Android Studio**:
   - Download and install the latest version of Android Studio from [here](https://developer.android.com/studio).

2. **Configure Android SDK**:
   - Open Android Studio.
   - If Android sdk is installed correctly without errors at the start after Android studio installation then go to step 3.
   - Go to **File > Settings > Appearance & Behavior > System Settings > Android SDK**.
   - In the **SDK Platforms** tab, check the latest Android API level (e.g., API 35) and click **Apply**.
   - In the **SDK Tools** tab, select the required tools, including:
     - Android SDK Build-Tools
     - Android Emulator
     - Android SDK Command-line Tools
   - Click **Apply** to download and install.

3. **Set Up a Physical Device:**:
   - Connect your Android device to your computer using a USB cable.
   - Enable Developer Options on the device:
   - Go to Settings > About Phone > Build Number and tap it seven times to enable Developer Options.
   - Enable USB Debugging in Developer Options.
   - Click allow when Android studio tries to connect with you phone, if it doesn't appear then try to loose the USB cable a little bit sometimes they can be a problem.
   - When your device is connected, your device name will show near the run button.

4. **Test the Setup**:
   - Build and run the project by clicking the **Run** button in Android Studio.
   - The app will take some time and eventually get launched on your android device.

---

## Demo of the Game

https://www.youtube.com/shorts/TfeHb0OnGz4

## Running the Project

1. Clone the repository:
   ```bash
   git clone https://github.com/anuragkanojiya1/NexusXP
   ```
2. Open the project in Android Studio.
3. Sync Gradle dependencies.
4. Run the app on a physical device or emulator(Preferably physical device here).

---

## Contribution Guidelines

We welcome contributions! To contribute:
1. Fork the repository.
2. Create a feature branch.
3. Submit a pull request with a detailed description.

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## Support

If you encounter any issues or have questions, feel free to reach out via the Issues section of this repository.

