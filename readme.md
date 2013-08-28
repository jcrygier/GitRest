GitRest
=======

Git REST is a project to wrap common commands in JGit in a REST-ful architecture.  This project was started in order
to enable a web-based Git client.  Initially, the desire was to use Git JS, but it was determined that this project
would be less effort and less maintenance (since most of the work is done by the JGit team).

Starting the Server
-------------------

After the classpath is set properly, you can run "java com.crygier.git.rest.Main start <conf.properties file>".

If you would like to build the package for deployment as a windows service, you can execute 'gradle buildWindowsService'.
This will put everything needed in build/windowsService.  You can run 'GitRest.bat install' in this directory to install the
GitRest service in windows.  This will default to pointing to the build/windowsService/conf.properties.

If you want to uninstall the service, you can run 'GitRest.bat uninstall'.

This package will also be available for download, for easy installation.

Running in 'Detached' Mode
--------------------------

During development of the front end, it may be helpful to run without a server.  In this case, you can open index.html
in your browser of choice for development.  There is code in loadApp.js that will recognize that you're running over
the 'file' protocol (as opposed to http), and load the actual partials, as well as mockTemplates.js (Hardcoded, mocked
resources).

Since Chrome does not like loading from the file protocol over AJAX, you will need to start chrome with the
"--allow-file-access-from-files" setting.