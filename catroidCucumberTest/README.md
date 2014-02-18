#### Cucumber tests for Catroid

* Features must be placed inside `assets/features/`.
* Features which should be run can be specified in `Cucumber.java` as `@CucumberOptions`
* Run with CucumberInstrumentation instrumentation test runner.
* See [cucumber-jvm](https://github.com/cucumber/cucumber-jvm) for details.
* Testing device or emulator should have a directory `/sdcard`.
* Pull html reports with `adb pull /sdcard/cucumber/report cucumber-report`
* If the more readable syntax is used, name the features *.source and run makeCucumberFeatures (or makeCucumberFeatures.bat) beforehand

