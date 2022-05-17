**Visual File Browser**

**Description**: A desktop file browser that displays visual previews for text documents (.txt, .md) and images (.png, .jpg, .bmp). Includes all the typical functionality expected from a modern file browser and supports hotkey shortcuts for all functionality.

**Core Functionality**:
- Displays a list of the files and directories within a selected directory
- Allows full navigation between directories
- Displays the path of a selected file or directory
- Displays a visual preview of a selected text or image file
- Allows renaming, moving, and deleting of files and directories
- Toggle the visibility of hidden files

**Sample Screenshots**:

Image File Preview

![Visual File Browser - Image Preview](https://user-images.githubusercontent.com/55682406/168735363-6603c046-f82e-4e36-96fa-2d1f49c3b843.png)

Text Document Preview

![Visual File Browser - Text Preview](https://user-images.githubusercontent.com/55682406/168736544-f3d78d53-c516-48ed-9f32-cd62385b55bd.png)

Delete File

![Visual File Browser - Delete File](https://user-images.githubusercontent.com/55682406/168735829-1d086e5e-8bc9-4b08-bae3-d837f82c525b.png)

**Developers**: Emon Sen Majumder

**Developed using**: kotlinc-jvm 1.5.21 (OpenJDK 11.0.12+7), JavaFX

**Tested on**: macOS 10.14.6 (MacBook Air 2015)

**Commands**:

**Quit** - Hotkey (Ctrl+Shift+Q) or Menu (File -> Quit)

**Home** - Hotkey (/) or Button (Home) or Menu (View -> Home)

**Next** - Hotkey (Enter) or Button (Next) or Menu (View -> Next)

**Prev** - Hotkey (Backspace or Delete) or Button (Prev) or Menu (View -> Prev)

**Rename** - Hotkey (Ctrl+Shift+R) or Button (Rename) or Menu (Actions -> Rename)

**Move** - Hotkey (Ctrl+Shift+M) or Button (Move) or Menu (Actions -> Move)

**Delete** - Hotkey (Ctrl+Shift+D) or Button (Delete) or Menu (Actions -> Delete)

**Show Hidden Files** - Hotkey (H) or Menu (Options -> Show/Hide Hidden Files)

**Notes**:

1. The Rename, Move, and Delete buttons & menu dropdown items are only enabled when a file or directory is currently selected from the list of files and directories on the left-hand side.

2. The Next button & menu dropdown item are only enabled when a directory (i.e. not a file) is currently selected from the list of files and directories on the left-hand side.
