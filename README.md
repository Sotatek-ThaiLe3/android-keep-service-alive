# App Behavior Overview

## 1. App Launch (Normal Mode)
- Bind the **SERVICE** to the **ACTIVITY**.

## 2. On Button Click
- Start a **FOREGROUND SERVICE** (this service will be noticeable to the user).

## 3. On App Termination
- Start the **FOREGROUND SERVICE** again (this will also be noticeable to the user).

## 4. Restoring UI and Data
- Retrieve the previous data from the **SERVICE** to restore the UI and app state.
