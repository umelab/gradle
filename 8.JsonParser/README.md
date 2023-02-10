# Content

## Outline
sample code for how to use json parser in gradle.
```text
{
  "name": "Tom",
  "age": 40,
  "address": {
    "country": "England",
    "city": "London"
  },
  "phones": [
      {
        "id": 1,
        "number": "09011112222",
      },
      {
        "id": 2,
        "number": "08011112222"
      }
  ]
}
## Plugin Source Code
- JsonParserPlugin  
  creates tasks as add, update and remove element.

## Test Code
- GreetingPluginTest
  test each tasks
  test if task outcome is succeeded  

