# Stitch [ ![Download](https://api.bintray.com/packages/amitkma/maven/stitch-lib/images/download.svg) ](https://bintray.com/amitkma/maven/stitch-lib/_latestVersion)
Simple threading library using annotations for Android

## Download

```gradle
dependencies {
  compile 'com.github.amitkma:stitch-lib:1.0.1'
  annotationProcessor 'com.github.amitkma:compiler:1.0.1'
}
```
## Usage
Using this library is as simple as creating Hello World Android app. Currently, there are three types of annotations which we support.
- `@CallOnAnyThread` : 
This annotation annotates a method to execute on any thread from thread pool. This library create a fixed size thread pool on the basis of availbale processors. If all threads of the thread pool are busy, the task will be added to the task queue wait until a thread is available to process that task.
- `@CallOnNewThread` : 
A method which is annotated with `@CallOnNewThread` will always execute on a new thread. So the task will never have to wait and always start executing immediately.
- `@CallOnUiThread` :
Want to do some ui changes like setting text, starting animation or anything else, simply annotate the method with `@CallOnUiThread`.


Any method which you want to execute on any particular thread, simply annotate that method like below. 
```Java


class ExampleClass {
    
    public ExampleClass() {
    }

    @CallOnAnyThread
    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
}

```
Now, build the project using terminal or by `Build -> Rebuild Project` in android studio. StitchCompiler will generate a new class file for the class in which annotated methods are. The name of generated class will be the name of existing class + "Stitch". For example in above case, name of generated file will be "ExampleClassStitch".

Next is to get the instance of the generated class (for calling the methods). So to get the instance of generated class, you need to stitch generated class and the required class like below : 
```Java
// If getting instance in other class.
ExampleClassStitch exampleClassStitch = ExampleClassStitch.stitch(new ExampleClass()); 

// Or if getting instance in same class
ExampleClassStitch exampleClassStitch = ExampleClassStitch.stitch(this);.
```
After that, you can call the method as usual and the method will execute upon the annotated thread.
``` Java
exampleClassStitch.getBitmapFromURL("https://image.freepik.com/free-vector/android-boot-logo_634639.jpg");
```
This is it. 

## Performance Issue 
The answer is NO. This is because this library uses compile time processing and generates the code at compile time. So no reflection, No perf issue.

##### Remember - Don't make any change/update UI using @CallOnAnyThread or @CallOnNewThread otherwise android will throw an exception.

## Contribution 
For contribution guidelines, read [Contribution guidelines for this project](CONTRIBUTING.md)

## License
```
Copyright 2017 Amit Kumar

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

