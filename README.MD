Fork of the original [CodeCatcher Vaadin-Addon](https://vaadin.com/directory#addon/codecatcher) by lelmarir to make it work with Vaadin7+. Thanks go to lelmrir for creating the original addon.

I did the following changes:
* Vaadin7+ support
* "Catch-all"-Modus which intercepts each input without start/stop characters. Sends content to the server on pressing "return".
* Option to skip "Return" which some scanners send, after reading a scan code.
* Added my own shortcut handling because the original vaadin-shortcuts didn't work in "catch-all" mode
 
I use this addon in one of the projects I develop at work, so it will most likely be updated to work with future version of vaadin.
 
License: Apache License 2.0
