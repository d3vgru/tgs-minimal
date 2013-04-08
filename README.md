#Dependencies

* Android libs
    * [ABS](https://github.com/d3vgru/ActionBarSherlock/tree/tgs-android)
    * [spydroid-ipcamera](https://github.com/d3vgru/spydroid-ipcamera/tree/tgs-android)
    * [UnifiedPreference](https://github.com/saik0/UnifiedPreference)



* Submodules in git
    * [Dispersy](https://github.com/d3vgru/dispersy/tree/tgs-android)
    * [pymdht](https://github.com/d3vgru/pymdht/tree/tgs-android)


#Building

* Using [python-for-android](https://github.com/d3vgru/python-for-android/tree/tgs-android), run dist-tgs.sh to make dist/default

* Replace python-install, private and libs folders in this project with the ones from dist/default

* Update paths to Android libs in project.properties

* Run build-apk.sh

