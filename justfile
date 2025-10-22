[doc("Default command; list all available commands.")]
@list:
  just --list --unsorted

[doc("Open the repo on GitHub in your default browser.")]
repo:
  open https://github.com/thunderbiscuit/bitcoin-snake-desktop

[doc("Build and run the application.")]
run:
  ./gradlew run
