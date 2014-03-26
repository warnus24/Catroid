: Batch script for deleting not needed files/directories for the LiveWallpaper
:
: %~dp0  - Path to directory in which the script was started
: ATTRIB - change atributes of files/directories
: +/-h   - Add/Remove the hidden atribute to/from files
: @echo  - print output message
: functions
: - deleteAllFiles - deletes all files in a directory
: - deleteFolderAndSubfolders - Takes a String with the path to the directory
:                               which should be deleted and deletes this directory
:                               and everything in it.
: The Script works so, that you need to "whitelist" the files which shouldn't be
: deleted from a certain accessed folder and then deleting every other file in 
: that directory. The whitelisting works in that way, that you need to first
: hide the files you don't want to be deleted, then delete everything in this 
: directory with the function 'deleteAllFiles' and then unhide the whitelisted 
: files again.
: For deleting a whole directory with all contents in it simply call the function
: 'deleteFolderAndSubfolder'
:
: Script needs to be held up to date by adding files which shouldn't be deleted
: and by deleting not needed directories
 
:   +------------------+
:   | SOURCE DIRECTORY |
:   +------------------+
@echo Deleting unnecessary files
@echo Please wait... deleting takes a few seconds...
@echo off
@echo """""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
@echo "+---------------------------------------------------------+"
@echo "| SOURCE DIRECTORY                                        |"
@echo "+---------------------------------------------------------+"
:------------------------------
:org.catrobat.catroid.bluetooth
:------------------------------
cd "%~dp0\src\org\catrobat\catroid\bluetooth"
ATTRIB +h "BtCommunicator.java" /s
ATTRIB +h "BTConnectable.java" /s
ATTRIB +h "BluetoothManager.java" /s
ATTRIB +h "DeviceListActivitiy.java" /s
call:deleteAllFiles
ATTRIB -h "BtCommunicator.java" /s
ATTRIB -h "BTConnectable.java" /s
ATTRIB -h "BluetoothManager.java" /s
ATTRIB -h "DeviceListActivitiy.java" /s
@echo "| bluetooth                                  |    DONE    |"
@echo "+--------------------------------------------+------------+"

:---------------------------
:org.catrobat.catroid.common
:---------------------------
cd "%~dp0\src\org\catrobat\catroid\common"
ATTRIB +h "BrickValues.java" /s
ATTRIB +h "BroadcastWaitSequenceMap.java" /s
ATTRIB +h "BroadcastSequenceMap.java" /s
ATTRIB +h "Constants.java" /s
ATTRIB +h "FileChecksumContainer.java" /s
ATTRIB +h "LookData.java" /s
ATTRIB +h "MessageContainer.java" /s
ATTRIB +h "ProjectData.java" /s
ATTRIB +h "ScreenValues.java" /s
ATTRIB +h "ScreenModes.java" /s
ATTRIB +h "SoundInfo.java" /s
ATTRIB +h "StandardProjectHandler.java" /s
call:deleteAllFiles
ATTRIB -h "BroadcastWaitSequenceMap.java" /s
ATTRIB -h "BroadcastSequenceMap.java" /s
ATTRIB -h "BrickValues.java" /s
ATTRIB -h "Constants.java" /s
ATTRIB -h "FileChecksumContainer.java" /s
ATTRIB -h "LookData.java" /s
ATTRIB -h "MessageContainer.java" /s
ATTRIB -h "ProjectData.java" /s
ATTRIB -h "ScreenValues.java" /s
ATTRIB -h "ScreenModes.java" /s
ATTRIB -h "SoundInfo.java" /s
ATTRIB -h "StandardProjectHandler.java" /s
@echo "| common                                     |    DONE    |"
@echo "+--------------------------------------------+------------+"

:----------------------------
:org.catrobat.catroid.content
:----------------------------
cd "%~dp0\src\org\catrobat\catroid\content"
ATTRIB +h "BroadcastEvent.java" /s
ATTRIB +h "BroadcastHandler.java" /s
ATTRIB +h "BroadcastListener.java" /s
ATTRIB +h "BroadcastMessage.java" /s
ATTRIB +h "BroadcastScript.java" /s
ATTRIB +h "Look.java" /s
ATTRIB +h "Project.java" /s
ATTRIB +h "Script.java" /s
ATTRIB +h "Sprite.java" /s
ATTRIB +h "StartScript.java" /s
ATTRIB +h "WhenScript.java" /s
ATTRIB +h "XmlHeader.java" /s
call:deleteAllFiles
ATTRIB -h "BroadcastEvent.java" /s
ATTRIB -h "BroadcastHandler.java" /s
ATTRIB -h "BroadcastListener.java" /s
ATTRIB -h "BroadcastMessage.java" /s
ATTRIB -h "BroadcastScript.java" /s
ATTRIB -h "Look.java" /s
ATTRIB -h "Project.java" /s
ATTRIB -h "Script.java" /s
ATTRIB -h "Sprite.java" /s
ATTRIB -h "StartScript.java" /s
ATTRIB -h "WhenScript.java" /s
ATTRIB -h "XmlHeader.java" /s
@echo "| content                                    |    DONE    |"
@echo "+--------------------------------------------+------------+"

:------------------------------------
:org.catrobat.catroid.content.actions - nothing should be deleted
:------------------------------------

:------------------------------------
:org.catrobat.catroid.content.bricks - nothing should be deleted
:------------------------------------

:----------------------------------
:org.catrobat.catroid.formulaeditor
:----------------------------------
cd "%~dp0\src\org\catrobat\catroid\formulaeditor"
ATTRIB +h "Formula.java" /s
ATTRIB +h "FormulaElement.java" /s
ATTRIB +h "Functions.java" /s
ATTRIB +h "InternFormulaParser.java" /s
ATTRIB +h "InternFormulaUtils.java" /s
ATTRIB +h "InternToken.java" /s
ATTRIB +h "InternTokenType.java" /s
ATTRIB +h "Operators.java" /s
ATTRIB +h "SensorCustomEvent.java" /s
ATTRIB +h "SensorCustomEventListener.java" /s
ATTRIB +h "SensorHandler.java" /s
ATTRIB +h "SensorLoudness.java" /s
ATTRIB +h "SensorManager.java" /s
ATTRIB +h "SensorManagerInterface.java" /s
ATTRIB +h "Sensors.java" /s
ATTRIB +h "UserVariable.java" /s
ATTRIB +h "UserVariablesContainer.java" /s
call:deleteAllFiles
ATTRIB -h "Formula.java" /s
ATTRIB -h "FormulaElement.java" /s
ATTRIB -h "Functions.java" /s
ATTRIB -h "InternFormulaParser.java" /s
ATTRIB -h "InternFormulaUtils.java" /s
ATTRIB -h "InternToken.java" /s
ATTRIB -h "InternTokenType.java" /s
ATTRIB -h "Operators.java" /s
ATTRIB -h "SensorCustomEvent.java" /s
ATTRIB -h "SensorCustomEventListener.java" /s
ATTRIB -h "SensorHandler.java" /s
ATTRIB -h "SensorLoudness.java" /s
ATTRIB -h "SensorManager.java" /s
ATTRIB -h "SensorManagerInterface.java" /s
ATTRIB -h "Sensors.java" /s
ATTRIB -h "UserVariable.java" /s
ATTRIB -h "UserVariablesContainer.java" /s
@echo "| formulaeditor                              |    DONE    |"
@echo "+--------------------------------------------+------------+"


:-----------------------
:org.catrobat.catroid.io
:-----------------------
cd "%~dp0\src\org\catrobat\catroid\io"
ATTRIB +h "CatroidFieldKeySorter.java" /s
ATTRIB +h "LoadProjectTask.java" /s
ATTRIB +h "ProjectScreenshotLoader.java" /s
ATTRIB +h "SoundManager.java" /s
ATTRIB +h "StorageHandler.java" /s
call:deleteAllFiles
ATTRIB -h "CatroidFieldKeySorter.java" /s
ATTRIB -h "LoadProjectTask.java" /s
ATTRIB -h "ProjectScreenshotLoader.java" /s
ATTRIB -h "SoundManager.java" /s
ATTRIB -h "StorageHandler.java" /s
@echo "| io                                         |    DONE    |"
@echo "+--------------------------------------------+------------+"


:----------------------------------
:org.catrobat.catroid.soundrecorder
:----------------------------------
cd "%~dp0\src\org\catrobat\catroid\soundrecorder"
ATTRIB +h "SoundRecorder.java" /s
call:deleteAllFiles
ATTRIB -h "SoundRecorder.java" /s
@echo "| soundrecorder                              |    DONE    |"
@echo "+--------------------------------------------+------------+"


:--------------------------
:org.catrobat.catroid.stage
:--------------------------
cd "%~dp0\src\org\catrobat\catroid\stage"
ATTRIB +h "OnUtteranceCompletedListenerContainer.java" /s
ATTRIB +h "OrthoCamController.java" /s
ATTRIB +h "PreStageActivity.java" /s
ATTRIB +h "StageListener.java" /s
call:deleteAllFiles
ATTRIB -h "OnUtteranceCompletedListenerContainer.java" /s
ATTRIB -h "OrthoCamController.java" /s
ATTRIB -h "PreStageActivity.java" /s
ATTRIB -h "StageListener.java" /s
@echo "| stage                                      |    DONE    |"
@echo "+--------------------------------------------+------------+"


:------------------------------
:org.catrobat.catroid.transfers
:------------------------------
cd "%~dp0\src\org\catrobat\catroid\transfers"
ATTRIB +h "CheckTokenTask.java" /s
ATTRIB +h "ProjectDownloadService.java" /s
call:deleteAllFiles
ATTRIB -h "CheckTokenTask.java" /s
ATTRIB -h "ProjectDownloadService.java" /s
@echo "| transfers                                  |    DONE    |"
@echo "+--------------------------------------------+------------+"


:-----------------------
:org.catrobat.catroid.ui
:-----------------------
cd "%~dp0\src\org\catrobat\catroid\ui"
ATTRIB +h "CapitalizedTextView.java" /s
ATTRIB +h "SoundViewHolder.java" /s
call:deleteAllFiles
ATTRIB -h "CapitalizedTextView.java" /s
ATTRIB -h "SoundViewHolder.java" /s
@echo "| ui                                         |    DONE    |"
@echo "+--------------------------------------------+------------+"

:-------------------------------
:org.catrobat.catroid.ui.adapter
:-------------------------------
cd "%~dp0\src\org\catrobat\catroid\ui\adapter"
ATTRIB +h "ProjectAdapter.java" /s
call:deleteAllFiles
ATTRIB -h "ProjectAdapter.java" /s
@echo "| ui-adapter                                 |    DONE    |"
@echo "+--------------------------------------------+------------+"


:----------------------------------
:org.catrobat.catroid.ui.controller
:----------------------------------
cd "%~dp0\src\org\catrobat\catroid\ui\controller"
ATTRIB +h "BackPackListManager.java" /s
call:deleteAllFiles
ATTRIB -h "BackPackListManager.java" /s
@echo "| ui-controller                              |    DONE    |"
@echo "+--------------------------------------------+------------+"


:-------------------------------
:org.catrobat.catroid.ui.dialogs
:-------------------------------
cd "%~dp0\src\org\catrobat\catroid\ui\dialogs"
ATTRIB +h "AboutDialogFragment.java" /s
ATTRIB +h "CustomAlertDialogBuilder.java" /s
ATTRIB +h "OverwriteRenameDialog.java" /s
ATTRIB +h "RenameProjectDialog.java" /s
ATTRIB +h "TextDialog.java" /s
call:deleteAllFiles
ATTRIB -h "AboutDialogFragment.java" /s
ATTRIB -h "CustomAlertDialogBuilder.java" /s
ATTRIB -h "OverwriteRenameDialog.java" /s
ATTRIB -h "RenameProjectDialog.java" /s
ATTRIB -h "TextDialog.java" /s
@echo "| ui-dialogs                                 |    DONE    |"
@echo "+--------------------------------------------+------------+"



:--------------------------
:org.catrobat.catroid.utils
:--------------------------
cd "%~dp0\src\org\catrobat\catroid\utils"
ATTRIB +h "CopyProjectTask.java" /s
ATTRIB +h "DownloadUtil.java" /s
ATTRIB +h "ImageEditing.java" /s
ATTRIB +h "NotificationData.java" /s
ATTRIB +h "UtilCamera.java" /s
ATTRIB +h "UtilDeviceInfo.java" /s
ATTRIB +h "UtilFile.java" /s
ATTRIB +h "Utils.java" /s
ATTRIB +h "UtilZip.java" /s
call:deleteAllFiles
ATTRIB -h "CopyProjectTask.java" /s
ATTRIB -h "DownloadUtil.java" /s
ATTRIB -h "ImageEditing.java" /s
ATTRIB -h "NotificationData.java" /s
ATTRIB -h "UtilCamera.java" /s
ATTRIB -h "UtilDeviceInfo.java" /s
ATTRIB -h "UtilFile.java" /s
ATTRIB -h "Utils.java" /s
ATTRIB -h "UtilZip.java" /s
@echo "| utils                                      |    DONE    |"
@echo "+--------------------------------------------+------------+"


:------------------------
:org.catrobat.catroid.web
:------------------------
cd "%~dp0\src\org\catrobat\catroid\web"
ATTRIB +h "ConnectionWrapper.java" /s
ATTRIB +h "ProgressBufferedOutputStream.java" /s
ATTRIB +h "ServerCalls.java" /s
ATTRIB +h "WebconnectionException.java" /s
call:deleteAllFiles
ATTRIB -h "ConnectionWrapper.java" /s
ATTRIB -h "ProgressBufferedOutputStream.java" /s
ATTRIB -h "ServerCalls.java" /s
ATTRIB -h "WebconnectionException.java" /s
@echo "| web                                        |    DONE    |"
@echo "+--------------------------------------------+------------+"
@echo """""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

:--------------------
:org.catrobat.catroid
:--------------------
cd "%~dp0\src\org\catrobat\catroid
ATTRIB +h "ProjectManager.java" /s
call:deleteAllFiles
ATTRIB -h "ProjectManager.java" /s

:-----------------------------
:Delete not needed directories
:-----------------------------
call:deleteFolderAndSubfolder "%~dp0\src\org\catrobat\catroid\legonxt"
call:deleteFolderAndSubfolder "%~dp0\src\org\catrobat\catroid\ui\dragndrop"
call:deleteFolderAndSubfolder "%~dp0\src\org\catrobat\catroid\ui\fragment"

:   +-------------------+
:   |   RES DIRECTORY   |
:   +-------------------+
@echo.

@echo """""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
@echo "+---------------------------------------------------------+"
@echo "| RES DIRECTORY                                           |"
@echo "+---------------------------------------------------------+"
:--------
:res.anim
:--------
cd "%~dp0\res\anim"
ATTRIB +h "blink.xml" /s
ATTRIB +h "slide_down.xml" /s
ATTRIB +h "slide_in.xml" /s
ATTRIB +h "slide_out.xml" /s
ATTRIB +h "slide_up.xml" /s
call:deleteAllFiles
ATTRIB -h "blink.xml" /s
ATTRIB -h "slide_down.xml" /s
ATTRIB -h "slide_in.xml" /s
ATTRIB -h "slide_out.xml" /s
ATTRIB -h "slide_up.xml" /s
@echo "| anim                                       |    DONE    |"
@echo "+--------------------------------------------+------------+"

:------------
:res.drawable
:------------
cd "%~dp0\res\drawable"
ATTRIB +h "bottom_bar_background.xml" /s
ATTRIB +h "bottom_bar_background_pressed.xml" /s
ATTRIB +h "bottom_bar_background_selector.xml" /s
ATTRIB +h "button_background.xml" /s
ATTRIB +h "button_background_pressed.xml" /s
ATTRIB +h "button_background_selector.xml" /s
ATTRIB +h "button_background_shadowed.xml" /s
ATTRIB +h "formula_editor_keyboard_button.xml" /s
ATTRIB +h "image_contour.xml" /s
ATTRIB +h "my_projects_activity_list_item_thumbnail_border.xml" /s
ATTRIB +h "spritelist_item_thumbnail_border.xml" /s
ATTRIB +h "stage_dialog_button_back_selector.xml" /s
ATTRIB +h "stage_dialog_button_continue_selector.xml" /s
ATTRIB +h "stage_dialog_button_restart_selector.xml" /s
ATTRIB +h "stage_dialog_button_screenshot_selector.xml" /s
ATTRIB +h "stage_dialog_button_stretch_selector.xml" /s
ATTRIB +h "stage_dialog_button_toggle_axis_selector.xml" /s
call:deleteAllFiles
ATTRIB -h "bottom_bar_background.xml" /s
ATTRIB -h "bottom_bar_background_pressed.xml" /s
ATTRIB -h "bottom_bar_background_selector.xml" /s
ATTRIB -h "button_background.xml" /s
ATTRIB -h "button_background_pressed.xml" /s
ATTRIB -h "button_background_selector.xml" /s
ATTRIB -h "button_background_shadowed.xml" /s
ATTRIB -h "formula_editor_keyboard_button.xml" /s
ATTRIB -h "image_contour.xml" /s
ATTRIB -h "my_projects_activity_list_item_thumbnail_border.xml" /s
ATTRIB -h "spritelist_item_thumbnail_border.xml" /s
ATTRIB -h "stage_dialog_button_back_selector.xml" /s
ATTRIB -h "stage_dialog_button_continue_selector.xml" /s
ATTRIB -h "stage_dialog_button_restart_selector.xml" /s
ATTRIB -h "stage_dialog_button_screenshot_selector.xml" /s
ATTRIB -h "stage_dialog_button_stretch_selector.xml" /s
ATTRIB -h "stage_dialog_button_toggle_axis_selector.xml" /s
@echo "| drawable                                   |    DONE    |"
@echo "+--------------------------------------------+------------+"

:-----------------
:res.drawable-hdpi
:-----------------
cd "%~dp0\res\drawable-hdpi"
ATTRIB +h "actionbar_background.png" /s
ATTRIB +h "bottombar_separator.png" /s
ATTRIB +h "ic_actionbar_back.png" /s
ATTRIB +h "ic_arrow_right.png" /s
ATTRIB +h "ic_launcher.png" /s
ATTRIB +h "ic_main_menu_community.png" /s
ATTRIB +h "ic_main_menu_continue.png" /s
ATTRIB +h "ic_main_menu_help.png" /s
ATTRIB +h "ic_main_menu_new.png" /s
ATTRIB +h "ic_main_menu_programs.png" /s
ATTRIB +h "ic_main_menu_upload.png" /s
ATTRIB +h "ic_media_play.png" /s
ATTRIB +h "ic_media_stop.png" /s
ATTRIB +h "ic_menu_delete.png" /s
ATTRIB +h "ic_microphone.png" /s
ATTRIB +h "ic_microphone_active.png" /s
ATTRIB +h "ic_play.png" /s
ATTRIB +h "ic_plus.png" /s
ATTRIB +h "icon_backspace.png" /s
ATTRIB +h "icon_backspace_disabled.png" /s
ATTRIB +h "icon_redo.png" /s
ATTRIB +h "icon_redo_disabled.png" /s
ATTRIB +h "icon_undo.png" /s
ATTRIB +h "icon_undo_disabled.png" /s
ATTRIB +h "stage_dialog_background_middle.png" /s
ATTRIB +h "stage_dialog_background_side.png" /s
ATTRIB +h "stage_dialog_button_back.png" /s
ATTRIB +h "stage_dialog_button_back_pressed.png" /s
ATTRIB +h "stage_dialog_button_continue.png" /s
ATTRIB +h "stage_dialog_button_continue_pressed.png" /s
ATTRIB +h "stage_dialog_button_restart.png" /s
ATTRIB +h "stage_dialog_button_restart_pressed.png" /s
ATTRIB +h "stage_dialog_button_screenshot.png" /s
ATTRIB +h "stage_dialog_button_screenshot_pressed.png" /s
ATTRIB +h "stage_dialog_button_stretch.png" /s
ATTRIB +h "stage_dialog_button_stretch_pressed.png" /s
ATTRIB +h "stage_dialog_button_toggle_axis.png" /s
ATTRIB +h "stage_dialog_button_toggle_axis_pressed.png" /s
ATTRIB +h "textfield_pressed.9.png" /s
call:deleteAllFiles
ATTRIB -h "actionbar_background.png" /s
ATTRIB -h "bottombar_separator.png" /s
ATTRIB -h "ic_actionbar_back.png" /s
ATTRIB -h "ic_arrow_right.png" /s
ATTRIB -h "ic_launcher.png" /s
ATTRIB -h "ic_main_menu_community.png" /s
ATTRIB -h "ic_main_menu_continue.png" /s
ATTRIB -h "ic_main_menu_help.png" /s
ATTRIB -h "ic_main_menu_new.png" /s
ATTRIB -h "ic_main_menu_programs.png" /s
ATTRIB -h "ic_main_menu_upload.png" /s
ATTRIB -h "ic_media_play.png" /s
ATTRIB -h "ic_media_stop.png" /s
ATTRIB -h "ic_menu_delete.png" /s
ATTRIB -h "ic_microphone.png" /s
ATTRIB -h "ic_microphone_active.png" /s
ATTRIB -h "ic_play.png" /s
ATTRIB -h "ic_plus.png" /s
ATTRIB -h "icon_backspace.png" /s
ATTRIB -h "icon_backspace_disabled.png" /s
ATTRIB -h "icon_redo.png" /s
ATTRIB -h "icon_redo_disabled.png" /s
ATTRIB -h "icon_undo.png" /s
ATTRIB -h "icon_undo_disabled.png" /s
ATTRIB -h "stage_dialog_background_middle.png" /s
ATTRIB -h "stage_dialog_background_side.png" /s
ATTRIB -h "stage_dialog_button_back.png" /s
ATTRIB -h "stage_dialog_button_back_pressed.png" /s
ATTRIB -h "stage_dialog_button_continue.png" /s
ATTRIB -h "stage_dialog_button_continue_pressed.png" /s
ATTRIB -h "stage_dialog_button_restart.png" /s
ATTRIB -h "stage_dialog_button_restart_pressed.png" /s
ATTRIB -h "stage_dialog_button_screenshot.png" /s
ATTRIB -h "stage_dialog_button_screenshot_pressed.png" /s
ATTRIB -h "stage_dialog_button_stretch.png" /s
ATTRIB -h "stage_dialog_button_stretch_pressed.png" /s
ATTRIB -h "stage_dialog_button_toggle_axis.png" /s
ATTRIB -h "stage_dialog_button_toggle_axis_pressed.png" /s
ATTRIB -h "textfield_pressed.9.png" /s
@echo "| drawable-hdpi                              |    DONE    |"
@echo "+--------------------------------------------+------------+"

:-----------------
:res.drawable-ldpi
:-----------------
cd "%~dp0\res\drawable-ldpi"
ATTRIB +h "brick_blue_1h.9.png" /s
ATTRIB +h "brick_blue_2h.9.png" /s
ATTRIB +h "brick_blue_3h.9.png" /s
ATTRIB +h "brick_control_1h.9.png" /s
ATTRIB +h "brick_control_2h.9.png" /s
ATTRIB +h "brick_cyan_1h.9.png" /s
ATTRIB +h "brick_cyan_2h.9.png" /s
ATTRIB +h "brick_cyan_3h.9.png" /s
ATTRIB +h "brick_green_1h.9.png" /s
ATTRIB +h "brick_green_2h.9.png" /s
ATTRIB +h "brick_green_3h.9.png" /s
ATTRIB +h "brick_orange_1h.9.png" /s
ATTRIB +h "brick_orange_1h_no_plug.9.png" /s
ATTRIB +h "brick_orange_1h_no_plug_slot.9.png" /s
ATTRIB +h "brick_orange_2h.9.png" /s
ATTRIB +h "brick_orange_3h.9.png" /s
ATTRIB +h "brick_red_1h.9.png" /s
ATTRIB +h "brick_red_2h.9.png" /s
ATTRIB +h "brick_red_3h.9.png" /s
ATTRIB +h "brick_violet_1h.9.png" /s
ATTRIB +h "brick_violet_2h.9.png" /s
ATTRIB +h "brick_violet_3h.9.png" /s
ATTRIB +h "ic_launcher.png" /s
call:deleteAllFiles
ATTRIB -h "brick_blue_1h.9.png" /s
ATTRIB -h "brick_blue_2h.9.png" /s
ATTRIB -h "brick_blue_3h.9.png" /s
ATTRIB -h "brick_control_1h.9.png" /s
ATTRIB -h "brick_control_2h.9.png" /s
ATTRIB -h "brick_cyan_1h.9.png" /s
ATTRIB -h "brick_cyan_2h.9.png" /s
ATTRIB -h "brick_cyan_3h.9.png" /s
ATTRIB -h "brick_green_1h.9.png" /s
ATTRIB -h "brick_green_2h.9.png" /s
ATTRIB -h "brick_green_3h.9.png" /s
ATTRIB -h "brick_orange_1h.9.png" /s
ATTRIB -h "brick_orange_1h_no_plug.9.png" /s
ATTRIB -h "brick_orange_1h_no_plug_slot.9.png" /s
ATTRIB -h "brick_orange_2h.9.png" /s
ATTRIB -h "brick_orange_3h.9.png" /s
ATTRIB -h "brick_red_1h.9.png" /s
ATTRIB -h "brick_red_2h.9.png" /s
ATTRIB -h "brick_red_3h.9.png" /s
ATTRIB -h "brick_violet_1h.9.png" /s
ATTRIB -h "brick_violet_2h.9.png" /s
ATTRIB -h "brick_violet_3h.9.png" /s
ATTRIB -h "ic_launcher.png" /s
@echo "| drawable-ldpi                              |    DONE    |"
@echo "+--------------------------------------------+------------+"

:-----------------
:res.drawable-mdpi
:-----------------
cd "%~dp0\res\drawable-mdpi"
ATTRIB +h "actionbar_background.png" /s
ATTRIB +h "bottombar_separator.png" /s
ATTRIB +h "brick_blue_1h.9.png" /s
ATTRIB +h "brick_blue_2h.9.png" /s
ATTRIB +h "brick_blue_3h.9.png" /s
ATTRIB +h "brick_control_1h.9.png" /s
ATTRIB +h "brick_control_2h.9.png" /s
ATTRIB +h "brick_cyan_1h.9.png" /s
ATTRIB +h "brick_cyan_2h.9.png" /s
ATTRIB +h "brick_cyan_3h.9.png" /s
ATTRIB +h "brick_green_1h.9.png" /s
ATTRIB +h "brick_green_2h.9.png" /s
ATTRIB +h "brick_green_3h.9.png" /s
ATTRIB +h "brick_orange_1h.9.png" /s
ATTRIB +h "brick_orange_1h_no_plug.9.png" /s
ATTRIB +h "brick_orange_1h_no_plug_slot.9.png" /s
ATTRIB +h "brick_orange_2h.9.png" /s
ATTRIB +h "brick_orange_3h.9.png" /s
ATTRIB +h "brick_red_1h.9.png" /s
ATTRIB +h "brick_red_2h.9.png" /s
ATTRIB +h "brick_red_3h.9.png" /s
ATTRIB +h "brick_violet_1h.9.png" /s
ATTRIB +h "brick_violet_2h.9.png" /s
ATTRIB +h "brick_violet_3h.9.png" /s
ATTRIB +h "formula_editor_button.9.png" /s
ATTRIB +h "formula_editor_button_pressed.9.png" /s
ATTRIB +h "ic_arrow_right.png" /s
ATTRIB +h "ic_launcher.png" /s
ATTRIB +h "ic_main_menu_community.png" /s
ATTRIB +h "ic_main_menu_continue.png" /s
ATTRIB +h "ic_main_menu_help.png" /s
ATTRIB +h "ic_main_menu_new.png" /s
ATTRIB +h "ic_main_menu_programs.png" /s
ATTRIB +h "ic_main_menu_upload.png" /s
ATTRIB +h "ic_media_play.png" /s
ATTRIB +h "ic_media_stop.png" /s
ATTRIB +h "ic_microphone.png" /s
ATTRIB +h "ic_microphone_active.png" /s
ATTRIB +h "ic_play.png" /s
ATTRIB +h "ic_plus.png" /s
ATTRIB +h "icon_backspace.png" /s
ATTRIB +h "icon_backspace_disabled.png" /s
ATTRIB +h "icon_redo.png" /s
ATTRIB +h "icon_redo_disabled.png" /s
ATTRIB +h "icon_undo.png" /s
ATTRIB +h "icon_undo_disabled.png" /s
ATTRIB +h "stage_dialog_background_middle.png" /s
ATTRIB +h "stage_dialog_background_side.png" /s
ATTRIB +h "stage_dialog_button_back.png" /s
ATTRIB +h "stage_dialog_button_back_pressed.png" /s
ATTRIB +h "stage_dialog_button_continue.png" /s
ATTRIB +h "stage_dialog_button_continue_pressed.png" /s
ATTRIB +h "stage_dialog_button_restart.png" /s
ATTRIB +h "stage_dialog_button_restart_pressed.png" /s
ATTRIB +h "stage_dialog_button_screenshot.png" /s
ATTRIB +h "stage_dialog_button_screenshot_pressed.png" /s
ATTRIB +h "stage_dialog_button_stretch.png" /s
ATTRIB +h "stage_dialog_button_stretch_pressed.png" /s
ATTRIB +h "stage_dialog_button_toggle_axis.png" /s
ATTRIB +h "stage_dialog_button_toggle_axis_pressed.png" /s
ATTRIB +h "textfield_pressed_android4.9.png" /s
call:deleteAllFiles
ATTRIB -h "actionbar_background.png" /s
ATTRIB -h "bottombar_separator.png" /s
ATTRIB -h "brick_blue_1h.9.png" /s
ATTRIB -h "brick_blue_2h.9.png" /s
ATTRIB -h "brick_blue_3h.9.png" /s
ATTRIB -h "brick_control_1h.9.png" /s
ATTRIB -h "brick_control_2h.9.png" /s
ATTRIB -h "brick_cyan_1h.9.png" /s
ATTRIB -h "brick_cyan_2h.9.png" /s
ATTRIB -h "brick_cyan_3h.9.png" /s
ATTRIB -h "brick_green_1h.9.png" /s
ATTRIB -h "brick_green_2h.9.png" /s
ATTRIB -h "brick_green_3h.9.png" /s
ATTRIB -h "brick_orange_1h.9.png" /s
ATTRIB -h "brick_orange_1h_no_plug.9.png" /s
ATTRIB -h "brick_orange_1h_no_plug_slot.9.png" /s
ATTRIB -h "brick_orange_2h.9.png" /s
ATTRIB -h "brick_orange_3h.9.png" /s
ATTRIB -h "brick_red_1h.9.png" /s
ATTRIB -h "brick_red_2h.9.png" /s
ATTRIB -h "brick_red_3h.9.png" /s
ATTRIB -h "brick_violet_1h.9.png" /s
ATTRIB -h "brick_violet_2h.9.png" /s
ATTRIB -h "brick_violet_3h.9.png" /s
ATTRIB -h "formula_editor_button.9.png" /s
ATTRIB -h "formula_editor_button_pressed.9.png" /s
ATTRIB -h "ic_arrow_right.png" /s
ATTRIB -h "ic_launcher.png" /s
ATTRIB -h "ic_main_menu_community.png" /s
ATTRIB -h "ic_main_menu_continue.png" /s
ATTRIB -h "ic_main_menu_help.png" /s
ATTRIB -h "ic_main_menu_new.png" /s
ATTRIB -h "ic_main_menu_programs.png" /s
ATTRIB -h "ic_main_menu_upload.png" /s
ATTRIB -h "ic_media_play.png" /s
ATTRIB -h "ic_media_stop.png" /s
ATTRIB -h "ic_microphone.png" /s
ATTRIB -h "ic_microphone_active.png" /s
ATTRIB -h "ic_play.png" /s
ATTRIB -h "ic_plus.png" /s
ATTRIB -h "icon_backspace.png" /s
ATTRIB -h "icon_backspace_disabled.png" /s
ATTRIB -h "icon_redo.png" /s
ATTRIB -h "icon_redo_disabled.png" /s
ATTRIB -h "icon_undo.png" /s
ATTRIB -h "icon_undo_disabled.png" /s
ATTRIB -h "stage_dialog_background_middle.png" /s
ATTRIB -h "stage_dialog_background_side.png" /s
ATTRIB -h "stage_dialog_button_back.png" /s
ATTRIB -h "stage_dialog_button_back_pressed.png" /s
ATTRIB -h "stage_dialog_button_continue.png" /s
ATTRIB -h "stage_dialog_button_continue_pressed.png" /s
ATTRIB -h "stage_dialog_button_restart.png" /s
ATTRIB -h "stage_dialog_button_restart_pressed.png" /s
ATTRIB -h "stage_dialog_button_screenshot.png" /s
ATTRIB -h "stage_dialog_button_screenshot_pressed.png" /s
ATTRIB -h "stage_dialog_button_stretch.png" /s
ATTRIB -h "stage_dialog_button_stretch_pressed.png" /s
ATTRIB -h "stage_dialog_button_toggle_axis.png" /s
ATTRIB -h "stage_dialog_button_toggle_axis_pressed.png" /s
ATTRIB -h "textfield_pressed_android4.9.png" /s
@echo "| drawable-mdpi                              |    DONE    |"
@echo "+--------------------------------------------+------------+"

:------------------
:res.drawable-nodpi
:------------------
cd "%~dp0\res\drawable-nodpi"
ATTRIB +h "default_project_background.png" /s
ATTRIB +h "default_project_mole_digged_out.png" /s
ATTRIB +h "default_project_mole_moving.png" /s
ATTRIB +h "default_project_mole_whacked.png" /s
ATTRIB +h "default_project_screenshot.png" /s
call:deleteAllFiles
ATTRIB -h "default_project_background.png" /s
ATTRIB -h "default_project_mole_digged_out.png" /s
ATTRIB -h "default_project_mole_moving.png" /s
ATTRIB -h "default_project_mole_whacked.png" /s
ATTRIB -h "default_project_screenshot.png" /s
@echo "| drawable-nodpi                             |    DONE    |"
@echo "+--------------------------------------------+------------+"

:------------------
:res.drawable-xhdpi
:------------------
cd "%~dp0\res\drawable-xhdpi"
ATTRIB +h "actionbar_background.png" /s
ATTRIB +h "bottombar_separator.png" /s
ATTRIB +h "brick_blue_1h.9.png" /s
ATTRIB +h "brick_blue_2h.9.png" /s
ATTRIB +h "brick_blue_3h.9.png" /s
ATTRIB +h "brick_control_1h.9.png" /s
ATTRIB +h "brick_control_2h.9.png" /s
ATTRIB +h "brick_cyan_1h.9.png" /s
ATTRIB +h "brick_cyan_2h.9.png" /s
ATTRIB +h "brick_cyan_3h.9.png" /s
ATTRIB +h "brick_green_1h.9.png" /s
ATTRIB +h "brick_green_2h.9.png" /s
ATTRIB +h "brick_green_3h.9.png" /s
ATTRIB +h "brick_orange_1h.9.png" /s
ATTRIB +h "brick_orange_1h_no_plug.9.png" /s
ATTRIB +h "brick_orange_1h_no_plug_slot.9.png" /s
ATTRIB +h "brick_orange_2h.9.png" /s
ATTRIB +h "brick_orange_3h.9.png" /s
ATTRIB +h "brick_red_1h.9.png" /s
ATTRIB +h "brick_red_2h.9.png" /s
ATTRIB +h "brick_red_3h.9.png" /s
ATTRIB +h "brick_selection_background_control.9.png" /s
ATTRIB +h "brick_selection_background_lego.9.png" /s
ATTRIB +h "brick_selection_background_looks.9.png" /s
ATTRIB +h "brick_selection_background_motion.9.png" /s
ATTRIB +h "brick_selection_background_sounds.9.png" /s
ATTRIB +h "brick_selection_background_variables.9.png" /s
ATTRIB +h "brick_violet_1h.9.png" /s
ATTRIB +h "brick_violet_2h.9.png" /s
ATTRIB +h "brick_violet_3h.9.png" /s
ATTRIB +h "ic_arrow_right.png" /s
ATTRIB +h "ic_launcher.png" /s
ATTRIB +h "ic_main_menu_community.png" /s
ATTRIB +h "ic_main_menu_continue.png" /s
ATTRIB +h "ic_main_menu_help.png" /s
ATTRIB +h "ic_main_menu_new.png" /s
ATTRIB +h "ic_main_menu_programs.png" /s
ATTRIB +h "ic_main_menu_upload.png" /s
ATTRIB +h "ic_media_play.png" /s
ATTRIB +h "ic_media_stop.png" /s
ATTRIB +h "ic_menu_delete.png" /s
ATTRIB +h "ic_microphone.png" /s
ATTRIB +h "ic_microphone_active.png" /s
ATTRIB +h "ic_play.png" /s
ATTRIB +h "ic_plus.png" /s
ATTRIB +h "ic_program_menu_looks.png" /s
ATTRIB +h "ic_program_menu_scripts.png" /s
ATTRIB +h "ic_program_menu_sounds.png" /s
ATTRIB +h "icon_backspace.png" /s
ATTRIB +h "icon_backspace_disabled.png" /s
ATTRIB +h "icon_redo.png" /s
ATTRIB +h "icon_redo_disabled.png" /s
ATTRIB +h "icon_undo.png" /s
ATTRIB +h "icon_undo_disabled.png" /s
ATTRIB +h "main_menu_button_arrow_control.png" /s
ATTRIB +h "main_menu_button_arrow_lego.png" /s
ATTRIB +h "main_menu_button_arrow_looks.png" /s
ATTRIB +h "main_menu_button_arrow_motion.png" /s
ATTRIB +h "main_menu_button_arrow_sounds.png" /s
ATTRIB +h "main_menu_button_arrow_variables.png" /s
ATTRIB +h "stage_dialog_background_middle.png" /s
ATTRIB +h "stage_dialog_background_side.png" /s
ATTRIB +h "stage_dialog_button_back.png" /s
ATTRIB +h "stage_dialog_button_back_pressed.png" /s
ATTRIB +h "stage_dialog_button_continue.png" /s
ATTRIB +h "stage_dialog_button_continue_pressed.png" /s
ATTRIB +h "stage_dialog_button_restart.png" /s
ATTRIB +h "stage_dialog_button_restart_pressed.png" /s
ATTRIB +h "stage_dialog_button_screenshot.png" /s
ATTRIB +h "stage_dialog_button_screenshot_pressed.png" /s
ATTRIB +h "stage_dialog_button_stretch.png" /s
ATTRIB +h "stage_dialog_button_stretch_pressed.png" /s
ATTRIB +h "stage_dialog_button_toggle_axis.png" /s
ATTRIB +h "stage_dialog_button_toggle_axis_pressed.png" /s
call:deleteAllFiles
ATTRIB -h "actionbar_background.png" /s
ATTRIB -h "bottombar_separator.png" /s
ATTRIB -h "brick_blue_1h.9.png" /s
ATTRIB -h "brick_blue_2h.9.png" /s
ATTRIB -h "brick_blue_3h.9.png" /s
ATTRIB -h "brick_control_1h.9.png" /s
ATTRIB -h "brick_control_2h.9.png" /s
ATTRIB -h "brick_cyan_1h.9.png" /s
ATTRIB -h "brick_cyan_2h.9.png" /s
ATTRIB -h "brick_cyan_3h.9.png" /s
ATTRIB -h "brick_green_1h.9.png" /s
ATTRIB -h "brick_green_2h.9.png" /s
ATTRIB -h "brick_green_3h.9.png" /s
ATTRIB -h "brick_orange_1h.9.png" /s
ATTRIB -h "brick_orange_1h_no_plug.9.png" /s
ATTRIB -h "brick_orange_1h_no_plug_slot.9.png" /s
ATTRIB -h "brick_orange_2h.9.png" /s
ATTRIB -h "brick_orange_3h.9.png" /s
ATTRIB -h "brick_red_1h.9.png" /s
ATTRIB -h "brick_red_2h.9.png" /s
ATTRIB -h "brick_red_3h.9.png" /s
ATTRIB -h "brick_selection_background_control.9.png" /s
ATTRIB -h "brick_selection_background_lego.9.png" /s
ATTRIB -h "brick_selection_background_looks.9.png" /s
ATTRIB -h "brick_selection_background_motion.9.png" /s
ATTRIB -h "brick_selection_background_sounds.9.png" /s
ATTRIB -h "brick_selection_background_variables.9.png" /s
ATTRIB -h "brick_violet_1h.9.png" /s
ATTRIB -h "brick_violet_2h.9.png" /s
ATTRIB -h "brick_violet_3h.9.png" /s
ATTRIB -h "ic_arrow_right.png" /s
ATTRIB -h "ic_launcher.png" /s
ATTRIB -h "ic_main_menu_community.png" /s
ATTRIB -h "ic_main_menu_continue.png" /s
ATTRIB -h "ic_main_menu_help.png" /s
ATTRIB -h "ic_main_menu_new.png" /s
ATTRIB -h "ic_main_menu_programs.png" /s
ATTRIB -h "ic_main_menu_upload.png" /s
ATTRIB -h "ic_media_play.png" /s
ATTRIB -h "ic_media_stop.png" /s
ATTRIB -h "ic_menu_delete.png" /s
ATTRIB -h "ic_microphone.png" /s
ATTRIB -h "ic_microphone_active.png" /s
ATTRIB -h "ic_play.png" /s
ATTRIB -h "ic_plus.png" /s
ATTRIB -h "ic_program_menu_looks.png" /s
ATTRIB -h "ic_program_menu_scripts.png" /s
ATTRIB -h "ic_program_menu_sounds.png" /s
ATTRIB -h "icon_backspace.png" /s
ATTRIB -h "icon_backspace_disabled.png" /s
ATTRIB -h "icon_redo.png" /s
ATTRIB -h "icon_redo_disabled.png" /s
ATTRIB -h "icon_undo.png" /s
ATTRIB -h "icon_undo_disabled.png" /s
ATTRIB -h "main_menu_button_arrow_control.png" /s
ATTRIB -h "main_menu_button_arrow_lego.png" /s
ATTRIB -h "main_menu_button_arrow_looks.png" /s
ATTRIB -h "main_menu_button_arrow_motion.png" /s
ATTRIB -h "main_menu_button_arrow_sounds.png" /s
ATTRIB -h "main_menu_button_arrow_variables.png" /s
ATTRIB -h "stage_dialog_background_middle.png" /s
ATTRIB -h "stage_dialog_background_side.png" /s
ATTRIB -h "stage_dialog_button_back.png" /s
ATTRIB -h "stage_dialog_button_back_pressed.png" /s
ATTRIB -h "stage_dialog_button_continue.png" /s
ATTRIB -h "stage_dialog_button_continue_pressed.png" /s
ATTRIB -h "stage_dialog_button_restart.png" /s
ATTRIB -h "stage_dialog_button_restart_pressed.png" /s
ATTRIB -h "stage_dialog_button_screenshot.png" /s
ATTRIB -h "stage_dialog_button_screenshot_pressed.png" /s
ATTRIB -h "stage_dialog_button_stretch.png" /s
ATTRIB -h "stage_dialog_button_stretch_pressed.png" /s
ATTRIB -h "stage_dialog_button_toggle_axis.png" /s
ATTRIB -h "stage_dialog_button_toggle_axis_pressed.png" /s
@echo "| drawable-xhdpi                             |    DONE    |"
@echo "+--------------------------------------------+------------+"

:----------
:res.layout
:----------
cd "%~dp0\res\layout"
ATTRIB +h "activity_main_menu.xml" /s
ATTRIB +h "activity_my_projects_list_item.xml" /s
ATTRIB +h "activity_my_projects_title_details_view.xml" /s
ATTRIB +h "activity_soundrecorder.xml" /s
ATTRIB +h "activity_webview.xml" /s
ATTRIB +h "bottom_bar.xml" /s
ATTRIB +h "dialog_about.xml" /s
ATTRIB +h "dialog_custom_alert_dialog.xml" /s
ATTRIB +h "dialog_lwp_about.xml" /s
ATTRIB +h "dialog_lwp_about_this_wallpaper.xml" /s
ATTRIB +h "dialog_overwrite_project.xml" /s
ATTRIB +h "dialog_text_dialog.xml" /s
ATTRIB +h "dialog_web_warning.xml" /s
ATTRIB +h "fragment_lwp_select_program.xml" /s
ATTRIB +h "fragment_sound_soundlist_item.xml" /s
ATTRIB +h "action_mode_select_all.xml" /s
ATTRIB +h "activity_my_projects.xml" /s

call:deleteAllFiles
ATTRIB -h "activity_main_menu.xml" /s
ATTRIB -h "activity_my_projects_list_item.xml" /s
ATTRIB -h "activity_my_projects_title_details_view.xml" /s
ATTRIB -h "activity_soundrecorder.xml" /s
ATTRIB -h "activity_webview.xml" /s
ATTRIB -h "bottom_bar.xml" /s
ATTRIB -h "dialog_about.xml" /s
ATTRIB -h "dialog_custom_alert_dialog.xml" /s
ATTRIB -h "dialog_lwp_about.xml" /s
ATTRIB -h "dialog_lwp_about_this_wallpaper.xml" /s
ATTRIB -h "dialog_overwrite_project.xml" /s
ATTRIB -h "dialog_text_dialog.xml" /s
ATTRIB -h "dialog_web_warning.xml" /s
ATTRIB -h "fragment_lwp_select_program.xml" /s
ATTRIB -h "fragment_sound_soundlist_item.xml" /s
ATTRIB -h "action_mode_select_all.xml" /s
ATTRIB -h "activity_my_projects.xml" /s
@echo "| layout                                     |    DONE    |"
@echo "+--------------------------------------------+------------+"

:-------
:res.raw
:-------
cd "%~dp0\res\raw"
ATTRIB +h "default_project_sound_mole_1.mp3" /s
ATTRIB +h "default_project_sound_mole_2.mp3" /s
ATTRIB +h "default_project_sound_mole_3.mp3" /s
ATTRIB +h "default_project_sound_mole_4.mp3" /s
call:deleteAllFiles
ATTRIB -h "default_project_sound_mole_1.mp3" /s
ATTRIB -h "default_project_sound_mole_2.mp3" /s
ATTRIB -h "default_project_sound_mole_3.mp3" /s
ATTRIB -h "default_project_sound_mole_4.mp3" /s
@echo "| raw                                        |    DONE    |"
@echo "+--------------------------------------------+------------+"

:-------
:res.values
:-------
cd "%~dp0\res\values"
ATTRIB +h "attrs.xml" /s
ATTRIB +h "colors.xml" /s
ATTRIB +h "dimens.xml" /s
ATTRIB +h "ids.xml" /s
ATTRIB +h "strings.xml" /s
ATTRIB +h "strings-global.xml" /s
ATTRIB +h "styles.xml" /s
call:deleteAllFiles
ATTRIB -h "attrs.xml" /s
ATTRIB -h "colors.xml" /s
ATTRIB -h "dimens.xml" /s
ATTRIB -h "ids.xml" /s
ATTRIB -h "strings.xml" /s
ATTRIB -h "strings-global.xml" /s
ATTRIB -h "styles.xml" /s
@echo "| values                                     |    DONE    |"
@echo "+--------------------------------------------+------------+"

:-------
:res.values-de
:-------
cd "%~dp0\res\values-de"
ATTRIB +h "strings.xml" /s
call:deleteAllFiles
ATTRIB -h "strings.xml" /s
@echo "| values-de                                  |    DONE    |"
@echo "+--------------------------------------------+------------+"

:-------
:res.values-ja
:-------
cd "%~dp0\res\values-ja"
ATTRIB +h "strings.xml" /s
call:deleteAllFiles
ATTRIB -h "strings.xml" /s
@echo "| values-ja                                  |    DONE    |"
@echo "+--------------------------------------------+------------+"

:-------
:res.values-ko
:-------
cd "%~dp0\res\values-ko"
ATTRIB +h "strings.xml" /s
call:deleteAllFiles
ATTRIB -h "strings.xml" /s
@echo "| values-ko                                  |    DONE    |"
@echo "+--------------------------------------------+------------+"

:-------
:res.values-nl
:-------
cd "%~dp0\res\values-nl"
ATTRIB +h "strings.xml" /s
call:deleteAllFiles
ATTRIB -h "strings.xml" /s
@echo "| values-nl                                  |    DONE    |"
@echo "+--------------------------------------------+------------+"

:-------
:res.values-pt
:-------
cd "%~dp0\res\values-pt"
ATTRIB +h "strings.xml" /s
call:deleteAllFiles
ATTRIB -h "strings.xml" /s
@echo "| values-pt                                  |    DONE    |"
@echo "+--------------------------------------------+------------+"

:-------
:res.values-ro
:-------
cd "%~dp0\res\values-ro"
ATTRIB +h "strings.xml" /s
call:deleteAllFiles
ATTRIB -h "strings.xml" /s
@echo "| values-ro                                  |    DONE    |"
@echo "+--------------------------------------------+------------+"

:-------
:res.values-ru
:-------
cd "%~dp0\res\values-ru"
ATTRIB +h "strings.xml" /s
call:deleteAllFiles
ATTRIB -h "strings.xml" /s
@echo "| values-ru                                  |    DONE    |"
@echo "+--------------------------------------------+------------+"

:-------
:res.values-v14
:-------
cd "%~dp0\res\values-v14"
ATTRIB +h "strings-global.xml" /s
call:deleteAllFiles
ATTRIB -h "strings-global.xml" /s
@echo "| values-v14                                 |    DONE    |"
@echo "+--------------------------------------------+------------+"

:-------
:res.xml
:-------
cd "%~dp0\res\xml"
ATTRIB +h "livewallpaper.xml" /s
ATTRIB +h "livewallpapersettings.xml" /s
call:deleteAllFiles
ATTRIB -h "livewallpaper.xml" /s
ATTRIB -h "livewallpapersettings.xml" /s
@echo "| xml                                        |    DONE    |"
@echo "+--------------------------------------------+------------+"
@echo """""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

:-------
:menu
:-------
cd "%~dp0\res\menu"
ATTRIB +h "menu_actionmode.xml" /s
ATTRIB +h "menu_selectprogram.xml" /s
call:deleteAllFiles
ATTRIB -h "menu_actionmode.xml" /s
ATTRIB -h "menu_selectprogram.xml" /s
@echo "| menu                                       |    DONE    |"
@echo "+--------------------------------------------+------------+"
@echo """""""""""""""""""""""""""""""""""""""""""""""""""""""""""""


echo.&pause.&goto:eof

:deleteFolderAndSubfolder
@echo off
IF exist %~1 ( echo %~1 exists
cd /d %~1
for /F "delims=" %%i in ('dir /b') do (rmdir "%%i" /s/q || del "%%i" /s/q)
cd ..
rmdir %~1 )
goto:eof

:deleteAllFiles
del "*.*" /Q
goto:eof