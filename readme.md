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