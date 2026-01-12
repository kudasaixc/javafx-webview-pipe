# javafx-webview-pipe
Pipe WebView Displayer in JavaFX + Maven.

## What this does
This project uses a lightweight local registry so a JavaFX app can resolve a **pipe string**
like `navalbattle` into a local URL. When the user types a pipe string, the app looks it up
in the registry and loads the matching URL in a JavaFX `WebView`.

The registry can map multiple pipe strings to different local servers, e.g.:

```
navalbattle -> http://localhost:8912
hello -> http://localhost:2734
```

## Running the app
1. Register your local servers (see registry format below).
2. Start your local web servers.
3. Run the JavaFX application:

```
./mvnw javafx:run
```

If you do not have the Maven wrapper, run:

```
mvn javafx:run
```

Then enter a pipe string (for example `navalbattle`) in the input field and click **Connect**.

## Where the pipe strings live
Pipe strings are stored in a local registry file at:

```
~/.pipeviewer/registry.json
```

The JavaFX app reads this file each time you click **Connect**.

### Registry format
The registry is a small JSON document with a `pipes` object:

```json
{
  "pipes": {
    "navalbattle": "http://localhost:8912",
    "hello": "http://localhost:2734"
  }
}
```

## Developer SDK (registering pipe strings)
Each web server can register its pipe string by writing or updating the registry file.
Below are small helper examples you can integrate into your project to register a pipe.

## Python guide: register a pipe from any project
This guide shows how a standalone Python script can register a pipe string and URL.

### 1) Create `register_pipe.py`
Create a file named `register_pipe.py` in **any** folder (it does not have to be inside
the JavaFX project):

```python
import json
from pathlib import Path

def register_pipe(pipe_name: str, url: str) -> None:
    registry_path = Path.home() / ".pipeviewer" / "registry.json"
    registry_path.parent.mkdir(parents=True, exist_ok=True)
    data = {"pipes": {}}
    if registry_path.exists():
        with registry_path.open("r", encoding="utf-8") as handle:
            data = json.load(handle)
    data.setdefault("pipes", {})[pipe_name] = url
    with registry_path.open("w", encoding="utf-8") as handle:
        json.dump(data, handle, indent=2)

if __name__ == "__main__":
    register_pipe("navalbattle", "http://localhost:8912")
```

### 2) Run the script
From the folder where you saved the file:

```
python register_pipe.py
```

### 3) Verify the registry file
After running, you should see the mapping in:

```
~/.pipeviewer/registry.json
```

Open the JavaFX app, type `navalbattle`, and click **Connect** to load the URL.

### Python
```python
import json
from pathlib import Path

def register_pipe(pipe_name: str, url: str) -> None:
    registry_path = Path.home() / ".pipeviewer" / "registry.json"
    registry_path.parent.mkdir(parents=True, exist_ok=True)
    data = {"pipes": {}}
    if registry_path.exists():
        with registry_path.open("r", encoding="utf-8") as handle:
            data = json.load(handle)
    data.setdefault("pipes", {})[pipe_name] = url
    with registry_path.open("w", encoding="utf-8") as handle:
        json.dump(data, handle, indent=2)

register_pipe("navalbattle", "http://localhost:8912")
```

### JavaScript (Node.js)
```javascript
import fs from "fs";
import os from "os";
import path from "path";

function registerPipe(pipeName, url) {
  const registryPath = path.join(os.homedir(), ".pipeviewer", "registry.json");
  fs.mkdirSync(path.dirname(registryPath), { recursive: true });
  let data = { pipes: {} };
  if (fs.existsSync(registryPath)) {
    data = JSON.parse(fs.readFileSync(registryPath, "utf-8"));
  }
  data.pipes ??= {};
  data.pipes[pipeName] = url;
  fs.writeFileSync(registryPath, JSON.stringify(data, null, 2));
}

registerPipe("navalbattle", "http://localhost:8912");
```

### C#
```csharp
using System;
using System.Collections.Generic;
using System.IO;
using System.Text.Json;

void RegisterPipe(string pipeName, string url)
{
    var registryPath = Path.Combine(
        Environment.GetFolderPath(Environment.SpecialFolder.UserProfile),
        ".pipeviewer",
        "registry.json"
    );
    Directory.CreateDirectory(Path.GetDirectoryName(registryPath)!);
    var data = new Dictionary<string, Dictionary<string, string>> { { "pipes", new() } };
    if (File.Exists(registryPath))
    {
        var json = File.ReadAllText(registryPath);
        data = JsonSerializer.Deserialize<Dictionary<string, Dictionary<string, string>>>(json)
               ?? data;
    }
    data["pipes"][pipeName] = url;
    var output = JsonSerializer.Serialize(data, new JsonSerializerOptions { WriteIndented = true });
    File.WriteAllText(registryPath, output);
}

RegisterPipe("navalbattle", "http://localhost:8912");
```

### C++
```cpp
#include <fstream>
#include <filesystem>
#include <nlohmann/json.hpp>

void registerPipe(const std::string& pipeName, const std::string& url) {
    auto registryPath = std::filesystem::path(std::getenv("HOME")) / ".pipeviewer" / "registry.json";
    std::filesystem::create_directories(registryPath.parent_path());
    nlohmann::json data = { {"pipes", nlohmann::json::object()} };
    if (std::filesystem::exists(registryPath)) {
        std::ifstream input(registryPath);
        input >> data;
    }
    data["pipes"][pipeName] = url;
    std::ofstream output(registryPath);
    output << data.dump(2);
}

registerPipe("navalbattle", "http://localhost:8912");
```
