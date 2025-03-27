# Aurora Search Engine

This is a project I built to visualize a custom HTML like language. This visualizer allows you to search through files, bookmark tabs, swap between light and dark mode, and many other features similar to more popular browsers.

**Our Language:**
The language has a unique syntax. A website must have a `style.sbq` and `index.swq` file, similar to HTML / CSS. In order to create divs write `.my_div_name`. For example:
```
.my_div
  Hello          #Hello-Text
  .my_sub_div
      Hello, again   #Second-Text
  /enddiv
/enddiv
```
From there you can style the file with a seperate language:
```
@global

my_div{
    font-size: 20
    font-weight: bold
    top: 20
}

#Hello-Text{
    color: blue
  	  font-size: 40
    left: 50
    font: \Italian Simple\Bold
    qref: \Linked Website
}

.my_sub_div{
    alignment: horizontal
    left: 25
    top: 10
}

#Second-Text{
    color: black
}
```
With this you will be able to see a full website, and you will be able to click on the `Hello` text to be linked to a seperate website named `Linked Website`.

**Installation:**
* Download the newest release.
* Install Java:
    * You can go to https://www.java.com/en/download/manual.jsp to download Java.
    * On windows once downloaded link the `bin` directory in your system path variables. This can be done by:
       * Navigating to the directory in your Java install named `bin`,
       * Searching `env` in your windows search bar,
       * Open `Edit the System Enviorenment Variables`,
       * Click `Enviorenment Variables` at the bottom right,
       * In the top menu double click on `Path`
       * Click `New` in the top right,
       * Paste in the path of the bin directory, and save your Path variables.
    * For other OS's please look up instructions on installing Java.
    * Once installed, you should be able to run our program with:
      ```
      javac [PATH TO THE CONTROL.JAVA FILE]
      java App
      ```
