# SmartBudget Launcher Icon

## Overview
Custom launcher icon created for the SmartBudget app with emerald green branding.

## Design Elements

### Background (`ic_launcher_background.xml`)
- **Gradient**: Linear gradient from Emerald 500 (#10B981) to Emerald 600 (#059669)
- **Angle**: 135 degrees for modern diagonal effect
- **Overlay**: Subtle white overlay on top half for depth

### Foreground (`ic_launcher_foreground.xml`)
- **Main Icon**: White wallet with card slot
- **Accent**: Green card inside wallet with white details
- **Dollar Sign**: Emerald green dollar symbol on the right
- **Style**: Modern, clean, finance-focused design

### Monochrome (`ic_launcher_monochrome.xml`)
- **Purpose**: Android 13+ themed icons support
- **Colors**: Grayscale version of the wallet design
- **Compatibility**: Automatically adapts to user's theme color

## Icon Files Structure

```
app/src/main/res/
├── drawable/
│   ├── ic_launcher_background.xml (NEW - Emerald gradient background)
│   ├── ic_launcher_foreground.xml (UPDATED - Wallet icon)
│   └── ic_launcher_monochrome.xml (NEW - Android 13+ themed icon)
├── mipmap-anydpi-v26/
│   ├── ic_launcher.xml (UPDATED - References new drawables)
│   └── ic_launcher_round.xml (UPDATED - References new drawables)
├── mipmap-mdpi/
│   ├── ic_launcher.webp (Existing - Fallback)
│   └── ic_launcher_round.webp (Existing - Fallback)
├── mipmap-hdpi/ [...]
├── mipmap-xhdpi/ [...]
├── mipmap-xxhdpi/ [...]
└── mipmap-xxxhdpi/ [...]
```

## Features

✅ **Adaptive Icon Support** - Works on Android 8.0+ with dynamic shapes
✅ **Themed Icon Support** - Android 13+ monochrome variant
✅ **Brand Consistency** - Matches app's emerald green color scheme (#059669)
✅ **Modern Design** - Professional wallet icon with dollar sign
✅ **Multiple Densities** - Supports all screen densities via adaptive icons
✅ **Round Icon Variant** - Separate configuration for round launchers

## Testing

To view the new launcher icon:
1. Build and install the app on a device/emulator
2. Check the app launcher
3. On Android 13+, test with different themed icon settings
4. Verify appearance on different launcher types (round, square, squircle)

## Color References

- Primary Green: `#059669` (Emerald 600)
- Light Green: `#10B981` (Emerald 500)
- White: `#FFFFFF`

## Build Status
✅ Successfully built and compiled
✅ No linter errors
✅ All resources validated
