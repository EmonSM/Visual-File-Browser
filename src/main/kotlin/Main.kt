import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.Stage
import java.io.File
import java.io.FileInputStream
import java.util.*

class Main : Application()  {

    override fun start(stage: Stage) {

        // Root of the Scene Graph
        val layout = BorderPane()

        // Variables used to track the current state of the file browser
        val homeDir = File("${System.getProperty("user.dir")}/test/")
        var currentDir = homeDir
        var showHiddenFiles = false

        // Menu Bar
        val menuBar = MenuBar()
        menuBar.padding = Insets(3.0)

        // Menu Bar: File Menu
        val fileMenu = Menu("File")

        val fileQuit = MenuItem("Quit")
        fileQuit.accelerator = KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN)
        fileQuit.setOnAction { Platform.exit() }
        fileMenu.items.add(fileQuit)

        // Menu Bar: View Menu
        val viewMenu = Menu("View")

        val viewHome = MenuItem("Home")
        viewHome.accelerator = KeyCodeCombination(KeyCode.SLASH)

        val viewPrev = MenuItem("Prev")
        viewPrev.accelerator = KeyCodeCombination(KeyCode.BACK_SPACE)

        val viewNext = MenuItem("Next")
        viewNext.accelerator = KeyCodeCombination(KeyCode.ENTER)
        viewNext.isDisable = true

        viewMenu.items.addAll(viewHome, viewPrev, viewNext)

        // Menu Bar: Action Menu
        val actionsMenu = Menu("Actions")

        val actionsRename = MenuItem("Rename")
        actionsRename.accelerator = KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN)
        actionsRename.isDisable = true

        val actionsMove = MenuItem("Move")
        actionsMove.accelerator = KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN)
        actionsMove.isDisable = true

        val actionsDelete = MenuItem("Delete")
        actionsDelete.accelerator = KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN)
        actionsDelete.isDisable = true

        actionsMenu.items.addAll(actionsRename, actionsMove, actionsDelete)

        // Menu Bar: Options Menu
        val optionsMenu = Menu("Options")

        val optionsToggleHidden = MenuItem("Show Hidden Files")
        optionsToggleHidden.accelerator = KeyCodeCombination(KeyCode.H)
        optionsMenu.items.add(optionsToggleHidden)

        // Add all menus to menu bar
        menuBar.menus.addAll(fileMenu, viewMenu, actionsMenu, optionsMenu)

        // Button Bar
        val buttonBar = HBox()
        buttonBar.padding = Insets(5.0)
        buttonBar.spacing = 10.0

        // Button Bar: Home Button
        val homeImageView = ImageView(Image("HomeIcon.png"))
        homeImageView.isPreserveRatio = true
        homeImageView.fitHeight = 19.0
        val homeButton = Button("Home", homeImageView)

        // Button Bar: Prev Button
        val prevImageView = ImageView(Image("PrevIcon.png"))
        prevImageView.isPreserveRatio = true
        prevImageView.fitHeight = 19.0
        val prevButton = Button("Prev", prevImageView)

        // Button Bar: Next Button
        val nextImageView = ImageView(Image("NextIcon.png"))
        nextImageView.isPreserveRatio = true
        nextImageView.fitHeight = 19.0
        val nextButton = Button("Next", nextImageView)
        nextButton.isDisable = true

        // Button Bar: Rename Button
        val renameImageView = ImageView(Image("RenameIcon.png"))
        renameImageView.isPreserveRatio = true
        renameImageView.fitHeight = 19.0
        val renameButton = Button("Rename", renameImageView)
        renameButton.isDisable = true

        // Button Bar: Move Button
        val moveImageView = ImageView(Image("MoveIcon.png"))
        moveImageView.isPreserveRatio = true
        moveImageView.fitHeight = 19.0
        val moveButton = Button("Move", moveImageView)
        moveButton.isDisable = true

        // Button Bar: Delete Button
        val deleteImageView = ImageView(Image("DeleteIcon.png"))
        deleteImageView.isPreserveRatio = true
        deleteImageView.fitHeight = 19.0
        val deleteButton = Button("Delete", deleteImageView)
        deleteButton.isDisable = true

        // Add all buttons to button bar
        buttonBar.children.addAll(homeButton, prevButton, nextButton, renameButton, moveButton, deleteButton)

        // Disable focus from all 6 buttons
        homeButton.isFocusTraversable = false
        prevButton.isFocusTraversable = false
        nextButton.isFocusTraversable = false
        renameButton.isFocusTraversable = false
        moveButton.isFocusTraversable = false
        deleteButton.isFocusTraversable = false

        // Top Position: Top Bars
        val topBars = VBox()
        topBars.children.addAll(menuBar, buttonBar)

        // Center Position: Blank File Preview
        val blank = TextArea()
        blank.isFocusTraversable = false
        blank.isDisable = true

        // Bottom Position: File Path Label
        val filePath = Label(currentDir.absolutePath + "/")
        filePath.padding = Insets(5.0)

        // Left Position: List of Files
        val listOfFiles = ListView<String>()
        resetToCurrentDirectory(filePath, currentDir, listOfFiles, showHiddenFiles, layout, blank)
        listOfFiles.isFocusTraversable = true

        // Handles files being clicked on
        listOfFiles.setOnMouseClicked {
            navigateToFile(listOfFiles, currentDir, filePath, nextButton, viewNext, renameButton, actionsRename, moveButton, actionsMove, deleteButton, actionsDelete, layout, blank)
            // Handle files being double-clicked (Same functionality as Next action)
            if (it.clickCount >= 2) { viewNext.fire() }
        }

        // Handle files being navigated using up and down arrows
        listOfFiles.setOnKeyPressed {
            navigateToFile(listOfFiles, currentDir, filePath, nextButton, viewNext, renameButton, actionsRename, moveButton, actionsMove, deleteButton, actionsDelete, layout, blank)
            // Enter Hotkey for Next action is implemented here because accelerator for "Enter" does not work
            if (it.code == KeyCode.ENTER) { viewNext.fire() }
            // Delete Hotkey for Prev action is implemented here because Prev action requires two hotkeys (Delete and Backspace)
            if (it.code == KeyCode.DELETE) { viewPrev.fire() }
        }

        // Implementation of menu item functionality
        viewHome.setOnAction {
            currentDir = homeDir
            resetToCurrentDirectory(filePath, currentDir, listOfFiles, showHiddenFiles, layout, blank)
            disableFourButtonsAndMenuItems(nextButton, viewNext, renameButton, actionsRename, moveButton, actionsMove, deleteButton, actionsDelete)
        }

        viewPrev.setOnAction {
            if (currentDir.parentFile != null) {
                currentDir = currentDir.parentFile
                resetToCurrentDirectory(filePath, currentDir, listOfFiles, showHiddenFiles, layout, blank)
                disableFourButtonsAndMenuItems(nextButton, viewNext, renameButton, actionsRename, moveButton, actionsMove, deleteButton, actionsDelete)
            }
        }

        viewNext.setOnAction {
            val currentFile = File(filePath.text)
            if (currentFile.isDirectory) {
                currentDir = currentFile
                resetToCurrentDirectory(filePath, currentDir, listOfFiles, showHiddenFiles, layout, blank)
                disableFourButtonsAndMenuItems(nextButton, viewNext, renameButton, actionsRename, moveButton, actionsMove, deleteButton, actionsDelete)
            }
        }

        actionsRename.setOnAction {
            if (listOfFiles.selectionModel.selectedItem != null) {
                val currentFile = File(filePath.text)
                val newNameDialog = TextInputDialog(currentFile.name)
                newNameDialog.title = "Rename \"" + filePath.text.substringAfterLast('/') + "\""
                newNameDialog.headerText = "Please enter the new name:"
                val newName = newNameDialog.showAndWait()

                if (newName.isPresent) {
                    var newFile = File(currentDir.absolutePath + "/" + newName.get())
                    if (currentDir.absolutePath == "/") newFile = File(currentDir.absolutePath + newName.get())
                    if (newFile.exists()) {
                        val alert = Alert(AlertType.ERROR)
                        alert.title = "Error"
                        alert.headerText = "Renaming \"" + filePath.text.substringAfterLast('/') + "\" failed."
                        alert.contentText = "A file or directory with the name \"" + newName.get() + "\" already exists in this directory."
                        alert.showAndWait()
                    } else {
                        if (currentFile.renameTo(newFile)) {
                            resetToCurrentDirectory(filePath, currentDir, listOfFiles, showHiddenFiles, layout, blank)
                            disableFourButtonsAndMenuItems(nextButton, viewNext, renameButton, actionsRename, moveButton, actionsMove, deleteButton, actionsDelete)
                        } else {
                            val alert = Alert(AlertType.ERROR)
                            alert.title = "Error"
                            alert.headerText = "Renaming \"" + filePath.text.substringAfterLast('/') + "\" failed."
                            alert.contentText = "\"" + newName.get() + "\" is an invalid name."
                            alert.showAndWait()
                        }
                    }
                }
            }
        }

        actionsMove.setOnAction {
            if (listOfFiles.selectionModel.selectedItem != null) {
                val currentFile = File(filePath.text)
                val newDirectoryDialog = TextInputDialog(currentDir.absolutePath)
                newDirectoryDialog.title = "Move \"" + filePath.text.substringAfterLast('/') + "\""
                newDirectoryDialog.headerText = "Please enter the new destination directory:"
                val newDirectory = newDirectoryDialog.showAndWait()

                if (newDirectory.isPresent) {
                    var newFile = File(newDirectory.get() + '/' + filePath.text.substringAfterLast('/'))
                    if (newDirectory.get().endsWith('/')) newFile = File(newDirectory.get() + filePath.text.substringAfterLast('/'))
                    if (newFile.exists()) {
                        val alert = Alert(AlertType.ERROR)
                        alert.title = "Error"
                        alert.headerText = "Moving \"" + filePath.text.substringAfterLast('/') + "\" failed."
                        alert.contentText = "A file or directory with the name \"" + filePath.text.substringAfterLast('/') + "\" already exists in the directory \"" + newDirectory.get() + "\"."
                        alert.showAndWait()
                    } else {
                        if (currentFile.renameTo(newFile)) {
                            resetToCurrentDirectory(filePath, currentDir, listOfFiles, showHiddenFiles, layout, blank)
                            disableFourButtonsAndMenuItems(nextButton, viewNext, renameButton, actionsRename, moveButton, actionsMove, deleteButton, actionsDelete)
                        } else {
                            val alert = Alert(AlertType.ERROR)
                            alert.title = "Error"
                            alert.headerText = "Moving \"" + filePath.text.substringAfterLast('/') + "\" failed."
                            alert.contentText = "\"" + newDirectory.get() + "\" is an invalid destination directory."
                            alert.showAndWait()
                        }
                    }
                }
            }
        }

        actionsDelete.setOnAction {
            if (listOfFiles.selectionModel.selectedItem != null) {
                val currentFile = File(filePath.text)
                val alert = Alert(AlertType.CONFIRMATION)
                alert.title = "Delete \"" + filePath.text.substringAfterLast('/') + "\""
                alert.headerText = "Are you sure that you want to delete \"" + filePath.text.substringAfterLast('/') + "\"?"
                alert.contentText = "Click \"OK\" to delete this file. Otherwise, click \"Cancel\" to cancel this action."
                val stillDelete = alert.showAndWait()

                if (stillDelete.get() == ButtonType.OK) {
                    if (currentFile.deleteRecursively()) {
                        resetToCurrentDirectory(filePath, currentDir, listOfFiles, showHiddenFiles, layout, blank)
                        disableFourButtonsAndMenuItems(nextButton, viewNext, renameButton, actionsRename, moveButton, actionsMove, deleteButton, actionsDelete)
                    } else {
                        val alert2 = Alert(AlertType.ERROR)
                        alert2.title = "Error"
                        alert2.headerText = "Deleting \"" + filePath.text.substringAfterLast('/') + "\" failed."
                        alert2.contentText = "You do not have the access to delete \"" + filePath.text.substringAfterLast('/') + "\"."
                        alert2.showAndWait()
                    }
                }
            }
        }

        optionsToggleHidden.setOnAction {
            showHiddenFiles = !showHiddenFiles
            if (showHiddenFiles) optionsToggleHidden.text = "Hide Hidden Files"
            else optionsToggleHidden.text = "Show Hidden Files"
            resetToCurrentDirectory(filePath, currentDir, listOfFiles, showHiddenFiles, layout, blank)
            disableFourButtonsAndMenuItems(nextButton, viewNext, renameButton, actionsRename, moveButton, actionsMove, deleteButton, actionsDelete)
        }

        // Assign Button clicks to trigger the corresponding Menu Action event
        homeButton.setOnMouseClicked { viewHome.fire() }
        prevButton.setOnMouseClicked { viewPrev.fire() }
        nextButton.setOnMouseClicked { viewNext.fire() }
        renameButton.setOnMouseClicked { actionsRename.fire() }
        moveButton.setOnMouseClicked { actionsMove.fire() }
        deleteButton.setOnMouseClicked { actionsDelete.fire() }

        // Scene Graph: BorderPane
        layout.top = topBars
        layout.left = listOfFiles
        layout.center = blank
        layout.bottom = filePath

        // Stage
        val scene = Scene(layout)
        stage.width = 800.0
        stage.height = 500.0
        stage.title = "File Browser"
        stage.scene = scene
        stage.isResizable = false
        stage.show()
    }

    private fun navigateToFile(listOfFiles: ListView<String>, currentDir: File, filePath: Label,
                               nextButton: Button, viewNext: MenuItem,
                               renameButton: Button, actionsRename: MenuItem,
                               moveButton: Button, actionsMove: MenuItem,
                               deleteButton: Button, actionsDelete: MenuItem,
                               layout: BorderPane, blank: TextArea) {
        if (listOfFiles.selectionModel.selectedItem != null) {
            if (currentDir.absolutePath != "/") filePath.text =
                currentDir.absolutePath + "/" + listOfFiles.selectionModel.selectedItem
            else filePath.text = currentDir.absolutePath + listOfFiles.selectionModel.selectedItem

            nextButton.isDisable = !File(filePath.text).isDirectory
            viewNext.isDisable = !File(filePath.text).isDirectory
            renameButton.isDisable = false
            actionsRename.isDisable = false
            moveButton.isDisable = false
            actionsMove.isDisable = false
            deleteButton.isDisable = false
            actionsDelete.isDisable = false

            // Displays correct file preview based on file extension
            if (listOfFiles.selectionModel.selectedItem.lowercase().endsWith(".png") ||
                listOfFiles.selectionModel.selectedItem.lowercase().endsWith(".jpg") ||
                listOfFiles.selectionModel.selectedItem.lowercase().endsWith(".bmp")) {
                val input = FileInputStream(filePath.text)
                val imageFileView = ImageView(Image(input))
                imageFileView.isPreserveRatio = true
                imageFileView.fitHeight = 350.0
                imageFileView.isFocusTraversable = false
                imageFileView.isDisable = true
                layout.center = imageFileView
                input.close()
            } else if (listOfFiles.selectionModel.selectedItem.lowercase().endsWith(".txt") ||
                listOfFiles.selectionModel.selectedItem.lowercase().endsWith(".md")) {
                var textFile = ""
                val currentFile = File(filePath.text)
                val textScanner = Scanner(currentFile)
                while (textScanner.hasNextLine()) {
                    textFile += textScanner.nextLine()
                }
                val text = Text(textFile)
                text.wrappingWidth = 510.0
                text.isFocusTraversable = false

                val scrollPane = ScrollPane()
                scrollPane.content = text
                scrollPane.isFocusTraversable = false
                scrollPane.isFitToHeight = true
                scrollPane.isFitToWidth = true
                layout.center = scrollPane
                textScanner.close()
            } else {
                layout.center = blank
            }
        }
    }

    private fun disableFourButtonsAndMenuItems(nextButton: Button, viewNext: MenuItem,
                                               renameButton: Button, actionsRename: MenuItem,
                                               moveButton: Button, actionsMove: MenuItem,
                                               deleteButton: Button, actionsDelete: MenuItem) {
        nextButton.isDisable = true
        viewNext.isDisable = true
        renameButton.isDisable = true
        actionsRename.isDisable = true
        moveButton.isDisable = true
        actionsMove.isDisable = true
        deleteButton.isDisable = true
        actionsDelete.isDisable = true
    }

    private fun resetToCurrentDirectory(filePath: Label, currentDir: File,
                                        listOfFiles: ListView<String>, showHiddenFiles: Boolean,
                                        layout: BorderPane, blank: TextArea) {
        filePath.text = currentDir.absolutePath
        if (currentDir.absolutePath != "/") filePath.text += "/"
        listOfFiles.items.clear()
        if (currentDir.listFiles() != null) {
            for (fileEntry in currentDir.listFiles()) {
                if (fileEntry.name[0] != '.' ||
                    (fileEntry.name[0] == '.' && showHiddenFiles)
                ) listOfFiles.items.add(fileEntry.name)
            }
        }
        layout.center = blank
    }

}