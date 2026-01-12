# javafx-webview-pipe
Pipe WebView Displayer in JavaFX + Maven.

## What this does
This project hardcodes a "pipe string" inside a JavaFX app. When the user types the exact
pipe string, the app connects to a local Flask server and displays it in a JavaFX `WebView`.

The default pipe string is:

```
pipe://localhost:5000
```

When the string matches, the app loads:

```
http://localhost:5000
```

## Running the app
1. Start your local Flask server on `http://localhost:5000`.
2. Run the JavaFX application:

```
./mvnw javafx:run
```

If you do not have the Maven wrapper, run:

```
mvn javafx:run
```

Then enter `pipe://localhost:5000` in the input field and click **Connect**.

## Where the pipe string lives
The pipe string is hardcoded in:

```
src/main/java/com/example/pipeviewer/PipeViewerApp.java
```

Change `PIPE_STRING` and `LOCAL_HTTP_FALLBACK` to match your local server.

## Pipe string implementations
The pipe string is just a custom URL scheme that maps to a local HTTP URL. Below are
examples for other languages that parse a `pipe://host:port` value and translate it to
`http://host:port`.

### Python
```python
from urllib.parse import urlparse

def pipe_to_http(pipe_string: str) -> str:
    parsed = urlparse(pipe_string)
    if parsed.scheme != "pipe" or not parsed.hostname or not parsed.port:
        raise ValueError("Invalid pipe string")
    return f"http://{parsed.hostname}:{parsed.port}"

# Example
print(pipe_to_http("pipe://localhost:5000"))
```

### JavaScript
```javascript
function pipeToHttp(pipeString) {
  const url = new URL(pipeString);
  if (url.protocol !== "pipe:") {
    throw new Error("Invalid pipe string");
  }
  return `http://${url.hostname}:${url.port}`;
}

console.log(pipeToHttp("pipe://localhost:5000"));
```

### C#
```csharp
using System;

string PipeToHttp(string pipeString)
{
    var uri = new Uri(pipeString);
    if (!uri.Scheme.Equals("pipe", StringComparison.OrdinalIgnoreCase) || uri.Host.Length == 0 || uri.Port == -1)
    {
        throw new ArgumentException("Invalid pipe string");
    }

    return $"http://{uri.Host}:{uri.Port}";
}

Console.WriteLine(PipeToHttp("pipe://localhost:5000"));
```

### C++
```cpp
#include <iostream>
#include <regex>
#include <string>

std::string pipeToHttp(const std::string& pipeString) {
    std::regex pattern(R"(^pipe://([^:/]+):(\d+)$)");
    std::smatch matches;
    if (!std::regex_match(pipeString, matches, pattern)) {
        throw std::invalid_argument("Invalid pipe string");
    }
    return "http://" + matches[1].str() + ":" + matches[2].str();
}

int main() {
    std::cout << pipeToHttp("pipe://localhost:5000") << std::endl;
    return 0;
}
```
