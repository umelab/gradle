# Content

## Plugin Source Code
- EnvPropertyPlugin
  Output environment variables, GRADLE_HOME, with `gradle env` command. Do not forget to set the variables
  in .bashrc.
  Output system property variables, Foo, with `gradle prop` command

## Test Code
- EnvPropertyPluginTest
  Sample code with testing updated environment/property variable.
  - env task  
    test if task output message is `/opt/homebrew/bin/gradle`
    test if task output message is `/opt/bin/gradle/` when environment variable is updated  
  - prop task
    test if task output message is `bar`  
    test if task output message is `boo` when property variable is updated  
