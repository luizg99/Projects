Set shell = CreateObject("WScript.Shell")
Set sistema = CreateObject("Scripting.FileSystemObject")
pasta = sistema.GetParentFolderName(WScript.ScriptFullName)
shell.CurrentDirectory = pasta
shell.Run "cmd /c node --env-file=.env interface.js", 0, False
