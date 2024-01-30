<p align="center">
  <a href="#"><img alt="skanalyzer logo" src=https://github.com/SkEditorPlus/SkAnalyzer/assets/67753196/c9d96e50-db0c-4797-ac51-4ac3a3e1c617></a>
</p>
<h1 align="center">
  SkAnalyzer
  <a href="https://github.com/SkEditorPlus/SkAnalyzer/releases/latest"><img src="https://img.shields.io/github/v/release/SkEditorPlus/SkAnalyzer"></a>
  <a href="#"><img src="https://img.shields.io/github/downloads/SkEditorPlus/SkAnalyzer/total"></a>
  <a href="https://github.com/SkEditorPlus/SkAnalyzer/issues"><img src="https://img.shields.io/github/issues/SkEditorPlus/SkAnalyzer"></a>
  <a href="https://github.com/SkEditorPlus/SkAnalyzer/pulls"><img src="https://img.shields.io/github/issues-pr/SkEditorPlus/SkAnalyzer"></a>
  <a href="#"><img src="https://img.shields.io/github/stars/SkEditorPlus/SkAnalyzer"></a>
</h1>
SkAnalyzer is a simple Skript parser created for SkEditor

## Usage
> [!NOTE]
> If you want to use SkAnalyzer in your project, please copyright us

### As app
Simply run SkAnalyzer in your command prompt and enter paths to your scripts

You might need to see the [wiki](https://github.com/SkEditorTeam/SkAnalyzer/wiki) for additional features

### As api
<details>
<summary>Java</summary>

In Java you can simply create `SkAnalyzer` using `SkAnalyzerBuilder`, for example:
```java
SkAnalyzer.builder()
    .flags(AnalyzerFlag.FORCE_VAULT_HOOK, AnalyzerFlag.FORCE_REGIONS_HOOK, AnalyzerFlag.ENABLE_PLAIN_LOGGER)
    .build()
```
</details>

<details>
<summary>Other languages</summary>

In other languages you can use [SkAnalyzerBridge](https://github.com/SkEditorTeam/SkAnalyzerBridge), however it doesn't have all features

C# example:
```cs
[DllImport("SkAnalyzerBridge.dll", CharSet = CharSet.Unicode, CallingConvention = CallingConvention.StdCall)]
static extern void Init(byte[] javaHome, byte[] analyzerJar);
[DllImport("SkAnalyzerBridge.dll", CharSet = CharSet.Unicode, CallingConvention = CallingConvention.StdCall)]
static extern void Parse(byte[] path);
[DllImport("SkAnalyzerBridge.dll", CallingConvention = CallingConvention.StdCall)]
static extern void Exit();

var javaHome = Encoding.UTF8.GetBytes(Environment.GetEnvironmentVariable("JAVA_HOME")!).ToArray();
Init(javaHome, "SkAnalyzer.jar"u8.ToArray());
Parse("SkAnalyzerTest.sk"u8.ToArray());
Exit();
```
</details>

## Compiling
To clone the repository with all the submodules, open command prompt and run
```
git clone https://github.com/SkEditorTeam/SkAnalyzer.git --recurse-submodules
```
and after that, run this command in SkAnalyzer folder
```
./gradlew shadowJar
``` 
