@echo off
cls
::This script will setup the environment for SDK Development

echo Cast ID Setup
echo Note: This will be displayed (due to limitations in windows).
echo It is suggested to make sure nobody can see what is being entered here
set /p ID="Your Google Cast application ID: "
set /p DEBUG="Do you want the application ID to be printed out during Gradle builds for debugging purposes? [y/n]:"

echo Creating Gradle files
del mobile\gradle.properties >nul
echo SECURE_CAST_ID = %ID%>>app\gradle.properties
echo ALLOWIDDEBUG = %DEBUG%>>app\gradle.properties

echo Done! You should now be able to compile