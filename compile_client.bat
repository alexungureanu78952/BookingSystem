@echo off
cd /d "d:\GitHub\BookingSystem\Client\src\main\java"
javac -d "d:\GitHub\BookingSystem\Client\target\classes" -cp "d:\GitHub\BookingSystem\Client\target\dependency\*" ^
  shareable\BookingDTO.java ^
  shareable\CancelCommand.java ^
  shareable\Command.java ^
  shareable\ExitCommand.java ^
  shareable\GetUserCommand.java ^
  shareable\ListSlotsCommand.java ^
  shareable\LoginCommand.java ^
  shareable\MyBookingsCommand.java ^
  shareable\RegisterCommand.java ^
  shareable\ReserveCommand.java ^
  shareable\ServerResponse.java ^
  shareable\TimeSlotDTO.java ^
  shareable\UserDTO.java ^
  booking\client\BookingClient.java ^
  booking\client\ClientApplication.java ^
  booking\client\ui\ThemeManager.java ^
  booking\client\ui\ModernButton.java ^
  booking\client\ui\AuthPanel.java ^
  booking\client\ui\TimeSlotCard.java ^
  booking\client\ui\BookingCard.java ^
  booking\client\ui\UserProfilePanel.java ^
  booking\client\ui\EditableUserProfilePanel.java ^
  booking\client\ui\BookingGUI.java
echo Compilation done!
pause
