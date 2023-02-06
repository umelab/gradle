# Content

## Outline
sample code for how to use xml parser in gradle.
in the code, handling with following xml sample.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<tomcatProjectProperties>
    <rootDir>/war/target/aipo</rootDir>
    <exportSource>false</exportSource>
    <reloadable>false</reloadable>
    <redirectLogger>false</redirectLogger>
    <updateXml>true</updateXml>
    <warLocation></warLocation>
    <extraInfo></extraInfo>
    <webPath>/</webPath>
</tomcatProjectProperties>
```

## Plugin Source Code
- XmlParserPlugin  

## Test Code
- XmlParserPluginTest
  test if task outcome is succeeded  

