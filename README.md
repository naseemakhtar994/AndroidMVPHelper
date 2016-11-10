# AndroidMVPHelper

Library manages lifecycle of Activities and Fragments, their Presenters and ViewStates.

For library usage add:

```gradle
android {  
  defaultConfig{
    enforceUniquePackageName = false
  }
}

repositories {
    maven { url 'https://dl.bintray.com/ufkoku/maven/' }
}
```

### [mvp_base](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp_base)
Contains basic interfaces without any implementation.

### [mvp](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp_base)
Contains implementations of:
* [Retainable elements](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp/src/main/kotlin/com/ufkoku/mvp/retainable):
  * [BaseRetainableActivity](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp/src/main/kotlin/com/ufkoku/mvp/retainable/BaseRetainableActivity.kt);
  * [BaseRetainableFragment](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp/src/main/kotlin/com/ufkoku/mvp/retainable/BaseRetainableFragment.kt);
  * [BaseRetainableDialogFragment](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp/src/main/kotlin/com/ufkoku/mvp/retainable/BaseRetainableDialogFragment.kt);
* [Savable elements](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp/src/main/kotlin/com/ufkoku/mvp/savable):
  * [BaseSavableActivity](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp/src/main/kotlin/com/ufkoku/mvp/savable/BaseSavableActivity.kt);
  * [BaseSavableFragment](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp/src/main/kotlin/com/ufkoku/mvp/savable/BaseSavableFragment.kt);
  * [BaseSavableDialogFragment](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp/src/main/kotlin/com/ufkoku/mvp/savable/BaseSavableDialogFragment.kt).

Retainable elements store their ViewStates and Presenters on screen rotation (Fragments use [setRetainInstance(boolean)](https://developer.android.com/reference/android/support/v4/app/Fragment.html#setRetainInstance(boolean)) and Activities store them in retainable fragment)

Savable elements use onSaveInstance(), and recreating ViewState and Presenter on screen rotation.

Examples of usage:
* [BaseRetainableActivity](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/app/src/main/java/com/ufkoku/demo_app/ui/retainable)
* [BaseRetainableFragment](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/app/src/main/java/com/ufkoku/demo_app/ui/fragments/retainable)
* [BaseSavableActivity](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/app/src/main/java/com/ufkoku/demo_app/ui/savable)
* [BaseSavableFragment](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/app/src/main/java/com/ufkoku/demo_app/ui/fragments/savable)

//Note: mvp contains module mvp_base
```gradle
dependencies{
  compile "com.ufkoku.mvp:mvp:$mvp_ver"
}
```

### [mvp_presenter](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp_presenter)
Contains implementation of:
* [IPresenter](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp_base/src/main/kotlin/com/ufkoku/mvp_base/presenter/IPresenter.kt) - [BasePresenter](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp_presenter/src/main/kotlin/com/ufkoku/mvp/presenter/BasePresenter.kt)
* [IAsyncPresenter](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp_base/src/main/kotlin/com/ufkoku/mvp_base/presenter/IAsyncPresenter.kt) - [BaseAsyncPresenter](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp_presenter/src/main/kotlin/com/ufkoku/mvp/presenter/BaseAsyncPresenter.kt)

BasePresenter implements methods onAttachView() and onDetachView().

BaseAsyncPresenter additionaly implements managing of background executor. Method waitForViewIfNeeded() allows you to pause worker thread, prior to attaching view to presenter.

//Note: mvp contains module mvp_base
```gradle
dependencies{
  compile "com.ufkoku.mvp:mvp_presenter:$mvp_ver"
}
```

### [mvp_rx_presenter](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp_rx_presenter)
Containts [BaseAsyncRxPresenter](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp_rx_presenter/src/main/kotlin/com/ufkoku/mvp/presenter/rx/BaseAsyncRxPresenter.kt) that extends [BaseAsyncPresenter](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp_presenter/src/main/kotlin/com/ufkoku/mvp/presenter/BaseAsyncPresenter.kt)

It creates Scheduler based on BaseAsyncPresenter.executor.

Also it contains classes UiWaitingOnSubscribe and UiWaitingOnSubscriber that implements calling of BaseAsyncPresenter.waitForViewIfNeeded() before populating result.

//Note: mvp_rx_presenter contains module mvp_presenter
```gradle
dependencies{
  compile "com.ufkoku.mvp:mvp_rx_presenter:$mvp_ver"
}
```

### [mvp_autosavable](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp_autosavable) and [mvp_autosavable_annotation](https://github.com/Ufkoku/AndroidMVPHelper/tree/master/mvp_autosavable)

This modules provides autosaving to Bundle and restoring state back.

This two modules performing generating of next classes:
* com.ufkoku.mvp.viewstate.autosavable.AutoSavableViewState
* com.ufkoku.mvp.viewstate.autosavable.SaveHandler
* com.ufkoku.mvp.viewstate.autosavable.{class_name}Saver

When annotation processor generates com.ufkoku.mvp.viewstate.autosavable.{class_name}Saver it looks for:
* Public access to field;
* Getters and Setters (get/set){FieldName};
* Using reflection to access fields.

Annotation processor ignores:
* transient fields;
* static fields;
* final fields.

To use it you should:
* Annotate your class with @AutoSavable
* Extend an AutoSavableViewState, that implements save() and restore() methods from [ISavableViewState](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/mvp_base/src/main/kotlin/com/ufkoku/mvp_base/viewstate/ISavableViewState.kt)

Example of usage [SavableViewState](https://github.com/Ufkoku/AndroidMVPHelper/blob/master/app/src/main/java/com/ufkoku/demo_app/ui/savable/SavableViewState.java)

* If you are using kotlin:
```gradle
dependencies{
  compile "com.ufkoku.mvp:mvp_base:$mvp_ver" //if any other module not used
  compile("com.ufkoku.mvp:mvp_autosavable_annotation:$mvp_ver")
  kapt("com.ufkoku.mvp:mvp_autosavable:$mvp_ver")
}

kapt {
  generateStubs = true
}
```
* else you should use [APT](https://bitbucket.org/hvisser/android-apt)
```gradle
buildscript {
    repositories {
      mavenCentral()
    }
    dependencies {
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
    }
}

apply plugin: 'com.neenbedankt.android-apt'

dependencies {
    compile "com.ufkoku.mvp:mvp_base:$mvp_ver" //if any other module not used
    compile("com.ufkoku.mvp:mvp_autosavable_annotation:$mvp_ver")
    apt("com.ufkoku.mvp:mvp_autosavable:$mvp_ver")
    androidTestApt("com.ufkoku.mvp:mvp_autosavable:$mvp_ver")
}
```

```license
Copyright 2016 Ufkoku (https://github.com/Ufkoku/AndroidMVPHelper)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
